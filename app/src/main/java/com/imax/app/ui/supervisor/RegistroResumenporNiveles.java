package com.imax.app.ui.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
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
import com.imax.app.intents.supervisor.InspeccionSupervisor_3;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistroResumenporNiveles extends AppCompatActivity {
    private final String TAG = getClass().getName();

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
    private InspeccionSupervisor_1 inspeccion;

    private EditText ed_Torres;
    private TableLayout tableLayout;
    private Button agregar1, eliminar1;
    private Spinner spn_Torres1;
    private ArrayAdapter<String> torreAdapter;
    private ArrayList<String> listaTorres;
    private ArrayList<LinearLayout> torresLayouts;

    private int numeroTorres, numeroPisos, numeroSotanos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_por_niveles);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.registro_supervisor), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        calendar = Calendar.getInstance();
        daoExtras = new DAOExtras(getApplicationContext());

        agregar1 = findViewById(R.id.Agregar1);
        eliminar1 = findViewById(R.id.Eliminar1);
        spn_Torres1 = findViewById(R.id.spn_Torres1);
        tableLayout = findViewById(R.id.tableLayout1);

        inspeccion = (InspeccionSupervisor_1) getIntent().getSerializableExtra("InspeccionSupervisor_1");

        listaTorres = new ArrayList<>();
        torresLayouts = new ArrayList<>();

        numeroTorres = Integer.parseInt(inspeccion.getTorres());
        numeroPisos = Integer.parseInt(inspeccion.getPisos());
        numeroSotanos = Integer.parseInt(inspeccion.getSotanos());

        for (int i = 1; i <= numeroTorres; i++) {
            listaTorres.add("Torre " + i);
            agregarNuevaTorre("Torre " + i); // Crear la torre inicial
        }

        torreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaTorres);
        torreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Torres1.setAdapter(torreAdapter);

        loadSavedData(inspeccion.getNumInspeccion());

        agregar1.setOnClickListener(v -> {
            String torreSeleccionada = spn_Torres1.getSelectedItem().toString();
            agregarDetalleTorre(torreSeleccionada, null);
        });

        eliminar1.setOnClickListener(v -> {
            String torreSeleccionada = spn_Torres1.getSelectedItem().toString();
            eliminarUltimoDetalle(torreSeleccionada);
        });

        progressDialog = new ProgressDialog(RegistroResumenporNiveles.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

    }

    private void agregarNuevaTorre(String nombreTorre) {
        LinearLayout torreLayout = new LinearLayout(this);
        torreLayout.setOrientation(LinearLayout.VERTICAL);
        torreLayout.setPadding(8, 8, 8, 8);
        torreLayout.setBackgroundResource(android.R.drawable.edit_text);

        TextView torre = new TextView(this);
        torre.setText(nombreTorre);
        torre.setTextSize(18);
        torreLayout.addView(torre);

        tableLayout.addView(torreLayout);
        torresLayouts.add(torreLayout);
    }

    private void loadSavedData(String numeroAsignacion) {
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numeroAsignacion);
        String detallesJson = inspeccionRequest.getResumenNiveles();

        if (detallesJson != null && !detallesJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<InspeccionSupervisor_3>>() {
            }.getType();
            List<InspeccionSupervisor_3> filas = gson.fromJson(detallesJson, listType);

            for (InspeccionSupervisor_3 fila : filas) {
                agregarDetalleTorre(fila.getTorre(), fila);
            }
        }

    }

    private void agregarDetalleTorre(String torreSeleccionada, InspeccionSupervisor_3 detalle) {
        int index = (detalle != null)
                ? Integer.parseInt(detalle.getTorre()) - 1
                : listaTorres.indexOf(torreSeleccionada);

        if (index == -1) return; // Validar que el índice sea válido
        LinearLayout torreLayout = torresLayouts.get(index);

        // Contenedor principal
        LinearLayout contenedorPrincipal = new LinearLayout(this);
        contenedorPrincipal.setOrientation(LinearLayout.VERTICAL);
        contenedorPrincipal.setPadding(16, 16, 16, 16);

        // Layout para los Spinners
        LinearLayout deALayout = new LinearLayout(this);
        deALayout.setOrientation(LinearLayout.HORIZONTAL);

        // Spinner De
        Spinner spinnerDe = new Spinner(this);
        ArrayAdapter<String> adapterDe = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                generarOpciones(true)
        );
        adapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDe.setAdapter(adapterDe);

        // Spinner A
        Spinner spinnerA = new Spinner(this);
        ArrayAdapter<String> adapterA = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                generarOpciones(false)
        );
        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerA.setAdapter(adapterA);

        // Configurar selección inicial si se proporciona un DetalleTorre
        if (detalle != null) {
            spinnerDe.setSelection(adapterDe.getPosition(detalle.getDe()));
            spinnerA.setSelection(adapterA.getPosition(detalle.getA()));
        }

        // Ajustar layout de Spinners
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                370, // Ancho en píxeles
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerDe.setLayoutParams(layoutParams);
        spinnerA.setLayoutParams(layoutParams);

        deALayout.addView(spinnerDe);
        deALayout.addView(spinnerA);
        contenedorPrincipal.addView(deALayout);

        // EditText: Descripción
        EditText descripcion = new EditText(this);
        if (detalle != null) {
            descripcion.setText(detalle.getDescripcion());
        } else {
            descripcion.setHint("Descripción avance");
        }
        descripcion.setMinLines(3);
        descripcion.setPadding(8, 8, 8, 8);
        descripcion.setBackgroundResource(android.R.drawable.edit_text);
        contenedorPrincipal.addView(descripcion);

        // Añadir contenedor a la torre
        torreLayout.addView(contenedorPrincipal);
    }


    private List<String> generarOpciones(boolean isDe) {
        List<String> opciones = new ArrayList<>();
        opciones.add(isDe ? "De" : "A");
        for (int i = 1; i <= numeroSotanos; i++) {
            opciones.add("S" + String.format("%02d", i));
        }
        for (int i = 1; i <= numeroPisos; i++) {
            opciones.add("P" + String.format("%02d", i));
        }
        opciones.add("SS");
        opciones.add("Az");
        opciones.add("Tech");
        return opciones;
    }

    private void eliminarUltimoDetalle(String torreSeleccionada) {
        int index = listaTorres.indexOf(torreSeleccionada);
        if (index == -1) return;
        LinearLayout torreLayout = torresLayouts.get(index);

        if (torreLayout.getChildCount() > 1) {
            View ultimaFila = torreLayout.getChildAt(torreLayout.getChildCount() - 1);
            torreLayout.removeViewAt(torreLayout.getChildCount() - 1);
            torreLayout.removeView(ultimaFila);
        }
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
                if (verificarTorresConChild()) {
                    List<InspeccionSupervisor_3> filas = new ArrayList<>();
                    for (int i = 0; i < listaTorres.size(); i++) {
                        String torreSeleccionada = listaTorres.get(i);
                        LinearLayout torreLayout = torresLayouts.get(i);

                        for (int j = 0; j < torreLayout.getChildCount(); j++) {
                            View contenedorPrincipal = torreLayout.getChildAt(j);

                            if (contenedorPrincipal instanceof LinearLayout) {
                                LinearLayout layout = (LinearLayout) contenedorPrincipal;

                                Spinner spinnerDe = (Spinner) ((LinearLayout) layout.getChildAt(0)).getChildAt(0);
                                Spinner spinnerA = (Spinner) ((LinearLayout) layout.getChildAt(0)).getChildAt(1);
                                EditText descripcion = (EditText) layout.getChildAt(1);

                                String de = spinnerDe.getSelectedItem().toString();
                                String a = spinnerA.getSelectedItem().toString();
                                String desc = descripcion.getText().toString();

                                filas.add(new InspeccionSupervisor_3(String.valueOf(i + 1), de, a, desc));
                            }
                        }
                    }

                    Gson gson = new Gson();
                    String detallesJson = gson.toJson(filas);

                    daoExtras.actualizarRegistroSupervisor3(inspeccion.getNumInspeccion(), detallesJson);

                    Intent intent = new Intent(RegistroResumenporNiveles.this, EstimacionAvanceObra.class);
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

    public boolean verificarTorresConChild() {
        boolean alMenosUnaTorreValida = false;

        for (LinearLayout torreLayout : torresLayouts) {
            int childCount = torreLayout.getChildCount();

            if (childCount > 1) {
                for (int i = 1; i < childCount; i++) {
                    LinearLayout contenedorPrincipal = (LinearLayout) torreLayout.getChildAt(i);

                    LinearLayout deALayout = (LinearLayout) contenedorPrincipal.getChildAt(0);
                    Spinner spinnerDe = (Spinner) deALayout.getChildAt(0);
                    Spinner spinnerA = (Spinner) deALayout.getChildAt(1);
                    EditText descripcion = (EditText) contenedorPrincipal.getChildAt(1);

                    String valorDe = spinnerDe.getSelectedItem().toString();
                    String valorA = spinnerA.getSelectedItem().toString();
                    String textoDescripcion = descripcion.getText().toString().trim();

                    if (!valorDe.equals("De") && !valorA.equals("A") && !textoDescripcion.isEmpty()) {
                        alMenosUnaTorreValida = true;
                        break;
                    }
                }
            }

            if (alMenosUnaTorreValida) {
                break;
            }
        }

        if (!alMenosUnaTorreValida) {
            Toast.makeText(this, "Al menos una torre debe tener un registro válido.",
                    Toast.LENGTH_SHORT).show();
        }

        return alMenosUnaTorreValida;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroResumenporNiveles.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroResumenporNiveles.this);
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
