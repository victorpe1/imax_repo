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
import com.imax.app.ui.login.LoginActivity;
import com.imax.app.utils.Constants;
import com.imax.app.utils.MyDetailDialog;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                    //Util.backupdDatabase(getApplicationContext());
                    Log.d(TAG,"sincronizando datos...");
                    Response<ResponseBody> response;

                   /*  response = XMSApi.getApiEasyfact(getApplicationContext()).getClientes().execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_client.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getProductos().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_product.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getAlmacenes().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_almacenes.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getCondiciones().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_condiciones.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getDistritos().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_distritos.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getPersonal().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_personal.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getTCambio().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_tcambio.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getMarcas().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_marcas.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getUnidadMedida().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_unidad_medida.table);

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getMedidas().execute();
                    dataBaseHelper.sincro(response,TablesHelper.xms_medidas.table);

                    LocalDate today = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fechaToday = today.format(formatter);
                    //fechaToday = "2024-08-19";

                    response = XMSApi.getApiEasyfact(getApplicationContext()).getPedidos(fechaToday).execute();
                    dataBaseHelper.sincronizarPedido(response); */

                    app.setPref_lastSyncro(System.currentTimeMillis());
                    app.setPref_serieUsuario("001");
                    app.setPref_idPuntoVenta(1);

                    return Constants.CORRECT;
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
