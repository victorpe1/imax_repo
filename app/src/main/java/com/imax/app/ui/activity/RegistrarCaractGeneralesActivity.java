package com.imax.app.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.models.AsignacionModel;
import com.imax.app.models.CatalogModel;
import com.imax.app.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrarCaractGeneralesActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    public static final String EXTRA_DIRECCION = "direccion";

    public int ACCION_PEDIDO = ACCION_NUEVO_REGISTRO;

    public String idTipoDocumentoOriginal = "";

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;


    private TextView tvContactoSitu, tvInmuebleMultiple, tvRecibeInmueble, tvDireccionInSitu,
            tvDireccion, tvReferencia, tvDistrito, tvProvincia, tvDepartamento, tvNPisos;
    private Spinner spnTipoInmueble, spnRecibeInmueble;
    private EditText edOtro, edtComentarios, edtOtros, edtReferencia,
            edtNPisos, edtDeposito, edtEstacionamiento, edtDepto, edtDistribucion;
    private CheckBox cbVivienda, cbComercio, cbIndustria, cbEducativo, cbOther;
    private GridLayout gridCheckbox;

    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    private Drawable defaultBackground;
    private ProgressBar progress;
    private AntesInspeccion inspeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caracteristicas_general);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.caracteristicas), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);

        inspeccion = (AntesInspeccion) getIntent().getSerializableExtra("inspeccion");

        app = (App) getApplicationContext();
        daoExtras = new DAOExtras(getApplicationContext());

        spnTipoInmueble = findViewById(R.id.spn_modalidad);

        gridCheckbox = findViewById(R.id.grid_checkbox);

        cbVivienda = findViewById(R.id.cb_vivienda);
        cbComercio = findViewById(R.id.cb_comercio);
        cbIndustria = findViewById(R.id.cb_industria);
        cbEducativo = findViewById(R.id.cb_educativo);
        cbOther = findViewById(R.id.cb_other);

        edtComentarios = findViewById(R.id.edt_comentarios);
        spnRecibeInmueble = findViewById(R.id.spn_recibe_inmueble);

        edtReferencia = findViewById(R.id.edt_referencia);
        edtOtros = findViewById(R.id.edt_otros);
        edtNPisos = findViewById(R.id.edt_n_pisos);
        edtDeposito = findViewById(R.id.edt_deposito);
        edtEstacionamiento = findViewById(R.id.edt_d);
        edtDepto = findViewById(R.id.edt_depto);
        edtDistribucion = findViewById(R.id.edt_n_depart);

        loadDataTipoInmueble();
        loadDataIfExists(inspeccion.getNumInspeccion());

        progressDialog = new ProgressDialog(RegistrarCaractGeneralesActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);


        spnRecibeInmueble.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Suponiendo que la posición 0 es "Seleccione una opción"
                    spnRecibeInmueble.setBackground(ContextCompat.getDrawable(RegistrarCaractGeneralesActivity.this, android.R.drawable.edit_text));// Asegúrate de usar el drawable correcto
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cbVivienda.setOnCheckedChangeListener(checkboxListener);
        cbComercio.setOnCheckedChangeListener(checkboxListener);
        cbIndustria.setOnCheckedChangeListener(checkboxListener);
        cbEducativo.setOnCheckedChangeListener(checkboxListener);
        cbOther.setOnCheckedChangeListener(checkboxListener);

        configurarValidacionCampoError(edtNPisos);
        configurarValidacionCampoError(edtDistribucion);
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
                            ContextCompat.getDrawable(RegistrarCaractGeneralesActivity.this,
                                    R.drawable.error_border)); // Aplica el borde de error
                }
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener checkboxListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (cbVivienda.isChecked() || cbComercio.isChecked() || cbIndustria.isChecked() ||
                    cbEducativo.isChecked() || cbOther.isChecked()) {
                gridCheckbox.setBackground(defaultBackground);
            }
        }
    };

    private void loadDataTipoInmueble(){
        List<CatalogModel> modalidades = new ArrayList<>();
        for (AsignacionModel model : daoExtras.getListAsignacion()) {
            if(model.getNumber().equalsIgnoreCase(inspeccion.getNumInspeccion())){
                modalidades.add(new CatalogModel(model.getMotiveId(), model.getMotiveName()));
            }
        }

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoInmueble.setAdapter(adapter);
    }
    private void loadDataRecibeInmueble(String codigo) {
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("recide_inmueble");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRecibeInmueble.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnRecibeInmueble.setSelection(Math.max(position, 0));
    }

    private int getIndexByCodigo(List<CatalogModel> modalidades, String codigo) {
        for (int i = 0; i < modalidades.size(); i++) {
            if (modalidades.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    private void loadDataIfExists(String numero){
        InspeccionRequest inspeccionRequest =  daoExtras.getListAsignacionByNumero(numero);

        edtOtros.setText(inspeccionRequest.getOtros());
        if (inspeccionRequest.getUsosInmueble() != null && !inspeccionRequest.getUsosInmueble().trim().isEmpty()) {
            List<String> usosSeleccionados = Arrays.asList(inspeccionRequest.getUsosInmueble().split(","));
            cbVivienda.setChecked(usosSeleccionados.contains("001"));
            cbComercio.setChecked(usosSeleccionados.contains("002"));
            cbIndustria.setChecked(usosSeleccionados.contains("003"));
            cbEducativo.setChecked(usosSeleccionados.contains("004"));
            cbOther.setChecked(usosSeleccionados.contains("005"));
        }
        edtComentarios.setText(inspeccionRequest.getComentarios());
        edtNPisos.setText(inspeccionRequest.getnPisos());
        edtDistribucion.setText(inspeccionRequest.getDistribucion());
        edtReferencia.setText(inspeccionRequest.getReferencia());
        edtDeposito.setText(inspeccionRequest.getDeposito());
        edtEstacionamiento.setText(inspeccionRequest.getEstacionamiento());
        edtDepto.setText(inspeccionRequest.getDepto());

        loadDataRecibeInmueble(inspeccionRequest.getRecibeInmueble());
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
                    CatalogModel selectedItem = (CatalogModel) spnRecibeInmueble.getSelectedItem();
                    CatalogModel selectedTipoItem = (CatalogModel) spnTipoInmueble.getSelectedItem();

                    CaracteristicasGenerales caracteristicasGenerales = new CaracteristicasGenerales(
                            selectedTipoItem.getCodigo(),
                            edtOtros.getText().toString(),
                            cbVivienda.isChecked(),
                            cbComercio.isChecked(),
                            cbIndustria.isChecked(),
                            cbEducativo.isChecked(),
                            cbOther.isChecked(),
                            edtComentarios.getText().toString(),
                            selectedItem.getCodigo(),
                            edtNPisos.getText().toString(),
                            edtDistribucion.getText().toString(),
                            edtReferencia.getText().toString(),
                            edtDeposito.getText().toString(),
                            edtEstacionamiento.getText().toString(),
                            edtDepto.getText().toString()
                    );

                    daoExtras.actualizarRegistroCaracGeneral(caracteristicasGenerales, inspeccion.getNumInspeccion());

                    caracteristicasGenerales.setRecibeInmueble(spnRecibeInmueble.getSelectedItem().toString());
                    caracteristicasGenerales.setTipoInmueble(spnTipoInmueble.getSelectedItem().toString());

                    Intent intent = new Intent(RegistrarCaractGeneralesActivity.this, RegistroCaracteristicasEdificacionActivity.class);
                    intent.putExtra("inspeccion", inspeccion);
                    intent.putExtra("caracteristicasGenerales", caracteristicasGenerales);
                    startActivity(intent);
                }
                break;
            case R.id.menu_pedido_guardar:

                break;
            case android.R.id.home:
                System.out.println("VOLVER 1 SECCION");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validarCampos() {
        boolean isValid = true;
        Drawable errorBackground = ContextCompat.getDrawable(this, R.drawable.error_border);

        if (!cbVivienda.isChecked() && !cbComercio.isChecked() && !cbIndustria.isChecked()
                && !cbEducativo.isChecked() && !cbOther.isChecked()) {
            gridCheckbox.setBackground(errorBackground);
            isValid = false;
        } else {
            gridCheckbox.setBackground(defaultBackground);
        }

        isValid &= validarSpinner(spnTipoInmueble, errorBackground);
        isValid &= validarSpinner(spnRecibeInmueble, errorBackground);
        isValid &= validarEditText(edtDistribucion, errorBackground);
        isValid &= validarEditText(edtNPisos, errorBackground);

        if (!isValid) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private boolean validarEditText(EditText editText, Drawable errorBackground) {
        if (editText.getText().toString().isEmpty()) {
            editText.setBackground(errorBackground);
            return false;
        } else {
            editText.setBackground(defaultBackground);
            return true;
        }
    }
    private boolean validarSpinner(Spinner spinner, Drawable errorBackground) {
        String defaultValue = "Seleccione una opción";
        if (spinner.getSelectedItem().toString().equals(defaultValue)) {
            spinner.setBackground(errorBackground);
            return false;
        } else {
            spinner.setBackground(defaultBackground);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showLoader() {
        progressDialog.show();
    }
    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrarCaractGeneralesActivity.this);
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
