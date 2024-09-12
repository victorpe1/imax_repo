package com.sales.storeapp.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sales.storeapp.App;
import com.sales.storeapp.managers.DataBaseHelper;
import com.sales.storeapp.managers.TablesHelper;
import com.sales.storeapp.models.Almacen;
import com.sales.storeapp.models.CondicionPago;
import com.sales.storeapp.models.GenericModel;
import com.sales.storeapp.models.Personal;
import com.sales.storeapp.models.UsuarioModel;
import com.sales.storeapp.utils.Constants;
import com.sales.storeapp.utils.Security;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DAOExtras {
    public final String TAG = getClass().getName();
    public DataBaseHelper dataBaseHelper;
    Context context;

    public DAOExtras(Context context){
        this.context = context;
        dataBaseHelper = DataBaseHelper.getInstance(context);
    }

    public boolean login(String usuario, String clave){
        boolean flag = false;
        Log.d("TAG",Security.getSHA512Secure(clave));
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM xms_usuario WHERE usuario = ? AND clave = ?", new String[]{usuario, Security.getSHA512Secure(clave)});
            flag = (cursor.getCount() > 0);
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public String sincroUsuario(Response<ResponseBody> response,String usuario,String contrasena) throws Exception{
        Log.e(TAG,"sincroUsuario...");
        if (response.isSuccessful()) {
            JSONObject jsonObject = new JSONObject(response.body().string());
            boolean success = jsonObject.getBoolean("success");
            String data = jsonObject.getString("data");
            String message = jsonObject.getString("message");

            if (success){
                JSONObject jsonData = new JSONObject(data);

                UsuarioModel usuarioModel = new UsuarioModel();
                usuarioModel.setIdUsuario(jsonData.getInt("id_usuario"));
                usuarioModel.setNombre(jsonData.getString("nombre"));
                usuarioModel.setTelefono(jsonData.getString("celular"));
                usuarioModel.setTipoUsuario(jsonData.getString("tipo"));
                //Cuando se obtenga datos de usuario desde login,
                if (usuario != null){
                    Log.e(TAG,"EL USUARIO CCORREO ES NULL");
                    usuarioModel.setCorreo(usuario);
                }else{
                    Log.e(TAG,"EL USUARIO CCORREO NO ES NULL");
                }

                if (contrasena != null)
                    usuarioModel.setClave(Security.getSHA512Secure(contrasena));
                usuarioModel.setSerie((jsonData.isNull("serie"))?null:jsonData.getString("serie"));//resultado = (condicion)?valor1:valor2;

                if (usuarioModel.getTipoUsuario().equals(UsuarioModel.TIPO_USUARIO_NEGOCIO)) {
                    usuarioModel.setIdBodegaCliente(jsonData.getString("id_bodega"));
                    usuarioModel.setRuc(jsonData.getString("ruc"));
                    usuarioModel.setRazonSocial(jsonData.getString("razon_social"));
                    usuarioModel.setLatitud(jsonData.getString("latitud"));
                    usuarioModel.setLongitud(jsonData.getString("longitud"));
                    usuarioModel.setDireccion(jsonData.getString("direccion"));
                    usuarioModel.setIdClienteMagic((jsonData.isNull("id_cliente_magic"))?null:jsonData.getString("id_cliente_magic"));
                }else {
                    usuarioModel.setIdBodegaCliente(jsonData.getString("id_cliente"));
                }

                Log.i(TAG,new Gson().toJson(usuarioModel));
                App app = (App) context;
                //app.setPref_idBodegaCliente(usuarioModel.getIdBodegaCliente());
                //app.setPref_idUsuario(usuarioModel.getIdUsuario());
                app.setPref_serieUsuario(usuarioModel.getSerie());
                if (usuarioModel.getIdClienteMagic() != null)
                    //app.setPref_idClienteMagic(usuarioModel.getIdClienteMagic());
                guardarUsuario(usuarioModel);
                return Constants.CORRECT;
            }else{
                return message;
            }
        } else {
            switch (response.code()) {
                case 404:
                    return Constants.FAIL_JSON;
                default:
                    return Constants.FAIL_OTHER;
            }
        }
    }

    public void guardarUsuario(UsuarioModel model){
        if (model != null){
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            db.delete(TablesHelper.xms_usuario.table, "id <> ?",new String[]{String.valueOf(model.getIdUsuario())});

            String rawQuery =
                    "SELECT * "+
                    "FROM "+ TablesHelper.xms_usuario.table+" "+
                    "WHERE "+TablesHelper.xms_usuario.id+" ='"+model.getIdUsuario()+"'";
            Cursor cur = db.rawQuery(rawQuery, null);
            cur.moveToFirst();

            if (cur.getCount() > 0){
                ContentValues values = new ContentValues();
                values.put(TablesHelper.xms_usuario.nombre, model.getNombre());
                values.put(TablesHelper.xms_usuario.celular, model.getTelefono());
                values.put(TablesHelper.xms_usuario.correo, model.getCorreo());
                if (model.getUsuario() != null) {
                    //Log.e(TAG,"GET CORREO != NULL, ACTUALIZANDO USUARIO CON:"+model.getUsuario());
                    values.put(TablesHelper.xms_usuario.correo, model.getUsuario());
                }else{
                    //Log.e(TAG,"GET CORREO == NULL, NO ACTUALIZANDO USUARIO");
                }
                if (model.getClave() != null)
                    values.put(TablesHelper.xms_usuario.clave, model.getClave());
                values.put(TablesHelper.xms_usuario.tipo_usuario, model.getTipoUsuario());
                values.put(TablesHelper.xms_usuario.serie, model.getSerie());
                values.put(TablesHelper.xms_usuario.id_bodega_cliente, model.getIdBodegaCliente());
                db.update(TablesHelper.xms_usuario.table, values,TablesHelper.xms_usuario.id+" = ?",new String[]{String.valueOf(model.getIdUsuario())});
            }else{
                ContentValues values = new ContentValues();
                values.put(TablesHelper.xms_usuario.id, model.getIdUsuario());
                values.put(TablesHelper.xms_usuario.nombre, model.getNombre());
                values.put(TablesHelper.xms_usuario.celular, model.getTelefono());
                values.put(TablesHelper.xms_usuario.correo, model.getCorreo());
                values.put(TablesHelper.xms_usuario.clave, model.getClave());
                values.put(TablesHelper.xms_usuario.tipo_usuario, model.getTipoUsuario());
                values.put(TablesHelper.xms_usuario.serie, model.getSerie());
                values.put(TablesHelper.xms_usuario.id_bodega_cliente, model.getIdBodegaCliente());
                values.put(TablesHelper.xms_usuario.usuario, model.getUsuario());
                db.insert(TablesHelper.xms_usuario.table, null, values);
            }
            cur.close();
            Log.i(TAG, "Usuario registrado: "+model.getIdUsuario());
        }
    }

    public UsuarioModel getDatosUsuario(){
        UsuarioModel usuarioModel = null;
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery = "SELECT u.id,nombre,correo,tipo_usuario,id_bodega_cliente " +
                    "FROM xms_usuario u ";
            Cursor cursor = db.rawQuery(rawQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                usuarioModel = new UsuarioModel();
                usuarioModel.setIdUsuario(cursor.getInt(0));
                usuarioModel.setNombre(cursor.getString(1));
                usuarioModel.setCorreo(cursor.getString(2));
                usuarioModel.setTipoUsuario(cursor.getString(3));
                usuarioModel.setIdBodegaCliente(cursor.getString(4));
                /*if (usuarioModel.getTipoUsuario().equals(UsuarioModel.TIPO_USUARIO_NEGOCIO)){
                    usuarioModel.setIdBodega(cursor.getString(4));
                }else{
                    usuarioModel.setIdCliente(cursor.getString(4));
                }
                usuarioModel.setRazonSocial(cursor.getString(5));
                usuarioModel.setRuc(cursor.getString(6));
                usuarioModel.setDireccion(cursor.getString(7)); */

                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return usuarioModel;
    }

    public boolean existeUsuario(){
        boolean flag = false;
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery = "SELECT * FROM xms_usuario";
            Cursor cursor = db.rawQuery(rawQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                flag = true;
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public ArrayList<CondicionPago> getListCondicionPago(){
        String rawQuery = "SELECT * FROM " + TablesHelper.xms_condiciones.table +
                            " WHERE nombre LIKE '%CONTADO%'";
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);

        ArrayList<CondicionPago> lista = new ArrayList<>();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            CondicionPago condicionPago = new CondicionPago();
            condicionPago.setIdCondicion(cur.getInt(0));
            condicionPago.setNombre(cur.getString(1));
            condicionPago.setDias(cur.getInt(2));
            condicionPago.setTipo(cur.getString(3));
            lista.add(condicionPago);
            cur.moveToNext();
        }
        cur.close();
        return lista;
    }

    public ArrayList<Almacen> getAlmacenes(){
        String rawQuery = "SELECT * FROM " + TablesHelper.xms_almacenes.table
                + " WHERE nombre LIKE '%PROD TERMINADO%'";
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);

        ArrayList<Almacen> lista = new ArrayList<>();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            Almacen almacen = new Almacen();
            almacen.setIdAlmacen(cur.getInt(0));
            almacen.setNombre(cur.getString(1));
            almacen.setUbicacion(cur.getString(2));
            lista.add(almacen);
            cur.moveToNext();
        }
        cur.close();
        return lista;
    }

    public double getTCambioToday(){

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        double cambioToday = 0.00d;

        String rawQuery = "SELECT * FROM xms_tcambio WHERE strftime('%Y-%m-%d', fecha) = ?";
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, new String[]{today});

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            cambioToday = cur.getDouble(3);

            cur.moveToNext();
        }
        cur.close();

        return cambioToday;
    }

    public List<GenericModel> getTipoDocumento(){
        List<GenericModel> lista = new ArrayList<>();

        GenericModel item = new GenericModel("FA", "FACTURA");
        lista.add(item);
        item = new GenericModel("BV", "BOLETA");
        lista.add(item);
        item = new GenericModel("NE", "NOTA DE ENTREGA");
        lista.add(item);


        return lista;
    }

    public ArrayList<Personal> getPersonal(){
        String rawQuery =
                "SELECT " +
                        TablesHelper.xms_personal.id_personal +
                        ","+TablesHelper.xms_personal.nombre +
                        ","+TablesHelper.xms_personal.domicilio +
                        ","+TablesHelper.xms_personal.id_distrito +
                        ","+TablesHelper.xms_personal.telefonos +
                        ","+TablesHelper.xms_personal.dni +
                        ","+TablesHelper.xms_personal.ruc +
                        ","+TablesHelper.xms_personal.pcomision +
                        ","+TablesHelper.xms_personal.id_ocupacion +
                        ","+TablesHelper.xms_personal.fechanac +
                        ","+TablesHelper.xms_personal.sexo +
                        " FROM "+TablesHelper.xms_personal.table+
                        " WHERE "+ TablesHelper.xms_personal.activo +
                        " = 1 ORDER BY 3 ";

        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        ArrayList<Personal> lista = new ArrayList<>();
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            Personal personal = new Personal();
            personal.setIdPersonal(cur.getInt(0));
            personal.setNombre(cur.getString(1));
            personal.setDomicilio(cur.getString(2));
            personal.setIdDistrito(cur.getInt(3));
            personal.setTelefonos(cur.getString(4));
            personal.setDni(cur.getString(5));
            personal.setRuc(cur.getString(6));
            personal.setPcomision(cur.getInt(7));
            personal.setIdOcupacion(cur.getInt(8));
            personal.setFechanac(cur.getString(9));
            personal.setSexo(cur.getInt(10));

            lista.add(personal);
            cur.moveToNext();
        }
        cur.close();
        return lista;
    }

    public String getMaximoPedido() {
        String valor="";
        try{
            String rawQuery =
                    "SELECT * FROM "+TablesHelper.xms_configuracion_usuario.table+" " +
                            "WHERE "+TablesHelper.xms_configuracion_usuario.identificador +
                            " = '"+TablesHelper.xms_configuracion_usuario.MaximoPedido+"'";

            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            //Log.d(TAG,rawQuery);
            Cursor cur = db.rawQuery(rawQuery, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                valor = cur.getString(1);
                cur.moveToNext();
            }
            cur.close();
            Log.d(TAG,rawQuery+" :"+valor);
        }catch (Exception e){
            e.printStackTrace();
        }
        return valor;
    }

    public UsuarioModel getCuenta(){
        UsuarioModel usuarioModel = null;
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery =
                    "SELECT " +TablesHelper.xms_usuario.nombre +
                    ","+TablesHelper.xms_usuario.celular +
                    ","+TablesHelper.xms_usuario.correo +
                    " FROM "+TablesHelper.xms_usuario.table;
            Cursor cursor = db.rawQuery(rawQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                usuarioModel = new UsuarioModel();
                usuarioModel.setNombre(cursor.getString(0));
                usuarioModel.setTelefono(cursor.getString(1));
                usuarioModel.setCorreo(cursor.getString(2));

                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return usuarioModel;
    }

    public void limpiarTablas(){
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            //db.delete(TablesHelper.xms_mi_pedido_cabecera.table,null,null);
            //db.delete(TablesHelper.xms_mi_pedido_detalle.table,null,null);

            Log.w(TAG,"limpiarTablas: Limpiando tablas...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<GenericModel> getTiposCorreo(){
        List<GenericModel> lista = new ArrayList<>();

        lista.add(new GenericModel("0","No envía correo"));
        lista.add(new GenericModel("1","Correo al emitir"));
        lista.add(new GenericModel("2","Correo cuando SUNAT acepte el comprobante"));
        return lista;
    }

    public List<String> getMedioPago(){
        List<String> listMedioPago = new ArrayList<>();
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery =
                    "SELECT " +TablesHelper.xms_condiciones.nombre +
                            " FROM "+TablesHelper.xms_condiciones.table;
            Cursor cursor = db.rawQuery(rawQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                listMedioPago.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return listMedioPago;
    }
}
