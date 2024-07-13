package com.sales.storeapp.ui.activity;

import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.models.UsuarioModel;
import com.sales.storeapp.ui.pedido.PedidoActivity;
import com.sales.storeapp.utils.Util;
import com.sales.storeapp.ui.configuraciones.ConfiguracionesFragment;
import com.sales.storeapp.ui.listapedidos.PedidosFragment;

import java.util.HashMap;

public class MenuPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final String TAG = getClass().getName();
    private final int REQUEST_SINCRONIZAR = 0;
    private int MENU_ITEM_ID_SELECTED = -1;
    private static final int REQUEST_CONECTAR_DISPOSITIVO = 1;
    private static final int REQUEST_NUEVO_PEDIDO = 1;

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

    public void showPuntoVentaDialog() {
        Log.i(TAG, "getPref_idPuntoVenta:" + app.getPref_idPuntoVenta());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.atencion)
                .setMessage(R.string.message_punto_venta)
                .setCancelable(false)
                .setPositiveButton(R.string.aceptar, (dialog, which) -> {
                    replaceFragment(new ConfiguracionesFragment());
                    navigationView.setCheckedItem(R.id.nav_configuracion);
                    drawerLayout.closeDrawer(GravityCompat.START);
                });
        builder.show();
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

        if (id != R.id.nav_configuracion && id!= R.id.nav_salir){
            MENU_ITEM_ID_SELECTED = id;
        }

        switch (id){
            case R.id.nav_mi_cuenta:
                //replaceFragment(new MiCuentaFragment());
                break;
            case R.id.nav_ventas:
                //replaceFragment(new PedidosFragment());
                nuevoPedido();
                break;
            /*case R.id.nav_blu:
                Intent intent = new Intent(this, ImprimirPedidoActivity.class);
                intent.putExtra("headerToPrint", "Vincular Bluetooth para las impresiones directas");
                intent.putExtra("bodyToPrint", "");
                intent.putExtra("footerToPrint", "");
                intent.putExtra("qrToPrint", "");
                startActivity(intent);
                break;
            case R.id.nav_configuracion:
                Intent configuracion = new Intent(MenuPrincipalActivity.this,ConfiguracionActivity.class);
                configuracion.putExtra("origen", ConfiguracionActivity.ORIGEN_MENU);
                startActivityForResult(configuracion,REQUEST_SINCRONIZAR);
                replaceFragment(new ConfiguracionesFragment());
                break;*/
            case R.id.nav_salir:
                Salir();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void nuevoPedido() {
        if (app.getPref_idPuntoVenta() <= 0) {
            this.showPuntoVentaDialog();
        } else {
            Intent intent = new Intent(this, PedidoActivity.class);
            intent.putExtra("accion", PedidoActivity.ACCION_NUEVO_PEDIDO);
            startActivityForResult(intent, REQUEST_NUEVO_PEDIDO);
        }
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
