package com.sales.storeapp.data.tasks;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;

import com.sales.storeapp.R;
import com.sales.storeapp.data.api.XMSApi;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.models.UsuarioModel;
import com.sales.storeapp.utils.Security;
import com.sales.storeapp.utils.UnauthorizedException;
import com.sales.storeapp.utils.Util;
import com.sales.storeapp.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<LoginActivity> weakReference;
    private String usuario;
    private String password;
    private UsuarioModel usuarioModel;
    private DAOExtras daoExtras;

    public LoginTask(LoginActivity loginActivity, String usuario, String password){
        this.weakReference = new WeakReference<>(loginActivity);
        this.usuario = usuario;
        this.password = password;
        this.daoExtras = new DAOExtras(loginActivity.getApplicationContext());
    }

    @Override
    protected void onPreExecute() {
        weakReference.get().showLoader();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (Util.isConnectingToRed(weakReference.get().getApplicationContext())) {
            String credentials = usuario + ":" + password;

            final String basic =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {
                Response<ResponseBody> response = XMSApi.getApiEasyfact(weakReference.get().getApplicationContext()).login(basic).execute();
                if (response.isSuccessful() && response.body() != null) {

                    JSONObject jsonObject = new JSONObject(response.body().string());

                        usuarioModel = new UsuarioModel();
                        usuarioModel.setIdUsuario(jsonObject.getInt("idUsuario"));
                        usuarioModel.setNombre(jsonObject.getString("nombre"));
                        usuarioModel.setTelefono("");
                        usuarioModel.setUsuario(jsonObject.getString("usuario"));

                        usuarioModel.setTipoUsuario(UsuarioModel.TIPO_USUARIO_NEGOCIO);

                        /*boolean isAdmin = false;
                        if (single.has("rol")){
                            JSONObject rol = new JSONObject(single.getString("rol"));
                            if (rol.getString("codigo").equals(UsuarioModel.ROL_CODIGO_ADMINISTRADOR))
                                isAdmin = true;
                            if (rol.getString("codigo").equals(UsuarioModel.ROL_CODIGO_VENDEDOR))
                                isAdmin = true;
                            if (rol.getString("codigo").equals(UsuarioModel.ROL_CODIGO_VENDEDOR_2))
                                isAdmin = true;
                        }
                        weakReference.get().app.setPref_isAdmin(isAdmin);

                        usuarioModel.setAdmin(isAdmin);
                        */

                        daoExtras.guardarUsuario(usuarioModel);

                    weakReference.get().app.setPref_serieUsuario("001");
                    weakReference.get().app.setPref_idPuntoVenta(1);
                    weakReference.get().app.setPref_idUsuario(usuarioModel.getIdUsuario());

                } else {
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
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(usuarioModel == null){
            if (daoExtras.login(usuario, password)){
                usuarioModel = new UsuarioModel();
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
