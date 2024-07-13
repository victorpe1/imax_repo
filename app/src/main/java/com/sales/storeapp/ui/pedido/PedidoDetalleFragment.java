package com.sales.storeapp.ui.pedido;


import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.models.OrderDetail;
import com.sales.storeapp.ui.adapters.RecyclerViewProductoPedidoAdapter;
import com.sales.storeapp.utils.Util;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidoDetalleFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{
    private final String TAG = getClass().getName();

    private TextView tv_montoTotal, tv_subTotal, tv_descuento, tv_cantidadProductos;
    private RecyclerView recycler_productosPedido;

    private FloatingActionButton fab_agregarProducto, id_producto_libre, id_buscar_prod_sku;

    private ArrayList<OrderDetail> listaProductos = new ArrayList<>();
    private RecyclerViewProductoPedidoAdapter adapter;

    private String numeroPedido;
    private DAOPedido daoPedido;
    DecimalFormat formateador;

    private int ACCION_PEDIDO;

    public static double montoTotal = 0;
    private static final double IGV_RATE = 0.18;

    public PedidoDetalleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_detalle, container, false);
        setHasOptionsMenu(false);//Indica que podrá manipular las opciones del actionBar desde este fragment

        daoPedido = new DAOPedido(getActivity());
        formateador = Util.formateador();

        tv_subTotal = view.findViewById(R.id.tv_subTotal);
        tv_descuento = view.findViewById(R.id.tv_descuento);
        tv_montoTotal = view.findViewById(R.id.tv_montoTotal);
        tv_cantidadProductos = view.findViewById(R.id.tv_cantidadProductos);
        recycler_productosPedido = view.findViewById(R.id.recycler_productosPedido);

        recycler_productosPedido.setItemAnimator(new DefaultItemAnimator());

        fab_agregarProducto = view.findViewById(R.id.fab_agregarProducto);

        //Mandamos el boton al activity PedidoActivty desde donde se trabajará
        ((PedidoActivity)getActivity()).setFab_agregarProducto(fab_agregarProducto);

        //Obtenermos el ocnumero del activity PedidoActivity y este a su vez lo obtiene de PedidoCabeceraFragment
        numeroPedido = ((PedidoActivity)getActivity()).getNumeroPedidoFromActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean editarItems = true;
        ACCION_PEDIDO = ((PedidoActivity)getActivity()).getACCION_PEDIDO();
        if (ACCION_PEDIDO  == PedidoActivity.ACCION_NUEVO_PEDIDO){

        }else{
            //Los campos idCliente y numeroPedido ya han sido cargados desde PedidoActivity
            if (ACCION_PEDIDO == PedidoActivity.ACCION_VER_PEDIDO){
                fab_agregarProducto.setEnabled(false);
                editarItems = false;
            }
        }

        adapter = new RecyclerViewProductoPedidoAdapter(listaProductos, getActivity(), editarItems, view1 -> openMenu(view1) );
        recycler_productosPedido.setAdapter(adapter);
        recycler_productosPedido.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mostrarListaProductos();
    }

    public void openMenu( View view){

        PopupMenu popup = new PopupMenu(this.getContext(), view,  Gravity.RIGHT, R.attr.actionOverflowMenuStyle, 0);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_contextual_general);
        popup.show();

    }

    public void mostrarListaProductos(){
        this.listaProductos.clear();

        double total = 0;
        double descuento = 0;
        montoTotal = 0;
        int cantidadProductos = 0;

        this.listaProductos.addAll(getListaProductos());
        cantidadProductos = listaProductos.size();
        adapter.notifyDataSetChanged();

        for (OrderDetail producto : listaProductos){
            total += producto.getMonto();
        }

        montoTotal = Util.redondearDouble(total - descuento);
        daoPedido.actualizarPedidoTotales(montoTotal, numeroPedido);

        double subTotal = montoTotal / (1 + IGV_RATE);

        tv_subTotal.setText(getString(R.string.moneda)+formateador.format(subTotal));
        tv_descuento.setText(getString(R.string.moneda)+formateador.format(descuento));
        tv_montoTotal.setText(getString(R.string.moneda)+formateador.format(montoTotal));
        tv_cantidadProductos.setText(""+cantidadProductos);
    }

    public ArrayList<OrderDetail> getListaProductos() {
        return daoPedido.getListaProductoPedido(numeroPedido);
    }


    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contextual_general_editar: //EDITAR MODIFICAR - CAMBIE VALOR
                ((PedidoActivity)getActivity()).modificarProducto(
                        adapter.getItemSelected().getIdProduct(),
                        adapter.getItemSelected().getProducto(),
                        adapter.getItemSelected().getCodigoProducto(),
                        adapter.getItemSelected().getProducto(),
                        adapter.getItemSelected().getPrecioUnit(),
                        adapter.getItemSelected().getProducto());
                break;
            case R.id.menu_contextual_general_quitar:
                if (!daoPedido.eliminarItemDetallePedido(numeroPedido,adapter.getItemSelected().getCodigoProducto(),adapter.getItemSelected().getCodigoProducto())) {
                    Toast.makeText(getActivity(), "No se pudo quitar el producto", Toast.LENGTH_SHORT).show();
                }
                mostrarListaProductos();
                if (ACCION_PEDIDO == PedidoActivity.ACCION_EDITAR_PEDIDO) {
                    ((PedidoActivity) getActivity()).noPermitirCerrar();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    public double getMontoTotal(){
        return montoTotal;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contextual_general_editar:
                ((PedidoActivity)getActivity()).modificarProducto(
                        adapter.getItemSelected().getIdProduct(),
                        adapter.getItemSelected().getProducto(),
                        adapter.getItemSelected().getCodigoProducto(),
                        adapter.getItemSelected().getProducto(),
                        adapter.getItemSelected().getPrecioUnit(),
                        adapter.getItemSelected().getProducto());
                return true;
            case R.id.menu_contextual_general_quitar:
                if (!daoPedido.eliminarItemDetallePedido(numeroPedido,adapter.getItemSelected().getCodigoProducto(),adapter.getItemSelected().getCodigoProducto())) {
                    Toast.makeText(getActivity(), "No se pudo quitar el producto", Toast.LENGTH_SHORT).show();
                }
                mostrarListaProductos();
                if (ACCION_PEDIDO == PedidoActivity.ACCION_EDITAR_PEDIDO) {
                    ((PedidoActivity) getActivity()).noPermitirCerrar();
                }
            return true;

            default:
                return false;
        }

    }
}
