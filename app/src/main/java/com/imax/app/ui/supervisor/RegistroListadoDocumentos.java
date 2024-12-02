package com.imax.app.ui.supervisor;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imax.app.R;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.supervisor.InspeccionSupervisor_1;
import com.imax.app.intents.supervisor.InspeccionSupervisor_2;
import com.imax.app.models.CatalogModel;
import com.imax.app.utils.Util;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegistroListadoDocumentos extends AppCompatActivity {

    private TableRow tableRow;
    private TableLayout tableLayout;
    private DAOExtras daoExtras;
    private InspeccionSupervisor_1 inspeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_documentos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Util.actualizarToolBar(getString(R.string.registro_supervisor), true, this, R.drawable.ic_action_close);
        daoExtras = new DAOExtras(getApplicationContext());

        tableRow = findViewById(R.id.tableRow);
        Button addColumnButton = findViewById(R.id.addColumnButton);
        Button addColumnButton1 = findViewById(R.id.addColumnButton1);
        tableLayout = findViewById(R.id.tableLayout);

        inspeccion = (InspeccionSupervisor_1) getIntent().getSerializableExtra("InspeccionSupervisor_1");

        loadSavedData(inspeccion.getNumInspeccion());

        addColumnButton.setOnClickListener(new View.OnClickListener() {
            final int MAX_ROWS = 20;

            @Override
            public void onClick(View v) {
                if (tableLayout.getChildCount() >= MAX_ROWS) {
                    Toast.makeText(RegistroListadoDocumentos.this,
                            "Solo puedes agregar un máximo de 20 filas", Toast.LENGTH_SHORT).show();
                }else{
                    addNewRow(null, null, null);
                }
            }
        });
        addColumnButton1.setOnClickListener(v -> {
            if (tableLayout.getChildCount() >= 1) {
                tableLayout.removeViewAt(tableLayout.getChildCount() - 1);
            }
        });
        
    }

    private void addNewRow(Boolean isChecked, String description, String spinnerValue) {
        TableRow newRow = new TableRow(this);
        newRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        newRow.setBackgroundResource(android.R.drawable.edit_text);

        CheckBox newCheckBox = new CheckBox(this);
        newCheckBox.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        newCheckBox.setId(View.generateViewId());

        if (isChecked != null) {
            newCheckBox.setChecked(isChecked);
            updateRowBackground(newRow, newCheckBox);
        }
        newCheckBox.setOnClickListener(v -> updateRowBackground(newRow, newCheckBox));
        newRow.addView(newCheckBox);

        EditText newEditText = new EditText(this);
        newEditText.setLayoutParams(new TableRow.LayoutParams(
                (int) getResources().getDimension(R.dimen.edit_text_width),
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        newEditText.setPadding(8, 8, 8, 8);
        newEditText.setId(View.generateViewId());
        newEditText.setBackground(null);
        newEditText.setHint("Escribir aquí ...");
        if (description != null) {
            newEditText.setText(description);
        }
        newRow.addView(newEditText);

        Spinner newSpinner = new Spinner(this);
        loadData(spinnerValue, newSpinner);
        newSpinner.setId(View.generateViewId());
        newRow.addView(newSpinner);

        tableLayout.addView(newRow);
    }

    private void updateRowBackground(TableRow row, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setButtonDrawable(R.drawable.ic_dialog_check);
            checkBox.setButtonTintList(ContextCompat
                    .getColorStateList(RegistroListadoDocumentos.this, R.color.white));
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.green_300));
        } else {
            checkBox.setButtonDrawable(R.drawable.ic_dialog_error);
            checkBox.setButtonTintList(ContextCompat
                    .getColorStateList(RegistroListadoDocumentos.this, R.color.white));
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.red_300));
        }
    }

    private boolean validateRows() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);

            Drawable background = row.getBackground();
            if (background instanceof ColorDrawable) {
                int backgroundColor = ((ColorDrawable) background).getColor();
                int redColor = ContextCompat.getColor(this, R.color.red_300);
                int greenColor = ContextCompat.getColor(this, R.color.green_300);

                if (backgroundColor != redColor && backgroundColor != greenColor) {
                    Toast.makeText(this, "Todos los registros deben tener un estado (rojo o verde).",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(this, "Todos los registros deben tener un estado válido (rojo o verde).",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void loadSavedData(String numeroAsignacion) {
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numeroAsignacion);
        String detallesJson = inspeccionRequest.getListadoDocumentos();

        if (detallesJson != null && !detallesJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<InspeccionSupervisor_2>>() {}.getType();
            List<InspeccionSupervisor_2> filas = gson.fromJson(detallesJson, listType);

            for (InspeccionSupervisor_2 fila : filas) {
                addNewRow(fila.isChecked(), fila.getDescription(), fila.getSpinnerValue());
            }
        }
    }

    private int getIndexByCodigo(List<CatalogModel> modalidades, String codigo) {
        for (int i = 0; i < modalidades.size(); i++) {
            if (modalidades.get(i).getCodigo().equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    private void loadData(String codigo, Spinner spn){
        List<CatalogModel> modalidades = daoExtras.obtenerCatalogDesdeDB("certificado_pruebas_concreto");

        ArrayAdapter<CatalogModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modalidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(adapter);

        int position = getIndexByCodigo(modalidades, codigo);
        spn.setSelection(Math.max(position, 0));
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
                if (validateRows()) {

                    List<InspeccionSupervisor_2> filas = new ArrayList<>();
                    for (int i = 0; i < tableLayout.getChildCount(); i++) {
                        TableRow row = (TableRow) tableLayout.getChildAt(i);
                        CheckBox checkBox = (CheckBox) row.getChildAt(0);
                        EditText editText = (EditText) row.getChildAt(1);
                        Spinner spinner = (Spinner) row.getChildAt(2);

                        CatalogModel selectedItem = (CatalogModel) spinner.getSelectedItem();
                        filas.add(new InspeccionSupervisor_2(
                                checkBox.isChecked(),
                                editText.getText().toString(),
                                selectedItem.getCodigo()
                        ));
                    }

                    Gson gson = new Gson();
                    String detallesJson = gson.toJson(filas);

                    daoExtras.actualizarRegistroInspeccion2(inspeccion.getNumInspeccion(), detallesJson);

                    Intent intent = new Intent(RegistroListadoDocumentos.this, RegistroResumenporNiveles.class);
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
