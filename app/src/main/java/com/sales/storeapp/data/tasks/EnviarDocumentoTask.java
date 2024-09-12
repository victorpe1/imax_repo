package com.sales.storeapp.data.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.api.EasyfactApiInterface;
import com.sales.storeapp.data.api.XMSApi;
import com.sales.storeapp.data.api.request.OrderRequest;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.data.dao.DAOProducto;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.ui.listapedidos.PedidosFragment;
import com.sales.storeapp.ui.pedido.PedidoActivity;
import com.sales.storeapp.utils.Constants;
import com.sales.storeapp.utils.Util;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EnviarDocumentoTask extends AsyncTask<Void, Void, String> {
    private WeakReference<PedidoActivity> pedidoWeakReference;
    private WeakReference<PedidosFragment> pedidosWeakReference;

    private DAOPedido daoPedido;
    private DAOExtras daoExtras;
    private DAOProducto daoProducto;
    private EasyfactApiInterface apiInterface;
    private App app;
    private String numeroPedido;
    private boolean actualizarDocumento = false;

    public EnviarDocumentoTask(PedidoActivity activity, String numeroPedido, boolean actualizarDocumento) {
        pedidoWeakReference = new WeakReference<>(activity);
        daoPedido = new DAOPedido(activity.getApplicationContext());
        daoProducto = new DAOProducto(activity.getApplicationContext());
        daoExtras = new DAOExtras(activity.getApplicationContext());
        apiInterface = XMSApi.getApiEasyfact(activity);
        app = (App) activity.getApplicationContext();
        this.numeroPedido = numeroPedido;
        this.actualizarDocumento = actualizarDocumento;
    }


    public EnviarDocumentoTask(PedidosFragment reference, String numeroPedido) {
        pedidosWeakReference = new WeakReference<>(reference);
        daoPedido = new DAOPedido(reference.requireContext());
        daoProducto = new DAOProducto(reference.requireContext());
        daoExtras = new DAOExtras(reference.requireContext());
        apiInterface = XMSApi.getApiEasyfact(reference.requireContext());
        app = (App) reference.requireActivity().getApplicationContext();
        this.numeroPedido = numeroPedido;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pedidoWeakReference != null) {
            pedidoWeakReference.get().showLoader();
        }
    }

    @Override
    protected String doInBackground(Void... strings) {
        Context context;
        String aux1 = "";
        if (pedidoWeakReference != null)
            context = pedidoWeakReference.get().getApplicationContext();
        else
            context = pedidosWeakReference.get().requireActivity().getApplicationContext();

        if (Util.isConnectingToRed(context)) {
            try {
                double tCambio = daoExtras.getTCambioToday();
                OrderRequest ventaRequest = Util.generarCamposFacturacion(daoPedido.getPostVenta(numeroPedido), tCambio);

                String json = new Gson().toJson(ventaRequest);
                Log.i("VENTA REQUEST", json);

                Response<ResponseBody> response = apiInterface.enviarPedido(ventaRequest).execute();

                if (response.code() == 400) {
                    return Constants.FAIL_TIME_MAX;
                }

                if (response.isSuccessful()) {
                    return daoPedido.actualizarRepuestaPedido(response, ventaRequest.getIdNumero(), ventaRequest);
                } else {
                    return Constants.FAIL_OTHER;
                }
            } catch (JsonParseException ex) {
                ex.printStackTrace();
                return Constants.FAIL_JSON;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                return e.getMessage();
            }
        } else {
            return Constants.FAIL_CONNECTION;
        }
    }

    @Override
    protected void onPostExecute(String respuesta) {
        super.onPostExecute(respuesta);

        int tituloRes, mensajeRes;
        int icon;

        switch (respuesta) {
            case (Order.FLAG_ENVIADO):
                tituloRes = R.string.enviado;
                mensajeRes = R.string.venta_registrada;
                icon = R.drawable.ic_dialog_check;
                break;
            case Order.FLAG_PENDIENTE:
                tituloRes = R.string.atencion;
                mensajeRes = R.string.error_no_registrar_venta;
                icon = R.drawable.ic_dialog_error;
                break;
            case Constants.FAIL_CONNECTION:
                tituloRes = R.string.sin_conexion;
                mensajeRes = R.string.sin_internet_localmente;
                icon = R.drawable.ic_dialog_alert;
                break;
            case Constants.FAIL_JSON:
                tituloRes = R.string.atencion;
                mensajeRes = R.string.venta_no_verificada;
                icon = R.drawable.ic_dialog_alert;
                break;
            case Constants.FAIL_TIME_MAX:
                tituloRes = R.string.atencion;
                mensajeRes = R.string.venta_time_max;
                icon = R.drawable.ic_dialog_error;
                break;
            case Constants.FAIL_RESOURCE:
                tituloRes = R.string.atencion;
                mensajeRes = R.string.error_recurso_pendiente;
                icon = R.drawable.ic_dialog_error;
                break;
            case Constants.FAIL_OTHER:
                tituloRes = R.string.enviado;
                mensajeRes = R.string.venta_registrada_no_facturada;
                icon = R.drawable.ic_dialog_alert;
                break;
            default:
                tituloRes = R.string.atencion;
                mensajeRes = R.string.error_no_registrar_venta;
                icon = R.drawable.ic_dialog_error;
                break;
        }

        if (pedidoWeakReference != null) {
            pedidoWeakReference.get().hideLoader();
            pedidoWeakReference.get().showDialogoPostEnvio(tituloRes, mensajeRes, icon);
        }

    }
}