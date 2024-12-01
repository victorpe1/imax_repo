package com.imax.app.ui.Supervisor;

import android.content.Intent;
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


import com.imax.app.R;
import com.imax.app.utils.Util;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegistroListadoDocumentos extends AppCompatActivity {
    private CheckBox checkBox1;
    private EditText editTextDescription1;
    private Spinner spinner1;
    private TableRow TableRow1;
    private TableRow tableRow;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listado_documentos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.Supervisor), true, this, R.drawable.ic_action_close);

        tableRow = findViewById(R.id.tableRow);
        Button addColumnButton = findViewById(R.id.addColumnButton);
        Button addColumnButton1 = findViewById(R.id.addColumnButton1);
        tableLayout = findViewById(R.id.tableLayout);


        addColumnButton.setOnClickListener(new View.OnClickListener() {
            int rowCount = 0;
            final int MAX_ROWS = 20;
            @Override
            public void onClick(View v) {
                // Verificar si se ha alcanzado el límite de filas
                if (rowCount >= MAX_ROWS) {
                    // Mostrar un mensaje indicando que solo se pueden agregar 20 filas
                    Toast.makeText(RegistroListadoDocumentos.this, "Solo puedes agregar un máximo de 20 filas", Toast.LENGTH_SHORT).show();
                    return; // No agregar más filas
                }

                TableRow newRow = new TableRow(RegistroListadoDocumentos.this);
                newRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));
                newRow.setBackgroundResource(android.R.drawable.edit_text);

                CheckBox newCheckBox = new CheckBox(RegistroListadoDocumentos.this);
                newCheckBox.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));
                newCheckBox.setId(View.generateViewId()); // Generar ID único
                newRow.addView(newCheckBox);

                newCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (newCheckBox.isChecked()) {
                            newCheckBox.setButtonDrawable(R.drawable.ic_dialog_check); // ícono de check
                            newCheckBox.setButtonTintList(ContextCompat.getColorStateList(RegistroListadoDocumentos.this, R.color.white));
                            newRow.setBackgroundColor(ContextCompat.getColor(RegistroListadoDocumentos.this, R.color.green1)); // Color verde para la fila
                        } else {
                            newCheckBox.setButtonDrawable(R.drawable.ic_dialog_error); // ícono de error
                            newCheckBox.setButtonTintList(ContextCompat.getColorStateList(RegistroListadoDocumentos.this, R.color.white));
                            newRow.setBackgroundColor(ContextCompat.getColor(RegistroListadoDocumentos.this, R.color.red_402)); // Color rojo para la fila
                        }
                    }
                });

                EditText newEditText = new EditText(RegistroListadoDocumentos.this);
                newEditText.setLayoutParams(new TableRow.LayoutParams(
                        (int) getResources().getDimension(R.dimen.edit_text_width), // Ancho en dp
                        TableRow.LayoutParams.WRAP_CONTENT // Alto automático
                ));
                newEditText.setPadding(8, 8, 8, 8); // Padding
                newEditText.setId(View.generateViewId()); // Generar ID único
                newEditText.setBackground(null);
                newEditText.setHint("Describir Aquí");
                newRow.addView(newEditText);

                Spinner newSpinner = new Spinner(RegistroListadoDocumentos.this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        RegistroListadoDocumentos.this,
                        R.array.medio_options,
                        android.R.layout.simple_spinner_item
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newSpinner.setAdapter(adapter);
                newSpinner.setId(View.generateViewId()); // Generar ID único
                newRow.addView(newSpinner);

                // Agregar la nueva fila al TableLayout
                tableLayout.addView(newRow);

                addColumnButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tableLayout.getChildCount() >= 1) {
                            // Eliminar la fila solo si ha sido agregada al TableLayout
                            tableLayout.removeViewAt(tableLayout.getChildCount() - 1);
                        }
                    }
                });
                rowCount++;
            }
        });
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
                Intent intent = new Intent(RegistroListadoDocumentos.this, RegistroResumenporNiveles.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
