package com.imax.app.data.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.ListaPrecioModel;
import com.imax.app.models.XMSProductModel;

import java.util.ArrayList;
import java.util.List;

public class DAOProducto {
    public final String TAG = getClass().getName();
    DataBaseHelper dataBaseHelper;
    Context context;

    public DAOProducto(Context context){
        this.context = context;
        dataBaseHelper = DataBaseHelper.getInstance(context);
    }

    public Double getIGVProducto(int idProducto) {
        String rawQuery =
                "SELECT ifnull("+TablesHelper.xms_producto_detalle.natural+", 0.0) "+
                        " FROM "+ TablesHelper.xms_producto_detalle.table+
                        " WHERE "+TablesHelper.xms_producto_detalle.id_producto+" ='"+idProducto+"' AND tipo = 'IGV' ";

        Double igvProducto = 0.0;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            igvProducto = cur.getDouble(0);
            cur.moveToNext();
        }
        cur.close();
        return igvProducto;
    }

    public ArrayList<XMSProductModel> getProductosVenta() {

        String rawQuery =
                "SELECT p."+TablesHelper.xms_product.id_producto+", "+
                        " p."+TablesHelper.xms_product.nombre+", "+
                        " p."+TablesHelper.xms_product.facuni+", "+
                        " p."+TablesHelper.xms_product.id_medida+", "+
                        " p."+TablesHelper.xms_product.id_unidad+", "+
                        " p."+TablesHelper.xms_product.moneda+", "+
                        " p."+TablesHelper.xms_product.kardex+", "+
                        " p."+TablesHelper.xms_product.series+", "+
                        " p."+TablesHelper.xms_product.stockmin+", "+
                        " p."+TablesHelper.xms_product.stockmax+", "+
                        " p."+TablesHelper.xms_product.prevtaigv+", "+
                        " p."+TablesHelper.xms_product.preciovtamen+", "+
                        " p."+TablesHelper.xms_product.costo+", "+
                        " p."+TablesHelper.xms_product.codigo+", "+
                        " lp."+TablesHelper.xms_medidas.nombre+", "+
                        " m."+TablesHelper.xms_marcas.descripcion+", "+
                        " p."+TablesHelper.xms_product.tipo_atributo +
                        " FROM "+TablesHelper.xms_product.table+" p "+
                        " INNER JOIN " + TablesHelper.xms_medidas.table + " lp ON p."+
                        TablesHelper.xms_product.id_medida + " = lp." +
                        TablesHelper.xms_medidas.id_medida +
                        " LEFT JOIN " + TablesHelper.xms_marcas.table + " m ON p."+
                        TablesHelper.xms_product.id_marca + " = m." + TablesHelper.xms_marcas.id;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        ArrayList<XMSProductModel> lista = new ArrayList<>();
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            XMSProductModel productoModel = new XMSProductModel();
            productoModel.setIdProducto(cur.getString(0));
            productoModel.setNombre(cur.getString(1));
            productoModel.setFacUni(cur.getInt(2));
            productoModel.setIdMedida(cur.getInt(3));
            productoModel.setIdUnidad(cur.getInt(4));
            productoModel.setMoneda(cur.getString(5));
            productoModel.setKardex(cur.getInt(6));
            productoModel.setSeries(cur.getInt(7));
            productoModel.setStockMin(cur.getInt(8));
            productoModel.setStockMax(cur.getInt(9));
            productoModel.setPrevtaIgv(cur.getString(10));
            productoModel.setPrecioVtaMen(cur.getDouble(11));
            productoModel.setCosto(cur.getDouble(12));
            productoModel.setCodigo(cur.getString(13));
            productoModel.setUnidad(cur.getString(14).trim());
            productoModel.setMarca(cur.getString(15));
            productoModel.setTipoAtributo(cur.getInt(16));

            lista.add(productoModel);
            cur.moveToNext();
        }
        cur.close();
        return lista;
    }

    public List<ListaPrecioModel> getListaPrecios() {
        String rawQuery = "SELECT * FROM "+ TablesHelper.xms_lista_precio.table;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        cur.moveToFirst();

        List<ListaPrecioModel> list = new ArrayList<>();
        ListaPrecioModel model = null;
        while (!cur.isAfterLast()) {
            model = new ListaPrecioModel();
            model.setId(cur.getInt(0));
            model.setCodigo(cur.getString(1));
            model.setDescripcion(cur.getString(2));
            list.add(model);
            cur.moveToNext();
        }
        cur.close();
        return list;
    }

    public void eliminarListaPrecioProductos() {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.delete(TablesHelper.xms_lista_precio_producto.table, null, null);
    }

    public List<ListaPrecioModel> getListaPrecios(String idProducto) {
        String rawQuery =
                "SELECT DISTINCT " +
                        "lp." + TablesHelper.xms_lista_precio.id +
                        ",lp." + TablesHelper.xms_lista_precio.codigo +
                        ",lp." + TablesHelper.xms_lista_precio.descripcion +
                " FROM "+ TablesHelper.xms_lista_precio_producto.table + " lpp " +
                " INNER JOIN " + TablesHelper.xms_lista_precio.table + " lp ON lpp."+TablesHelper.xms_lista_precio_producto.id_lista + " = lp." + TablesHelper.xms_lista_precio.id +
                " WHERE lpp." + TablesHelper.xms_lista_precio_producto.id_producto + " = '"+ idProducto +"'";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        cur.moveToFirst();

        List<ListaPrecioModel> list = new ArrayList<>();
        ListaPrecioModel model = null;
        while (!cur.isAfterLast()) {
            model = new ListaPrecioModel();
            model.setId(cur.getInt(0));
            model.setCodigo(cur.getString(1));
            model.setDescripcion(cur.getString(2));
            list.add(model);
            cur.moveToNext();
        }
        cur.close();
        return list;
    }

    public double getPrecioProducto(String idProducto) {
        String rawQuery =
                "SELECT ifnull("+TablesHelper.xms_product.preciovtamen+", 0.0) " +
                        "FROM " + TablesHelper.xms_product.table +
                        " WHERE " + TablesHelper.xms_product.id_producto + " = '"+ idProducto +"'";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        cur.moveToFirst();

        double precio = 0.0;
        while (!cur.isAfterLast()) {
            precio = cur.getDouble(0);
            cur.moveToNext();
        }
        cur.close();
        return precio;
    }
}
