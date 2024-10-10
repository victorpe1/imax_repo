package com.imax.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imax.app.R;
import com.imax.app.models.Order;
import com.imax.app.ui.listapedidos.PedidosFragment;
import com.imax.app.utils.quickaction.QuickAction;

import java.util.ArrayList;
import java.util.List;

public class RecyclerXMSPedidoAdapter  extends RecyclerView.Adapter<RecyclerXMSPedidoAdapter.ItemViewHolder> {
    public final String TAG = getClass().getName();
    private PedidosFragment fragment;
    private ArrayList<Order> lista;
    QuickAction quickAction;
    int positionSelectedQuickAction;

    public RecyclerXMSPedidoAdapter(PedidosFragment fragment, ArrayList<Order> lista) {
        this.fragment = fragment;
        this.lista = lista;
    }

    @NonNull
    @Override
    public RecyclerXMSPedidoAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new RecyclerXMSPedidoAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerXMSPedidoAdapter.ItemViewHolder holder, final int position) {
        final Order model = lista.get(position);

        holder.tv_numeroPedido.setText(model.getIdNumero());
        holder.tv_cliente.setText(model.getObservacion()); //nombreCliente
        holder.tv_montoTotal.setText(fragment.getString(R.string.moneda) + model.getTotal());
        holder.tv_condicionPago.setText(model.getEmail()); //nombreCond
        int color = fragment.getActivity().getResources().getColor(R.color.red_400);
        String flagTexto = fragment.getString(R.string.pendiente);

        color = fragment.getActivity().getResources().getColor(R.color.green_400);
        flagTexto = fragment.getString(R.string.enviado);

        holder.tv_flag.setTextColor(color);
        holder.view_flag.setBackgroundColor(color);
        holder.tv_flag.setText(flagTexto);
        holder.tvSerieNumero.setText("");

        holder.itemView.setOnClickListener(v -> {
            if (quickAction != null){
                positionSelectedQuickAction = position;
                quickAction.show(v);
            }
        });
    }
    public void setQuickAction(QuickAction quickAction){
        this.quickAction = quickAction;
    }

    public Order getItemSelectedByQuickAction(){
        return lista.get(positionSelectedQuickAction);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setFilter(List<Order> listaPedidosFiltrada) {
        this.lista = new ArrayList<>();
        this.lista.addAll(listaPedidosFiltrada);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_numeroPedido;
        private TextView tv_cliente;
        private TextView tv_montoTotal;
        private TextView tv_condicionPago;
        private TextView tv_motivoNoVenta;
        private TextView tvSerieNumero;
        private TextView tv_flag;
        private View view_flag;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_numeroPedido = itemView.findViewById(R.id.tv_numeroPedido);
            tvSerieNumero = itemView.findViewById(R.id.tv_serie_numero);
            tv_cliente = itemView.findViewById(R.id.tv_cliente);
            tv_montoTotal = itemView.findViewById(R.id.tv_montoTotal);
            tv_condicionPago = itemView.findViewById(R.id.tv_condicionPago);
            tv_motivoNoVenta = itemView.findViewById(R.id.tv_motivoNoVenta);
            tv_flag = itemView.findViewById(R.id.tv_flag);
            view_flag = itemView.findViewById(R.id.view_flag);
        }
    }

}
