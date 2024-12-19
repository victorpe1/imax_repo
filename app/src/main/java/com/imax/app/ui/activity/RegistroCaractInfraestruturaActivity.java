package com.imax.app.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.AntesInspeccion;
import com.imax.app.intents.CaracteristicasEdificacion;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.utils.Util;

public class RegistroCaractInfraestruturaActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    public static final String EXTRA_DIRECCION = "direccion";

    public int ACCION_PEDIDO = ACCION_NUEVO_REGISTRO;

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;

    private EditText edt_comentarios;

    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    private AntesInspeccion inspeccion;
    private CaracteristicasGenerales caracteristicasGenerales;
    private CaracteristicasEdificacion caracteristicasEdificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicio_entorno);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_back);

        inspeccion = (AntesInspeccion) getIntent().getSerializableExtra("inspeccion");
        caracteristicasGenerales = (CaracteristicasGenerales) getIntent().getSerializableExtra("caracteristicasGenerales");
        caracteristicasEdificacion = (CaracteristicasEdificacion) getIntent().getSerializableExtra("caracteristicasEdificacion");

        app = (App) getApplicationContext();

        daoExtras = new DAOExtras(getApplicationContext());
        edt_comentarios = findViewById(R.id.edt_comentarios);

        loadDataIfExists(inspeccion.getNumInspeccion());

        progressDialog = new ProgressDialog(RegistroCaractInfraestruturaActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        configurarValidacionCampoError(edt_comentarios);
    }


    private void configurarValidacionCampoError(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText.setBackgroundResource(android.R.drawable.edit_text);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    editText.setBackground(
                            ContextCompat.getDrawable(RegistroCaractInfraestruturaActivity.this,
                                    R.drawable.error_border));
                }
            }
        });
    }

    private void loadDataIfExists(String numero){
        InspeccionRequest inspeccionRequest =  daoExtras.getListAsignacionByNumero(numero);

        edt_comentarios.setText(inspeccionRequest.getInfraestructura_comentario());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        menu.findItem(R.id.menu_pedido_siguiente).setVisible(true);
        menu.findItem(R.id.menu_pedido_guardar).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_pedido_siguiente:
                if (validarCampos()) {
                    daoExtras.actualizarRegistroInfraestructura(edt_comentarios.getText().toString(),
                                        inspeccion.getNumInspeccion());

                    Intent intent = new Intent(RegistroCaractInfraestruturaActivity.this, RegistroDespuesInspeccionActivity.class);
                    intent.putExtra("infraestructura_comentario", edt_comentarios.getText().toString());
                    intent.putExtra("inspeccion", inspeccion);
                    intent.putExtra("caracteristicasGenerales", caracteristicasGenerales);
                    intent.putExtra("caracteristicasEdificacion", caracteristicasEdificacion);
                    startActivity(intent);
                }
                break;
            case R.id.menu_pedido_guardar:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarCampos() {
        boolean isValid = true;
        Drawable errorBackground = ContextCompat.getDrawable(this, R.drawable.error_border);

        isValid &= validarEditText(edt_comentarios, errorBackground);

        if (!isValid) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private boolean validarEditText(EditText editText, Drawable errorBackground) {
        Drawable defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

        if (editText.getText().toString().isEmpty()) {
            editText.setBackground(errorBackground);
            return false;
        } else {
            editText.setBackground(defaultBackground);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroCaractInfraestruturaActivity.this);
        if (ACCION_PEDIDO == ACCION_NUEVO_REGISTRO) {
            builder.setTitle(getString(R.string.descartar_pedido));
            builder.setMessage(getString(R.string.se_perdera_pedido));
        } else {
            builder.setTitle(getString(R.string.descartar_cambios));
            builder.setMessage(getString(R.string.se_perdera_cambios));
        }
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();

    }

    void showAlertError(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_dialog_error);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.aceptar), (dialog, which) -> finish());
        builder.show();
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroCaractInfraestruturaActivity.this);
        builder.setTitle(tituloRes);
        builder.setMessage(mensajeRes);
        builder.setIcon(icon);
        builder.setCancelable(false);

       builder.setPositiveButton(getString(R.string.aceptar), (dialog, which) -> {
            /*Intent intent = new Intent(getApplicationContext(), DetallePedidoActivity.class);
            intent.putExtra("numeroPedido",numeroPedido);
            intent.putExtra("idCliente",idCliente);
            intent.putExtra("nombreCliente",autocomplete_busqueda.getText().toString());
            startActivity(intent); */
            setResult(RESULT_OK);
            finish();
        });
        builder.show();
    }

}
