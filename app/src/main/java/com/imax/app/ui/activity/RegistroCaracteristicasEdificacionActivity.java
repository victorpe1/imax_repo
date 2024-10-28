package com.imax.app.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.imax.app.intents.CaracteristicasEdificacion;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.models.CatalogModel;
import com.imax.app.ui.pedido.AgregarProductoActivity;
import com.imax.app.ui.pedido.AgregarProductoArgument;
import com.imax.app.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RegistroCaracteristicasEdificacionActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    public static final String EXTRA_DIRECCION = "direccion";

    public int ACCION_PEDIDO = ACCION_NUEVO_REGISTRO;

    private final int REQUEST_CODE_AGREGAR_PRODUCTO = 0;

    public String idTipoDocumentoOriginal = "";

    private boolean cabeceraGuardada = false;

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;

    private TextView tv_radioGroup;
    private EditText edtOtroEstructura, edtOtroAlbañeria, edtOtroPisos, edtOtroPuertas,
            edtOtroVentana, edtOtroMampara, edtOtroCocina, edtOtroBaño, edtOtroSanitarias, edtOtroElectricas;
    private Spinner spnTipoPuerta, spnMaterialPuerta, spnSistemaPuerta,
            spnMarcoVentana, spnVidrioVentana, spnSistemaVentana,
            spnMarcoMampara, spnVidrioMampara, spnSistemaMampara,
            spnMuebleCocina, spnMuebleCocina2, spnTablero,
            spnLavaderos, spnSanitarioTipo, spnSanitarioColor,
            spnSanitario, spnIss, spnIiee;
    private Button btnSiguiente;
    RadioGroup radioGroupSistemaIncendio;
    RadioButton radioTiene, radioNoTiene;
    private MultiAutoCompleteTextView multiAutoCompleteTextView, multiCompleteMuros, multiCompleteRevestimiento,
            multiCompletePisos, multiCompletePisosCocina, multiCompleteParedesCocina,multiCompletePisosBaños,
            multiCompleteParedesBaño;

    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;
    private AntesInspeccion inspeccion;
    private CaracteristicasGenerales caracteristicasGenerales;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caracteristicas_edificacion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_back);

        inspeccion = (AntesInspeccion) getIntent().getSerializableExtra("inspeccion");
        caracteristicasGenerales = (CaracteristicasGenerales) getIntent().getSerializableExtra("caracteristicasGenerales");

        app = (App) getApplicationContext();

        daoExtras = new DAOExtras(getApplicationContext());

        edtOtroEstructura = findViewById(R.id.edt_otro_estructura);
        edtOtroAlbañeria = findViewById(R.id.edt_otros_albañeria);
        edtOtroPisos = findViewById(R.id.edt_otros_pisos);
        edtOtroPuertas = findViewById(R.id.edt_otros_puertas);
        edtOtroVentana = findViewById(R.id.edt_otros_ventana);
        edtOtroMampara = findViewById(R.id.edt_otros_mampara);
        edtOtroCocina = findViewById(R.id.edt_otros_cocina);
        edtOtroBaño = findViewById(R.id.edt_otros_baño);
        edtOtroSanitarias = findViewById(R.id.edt_otros_sanitarias);
        edtOtroElectricas = findViewById(R.id.edt_otros_electricas);
        spnTipoPuerta = findViewById(R.id.spn_tipo_puertas);
        spnMaterialPuerta = findViewById(R.id.spn_material_puertas);
        spnSistemaPuerta = findViewById(R.id.spn_sistemas_puertas);
        spnMarcoVentana = findViewById(R.id.spn_marco_ventana);
        spnVidrioVentana = findViewById(R.id.spn_vidrio_ventana);
        spnSistemaVentana = findViewById(R.id.spn_sistemas_ventana);
        spnMarcoMampara = findViewById(R.id.spn_marco_mamparas);
        spnVidrioMampara = findViewById(R.id.spn_vidrio_mamparas);
        spnSistemaMampara = findViewById(R.id.spn_sistemas_mamparas);
        spnMuebleCocina = findViewById(R.id.spn_mueble_cocina);
        spnMuebleCocina2 = findViewById(R.id.spn_mueble_cocina2);
        spnTablero = findViewById(R.id.spn_tableros);
        spnLavaderos = findViewById(R.id.spn_lavaderos);
        spnSanitarioTipo = findViewById(R.id.spn_mueble_baño);
        spnSanitarioColor = findViewById(R.id.spn_sanitario_color);
        spnSanitario = findViewById(R.id.spn_sanitarios);
        spnIss = findViewById(R.id.spn_iss);
        spnIiee = findViewById(R.id.spn_iiee);

        tv_radioGroup = findViewById(R.id.radioGroup);
        radioGroupSistemaIncendio = findViewById(R.id.radioGroupSistemaIncendio);
        radioTiene = findViewById(R.id.radio_tiene);
        radioNoTiene = findViewById(R.id.radio_no_tiene);
        multiAutoCompleteTextView = findViewById(R.id.multiCompleteEstructura);
        multiCompleteMuros = findViewById(R.id.multiCompleteMuros);
        multiCompleteRevestimiento = findViewById(R.id.multiCompleteRevestimiento);
        multiCompletePisos = findViewById(R.id.multiCompletePisos);
        multiCompletePisosCocina = findViewById(R.id.multiCompletePisosCocina);
        multiCompleteParedesCocina = findViewById(R.id.multiCompleteParedesCocina);
        multiCompletePisosBaños = findViewById(R.id.multiCompletePisosBaños);
        multiCompleteParedesBaño = findViewById(R.id.multiCompleteParedesBaños);

        loadDataIfExists(inspeccion.getNumInspeccion());

        progressDialog = new ProgressDialog(RegistroCaracteristicasEdificacionActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

    }

    private void loadDataIfExists(String numero){
        InspeccionRequest inspeccionRequest =  daoExtras.getListAsignacionByNumero(numero);

        loadDataEstructura(inspeccionRequest.getEstructura());
        loadDataMuro(inspeccionRequest.getMuros());
        loadDataRevestimiento(inspeccionRequest.getRevestimiento());
        loadDataPisos(inspeccionRequest.getPisos());
        loadDataPisosCocina(inspeccionRequest.getPisosCocina());
        loadDataParedesCocina(inspeccionRequest.getParedesCocina());
        loadDataPisosBaños(inspeccionRequest.getPisosBaños());
        loadDataParedesBaños(inspeccionRequest.getParedesBaño());

        loadDataTipoPuerta(inspeccionRequest.getTipoPuerta());
        loadDataMaterialPuerta(inspeccionRequest.getMaterialPuerta());
        loadDataSistemaPuerta(inspeccionRequest.getSistemaPuerta());
        loadDataMarcoVentana(inspeccionRequest.getMarcoVentana());
        loadDataVidrioVentana(inspeccionRequest.getVidrioVentana());
        loadDataSistemaVentana(inspeccionRequest.getSistemaVentana());
        loadDataMarcoMampara(inspeccionRequest.getMarcoMampara());
        loadDataVidrioMampara(inspeccionRequest.getVidrioMampara());
        loadDataSistemasMampara(inspeccionRequest.getSistemaMampara());
        loadDataMueblesCocina(inspeccionRequest.getMuebleCocina());
        loadDataMueblesCocina2(inspeccionRequest.getMuebleCocina2());
        loadDataTablero(inspeccionRequest.getTablero());
        loadDataLavadero(inspeccionRequest.getLavaderos());
        loadDataTipoSanitarios(inspeccionRequest.getSanitarioTipo());
        loadDataColorSanitarios(inspeccionRequest.getSanitarioColor());
        loadDataSanitarios(inspeccionRequest.getSanitario());
        loadDataISS(inspeccionRequest.getIss());
        loadDataIIEE(inspeccionRequest.getIiee());

        edtOtroEstructura.setText(inspeccionRequest.getOtroEstructura());
        edtOtroAlbañeria.setText(inspeccionRequest.getOtroAlbañeria());
        edtOtroBaño.setText(inspeccionRequest.getOtroBaño());
        edtOtroCocina.setText(inspeccionRequest.getOtroCocina());
        edtOtroElectricas.setText(inspeccionRequest.getOtroElectricas());
        edtOtroMampara.setText(inspeccionRequest.getOtroMampara());
        edtOtroPisos.setText(inspeccionRequest.getOtroPisos());
        edtOtroPuertas.setText(inspeccionRequest.getOtroPuertas());
        edtOtroSanitarias.setText(inspeccionRequest.getOtroSanitarias());
        edtOtroVentana.setText(inspeccionRequest.getOtroVentana());

        String sistemaIncendio = inspeccionRequest.getSistemaIncendio();

        if ("Tiene".equals(sistemaIncendio)) {
            radioTiene.setChecked(true);
            radioNoTiene.setChecked(false);
        } else if ("No tiene".equals(sistemaIncendio)) {
            radioNoTiene.setChecked(true);
            radioTiene.setChecked(false);
        } else {
            radioTiene.setChecked(false);
            radioNoTiene.setChecked(false);
        }

    }
    private void loadDataEstructura(String valoresBD) {
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("estructura");

        setupMultiAutoCompleteTextView(multiAutoCompleteTextView, opciones, valoresBD);
    }
    private void loadDataMuro(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("muros");

        setupMultiAutoCompleteTextView(multiCompleteMuros, opciones, valoresBD);
    }
    private void loadDataRevestimiento(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("revestimientos");

        setupMultiAutoCompleteTextView(multiCompleteRevestimiento, opciones, valoresBD);
    }
    private void loadDataPisos(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("pisos");

        setupMultiAutoCompleteTextView(multiCompletePisos, opciones, valoresBD);
    }
    private void loadDataPisosCocina(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("pisos_cocina");

        setupMultiAutoCompleteTextView(multiCompletePisosCocina, opciones, valoresBD);
    }
    private void loadDataParedesCocina(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("paredes_cocina");

        setupMultiAutoCompleteTextView(multiCompleteParedesCocina, opciones, valoresBD);
    }
    private void loadDataPisosBaños(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("piso_banio");

        setupMultiAutoCompleteTextView(multiCompletePisosBaños, opciones, valoresBD);
    }
    private void loadDataParedesBaños(String valoresBD){
        List<CatalogModel> opciones = daoExtras.obtenerCatalogDesdeDB("paredes_banio");

        setupMultiAutoCompleteTextView(multiCompleteParedesBaño, opciones, valoresBD);
    }

    private void setupMultiAutoCompleteTextView(MultiAutoCompleteTextView multiAuto,
                                                List<CatalogModel> opciones, String valoresBD) {
        Set<String> seleccionados = new LinkedHashSet<>();
        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(opciones)
        );

        multiAuto.setAdapter(adapter);
        multiAuto.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        multiAuto.setOnClickListener(v -> multiAuto.showDropDown());
        multiAuto.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                multiAuto.showDropDown();
            }
        });

        // Cargar valores iniciales
        if (valoresBD != null && !valoresBD.trim().isEmpty()) {
            String[] valoresSeleccionados = valoresBD.split(", ");
            for (String valor : valoresSeleccionados) {
                seleccionados.add(valor.trim());
            }
            String textoInicial = TextUtils.join(", ", seleccionados);
            multiAuto.setText(textoInicial);
            multiAuto.setSelection(multiAuto.getText().length());
        }

        multiAuto.setOnItemClickListener((parent, view, position, id) -> {
            CatalogModel seleccion = adapter.getItem(position);
            if (seleccion != null) {
                String selectedValue = seleccion.getCodigo(); // Obtén el código
                if (!seleccionados.contains(selectedValue)) {
                    seleccionados.add(selectedValue);
                    multiAuto.setText(TextUtils.join(", ", seleccionados) + ", ");
                    multiAuto.setSelection(multiAuto.getText().length());
                }
            }
        });

        multiAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String[] valores = s.toString().split(", ");
                seleccionados.clear();

                for (String valor : valores) {
                    if (isInCatalog(valor.trim(), opciones)) {
                        seleccionados.add(valor.trim());
                    }
                }
            }
        });
    }


    private boolean isInCatalog(String value, List<CatalogModel> catalog) {
        for (CatalogModel item : catalog) {
            if (item.getCodigo().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private int getIndexByCodigo(List<CatalogModel> modalidades, String codigo) {
        for (int i = 0; i < modalidades.size(); i++) {
            if (modalidades.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    private void loadDataTipoPuerta(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("tipo_puertas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoPuerta.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnTipoPuerta.setSelection(Math.max(position, 0));
    }
    private void loadDataMaterialPuerta(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("material_puertas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaterialPuerta.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnMaterialPuerta.setSelection(Math.max(position, 0));
    }
    private void loadDataSistemaPuerta(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("sistema_puertas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSistemaPuerta.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSistemaPuerta.setSelection(Math.max(position, 0));
    }
    private void loadDataMarcoVentana(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("marco_ventanas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMarcoVentana.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnMarcoVentana.setSelection(Math.max(position, 0));
    }
    private void loadDataVidrioVentana(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("vidrio_ventanas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVidrioVentana.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnVidrioVentana.setSelection(Math.max(position, 0));
    }
    private void loadDataSistemaVentana(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("sistema_ventanas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSistemaVentana.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSistemaVentana.setSelection(Math.max(position, 0));
    }
    private void loadDataMarcoMampara(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("marco_mamparas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMarcoMampara.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnMarcoMampara.setSelection(Math.max(position, 0));
    }
    private void loadDataVidrioMampara(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("vidrio_mamparas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVidrioMampara.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnVidrioMampara.setSelection(Math.max(position, 0));
    }
    private void loadDataSistemasMampara(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("sistema_mamparas");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSistemaMampara.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSistemaMampara.setSelection(Math.max(position, 0));
    }
    private void loadDataMueblesCocina(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("muebles_cocina");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMuebleCocina.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnMuebleCocina.setSelection(Math.max(position, 0));
    }
    private void loadDataMueblesCocina2(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("mueble_cocina_material");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMuebleCocina2.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnMuebleCocina2.setSelection(Math.max(position, 0));
    }
    private void loadDataTablero(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("tipo_taleros");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTablero.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnTablero.setSelection(Math.max(position, 0));
    }
    private void loadDataLavadero(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("material_lavadero");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLavaderos.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnLavaderos.setSelection(Math.max(position, 0));
    }
    private void loadDataTipoSanitarios(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("tipo_sanitario");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSanitarioTipo.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSanitarioTipo.setSelection(Math.max(position, 0));
    }
    private void loadDataColorSanitarios(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("color_sanitario");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSanitarioColor.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSanitarioColor.setSelection(Math.max(position, 0));
    }
    private void loadDataSanitarios(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("sanitario");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSanitario.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnSanitario.setSelection(Math.max(position, 0));
    }
    private void loadDataISS(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("iiss");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIss.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnIss.setSelection(Math.max(position, 0));
    }
    private void loadDataIIEE(String codigo){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("iiee");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIiee.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spnIiee.setSelection(Math.max(position, 0));
    }

    private boolean validateSpinner(Spinner spinner, String defaultValue, Drawable errorBackground, Drawable defaultBackground) {
        if (spinner.getSelectedItem().toString().equals(defaultValue)) {
            spinner.setBackground(errorBackground);
            return false;
        } else {
            spinner.setBackground(defaultBackground);
            return true;
        }
    }
    private boolean validateMultiAutoComplete(MultiAutoCompleteTextView multiAutoComplete, Drawable errorBackground, Drawable defaultBackground) {
        if (multiAutoComplete.getText().toString().trim().isEmpty()) {
            multiAutoComplete.setBackground(errorBackground);
            return false;
        } else {
            multiAutoComplete.setBackground(defaultBackground);
            return true;
        }
    }

    private boolean validarCampos() {
        String defaultValue = "Seleccione una opción";
        boolean isValid = true;

        Drawable errorBackground = ContextCompat.getDrawable(this, R.drawable.error_border);
        Drawable defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

        List<Spinner> spinners = Arrays.asList(
                spnTipoPuerta, spnMaterialPuerta, spnSistemaPuerta,
                spnMarcoVentana, spnVidrioVentana, spnSistemaVentana,
                spnMuebleCocina, spnMuebleCocina2, spnTablero, spnLavaderos,
                spnSanitarioTipo, spnSanitarioColor, spnSanitario, spnIss, spnIiee
        );

        for (Spinner spinner : spinners) {
            if (!validateSpinner(spinner, defaultValue, errorBackground, defaultBackground)) {
                isValid = false;
            }
        }

        List<MultiAutoCompleteTextView> multiAutoCompletes = Arrays.asList(
                multiAutoCompleteTextView, multiCompleteMuros, multiCompleteRevestimiento,
                multiCompletePisos, multiCompletePisosCocina, multiCompleteParedesCocina,
                multiCompletePisosBaños, multiCompleteParedesBaño
        );

        // Validar MultiAutoCompleteTextViews
        for (MultiAutoCompleteTextView multiAutoComplete : multiAutoCompletes) {
            if (!validateMultiAutoComplete(multiAutoComplete, errorBackground, defaultBackground)) {
                isValid = false;
            }
        }

        if (radioGroupSistemaIncendio.getCheckedRadioButtonId() == -1) {
            isValid = false;
            tv_radioGroup.setBackground(errorBackground);
        } else {
            tv_radioGroup.setBackground(defaultBackground);
        }

        return isValid;
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
                    CatalogModel selectedSpnTipoPuerta = (CatalogModel) spnTipoPuerta.getSelectedItem();
                    CatalogModel selectedSpnMaterialPuerta = (CatalogModel) spnMaterialPuerta.getSelectedItem();
                    CatalogModel selectedSpnSistemaPuerta = (CatalogModel) spnSistemaPuerta.getSelectedItem();
                    CatalogModel selectedSspnMarcoVentana = (CatalogModel) spnMarcoVentana.getSelectedItem();
                    CatalogModel selectedSpnVidrioVentana = (CatalogModel) spnVidrioVentana.getSelectedItem();
                    CatalogModel selectedSpnSistemaVentana = (CatalogModel) spnSistemaVentana.getSelectedItem();
                    CatalogModel selectedSpnMarcoMampara = (CatalogModel) spnMarcoMampara.getSelectedItem();
                    CatalogModel selectedSpnVidrioMampara = (CatalogModel) spnVidrioMampara.getSelectedItem();
                    CatalogModel selectedSpnSistemaMampara = (CatalogModel) spnSistemaMampara.getSelectedItem();
                    CatalogModel selectedSpnMuebleCocina = (CatalogModel) spnMuebleCocina.getSelectedItem();
                    CatalogModel selectedSpnMuebleCocina2 = (CatalogModel) spnMuebleCocina2.getSelectedItem();
                    CatalogModel selectedSpnTablero = (CatalogModel) spnTablero.getSelectedItem();
                    CatalogModel selectedSpnLavaderos = (CatalogModel) spnLavaderos.getSelectedItem();
                    CatalogModel selectedSpnSanitarioTipo = (CatalogModel) spnSanitarioTipo.getSelectedItem();
                    CatalogModel selectedSpnSanitarioColor = (CatalogModel) spnSanitarioColor.getSelectedItem();
                    CatalogModel selectedSpnSanitario = (CatalogModel) spnSanitario.getSelectedItem();
                    CatalogModel selectedSpnIss = (CatalogModel) spnIss.getSelectedItem();
                    CatalogModel selectedSpIiee = (CatalogModel) spnIiee.getSelectedItem();

                    CaracteristicasEdificacion datos = new CaracteristicasEdificacion();

                    datos.setOtroEstructura(edtOtroEstructura.getText().toString());
                    datos.setOtroAlbañeria(edtOtroAlbañeria.getText().toString());
                    datos.setOtroPisos(edtOtroPisos.getText().toString());
                    datos.setOtroPuertas(edtOtroPuertas.getText().toString());
                    datos.setOtroVentana(edtOtroVentana.getText().toString());
                    datos.setOtroMampara(edtOtroMampara.getText().toString());
                    datos.setOtroCocina(edtOtroCocina.getText().toString());
                    datos.setOtroBaño(edtOtroBaño.getText().toString());
                    datos.setOtroSanitarias(edtOtroSanitarias.getText().toString());
                    datos.setOtroElectricas(edtOtroElectricas.getText().toString());

                    datos.setTipoPuerta(selectedSpnTipoPuerta.getCodigo());
                    datos.setMaterialPuerta(selectedSpnMaterialPuerta.getCodigo());
                    datos.setSistemaPuerta(selectedSpnSistemaPuerta.getCodigo());
                    datos.setMarcoVentana(selectedSspnMarcoVentana.getCodigo());
                    datos.setVidrioVentana(selectedSpnVidrioVentana.getCodigo());
                    datos.setSistemaVentana(selectedSpnSistemaVentana.getCodigo());
                    datos.setMarcoMampara(selectedSpnMarcoMampara.getCodigo());
                    datos.setVidrioMampara(selectedSpnVidrioMampara.getCodigo());
                    datos.setSistemaMampara(selectedSpnSistemaMampara.getCodigo());
                    datos.setMuebleCocina(selectedSpnMuebleCocina.getCodigo());
                    datos.setMuebleCocina2(selectedSpnMuebleCocina2.getCodigo());
                    datos.setTablero(selectedSpnTablero.getCodigo());
                    datos.setLavaderos(selectedSpnLavaderos.getCodigo());
                    datos.setSanitarioTipo(selectedSpnSanitarioTipo.getCodigo());
                    datos.setSanitarioColor(selectedSpnSanitarioColor.getCodigo());
                    datos.setSanitario(selectedSpnSanitario.getCodigo());
                    datos.setIss(selectedSpnIss.getCodigo());
                    datos.setIiee(selectedSpIiee.getCodigo());

                    if (radioTiene.isChecked()) {
                        datos.setSistemaIncendio("Tiene");
                    } else if (radioNoTiene.isChecked()) {
                        datos.setSistemaIncendio("No tiene");
                    }

                    datos.setEstructura(multiAutoCompleteTextView.getText().toString());
                    datos.setMuros(multiCompleteMuros.getText().toString());
                    datos.setRevestimiento(multiCompleteRevestimiento.getText().toString());
                    datos.setPisos(multiCompletePisos.getText().toString());
                    datos.setPisosCocina(multiCompletePisosCocina.getText().toString());
                    datos.setParedesCocina(multiCompleteParedesCocina.getText().toString());
                    datos.setPisosBaños(multiCompletePisosBaños.getText().toString());
                    datos.setParedesBaño(multiCompleteParedesBaño.getText().toString());

                    daoExtras.actualizarRegistroCaracEdificacion(datos, inspeccion.getNumInspeccion());

                    Intent intent = new Intent(RegistroCaracteristicasEdificacionActivity.this, RegistroCaractInfraestruturaActivity.class);
                    intent.putExtra("inspeccion", inspeccion);
                    intent.putExtra("caracteristicasGenerales", caracteristicasGenerales);
                    intent.putExtra("caracteristicasEdificacion", datos);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroCaracteristicasEdificacionActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_AGREGAR_PRODUCTO: {
                if (resultCode == RESULT_OK) {
                    AgregarProductoArgument argument = (AgregarProductoArgument) data.getSerializableExtra(
                            AgregarProductoActivity.EXTRA_PRODUCTO_AGREGADO);

                }
                break;
            }
        }
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroCaracteristicasEdificacionActivity.this);
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
