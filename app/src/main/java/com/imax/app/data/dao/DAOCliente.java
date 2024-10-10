package com.imax.app.data.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.ClienteModel;

import java.util.ArrayList;

public class DAOCliente {
    public final String TAG = getClass().getName();
    DataBaseHelper dataBaseHelper;
    Context context;

    public DAOCliente(Context context){
        this.context = context;
        dataBaseHelper = DataBaseHelper.getInstance(context);
    }

   public ArrayList<ClienteModel> getClientes() {

        String rawQuery =
                "SELECT " +
                        TablesHelper.xms_client.id_cliente +
                        ",c."+TablesHelper.xms_client.nombre +
                        ","+TablesHelper.xms_client.representante +
                        ","+TablesHelper.xms_client.domicilio +
                        ",c."+TablesHelper.xms_client.id_distrito +
                        ","+TablesHelper.xms_client.ocupacion +
                        ","+TablesHelper.xms_client.telefonos +
                        ","+TablesHelper.xms_client.fax +
                        ","+TablesHelper.xms_client.ruc +
                        ","+TablesHelper.xms_client.dni +
                        ","+TablesHelper.xms_client.moneda +
                        ","+TablesHelper.xms_client.direcc1 +
                        ","+TablesHelper.xms_client.direcc2 +
                        ","+TablesHelper.xms_client.direcc3 +
                        ","+TablesHelper.xms_client.direcc4 +
                        ","+TablesHelper.xms_client.email1 +
                        ","+TablesHelper.xms_client.email2 +
                        ","+TablesHelper.xms_client.email3 +
                        ","+TablesHelper.xms_client.email4 +
                        ","+TablesHelper.xms_client.codubigeo1 +
                        ","+TablesHelper.xms_client.codubigeo2 +
                        ","+TablesHelper.xms_client.codubigeo3 +
                        ","+TablesHelper.xms_client.codubigeo4 +
                        ", c."+TablesHelper.xms_client.codubigeo +
                        ", d."+TablesHelper.xms_distritos.nombre +
                        ", "+TablesHelper.xms_client.id_vendedor
                        + " FROM "+TablesHelper.xms_client.table+" c"
                        + " INNER JOIN "+TablesHelper.xms_distritos.table+" d ON c."
                        + TablesHelper.xms_client.id_distrito+" = d."+TablesHelper.xms_distritos.id_distrito
                        + " WHERE  "+ TablesHelper.xms_client.activo + " = 1 ORDER BY 3";
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, null);
        ArrayList<ClienteModel> lista = new ArrayList<>();
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            ClienteModel clienteModel = new ClienteModel();
            clienteModel.setIdCliente(cur.getString(0));
            clienteModel.setRazonSocial(cur.getString(1));
            clienteModel.setRepresentante(cur.getString(2));
            clienteModel.setDomicilio(cur.getString(3));
            clienteModel.setIdDistrito(cur.getInt(4));
            clienteModel.setOcupacion(cur.getString(5));
            clienteModel.setTelefonos(cur.getString(6));
            clienteModel.setFax(cur.getString(7));
            clienteModel.setRuc(cur.getString(8));
            clienteModel.setDni(cur.getString(9));
            clienteModel.setMoneda(cur.getString(10));
            clienteModel.setDireccion(cur.getString(11));
            clienteModel.setDireccion2(cur.getString(12));
            clienteModel.setDireccion3(cur.getString(13));
            clienteModel.setDireccion4(cur.getString(14));
            clienteModel.setEmail1(cur.getString(15));
            clienteModel.setEmail2(cur.getString(16));
            clienteModel.setEmail3(cur.getString(17));
            clienteModel.setEmail4(cur.getString(18));
            clienteModel.setCodUbigeo1(cur.getString(19));
            clienteModel.setCodUbigeo2(cur.getString(20));
            clienteModel.setCodUbigeo3(cur.getString(21));
            clienteModel.setCodUbigeo4(cur.getString(22));
            clienteModel.setCodUbigeo(cur.getString(23));
            clienteModel.setDistrito(cur.getString(24));

            if (!cur.isNull(25)) {
                clienteModel.setIdVendedor(cur.getInt(25));
            } else {
                clienteModel.setIdVendedor(0);
            }

            lista.add(clienteModel);
            cur.moveToNext();
        }
        cur.close();
        return lista;
    }
}
