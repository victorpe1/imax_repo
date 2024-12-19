package com.imax.app.data.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.models.UsuarioModel;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;
import com.imax.app.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<LoginActivity> weakReference;
    private String usuario;
    private String password;
    private UsuarioModel usuarioModel;
    private DAOExtras daoExtras;
    private App app;

    public LoginTask(LoginActivity loginActivity, String usuario, String password){
        this.weakReference = new WeakReference<>(loginActivity);
        this.usuario = usuario;
        this.password = password;
        this.daoExtras = new DAOExtras(loginActivity.getApplicationContext());
        app = (App) loginActivity.getApplicationContext();
    }

    public void limpiarCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                eliminarDirectorio(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean eliminarDirectorio(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = eliminarDirectorio(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        weakReference.get().showLoader();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (Util.isConnectingToRed(weakReference.get().getApplicationContext())) {
            try {
                Response<ResponseBody> response =
                        XMSApi.getApiEasyfactBase2(weakReference.get().getApplicationContext()).
                                obtenerTokens(usuario,password,"IMAX_INSPECCIONES").execute();

                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String access_token = jsonObject.getString("access_token");

                    JSONArray dataArray = new JSONArray();
                    JSONObject userData = new JSONObject();
                    userData.put("usuario", usuario);
                    userData.put("password", password);
                    dataArray.put(userData);

                    JSONObject paramsObject = new JSONObject();
                    paramsObject.put("data", dataArray);

                    JSONObject fullBody = new JSONObject();
                    fullBody.put("params", paramsObject);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), fullBody.toString());

                    Response<ResponseBody> responseLogin =
                            XMSApi.getApiEasyfactBase2(weakReference.get().getApplicationContext()).
                                    consultarUsuario(access_token, body).execute();

                    if (responseLogin.isSuccessful()) {
                        limpiarCache(weakReference.get().getApplicationContext());
                        usuarioModel = new UsuarioModel();

                        app.setPref_serieUsuario(usuario);
                        app.setPref_token(password);

                        daoExtras.refrescarCache();
                        daoExtras.refrescarFotoCache();
                        daoExtras.refrescarSupervisorCache();
                    }else{
                        switch (response.code()) {
                            case 401:
                                throw new UnauthorizedException("401 Unauthorized");
                            case 404:
                                throw new Resources.NotFoundException();
                            default:
                                throw new Exception();
                        }
                    }

                }else{
                    switch (response.code()) {
                        case 401:
                            throw new UnauthorizedException("401 Unauthorized");
                        case 404:
                            throw new Resources.NotFoundException();
                        default:
                            throw new Exception();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (UnauthorizedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        weakReference.get().hideLoader();
        if (weakReference.get() != null){
            if (usuarioModel != null){
                weakReference.get().goToSplash();
            }else{
                weakReference.get().showErrorMessage(weakReference.get().getString(R.string.error_login));
            }
        }
    }
}
