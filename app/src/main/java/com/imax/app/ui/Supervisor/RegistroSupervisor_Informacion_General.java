package com.imax.app.ui.Supervisor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.imax.app.R;
import com.imax.app.utils.Util;

public class RegistroSupervisor_Informacion_General extends AppCompatActivity {
    private Spinner spnAsignarNumero;
    private EditText ed_Fecha, ed_Hora, ed_Proyecto, ed_Solicitud, ed_Respon_Obra;
    private EditText ed_Cargo, ed_Sotano, ed_Piso, ed_Obra, ed_Torres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_general);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.Supervisor), true, this, R.drawable.ic_action_close);

        spnAsignarNumero = findViewById(R.id.spn_asignarNumero);
        ed_Fecha = findViewById(R.id.ed_Fecha);
        ed_Hora = findViewById(R.id.ed_Hora);
        ed_Proyecto = findViewById(R.id.ed_Proyecto);
        ed_Solicitud =  findViewById(R.id.ed_Solicitud);
        ed_Respon_Obra = findViewById(R.id.ed_Respon_Obra);
        ed_Cargo = findViewById(R.id.ed_Cargo);
        ed_Sotano = findViewById(R.id.ed_Sotano);
        ed_Piso = findViewById(R.id.ed_Piso);
        ed_Obra = findViewById(R.id.ed_Obra);
        ed_Torres = findViewById(R.id.ed_Torres);
    }
    //Libre


    //Aqui es donde hace que cambie otra ecena de la app
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
                Intent intent = new Intent(RegistroSupervisor_Informacion_General.this, RegistroListadoDocumentos.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
