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
import com.sales.storeapp.models.ClienteModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteClienteAdapter extends ArrayAdapter<ClienteModel> {
    private List<ClienteModel> listaOriginal, listaTemporal, listaFiltrada;

    public AutoCompleteClienteAdapter(@NonNull Context context, @NonNull List<ClienteModel> clientes) {
        super(context, 0, clientes);
        this.listaOriginal=clientes;
        this.listaTemporal = new ArrayList<>(clientes);
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
        return ClientesFilter;
    }

    //Método importante para que el listener del adaptador devuelva el item desde la lista filtrada y no desde la lista base
    @Nullable
    @Override
    public ClienteModel getItem(int position) {
        return listaOriginal.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        //Inflar la vista customizada para la fila
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Se puede usar getContext() directamente, pero utilizo parent.getContext() para tomar el contexto del padre y también su tema
        //De lo contrario el inflater se crearía basandose en su propio tema, puede que sea Dark o Light pero no será el mismo de quien lo invocó

        try{
            if(convertView==null){
                //convertView = inflater.inflate(R.layout.item_busqueda_cliente, null);
                convertView = inflater.inflate(R.layout.item_busqueda_cliente, parent, false);
                holder = new ViewHolder();

                holder.tv_cliente = convertView.findViewById(R.id.tv_cliente);
                holder.tv_dniruc = convertView.findViewById(R.id.tv_rucDni);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //Obtener la data del item de la lista filtrada
            ClienteModel cliente = getItem(position);
            //Seteamos los valores a las vistas
            holder.tv_cliente.setText(cliente.getRazonSocial());
            holder.tv_dniruc.setText(cliente.getRuc().equals( "null") || cliente.getRuc() == null ? cliente.getDni() : cliente.getRuc());
        }catch (Exception e){
            ClienteModel cliente = getItem(position);
            if (cliente != null) {
                Log.e("Autocomplete", "ERROR POR DIRECCION: " + getItem(position).getIdCliente());
            }else{
                Log.e("Autocomplete", "ERROR POR NULL: " + position);
            }

            e.printStackTrace();
        }
        return convertView;
    }


    //La clase viewHolder ayuda a agilizar la vista, ya que guarda o mantiene los componentes y los va seteando en lugar de crear cada componente para cada fila
    class ViewHolder{
        TextView tv_cliente;
        TextView tv_dniruc;
    }
    private Filter ClientesFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence textoBusqueda) {
            listaFiltrada.clear();
            if (textoBusqueda == null || textoBusqueda.length() == 0){
                listaFiltrada.addAll(listaOriginal);
            }else{
                //El texto a buscar tiene que ser tratado convitiendolo a String y pasándolo a minúsculas y quitando espacios
                String textoTratado = textoBusqueda.toString().toLowerCase().trim();

                for (ClienteModel clienteModel : listaTemporal){ //Por cada cliente de la lista original
                    //Log.v("AutoCompleteCliente","clienteModel:"+clienteModel.getRazonSocial());
                    if (TextUtils.isDigitsOnly(textoTratado)){
                        //Se filtra por ruc o dni
                        if(clienteModel.getRuc() == null){
                            if (clienteModel.getDni().contains(textoTratado)){
                                listaFiltrada.add(clienteModel);
                            }
                        }else{
                            if (clienteModel.getRuc().contains(textoTratado)){
                                listaFiltrada.add(clienteModel);
                            }
                        }
                    }else{
                        //De lo contrario se filtra por la razon social
                        if (clienteModel.getRazonSocial().toLowerCase().contains(textoTratado)){
                            listaFiltrada.add(clienteModel);
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
