package com.imax.app.ui.supervisor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imax.app.R;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.supervisor.InspeccionSupervisor_1;
import com.imax.app.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegistroseguridadTrabajoySeñalizacion extends AppCompatActivity {

    RadioButton[] radioChecks = new RadioButton[10];
    Spinner[] spinnersCalificacion = new Spinner[10];
    LinearLayout[] tableRows = new LinearLayout[10];

    private boolean isChecked = false;
    private DAOExtras daoExtras;

    private TextView textPromedio;
    private InspeccionSupervisor_1 inspeccion;
    private EditText edt_especificar;
    private int promedioFinal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registroseguridadts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.registro_supervisor), true, this, R.drawable.ic_action_back);

        daoExtras = new DAOExtras(getApplicationContext());

        edt_especificar = findViewById(R.id.edt_observaciones);
        textPromedio = findViewById(R.id.textPromedio);

        inspeccion = (InspeccionSupervisor_1) getIntent().getSerializableExtra("InspeccionSupervisor_1");

        for (int i = 0; i < 10; i++) {
            int radioId = getResources().getIdentifier("radioCheck" + (i + 1), "id", getPackageName());
            radioChecks[i] = findViewById(radioId);

            int spinnerId = getResources().getIdentifier("spinnerCalificacion" + (i + 1), "id", getPackageName());
            spinnersCalificacion[i] = findViewById(spinnerId);

            int tableRowId = getResources().getIdentifier("tableRow" + (i + 1), "id", getPackageName());
            tableRows[i] = findViewById(tableRowId);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    Arrays.asList("Seleccionar", "Malo", "Regular", "Bueno"));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnersCalificacion[i].setAdapter(adapter);

            int finalI = i;
            radioChecks[i].setOnClickListener(v -> manejarCheck(finalI));

            spinnersCalificacion[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cambiarColorCalificacion(finalI);
                    actualizarPromedio();  // Actualiza el promedio cada vez que se cambia la calificación
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        cargarDatos(inspeccion.getNumInspeccion());

    }

    private void actualizarPromedio() {
        int sumaPrimerGrupo = 0, contadorPrimerGrupo = 0;
        int sumaSegundoGrupo = 0, contadorSegundoGrupo = 0;

        for (int i = 0; i < 5; i++) {
            if (radioChecks[i].isChecked()) { // Considerar solo si el radioCheck está habilitado
                String seleccion = spinnersCalificacion[i].getSelectedItem().toString();
                int valor = obtenerValorCalificacion(seleccion);
                if (valor != -1) {
                    sumaPrimerGrupo += valor;
                    contadorPrimerGrupo++;
                }
            }
        }

        for (int i = 5; i < 10; i++) {
            if (radioChecks[i].isChecked()) { // Considerar solo si el radioCheck está habilitado
                String seleccion = spinnersCalificacion[i].getSelectedItem().toString();
                int valor = obtenerValorCalificacion(seleccion);
                if (valor != -1) {
                    sumaSegundoGrupo += valor;
                    contadorSegundoGrupo++;
                }
            }
        }

        int sumaTotal = sumaPrimerGrupo + sumaSegundoGrupo;
        int contadorTotal = contadorPrimerGrupo + contadorSegundoGrupo;

        if (contadorTotal == 0) {
            textPromedio.setText("Sin calificaciones");
            return;
        }

        double promedio = (double) sumaTotal / contadorTotal;
        int promedioRedondeado = (int) Math.round(promedio);

        String calificacionFinal = obtenerCalificacion(promedioRedondeado);


        promedioFinal = promedioRedondeado;
        textPromedio.setText(String.format("Promedio:  %d - %s", promedioRedondeado, calificacionFinal));
    }

    private int obtenerValorCalificacion(String calificacion) {
        switch (calificacion) {
            case "Malo": return 1;
            case "Regular": return 2;
            case "Bueno": return 3;
            default: return -1; // Indica que no se seleccionó una calificación válida
        }
    }

    private String obtenerCalificacion(int promedioRedondeado) {
        if (promedioRedondeado < 2) {
            return "Mala";
        } else if (promedioRedondeado < 3) {
            return "Regular";
        } else {
            return "Buena";
        }
    }


    private void cargarDatos(String numAsignacion) {
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numAsignacion);

        String gsonCalificacion = inspeccionRequest.getCalificacionSeguridad();
        String gsonRadioCheck = inspeccionRequest.getRadioCheckSeguridad();
        String observa = inspeccionRequest.getObservacionSeguridad();

        if (inspeccionRequest.getRadioCheckSeguridad() != null) {
            edt_especificar.setText(observa);
            int calificacionFinal  = Integer.parseInt(inspeccionRequest.getPromedioCalidad() == null ? "0" : inspeccionRequest.getPromedioCalidad());
            textPromedio.setText(String.format("Promedio:  %d - %s", calificacionFinal, obtenerCalificacion(calificacionFinal)));

            Gson gson = new Gson();
            Type radioChecksType = new TypeToken<List<Boolean>>() {}.getType();
            List<Boolean> radioChecksList = gson.fromJson(gsonRadioCheck, radioChecksType);

            Type calificacionesType = new TypeToken<List<String>>() {}.getType();
            List<String> calificacionesList = gson.fromJson(gsonCalificacion, calificacionesType);

            for (int i = 0; i < radioChecks.length; i++) {
                boolean checked = radioChecksList.get(i);
                spinnersCalificacion[i].setSelection(getSpinnerIndex(spinnersCalificacion[i], calificacionesList.get(i)));
                spinnersCalificacion[i].setVisibility(checked ? View.VISIBLE : View.INVISIBLE);

                radioChecks[i].setChecked(checked);
                radioChecks[i].setText(checked ? "✔" : "X");
                tableRows[i].setBackgroundColor(checked ? cambiarColorCalificacion(i) : Color.TRANSPARENT);
            }
        }
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                return i;
            }
        }
        return 0; // Retornar 0 si no se encuentra el valor
    }

    private void manejarCheck(int index) {
        isChecked = !isChecked; // Cambia el estado

        radioChecks[index].setChecked(isChecked);
        spinnersCalificacion[index].setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        radioChecks[index].setText(isChecked ? "✔" : "X");
        tableRows[index].setBackgroundColor(isChecked ? cambiarColorCalificacion(index) : Color.TRANSPARENT);
    }

    private int cambiarColorCalificacion(int index) {
        String seleccion = spinnersCalificacion[index].getSelectedItem().toString();
        int color;
        switch (seleccion) {
            case "Malo":
                color = Color.parseColor("#FF9999");
                break;
            case "Regular":
                color = Color.parseColor("#FFFF99");
                break;
            case "Bueno":
                color = Color.parseColor("#99FF99");
                break;
            default:
                color = Color.TRANSPARENT;
        }
        tableRows[index].setBackgroundColor(color);
        return color;
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

    private boolean haySeleccionado() {
        for (int i = 0; i < radioChecks.length; i++) {
            if (radioChecks[i].getText().toString().equals("✔") ||
                    radioChecks[i].getText().toString().equals("X")) {
                return true;
            }
            if(radioChecks[i].isChecked()){
                if (!spinnersCalificacion[i].getSelectedItem().toString().equals("Seleccionar")) {
                    return false;
                }
            }
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_pedido_siguiente:
                if(haySeleccionado()){
                    List<Boolean> radioChecksList = new ArrayList<>();
                    List<String> calificacionesList = new ArrayList<>();

                    for (int i = 0; i < radioChecks.length; i++) {
                        radioChecksList.add(radioChecks[i].isChecked());
                        if(radioChecks[i].isChecked()){
                            calificacionesList.add(spinnersCalificacion[i].getSelectedItem().toString());
                        }else{
                            calificacionesList.add("Seleccionar");
                        }
                    }

                    Gson gson = new Gson();
                    String radioChecksJson = gson.toJson(radioChecksList);
                    String calificacionesJson = gson.toJson(calificacionesList);
                    String promedio = String.valueOf(promedioFinal);
                    String observacion = edt_especificar.getText().toString();

                    daoExtras.actualizarRegistroSupervisor6(inspeccion.getNumInspeccion(),
                            radioChecksJson, calificacionesJson, promedio, observacion);

                    Intent intent = new Intent(RegistroseguridadTrabajoySeñalizacion.this, RegistroFotoEvidenciaActivity.class);
                    intent.putExtra("InspeccionSupervisor_1", inspeccion);
                    startActivity(intent);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}