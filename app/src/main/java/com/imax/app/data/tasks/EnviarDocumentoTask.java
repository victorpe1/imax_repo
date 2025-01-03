package com.imax.app.data.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.EasyfactApiInterface;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.api.request.InspeccionRequestWrapper;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOProducto;
import com.imax.app.models.Order;
import com.imax.app.ui.activity.MenuPrincipalActivity;
import com.imax.app.ui.activity.RegistroDespuesInspeccionFirmaActivity;
import com.imax.app.utils.Constants;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EnviarDocumentoTask extends AsyncTask<Void, Void, String> {
    private WeakReference<RegistroDespuesInspeccionFirmaActivity> weakReference;
    private WeakReference<MenuPrincipalActivity> weakReferencePrincipal;

    private DAOExtras daoExtras;
    private DAOProducto daoProducto;
    private EasyfactApiInterface apiInterface;
    private App app;
    private String numeroPedido;
    private boolean actualizarDocumento = false;
    private InspeccionRequest inspeccionRequest;

    public EnviarDocumentoTask(RegistroDespuesInspeccionFirmaActivity reference, InspeccionRequest inspeccionRequest) {
        weakReference = new WeakReference<>(reference);

        daoExtras = new DAOExtras(reference.getApplicationContext());
        apiInterface = XMSApi.getApiEasyfactBase2(reference);
        app = (App) reference.getApplicationContext();

        this.inspeccionRequest = inspeccionRequest;
    }

    private String convertImageToBase64(String filePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (weakReference != null) {
            weakReference.get().showLoader();
        }
    }

    @Override
    protected String doInBackground(Void... strings) {
        Context context;
        context = weakReference.get().getApplicationContext();

        if (Util.isConnectingToRed(context)) {
            String usuario = app.getPref_serieUsuario();
            String password = app.getPref_token();
            try {

                Response<ResponseBody> responseToken = apiInterface.
                        obtenerTokens(usuario, password,"IMAX_INSPECCIONES").execute();

                if (responseToken.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responseToken.body().string());
                    String access_token = jsonObject.getString("access_token");

                    ArrayList<String> base64List = new ArrayList<>();
                    if (inspeccionRequest.getFiles() != null) {
                        for (String item : inspeccionRequest.getFiles()) {
                            String[] parts = item.split(";");
                            String mimeType = null;
                            String fileName = null;
                            String filePath = null;

                            for (String part : parts) {
                                if (part.startsWith("data:")) {
                                    mimeType = part.replace("data:", "");
                                } else if (part.startsWith("name=")) {
                                    fileName = part.split("=")[1];
                                } else if (part.startsWith("file,")) {
                                    filePath = part.split(",")[1];
                                }
                            }

                            if (mimeType != null && fileName != null && filePath != null) {
                                String base64String = convertImageToBase64(filePath);
                                if (base64String != null) {
                                    String combinedItem = "data:" + mimeType + ";name=" + fileName + ";base64," + base64String;
                                    base64List.add(combinedItem);
                                }
                            }else {
                                System.out.println("Formato inesperado en item.");
                            }
                        }
                    }

                    inspeccionRequest.setFiles(base64List);
                    inspeccionRequest.setUser_email(usuario);

                    ArrayList<InspeccionRequest> inspeccionRequestWrappers = new ArrayList<>();
                    inspeccionRequestWrappers.add(inspeccionRequest);
                    InspeccionRequestWrapper inspeccionRequestWrapper =
                            new InspeccionRequestWrapper(inspeccionRequestWrappers);

                    String json = new Gson().toJson(inspeccionRequestWrapper);
                    Log.i("VENTA REQUEST", json);

                    Response<ResponseBody> response = apiInterface.
                            enviarRegistro(access_token, inspeccionRequestWrapper).execute();

                    if (response.code() == 400) {
                        return Constants.FAIL_TIME_MAX;
                    }

                    if (response.isSuccessful()) {
                        return daoExtras.actualizarRepuestaRegistro(response,
                                inspeccionRequest.getNumInspeccion());
                    } else {
                        return Constants.FAIL_OTHER;
                    }
                }else{
                    switch (responseToken.code()) {
                        case 401:
                            throw new UnauthorizedException("401 Unauthorized");
                        case 404:
                            throw new Resources.NotFoundException();
                        default:
                            throw new Exception();
                    }
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

        if (weakReference != null) {
            weakReference.get().hideLoader();
            weakReference.get().mostrarPopup(tituloRes, mensajeRes, icon);
        }

    }
}