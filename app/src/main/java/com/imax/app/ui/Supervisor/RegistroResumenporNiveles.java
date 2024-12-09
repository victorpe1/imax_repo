package com.imax.app.ui.Supervisor;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.imax.app.R;
import com.imax.app.utils.Util;

import java.util.ArrayList;

public class RegistroResumenporNiveles extends AppCompatActivity {
    private EditText ed_Torres;
    private TableLayout tableLayout;
    private Button Agregar1, Eliminar1;
    private Spinner spn_Torres1;
    private ArrayAdapter<String> torreAdapter;
    private ArrayList<String> listaTorres;
    private ArrayList<LinearLayout> torresLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_por_niveles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.Supervisor), true, this, R.drawable.ic_action_close);

        Agregar1 = findViewById(R.id.Agregar1);
        Eliminar1 = findViewById(R.id.Eliminar1);
        spn_Torres1 = findViewById(R.id.spn_Torres1);
        tableLayout = findViewById(R.id.tableLayout1);

        // Configura las torres dinámicamente
        listaTorres = new ArrayList<>();
        torresLayouts = new ArrayList<>();

        int numeroTorres = getIntent().getIntExtra("NUMERO_TORRES", 0); // Recibe el número de torres desde el Intent
        for (int i = 1; i <= numeroTorres; i++) {
            listaTorres.add("Torre " + i);
            agregarNuevaTorre("Torre " + i); // Crear la torre inicial
        }
        // Configuración del Spinner con las torres
        torreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaTorres);
        torreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Torres1.setAdapter(torreAdapter);
        // Configuración del botón Agregar1
        Agregar1.setOnClickListener(v -> {
            String torreSeleccionada = spn_Torres1.getSelectedItem().toString();
            agregarDetalleTorre(torreSeleccionada);
        });

        Eliminar1.setOnClickListener(v -> {
            String torreSeleccionada = spn_Torres1.getSelectedItem().toString();
            eliminarUltimoDetalle(torreSeleccionada);
        });
    }
    private void agregarNuevaTorre(String nombreTorre) {
        // Crear el contenedor para la torre
        LinearLayout torreLayout = new LinearLayout(this);
        torreLayout.setOrientation(LinearLayout.VERTICAL);
        torreLayout.setPadding(8, 8, 8, 8);
        torreLayout.setBackgroundResource(android.R.drawable.edit_text);

        // Título de la torre
        TextView torre = new TextView(this);
        torre.setText(nombreTorre);
        torre.setTextSize(18);
        torreLayout.addView(torre);

        // Agregar el contenedor de la torre al TableLayout
        tableLayout.addView(torreLayout);
        torresLayouts.add(torreLayout); // Guardar el layout en la lista
    }

    private void agregarDetalleTorre(String torreSeleccionada) {
        // Encuentra el layout de la torre seleccionada
        int index = listaTorres.indexOf(torreSeleccionada);
        if (index == -1) return;

        LinearLayout torreLayout = torresLayouts.get(index);

        // Contenedor horizontal para "De" y "A"
        LinearLayout deALayout = new LinearLayout(this);
        deALayout.setOrientation(LinearLayout.HORIZONTAL);

        // Spinner: De
        Spinner spinnerDe = new Spinner(this);
        ArrayAdapter<String> adapterDe = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "De", "S05", "S04", "S03", "S02", "S01", "SS", "P01", "P02", "P03", "P04", "P06",
                "P07", "P08", "P09", "P10", "P11", "P12", "P13", "P14", "P15", "Az", "Tech"
        });
        adapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDe.setAdapter(adapterDe);
        deALayout.addView(spinnerDe);

        // Spinner: A
        Spinner spinnerA = new Spinner(this);
        ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                "A", "S05", "S04", "S03", "S02", "S01", "SS", "P01", "P02", "P03", "P04", "P06",
                "P07", "P08", "P09", "P10", "P11", "P12", "P13", "P14", "P15", "Az", "Tech"
        });
        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerA.setAdapter(adapterA);
        deALayout.addView(spinnerA);

        torreLayout.addView(deALayout);

        // EditText: Descripción de Avance
        EditText descripcion = new EditText(this);
        descripcion.setHint("Descripción avance");
        descripcion.setMinLines(3);
        descripcion.setPadding(8, 8, 8, 8);
        descripcion.setBackgroundResource(android.R.drawable.edit_text);
        torreLayout.addView(descripcion);

    }
    
    private void eliminarUltimoDetalle(String torreSeleccionada ) {
        int index = listaTorres.indexOf(torreSeleccionada);
        if (index == -1) return;

        LinearLayout torreLayout = torresLayouts.get(index);

        // Protege la base de la torre y elimina la última fila dinámica completa
        if (torreLayout.getChildCount() > 1) {
            View ultimaFila = torreLayout.getChildAt(torreLayout.getChildCount() - 1);

            // Asegurarse de eliminar solo las filas dinámicas completas
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
                Intent intent = new Intent(RegistroResumenporNiveles.this, RegistroResumenporNiveles.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
