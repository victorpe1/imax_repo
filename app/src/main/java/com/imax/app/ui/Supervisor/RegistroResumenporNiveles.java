package com.imax.app.ui.Supervisor;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class RegistroResumenporNiveles extends AppCompatActivity {
    private EditText ed_Torres;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_por_niveles);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.Supervisor), true, this, R.drawable.ic_action_close);

        // Recibe el número de torres desde el Intent
        int numeroTorres = getIntent().getIntExtra("NUMERO_TORRES", 0);

        // Genera las filas dinámicamente en el TableLayout
        generarCuadros(numeroTorres);
    }

    private void generarCuadros(int numeroTorres) {
        TableLayout tableLayout = findViewById(R.id.tableLayout1);


        for (int i = 0; i < numeroTorres; i++) {

            // Crear la sección principal para la Torre
            LinearLayout torreLayout = new LinearLayout(this);
            torreLayout.setOrientation(LinearLayout.VERTICAL);
            torreLayout.setBackgroundResource(android.R.drawable.edit_text);
            torreLayout.setPadding(8, 8, 8, 8);

            // Celda: Torre
            TextView torre = new TextView(this);
            torre.setText("Torre " + (i + 1));
            torre.setTextSize(18);
            torreLayout.addView(torre);

            // Crear el contenedor horizontal para "De" y "A"
            LinearLayout deALayout = new LinearLayout(this);
            deALayout.setOrientation(LinearLayout.HORIZONTAL);

            // Spinner: De
            Spinner spinnerDe = new Spinner(this);
            ArrayAdapter<String> adapterDe = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                    "De","S05","S04","S03","S02","S01",
                    "SS","P01","P02","P03","P04","P06","P07","P08","P09","P10",
                    "P11","P12","P13","P14","P15","Az","Tech"});
            adapterDe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDe.setAdapter(adapterDe);
            deALayout.addView(spinnerDe);

            // Espacio entre Spinner De y Spinner A
            Space espacio = new Space(this);
            LinearLayout.LayoutParams paramsEspacio = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            espacio.setLayoutParams(paramsEspacio);
            deALayout.addView(espacio);

            // Spinner: A
            Spinner spinnerA = new Spinner(this);
            ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                    "A","S05","S04","S03","S02","S01",
                    "SS","P01","P02","P03","P04","P06","P07","P08","P09","P10",
                    "P11","P12","P13","P14","P15","Az","Tech"});
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


            // Crear el contenedor horizontal para "De" y "A"
            LinearLayout deALayout1 = new LinearLayout(this);
            deALayout1.setOrientation(LinearLayout.HORIZONTAL);

            // Spinner: De
            Spinner spinnerDe1 = new Spinner(this);
            ArrayAdapter<String> adapterDe1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                    "De","S05","S04","S03","S02","S01",
                    "SS","P01","P02","P03","P04","P06","P07","P08","P09","P10",
                    "P11","P12","P13","P14","P15","Az","Tech"});
            adapterDe1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDe1.setAdapter(adapterDe1);
            deALayout1.addView(spinnerDe1);

            // Espacio entre Spinner De y Spinner A
            Space espacio1 = new Space(this);
            LinearLayout.LayoutParams paramsEspacio1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            espacio1.setLayoutParams(paramsEspacio1);
            deALayout1.addView(espacio1);

            // Spinner: A
            Spinner spinnerA1 = new Spinner(this);
            ArrayAdapter<String> adapterA1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{
                    "A","S05","S04","S03","S02","S01",
                    "SS","P01","P02","P03","P04","P06","P07","P08","P09","P10",
                    "P11","P12","P13","P14","P15","Az","Tech"});
            adapterA1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerA1.setAdapter(adapterA1);
            deALayout1.addView(spinnerA1);

            torreLayout.addView(deALayout1);

            // EditText: Descripción de Avance
            EditText descripcion1 = new EditText(this);
            descripcion1.setHint("Descripción avance");
            descripcion1.setMinLines(3);
            descripcion1.setPadding(8, 8, 8, 8);
            descripcion1.setBackgroundResource(android.R.drawable.edit_text);
            torreLayout.addView(descripcion1);

            // Agregar la vista completa de la Torre al TableLayout
            tableLayout.addView(torreLayout);
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
