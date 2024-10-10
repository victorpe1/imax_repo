package com.imax.app.data.tasks;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;

import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.models.UsuarioModel;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;
import com.imax.app.ui.login.LoginActivity;

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
