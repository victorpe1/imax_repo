package com.imax.app.ui.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.request.FotoRequest;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.tasks.EnviarDocumentoFotoTask;
import com.imax.app.data.tasks.EnviarDocumentoSupervisorTask;
import com.imax.app.intents.supervisor.InspeccionSupervisor_1;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.ui.activity.MenuPrincipalActivity;
import com.imax.app.ui.activity.RegistroDespuesInspeccionFirmaActivity;
import com.imax.app.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RegistroFotoEvidenciaActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final String EXTRA_DIRECCION = "direccion";

    private ProgressDialog progressDialog;

    private DAOExtras daoExtras;

    private ProgressBar progress;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<AsignacionModel> lista = new ArrayList<>();

    private Spinner spinnerTorre, spinnerPiso;
    private Button btnIniciarCaptura;
    private ListView listViewSecciones;
    private ArrayAdapter<String> seccionesAdapter;
    private ArrayList<String> seccionesList;

    private int torresN, pisosN;
    private String numAsignacion;

    private InspeccionSupervisor_1 inspeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_evidencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.tomar_foto), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        daoExtras = new DAOExtras(getApplicationContext());

        spinnerTorre = findViewById(R.id.spinnerTorre);
        spinnerPiso = findViewById(R.id.spinnerPiso);
        btnIniciarCaptura = findViewById(R.id.btnIniciarCaptura);
        listViewSecciones = findViewById(R.id.listViewSecciones);

        inspeccion = (InspeccionSupervisor_1) getIntent().getSerializableExtra("InspeccionSupervisor_1");
        torresN = Integer.parseInt(inspeccion.getTorres());
        pisosN = Integer.parseInt(inspeccion.getPisos());
        numAsignacion = inspeccion.getNumInspeccion();

        // Configurar Spinners
        setupSpinners();

        // Configurar ListView
        seccionesList = new ArrayList<>();
        seccionesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, seccionesList);
        listViewSecciones.setAdapter(seccionesAdapter);
        loadSeccionesExistentes(numAsignacion);

        // Configurar botón
        btnIniciarCaptura.setOnClickListener(view -> {
            String torreSeleccionada = spinnerTorre.getSelectedItem().toString();
            String pisoSeleccionado = spinnerPiso.getSelectedItem().toString();

            Intent intent = new Intent(RegistroFotoEvidenciaActivity.this, CaptureActivity.class);
            intent.putExtra("torre", torreSeleccionada);
            intent.putExtra("piso", pisoSeleccionado);
            intent.putExtra("asignacion", numAsignacion);
            startActivity(intent);
        });

        listViewSecciones.setOnItemClickListener((adapterView, view, position, l) -> {
            String seccion = seccionesList.get(position);
            String[] parts = seccion.split(" - Piso ");
            if (parts.length == 2) {
                String torre = parts[0].replace("Torre ", "");
                String piso = parts[1];

                Intent intent = new Intent(RegistroFotoEvidenciaActivity.this, CaptureActivity.class);
                intent.putExtra("torre", "Torre " + torre);
                intent.putExtra("piso", "Piso " + piso);
                intent.putExtra("asignacion", numAsignacion);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(RegistroFotoEvidenciaActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

    }

    private void setupSpinners() {
        ArrayList<String> torres = new ArrayList<>();
        for (int i = 1; i <= torresN; i++) {
            torres.add("Torre " + i);
        }
        ArrayAdapter<String> torreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, torres);
        torreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTorre.setAdapter(torreAdapter);

        ArrayList<String> pisos = new ArrayList<>();
        for (int i = 1; i <= pisosN; i++) {
            pisos.add("Piso " + i);
        }
        ArrayAdapter<String> pisoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pisos);
        pisoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPiso.setAdapter(pisoAdapter);
    }

    private void loadSeccionesExistentes(String asignacion) {
        seccionesList.clear();

        File fotosDir = new File(Environment.getExternalStorageDirectory(), "Fotos");
        if (fotosDir.exists() && fotosDir.isDirectory()) {
            File[] asignaciones = fotosDir.listFiles();
            if (asignaciones != null) {
                for (File asignacionDir : asignaciones) {
                    if (asignacionDir.isDirectory() && asignacionDir.getName().equals(asignacion)) {
                        File[] torres = asignacionDir.listFiles(); // Directorios de torres
                        if (torres != null) {
                            for (File torre : torres) {
                                if (torre.isDirectory()) {
                                    File[] pisos = torre.listFiles(); // Directorios de pisos
                                    if (pisos != null) {
                                        for (File piso : pisos) {
                                            if (piso.isDirectory()) {
                                                // Crear el texto de la sección
                                                String seccion = torre.getName() + " - " + piso.getName();
                                                seccionesList.add(seccion);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        seccionesAdapter.notifyDataSetChanged();
    }

    public static String construirJsonTorresConMIME(String asignacion) throws JSONException {
        JSONArray torresArray = new JSONArray();

        File asignacionDir = new File(Environment.getExternalStorageDirectory(), "Fotos/" + asignacion);

        if (asignacionDir.exists() && asignacionDir.isDirectory()) {
            File[] torres = asignacionDir.listFiles();

            if (torres != null) {
                for (File torre : torres) {
                    if (torre.isDirectory()) {
                        JSONObject torreJson = new JSONObject();
                        torreJson.put("torre", torre.getName());

                        JSONArray pisosArray = new JSONArray();
                        File[] pisos = torre.listFiles();

                        if (pisos != null) {
                            for (File piso : pisos) {
                                if (piso.isDirectory()) {
                                    JSONObject pisoJson = new JSONObject();
                                    pisoJson.put("piso", piso.getName());

                                    JSONArray fotosArray = new JSONArray();
                                    File[] fotos = piso.listFiles();

                                    if (fotos != null) {
                                        for (File foto : fotos) {
                                            if (foto.isFile() && (foto.getName().endsWith(".jpg") || foto.getName().endsWith(".png"))) {
                                                String mimeType = getMimeType(foto.getPath());
                                                String combinedItem = "data:" + mimeType + ";name=" + foto.getName() + ";file," + foto.getAbsolutePath();
                                                fotosArray.put(combinedItem);
                                            }
                                        }
                                    }

                                    pisoJson.put("fotos", fotosArray);
                                    pisosArray.put(pisoJson);
                                }
                            }
                        }

                        torreJson.put("pisos", pisosArray);
                        torresArray.put(torreJson);
                    }
                }
            }
        }

        // Crear un objeto final con "torres"
        JSONObject resultado = new JSONObject();
        resultado.put("torres", torresArray);

        // Convertir a string para guardar en la base de datos o pasar al siguiente Activity
        return resultado.toString();
    }

    private static String getMimeType(String path) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type != null ? type : "application/octet-stream";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        menu.findItem(R.id.menu_pedido_siguiente).setVisible(false);
        menu.findItem(R.id.menu_pedido_guardar).setVisible(true);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSeccionesExistentes(numAsignacion);  // Actualizar la lista cada vez que la actividad principal se reanuda
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
                break;
            case R.id.menu_pedido_guardar:
                SupervisorRequest fotoRequest = new SupervisorRequest();
                fotoRequest.setNumInspeccion(numAsignacion);

                try {
                    String torresJsonString = construirJsonTorresConMIME(numAsignacion);
                    fotoRequest.setFotosAdicional(torresJsonString);

                    daoExtras.actualizarRegistroSupervisorFinal(inspeccion.getNumInspeccion(), torresJsonString);

                    new AlertDialog.Builder(this)
                            .setTitle("Confirmación")
                            .setMessage("¿Estás seguro de que deseas continuar?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                SupervisorRequest fotoRequestSend = daoExtras.getListAsignacionByNumeroSupervisor(numAsignacion);
                                new EnviarDocumentoSupervisorTask(RegistroFotoEvidenciaActivity.this,
                                        fotoRequestSend).execute();
                            })
                            .setNegativeButton("No", null)
                            .show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroFotoEvidenciaActivity.this);
        builder.setTitle(getString(R.string.descartar_cambios));
        builder.setMessage(getString(R.string.se_perdera_cambios));
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void mostrarPopup(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int iconResId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView2 = inflater.inflate(R.layout.popup_gracias, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView2);

        AlertDialog dialog2 = builder.create();
        dialog2.show();

        ImageView imageViewCheck = dialogView2.findViewById(R.id.imageViewCheck);
        TextView tvSubtitulo = dialogView2.findViewById(R.id.tvSubtitulo);
        Button btnAceptar = dialogView2.findViewById(R.id.btnAceptar);

        imageViewCheck.setImageResource(iconResId);
        tvSubtitulo.setText(mensajeRes);

        btnAceptar.setOnClickListener(v -> {
            dialog2.dismiss();
            finish();
            Intent intent = new Intent(RegistroFotoEvidenciaActivity.this, MenuPrincipalActivity.class);
            startActivity(intent);
        });
    }

}
