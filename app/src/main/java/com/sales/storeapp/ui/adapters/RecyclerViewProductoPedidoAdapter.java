package com.sales.storeapp.ui.adapters;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sales.storeapp.R;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.models.OrderDetail;
import com.sales.storeapp.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerViewProductoPedidoAdapter extends RecyclerView.Adapter<RecyclerViewProductoPedidoAdapter.ItemViewHolder>
 {

    private ArrayList<OrderDetail> lista;
    private Activity activity;
    private DecimalFormat formateador;
    private int positionItemSelected;
    private boolean editarItems;
    private RequestOptions requestOptions;
    final RecyclerViewProductoPedidoAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick( View v);
    }



    public RecyclerViewProductoPedidoAdapter(ArrayList<OrderDetail> lista, Activity activity,
                                             boolean editarItems,
                                             RecyclerViewProductoPedidoAdapter.OnItemClickListener listener) {
        this.lista = lista;
        this.activity = activity;
        this.editarItems = editarItems;
        this.formateador = Util.formateador();
        this.requestOptions = new RequestOptions().placeholder(R.drawable.icon_paquete);
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_pedido, parent, false);
        return new ItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final OrderDetail producto = lista.get(position);


        if(producto.getCodigoProducto().equalsIgnoreCase("")){
            holder.tv_codigoProducto.setText(producto.getIdProduct());
        }else{
            holder.tv_codigoProducto.setText(producto.getCodigoProducto());
        }
        holder.tv_descripcion.setText(producto.getProducto());
        holder.tv_precio.setText(activity.getString(R.string.moneda) + formateador.format(producto.getPrecioUnit()));
        holder.tv_cantidad.setText(String.valueOf(Math.round(producto.getCantidad())));

        holder.tv_unidadMedida.setText(producto.getMedida());
        holder.tv_subTotal.setText(activity.getString(R.string.moneda) + formateador.format(producto.getMonto()));

        //Los productos que son bonificacion no se pueden cambiar ni quitar
        /*if (editarItems && (!producto.getTipoProducto().equals(TipoProducto.BONIFICACION.getCode()))) {

            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuInflater inflater = activity.getMenuInflater();
                    menu.setHeaderTitle(producto.getProducto());
                    inflater.inflate(R.menu.menu_contextual_general, menu);
                    positionItemSelected = position;
                }
            });

            holder.itemView.setOnClickListener(v -> {
                        positionItemSelected = position;
                        listener.onItemClick(v);
                    }
            );
        }
*/
        Glide.with(activity)
                .setDefaultRequestOptions(requestOptions)
                .load(producto.getImagen())
                .into(holder.img_producto);
    }

    public OrderDetail getItemSelected() {
        return lista.get(positionItemSelected);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linear_icon;
        private ImageView img_producto;
        private TextView tv_codigoProducto;
        private TextView tv_descripcion;
        private TextView tv_unidadMedida;
        private TextView tv_precio;
        private TextView tv_cantidad;
        private TextView tv_subTotal;

        public ItemViewHolder(View itemView) {
            super(itemView);
            linear_icon = itemView.findViewById(R.id.linear_icon);
            img_producto = itemView.findViewById(R.id.img_producto);
            tv_codigoProducto = itemView.findViewById(R.id.tv_codigoProducto);
            tv_descripcion = itemView.findViewById(R.id.tv_descripcion);
            tv_unidadMedida = itemView.findViewById(R.id.tv_unidadMedida);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            tv_cantidad = itemView.findViewById(R.id.tv_cantidad);
            tv_subTotal = itemView.findViewById(R.id.tv_subTotal);
        }
    }
}
