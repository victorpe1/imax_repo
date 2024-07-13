package com.sales.storeapp.ui.pedido;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.data.dao.DAOProducto;
import com.sales.storeapp.models.ListaPrecioModel;
import com.sales.storeapp.models.OrderDetail;
import com.sales.storeapp.models.XMSProductModel;
import com.sales.storeapp.ui.adapters.AutoCompleteProductoAdapter;
import com.sales.storeapp.utils.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AgregarProductoActivity extends AppCompatActivity {
    final String TAG = getClass().getName();
    public static final String EXTRA_PRODUCTO = "EXTRA_PRODUCTO";
    public static final String EXTRA_PRODUCTO_AGREGADO = "EXTRA_PRODUCTO_AGREGADO";

    private int ACCION_PRODUCTO;

    private TextView tv_idProducto;
    private TextInputEditText  edt_precio, edt_cantidad, edt_total, edt_und, edt_marca, edt_peso;
    private DAOProducto daoProducto;
    private DAOPedido daoPedido;

    private App app;
    private List<ListaPrecioModel> listaPrecios = new ArrayList<>();

    private int ordeItem;
    String idCliente = "";
    String numeroPedido = "";
    double precioForzado = 0.0;
    boolean forzarPrecio;

    private XMSProductModel productModel;

    /**
     * Variables de Preferencias
     **/
    boolean settings_productoSinPrecio;
    boolean bloquearPrecio = false;

    private AutoCompleteTextView autocomplete_busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.agregar_producto), true, this, R.drawable.ic_action_close);

        app = (App) getApplicationContext();
        daoProducto = new DAOProducto(getApplicationContext());
        daoPedido = new DAOPedido(getApplicationContext());

        tv_idProducto           = findViewById(R.id.tv_idProducto);
        autocomplete_busqueda   = findViewById(R.id.autocomplete_producto_descripcion);

        edt_precio              = findViewById(R.id.edt_precio);
        edt_cantidad            = findViewById(R.id.edt_cantidad);
        edt_total               = findViewById(R.id.edt_total);
        edt_und                 = findViewById(R.id.edt_und);
        edt_marca               = findViewById(R.id.edt_marca);
        edt_peso              = findViewById(R.id.edt_peso);
        edt_cantidad.requestFocus();

        //Se obtiene el parametro del tipo para saber si la accion a realizar es para agregar o modificar un producto
        Bundle data = getIntent().getExtras();
        ACCION_PRODUCTO = data.getInt("accion", PedidoActivity.ACCION_AGREGAR_PRODUCTO);
        numeroPedido = data.getString("numeroPedido");
        idCliente = data.getString("idCliente");
        ordeItem = data.getInt("ordeItem");

         //CARGA AUTOMATICA
          autocomplete_busqueda.setOnItemClickListener((parent, view, position, id) -> {
              XMSProductModel productoModel = (XMSProductModel) parent.getItemAtPosition(position);
              setProductoModel(productoModel);
              double precio = daoProducto.getPrecioProducto(String.valueOf(productoModel.getIdProducto()));
              edt_precio.setText(String.valueOf(precio));
          });

          autocomplete_busqueda.setOnLongClickListener(v -> {
              autocomplete_busqueda.setText("");
              return true;
          });

        if(ACCION_PRODUCTO == PedidoActivity.ACCION_AGREGAR_PRODUCTO){
            new async_cargarProducto().execute();

            Intent data2 = getIntent();
            productModel = (XMSProductModel) data2.getSerializableExtra(EXTRA_PRODUCTO);
            edt_cantidad.setText("1");
            edt_peso.setText("1");
            //edt_und.setText(productModel.getUnidad());
            cargarPreciosUnidades();

        }else {
            //Si es para modificar, lo primero que se debe hacer es cargar los datos del producto a la vista, para que se pueda editar
            productModel = (XMSProductModel) data.getSerializable(EXTRA_PRODUCTO);

            autocomplete_busqueda.setEnabled(false);
            autocomplete_busqueda.setFocusableInTouchMode(false);
            autocomplete_busqueda.setInputType(InputType.TYPE_CLASS_TEXT);

            //productModel.setFactorConversion(daoProducto.getFactorConversion(productModel.getIdProducto()));

            tv_idProducto.setText(productModel.getCodigo());
            autocomplete_busqueda.setText(productModel.getNombre());

            DAOPedido daoPedido = new DAOPedido(getApplicationContext());
            //PedidoDetalleModel productoPedido = daoPedido.getProductoPedido(numeroPedido, productModel.getIdProducto());
            OrderDetail productoPedido = new OrderDetail();

            if (productoPedido != null) {
                cargarPreciosUnidades();
                //cargar precios

                Log.e(TAG, "producto.getPrecioNeto():" + productoPedido.getPrecioUnit());
                forzarPrecio = true;
                precioForzado = productoPedido.getPrecioUnitTipoCambio();
                edt_precio.setText(String.valueOf(productoPedido.getMonto()));
                edt_cantidad.setText(String.valueOf(productoPedido.getCantidad()));
                edt_total.setText(String.valueOf(productoPedido.getPrecioUnit()));
                edt_cantidad.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(), "No se obtuvo el producto", Toast.LENGTH_SHORT).show();
            }
        }
        setListeners();
    }

    //region watchers
    TextWatcher totalWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            calculateQuantityByTotal(editable.toString());
        }
    };

    TextWatcher priceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            calculateTotalByPrice(editable.toString());
        }
    };

    TextWatcher quantityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            calculateTotalByQuantity(editable.toString());
        }
    };
    //endregion

    private void setProductoModel(XMSProductModel productoModel){
        productModel = productoModel;

        autocomplete_busqueda.setText(productoModel.getNombre());
        // edt_dni_ev.setText(clienteModel.getRucDni());
        // idProducto = Integer.parseInt(productoModel.getIdProducto());
        cargarPreciosUnidades();

        tv_idProducto.setText(productoModel.getCodigo());
        edt_cantidad.setText("1");
        edt_peso.setText("1");
        edt_und.setText(productModel.getUnidad());
        edt_marca.setText(productModel.getMarca());

        //Una vez se obtenga el codigo del cliente se tiene que mandar ese codigo al activity para poder usarlo
        autocomplete_busqueda.requestFocus();
        Util.cerrarTeclado(getApplicationContext(), autocomplete_busqueda);
    }

    private void setListeners() {
        enableTextListeners();
    }

    private void disableTextListeners() {
        edt_total.removeTextChangedListener(totalWatcher);
        edt_precio.removeTextChangedListener(priceWatcher);
        edt_peso.removeTextChangedListener(quantityWatcher);
    }

    private void enableTextListeners() {
        edt_total.addTextChangedListener(totalWatcher);
        edt_precio.addTextChangedListener(priceWatcher);
        edt_peso.addTextChangedListener(quantityWatcher);
    }

    private void calculateTotalByQuantity(String stringQuantity) {
        String stringTotal = "";
        if (edt_precio.getText().toString().isEmpty())
            return;

        if (!stringQuantity.isEmpty()) {
            double quantity = Double.parseDouble(stringQuantity);
            double price = Double.parseDouble(edt_precio.getText().toString());
            double total = Util.redondearDouble(price * quantity);
            stringTotal = String.valueOf(total);
        }

        disableTextListeners();
        edt_total.setText(stringTotal);
        enableTextListeners();
    }

    private void calculateQuantityByTotal(String stringTotal) {
        String stringQuantity = "";
        if (!edt_precio.getText().toString().isEmpty()) {
            if (!stringTotal.isEmpty()) {
                double total = Double.parseDouble(stringTotal);
                double price = Double.parseDouble(edt_precio.getText().toString());

                if (price > 0) {
                    double quantity = Util.redondearDouble(total / price);
                    stringQuantity = String.valueOf(quantity);
                }
            }
        }

        disableTextListeners();
        edt_peso.setText(stringQuantity);
        enableTextListeners();
    }

    private void calculateTotalByPrice(String stringPrice) {
        String stringTotal = "";
        if (edt_peso.getText().toString().isEmpty())
            return;

        if (!stringPrice.isEmpty()) {
            double price = Double.parseDouble(stringPrice);
            double quantity = Double.parseDouble(edt_peso.getText().toString());
            double total = Util.redondearDouble(price * quantity);
            stringTotal = String.valueOf(total);
        }

        disableTextListeners();
        edt_total.setText(stringTotal);
        enableTextListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_aceptar:
               // autocomplete_unidades.showDropDown();
                if (validarCampos()) {
                    if(ACCION_PRODUCTO == PedidoActivity.ACCION_AGREGAR_PRODUCTO ||
                            ACCION_PRODUCTO == PedidoActivity.ACCION_MODIFICAR_PRODUCTO ) {
                       productModel.setNombre(autocomplete_busqueda.getText().toString());
                       agregarProducto();
                    }
                }
                break;
            case android.R.id.home:
                //Cuando AgregarProductoActivity esté mostrandose el usuario pensará que si cierra retornará al activity de búsqueda
                //Sin embargo pasa lo contrario ya que el flujo es el siguiente PedidoActivity->AgregarProductoActivity->BuscarProductoActivity
                //Por lo tanto si estamos en AgregarProductoActivity y se quere ir a BuscarProductoActivity, este último debe llamarse y matener
                //la actividad esperando por los datos resultantes de la búsqueda (No finalizarla)
                if(ACCION_PRODUCTO == PedidoActivity.ACCION_AGREGAR_PRODUCTO ||
                    ACCION_PRODUCTO == PedidoActivity.ACCION_MODIFICAR_PRODUCTO) {
                    setResult(RESULT_CANCELED);
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarPreciosUnidades() {
        Log.d(TAG, "obteniendo precios");


        //Lista de precios predefinidos por el negocio
        listaPrecios.clear();
        ListaPrecioModel sinListaPrecioModel = new ListaPrecioModel();
        sinListaPrecioModel.setId(0);
        sinListaPrecioModel.setCodigo("0");
        sinListaPrecioModel.setDescripcion(getString(R.string.sin_lista_precio));
        listaPrecios.add(sinListaPrecioModel);
        //listaPrecios.addAll(daoProducto.getListaPrecios(productModel.getIdProducto()));
        ArrayList<String> arrayListaPrecios = new ArrayList<>();
        for (ListaPrecioModel listaPrecioModel : listaPrecios) {
            arrayListaPrecios.add(listaPrecioModel.getDescripcion());
        }
        ArrayAdapter<String> adapterListaPrecio = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayListaPrecios);
    }

    void agregarProducto() {

        if(productModel.getCodigo() == null){
            productModel.setCodigo("");
        }
        if(productModel.getNombre().equals("")){
            productModel.setNombre(autocomplete_busqueda.getText().toString());
        }

        AgregarProductoArgument argument = new AgregarProductoArgument(
                Integer.parseInt(productModel.getIdProducto()),
                productModel.getCodigo(),
                productModel.getNombre(),
                Double.parseDouble(edt_precio.getText().toString()),
                Double.parseDouble(edt_cantidad.getText().toString()),
                Double.parseDouble(edt_peso.getText().toString()),
                Double.parseDouble(edt_total.getText().toString()),
                productModel.getIdMedida()
        );

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_PRODUCTO_AGREGADO, argument);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    boolean validarCampos() {
        Util.cerrarTeclado(this, autocomplete_busqueda);

        if(daoPedido.hasProductInPreviewList(numeroPedido,
                productModel.getIdProducto())){
            Snackbar.make(findViewById(android.R.id.content), "El producto ya está en la lista de pedidos",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (productModel.getPrecioVtaMen() > Double.parseDouble(edt_precio.getText().toString())) {
            Snackbar.make(findViewById(android.R.id.content), "El precio es menor al precio minimo para este producto", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(ACCION_PRODUCTO == PedidoActivity.ACCION_AGREGAR_PRODUCTO){
            if(autocomplete_busqueda.getText().toString().matches("") ){
                autocomplete_busqueda.setError("Ingrese una descripcion al producto");
                autocomplete_busqueda.requestFocus();
                return false;
            }
        }

        if (productModel.getIdProducto().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "El producto no es válido", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (edt_cantidad.getText().toString().matches("") ||
                (Double.parseDouble(edt_cantidad.getText().toString())) == 0) {

            edt_cantidad.setError("Ingrese una cantidad");
            edt_cantidad.requestFocus();
            return false;
        }

        if (edt_peso.getText().toString().matches("") ||
                (Double.parseDouble(edt_peso.getText().toString())) == 0) {

            edt_peso.setError("Ingrese un peso");
            edt_peso.requestFocus();
            return false;
        }

        if (settings_productoSinPrecio) {
            if (TextUtils.isEmpty(edt_precio.getText().toString()) || Double.parseDouble(edt_precio.getText().toString()) == 0) {
                edt_precio.setText("0.0");
                showSinPrecioDialog();
                return false;
            }
        } else {
            if (TextUtils.isEmpty(edt_precio.getText().toString()) || Double.parseDouble(edt_precio.getText().toString()) == 0) {
                edt_precio.setError("No tiene precio");
                edt_precio.requestFocus();
                return false;
            }

            if (TextUtils.isEmpty(edt_total.getText().toString()) || Double.parseDouble(edt_total.getText().toString()) == 0) {
                edt_total.setError("No tiene total");
                edt_total.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void showSinPrecioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarProductoActivity.this);
        builder.setTitle(getString(R.string.atencion));
        builder.setMessage(getString(R.string.sin_precio));
        builder.setIcon(R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.agregar), (view, which) -> {
            agregarProducto();
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    class async_cargarProducto extends AsyncTask<Void, Void, ArrayList<XMSProductModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<XMSProductModel> doInBackground(Void... strings) {
            return daoProducto.getProductosVenta();
        }

        @Override
        protected void onPostExecute(ArrayList<XMSProductModel> listaProductos) {
            super.onPostExecute(listaProductos);
            //pDialog.dismiss();
            AutoCompleteProductoAdapter adapter = new AutoCompleteProductoAdapter(getApplicationContext(), listaProductos);
            autocomplete_busqueda.setThreshold(1);
            autocomplete_busqueda.setAdapter(adapter);

            Util.cerrarTeclado(getApplicationContext(),autocomplete_busqueda);
        }
    }
}
