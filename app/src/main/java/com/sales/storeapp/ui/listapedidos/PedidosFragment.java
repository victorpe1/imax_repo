package com.sales.storeapp.ui.listapedidos;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.api.XMSApi;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.data.tasks.EnviarDocumentoTask;
import com.sales.storeapp.managers.DataBaseHelper;
import com.sales.storeapp.managers.TablesHelper;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.models.OrderDetail;
import com.sales.storeapp.ui.activity.MenuPrincipalActivity;
import com.sales.storeapp.ui.adapters.RecyclerXMSPedidoAdapter;
import com.sales.storeapp.ui.pedido.PedidoActivity;
import com.sales.storeapp.utils.Constants;
import com.sales.storeapp.utils.MenuItemCustomListener;
import com.sales.storeapp.utils.MyDetailDialog;
import com.sales.storeapp.utils.UnauthorizedException;
import com.sales.storeapp.utils.Util;
import com.sales.storeapp.utils.quickaction.ActionItem;
import com.sales.storeapp.utils.quickaction.QuickAction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosFragment extends Fragment implements SearchView.OnQueryTextListener {
    public final String TAG = getClass().getName();

    private ProgressBar progress;
    private RecyclerView recycler_pedidos;
    private TextView tv_cantidadTotal, tv_montoTotal;
    private FloatingActionButton fab_nuevoPedido;
    private RecyclerXMSPedidoAdapter adapter;
    private ArrayList<Order> lista = new ArrayList<>();
    private ArrayList<Order> listaPendientes = new ArrayList<>();
    private List<String> pedidosPendientes = new ArrayList<>();
    private boolean envioCompleto = false;

    private DataBaseHelper dataBaseHelper;
    private DAOPedido daoPedido;
    private DAOExtras daoExtras;
    Order pedidoCabeceraModel;
    ArrayList<OrderDetail> listaProductos = new ArrayList<>();

    private static final int QUICK_ITEM_VER_DETALLE = 0;
    private static final int QUICK_ITEM_MODIFICAR = 1;
    private static final int QUICK_ITEM_ANULAR = 2;
    private static final int QUICK_COPIAR_ITEM = 3;


    private static final int REQUEST_NUEVO_PEDIDO = 1;

    private int numeroPedidosPendientes = 0;
    private DecimalFormat decimalFormat;

    private SearchView searchView;
    private boolean isSeaching = false;
    private String textSearching = "";
    private ProgressDialog progressDialog;
    QuickAction quickAction;
    public App app;

    public PedidosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        setHasOptionsMenu(true);
        Util.actualizarToolBar(getString(R.string.menu_ventas), false, getActivity());
        app = (App) getActivity().getApplicationContext();
        dataBaseHelper = DataBaseHelper.getInstance(getActivity());
        daoPedido = new DAOPedido(getActivity());

        adapter = new RecyclerXMSPedidoAdapter(this, lista);
        progress = view.findViewById(R.id.progress);
        fab_nuevoPedido = view.findViewById(R.id.fab_nuevoPedido);
        recycler_pedidos = view.findViewById(R.id.recycler_pedidos);
        recycler_pedidos.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycler_pedidos.setAdapter(adapter);
        recycler_pedidos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab_nuevoPedido.show();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && fab_nuevoPedido.isShown())
                    fab_nuevoPedido.hide();
            }
        });

        daoExtras = new DAOExtras(getActivity());

        tv_cantidadTotal = view.findViewById(R.id.tv_cantidadTotal);
        tv_montoTotal = view.findViewById(R.id.tv_montoTotal);

        decimalFormat = Util.formateador();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        fab_nuevoPedido.setOnClickListener(v -> nuevoPedido());

        if (app.getPref_lastSyncro() == 0 ||
                (Util.getTimeDifference(app.getPref_lastSyncro(), System.currentTimeMillis(), Util.LONG_MINUTO) > 10))
            new async_sincronizacion().execute();
        else
            refreshLista();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quickAction = new QuickAction(getActivity());

        ActionItem quickItemDetalle = new ActionItem(QUICK_ITEM_VER_DETALLE, "Ver detalle",
                ContextCompat.getDrawable(getActivity(), R.drawable.icon_document_detalle));
        ActionItem quickItemModificar = new ActionItem(QUICK_ITEM_MODIFICAR, "Modificar",
                ContextCompat.getDrawable(getActivity(), R.drawable.icon_document_editar));
        ActionItem quickItemAnular = new ActionItem(QUICK_ITEM_ANULAR, "Anular",
                ContextCompat.getDrawable(getActivity(), R.drawable.icon_document_anular));
        ActionItem quickItemCopiar = new ActionItem(QUICK_COPIAR_ITEM, "Copiar",
                ContextCompat.getDrawable(getActivity(), R.drawable.icon_document_editar));

        quickAction.addActionItem(quickItemDetalle);
        quickAction.addActionItem(quickItemCopiar);

        if (app.getPref_isAdmin()) {
            quickAction.addActionItem(quickItemModificar);
            quickAction.addActionItem(quickItemAnular);
        }

        quickAction.setOnActionItemClickListener((source, pos, actionId) -> {
            Order cabeceralModel = adapter.getItemSelectedByQuickAction();

            switch (actionId) {
                case QUICK_ITEM_VER_DETALLE:
                    System.out.println(app.getPref_rubro_empre());

                    /*Intent intent = DetallePedidoActivity.getStartIntent(
                            getActivity(),
                            cabeceralModel.getNumeroPedido(),
                            cabeceralModel.getIdCliente(),
                            cabeceralModel.getRazonSocial(),
                            app.getPref_rubro_empre(),
                            "false"
                    );
                    startActivityForResult(intent, REQUEST_DETALLE_PEDIDO);*/
                    break;
                case QUICK_ITEM_MODIFICAR:
                    break;
                case QUICK_COPIAR_ITEM:
                        /*intent = new Intent(getActivity(), PedidoActivity.class);
                        intent.putExtra("accion", PedidoActivity.ACCION_COPIAR_PEDIDO);
                        intent.putExtra(PedidoActivity.EXTRA_ID_CLIENTE, cabeceralModel.getIdCliente());
                        intent.putExtra(PedidoActivity.EXTRA_NUMERO_PEDIDO, cabeceralModel.getNumeroPedido());
                        intent.putExtra(PedidoActivity.EXTRA_NOMBRE_CLIENTE, cabeceralModel.getRazonSocial());

                        startActivityForResult(intent, REQUEST_NUEVO_PEDIDO);*/
                    break;
                case QUICK_ITEM_ANULAR:
                    //validarAnulacion(cabeceralModel);
                    break;
                default:
                    break;
            }
        });
        quickAction.setOnDismissListener((QuickAction.OnDismissListener) () -> {

        });
        adapter.setQuickAction(quickAction);
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_pedidos, menu);

        final View menuItemPendientes = menu.findItem(R.id.menu_pedidos_pendientes).getActionView();
        TextView tv_contador = (TextView) menuItemPendientes.findViewById(R.id.tv_contador);

        new MenuItemCustomListener(menuItemPendientes, getString(R.string.pendientes)) {
            @Override
            public void onClick(View v) {
                showPedidosPendientesDialog();
            }
        }.actualizarTextView(tv_contador, numeroPedidosPendientes);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSeaching = true;
                menu.findItem(R.id.menu_pedidos_pendientes).setVisible(false);
                searchView.requestFocus();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSeaching = false;
                if (numeroPedidosPendientes > 0)
                    menu.findItem(R.id.menu_pedidos_pendientes).setVisible(true);
                return false;
            }
        });
        /*Cada vez que se ejecuta el método refreshLista, el menú se refresa, recreando el searchView. En este proceso se pierde el estado que tenía el serchView (Si estaba abierto o si tenia un texto)
         * Lo que se hace es guardar el estado junto al texto en variables y al momento de recrear el searchView, actualizar a como estaba antes de recrear.*/
        if (isSeaching) {
            searchView.setIconified(false);
            searchView.setQuery(textSearching, false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //super.onPrepareOptionsMenu(menu);
        if (numeroPedidosPendientes == 0) {
            menu.findItem(R.id.menu_pedidos_pendientes).setVisible(false);
        } else {
            menu.findItem(R.id.menu_pedidos_pendientes).setVisible(true);
        }

        /*try{
            menu.findItem(R.id.menu_pedido).setVisible(false);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    public void showPedidosPendientesDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.dialog_list, null);
        final RecyclerView recyclerPendientes = alertLayout.findViewById(R.id.recycler_lista);

        RecyclerXMSPedidoAdapter adapter = new RecyclerXMSPedidoAdapter(PedidosFragment.this, listaPendientes);
        recyclerPendientes.setAdapter(adapter);
        recyclerPendientes.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerPendientes.setPadding(0, 25, 0, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.pendientes) + " (" + numeroPedidosPendientes + ")");
        builder.setView(alertLayout);
        if (!pedidosPendientes.isEmpty()) {
            builder.setPositiveButton(getString(R.string.enviar), (dialog, which) -> {
                enviarPendientes();
            });
        }
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }


    private void enviarPendientes() {
        envioCompleto = true;
        showLoader(getString(R.string.enviando_pendientes));
        new EnviarDocumentoTask(this, pedidosPendientes.get(0)).execute();
    }

    public void postEnvioPedido(String numeroPedido, String estado) {
        pedidosPendientes.remove(0);
        if (pedidosPendientes.isEmpty()) {
            hideLoader();
            if (envioCompleto)
                showDialogoPostEnvio(getString(R.string.envio_pendientes), getString(R.string.envio_pendientes_completo), R.drawable.ic_dialog_check);
            else
                showDialogoPostEnvio(getString(R.string.envio_pendientes), getString(R.string.envio_pendientes_incompleto), R.drawable.ic_dialog_alert);
        } else
            enviarPendientes();
    }

    private void nuevoPedido() {
        if (app.getPref_idPuntoVenta() <= 0) {
            ((MenuPrincipalActivity) getActivity()).showPuntoVentaDialog();
        } else {
            Intent intent = new Intent(getActivity(), PedidoActivity.class);
            intent.putExtra("accion", PedidoActivity.ACCION_NUEVO_PEDIDO);
            startActivityForResult(intent, REQUEST_NUEVO_PEDIDO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("PedidosFragment", "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);

        if (resultCode == getActivity().RESULT_OK) {
            refreshLista();
        }
    }

    public void refreshLista() {
        lista.clear();
        //lista.addAll(daoPedido.getPedidosCabecera(app.getPref_idUsuario()));
        adapter.notifyDataSetChanged();

        int numeroPedidos = lista.size();
        double importeTotal = 0;

        numeroPedidosPendientes = 0;
        listaPendientes.clear();
        pedidosPendientes.clear();
        for (Order pedido : lista) {
            importeTotal += pedido.getTotal();
        }
        getActivity().invalidateOptionsMenu();
        tv_cantidadTotal.setText("" + numeroPedidos);
        tv_montoTotal.setText(getString(R.string.moneda) + decimalFormat.format(importeTotal));
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        filtrarCliente(newText);
        textSearching = newText;
        return true;
    }

    public void filtrarCliente(String newText) {
        List<Order> filteredModelList = filtrar(lista, newText);
        adapter.setFilter(filteredModelList);
    }

    private List<Order> filtrar(ArrayList<Order> listaPedidos, String query) {
        query = query.toLowerCase();
        final List<Order> listaFiltrada = new ArrayList<>();
        for (Order pedido : listaPedidos) {

            //Si se busca por codigo
            if (TextUtils.isDigitsOnly(query)) {
                String codigo = String.valueOf(pedido.getIdCliente());
                if (codigo.contains(query)) {
                    listaFiltrada.add(pedido);
                }
            } else {
                //De lo contrario se filtra por el nombre - arreglar
                String descripcion = pedido.getDirecc().toLowerCase();
                if (descripcion.contains(query.toLowerCase().trim())) {
                    listaFiltrada.add(pedido);
                }
            }
        }
        return listaFiltrada;
    }

    private void showDialogoPostEnvio(String titulo, String mensaje, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setIcon(icon);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.aceptar), (dialog, which) -> refreshLista());
        builder.show();
    }

    class async_sincronizacion extends AsyncTask<Void, String, String> {

        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (Util.isConnectingToRed(getActivity())) {
                    Log.d(TAG, "sincronizando datos...");
                    Response<ResponseBody> response;

                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).getTCambio().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_tcambio.table);

                    return Constants.CORRECT;
                } else {
                    return Constants.FAIL_CONNECTION;
                }
            } catch (UnauthorizedException e) {
                e.printStackTrace();
                return Constants.FAIL_UNAUTHORIZED;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return Constants.FAIL_RESOURCE;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return Constants.FAIL_TIMEOUT;
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute " + result);
            progress.setVisibility(View.GONE);
            switch (result) {
                case Constants.CORRECT:

                    break;
                case Constants.FAIL_TIMEOUT:

                    break;
                case Constants.FAIL_CONNECTION:
                    Snackbar.make(recycler_pedidos, getString(R.string.compruebe_conexion), Snackbar.LENGTH_SHORT).show();
                    break;
                case Constants.FAIL_UNAUTHORIZED:
                    Toast.makeText(getActivity(), R.string.error_no_autorizado, Toast.LENGTH_SHORT).show();
                    ((App) getActivity().getApplicationContext()).reLogin(getActivity());
                    break;
                default:
                    MyDetailDialog myDetailDialog = new MyDetailDialog(getActivity(), R.drawable.ic_dialog_alert, getString(R.string.oops), getString(R.string.error_sincronizacion), result);
                    myDetailDialog.show();
                    break;
            }
            refreshLista();
        }
    }

    public void showLoader(String mensaje) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(mensaje);
            progressDialog.show();
        }
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }
}
