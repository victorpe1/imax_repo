package com.sales.storeapp.ui.pedido;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOCliente;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.data.dao.DAOProducto;
import com.sales.storeapp.data.tasks.EnviarDocumentoTask;
import com.sales.storeapp.models.ClienteModel;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.models.OrderDetail;
import com.sales.storeapp.models.Personal;
import com.sales.storeapp.models.XMSProductModel;
import com.sales.storeapp.utils.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.sales.storeapp.ui.pedido.AgregarProductoActivity.EXTRA_PRODUCTO;

public class PedidoActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_PEDIDO = 1;
    public static final int ACCION_EDITAR_PEDIDO = 2;
    public static final int ACCION_VER_PEDIDO = 3;
    public static final int ACCION_COPIAR_PEDIDO = 4;

    public static final int ACCION_AGREGAR_PRODUCTO = 1;
    public static final int ACCION_MODIFICAR_PRODUCTO = 2;


    public static final int ACCION_AGREGAR_PRODUCTO_SIN_REGISTRO = 11;

    public static final String EXTRA_ID_CLIENTE = "idCliente";
    public static final String EXTRA_NUMERO_PEDIDO = "numeroPedido";
    public static final String EXTRA_NOMBRE_CLIENTE = "nombreCliente";

    public static final String EXTRA_DIRECCION = "direccion";

    public int ACCION_PEDIDO = ACCION_NUEVO_PEDIDO;

    private final int REQUEST_CODE_AGREGAR_PRODUCTO = 0;
    private final int REQUEST_CODE_MODIFICAR_PRODUCTO = 1;


    private int ordeItem; //agregar el mismo producto en la lista (productos libres)

    private String idCliente = "0";
    private int idVendedor;
    private String razonSocial = "";
    private String RUCDNI = "";
    private String numeroPedido;//solo tiene valor cuando se visualiza o modifica el pedido, para un pedido nuevo que se está realizando, obtener el numero de pedido de "getNumeroPedidoFromFragment()"
    private String nuevo_numeroPedido;
    public String idTipoDocumentoOriginal = "";

    private boolean cabeceraGuardada = false;

    private DAOCliente daoCliente;
    private DAOPedido daoPedido;
    private FloatingActionButton fab_agregarProducto;
    private ProgressDialog progressDialog;
    private EditText edt_numeroDocumento, edt_razonSocial, edt_direccion;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;
    private PedidoCabeceraFragment pedidoCabeceraFragment;
    private PedidoDetalleFragment pedidoDetalleFragment;

    private int paginaActual;
    ArrayList<OrderDetail> listaProductos = new ArrayList<>();
    private DAOProducto daoProducto;
    private DAOExtras daoExtras;
    Order pedidoCabeceraModel;

    private App app;
    private XMSProductModel productModel;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_pedido), true, this, R.drawable.ic_action_close);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ACCION_PEDIDO = bundle.getInt("accion");
            if (ACCION_PEDIDO == ACCION_VER_PEDIDO || ACCION_PEDIDO == ACCION_EDITAR_PEDIDO ||
            ACCION_PEDIDO == ACCION_COPIAR_PEDIDO) {
                idCliente = bundle.getString(EXTRA_ID_CLIENTE);
                numeroPedido = bundle.getString(EXTRA_NUMERO_PEDIDO);
                razonSocial = bundle.getString(EXTRA_NOMBRE_CLIENTE, "");
            }
        }

        app = (App) getApplicationContext();
        daoCliente = new DAOCliente(getApplicationContext());
        daoPedido = new DAOPedido(getApplicationContext());
        daoProducto = new DAOProducto(getApplicationContext());
        daoExtras = new DAOExtras(getApplicationContext());

        progressDialog = new ProgressDialog(PedidoActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        pedidoCabeceraFragment = new PedidoCabeceraFragment();
        pedidoDetalleFragment = new PedidoDetalleFragment();

        /*Una vez cargados los componentes, se debe pasar tanto el codigo del cliente como el numero del pedido
        hacia los fragments que componen toda la vista del pedido, sin embargo los fragments son los que deben obtener
        los campos ya que no sabemos el momento exacto cuando terminan de crearse cada uno*/
        if (ACCION_PEDIDO == ACCION_NUEVO_PEDIDO) {

            //int idUsuario = app.getPref_idUsuario();
            int idUsuario = 10;
            String serieUsuario = app.getPref_serieUsuario();
            String numeroMaximo = daoPedido.getMaximoNumeroPedido(idUsuario);
            String fechaActual = Util.getFechaTelefonoString();
            //Util.getFechaHoraTelefonoString_formatoSql();

            Log.i(TAG, "serieUsuario:" + serieUsuario);
            if (serieUsuario == null || serieUsuario.isEmpty()) {
                showAlertError(getString(R.string.error_sin_serie), getString(R.string.error_sin_serie2));
            } else {
                numeroPedido = Util.calcularSecuencia(numeroMaximo, fechaActual, serieUsuario);
            }
        }
        else if (ACCION_PEDIDO == ACCION_VER_PEDIDO || ACCION_PEDIDO == ACCION_EDITAR_PEDIDO
                || ACCION_PEDIDO == ACCION_COPIAR_PEDIDO) {
            if (ACCION_PEDIDO == ACCION_VER_PEDIDO) {
                Util.actualizarToolBar("", true, this, R.drawable.ic_action_back);
            }
            if ( ACCION_PEDIDO == ACCION_COPIAR_PEDIDO ){
                //setNewNumeroPedido();
                //copyRegistroCabeceraXdetalles();
            }
        }

        //Una vez se obtenga la ACCION del pedido y los datos necesarios. Crear el adapter que retornará un fragment por cada sección del activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Preparar el ViewPager con las secciones del adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                paginaActual = position;
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        paginaActual = mSectionsPagerAdapter.PAGE_PRODUCTOS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        if (paginaActual == mSectionsPagerAdapter.PAGE_PEDIDO) {
            menu.findItem(R.id.menu_pedido_siguiente).setVisible(false);
            menu.findItem(R.id.menu_pedido_guardar).setVisible(true);
        } else {
            menu.findItem(R.id.menu_pedido_siguiente).setVisible(true);
            menu.findItem(R.id.menu_pedido_guardar).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ACCION_PEDIDO == ACCION_VER_PEDIDO) {
            menu.findItem(R.id.menu_pedido_guardar).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_pedido_siguiente:
                mViewPager.arrowScroll(View.FOCUS_RIGHT);
                break;
            case R.id.menu_pedido_guardar:
                //Como la cabecera ya está guardada, se actualizará los cambios

                if(!pedidoCabeceraFragment.validarCamposObligatorios()){
                    break;
                }

                boolean seGuardoCorrectamente = guardarPedido();
                if (seGuardoCorrectamente) {
                    if (daoPedido.verificarPedidoTieneDetalle(numeroPedido)) {
                        new EnviarDocumentoTask(PedidoActivity.this, numeroPedido,
                                ACCION_PEDIDO == ACCION_EDITAR_PEDIDO).execute();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_agregue_productos), Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case android.R.id.home:
                if (ACCION_PEDIDO == ACCION_VER_PEDIDO) {
                    finish();
                }
                else {
                    if (cabeceraGuardada) {
                        //Si la cabecera está guardada (se modificó algún campo o el detalle) se muestra la confirmación

                        //Si se está editando y la cabecera se ha guardado, no hay forma que llegue hasta aqui. Ya que no se mostrará el boton "cerrar".
                        //De todas formas se comprueba que no se esté editando para evitar mostrar la confirmación y se vaya sin guardar (necesariamente debe guardar)
                        if (ACCION_PEDIDO != ACCION_EDITAR_PEDIDO) {
                            DialogoConfirmacion();
                        }
                    } else {
                        /*Si la cabecera no está guardada, ya sea cuando se esté registrando o editando,
                          se verifica si se tiene un cliente seleccionado para confirmar, porque puede que
                          de casualidad se haya presionado "atras" o "cerrar"
                         */
                        if (!idCliente.isEmpty()) {
                            DialogoConfirmacion();
                        } else {
                            finish();
                        }
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (ACCION_PEDIDO == ACCION_VER_PEDIDO) {
            super.onBackPressed();
        } else {
            if (cabeceraGuardada) {
                //Si la cabecera está guardada (se modificó algún campo o el detalle) se muestra la confirmación

                //Si se está editando y la cabecera se ha guardado, la unica forma de llegar aqui es a través del boton "atras". Ya que no se mostrará el boton "cerrar".
                //Se comprueba que no se esté editando para evitar mostrar la confirmación y se vaya sin guardar (necesariamente debe guardar)
                if (ACCION_PEDIDO != ACCION_EDITAR_PEDIDO) {
                    DialogoConfirmacion();
                }
                //Si se está editando la unica forma de salir debe ser guardando el pedido, "atras" no debe funcionar en esa situación.
            } else {
                /*Si la cabecera no está guardada, ya sea cuando se esté registrando o editando,
                  se verifica si se tiene un cliente seleccionado para confirmar, porque puede que
                  de casualidad se haya presionado "atras" o "cerrar"
                 */
                if (!idCliente.isEmpty()) {
                    DialogoConfirmacion();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);
        if (ACCION_PEDIDO == ACCION_NUEVO_PEDIDO) {
            builder.setTitle(getString(R.string.descartar_pedido));
            builder.setMessage(getString(R.string.se_perdera_pedido));
        } else {
            builder.setTitle(getString(R.string.descartar_cambios));
            builder.setMessage(getString(R.string.se_perdera_cambios));
        }
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.setPositiveButton(getString(R.string.descartar), (dialog, which) -> {
            if (ACCION_PEDIDO == ACCION_NUEVO_PEDIDO) {
                daoPedido.eliminarPedido(numeroPedido);
            }
            finish();
        });
        builder.show();

    }

    public String getIdClienteFromActivity() {
        return this.idCliente;
    }

    public String getDNIRUCFromActivity() {
        TextView v_nombreRSocial1 =  (TextView) findViewById(R.id.autocomplete_busqueda);
        razonSocial = v_nombreRSocial1.getText().toString();

        return this.razonSocial;
    }

    public String getRazonSocialFromActivity() {
        return this.razonSocial;
    }

    public String getNumeroPedidoFromActivity() {
        return this.numeroPedido;
    }

    public int getACCION_PEDIDO() {
        return ACCION_PEDIDO;
    }

    public void setFab_agregarProducto(FloatingActionButton fab_agregarProducto) {

        this.fab_agregarProducto = fab_agregarProducto;
        this.fab_agregarProducto.setOnClickListener(v -> agregarProducto());
    }

    public void postConsultarCliente(String razonSocial, String direccion) {
        numeroDocumentoValidado = edt_numeroDocumento.getText().toString();
        edt_razonSocial.setText(razonSocial);
        edt_direccion.setText(direccion);
    }
    public void agregarProducto() {
        //La primera vez en agregar un producto se debe guardar la cabecera,
        // si no esta guardada se valida y se guarda para luego agregar el producto
        if (!cabeceraGuardada) {
            ordeItem = 0;
            guardarPedido();
            Log.d(TAG, "cabecera guardada");
        }

        //Si la cabecera ahora si está guardada
        if (cabeceraGuardada) {
            Log.d(TAG, "cabecera guardada, validando campos");
            if (pedidoCabeceraFragment.validarCampos()) {

                //REQUEST_CODE_PRODUCTO identificará desde donde(Activity) se está retornando cuando se ejecute el método onActivityResult
                Intent intent = new Intent(PedidoActivity.this, AgregarProductoActivity.class);
                //PRODUCTO_AGREGAR(buscar producto) o PRODUCTO_MODIFICAR(cargar un producto para modificar)
                intent.putExtra("accion", ACCION_AGREGAR_PRODUCTO);
                intent.putExtra("ordeItem", ordeItem);
                intent.putExtra("numeroPedido", numeroPedido);
                intent.putExtra("idCliente", idCliente);

                intent.putExtra(EXTRA_PRODUCTO, productModel);
                startActivityForResult(intent, REQUEST_CODE_AGREGAR_PRODUCTO);
            }
        }
    }
    private boolean guardarPedido() {
        //Validar los campos de la cabecera
        String v_nombreRSocial;
        TextView v_nombreRSocial1 =  (TextView) findViewById(R.id.autocomplete_busqueda);
        razonSocial = v_nombreRSocial1.getText().toString();
            if (pedidoCabeceraFragment.validarCampos()) {
                Order pedidoModel = pedidoCabeceraFragment.getPedido();
                //Si la cabecera ya está guardada, se actualiza los datos (puede que se hayan modificado mientras se ingresaban los productos)
                if (cabeceraGuardada) {
                    daoPedido.actualizarXMSPedidoCabecera(pedidoModel);
                } else {
                    //Si no esta guardada, determinar si es un pedido nuevo o se está modificando uno
                    if (ACCION_PEDIDO == ACCION_NUEVO_PEDIDO ) {
                        daoPedido.guardarXMSPedidoCabecera(pedidoModel);
                    } else if (ACCION_PEDIDO == ACCION_EDITAR_PEDIDO || ACCION_PEDIDO == ACCION_COPIAR_PEDIDO ) {
                        daoPedido.actualizarXMSPedidoCabecera(pedidoModel);
                    }
                }
                cabeceraGuardada = true;
                //Si cabeceraGuardada cambia su estado a true, quiere decir que ha sufrido cambios. Por lo tanto validamos si se está modificando el pedido
                //De esa forma quitamos la opción para descartar el pedido mediante el boton cerrar o el botón atrás y obligamos al usuario a guardar los cambios
                //Enviandolo al servidor o guardando localmente
                if (ACCION_PEDIDO == ACCION_EDITAR_PEDIDO) {
                    noPermitirCerrar();
                    Order model = new Order();
                    model.setIdNumero(numeroPedido);
                    //dodel.setEstado(PedidoCabeceraModel.ESTADO_MODIFICADO);
                    //dodel.setFlag(PedidoCabeceraModel.FLAG_PENDIENTE);
                    //daoPedido.actualizarEstadoFlagPedido(model);
                }

            } else {
                mViewPager.setCurrentItem(mSectionsPagerAdapter.PAGE_PEDIDO);
                return false;
        }
        return true;
    }

    public void setCliente(ClienteModel clienteModel) {
        guardarPedido();

        if (clienteModel != null) {
            this.idCliente = clienteModel.getIdCliente();
        } else
            this.idCliente = "";
       daoPedido.actualizarCliente(numeroPedido, clienteModel);
    }

    public void setVendedor(Personal personal) {
        guardarPedido();

        if (personal != null) {
            this.idVendedor = personal.getIdPersonal();
        } else
            this.idVendedor = 0;
        daoPedido.actualizarVendedor(numeroPedido, personal);
    }

    public void modificarProducto(int flgProductoLibre, String idProducto, String codigoProducto, String descripcion,
                                  double precioBruto, String tipoProducto) {
        //La primera vez en agregar un producto se debe guardar la cebecera,
        // si no esta guardada se valida y se guarda para luego agregar el producto
        if (!cabeceraGuardada) {
            guardarPedido();
        }

        if (cabeceraGuardada) {
            if (pedidoCabeceraFragment.validarCampos()) {
                //REQUEST_CODE_PRODUCTO identificará desde donde(Activity) se está retornando cuando se ejecute el método onActivityResult
                Intent intent = new Intent(PedidoActivity.this, AgregarProductoActivity.class);
                XMSProductModel productoModel = new XMSProductModel();
                productoModel.setIdProducto(idProducto);
                //productoModel.setFlgProdLibre(flgProductoLibre);
                productoModel.setCodigo(codigoProducto);
                productoModel.setNombre(descripcion);
                //productoModel.setTipoProducto(tipoProducto);
                //PRODUCTO_AGREGAR(buscar producto) o PRODUCTO_MODIFICAR(cargar un producto para modificar)
                intent.putExtra("accion", ACCION_MODIFICAR_PRODUCTO);
                intent.putExtra("idCliente", idCliente);
                intent.putExtra("numeroPedido", numeroPedido);
                intent.putExtra("precioBruto", precioBruto);

                intent.putExtra(EXTRA_PRODUCTO, productoModel);
                startActivityForResult(intent, REQUEST_CODE_MODIFICAR_PRODUCTO);
            }
        }
    }

    public void noPermitirCerrar() {
        Util.actualizarToolBar("", false, this);
        cabeceraGuardada = true; //Para que el onBack valide y no deje retroceder
    }

    void showAlertError(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_dialog_error);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.aceptar), (dialog, which) -> finish());
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_AGREGAR_PRODUCTO: {
                if (resultCode == RESULT_OK) {
                    AgregarProductoArgument argument = (AgregarProductoArgument) data.getSerializableExtra(
                            AgregarProductoActivity.EXTRA_PRODUCTO_AGREGADO);

                    OrderDetail pedidoDetalleModel = argument.toPedidoDetalleModel(numeroPedido);

                    if(!daoPedido.hasProductInPreviewList(pedidoDetalleModel.getIdNumber(),
                            String.valueOf(pedidoDetalleModel.getIdProduct()))){
                        daoPedido.agregarItemPedidoDetalle(pedidoDetalleModel);
                    }
                    pedidoDetalleFragment.mostrarListaProductos();

                }
                break;
            }
            case REQUEST_CODE_MODIFICAR_PRODUCTO: {
                if (resultCode == RESULT_OK) {
                    AgregarProductoArgument argument = (AgregarProductoArgument) data.getSerializableExtra(AgregarProductoActivity.EXTRA_PRODUCTO_AGREGADO);
                    OrderDetail pedidoDetalleModel = argument.toPedidoDetalleModel(numeroPedido);

                    //daoPedido.modificarItemDetallePedido(pedidoDetalleModel);
                    pedidoDetalleFragment.mostrarListaProductos();
                }
                break;
            }
        }
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PedidoActivity.this);
        builder.setTitle(tituloRes);
        builder.setMessage(mensajeRes);
        builder.setIcon(icon);
        builder.setCancelable(false);

       builder.setPositiveButton(getString(R.string.aceptar), (dialog, which) -> {
            /*Intent intent = new Intent(getApplicationContext(), DetallePedidoActivity.class);
            intent.putExtra("numeroPedido",numeroPedido);
            intent.putExtra("idCliente",idCliente);
            intent.putExtra("nombreCliente",autocomplete_busqueda.getText().toString());
            startActivity(intent); */
            setResult(RESULT_OK);
            finish();
        });
        builder.show();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_PEDIDO = 1;
        final int PAGE_PRODUCTOS = 0;

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PAGE_PEDIDO:
                    return pedidoCabeceraFragment;
                case PAGE_PRODUCTOS:
                    return pedidoDetalleFragment;
                default:
                    return pedidoCabeceraFragment;
            }
            // getItem es llamada para instanciar el fragment de la página dada.
        }

        @Override
        public int getCount() {
            // Mostrar 2 páginas en total.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PAGE_PEDIDO:
                    return "Pedido";
                case PAGE_PRODUCTOS:
                    return "Productos";
            }
            return null;
        }

    }

}
