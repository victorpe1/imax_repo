package com.sales.storeapp.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sales.storeapp.R;
import com.sales.storeapp.models.XMSProductModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteProductoAdapter extends ArrayAdapter<XMSProductModel> {

    private List<XMSProductModel> listaOriginal, listaTemporal, listaFiltrada;

    public AutoCompleteProductoAdapter(@NonNull Context context, @NonNull List<XMSProductModel> productos) {
        super(context, 0, productos);
        this.listaOriginal = productos;
        this.listaTemporal = new ArrayList<>(productos);
        this.listaFiltrada = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return listaOriginal.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        //Creamos un nuevo filtro que contiene la lógica de búsqueda
        //return new ClientesFilter(this, clientes);
        return ProductoFilter;
    }

    //Método importante para que el listener del adaptador devuelva el item desde la lista filtrada y no desde la lista base
    @Nullable
    @Override
    public XMSProductModel getItem(int position) {
        return listaOriginal.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AutoCompleteProductoAdapter.ViewHolder holder;
        //Inflar la vista customizada para la fila
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Se puede usar getContext() directamente, pero utilizo parent.getContext() para tomar el contexto del padre y también su tema
        //De lo contrario el inflater se crearía basandose en su propio tema, puede que sea Dark o Light pero no será el mismo de quien lo invocó

        try{
            if(convertView==null){
                //convertView = inflater.inflate(R.layout.item_busqueda_cliente, null);
                convertView = inflater.inflate(R.layout.item_busqueda_producto, parent, false);
                holder = new AutoCompleteProductoAdapter.ViewHolder();

                holder.tv_codigoProducto = convertView.findViewById(R.id.tv_codigoProducto);
                holder.tv_descripcion = convertView.findViewById(R.id.tv_descripcion);
                convertView.setTag(holder);
            }else {
                holder = (AutoCompleteProductoAdapter.ViewHolder) convertView.getTag();
            }

            //Obtener la data del item de la lista filtrada
            XMSProductModel producto = getItem(position);
            //Seteamos los valores a las vistas
            holder.tv_codigoProducto.setText(producto.getCodigo());
            holder.tv_descripcion.setText(producto.getNombre());
        }catch (Exception e){
            XMSProductModel producto = getItem(position);
            if (producto != null) {
                Log.e("Autocomplete", "ERROR POR ID: " + getItem(position).getIdProducto());
            }else{
                Log.e("Autocomplete", "ERROR POR NULL: " + position);
            }

            e.printStackTrace();
        }
        return convertView;
    }


    //La clase viewHolder ayuda a agilizar la vista, ya que guarda o mantiene los componentes y los va seteando en lugar de crear cada componente para cada fila
    class ViewHolder{
        TextView tv_descripcion;
        TextView tv_codigoProducto;
    }
    private Filter ProductoFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence textoBusqueda) {
            listaFiltrada.clear();
            if (textoBusqueda == null || textoBusqueda.length() == 0){
                listaFiltrada.addAll(listaOriginal);
            }else{
                //El texto a buscar tiene que ser tratado convitiendolo a String y pasándolo a minúsculas y quitando espacios
                String textoTratado = textoBusqueda.toString().toLowerCase().trim();

                for (XMSProductModel productoModel : listaTemporal){ //Por cada cliente de la lista original
                    //Log.v("AutoCompleteCliente","clienteModel:"+clienteModel.getRazonSocial());
                    if (TextUtils.isDigitsOnly(textoTratado)){
                        //Se filtra por descripcion
                        if (productoModel.getNombre().contains(textoTratado)){
                            listaFiltrada.add(productoModel);
                        }
                    }else{
                        //De lo contrario se filtra por la razon social
                        if (productoModel.getNombre().toLowerCase().contains(textoTratado)){
                            listaFiltrada.add(productoModel);
                        }
                    }
                }
            }
            //Por último llenamos la lista filtrada al resultado (FilterResults)
            FilterResults filterResults = new FilterResults();
            filterResults.values = listaFiltrada;
            filterResults.count = listaFiltrada.size();
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.values != null && filterResults.count >0){
                clear();
                addAll((List) filterResults.values);
                notifyDataSetChanged();
            }else {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
