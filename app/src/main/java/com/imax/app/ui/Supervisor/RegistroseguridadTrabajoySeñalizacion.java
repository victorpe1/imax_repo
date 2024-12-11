package com.imax.app.ui.Supervisor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.imax.app.R;
import com.imax.app.utils.Util;

import java.util.Arrays;

public class RegistroseguridadTrabajoySeñalizacion extends AppCompatActivity {

    private RadioButton radioCheck1,radioCheck2,radioCheck3,radioCheck4;
    private RadioButton radioCheck5,radioCheck6,radioCheck7,radioCheck8;
    private RadioButton radioCheck9,radioCheck10;

    private Spinner spinnerCalificacion1,spinnerCalificacion2,spinnerCalificacion3,spinnerCalificacion4;
    private Spinner spinnerCalificacion5,spinnerCalificacion6,spinnerCalificacion7,spinnerCalificacion8;
    private Spinner spinnerCalificacion9, spinnerCalificacion10;

    private boolean isChecked = false;

    private LinearLayout tableRow2,tableRow3,tableRow4,tableRow5;
    private LinearLayout tableRow6,tableRow7,tableRow8,tableRow9;
    private LinearLayout tableRow10,tableRow11;

    private TextView textPromedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registroseguridadts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.Supervisor), true, this, R.drawable.ic_action_close);


        // Enlazar vistas
        radioCheck1 = findViewById(R.id.radioCheck1);
        radioCheck2 = findViewById(R.id.radioCheck2);
        radioCheck3 = findViewById(R.id.radioCheck3);
        radioCheck4 = findViewById(R.id.radioCheck4);

        radioCheck5 = findViewById(R.id.radioCheck5);
        radioCheck6 = findViewById(R.id.radioCheck6);
        radioCheck7 = findViewById(R.id.radioCheck7);
        radioCheck8 = findViewById(R.id.radioCheck8);

        radioCheck9 = findViewById(R.id.radioCheck9);
        radioCheck10 = findViewById(R.id.radioCheck10);


        spinnerCalificacion1 = findViewById(R.id.spinnerCalificacion1);
        spinnerCalificacion2 = findViewById(R.id.spinnerCalificacion2);
        spinnerCalificacion3 = findViewById(R.id.spinnerCalificacion3);
        spinnerCalificacion4 = findViewById(R.id.spinnerCalificacion4);

        spinnerCalificacion5 = findViewById(R.id.spinnerCalificacion5);
        spinnerCalificacion6 = findViewById(R.id.spinnerCalificacion6);
        spinnerCalificacion7 = findViewById(R.id.spinnerCalificacion7);
        spinnerCalificacion8 = findViewById(R.id.spinnerCalificacion8);

        spinnerCalificacion9 = findViewById(R.id.spinnerCalificacion9);
        spinnerCalificacion10 = findViewById(R.id.spinnerCalificacion10);

        tableRow2 = findViewById(R.id.tableRow2);
        tableRow3 = findViewById(R.id.tableRow3);
        tableRow4 = findViewById(R.id.tableRow4);
        tableRow5 = findViewById(R.id.tableRow5);

        tableRow6 = findViewById(R.id.tableRow6);
        tableRow7 = findViewById(R.id.tableRow7);
        tableRow8 = findViewById(R.id.tableRow8);
        tableRow9 = findViewById(R.id.tableRow9);

        tableRow10 = findViewById(R.id.tableRow10);
        tableRow11 = findViewById(R.id.tableRow11);

        textPromedio = findViewById(R.id.textPromedio);

        // Opciones de spinnerCalificacion1
        ArrayAdapter<String> adapterCalificacion1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion1.setAdapter(adapterCalificacion1);

        // Opciones de spinnerCalificacion2
        ArrayAdapter<String> adapterCalificacion2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion2.setAdapter(adapterCalificacion2);

        // Opciones de spinnerCalificacion3
        ArrayAdapter<String> adapterCalificacion3 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion3.setAdapter(adapterCalificacion3);

        // Opciones de spinnerCalificacion4
        ArrayAdapter<String> adapterCalificacion4 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion4.setAdapter(adapterCalificacion4);

        // Opciones de spinnerCalificacion5
        ArrayAdapter<String> adapterCalificacion5 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion5.setAdapter(adapterCalificacion5);

        // Opciones de spinnerCalificacion6
        ArrayAdapter<String> adapterCalificacion6 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion6.setAdapter(adapterCalificacion6);

        // Opciones de spinnerCalificacion7
        ArrayAdapter<String> adapterCalificacion7 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion7.setAdapter(adapterCalificacion7);

        // Opciones de spinnerCalificacion8
        ArrayAdapter<String> adapterCalificacion8 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion8.setAdapter(adapterCalificacion8);

        // Opciones de spinnerCalificacion9
        ArrayAdapter<String> adapterCalificacion9 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion9.setAdapter(adapterCalificacion9);

        // Opciones de spinnerCalificacion10
        ArrayAdapter<String> adapterCalificacion10 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("Seleccionar","Malo", "Regular", "Bueno"));
        adapterCalificacion10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalificacion10.setAdapter(adapterCalificacion10);


        // Agregar el listener para el RadioButton
        radioCheck1.setOnClickListener(v -> manejarCheck1());
        radioCheck2.setOnClickListener(v -> manejarCheck2());
        radioCheck3.setOnClickListener(v -> manejarCheck3());
        radioCheck4.setOnClickListener(v -> manejarCheck4());

        radioCheck5.setOnClickListener(v -> manejarCheck5());
        radioCheck6.setOnClickListener(v -> manejarCheck6());
        radioCheck7.setOnClickListener(v -> manejarCheck7());
        radioCheck8.setOnClickListener(v -> manejarCheck8());

        radioCheck9.setOnClickListener(v -> manejarCheck9());
        radioCheck10.setOnClickListener(v -> manejarCheck10());

        // Listener para manejar el cambio en el Spinner1
        spinnerCalificacion1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion1(spinnerCalificacion1.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion2(spinnerCalificacion2.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion3(spinnerCalificacion3.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion4(spinnerCalificacion4.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion5(spinnerCalificacion5.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion6(spinnerCalificacion6.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion7(spinnerCalificacion7.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion8(spinnerCalificacion8.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion9(spinnerCalificacion9.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerCalificacion10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isChecked) {
                    cambiarColorCalificacion10(spinnerCalificacion10.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }


    private void manejarCheck1() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion1.setVisibility(View.VISIBLE);
            radioCheck1.setText("✔");
            String seleccion = spinnerCalificacion1.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow2.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow2.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow2.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow2.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck1.setText("X"); // Cambiar a X
            tableRow2.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion1.setVisibility(View.GONE);
        }
    }
    private void manejarCheck2() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion2.setVisibility(View.VISIBLE);
            radioCheck2.setText("✔");
            String seleccion = spinnerCalificacion2.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow3.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow3.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow3.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow3.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck2.setText("X"); // Cambiar a X
            tableRow3.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion2.setVisibility(View.GONE);
        }
    }
    private void manejarCheck3() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion3.setVisibility(View.VISIBLE);
            radioCheck3.setText("✔");
            String seleccion = spinnerCalificacion3.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow4.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow4.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow4.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow4.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck3.setText("X"); // Cambiar a X
            tableRow4.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion3.setVisibility(View.GONE);
        }
    }
    private void manejarCheck4() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion4.setVisibility(View.VISIBLE);
            radioCheck4.setText("✔");
            String seleccion = spinnerCalificacion4.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow5.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow5.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow5.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow5.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck4.setText("X"); // Cambiar a X
            tableRow5.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion4.setVisibility(View.GONE);
        }
    }

    private void manejarCheck5() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion5.setVisibility(View.VISIBLE);
            radioCheck5.setText("✔");
            String seleccion = spinnerCalificacion5.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow6.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow6.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow6.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow6.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck5.setText("X"); // Cambiar a X
            tableRow6.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion5.setVisibility(View.GONE);
        }
    }
    private void manejarCheck6() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion6.setVisibility(View.VISIBLE);
            radioCheck6.setText("✔");
            String seleccion = spinnerCalificacion6.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow7.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow7.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow7.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow7.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck6.setText("X"); // Cambiar a X
            tableRow7.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion6.setVisibility(View.GONE);
        }
    }
    private void manejarCheck7() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion7.setVisibility(View.VISIBLE);
            radioCheck7.setText("✔");
            String seleccion = spinnerCalificacion7.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow8.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow8.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow8.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow8.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck7.setText("X"); // Cambiar a X
            tableRow8.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion7.setVisibility(View.GONE);
        }
    }
    private void manejarCheck8() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion8.setVisibility(View.VISIBLE);
            radioCheck8.setText("✔");
            String seleccion = spinnerCalificacion8.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow9.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow9.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow9.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow9.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck8.setText("X"); // Cambiar a X
            tableRow9.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion8.setVisibility(View.GONE);
        }
    }

    private void manejarCheck9() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion9.setVisibility(View.VISIBLE);
            radioCheck9.setText("✔");
            String seleccion = spinnerCalificacion9.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow10.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow10.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow10.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow10.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck9.setText("X"); // Cambiar a X
            tableRow10.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion9.setVisibility(View.GONE);
        }
    }
    private void manejarCheck10() {
        isChecked = !isChecked; // Cambia el estado

        if (isChecked) {
            spinnerCalificacion10.setVisibility(View.VISIBLE);
            radioCheck10.setText("✔");
            String seleccion = spinnerCalificacion10.getSelectedItem().toString();
            if (seleccion.equals("Malo")) {
                tableRow11.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo pálido
            } else if (seleccion.equals("Regular")) {
                tableRow11.setBackgroundColor(Color.parseColor("#FFFACD")); // Amarillo pálido
            } else if (seleccion.equals("Bueno")) {
                tableRow11.setBackgroundColor(Color.parseColor("#DFFFD6")); // Verde pálido
            } else {
                tableRow11.setBackgroundColor(Color.TRANSPARENT); // Sin selección
            }
        } else {
            radioCheck10.setText("X"); // Cambiar a X
            tableRow11.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion10.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinnerCalificacion1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion1.getSelectedItem().toString();
                cambiarColorCalificacion1(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion2.getSelectedItem().toString();
                cambiarColorCalificacion2(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion3.getSelectedItem().toString();
                cambiarColorCalificacion3(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion4.getSelectedItem().toString();
                cambiarColorCalificacion4(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion5.getSelectedItem().toString();
                cambiarColorCalificacion5(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion6.getSelectedItem().toString();
                cambiarColorCalificacion6(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion7.getSelectedItem().toString();
                cambiarColorCalificacion7(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion8.getSelectedItem().toString();
                cambiarColorCalificacion8(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion9.getSelectedItem().toString();
                cambiarColorCalificacion9(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCalificacion10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = spinnerCalificacion10.getSelectedItem().toString();
                cambiarColorCalificacion10(seleccion);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void cambiarColorCalificacion1(String seleccion) {
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
        tableRow2.setBackgroundColor(color);

        if (!isChecked) {
            tableRow2.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion1.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion2(String seleccion) {
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
        tableRow3.setBackgroundColor(color);

        if (!isChecked) {
            tableRow3.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion2.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion3(String seleccion) {
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
        tableRow4.setBackgroundColor(color);

        if (!isChecked) {
            tableRow4.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion3.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion4(String seleccion) {
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
        tableRow5.setBackgroundColor(color);

        if (!isChecked) {
            tableRow5.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion4.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void cambiarColorCalificacion5(String seleccion) {
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
        tableRow6.setBackgroundColor(color);

        if (!isChecked) {
            tableRow6.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion5.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion6(String seleccion) {
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
        tableRow7.setBackgroundColor(color);

        if (!isChecked) {
            tableRow7.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion6.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion7(String seleccion) {
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
        tableRow8.setBackgroundColor(color);

        if (!isChecked) {
            tableRow8.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion7.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion8(String seleccion) {
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
        tableRow9.setBackgroundColor(color);

        if (!isChecked) {
            tableRow9.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion8.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void cambiarColorCalificacion9(String seleccion) {
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
        tableRow10.setBackgroundColor(color);

        if (!isChecked) {
            tableRow10.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion9.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    private void cambiarColorCalificacion10(String seleccion) {
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
        tableRow11.setBackgroundColor(color);

        if (!isChecked) {
            tableRow11.setBackgroundColor(Color.TRANSPARENT);
            spinnerCalificacion10.setBackgroundColor(Color.TRANSPARENT);
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
                Intent intent = new Intent(RegistroseguridadTrabajoySeñalizacion.this, RegistroseguridadTrabajoySeñalizacion.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
