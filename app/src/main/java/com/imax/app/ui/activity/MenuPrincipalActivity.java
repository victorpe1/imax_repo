package com.imax.app.ui.activity;

import android.content.DialogInterface;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOPedido;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.models.UsuarioModel;
import com.imax.app.ui.Supervisor.RegistroSupervisor_Informacion_General;
import com.imax.app.ui.foto.RegistroFotoActivity;
import com.imax.app.ui.pedido.PedidoActivity;
import com.imax.app.utils.Util;
import com.imax.app.ui.configuraciones.ConfiguracionesFragment;
import com.imax.app.ui.listapedidos.PedidosFragment;

public class MenuPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final String TAG = getClass().getName();
    private final int REQUEST_SINCRONIZAR = 0;
    private int MENU_ITEM_ID_SELECTED = -1;

    private static final int REQUEST_NUEVO_REGISTRO = 1;

    private TextView tv_fecha, tv_titulo, tv_subTitulo, tv_id;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DAOExtras daoExtras;
    private App app;
    private int cantidadPedido = 0;
    private double importePedido = 0;
    private DAOPedido daoPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        app = (App) getApplicationContext();
        daoExtras = new DAOExtras(getApplicationContext());
        daoPedido = new DAOPedido(getApplicationContext());
        //region Controls mapping
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout    = findViewById(R.id.drawer_layout);
        navigationView  = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        tv_titulo       = headerView.findViewById(R.id.tv_titulo);
        // tv_subTitulo    = headerView.findViewById(R.id.tv_subTitulo);
        tv_fecha        = headerView.findViewById(R.id.tv_fecha);
        // tv_id        = headerView.findViewById(R.id.tv_id);
        //endregion
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //tv_titulo.setText(app.getNombreTrabajador());
        //tv_subTitulo.setText("Código del Usuario: "+app.getIdTrabajador());
        //tv_fecha.setText(Util.getFechaExtendida());
        drawerLayout.openDrawer(GravityCompat.START);
        cargarUsuario();
        //refreshMiPedido();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showPuntoVentaDialog();
    }

    private void cargarUsuario() {
        tv_fecha.setText(Util.getFechaExtendida());
        UsuarioModel usuarioModel = daoExtras.getDatosUsuario();
        if (usuarioModel != null){
            tv_titulo.setText(usuarioModel.getNombre());
            //tv_id.setText(getString(R.string.codigo_usuario) + ": " + usuarioModel.getIdUsuario());
            if (usuarioModel.getTipoUsuario().equals(UsuarioModel.TIPO_USUARIO_NEGOCIO)){
                //tv_subTitulo.setText(usuarioModel.getIdBodegaCliente()+" | "+usuarioModel.getRazonSocial());
                //tv_id.setText(getString(R.string.codigo_bodega) + " " + usuarioModel.getIdBodega());
            }else{
                //tv_subTitulo.setText(getString(R.string.cuenta_personal));
                //tv_id.setText(getString(R.string.codigo_cliente) + ": " + usuarioModel.getIdCliente());
            }
        }else{
            Toast.makeText(this, "No se pudo cargar los datos del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id!= R.id.nav_salir){
            MENU_ITEM_ID_SELECTED = id;
        }

        switch (id){
            case R.id.nav_registro:
                nuevoRegistro();
                break;
            case R.id.nav_foto:
                nuevoRegistroFotografico();
                break;
            case R.id.nav_supervisor:
                RegistroSupervisor();
                break;
            case R.id.nav_salir:
                Salir();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void nuevoRegistro() {
        Intent intent = new Intent(this, RegistroInspeccionActivity.class);
        intent.putExtra("accion", RegistroInspeccionActivity.ACCION_NUEVO_REGISTRO);
        startActivityForResult(intent, REQUEST_NUEVO_REGISTRO);

    }

    private void nuevoRegistroFotografico() {
        Intent intent = new Intent(this, RegistroFotoActivity.class);
        intent.putExtra("accion", RegistroInspeccionActivity.ACCION_NUEVO_REGISTRO);
        startActivityForResult(intent, REQUEST_NUEVO_REGISTRO);
    }

    private  void RegistroSupervisor(){
        Intent intent = new Intent(this, RegistroSupervisor_Informacion_General.class);
        intent.putExtra("accion", RegistroInspeccionActivity.ACCION_NUEVO_REGISTRO);
        startActivityForResult(intent, REQUEST_NUEVO_REGISTRO);
    }


    private void Salir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.salir_app));
        builder.setPositiveButton(getString(R.string.salir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                app.logOut(MenuPrincipalActivity.this);
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar),null);
        builder.show();
    }

    public void replaceFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_general_pedido, menu);

        final View menuItemMiPedido = menu.findItem(R.id.menu_pedido).getActionView();
        TextView tv_contador = menuItemMiPedido.findViewById(R.id.tv_contador);
        TextView tv_monto = menuItemMiPedido.findViewById(R.id.tv_monto);

        MenuItemCustomListener menuItemCustomListener = new MenuItemCustomListener(menuItemMiPedido, getString(R.string.mi_pedido)) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalActivity.this, MiPedidoActivity.class);
                startActivity(intent);
            }
        };

        menuItemCustomListener.actualizarTextView(tv_contador,cantidadPedido);
        menuItemCustomListener.actualizarTextViewMoneda(tv_monto,importePedido, getString(R.string.moneda));*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //drawerLayout.closeDrawer(GravityCompat.START);
            finish();
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
