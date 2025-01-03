package com.imax.app.data.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.EasyfactApiInterface;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.api.request.SupervisorRequestWrapper;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOProducto;
import com.imax.app.models.Order;
import com.imax.app.ui.activity.MenuPrincipalActivity;
import com.imax.app.ui.fotosupervisor.RegistroSupervisorFotoActivity;
import com.imax.app.ui.supervisor.RegistroFotoEvidenciaActivity;
import com.imax.app.ui.supervisor.RegistroseguridadTrabajoySeñalizacion;
import com.imax.app.utils.Constants;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class EnviarDocumentoSupervisorTask extends AsyncTask<Void, Void, String> {
    private WeakReference<RegistroseguridadTrabajoySeñalizacion> weakReference;

    private DAOExtras daoExtras;
    private DAOProducto daoProducto;
    private EasyfactApiInterface apiInterface;
    private App app;
    private String numeroPedido;
    private boolean actualizarDocumento = false;

    private SupervisorRequest supervisorRequest;

    public EnviarDocumentoSupervisorTask(RegistroseguridadTrabajoySeñalizacion reference, SupervisorRequest fotoRequest) {
        weakReference = new WeakReference<>(reference);

        daoExtras = new DAOExtras(reference.getApplicationContext());
        apiInterface = XMSApi.getApiEasyfactBase2(reference);
        app = (App) reference.getApplicationContext();

        this.supervisorRequest = fotoRequest;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (weakReference != null) {
            weakReference.get().showLoader();
        }
    }


    public static String transformarCalificacionCalidad(String calificacionCalidad, String radioCheckCalidad) {
        Gson gson = new Gson();

        String[] calificaciones = gson.fromJson(calificacionCalidad, String[].class);
        Boolean[] radioChecks = gson.fromJson(radioCheckCalidad, Boolean[].class);

        JsonArray resultadoArray = new JsonArray();

        for (int i = 0; i < calificaciones.length; i++) {
            JsonObject item = new JsonObject();
            item.addProperty("id", i + 1); // El ID es el índice + 1
            item.addProperty("check", radioChecks[i]);
            item.addProperty("calificacion", calificaciones[i]);
            resultadoArray.add(item);
        }

        return gson.toJson(resultadoArray);
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

                    supervisorRequest.setUser_email(usuario);

                    String mapeoCalidad = transformarCalificacionCalidad(supervisorRequest.getCalificacionCalidad(),
                            supervisorRequest.getRadioCheckCalidad());

                    String mapeoSeguridad = transformarCalificacionCalidad(supervisorRequest.getCalificacionSeguridad(),
                            supervisorRequest.getRadioCheckSeguridad());

                    supervisorRequest.setMapeoRequestCalidad(mapeoCalidad);
                    supervisorRequest.setMapeoRequestSegurida(mapeoSeguridad);

                    ArrayList<SupervisorRequest> supervisorRequests = new ArrayList<>();
                    supervisorRequests.add(supervisorRequest);

                    SupervisorRequestWrapper fRequestWrapper =
                            new SupervisorRequestWrapper(supervisorRequests);

                    String json = new Gson().toJson(fRequestWrapper);
                    Log.i("fRequestWrapper REQUEST", json);

                    Response<ResponseBody> response = apiInterface.
                            enviarRegistroSupervisor(access_token, fRequestWrapper).execute();

                    if (response.code() == 400) {
                        return Constants.FAIL_TIME_MAX;
                    }

                    if (response.isSuccessful()) {
                        //eliminarCarpetaInspeccion(supervisorRequest.getNumInspeccion());

                        return daoExtras.actualizarRepuestaRegistroSupervisor(response,
                                supervisorRequest.getNumInspeccion(),true);
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

    private void eliminarCarpetaInspeccion(String numInspeccion) {
        File directorioBase = new File(app.getExternalFilesDir(null) + "/Fotos/" + numInspeccion);

        if (directorioBase.exists()) {
            eliminarDirectorio(directorioBase);
            Log.d("EliminarCarpeta", "Carpeta de inspección eliminada: " + directorioBase.getAbsolutePath());
        } else {
            Log.d("EliminarCarpeta", "La carpeta no existe: " + directorioBase.getAbsolutePath());
        }
    }

    private void eliminarDirectorio(File directorio) {
        if (directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    eliminarDirectorio(archivo);
                }
            }
        }
        if (directorio.delete()) {
            Log.d("EliminarDirectorio", "Eliminado: " + directorio.getAbsolutePath());
        } else {
            Log.e("EliminarDirectorio", "No se pudo eliminar: " + directorio.getAbsolutePath());
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