package com.imax.app.ui.detallepedido;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;


import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.dao.DAOPedido;
import com.imax.app.models.OrderDetail;
import com.imax.app.models.PedidoCabeceraModel;
import com.imax.app.utils.Util;
import com.imax.app.ui.adapters.RecyclerViewProductoPedidoAdapter;

import androidx.core.content.ContextCompat;

import android.widget.Toast;

import java.util.ArrayList;

public class DetallePedidoActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    public final String TAG = getClass().getName();

    public static final String EXTRA_ID_CLIENTE = "idCliente";
    public static final String EXTRA_NUMERO_PEDIDO = "numeroPedido";
    public static final String EXTRA_NOMBRE_CLIENTE = "nombreCliente";
    public static final String EXTRA_RUBRO = "rubroEmpresa";


    public static Intent getStartIntent(Context context, String numeroPedido, String idCliente,
                                        String rubroEmpresa) {
        Intent intent = new Intent(context, DetallePedidoActivity.class);
        intent.putExtra(EXTRA_NUMERO_PEDIDO, numeroPedido);
        intent.putExtra(EXTRA_RUBRO, rubroEmpresa);
        intent.putExtra(EXTRA_ID_CLIENTE, idCliente);

        return intent;
    }

    TableRow row_documento;
    TextView tv_razonSocial, tv_numeroPedido, tv_flag, tv_almacen, tv_subTotal,
            tv_estado, tv_importeTotal, tv_direccion, tv_fechaPedido,
            tv_condicion, tv_vendedor, tv_observacion, tv_totalOpGratuito, tv_totalExonerado;

    RecyclerView recycler_productos;
    private ProgressDialog progressDialog;

    RecyclerViewProductoPedidoAdapter adapter;
    ArrayList<OrderDetail> listaProductos = new ArrayList<>();

    DAOPedido daoPedido;
    DAOExtras daoExtras;
    PedidoCabeceraModel pedidoCabeceraModel;
    private App app;

    String numeroPedido = "", idCliente = "", nombreCliente = "", flag = "", rubro_empresa = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.detalle_pedido), true, this);

        app = (App) getApplicationContext();
        daoPedido = new DAOPedido(getApplicationContext());
        daoExtras = new DAOExtras(getApplicationContext());

        tv_flag = findViewById(R.id.tv_flag);

        tv_razonSocial = findViewById(R.id.tv_razonSocial);
        tv_numeroPedido = findViewById(R.id.tv_numeroPedido);
        tv_direccion = findViewById(R.id.tv_direccion);
        tv_estado = findViewById(R.id.tv_estado);
        tv_importeTotal = findViewById(R.id.tv_importeTotal);
        tv_fechaPedido = findViewById(R.id.tv_fechaPedido);
        tv_almacen = findViewById(R.id.tv_almacen);
        tv_vendedor = findViewById(R.id.tv_vendedor);
        tv_condicion = findViewById(R.id.tv_condicion);
        tv_observacion = findViewById(R.id.tv_observacion);
        tv_totalOpGratuito = findViewById(R.id.tv_totalOpGratuito);
        tv_totalExonerado = findViewById(R.id.tv_totalExonerado);
        tv_subTotal = findViewById(R.id.tv_sub_total);

        recycler_productos = findViewById(R.id.recycler_productos);


        progressDialog = new ProgressDialog(DetallePedidoActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            numeroPedido = bundle.getString(EXTRA_NUMERO_PEDIDO);
            idCliente = bundle.getString(EXTRA_ID_CLIENTE);
            nombreCliente = bundle.getString(EXTRA_NOMBRE_CLIENTE);
            rubro_empresa = bundle.getString(EXTRA_RUBRO);

            adapter = new RecyclerViewProductoPedidoAdapter(listaProductos, DetallePedidoActivity.this,
                    false, item -> openMenu(item));
            recycler_productos.setAdapter(adapter);
            recycler_productos.addItemDecoration(new DividerItemDecoration(DetallePedidoActivity.this, DividerItemDecoration.VERTICAL));

            cargarPedido();
        }
    }

    public void openMenu(View v){
        PopupMenu popup = new PopupMenu(this.getApplicationContext(), this.getCurrentFocus(), Gravity.END);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_contextual_general);
        popup.show();
    }


    private void cargarPedido() {
        try {
            pedidoCabeceraModel = daoPedido.getPedidoCabeceraReporte(numeroPedido);

            invalidateOptionsMenu();

            tv_razonSocial.setText(pedidoCabeceraModel.getNombre());
            String numeropedidoSimple = pedidoCabeceraModel.getId_numero();
            tv_numeroPedido.setText(numeropedidoSimple);

            tv_estado.setText(getString(R.string.enviado));
            tv_estado.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_400));

            tv_direccion.setText(pedidoCabeceraModel.getDirecc());
            tv_fechaPedido.setText(pedidoCabeceraModel.getFecha());

            tv_almacen.setText(pedidoCabeceraModel.getNombreAlmacen());
            tv_vendedor.setText(pedidoCabeceraModel.getNombrePersonal());
            tv_observacion.setText(pedidoCabeceraModel.getObservacion());
            tv_almacen.setText(pedidoCabeceraModel.getNombreAlmacen());
            tv_condicion.setText(pedidoCabeceraModel.getCondicion());

            tv_totalExonerado.setText(getString(R.string.moneda) + pedidoCabeceraModel.getTotal_opexonerado());
            tv_totalOpGratuito.setText(getString(R.string.moneda) + pedidoCabeceraModel.getTotal_opgratuito());
            tv_subTotal.setText(getString(R.string.moneda) + pedidoCabeceraModel.getSubtotal());
            tv_importeTotal.setText(getString(R.string.moneda) + pedidoCabeceraModel.getTotal());

            //Una vez se haya cargado los datos del pedido se refresca las opciones del menú, para determinar que opciones deben mostrarse
            invalidateOptionsMenu();

            listaProductos.clear();

            listaProductos.addAll(daoPedido.getListaProductoPedido(numeroPedido));

            adapter.notifyDataSetChanged();
        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_obtener_pedido), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detalle_pedido, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }


}
