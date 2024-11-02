package com.imax.app.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOProducto;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.UsuarioModel;
import com.imax.app.ui.login.LoginActivity;
import com.imax.app.utils.Constants;
import com.imax.app.utils.MyDetailDialog;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    public final String TAG = getClass().getName();
    private ImageView img_icon;
    DataBaseHelper dataBaseHelper;
    private DAOProducto daoProducto;
    DAOExtras daoExtras;
    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app = (App) getApplicationContext();

        dataBaseHelper = DataBaseHelper.getInstance(this);
        daoProducto = new DAOProducto(getApplicationContext());
        daoExtras = new DAOExtras(getApplicationContext());

        img_icon = findViewById(R.id.img_icon);

        animar();
        init();
    }



    private void init() {
        if (app.isPref_sessionOpen() && daoExtras.existeUsuario()) {
            new async_sincronizacion().execute();
            goToMenuPrincipal();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void animar() {
        Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.fade_scale_infinite);
        img_icon.startAnimation(animationScale);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashActivity.this, "POST", Toast.LENGTH_SHORT).show();
                img_icon.clearAnimation();        
            }
        }, 5000);*/
        
    }

    public void onUserLoggedOut() {
        Log.e(TAG, "RE LOGIN !!!!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashActivity.this, "RE LOGIN !!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    class async_sincronizacion extends AsyncTask<Void, String, String> {
        String message;
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (Util.isConnectingToRed(getApplicationContext())) {
                    Log.d(TAG,"sincronizando datos...");
                    String usuario = app.getPref_serieUsuario();
                    String password = app.getPref_token();
                    try {
                        Response<ResponseBody> response =
                                XMSApi.getApiEasyfactBase2(getApplicationContext()).
                                        obtenerTokens(usuario, password,"IMAX_INSPECCIONES").execute();

                        if (response.isSuccessful()) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String access_token = jsonObject.getString("access_token");

                            JSONObject jsonParams = new JSONObject();
                            jsonParams.put("params", new JSONObject());

                            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                    jsonParams.toString());

                           Response<ResponseBody> responseCata =
                                    XMSApi.getApiEasyfactBase2(getApplicationContext()).
                                            consultarCatalogo(access_token, body).execute();

                            if (responseCata.isSuccessful()) {
                                dataBaseHelper.sincronizarCatalogo(responseCata);
                                return Constants.CORRECT;

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
                } else {
                    return Constants.FAIL_CONNECTION;
                }
            } catch (Resources.NotFoundException e){
                e.printStackTrace();
                return Constants.FAIL_RESOURCE;
            }catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
            return Constants.CORRECT;
        }

        protected void onPostExecute(String result) {
            Log.d( TAG, "onPostExecute "+ result);
            switch (result){
                case Constants.CORRECT:
                    goToMenuPrincipal();
                    break;
                case Constants.FAIL_CONNECTION:
                    goToMenuPrincipal();
                    break;
                case Constants.FAIL_TIMEOUT:
                    goToMenuPrincipal();
                    break;
                case Constants.FAIL_RESOURCE:
                    retry(getString(R.string.error_resource));
                    break;
                case Constants.FAIL_UNAUTHORIZED:
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    app.reLogin(SplashActivity.this);
                    break;
                default:
                    retry(result);
                    break;
            }
        }
    }

    private void goToMenuPrincipal() {
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin(){
        app.logOut(this);
    }

    private void retry(String message){
        MyDetailDialog myDetailDialog = new MyDetailDialog(SplashActivity.this,R.drawable.ic_dialog_alert, getString(R.string.oops),getString(R.string.error_sincronizacion), message);
        myDetailDialog.setPositiveButton(getString(R.string.reintentar), SplashActivity.this::init);
        myDetailDialog.setNegativeButton(getString(R.string.salir), SplashActivity.this::goToLogin);
        myDetailDialog.show();
    }
}
