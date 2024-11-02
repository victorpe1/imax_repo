package com.imax.app.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.api.request.OrderItemRequest;
import com.imax.app.data.api.request.OrderRequest;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.ClienteModel;
import com.imax.app.models.OrderDetail;
import com.imax.app.models.Order;
import com.imax.app.models.PedidoCabeceraModel;
import com.imax.app.models.Personal;
import com.imax.app.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DAOPedido {
    public final String TAG = getClass().getName();
    DataBaseHelper dataBaseHelper;
    Context context;

    public DAOPedido(Context context){
        this.context = context;
        dataBaseHelper = DataBaseHelper.getInstance(context);
    }

    public String getQRtexto(String numeroPedido){
        String texto_qr = "";

        String rawQuery =
        "SELECT pc."+TablesHelper.xms_pedido_cabecera.cadena_qr +
                " FROM "+TablesHelper.xms_pedido_cabecera.table+" pc " +
                "WHERE "+TablesHelper.xms_pedido_cabecera.numero_pedido+" = '" + numeroPedido + "' ";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            texto_qr = cur.getString(0);
            cur.moveToNext();
        }
        cur.close();

        return texto_qr;
    }

    public void actualizarCliente (String numeroPedido, ClienteModel cliente) {
        String where = TablesHelper.xms_order.id_numero + " = ? ";
        String[] args = { numeroPedido };
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            ContentValues reg = new ContentValues();
            if (cliente != null){
                reg.put(TablesHelper.xms_order.id_cliente, cliente.getIdCliente());
                reg.put(TablesHelper.xms_order.direcc, cliente.getDireccion());
                reg.put(TablesHelper.xms_order.id_distrito, cliente.getIdDistrito());
                reg.put(TablesHelper.xms_order.codubigeo, cliente.getCodUbigeo());
            }else{
                reg.put(TablesHelper.xms_order.id_cliente, "");
                reg.put(TablesHelper.xms_order.direcc, "");
                reg.put(TablesHelper.xms_order.id_distrito, "");
                reg.put(TablesHelper.xms_order.codubigeo, "");
            }
            int rows = db.update(TablesHelper.xms_order.table, reg, where, args);
            Log.i(TAG,"actualizarCliente: pedido actualizado " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void actualizarVendedor(String numeroPedido, Personal personal) {
        String where = TablesHelper.xms_order.id_numero + " = ? ";
        String[] args = { numeroPedido };
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            ContentValues reg = new ContentValues();
            if (personal != null){
                reg.put(TablesHelper.xms_order.id_vendedor, personal.getIdPersonal());
                reg.put(TablesHelper.xms_order.direcc, personal.getDomicilio());
                reg.put(TablesHelper.xms_order.id_distrito, personal.getIdDistrito());
                reg.put(TablesHelper.xms_order.codubigeo, "");
            }else{
                reg.put(TablesHelper.xms_order.id_vendedor, 0);
                reg.put(TablesHelper.xms_order.direcc, "");
                reg.put(TablesHelper.xms_order.id_distrito, "");
                reg.put(TablesHelper.xms_order.codubigeo, "");
            }
            int rows = db.update(TablesHelper.xms_order.table, reg, where, args);
            Log.i(TAG,"actualizarCliente: pedido actualizado " + rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Order getPedidoCabecera(String numeroPedido) {
        String rawQuery =
                "SELECT pc."+TablesHelper.xms_order.id_numero +","+
                        "pc."+TablesHelper.xms_order.id_cliente +","+
                        "pc."+TablesHelper.xms_order.id_usuario +","+
                        "pc."+TablesHelper.xms_order.id_cond +","+
                        "pc."+TablesHelper.xms_order.id_vendedor +","+
                        "pc."+TablesHelper.xms_order.id_cobrador +","+
                        "pc."+TablesHelper.xms_order.id_almacen +","+
                        TablesHelper.xms_order.fecha +","+
                        TablesHelper.xms_order.total +","+
                        TablesHelper.xms_order.estado +
                        " FROM "+TablesHelper.xms_order.table+" pc " +
                        "LEFT JOIN "+TablesHelper.xms_client.table+" c ON pc."+TablesHelper.xms_order.id_cliente+
                        " = c."+TablesHelper.xms_client.id_cliente +" " +
                        "WHERE "+TablesHelper.xms_order.id_numero+" = '" + numeroPedido + "' ";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        Order model = null;
        cur.moveToFirst();
        Log.d(TAG,rawQuery);
        while (!cur.isAfterLast()) {
            model = new Order();
            model.setIdNumero(cur.getString(0));
            model.setIdCliente(cur.getInt(1));
            model.setIdUsuario(cur.getInt(2));
            model.setCodubigeo(cur.getString(3));
            model.setIdVendedor(cur.getInt(4));
            model.setIdCobrador(cur.getInt(5));
            model.setIdAlmacen(cur.getInt(6));
            model.setFecha(cur.getString(7));
            model.setTotal(cur.getDouble(8));
            model.setEstado(cur.getString(9));

            cur.moveToNext();
        }
        cur.close();
        return model;
    }

    public ArrayList<Order>getPedidosCabecera(String id_usuario) {
        ArrayList<Order> orders = new ArrayList<>();
        String rawQuery =
                "SELECT pc."+TablesHelper.xms_order.id_numero +","+
                        "pc."+TablesHelper.xms_order.id_cliente +","+
                        "pc."+TablesHelper.xms_order.id_usuario +","+
                        "pc."+TablesHelper.xms_order.id_cond +","+
                        "pc."+TablesHelper.xms_order.id_vendedor +","+
                        "pc."+TablesHelper.xms_order.id_cobrador +","+
                        "pc."+TablesHelper.xms_order.id_almacen +","+
                        TablesHelper.xms_order.fecha +","+
                        TablesHelper.xms_order.total +","+
                        TablesHelper.xms_order.estado +","+
                        "c."+TablesHelper.xms_client.nombre +","+
                        "co."+TablesHelper.xms_condiciones.nombre +
                        " FROM "+TablesHelper.xms_order.table+" pc " +
                        "LEFT JOIN "+TablesHelper.xms_client.table+" c ON pc."+TablesHelper.xms_order.id_cliente+
                        " = c."+TablesHelper.xms_client.id_cliente +" " +
                        "LEFT JOIN "+TablesHelper.xms_condiciones.table+" co ON pc."+TablesHelper.xms_order.id_cond+
                        " = co."+TablesHelper.xms_condiciones.id_condicion +" " +
                        "WHERE "+TablesHelper.xms_order.id_usuario+" = '" + id_usuario + "' ";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        Order model = null;
        cur.moveToFirst();
        Log.d(TAG,rawQuery);
        while (!cur.isAfterLast()) {
            model = new Order();
            model.setIdNumero(cur.getString(0));
            model.setIdCliente(cur.getInt(1));
            model.setIdUsuario(cur.getInt(2));
            model.setCodubigeo(cur.getString(3));
            model.setIdVendedor(cur.getInt(4));
            model.setIdCobrador(cur.getInt(5));
            model.setIdAlmacen(cur.getInt(6));
            model.setFecha(cur.getString(7));
            model.setTotal(cur.getDouble(8));
            model.setEstado(cur.getString(9));
            model.setObservacion(cur.getString(10)); //nombre cliente reference
            model.setEmail(cur.getString(11)); //nombre condicion reference
            orders.add(model);
            cur.moveToNext();
        }
        cur.close();

        return orders;
    }

    public PedidoCabeceraModel getPedidoCabeceraReporte(String numeroPedido) {
        String rawQuery =
                "SELECT pc." + TablesHelper.xms_order.id_numero + "," +
                        "pc." + TablesHelper.xms_order.id_cliente + "," +
                        "c." + TablesHelper.xms_client.nombre + "," +
                        "pc." + TablesHelper.xms_order.id_usuario + "," +
                        "pc." + TablesHelper.xms_order.fecha + "," +
                        "pc." + TablesHelper.xms_order.direcc + "," +
                        "pc." + TablesHelper.xms_order.id_vendedor + "," +
                        "p." + TablesHelper.xms_personal.nombre + "," +
                        "pc." + TablesHelper.xms_order.id_almacen + "," +
                        "a." + TablesHelper.xms_almacenes.nombre + "," +
                        "IFNULL(" + TablesHelper.xms_order.subtotal + ", 0.00) AS subtotal," +
                        "IFNULL(" + TablesHelper.xms_order.descuento + ", 0.00) AS descuento," +
                        "IFNULL(" + TablesHelper.xms_order.total + ", 0.00) AS total," +
                        "pc." + TablesHelper.xms_order.codubigeo + "," +
                        TablesHelper.xms_order.observacion + "," +
                        "IFNULL(" + TablesHelper.xms_order.total_opgratuito + ", 0.00) AS total_opgratuito," +
                        "IFNULL(" + TablesHelper.xms_order.total_opexonerado + ", 0.00) AS total_opexonerado," +
                        "cc." + TablesHelper.xms_condiciones.nombre +
                        " FROM " + TablesHelper.xms_order.table + " pc " +
                        "LEFT JOIN " + TablesHelper.xms_client.table + " c ON pc." + TablesHelper.xms_order.id_cliente +
                        " = c." + TablesHelper.xms_client.id_cliente + " " +
                        "LEFT JOIN " + TablesHelper.xms_personal.table + " p ON pc." + TablesHelper.xms_order.id_vendedor +
                        " = p." + TablesHelper.xms_personal.id_personal + " " +
                        "LEFT JOIN " + TablesHelper.xms_almacenes.table + " a ON pc." + TablesHelper.xms_order.id_almacen +
                        " = a." + TablesHelper.xms_almacenes.id_almacen + " " +
                        "LEFT JOIN " + TablesHelper.xms_condiciones.table + " cc ON pc." + TablesHelper.xms_order.id_cond +
                        " = cc." + TablesHelper.xms_condiciones.id_condicion + " " +
                        "WHERE " + TablesHelper.xms_order.id_numero + " = '" + numeroPedido + "' ";
        
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        PedidoCabeceraModel model = null;
        cur.moveToFirst();
        Log.d(TAG,rawQuery);
        while (!cur.isAfterLast()) {
            model = new PedidoCabeceraModel();

            model.setId_numero(cur.getString(0));
            model.setId_cliente(cur.getString(1));
            model.setNombre(cur.getString(2));
            model.setId_usuario(cur.getString(3));
            model.setFecha(cur.getString(4));
            model.setDirecc(cur.getString(5));
            model.setId_vendedor(cur.getString(6));
            model.setNombrePersonal(cur.getString(7));
            model.setId_almacen(cur.getString(8));
            model.setNombreAlmacen(cur.getString(9));
            model.setSubtotal(cur.getDouble(10));
            model.setDescuento(cur.getDouble(11));
            model.setTotal(cur.getDouble(12));
            model.setCodubigeo(cur.getString(13));
            model.setObservacion(cur.getString(14));
            model.setTotal_opgratuito(cur.getDouble(15));
            model.setTotal_opexonerado(cur.getDouble(16));
            model.setCondicion(cur.getString(17));
            cur.moveToNext();
        }
        cur.close();
        return model;
    }

    public String getMaximoNumeroPedidoCopiar() {
        String rawQuery;
        rawQuery = "select max("+TablesHelper.xms_pedido_cabecera.numero_pedido+") " +
                "from "+TablesHelper.xms_pedido_cabecera.table;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        String num_oc = "";

        cur.moveToFirst();
        if (cur.moveToFirst()) {
            do {
                num_oc = cur.getString(0);
            } while (cur.moveToNext());

        }
        Log.w(TAG,rawQuery+" :"+num_oc);
        if (num_oc == null || num_oc.trim().length() == 0) {
            num_oc = "";
        }
        cur.close();

        DAOExtras daoConfiguracion = new DAOExtras(context);
        String numeroConfiguracion =  daoConfiguracion.getMaximoPedido();

        if (num_oc.isEmpty()){
            //Si no se obtiene el maximo numero de los pedidos, verificar si se tiene alguno en la tabla configuracion. Ya que si es de una Guia anterior, se debe continuar
            return  numeroConfiguracion;
        }else{
            if (numeroConfiguracion.isEmpty()){
                Log.w(TAG,"numeroConfiguracion empty return :"+num_oc);
                return num_oc;
            }else{//Comparación para obtener el mayor. Siempre la seríe debe ser numérico (no contener letras, sino se tendría que hacer un substring de solo el numero)
                if (Long.parseLong(numeroConfiguracion) > Long.parseLong(num_oc)){
                    Log.w(TAG,"numeroConfiguracion > return :"+numeroConfiguracion);
                    return numeroConfiguracion;
                }else{
                    Log.w(TAG,"numeroConfiguracion < return :"+num_oc);
                    return num_oc;
                }
            }
        }
    }

    public String getMaximoNumeroPedido(int idUsuario) {
        String rawQuery;
        rawQuery = "select max("+TablesHelper.xms_order.id_numero+") from "
                + TablesHelper.xms_order.table + " where "
                + TablesHelper.xms_order.id_usuario + " = "+ idUsuario;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        String num_oc = "";

        cur.moveToFirst();
        if (cur.moveToFirst()) {
            do {
                num_oc = cur.getString(0);
            } while (cur.moveToNext());

        }
        Log.w(TAG,rawQuery+" :"+num_oc);
        if (num_oc == null || num_oc.trim().length() == 0) {
            num_oc = "";
        }
        cur.close();

        DAOExtras daoConfiguracion = new DAOExtras(context);
        String numeroConfiguracion =  daoConfiguracion.getMaximoPedido();

        if (num_oc.isEmpty()){
            //Si no se obtiene el maximo numero de los pedidos, verificar si se tiene alguno en la tabla configuracion. Ya que si es de una Guia anterior, se debe continuar
            return  numeroConfiguracion;
        }else{
            if (numeroConfiguracion.isEmpty()){
                Log.w(TAG,"numeroConfiguracion empty return :"+num_oc);
                return num_oc;
            }else{//Comparación para obtener el mayor. Siempre la seríe debe ser numérico (no contener letras, sino se tendría que hacer un substring de solo el numero)
                if (Long.parseLong(numeroConfiguracion) > Long.parseLong(num_oc)){
                    Log.w(TAG,"numeroConfiguracion > return :"+numeroConfiguracion);
                    return numeroConfiguracion;
                }else{
                    Log.w(TAG,"numeroConfiguracion < return :"+num_oc);
                    return num_oc;
                }
            }
        }
    }

    public String getFechaPedido(String numeroPedido) {
        String fecha = "";
        String rawQuery =
                "SELECT "+TablesHelper.xms_pedido_cabecera.fecha_pedido+" "
                        + "FROM "+TablesHelper.xms_pedido_cabecera.table+" "
                        + "WHERE "+TablesHelper.xms_pedido_cabecera.numero_pedido+" ='" + numeroPedido +"'";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);
        Log.e(TAG,rawQuery);
        if (cursor.moveToFirst()) {
            do {
                fecha = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return fecha;
    }

    public ArrayList<OrderDetail> getListaProductoPedido(String numeroPedido) {
        String rawQuery =
                "SELECT "
                        + "pd.id_producto"
                        + ",p.nombre"
                        + ",pd.preciounit"
                        + ",pd.monto"
                        + ",pd.cantidad"
                        + ",pd.preciounitaltipcam"
                        + ",pd.id_medida"
                        + ",lp."+TablesHelper.xms_medidas.nombre
                        + ",pd.order_item "
                        + ",ifnull(p.codigo, '') "
                        + ",ifnull(pd.cantidad2, 0) "
                        + ",ifnull(p.tipo_atributo, '1') "
                        + ",ifnull(pd.precunitgrat, '0.00') "
                        + ",ifnull(pd.montograt, '0.00') "
                        + "FROM "+TablesHelper.xms_order_detail.table+" pd "
                        + "INNER JOIN "+TablesHelper.xms_product.table+" p ON pd."
                        + TablesHelper.xms_order_detail.id_producto+" = p."+TablesHelper.xms_product.id_producto
                        + " INNER JOIN " + TablesHelper.xms_medidas.table + " lp ON p."+
                        TablesHelper.xms_product.id_medida + " = lp." + TablesHelper.xms_medidas.id_medida
                        +" WHERE id_numero ='" + numeroPedido + "' "
                        + "ORDER BY order_item";

        Log.d(TAG,"getListaProductoPedido: NumeroPedido:"+numeroPedido);
        Log.d(TAG,rawQuery);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(rawQuery, null);
        ArrayList<OrderDetail> lista = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                OrderDetail producto = new OrderDetail();
                producto.setIdProduct(cursor.getInt(0));
                producto.setProducto(cursor.getString(1));
                producto.setPrecioUnit(cursor.getDouble(2));
                producto.setMonto(cursor.getDouble(3));
                producto.setCantidad(cursor.getDouble(4));
                producto.setPrecioUnitTipoCambio(cursor.getDouble(5));
                producto.setIdMedida(cursor.getInt(6));
                producto.setMedida(cursor.getString(7));
                producto.setItem(cursor.getInt(8));
                producto.setImagen("");
                producto.setCodigoProducto(cursor.getString(9));
                producto.setPeso(cursor.getDouble(10));
                producto.setTipotributo(cursor.getInt(11));
                producto.setPrecunitgrat(cursor.getDouble(12));
                producto.setMontograt(cursor.getDouble(13));
                lista.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void actualizarPedidoTotales(double importeTotal, String numeroPedido) {

        String where = TablesHelper.xms_pedido_cabecera.numero_pedido+" = ?";
        String[] args = { numeroPedido };

        try {

            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues valor = new ContentValues();
            valor.put(TablesHelper.xms_pedido_cabecera.importe_total, importeTotal);
            db.update(TablesHelper.xms_pedido_cabecera.table, valor, where, args);
            Log.i(TAG,"actualizarPedidoTotales: Cabecera actualizada");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void  actualizarXMSPedidoCabecera(Order cabecera) {
        String where = TablesHelper.xms_order.id_numero + " = ?";
        String[] args = { cabecera.getIdNumero() };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();
            Nreg.put(TablesHelper.xms_order.id_numero,    cabecera.getIdNumero());
            //Nreg.put(TablesHelper.xms_pedido_cabecera.MontoTotal,     cabecera.getMontoTotal());
            // Este campo se actualiza al modificar el detalle directamente
            Nreg.put(TablesHelper.xms_order.fecha,     cabecera.getFecha());
            Nreg.put(TablesHelper.xms_order.id_cond,    cabecera.getIdCond());
            Nreg.put(TablesHelper.xms_order.id_cliente,       cabecera.getIdCliente());
            Nreg.put(TablesHelper.xms_order.id_usuario,       cabecera.getIdUsuario());
            Nreg.put(TablesHelper.xms_order.id_vendedor,       cabecera.getIdVendedor());
            Nreg.put(TablesHelper.xms_order.direcc,       cabecera.getDirecc());
            Nreg.put(TablesHelper.xms_order.codubigeo,       cabecera.getCodubigeo());
            Nreg.put(TablesHelper.xms_order.estado,           cabecera.getEstado());
            Nreg.put(TablesHelper.xms_order.fechaDeEntrega,     cabecera.getFechaEntrega());
            Nreg.put(TablesHelper.xms_order.observacion,       cabecera.getObservacion());
            Nreg.put(TablesHelper.xms_order.email,   cabecera.getEmail());
            Nreg.put(TablesHelper.xms_order.fechaDeVenc, cabecera.getFechaVencimiento());
            Nreg.put(TablesHelper.xms_order.typeDocument,  cabecera.getTypeDocument());

            db.update(TablesHelper.xms_order.table, Nreg, where, args);

            Log.i(TAG, "actualizarXMSPedidoCabecera: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "actualizarXMSPedidoCabecera: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void guardarXMSPedidoCabecera(Order cabecera) {
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();
            Nreg.put(TablesHelper.xms_order.id_numero,        cabecera.getIdNumero());
            Nreg.put(TablesHelper.xms_order.total,        cabecera.getTotal());
            Nreg.put(TablesHelper.xms_order.fecha,         cabecera.getFecha());
            Nreg.put(TablesHelper.xms_order.fechaDeVenc,         cabecera.getFechaVencimiento());
            Nreg.put(TablesHelper.xms_order.fechaDeEntrega,         cabecera.getFechaEntrega());
            Nreg.put(TablesHelper.xms_order.id_cond,        cabecera.getIdCond());
            Nreg.put(TablesHelper.xms_order.id_cliente,           cabecera.getIdCliente());
            Nreg.put(TablesHelper.xms_order.id_vendedor,   cabecera.getIdVendedor());
            Nreg.put(TablesHelper.xms_order.id_cobrador,   cabecera.getIdVendedor());
            Nreg.put(TablesHelper.xms_order.id_almacen,   cabecera.getIdAlmacen());
            Nreg.put(TablesHelper.xms_order.id_usuario,   cabecera.getIdUsuario());
            Nreg.put(TablesHelper.xms_order.estado,       cabecera.getEstado());
            Nreg.put(TablesHelper.xms_order.moneda,               "S");
            Nreg.put(TablesHelper.xms_order.direcc,      cabecera.getDirecc() == null ? "DIRECCION PRUEBA" : cabecera.getDirecc() );
            Nreg.put(TablesHelper.xms_order.observacion,   "");
            Nreg.put(TablesHelper.xms_order.typeDocument,  cabecera.getTypeDocument());

            db.insert(TablesHelper.xms_order.table, null, Nreg);
            Log.i(TAG, "guardarXMSPedidoCabecera: Registro insertado");
        } catch (Exception e) {
            Log.e(TAG, "guardarXMSPedidoCabecera: Error al insertar registro");
            e.printStackTrace();
        }
    }

    public boolean isHistorico(String numeroPedido){
        Boolean historic = true;

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            String subQuery = "SELECT COUNT(*) FROM " + TablesHelper.xms_pedido_cabecera.table +
                    " WHERE " + TablesHelper.xms_pedido_cabecera.numero_pedido + " ='" + numeroPedido + "'";
            Cursor curAux = db.rawQuery(subQuery, null);
            curAux.moveToFirst();
            while (!curAux.isAfterLast()) {
                if( curAux.getInt(0) > 0 ){
                    historic = false;
                }
                curAux.moveToNext();
            }
            curAux.close();
        } catch (Exception e) {
            Log.e(TAG, "Error get boolean if is historico pedido");
            e.printStackTrace();
            return historic;
        }
        return historic;
    }

    public void copiarItemPedidoDetalle(String numeroPedido, String nuevoNumeroPedido) {
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            String subQuery = "SELECT * FROM " + TablesHelper.xms_pedido_detalle.table +
                    " WHERE " + TablesHelper.xms_pedido_detalle.numero_pedido + " ='" + numeroPedido + "'";
            Cursor curAux = db.rawQuery(subQuery, null);
            curAux.moveToFirst();
            while (!curAux.isAfterLast()) {

                ContentValues Nreg = new ContentValues();

                Nreg.put(TablesHelper.xms_pedido_detalle.numero_pedido, nuevoNumeroPedido);
                Nreg.put(TablesHelper.xms_pedido_detalle.id_producto, curAux.getString(1));
                Nreg.put(TablesHelper.xms_pedido_detalle.precio_bruto, curAux.getString(3));
                Nreg.put(TablesHelper.xms_pedido_detalle.precio_neto, curAux.getString(5));
                Nreg.put(TablesHelper.xms_pedido_detalle.cantidad, curAux.getString(4));
                Nreg.put(TablesHelper.xms_pedido_detalle.tipo_producto, curAux.getString(2));
                Nreg.put(TablesHelper.xms_pedido_detalle.id_unidad_medida, curAux.getString(6));
                Nreg.put(TablesHelper.xms_pedido_detalle.item, curAux.getInt(10));
                Nreg.put(TablesHelper.xms_pedido_detalle.order_item, curAux.getString(9));//Es necesario una columna para poder ordenar los registros insertados
                Nreg.put(TablesHelper.xms_pedido_detalle.percepcion, curAux.getString(7));
                Nreg.put(TablesHelper.xms_pedido_detalle.ISC, curAux.getString(8));

                db.insert(TablesHelper.xms_pedido_detalle.table, null, Nreg);

                curAux.moveToNext();
            }

            curAux.close();
            Log.i(TAG, "agregarPedidoDetalle: lista insertado " + nuevoNumeroPedido);
        } catch (Exception e) {
            Log.e(TAG, "agregarPedidoDetalle: Error al insetar registro");
            e.printStackTrace();
        }
    }

    public boolean hasProductInPreviewList(String numPedido, String idProducto) {
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            String subQuery2 = "SELECT * FROM " +
                    TablesHelper.xms_order_detail.table +
                    " WHERE " + TablesHelper.xms_order_detail.id_producto + " ='" + idProducto + "'" +
                    " AND " + TablesHelper.xms_order_detail.id_numero + " ='" + numPedido + "'";
            Cursor curAux2 = db.rawQuery(subQuery2, null);

            if (curAux2.getCount() > 0) {
                return true;
            }
        }catch (Exception e) {
            Log.e(TAG, "agregarPedidoDetalle: Error al insetar registro");
            e.printStackTrace();
            return false;
        }
        return false;
    }
    public void agregarItemPedidoDetalle(OrderDetail item) {

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            String subQuery = "SELECT ifnull(max(order_item),0) FROM " +
                    TablesHelper.xms_order_detail.table +
                    " WHERE " + TablesHelper.xms_order_detail.id_numero + " ='" + item.getIdNumber() + "'";
            Cursor curAux = db.rawQuery(subQuery, null);
            curAux.moveToFirst();
            int nro_item = 0;
            while (!curAux.isAfterLast()) {
                nro_item = curAux.getInt(0);
                curAux.moveToNext();
            }
            curAux.close();
            nro_item++;

            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xms_order_detail.id_numero, item.getIdNumber());
            Nreg.put(TablesHelper.xms_order_detail.id_producto, item.getIdProduct());

            if(item.getTipotributo() == 3){
                Nreg.put(TablesHelper.xms_order_detail.precioUnit, Double.parseDouble("0.00"));
                Nreg.put(TablesHelper.xms_order_detail.precioUnitAlTipCam, Double.parseDouble("0.00"));
                Nreg.put(TablesHelper.xms_order_detail.monto, Double.parseDouble("0.00"));

                Nreg.put(TablesHelper.xms_order_detail.precunitgrat, item.getPrecioUnit());
                Nreg.put(TablesHelper.xms_order_detail.montograt, item.getMonto());
            }else{
                Nreg.put(TablesHelper.xms_order_detail.precioUnit, item.getPrecioUnit());
                Nreg.put(TablesHelper.xms_order_detail.precioUnitAlTipCam, item.getPrecioUnitTipoCambio());
                Nreg.put(TablesHelper.xms_order_detail.monto, item.getMonto());

                Nreg.put(TablesHelper.xms_order_detail.precunitgrat, Double.parseDouble("0.00"));
                Nreg.put(TablesHelper.xms_order_detail.montograt, Double.parseDouble("0.00"));
            }

            Nreg.put(TablesHelper.xms_order_detail.cantidad, item.getCantidad());
            Nreg.put(TablesHelper.xms_order_detail.moneda, "S");
            Nreg.put(TablesHelper.xms_order_detail.tipoDeCambio, item.getTipoCambio());
            Nreg.put(TablesHelper.xms_order_detail.id_medida, item.getIdMedida());
            Nreg.put(TablesHelper.xms_order_detail.cantidad2, item.getPeso());
            Nreg.put(TablesHelper.xms_order_detail.order_item, nro_item);

            db.insert(TablesHelper.xms_order_detail.table, null, Nreg);

            //Gson gson = new Gson();
            //Log.d(TAG,"agregarPedidoDetalle: Registrando producto..." + gson.toJson(item));// Cambiar
            Log.i(TAG, "agregarPedidoDetalle: Producto insertado " + item.getIdNumber() + " - " + item.getIdProduct());
        } catch (Exception e) {
            Log.e(TAG, "agregarPedidoDetalle: Error al insetar registro");
            e.printStackTrace();
        }

    }

    public void eliminarPedido(String numeroPedido) {
        String where = TablesHelper.xms_order.id_numero+" = ?";
        String[] args = { numeroPedido };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            db.delete(TablesHelper.xms_order.table, where, args);
            Log.i(TAG, "eliminarPedido: PedidoCabecera eliminado");
            db.delete(TablesHelper.xms_order_detail.table, where, args);
            Log.i(TAG, "eliminarPedido: PedidoDetalle eliminado");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean verificarPedidoTieneDetalle(String numeroPedido) {
        String rawQuery = "SELECT * FROM " + TablesHelper.xms_order_detail.table+
                " WHERE " + TablesHelper.xms_order_detail.id_numero
                +"= '"+numeroPedido+"'";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        if (cur.getCount() > 0) {
            cur.close();
            return true;
        }
        cur.close();
        return false;
    }

    public boolean eliminarItemDetallePedido (String numeroPedido, String idProducto, String tipoProducto) {
        String where = TablesHelper.xms_pedido_detalle.numero_pedido + " = ? AND "+TablesHelper.xms_pedido_detalle.id_producto + " = ? AND "+TablesHelper.xms_pedido_detalle.tipo_producto + " = ?";
        String[] args = { numeroPedido, idProducto, tipoProducto };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            db.delete(TablesHelper.xms_pedido_detalle.table,where,args);
            Log.i(TAG,"eliminarItemDetallePedido: producto removido");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getEstadoPedido(String numeroPedido) {
        String estado = "";
        try {
            String rawQuery;
            rawQuery = "SELECT ifnull("+TablesHelper.xms_pedido_cabecera.estado+",'') FROM "+TablesHelper.xms_pedido_cabecera.table+" WHERE "+TablesHelper.xms_pedido_cabecera.numero_pedido+" = '"+numeroPedido+"'";

            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            Cursor cur = db.rawQuery(rawQuery, null);

            cur.moveToFirst();
            if (cur.moveToFirst()) {
                do {
                    estado = cur.getString(0);
                } while (cur.moveToNext());
            }
            cur.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return estado;
    }

    public void actualizarIdCliente_old (String numeroPedido, String idCliente) {
        String where = TablesHelper.xms_pedido_cabecera.numero_pedido + " = ? AND "+TablesHelper.xms_pedido_cabecera.id_cliente + " = ? ";
        String[] args = { numeroPedido, idCliente };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues reg = new ContentValues();
            reg.put(TablesHelper.xms_pedido_cabecera.id_cliente, idCliente);
            db.update(TablesHelper.xms_pedido_cabecera.table, reg, where, args);
            Log.i(TAG,"actualizarIdCliente: actualizado");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getPdfPedido(String numeroPedido) {
        String pdfBase64 = null;
        try {
            String rawQuery;
            rawQuery = "SELECT "+TablesHelper.xms_pedido_cabecera.pdf_base64+" FROM "+
                    TablesHelper.xms_pedido_cabecera.table+" WHERE "+TablesHelper.xms_pedido_cabecera.numero_pedido+" = '"+
                    numeroPedido+"'";

            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            Cursor cur = db.rawQuery(rawQuery, null);

            cur.moveToFirst();
            if (cur.moveToFirst()) {
                do {
                    pdfBase64 = cur.getString(0);
                } while (cur.moveToNext());
            }
            cur.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return pdfBase64;
    }


    public OrderRequest getPostVenta(String numeroPedido) {
        String rawQuery = "SELECT * FROM " + TablesHelper.xms_order.table + " WHERE " +
                TablesHelper.xms_order.id_numero + " = '"+numeroPedido+"'";;

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        OrderRequest ventaRequest = null;

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            ventaRequest = new OrderRequest();

            ventaRequest.setIdNumero(cur.getString(0));
            ventaRequest.setFechaDeEntrega(cur.getString(3));
            ventaRequest.setFechaDeVenc(cur.getString(2));
            ventaRequest.setIdCliente(cur.getInt(4));
            ventaRequest.setDirecc(cur.getString(5));
            ventaRequest.setIdCond(cur.getInt(6));
            ventaRequest.setIdVendedor(cur.getInt(7));
            ventaRequest.setIdCobrador(cur.getInt(8));
            ventaRequest.setIdAlmacen(cur.getInt(10));
            ventaRequest.setMoneda(cur.getString(11).substring(0, 1));
            ventaRequest.setTipoDeCambio(cur.getDouble(12));
            ventaRequest.setSubtotal(cur.getDouble(13));
            ventaRequest.setDescuento(Double.parseDouble("0.00"));
            ventaRequest.setTotal(cur.getDouble(15));
            ventaRequest.setIdUsuario(cur.getInt(19));
            ventaRequest.setEstado(cur.getString(20));
            ventaRequest.setIdDistrito(cur.getInt(26));
            ventaRequest.setCodUbigeo(cur.getString(28));
            ventaRequest.setObservacion(cur.getString(33));
            ventaRequest.setTotalOpExonerado(cur.getDouble(34));
            ventaRequest.setTotalOpgratuito(cur.getDouble(35));
            ventaRequest.setTypeDocument(cur.getString(36));

            cur.moveToNext();
        }
        cur.close();

        if (ventaRequest != null){
            ArrayList<OrderItemRequest> items = getPostVentaItems(ventaRequest.getIdNumero());
            ventaRequest.setOrderDetalle(items);

            Log.d("TAG","items: " + items.size());
        }
        return ventaRequest;
    }

    private ArrayList<OrderItemRequest> getPostVentaItems(String numeroPedido) {
        String rawQuery;

        rawQuery = "SELECT " +
                "pd."+TablesHelper.xms_order_detail.id_producto +
                ",p."+TablesHelper.xms_product.codigo +
                ",pd."+TablesHelper.xms_order_detail.id_medida +
                ",pd."+TablesHelper.xms_order_detail.moneda +
                ",pd."+TablesHelper.xms_order_detail.cantidad +
                ",pd."+TablesHelper.xms_order_detail.precioUnit +
                ",pd."+TablesHelper.xms_order_detail.monto +
                ",pd."+TablesHelper.xms_order_detail.tipoDeCambio +
                ",pd."+TablesHelper.xms_order_detail.cantidad2 +
                ",p."+TablesHelper.xms_product.tipo_atributo +
                ",pd."+TablesHelper.xms_order_detail.precunitgrat +
                ",pd."+TablesHelper.xms_order_detail.montograt +
                " FROM "+ TablesHelper.xms_order_detail.table + " pd " +
                " INNER JOIN "+ TablesHelper.xms_product.table + " p ON pd."+
                TablesHelper.xms_order_detail.id_producto+" = p."+TablesHelper.xms_product.id_producto +
                " WHERE " + TablesHelper.xms_order_detail.id_numero + " = '" + numeroPedido+ "'";

        ArrayList<OrderItemRequest> lista = new ArrayList<>();
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cur = db.rawQuery(rawQuery, null);
        cur.moveToFirst();
        if (cur.moveToFirst()) {
            do {
                OrderItemRequest pedidoDetalle = new OrderItemRequest();

                pedidoDetalle.setIdProducto(cur.getInt(0));
                pedidoDetalle.setIdMedida(cur.getInt(2));
                pedidoDetalle.setMoneda(cur.getString(3).substring(0, 1));
                pedidoDetalle.setCantidad(cur.getDouble(4));
                pedidoDetalle.setPrecioUnit(cur.getDouble(5));
                pedidoDetalle.setPrecioUnitAlTipCam(cur.getDouble(5));
                pedidoDetalle.setMonto(cur.getDouble(6));
                pedidoDetalle.setMontoAlTipCam(cur.getDouble(6));
                pedidoDetalle.setTipoDeCambio(cur.getDouble(7));
                pedidoDetalle.setPeso(cur.getDouble(8));
                pedidoDetalle.setTipotributo(cur.getString(9));
                pedidoDetalle.setPrecunitgrat(cur.getDouble(10));
                pedidoDetalle.setMontograt(cur.getDouble(11));

                lista.add(pedidoDetalle);
            } while (cur.moveToNext());
        }
        cur.close();
        return lista;
    }

    public String actualizarRepuestaPedido(Response<ResponseBody> response, String numeroPedido,
                                           OrderRequest venta) throws Exception{

        JSONObject body = new JSONObject(response.body().string());

        if (body.has("status")){
                SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
                String where = TablesHelper.xms_order.id_numero + " = ?";

                ContentValues updateValues = new ContentValues();
                updateValues.put(TablesHelper.xms_order.total, venta.getTotal());
                updateValues.put(TablesHelper.xms_order.subtotal, venta.getSubtotal());
                updateValues.put(TablesHelper.xms_order.total_opexonerado, venta.getTotalOpExonerado());
                updateValues.put(TablesHelper.xms_order.total_opgratuito, venta.getTotalOpgratuito());

                String[] args = { numeroPedido };

                Log.i(TAG, "Actualizar "+TablesHelper.xms_order.table+": modificando..."+numeroPedido);
                db.update(TablesHelper.xms_order.table, updateValues, where, args );

                return Order.FLAG_ENVIADO;
        }else {
            return Constants.FAIL_CONNECTION;
        }
    }




}
