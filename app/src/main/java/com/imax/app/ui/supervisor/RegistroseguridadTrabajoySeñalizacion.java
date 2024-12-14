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

        // Cargar datos guardados
        cargarDatos(inspeccion.getNumInspeccion());

    }

    private void actualizarPromedio() {
        int sumaPrimerBloque = 0;
        int sumaSegundoBloque = 0;
        int cantidadPrimerBloque = 0;
        int cantidadSegundoBloque = 0;

        // Iteramos sobre el primer bloque (spinnersCalificacion[0] a spinnersCalificacion[3])
        for (int i = 0; i < 4; i++) {
            String seleccion = spinnersCalificacion[i].getSelectedItem().toString();
            int valor = 0;

            // Asignamos un valor numérico a cada opción
            switch (seleccion) {
                case "Bueno":
                    valor = 3;
                    break;
                case "Regular":
                    valor = 2;
                    break;
                case "Malo":
                    valor = 1;
                    break;
                case "Seleccionar":
                    continue; // Si está en "Seleccionar", no contamos esta calificación
            }

            sumaPrimerBloque += valor;
            cantidadPrimerBloque++;
        }

        // Iteramos sobre el segundo bloque (spinnersCalificacion[4] a spinnersCalificacion[7])
        for (int i = 4; i < 8; i++) {
            String seleccion = spinnersCalificacion[i].getSelectedItem().toString();
            int valor = 0;

            // Asignamos un valor numérico a cada opción
            switch (seleccion) {
                case "Bueno":
                    valor = 3;
                    break;
                case "Regular":
                    valor = 2;
                    break;
                case "Malo":
                    valor = 1;
                    break;
                case "Seleccionar":
                    continue; // Si está en "Seleccionar", no contamos esta calificación
            }

            sumaSegundoBloque += valor;
            cantidadSegundoBloque++;
        }

        // Si ambos bloques no tienen calificaciones válidas, mostramos una cadena vacía
        if (cantidadPrimerBloque == 0 && cantidadSegundoBloque == 0) {
            textPromedio.setText("");
        } else {
            // Calculamos los promedios de ambos bloques
            double promedioPrimerBloque = (cantidadPrimerBloque == 0) ? 0 : (double) sumaPrimerBloque / cantidadPrimerBloque;
            double promedioSegundoBloque = (cantidadSegundoBloque == 0) ? 0 : (double) sumaSegundoBloque / cantidadSegundoBloque;

            // Calculamos el promedio total de ambos bloques
            double promedioTotal = (promedioPrimerBloque + promedioSegundoBloque) / 2;

            // Redondeamos el promedio total
            int promedioRedondeado = (int) Math.round(promedioTotal);

            // Determinamos el texto a mostrar según el promedio
            String promedioTexto = "";
            if (promedioRedondeado >= 3) {
                promedioTexto = "Bueno";
            } else if (promedioRedondeado == 2) {
                promedioTexto = "Regular";
            } else if (promedioRedondeado == 1) {
                promedioTexto = "Malo";
            }

            // Actualizamos el TextView con el texto correspondiente
            textPromedio.setText(promedioTexto);
        }
    }


    private void cargarDatos(String numAsignacion) {
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numAsignacion);

        String gsonCalificacion = inspeccionRequest.getCalificacionSeguridad();
        String gsonRadioCheck = inspeccionRequest.getRadioCheckSeguridad();
        String observa = inspeccionRequest.getObservacionSeguridad();

        if (inspeccionRequest.getRadioCheckSeguridad() != null) {
            edt_especificar.setText(observa);

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
                    String promedio = "";
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