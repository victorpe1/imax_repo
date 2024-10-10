package com.imax.app.ui.configuraciones;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOProducto;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracionesFragment extends Fragment {
    public final String TAG = getClass().getName();
    DataBaseHelper dataBaseHelper;
    DAOExtras daoExtras;
    DAOProducto daoProducto;
    //private List<PuntoVenta> listaPuntoVenta;
    private App app;
    private Button btnSincronizar;
    private Spinner spnPuntoVenta;

    public ConfiguracionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuraciones, container, false);
        setHasOptionsMenu(true);
        Util.actualizarToolBar(getString(R.string.menu_configuracion),false,getActivity());

        dataBaseHelper = DataBaseHelper.getInstance(getActivity());
        daoExtras = new DAOExtras(getActivity().getApplicationContext());
        daoProducto = new DAOProducto(getActivity().getApplicationContext());
        app = (App) getActivity().getApplicationContext();

        btnSincronizar = view.findViewById(R.id.btn_sincronizar);
        spnPuntoVenta = view.findViewById(R.id.spn_punto_venta);

        /*listaPuntoVenta = daoExtras.getPuntoVentasSinContingencia();
        ArrayList<String> arrayPuntoVenta = new ArrayList<String>();
        for (PuntoVenta model : listaPuntoVenta) {
            arrayPuntoVenta.add(model.getCodigo() + " - " + model.getDescripcion());
        }*/
       /* ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.my_spinner_item, arrayPuntoVenta);
        spnPuntoVenta.setAdapter(adapter);
*/
        /*btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new async_sincronizacion().execute();
            }
        });*/
        return view;
    }

  /*  @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        for (int i = 0; i < listaPuntoVenta.size(); i++) {
            if (listaPuntoVenta.get(i).getId() == app.getPref_idPuntoVenta()){
                spnPuntoVenta.setSelection(i);
                break;
            }
        }
        spnPuntoVenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                app.setPref_idPuntoVenta(listaPuntoVenta.get(position).getId());
                app.setPref_serieFactura(listaPuntoVenta.get(position).getSerieFactura());
                app.setPref_serieBoleta(listaPuntoVenta.get(position).getSerieBoleta());
                app.setPref_serieTicket(listaPuntoVenta.get(position).getSerieTicket());


                app.setPref_esContingencia(listaPuntoVenta.get(position).esContingencia());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

   /* class async_sincronizacion extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.sincronizando));
            progressDialog.show();
        }

        *//*@Override
        protected String doInBackground(Void... voids) {
            try {
                if (Util.isConnectingToRed(getActivity())) {
                    Log.d(TAG, "sincronizando datos...");
                    //Si no llega a actualizar avisar siempre en la app que no está actualizado
                    Response<ResponseBody> response;

              *//**//*      response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                            getClientes(app.getPref_idBodegaCliente()).execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_cliente.table);

                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                            getProductos(app.getPref_idBodegaCliente()).execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_producto.table);

                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                            getUnidadesMedida().execute();
                    dataBaseHelper.sincroSimpleList(response, TablesHelper.xms_unidad_medida.table);

                    response = XMSApi.getApiEasyfact(getActivity()).getLogoEmpresa(app.getPref_idBodegaCliente(), app.getPref_idBodegaCliente()).execute();
                    dataBaseHelper.saveLogoAPK(response, TablesHelper.xms_cliente.table);

                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                            getListaPrecios(app.getPref_idBodegaCliente()).execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_lista_precio.table);

                    Date c = Calendar.getInstance().getTime();
                    Date tomorrow = new Date(c.getTime() + (1000 * 60 * 60 * 24));
                    Date yesterday = new Date(c.getTime() - (1000 * 60 * 60 * 24));

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String hoy = df.format(c);
                    String mñn = df.format(tomorrow);
                    String ayer = df.format(yesterday);


                    response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                            getListaPedidoGeneral(app.getPref_idBodegaCliente(),
                                    false, hoy, mñn, true).execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_pedido_cabecera.table);

                    String[][] pedidosActual;

                    pedidosActual = DataBaseHelper.pedidos;

                    int total =  DataBaseHelper.cantidadPedidos;

                    for (int i = 0; i < total; i++) {

                        response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                                getPDFPedido(pedidosActual[i][3], "PDF").execute();

                        dataBaseHelper.sincroPDFpedido(response);

                        response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                                getListaPedidoDetallado(pedidosActual[i][0], pedidosActual[i][1],
                                        pedidosActual[i][2], pedidosActual[i][4], 1).execute();
                        dataBaseHelper.sincroPedidoFacturaBoleta(pedidosActual[i][5], pedidosActual[i][6], pedidosActual[i][7],
                                pedidosActual[i][8], pedidosActual[i][9], pedidosActual[i][10], response, TablesHelper.xms_pedido_detalle.table, TablesHelper.xms_pedido_cabecera.table);
                    }


                    List<ListaPrecioModel> listaPrecioModels = daoProducto.getListaPrecios();
                    daoProducto.eliminarListaPrecioProductos();
                    if (listaPrecioModels.size() > 0) {
                    for (ListaPrecioModel listaPrecioModel : listaPrecioModels){
                        response = XMSApi.getApiEasyfact(getActivity().getApplicationContext()).
                                getListaPrecio(listaPrecioModel.getId(), app.getPref_idBodegaCliente()).execute();
                        dataBaseHelper.sincroListaPrecioProductos(response,TablesHelper.xms_lista_precio_producto.table);
                        }
                    }
*//**//*
                    *//**//*response = XMSApi.getApiInterface(getActivity()).getUsuario().execute();
                    daoExtras.sincroUsuario(response,null, null);

                    response = XMSApi.getApiInterface(getActivity()).getBodega().execute();
                    dataBaseHelper.sincroObject(response, TablesHelper.xms_bodega.table);



                    response = XMSApi.getApiInterface(getActivity()).getMarcasDistribuidor().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_bodega_marca.table);

                    response = XMSApi.getApiInterface(getActivity()).getPreciosDistribuidor().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_politica_precio_producto.table);

                    response = XMSApi.getApiInterface(getActivity()).getPoliticasBodega().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_politica_precio_bodega.table);

                    response = XMSApi.getApiInterface(getActivity()).getDistribuidores().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_empresa.table);




                    response = XMSApi.getApiInterface(getActivity()).getProductos().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_producto.table);

                    response = XMSApi.getApiInterface(getActivity()).getPedidos().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_pedido_cabecera.table);

                    response = XMSApi.getApiInterface(getActivity()).getMarcas().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_marca.table);

                    response = XMSApi.getApiInterface(getActivity()).getUnidades().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_unidad_medida.table);

                    response = XMSApi.getApiInterface(getActivity()).getPrecioSugerido().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_precio_sugerido.table);

                    response = XMSApi.getApiInterface(getActivity()).getPrecioBodega().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_precio_bodega.table);

                    response = XMSApi.getApiInterface(getActivity()).getClientes().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_cliente.table);*//**//*



                  *//**//*  app.setSettings_validarStock(true);
                    app.setSettings_stockEnLinea(true);
                    app.setSettings_productoSinPrecio(true);*//**//*

                    return Constants.CORRECT;
                } else {
                    return Constants.FAIL_CONNECTION;
                }
            }catch (UnauthorizedException e){
                e.printStackTrace();
                return Constants.FAIL_UNAUTHORIZED;
            }catch (Resources.NotFoundException e){
                e.printStackTrace();
                return Constants.FAIL_RESOURCE;
            }catch (SocketTimeoutException e){
                e.printStackTrace();
                return Constants.FAIL_TIMEOUT;
            }catch (IOException e){
                e.printStackTrace();
                return e.getMessage();
            }catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
        }*//*

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Log.d( TAG, "onPostExecute "+ result);
            switch (result){
                case Constants.CORRECT:
                    showDialogoPostEnvio(getString(R.string.actualizado),getString(R.string.sincronizacion_correcta), R.drawable.ic_dialog_check);
                    break;
                case Constants.FAIL_CONNECTION:
                    showDialogoPostEnvio(getString(R.string.sin_conexion), getString(R.string.sin_internet_localmente), R.drawable.ic_dialog_alert);
                    break;
                case Constants.FAIL_TIMEOUT:
                    showDialogoPostEnvio(getString(R.string.atencion), getString(R.string.error_tiempo_espera), R.drawable.ic_dialog_alert);
                    break;
                case Constants.FAIL_UNAUTHORIZED:
                    Toast.makeText(getActivity(), R.string.error_no_autorizado, Toast.LENGTH_SHORT).show();
                    app.reLogin(getActivity());
                    break;
                default:
                    MyDetailDialog myDetailDialog = new MyDetailDialog(getActivity(),R.drawable.ic_dialog_alert, getString(R.string.oops),getString(R.string.error_sincronizacion),result);
                    myDetailDialog.setPositiveButton(getString(R.string.aceptar), () -> {

                    });
                    myDetailDialog.show();
                    break;
            }
        }
    }*/

    private void showDialogoPostEnvio(String titulo, String mensaje,@DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setIcon(icon);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

            }
        });
        builder.show();
    }
}
