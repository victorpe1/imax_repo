package com.sales.storeapp.ui.listapedidos;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.api.XMSApi;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.managers.DataBaseHelper;
import com.sales.storeapp.managers.TablesHelper;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.ui.adapters.RecyclerXMSPedidoAdapter;
import com.sales.storeapp.ui.detallepedido.DetallePedidoActivity;
import com.sales.storeapp.utils.Constants;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosFragment extends Fragment{
    public final String TAG = getClass().getName();

    private ProgressBar progress;
    private RecyclerView recycler_pedidos;
    private TextView tv_cantidadTotal, tv_montoTotal;
    private RecyclerXMSPedidoAdapter adapter;
    private ArrayList<Order> lista = new ArrayList<>();
    private ArrayList<Order> listaPendientes = new ArrayList<>();
    private List<String> pedidosPendientes = new ArrayList<>();

    private DataBaseHelper dataBaseHelper;
    private DAOPedido daoPedido;

    private static final int QUICK_ITEM_VER_DETALLE = 0;

    private static final int REQUEST_DETALLE_PEDIDO = 3;

    private DecimalFormat decimalFormat;

    private ProgressDialog progressDialog;
    QuickAction quickAction;
    public App app;

    public PedidosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        setHasOptionsMenu(true);
        Util.actualizarToolBar(getString(R.string.menu_ventas_listado), false, getActivity());
        app = (App) getActivity().getApplicationContext();
        dataBaseHelper = DataBaseHelper.getInstance(getActivity());
        daoPedido = new DAOPedido(getActivity());

        adapter = new RecyclerXMSPedidoAdapter(this, lista);
        progress = view.findViewById(R.id.progress);
        recycler_pedidos = view.findViewById(R.id.recycler_pedidos);
        recycler_pedidos.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycler_pedidos.setAdapter(adapter);

        tv_cantidadTotal = view.findViewById(R.id.tv_cantidadTotal);
        tv_montoTotal = view.findViewById(R.id.tv_montoTotal);

        decimalFormat = Util.formateador();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

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

        quickAction.addActionItem(quickItemDetalle);

        quickAction.setOnActionItemClickListener((source, pos, actionId) -> {
            Order cabeceralModel = adapter.getItemSelectedByQuickAction();

            switch (actionId) {
                case QUICK_ITEM_VER_DETALLE:
                    System.out.println(app.getPref_rubro_empre());
                    Intent intent = DetallePedidoActivity.getStartIntent(
                            getActivity(),
                            cabeceralModel.getIdNumero(),
                            String.valueOf(cabeceralModel.getIdCliente()),
                            app.getPref_rubro_empre()
                    );
                    startActivityForResult(intent, REQUEST_DETALLE_PEDIDO);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("PedidosFragment", "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);

        if (resultCode == getActivity().RESULT_OK) {
            refreshLista();
        }
    }

    public void refreshLista() {
        lista.clear();

        lista.addAll(daoPedido.getPedidosCabecera(String.valueOf(app.getPref_idUsuario())));
        adapter.notifyDataSetChanged();

        int numeroPedidos = lista.size();
        double importeTotal = 0;

        listaPendientes.clear();
        pedidosPendientes.clear();
        for (Order pedido : lista) {
            importeTotal += pedido.getTotal();
        }
        getActivity().invalidateOptionsMenu();
        tv_cantidadTotal.setText("" + numeroPedidos);
        tv_montoTotal.setText(getString(R.string.moneda) + decimalFormat.format(importeTotal));
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

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fechaToday = today.format(formatter);
                    //fechaToday = "2024-08-19";

                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).getPedidos(fechaToday).execute();
                    dataBaseHelper.sincronizarPedido(response);

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
