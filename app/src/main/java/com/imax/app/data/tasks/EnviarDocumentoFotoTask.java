package com.imax.app.data.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.EasyfactApiInterface;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.api.request.FotoRequest;
import com.imax.app.data.api.request.FotoRequestWrapper;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.api.request.InspeccionRequestWrapper;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOProducto;
import com.imax.app.models.Order;
import com.imax.app.ui.activity.MenuPrincipalActivity;
import com.imax.app.ui.activity.RegistroDespuesInspeccionFirmaActivity;
import com.imax.app.ui.foto.RegistroFotoInsert5Activity;
import com.imax.app.utils.Constants;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EnviarDocumentoFotoTask extends AsyncTask<Void, Void, String> {
    private WeakReference<RegistroFotoInsert5Activity> weakReference;
    private WeakReference<MenuPrincipalActivity> weakReferencePrincipal;

    private DAOExtras daoExtras;
    private DAOProducto daoProducto;
    private EasyfactApiInterface apiInterface;
    private App app;
    private String numeroPedido;
    private boolean actualizarDocumento = false;
    private FotoRequest fotoRequest;

    public EnviarDocumentoFotoTask(RegistroFotoInsert5Activity reference, FotoRequest fotoRequest) {
        weakReference = new WeakReference<>(reference);

        daoExtras = new DAOExtras(reference.getApplicationContext());
        apiInterface = XMSApi.getApiEasyfactBase2(reference);
        app = (App) reference.getApplicationContext();

        this.fotoRequest = fotoRequest;
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

    private ArrayList<String> arrayList(List<String> fotoList){
        ArrayList<String> fotoArray1List = new ArrayList<>();

        if (fotoList != null) {
            for (String item : fotoList) {
                if (item != null) {
                    String[] parts = item.split(";");
                    String mimeType = null;
                    String fileName = null;
                    String filePath = null;

                    // Procesa cada parte y revisa el formato esperado
                    for (String part : parts) {
                        if (part.startsWith("data:")) {
                            mimeType = part.replace("data:", "");
                        } else if (part.startsWith("name=")) {
                            String[] nameParts = part.split("=");
                            if (nameParts.length > 1) {
                                fileName = nameParts[1];
                            }
                        } else if (part.startsWith("file,")) {
                            String[] fileParts = part.split(",");
                            if (fileParts.length > 1) {
                                filePath = fileParts[1];
                            }
                        }
                    }

                    if (mimeType != null && fileName != null && filePath != null) {
                        String base64String = convertImageToBase64(filePath);
                        if (base64String != null) {
                            String combinedItem = "data:" + mimeType + ";name=" + fileName + ";base64," + base64String;
                            fotoArray1List.add(combinedItem);
                        }
                    } else {
                        System.out.println("Formato inesperado en item: " + item);
                    }
                } else {
                    System.out.println("Item nulo en fotoList");
                }
            }
        }
        return fotoArray1List;
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

                    ArrayList<String> fotoArray1List = arrayList(fotoRequest.getFotoArray1());
                    ArrayList<String> fotoArray2List = arrayList(fotoRequest.getFotoArray2());
                    ArrayList<String> fotoArray3List = arrayList(fotoRequest.getFotoArray3());
                    ArrayList<String> fotoArray4List = arrayList(fotoRequest.getFotoArray4());

                    ArrayList<String> fotosArrayAdjunto1List = arrayList(fotoRequest.getFotosArrayAdjunto1());
                    ArrayList<String> fotosArrayAdjunto2List = arrayList(fotoRequest.getFotosArrayAdjunto2());
                    ArrayList<String> fotosArrayAdjunto3List = arrayList(fotoRequest.getFotosArrayAdjunto3());
                    ArrayList<String> fotosArrayAdjunto4List = arrayList(fotoRequest.getFotosArrayAdjunto4());
                    ArrayList<String> fotosArrayAdjunto5List = arrayList(fotoRequest.getFotosArrayAdjunto5());

                    fotoRequest.setFotoArray1(fotoArray1List);
                    fotoRequest.setFotoArray2(fotoArray2List);
                    fotoRequest.setFotoArray3(fotoArray3List);
                    fotoRequest.setFotoArray4(fotoArray4List);

                    fotoRequest.setFotosArrayAdjunto1(fotosArrayAdjunto1List);
                    fotoRequest.setFotosArrayAdjunto2(fotosArrayAdjunto2List);
                    fotoRequest.setFotosArrayAdjunto3(fotosArrayAdjunto3List);
                    fotoRequest.setFotosArrayAdjunto4(fotosArrayAdjunto4List);
                    fotoRequest.setFotosArrayAdjunto5(fotosArrayAdjunto5List);

                    fotoRequest.setUser_email("jose.lunarejo@imax.com.pe");

                    ArrayList<FotoRequest> fotoRequestWrappers = new ArrayList<>();
                    fotoRequestWrappers.add(fotoRequest);
                    FotoRequestWrapper fRequestWrapper =
                            new FotoRequestWrapper(fotoRequestWrappers);

                    String json = new Gson().toJson(fRequestWrapper);
                    Log.i("fRequestWrapper REQUEST", json);

                    Response<ResponseBody> response = apiInterface.
                            enviarRegistroFoto(access_token, fRequestWrapper).execute();

                    if (response.code() == 400) {
                        return Constants.FAIL_TIME_MAX;
                    }

                    if (response.isSuccessful()) {
                        return daoExtras.actualizarRepuestaRegistroFoto(response,
                                fotoRequest.getNumInspeccion());
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