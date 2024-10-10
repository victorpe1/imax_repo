package com.imax.app.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.imax.app.App;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.intents.AntesInspeccion;
import com.imax.app.intents.CaracteristicasEdificacion;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.intents.DespuesInspeccion;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.models.CondicionPago;
import com.imax.app.models.GenericModel;
import com.imax.app.models.Personal;
import com.imax.app.models.UsuarioModel;
import com.imax.app.utils.Constants;
import com.imax.app.utils.Security;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public List<AsignacionModel> getListAsignacion(){
        List<AsignacionModel> listAsignacion = new ArrayList<>();
        try{
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery =
                    "SELECT * FROM "+TablesHelper.xms_asignacion.table;

            Cursor cursor = db.rawQuery(rawQuery, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                AsignacionModel asignacionModel = new AsignacionModel();

                asignacionModel.setId(cursor.getString(0));
                asignacionModel.setAddress(cursor.getString(1));
                asignacionModel.setCoordinatorId(cursor.getString(2));
                asignacionModel.setCoordinatorName(cursor.getString(3));
                asignacionModel.setCustomField1(cursor.getString(4));
                asignacionModel.setCustomField19(cursor.getString(5));
                asignacionModel.setCustomField2(cursor.getString(6));
                asignacionModel.setCustomField3(cursor.getString(7));
                asignacionModel.setInspectionDate(cursor.getString(8));
                asignacionModel.setInspectorId(cursor.getString(9));
                asignacionModel.setInspectorName(cursor.getString(10));
                asignacionModel.setMotiveId(cursor.getString(11));
                asignacionModel.setMotiveName(cursor.getString(12));
                asignacionModel.setName(cursor.getString(13));
                asignacionModel.setNumber(cursor.getString(14));
                asignacionModel.setPartnerLatitude(cursor.getString(15));
                asignacionModel.setPartnerLongitude(cursor.getString(16));
                asignacionModel.setTypeId(cursor.getString(17));
                asignacionModel.setTypeName(cursor.getString(18));

                listAsignacion.add(asignacionModel);
                cursor.moveToNext();
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return listAsignacion;
    }


    public InspeccionRequest getListAsignacionByNumero(String numero) {
        InspeccionRequest inspeccionRequest = new InspeccionRequest();
        try {
            SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
            String rawQuery =
                    "SELECT * FROM " + TablesHelper.xml_inspeccion.table + " WHERE numInspeccion = ?";
            Cursor cursor = db.rawQuery(rawQuery, new String[]{numero});

            if (cursor.moveToFirst()) {
                inspeccionRequest.setNumInspeccion(cursor.getString(1));
                inspeccionRequest.setModalidad(cursor.getString(2));
                inspeccionRequest.setInscripcion(cursor.getString(3));
                inspeccionRequest.setFecha(cursor.getString(4));
                inspeccionRequest.setHora(cursor.getString(5));
                inspeccionRequest.setContacto(cursor.getString(6));
                inspeccionRequest.setLatitud(cursor.getString(7));
                inspeccionRequest.setLongitud(cursor.getString(8));
                inspeccionRequest.setDireccion(cursor.getString(9));
                inspeccionRequest.setDistrito(cursor.getString(10));
                inspeccionRequest.setProvincia(cursor.getString(11));
                inspeccionRequest.setDepartamento(cursor.getString(12));

                // Características Generales
                inspeccionRequest.setTipoInmueble(cursor.getString(13));
                inspeccionRequest.setOtros(cursor.getString(14));
                inspeccionRequest.setUsosInmueble(cursor.getString(15));
                inspeccionRequest.setComentarios(cursor.getString(16));
                inspeccionRequest.setRecibeInmueble(cursor.getString(17));
                inspeccionRequest.setnPisos(cursor.getString(18));
                inspeccionRequest.setDistribucion(cursor.getString(19));
                inspeccionRequest.setReferencia(cursor.getString(20));
                inspeccionRequest.setDeposito(cursor.getString(21));
                inspeccionRequest.setEstacionamiento(cursor.getString(22));
                inspeccionRequest.setDepto(cursor.getString(23));

                // Características de Edificación
                inspeccionRequest.setOtroEstructura(cursor.getString(24));
                inspeccionRequest.setOtroAlbañeria(cursor.getString(25));
                inspeccionRequest.setOtroPisos(cursor.getString(26));
                inspeccionRequest.setOtroPuertas(cursor.getString(27));
                inspeccionRequest.setOtroVentana(cursor.getString(28));
                inspeccionRequest.setOtroMampara(cursor.getString(29));
                inspeccionRequest.setOtroCocina(cursor.getString(30));
                inspeccionRequest.setOtroBaño(cursor.getString(31));
                inspeccionRequest.setOtroSanitarias(cursor.getString(32));
                inspeccionRequest.setOtroElectricas(cursor.getString(33));
                inspeccionRequest.setTipoPuerta(cursor.getString(34));
                inspeccionRequest.setMaterialPuerta(cursor.getString(35));
                inspeccionRequest.setSistemaPuerta(cursor.getString(36));
                inspeccionRequest.setMarcoVentana(cursor.getString(37));
                inspeccionRequest.setVidrioVentana(cursor.getString(38));
                inspeccionRequest.setSistemaVentana(cursor.getString(39));
                inspeccionRequest.setMarcoMampara(cursor.getString(40));
                inspeccionRequest.setVidrioMampara(cursor.getString(41));
                inspeccionRequest.setSistemaMampara(cursor.getString(42));
                inspeccionRequest.setMuebleCocina(cursor.getString(43));
                inspeccionRequest.setMuebleCocina2(cursor.getString(44));
                inspeccionRequest.setTablero(cursor.getString(45));
                inspeccionRequest.setLavaderos(cursor.getString(46));
                inspeccionRequest.setSanitarioTipo(cursor.getString(47));
                inspeccionRequest.setSanitarioColor(cursor.getString(48));
                inspeccionRequest.setSanitario(cursor.getString(49));
                inspeccionRequest.setIss(cursor.getString(50));
                inspeccionRequest.setIiee(cursor.getString(51));
                inspeccionRequest.setSistemaIncendio(cursor.getString(52));
                inspeccionRequest.setEstructura(cursor.getString(53));
                inspeccionRequest.setMuros(cursor.getString(54));
                inspeccionRequest.setRevestimiento(cursor.getString(55));
                inspeccionRequest.setPisos(cursor.getString(56));
                inspeccionRequest.setPisosCocina(cursor.getString(57));
                inspeccionRequest.setParedesCocina(cursor.getString(58));
                inspeccionRequest.setPisosBaños(cursor.getString(59));
                inspeccionRequest.setParedesBaño(cursor.getString(60));

                // Características de Infraestructura
                inspeccionRequest.setInfraestructura_comentario(cursor.getString(61));

                // Después de la Inspección
                inspeccionRequest.setCbCoincideInformacion(cursor.getString(62));
                inspeccionRequest.setCbDocumentacionSITU(cursor.getString(63));
                inspeccionRequest.setEspecificar(cursor.getString(64));
                inspeccionRequest.setEspecificar2(cursor.getString(65));

                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> files = gson.fromJson(cursor.getString(66), listType);

                inspeccionRequest.setFiles(files);
                inspeccionRequest.setObservacion(cursor.getString(67));
                inspeccionRequest.setBase64Firma(cursor.getString(68));

                // Estado de envío al backend
                //inspeccionRequest.setEs(cursor.getString(69));
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inspeccionRequest;
    }


    public boolean existeRegistro(String idAsignacion){

        boolean existe = false;

        String rawQuery = "SELECT * FROM xml_inspeccion WHERE numInspeccion = ?";
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(rawQuery, new String[]{idAsignacion});

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            existe = true;
            cur.moveToNext();
        }
        cur.close();

        return existe;
    }

    public void actualizarRegistroInpeccion(AntesInspeccion antesInspeccion){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { antesInspeccion.getNumInspeccion() };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xml_inspeccion.modalidad,        antesInspeccion.getModalidad());
            Nreg.put(TablesHelper.xml_inspeccion.inscripcion,         antesInspeccion.getInscripcion());
            Nreg.put(TablesHelper.xml_inspeccion.fecha,        antesInspeccion.getFecha());
            Nreg.put(TablesHelper.xml_inspeccion.hora,           antesInspeccion.getHora());
            Nreg.put(TablesHelper.xml_inspeccion.contacto,   antesInspeccion.getContacto());
            Nreg.put(TablesHelper.xml_inspeccion.latitud,           antesInspeccion.getLatitud());
            Nreg.put(TablesHelper.xml_inspeccion.longitud,        antesInspeccion.getLongitud());
            Nreg.put(TablesHelper.xml_inspeccion.direccion,           antesInspeccion.getDireccion());
            Nreg.put(TablesHelper.xml_inspeccion.distrito,   antesInspeccion.getDistrito());
            Nreg.put(TablesHelper.xml_inspeccion.provincia,           antesInspeccion.getProvincia());
            Nreg.put(TablesHelper.xml_inspeccion.departamento,           antesInspeccion.getDepartamento());

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void actualizarRegistroCaracGeneral(CaracteristicasGenerales caracteristicasGenerales, String numInspeccion){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { numInspeccion };

        String usosInmueble = "";
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();


            List<String> usosSeleccionados = new ArrayList<>();
            if (caracteristicasGenerales.isCbVivienda()) usosSeleccionados.add("Vivienda");
            if (caracteristicasGenerales.isCbComercio()) usosSeleccionados.add("Comercio");
            if (caracteristicasGenerales.isCbIndustria()) usosSeleccionados.add("Industria");
            if (caracteristicasGenerales.isCbEducativo()) usosSeleccionados.add("Educativo");
            if (caracteristicasGenerales.isCbOther()) usosSeleccionados.add("Otro");
            String textoResultado = TextUtils.join(", ", usosSeleccionados);
            usosInmueble = textoResultado;


            Nreg.put(TablesHelper.xml_inspeccion.tipoInmueble,        caracteristicasGenerales.getTipoInmueble());
            Nreg.put(TablesHelper.xml_inspeccion.otros,         caracteristicasGenerales.getOtros());
            Nreg.put(TablesHelper.xml_inspeccion.usosInmueble,        usosInmueble);
            Nreg.put(TablesHelper.xml_inspeccion.comentarios,           caracteristicasGenerales.getComentarios());
            Nreg.put(TablesHelper.xml_inspeccion.recibeInmueble,   caracteristicasGenerales.getRecibeInmueble());
            Nreg.put(TablesHelper.xml_inspeccion.nPisos,           caracteristicasGenerales.getNPisos());
            Nreg.put(TablesHelper.xml_inspeccion.distribucion,        caracteristicasGenerales.getDistribucion());
            Nreg.put(TablesHelper.xml_inspeccion.referencia,           caracteristicasGenerales.getReferencia());
            Nreg.put(TablesHelper.xml_inspeccion.estacionamiento,   caracteristicasGenerales.getEstacionamiento());
            Nreg.put(TablesHelper.xml_inspeccion.depto,           caracteristicasGenerales.getDepto());

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void actualizarRegistroCaracEdificacion(CaracteristicasEdificacion caracteristicasEdificacion, String numInspeccion){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { numInspeccion };

        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xml_inspeccion.otroEstructura,        caracteristicasEdificacion.getOtroEstructura());
            Nreg.put(TablesHelper.xml_inspeccion.otroAlbañeria,         caracteristicasEdificacion.getOtroAlbañeria());
            Nreg.put(TablesHelper.xml_inspeccion.otroPisos,        caracteristicasEdificacion.getOtroPisos());
            Nreg.put(TablesHelper.xml_inspeccion.otroPuertas,           caracteristicasEdificacion.getOtroPuertas());
            Nreg.put(TablesHelper.xml_inspeccion.otroVentana,   caracteristicasEdificacion.getOtroVentana());
            Nreg.put(TablesHelper.xml_inspeccion.otroMampara,           caracteristicasEdificacion.getOtroMampara());
            Nreg.put(TablesHelper.xml_inspeccion.otroCocina,        caracteristicasEdificacion.getOtroCocina());
            Nreg.put(TablesHelper.xml_inspeccion.otroBaño,           caracteristicasEdificacion.getOtroBaño());
            Nreg.put(TablesHelper.xml_inspeccion.otroSanitarias,   caracteristicasEdificacion.getOtroSanitarias());
            Nreg.put(TablesHelper.xml_inspeccion.otroElectricas,           caracteristicasEdificacion.getOtroElectricas());
            Nreg.put(TablesHelper.xml_inspeccion.tipoPuerta,        caracteristicasEdificacion.getTipoPuerta());
            Nreg.put(TablesHelper.xml_inspeccion.materialPuerta,           caracteristicasEdificacion.getMaterialPuerta());
            Nreg.put(TablesHelper.xml_inspeccion.sistemaPuerta,   caracteristicasEdificacion.getSistemaPuerta());
            Nreg.put(TablesHelper.xml_inspeccion.marcoVentana,           caracteristicasEdificacion.getMarcoVentana());
            Nreg.put(TablesHelper.xml_inspeccion.vidrioVentana,        caracteristicasEdificacion.getVidrioVentana());
            Nreg.put(TablesHelper.xml_inspeccion.sistemaVentana,           caracteristicasEdificacion.getSistemaVentana());
            Nreg.put(TablesHelper.xml_inspeccion.marcoMampara,   caracteristicasEdificacion.getMarcoMampara());
            Nreg.put(TablesHelper.xml_inspeccion.vidrioMampara,        caracteristicasEdificacion.getVidrioMampara());
            Nreg.put(TablesHelper.xml_inspeccion.sistemaMampara,           caracteristicasEdificacion.getSistemaMampara());
            Nreg.put(TablesHelper.xml_inspeccion.muebleCocina,   caracteristicasEdificacion.getMuebleCocina());
            Nreg.put(TablesHelper.xml_inspeccion.muebleCocina2,           caracteristicasEdificacion.getMuebleCocina2());
            Nreg.put(TablesHelper.xml_inspeccion.tablero,        caracteristicasEdificacion.getTablero());
            Nreg.put(TablesHelper.xml_inspeccion.lavaderos,           caracteristicasEdificacion.getLavaderos());
            Nreg.put(TablesHelper.xml_inspeccion.sanitarioTipo,   caracteristicasEdificacion.getSanitarioTipo());
            Nreg.put(TablesHelper.xml_inspeccion.sanitarioColor,           caracteristicasEdificacion.getSanitarioColor());
            Nreg.put(TablesHelper.xml_inspeccion.sanitario,        caracteristicasEdificacion.getSanitario());
            Nreg.put(TablesHelper.xml_inspeccion.iss,           caracteristicasEdificacion.getIss());
            Nreg.put(TablesHelper.xml_inspeccion.iiee,   caracteristicasEdificacion.getIiee());
            Nreg.put(TablesHelper.xml_inspeccion.sistemaIncendio,           caracteristicasEdificacion.getSistemaIncendio());
            Nreg.put(TablesHelper.xml_inspeccion.estructura,           caracteristicasEdificacion.getEstructura());
            Nreg.put(TablesHelper.xml_inspeccion.muros,        caracteristicasEdificacion.getMuros());
            Nreg.put(TablesHelper.xml_inspeccion.revestimiento,           caracteristicasEdificacion.getRevestimiento());
            Nreg.put(TablesHelper.xml_inspeccion.pisos,   caracteristicasEdificacion.getPisos());
            Nreg.put(TablesHelper.xml_inspeccion.pisosCocina,           caracteristicasEdificacion.getPisosCocina());
            Nreg.put(TablesHelper.xml_inspeccion.pisosBaños,           caracteristicasEdificacion.getPisosBaños());
            Nreg.put(TablesHelper.xml_inspeccion.paredesBaño,        caracteristicasEdificacion.getParedesBaño());

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void actualizarRegistroInfraestructura(String caracteristicasGenerales, String numInspeccion){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { numInspeccion };
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xml_inspeccion.infraestructura_comentario,  caracteristicasGenerales);

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void actualizarRegistroDespuesInspeccion(DespuesInspeccion despuesInspeccion,
                                                    String numInspeccion,
                                                    List<String> selected64Files){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { numInspeccion };
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            ContentValues Nreg = new ContentValues();

            Gson gson = new Gson();
            String jsonListaBase64 = gson.toJson(selected64Files);

            Nreg.put(TablesHelper.xml_inspeccion.cbCoincideInformacion,  despuesInspeccion.isTiene() ? "SI" : "NO");
            Nreg.put(TablesHelper.xml_inspeccion.cbDocumentacionSITU,  despuesInspeccion.isTiene2() ? "SI" : "NO");
            Nreg.put(TablesHelper.xml_inspeccion.especificar,  despuesInspeccion.getEspecificar());
            Nreg.put(TablesHelper.xml_inspeccion.especificar2,  despuesInspeccion.getEspecificar2());
            Nreg.put(TablesHelper.xml_inspeccion.files,  jsonListaBase64);

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void actualizarRegistroDespuesFirmaInspeccion(InspeccionRequest despuesInspeccion,
                                                         String numInspeccion){
        String where = TablesHelper.xml_inspeccion.numInspeccion + " = ?";
        String[] args = { numInspeccion };
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xml_inspeccion.observacion,  despuesInspeccion.getObservacion());
            Nreg.put(TablesHelper.xml_inspeccion.base64Firma,  despuesInspeccion.getBase64Firma());

            db.update(TablesHelper.xml_inspeccion.table, Nreg, where, args);

            Log.i(TAG, "xml_inspeccion: Registro actualizado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al actualizar registro");
            e.printStackTrace();
        }
    }

    public void crearRegistro(AntesInspeccion antesInspeccion){
        try {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

            ContentValues Nreg = new ContentValues();

            Nreg.put(TablesHelper.xml_inspeccion.numInspeccion,        antesInspeccion.getNumInspeccion());
            Nreg.put(TablesHelper.xml_inspeccion.modalidad,        antesInspeccion.getModalidad());
            Nreg.put(TablesHelper.xml_inspeccion.inscripcion,         antesInspeccion.getInscripcion());
            Nreg.put(TablesHelper.xml_inspeccion.fecha,        antesInspeccion.getFecha());
            Nreg.put(TablesHelper.xml_inspeccion.hora,           antesInspeccion.getHora());
            Nreg.put(TablesHelper.xml_inspeccion.contacto,   antesInspeccion.getContacto());
            Nreg.put(TablesHelper.xml_inspeccion.latitud,           antesInspeccion.getLatitud());
            Nreg.put(TablesHelper.xml_inspeccion.longitud,        antesInspeccion.getLongitud());
            Nreg.put(TablesHelper.xml_inspeccion.direccion,           antesInspeccion.getDireccion());
            Nreg.put(TablesHelper.xml_inspeccion.distrito,   antesInspeccion.getDistrito());
            Nreg.put(TablesHelper.xml_inspeccion.provincia,           antesInspeccion.getProvincia());
            Nreg.put(TablesHelper.xml_inspeccion.departamento,           antesInspeccion.getDepartamento());

            Nreg.put(TablesHelper.xml_inspeccion.estado,       "P");

            db.insert(TablesHelper.xml_inspeccion.table, null, Nreg);
            Log.i(TAG, "xml_inspeccion: Registro insertado");
        } catch (Exception e) {
            Log.e(TAG, "xml_inspeccion: Error al insertar registro");
            e.printStackTrace();
        }
    }
}
