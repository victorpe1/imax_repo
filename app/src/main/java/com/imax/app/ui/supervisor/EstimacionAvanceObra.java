package com.imax.app.ui.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.supervisor.InspeccionSupervisor_1;
import com.imax.app.intents.supervisor.RegistroTabla;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.models.CatalogModel;
import com.imax.app.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EstimacionAvanceObra extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;

    private DAOExtras daoExtras;

    private Drawable defaultBackground;
    private Calendar calendar;
    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    Button btnOpenMaps;
    private ProgressBar progress;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<AsignacionModel> lista = new ArrayList<>();

    private Spinner spinnerMoneda;
    private EditText editTipoCambio, edtObervacion;
    private TextView totalPresupuesto, currency;
    private TextView totalAvance;
    private Button btnCalcular;
    private InspeccionSupervisor_1 inspeccion;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimacion_avance_obra);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.registro_supervisor), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        calendar = Calendar.getInstance();
        daoExtras = new DAOExtras(getApplicationContext());

        spinnerMoneda = findViewById(R.id.spinnerMoneda);
        editTipoCambio = findViewById(R.id.editTipoCambio);
        totalPresupuesto = findViewById(R.id.totalPresupuesto);
        totalAvance = findViewById(R.id.totalAvance);
        edtObervacion = findViewById(R.id.edt_observaciones);
        tableLayout = findViewById(R.id.tableLayout);
        currency = findViewById(R.id.currency);

        inspeccion = (InspeccionSupervisor_1) getIntent().getSerializableExtra("InspeccionSupervisor_1");

        addFocusChangeValidation(editTipoCambio, false, false, null);
        for (int i = 1; i <= 15; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Crear un EditText para la descripción
            EditText descripcionEditText = new EditText(this);
            descripcionEditText.setHint("Descripción " + i);
            descripcionEditText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.5f));

            // Crear un EditText para el presupuesto
            EditText presupuestoEditText = new EditText(this);
            presupuestoEditText.setHint("Presupuesto");
            presupuestoEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            presupuestoEditText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            addFocusChangeValidation(presupuestoEditText, true, false, null);

            // Crear un FrameLayout para el avance
            FrameLayout avanceLayout = new FrameLayout(this);
            avanceLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

            View barraProgreso = new View(this);
            barraProgreso.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));

            // Crear un EditText para el avance estimado
            EditText avanceEditText = new EditText(this);
            avanceEditText.setHint("Avance %");
            avanceEditText.setText("0,00 %");
            avanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            avanceEditText.setBackgroundColor(Color.TRANSPARENT);
            avanceEditText.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            ));
            avanceEditText.setGravity(Gravity.CENTER);
            avanceEditText.addTextChangedListener(new TextWatcher() {
                private boolean isUpdating = false; // Evita bucles infinitos

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isUpdating) {
                        return;
                    }

                    isUpdating = true;

                    String input = s.toString();

                    if (input.contains("%")) {
                        input = input.replace("%", "").trim();
                    }

                    String formatted = input + " %";
                    avanceEditText.setText(formatted);
                    avanceEditText.setSelection(input.length());

                    actualizarBarraProgreso(barraProgreso, avanceEditText);

                    isUpdating = false;
                }
            });
            addFocusChangeValidation(avanceEditText, true, true, barraProgreso);

            avanceLayout.addView(barraProgreso);
            avanceLayout.addView(avanceEditText);

            tableRow.addView(descripcionEditText);
            tableRow.addView(presupuestoEditText);

            tableRow.addView(avanceLayout);

            tableLayout.addView(tableRow);
        }

        loadSavedData(inspeccion.getNumInspeccion());

        spinnerMoneda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = parent.getItemAtPosition(position).toString();
                currency.setText(selectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        progressDialog = new ProgressDialog(EstimacionAvanceObra.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

    }

    private void loadSavedData(String numeroAsignacion){
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numeroAsignacion);
        String detallesJson = inspeccionRequest.getDetalles();

        loadData(inspeccionRequest.getMoneda(), spinnerMoneda);
        editTipoCambio.setText(inspeccionRequest.getTipoMoneda() != null ? inspeccionRequest.getTipoMoneda() : "");
        totalPresupuesto.setText(String.valueOf(inspeccionRequest.getPresupuesto()));
        totalAvance.setText(String.valueOf(inspeccionRequest.getTotalesAvance()));
        edtObervacion.setText(inspeccionRequest.getDescripcion() != null ? inspeccionRequest.getDescripcion() : "");

        if (detallesJson != null && !detallesJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<RegistroTabla>>() {}.getType();
            List<RegistroTabla> filas = gson.fromJson(detallesJson, listType);

            int i = 0;
            for (RegistroTabla fila : filas) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);

                EditText descripcionEditText = (EditText) row.getChildAt(0);
                EditText presupuestoEditText = (EditText) row.getChildAt(1);
                FrameLayout avanceLayout = (FrameLayout) row.getChildAt(2);

                View barraProgreso = avanceLayout.getChildAt(0);
                EditText avanceEditText = (EditText) avanceLayout.getChildAt(1);

                descripcionEditText.setText(fila.getDescripcion());
                presupuestoEditText.setText(String.valueOf(fila.getPresupuesto()));

                double avance = fila.getAvance();
                String textoAvance = String.format("%.2f %%", avance);
                avanceEditText.setText(textoAvance);

                actualizarBarraProgreso(barraProgreso, avanceEditText);

                i++;
            }
        }

    }

    private void actualizarBarraProgreso(View barraProgreso, EditText avanceEditText) {
        barraProgreso.setBackgroundColor(Color.LTGRAY);
        String textoAvance = avanceEditText.getText().toString().replace("%", "").replace(",", ".").trim();

        try {
            double avance = Double.parseDouble(textoAvance);

            if (avance < 0) avance = 0;
            if (avance > 100) avance = 100;

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) barraProgreso.getLayoutParams();
            params.width = 0;
            barraProgreso.setLayoutParams(params);

            int barraAncho = ((View) barraProgreso.getParent()).getWidth(); // Usamos el ancho del padre (si es el contenedor de la barra)
            System.out.println("BARRA ANCHO -> " + barraAncho);
            if (barraAncho == 0) {
                barraAncho = 155;
            }

            int nuevoAncho = (int) (avance * barraAncho / 100);

            params.width = nuevoAncho;
            barraProgreso.setLayoutParams(params);

            int color = Color.rgb(
                    (int) (255 - (avance * 2.55)),  // Rojo disminuye con el progreso
                    (int) (avance * 2.55),          // Verde aumenta con el progreso
                    0                               // Azul permanece constante
            );

            barraProgreso.setBackgroundColor(color);
        } catch (NumberFormatException e) {
            Log.e("ErrorBarraProgreso", "Error al convertir avance: " + e.getMessage());
        }
    }

    private void addFocusChangeValidation(EditText editText, boolean actualizar,
                                          boolean isAvance, View barraProgreso) {
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String input = editText.getText().toString();

                if (!input.isEmpty()) {
                    try {
                        input = input.replace(",", ".");
                        input = input.replaceAll("[^0-9.]", "");

                        double value = Double.parseDouble(input);
                        String formatted = String.format("%.2f", value);

                        editText.setText(formatted);
                    } catch (NumberFormatException e) {
                        editText.setText("");
                        Log.e("ValidationError", "Error al convertir el número: " + e.getMessage());
                    }

                    if(actualizar)actualizarCalculos();
                    if(isAvance){
                        actualizarBarraProgreso(barraProgreso, editText);
                    }
                }
            }
        });
    }

    private void actualizarCalculos() {
        double totalPresupuesto = 0.0;
        double sumaAvancePresupuesto = 0.0;
        int contadorPresupuestosValidos = 0;

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow fila = (TableRow) tableLayout.getChildAt(i);
            EditText presupuestoEditText = (EditText) fila.getChildAt(1);

            FrameLayout avanceLayout = (FrameLayout) fila.getChildAt(2);
            EditText avanceEditText = (EditText) avanceLayout.getChildAt(1);

            String presupuestoTexto = presupuestoEditText.getText().toString().replace(",", ".");
            try {
                double presupuesto = Double.parseDouble(presupuestoTexto);
                if (presupuesto > 0) {
                    totalPresupuesto += presupuesto;
                    contadorPresupuestosValidos++;

                    String avanceTexto = avanceEditText.getText().toString().replace("%", "").replace(",", ".").trim();
                    try {
                        double avance = Double.parseDouble(avanceTexto) / 100.0;
                        sumaAvancePresupuesto += avance * presupuesto;
                    } catch (NumberFormatException e) {
                        Log.e("ErrorAvance", "Error al convertir avance: " + e.getMessage());
                    }
                }
            } catch (NumberFormatException e) {
                Log.e("ErrorPresupuesto", "Error al convertir presupuesto: " + e.getMessage());
            }
        }

        TextView totalPresupuestoTextView = findViewById(R.id.totalPresupuesto);
        TextView totalAvanceTextView = findViewById(R.id.totalAvance);

        totalPresupuestoTextView.setText(String.format(Locale.US, "%.2f", totalPresupuesto));
        if (totalPresupuesto > 0) {
            double promedioAvance = sumaAvancePresupuesto / totalPresupuesto;
            totalAvanceTextView.setText(String.format(Locale.US, "%.2f%%", promedioAvance * 100));
        } else {
            totalAvanceTextView.setText("0.00 %");
        }
    }

    private void loadData(String codigo, Spinner spn){
        List<CatalogModel> modalidades = daoExtras.obtenerMonedas();

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spn.setSelection(Math.max(position, 0));
    }

    private int getIndexByCodigo(List<CatalogModel> modalidades, String codigo) {
        for (int i = 0; i < modalidades.size(); i++) {
            if (modalidades.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
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
                if (validarDatos()) {
                    actualizarCalculos();

                    List<RegistroTabla> filas = new ArrayList<>();
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        TableRow row = (TableRow) tableLayout.getChildAt(i);
                        EditText descripcionEditText = (EditText) row.getChildAt(0);
                        EditText presupuestoEditText = (EditText) row.getChildAt(1);
                        EditText avanceEditText = (EditText) ((FrameLayout) row.getChildAt(2)).getChildAt(1);

                        if(!descripcionEditText.getText().toString().trim().isEmpty()){
                            String descripcion = descripcionEditText.getText().toString().trim();
                            double presupuesto = Double.parseDouble(presupuestoEditText.getText().toString().trim().replace(",", "."));

                            String avance2 = avanceEditText.getText().toString().replace(" %", "").trim();
                            double avance = Double.parseDouble(avance2.trim().replace(",", "."));

                            filas.add(new RegistroTabla(descripcion, presupuesto, avance));
                        }
                    }

                    Gson gson = new Gson();
                    String detallesJson = gson.toJson(filas);

                    CatalogModel selectedItem = (CatalogModel) spinnerMoneda.getSelectedItem();
                    String tipoMoneda = editTipoCambio.getText().toString();
                    String descripcion = edtObervacion.getText().toString();
                    double presupuesto = Double.parseDouble(totalPresupuesto.getText().toString().trim().replace(",", "."));

                    String avance2 = totalAvance.getText().toString().replace("%", "").trim();
                    double avance = Double.parseDouble(avance2.trim().replace(",", "."));


                    daoExtras.actualizarRegistroSupervisor4(inspeccion.getNumInspeccion(),
                            detallesJson, selectedItem.getCodigo(),
                            tipoMoneda, descripcion, presupuesto, avance);

                    Intent intent = new Intent(EstimacionAvanceObra.this, RegistroCalidad.class);
                    intent.putExtra("InspeccionSupervisor_1", inspeccion);
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


    public boolean validarDatos() {
        if(editTipoCambio.getText().toString().equals("-")){
            Toast.makeText(this, "No existe un tipo de cambio", Toast.LENGTH_SHORT).show();
            editTipoCambio.setError("No existe un tipo de cambio.");
            return false; //false
        }

        int verifyRegistry = 0;
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            EditText descripcionEditText = (EditText) row.getChildAt(0);
            EditText presupuestoEditText = (EditText) row.getChildAt(1);

            String descripcion = descripcionEditText.getText().toString().trim();
            double presupuesto = 0.0;
            String presupuestoComa;
            try {
                if (presupuestoEditText.getText().toString().trim().isEmpty()) {
                    presupuesto = 0.0;
                }else{
                    presupuestoComa = presupuestoEditText.getText().toString().trim().replace(",", ".");
                    presupuesto = Double.parseDouble(presupuestoComa);
                }

                System.out.println("presupuesto -> " + presupuesto);
            } catch (NumberFormatException e) {
                System.out.println("ERROR -> " + e);
                presupuesto = 0.0;
            }

            if (descripcion.trim().isEmpty() && presupuesto != 0.0) {
                Toast.makeText(this, "Deberiamos tener una descripcion asociada al presupuesto.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if(descripcion.trim().isEmpty() && presupuesto == 0.0){
                verifyRegistry++;
            }

        }

        System.out.println("count tableLayout.getChildCount() " + tableLayout.getChildCount());
        System.out.println("count " + verifyRegistry);
        if(verifyRegistry == tableLayout.getChildCount()){
            Toast.makeText(this, "Deberiamos tener al menos 1 registro.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EstimacionAvanceObra.this);
        builder.setTitle(getString(R.string.descartar_cambios));
        builder.setMessage(getString(R.string.se_perdera_cambios));
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();

    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EstimacionAvanceObra.this);
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
