package com.sales.storeapp.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.tasks.LoginTask;
import com.sales.storeapp.ui.activity.SplashActivity;

public class LoginActivity extends AppCompatActivity {
    final String TAG = getClass().getName();

    private EditText edt_usuario, edt_contrasena;
    private TextView tv_error, tv_olvido_contrasena, tv_registrate;
    private Button btn_login;
    private ProgressDialog progressDialog;
    public App app;
    private DAOExtras daoExtras;
    private String correoExtra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (App) getApplicationContext();

        //region controles
        edt_usuario = findViewById(R.id.edt_usuario);
        edt_contrasena = findViewById(R.id.edt_contrasena);
        tv_error = findViewById(R.id.tv_error);
        tv_olvido_contrasena = findViewById(R.id.tv_olvido_contrasena);
        tv_registrate = findViewById(R.id.tv_registrate);
        btn_login = findViewById(R.id.btn_login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        daoExtras = new DAOExtras(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            correoExtra = bundle.getString("correo", "");
            edt_usuario.setText(correoExtra);
            edt_contrasena.requestFocus();
        }

        btn_login.setOnClickListener(v -> {
            login();
        });
    }

    private void login() {
        if (validarCampos()) {
            tv_error.setText("");
            tv_error.setVisibility(View.GONE);
            new LoginTask(this, edt_usuario.getText().toString(), edt_contrasena.getText().toString()).execute();
        }
    }

    private boolean validarCampos() {
        boolean flag = true;

        edt_usuario.setError(null);
        edt_contrasena.setError(null);

        if (TextUtils.isEmpty(edt_usuario.getText())) {
            edt_usuario.setError(getString(R.string.error_campo_requerido));
            flag = false;
        }
        if (TextUtils.isEmpty(edt_contrasena.getText())) {
            edt_contrasena.setError(getString(R.string.error_campo_requerido));
            flag = false;
        }
        return flag;
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void goToSplash() {
        app.setPref_sessionOpen(true);
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
