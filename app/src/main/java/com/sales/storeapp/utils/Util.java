package com.sales.storeapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sales.storeapp.R;
import com.sales.storeapp.data.api.request.OrderItemRequest;
import com.sales.storeapp.data.api.request.OrderRequest;
import com.sales.storeapp.data.api.request.ServerRequest;
import com.sales.storeapp.data.api.request.VentaItemRequest;
import com.sales.storeapp.data.api.request.VentaRequest;
import com.sales.storeapp.data.dao.DAOProducto;
import com.sales.storeapp.managers.DataBaseHelper;

import java.io.File;
import java.math.BigDecimal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Util {
    public static final String TAG = "Util";
    public static int DECIMALES_REDONDEO = 2;
    public static int LONG_MINUTO = 1000 * 60;
    public static int LONG_HORA = LONG_MINUTO * 60;
    public static int LONG_DIA = LONG_HORA * 24;

    public final static String  caracteresEspeciales        = "ÜüÁáÉéÍíÓóÚúÑñ°-─";
    public final static byte[]  codigoCaracteresEspeciales  = new byte[] {
            (byte) 0x9A, (byte) 0x81, (byte) 0xB5, (byte) 0xA0, (byte) 0x90, (byte) 0x82,
            (byte) 0xD6, (byte) 0xA1, (byte) 0xE0, (byte) 0xA2, (byte) 0xE9,
            (byte) 0xA3, (byte) 0xA5, (byte) 0xA4, (byte) 0xF8, (byte) 0x2D, (byte) 0xC4};

    public static void actualizarToolBar(String title, boolean upButton, Activity activity){
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }


    public static byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static OrderRequest generarCamposFacturacion(OrderRequest ventaRequest, double tCambio){
        double subTotal = 0.00d;

        for (OrderItemRequest item : ventaRequest.getOrderDetalle()){
            item.setTipoDeCambio(tCambio);
            subTotal += item.getMonto();
        }
        ventaRequest.setSubtotal(subTotal);
        ventaRequest.setTotal(subTotal);

        ventaRequest.setEstado("C");
        ventaRequest.setTipoDeCambio(tCambio);

        return ventaRequest;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }


    public static void actualizarToolBar(String title, boolean upButton, Activity activity, @DrawableRes int drawable){
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    public static void actualizarToolBar(String title, String subtitle, boolean upButton, Activity activity){
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) activity).getSupportActionBar().setSubtitle(subtitle);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

    public static void actualizarToolBar(String title, String subtitle, boolean upButton, Activity activity, @DrawableRes int drawable){
        ((AppCompatActivity) activity).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) activity).getSupportActionBar().setSubtitle(subtitle);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    public static DecimalFormat formateador(){
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        simbolos.setGroupingSeparator(',');

        DecimalFormat formateador = new DecimalFormat("###,##0.00", simbolos);

        return formateador;
    }

    public static String validarString(String direccion)
    {
        String cadena = "";

        for (int i=0; i < direccion.length(); i++){
            char aux = direccion.charAt(i);

            if (aux == '–') {
                cadena += "-";
            }else{
                cadena += aux;
            }

        }

        return cadena;
    }

    public static byte[] stringABytes(String s)
    {
        int i, l, i_especial;
        byte b;
        byte[] b_arr;
        String s_sub;

        if(s == null)
            return null;
        if((l= s.length()) < 1)
            return new byte[0];

        // convertimos a byte carácter por carácter
        b_arr = new byte[l];
        for(i= 0; i < l; i++)
        {
            s_sub = s.substring(i, i + 1);
            i_especial = Util.caracteresEspeciales.indexOf(s_sub);
            if(i_especial < 0)
                b = (s_sub.getBytes())[0];
            else {
                b = Util.codigoCaracteresEspeciales[i_especial];
                //b = "0" + b;
            }
            b_arr[i]= b;
        }

        return b_arr;
    }

    @NonNull
    public static Double redondearDouble(double val){
        String r = val+"";
        BigDecimal big = new BigDecimal(r);
        big = big.setScale(DECIMALES_REDONDEO, RoundingMode.HALF_UP);
        return big.doubleValue();
    }
    @NonNull
    public static Double redondearDouble(double val, int decimalesRedondeo){
        String r = val+"";
        BigDecimal big = new BigDecimal(r);
        big = big.setScale(decimalesRedondeo, RoundingMode.HALF_UP);
        return big.doubleValue();
    }

    /**
     * Verifica si está conectado a alguna RED, sólo si está conectado a alguna retorna true, falso de lo contrario
     * @param context
     * @return
     */
    public static boolean isConnectingToRed(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i("isConnectingToRed","comprobando...");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                Log.i("isConnectingToRed",info.isConnectedOrConnecting()+"");
                return info.isConnectedOrConnecting();
            }
        }
        return false;
    }

    /**
     * @return true si hay conexión a internet y false de lo contrario. Es verificado haciendo ping a Google
     */
    public static Boolean isConnectingToInternet() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(3000);
            urlc.connect();
            Log.i(TAG,"isConnectingToInternet"+(urlc.getResponseCode() == 200));
            return (urlc.getResponseCode() == 200);
        } catch (Exception e) {
            Log.e(TAG, "Error checking internet connection", e);
        }
        return false;
        /*
        try {
            String comando = "ping -c 1 www.google.com";
            return (Runtime.getRuntime().exec (comando).waitFor() == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
        */
    }

    /**
     * @return Retorna la fecha actual en formato "dd/MM/yyyy"
     */
    public static String getFechaTelefonoString(){
        /*String fechaActual = "";
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (day<10){
            fechaActual += "0" + day;
        }else{
            fechaActual += "" + day;
        }

        if (month<10){
            fechaActual += "/0" + month;
        }else {
            fechaActual += "/" + month;
        }
        fechaActual += "/"+year;

        return fechaActual;*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    public static String getFechaHistoricString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3650);
        Date date_year_2year = c.getTime();

        return dateFormat.format(date_year_2year);
    }

    /**
     * @return Retorna la fecha actual en formato "01/01/2017 00:00:00"
     */
    public static String getFechaHoraTelefonoString(){
        String fechaActual = "";
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if (day<10){
            fechaActual += "0" + day;
        }else{
            fechaActual += "" + day;
        }

        if (month<10){
            fechaActual += "/0" + month;
        }else {
            fechaActual += "/" + month;
        }

        fechaActual += "/"+year;

        if (hour<10){
            fechaActual += " 0"+hour;
        }else{
            fechaActual += " "+hour;
        }

        if (minute<10){
            fechaActual += ":" + "0"+minute;
        }else{
            fechaActual += ":" + minute;
        }

        if (second<10){
            fechaActual += ":" + "0"+second;
        }else{
            fechaActual += ":" + second;
        }

        return fechaActual;

    }

    /**
     * @return Retorna la fecha actual en formato "yyyy-MM-dd HH:mm:ss (24 Horas)"
     */
    public static String getFechaHoraTelefonoString_formatoSql(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * @return Retorna la fecha actual en formato "yyyy-MM-dd"
     */
    public static String getFechaTelefonoString_formatoSql(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    /**
     * @return Retorna la hora actual en formato "HH:mm:ss (24 Horas)"
     */
    public static String getHoraTelefonoString_formatoSql(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * @param fecha en formato "dd/MM/yyyy"
     * @return fecha en formato "yyyy-MM-dd"
     */
    public static String parseDateFormat(String fecha){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String parsedDate = "";
        try {
            Date date = dateFormat.parse(fecha);
            SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            parsedDate = dateFormatSql.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate;
    }

    /**
     * @param dateString Fecha con el formato "dd/MM/yyyy"
     * @return Retorna la fecha en un objeto Calendar
     */
    public static Calendar convertirStringFecha_aCalendar(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime((Date) dateFormat.parse(dateString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * @param dateString Fecha con el formato "yyyy-MM-dd HH:mm:ss"
     *                   <p>(<b>Importante</b> el formato de hora "HH:mm:ss" es para 24horas, es decir que la 1 de la tarde sería asi "13:00:00" )</p>
     * @return Retorna la fecha en un objeto Calendar
     */
    public static Calendar convertirStringFechaHora_aCalendar2(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Si se quiere trabajar con un formato de 12horas (am-pm) se cambia el "HH" por "hh"
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime((Date) dateFormat.parse(dateString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * @param dateString Fecha con el formato "dd/MM/yyyy HH:mm:ss"
     *                   <p>(<b>Importante</b> el formato de hora "HH:mm:ss" es para 24horas, es decir que la 1 de la tarde sería asi "13:00:00" )</p>
     * @return Retorna la fecha en un objeto Calendar
     */
    public static Calendar convertirStringFechaHora_aCalendar(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //Si se quiere trabajar con un formato de 12horas (am-pm) se cambia el "HH" por "hh"
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime((Date) dateFormat.parse(dateString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar;
    }

    public static void abrirTeclado(Context context, View view){
        view.requestFocus(); //Asegurar que editText tiene focus
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void cerrarTeclado(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * @param fechaString Fecha con el siguiente formato (dia/mes/año). Ejemplo: "01/01/2017"
     * @return Retorna la fecha con el siguiente formato ejemplo: "Domingo 01 de Enero del 2017"
     */
    public static String getFechaExtendida(String fechaString){
        Calendar calendar = Util.convertirStringFecha_aCalendar(fechaString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());

        //Day of Name in full form like,"Saturday", or if you need the first three characters you have to put "EEE" in the date format and your result will be "Sat".
        SimpleDateFormat formatoDia = new SimpleDateFormat("EEEE");
        String dayName = capitalize(formatoDia.format(calendar.getTime()));
        return "" + dayName + " " + currentDate + "";
    }

    /**
     * @return Retorna la fecha actual con el siguiente formato ejemplo: "Domingo 01 de Enero del 2017"
     */
    public static String getFechaExtendida(){
        //Tomamos la hora actual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy");
        String currentDate = sdf.format(calendar.getTime());

        //Day of Name in full form like,"Saturday", or if you need the first three characters you have to put "EEE" in the date format and your result will be "Sat".
        SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        String dayName = sdf_.format(calendar.getTime());
        return "" + dayName + " " + currentDate + "";
    }

    /**
     * @param texto Texto a darle letra capital
     * @return retorna el texto con la letra capital (Primera letra en mayúscula)
     */
    public static String capitalize(@NonNull String texto) {
        if(texto.length() == 1){ return texto.toUpperCase(); }
        if(texto.length() > 1){ return texto.substring(0,1).toUpperCase() + texto.substring(1); }
        return "";
    }

    /**
     * @param numeroPedidoMaximo El maximo numero de pedido actual
     * @param fechaActual Fecha actual en el formato "01/01/2017"
     * @param serieUsuario Serie del usuario
     * @return Retorna la secuencia o correlativo del nuevo pedido
     */
    public static String calcularSecuencia(String numeroPedidoMaximo, String fechaActual, String serieUsuario) {
        String[] arrayFecha = fechaActual.split("/");
        String currentDay = arrayFecha[0];
        String currentMonth = arrayFecha[1];
        String currentYear = arrayFecha[2].substring(2,4);
        String currentDate = currentYear+currentMonth+currentDay;

        int secondsActual = new Date().getSeconds();

        int secuencia = 1;
        String secueciaCalculada = "";

        if (numeroPedidoMaximo.isEmpty()){
            secueciaCalculada = currentDate+serieUsuario+secondsActual+secuencia;
            Log.e(TAG,"return secueciaCalculada numeroPedido empty:" +secueciaCalculada);
            return secueciaCalculada;
        }

        /*--------------------------------------------------------------------*/
        //Se valida que por lo menos tenga 9 digitos (dia mes y año y correlativo)
        if (numeroPedidoMaximo.length() >= 9){
            String cadenaFecha = numeroPedidoMaximo.substring(0,6);
            int year = Integer.parseInt(cadenaFecha.substring(0,2));
            int month = Integer.parseInt(cadenaFecha.substring(2,4));
            int day = Integer.parseInt(cadenaFecha.substring(4,6));
            int sec = Integer.parseInt(numeroPedidoMaximo.substring(numeroPedidoMaximo.length()-3,numeroPedidoMaximo.length()));
            //Verificar cuando sea año nuevo
            if (Integer.parseInt(currentMonth) <= month){
                if (Integer.parseInt(currentDay) > day){
                    secuencia = 1;
                }else{
                    secuencia = sec + 1;
                }
            }else{
                secuencia = 1;
            }

            if (secuencia < 10){
                secueciaCalculada = currentDate+serieUsuario + secondsActual +secuencia;
            }else if(secuencia < 100){
                secueciaCalculada = currentDate+serieUsuario + "0"+secuencia;
            }else{
                secueciaCalculada = currentDate+serieUsuario + secuencia;
            }

            Log.e(TAG,"return secueciaCalculada numeroPedido >=9:" +secueciaCalculada);
            return secueciaCalculada;

        }else {
            secueciaCalculada = currentDate+serieUsuario+secondsActual+secuencia;
            Log.e(TAG,"return secueciaCalculada numeroPedido <9:" +secueciaCalculada);
            return secueciaCalculada;
        }
    }


    public static String calcularSecuenciaHistorico(String numeroPedidoMaximo, String fechaActual, String serieUsuario, int secuencia) {
        String[] arrayFecha = fechaActual.split("/");
        String currentDay = arrayFecha[0];
        String currentMonth = arrayFecha[1];
        String currentYear = arrayFecha[2].substring(2,4);
        String currentDate = currentYear+currentMonth+currentDay;

        String secueciaCalculada = "";

        if (numeroPedidoMaximo.isEmpty()){
            secueciaCalculada = currentDate+serieUsuario+"00"+secuencia;
            Log.e(TAG,"return secueciaCalculada numeroPedido empty:" +secueciaCalculada);
            return secueciaCalculada;
        }

        /*--------------------------------------------------------------------*/
        //Se valida que por lo menos tenga 9 digitos (dia mes y año y correlativo)
        if (numeroPedidoMaximo.length() >= 9){
            String cadenaFecha = numeroPedidoMaximo.substring(0,6);
            int year = Integer.parseInt(cadenaFecha.substring(0,2));
            int month = Integer.parseInt(cadenaFecha.substring(2,4));
            int day = Integer.parseInt(cadenaFecha.substring(4,6));
            int sec = Integer.parseInt(numeroPedidoMaximo.substring(numeroPedidoMaximo.length()-3,numeroPedidoMaximo.length()));
            //Verificar cuando sea año nuevo
            if (Integer.parseInt(currentMonth) <= month){
                if (Integer.parseInt(currentDay) > day){
                    secuencia = 1;
                }else{
                    secuencia = sec + 1;
                }
            }else{
                secuencia = 1;
            }

            if (secuencia < 10){
                secueciaCalculada = currentDate+serieUsuario + "00"+secuencia;
            }else if(secuencia < 100){
                secueciaCalculada = currentDate+serieUsuario + "0"+secuencia;
            }else{
                secueciaCalculada = currentDate+serieUsuario + secuencia;
            }

            Log.e(TAG,"return secueciaCalculada numeroPedido >=9:" +secueciaCalculada);
            return secueciaCalculada;

        }else {
            secueciaCalculada = currentDate+serieUsuario+"00"+secuencia;
            Log.e(TAG,"return secueciaCalculada numeroPedido <9:" +secueciaCalculada);
            return secueciaCalculada;
        }
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String alinearTextoDerecha(String texto, int limiteCaracteres){
        if (texto.length() > limiteCaracteres)
            return texto.substring(0,limiteCaracteres);

        String textoAlineado = "";
        int cantidadVacio = limiteCaracteres - texto.length();
        for (int i=0; i < cantidadVacio; i++){
            textoAlineado += " ";
        }
        textoAlineado += texto;
        return textoAlineado;
    }

    public static String alinearTextoIzquierda(String texto, int limiteCaracteres){
        if (texto.length() > limiteCaracteres) {
            int resta = texto.length() - limiteCaracteres;
            String comienzo_string = texto.substring(0, limiteCaracteres);
            String final_string = texto.substring(limiteCaracteres);

            return comienzo_string + "\n" + final_string;
        }
        //"xdd" - 2
        String textoAlineado = texto;
        int cantidadVacio = limiteCaracteres - texto.length();
        for (int i=0; i < cantidadVacio; i++){
            textoAlineado += " ";
        }
        return textoAlineado;
    }



    public static String alinearDecimalDerecha(Double decimal, int limiteCaracteres, int numeroDecimales){
        if (numeroDecimales > 2 || numeroDecimales < 0) numeroDecimales = 2;
        DecimalFormat formateador = formateador();
        String texto = formateador.format(decimal);
        texto = texto.substring(0,texto.length()-(2-numeroDecimales));
        if (texto.length() > limiteCaracteres)
            return texto;

        String textoAlineado = "";
        int cantidadVacio = limiteCaracteres - texto.length();
        for (int i=0; i < cantidadVacio; i++){
            textoAlineado += " ";
        }
        textoAlineado += texto;
        return textoAlineado;
    }

    public static boolean backupdDatabase(Context context){
        try {
            File sd = Environment.getExternalStorageDirectory();

            File data = Environment.getDataDirectory();
            String packageName  = context.getPackageName();
            String sourceDBName = DataBaseHelper.DATABASE_NAME;
            if (sd.canWrite()) {

                String currentDBPath = "data/" + packageName + "/databases/" + sourceDBName;
                File currentDB = new File(data, currentDBPath);

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
                String targetDBName = DataBaseHelper.DATABASE_NAME+"BK_" + timeStamp + ".db";

                File storageDir = Environment.getExternalStoragePublicDirectory(context.getResources().getString(R.string.Xalesmap_store_backups));
                if (!storageDir.exists())
                    storageDir.mkdirs();
                File backupDB = new File(storageDir,targetDBName);//Para crear el archivo permanente (crea el archivo con el nombre tal cual, sin agregar nada al final)

                Log.i(TAG,"backupDB=" + backupDB.getAbsolutePath());
                Log.i(TAG,"sourceDB=" + currentDB.getAbsolutePath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();

                dst.transferFrom(src, 0, src.size());

                src.close();
                dst.close();
                return true;
            }else{
                Log.w(TAG,"backupdDatabase No se puede escribir");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getNivelBateria(Context context){
        try{
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent bateryStatus = context.registerReceiver(null, ifilter);

            int level = bateryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = bateryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            return (100 * level) / scale;
        }catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public static void showDialogAlert(Activity activity, @DrawableRes int icon, String titulo, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View pop = activity.getLayoutInflater().inflate(R.layout.dialog_general,null);
        builder.setView(pop);

        ImageView img_icon = pop.findViewById(R.id.img_icon);
        TextView tv_titulo = pop.findViewById(R.id.tv_titulo);
        TextView tv_mensaje = pop.findViewById(R.id.tv_mensaje);

        img_icon.setImageResource(icon);
        tv_titulo.setText(titulo);
        tv_mensaje.setText(mensaje);

        builder.setPositiveButton(activity.getString(R.string.aceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public static boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static int getEstadoBateria(Context context){
        try{
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent bateryStatus = context.registerReceiver(null, ifilter);

            int level = bateryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = bateryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int porcentaje = (100 * level) / scale;
            return porcentaje;
        }catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * @param time1 time in millis
     * @param time2 time in millis
     * @param typeDifference {@link com.sales.storeapp.utils.Util#LONG_MINUTO}, {@link com.sales.storeapp.utils.Util#LONG_HORA}, {@link com.sales.storeapp.utils.Util#LONG_DIA}
     * @return
     */
    public static long getTimeDifference(long time1, long time2, long typeDifference) {
        long longDifference = Math.abs(time1 - time2); //result in millis
        long typedDifference = longDifference / typeDifference;
        return typedDifference;
    }

   /* public static OrderRequest generarCamposFacturacion(OrderRequest ventaRequest, double igv, DAOProducto daoProducto){
        String totalOPExoneradas = "0.00";
        String totalOPNoGravadas = "0.00";
        double totalOPGratuitas = 0.00d;
        String descuentosGlobales = "0.00";
        String totalAnticipos = "0.00";
        String sumatoriaISC = "0.00";
        double sumatoriaIGV = 0.00d;
        double totalDescuentos = 0.00d;
        double totalOPGravadas = 0.00d;
        double totalGravadasConIGV = 0.00d;

        for (OrderItemRequest item : ventaRequest.getOrderDetalle()){
            double precioUnitarioConIgv = Double.parseDouble(item.getPrecioUnitarioConIgv());

            double valorUnitarioSinIgv;
            Double igvProducto = daoProducto.getIGVProducto(item.getIdProducto());
            if(igvProducto != 0.0 && igvProducto != null){
                 valorUnitarioSinIgv = precioUnitarioConIgv / (1 + igvProducto);
            }else{
                 valorUnitarioSinIgv = precioUnitarioConIgv / (1 + igv);
            }

            BigDecimal bd = new BigDecimal(valorUnitarioSinIgv).setScale(4, RoundingMode.HALF_UP);
            valorUnitarioSinIgv = bd.doubleValue();

            int codigoAfectacionIGVItem = 10;

            double importeIGVItem = (precioUnitarioConIgv - valorUnitarioSinIgv) * item.getCantidadItem();
            BigDecimal bd2 = new BigDecimal(importeIGVItem).setScale(2, RoundingMode.HALF_UP);
            importeIGVItem = bd2.doubleValue();

            double descuentoItem = 0.00d;
            double valorVentaItem = (valorUnitarioSinIgv * item.getCantidadItem()) - descuentoItem;
            BigDecimal bd3 = new BigDecimal(valorVentaItem).setScale(2, RoundingMode.HALF_UP);
            valorVentaItem = bd3.doubleValue();

            item.setValorUnitarioSinIgv(String.valueOf(valorUnitarioSinIgv));
            item.setCodigoAfectacionIGVItem(codigoAfectacionIGVItem);
            item.setImporteIGVItem(String.valueOf(importeIGVItem));
            item.setDescuentoItem(String.valueOf(descuentoItem));
            item.setValorVentaItem(String.valueOf(valorVentaItem));
            item.setCodTipoPrecioVtaUnitarioItem("01");

            sumatoriaIGV += importeIGVItem;
            totalDescuentos += descuentoItem;
            totalOPGravadas += valorVentaItem;
            totalGravadasConIGV += Double.parseDouble(item.getMontoTotalItem());
        }
        ventaRequest.setTotalOPExoneradas(totalOPExoneradas);
        ventaRequest.setTotalOPNoGravadas(totalOPNoGravadas);
        ventaRequest.setTotalOPGratuitas(String.valueOf(totalOPGratuitas));
        ventaRequest.setDescuentosGlobales(descuentosGlobales);
        ventaRequest.setTotalAnticipos(totalAnticipos);
        ventaRequest.setSumatoriaISC(sumatoriaISC);
        ventaRequest.setSumatoriaIGV(String.valueOf(Util.redondearDouble(sumatoriaIGV)));
        ventaRequest.setTotalDescuentos(String.valueOf(totalDescuentos));
        ventaRequest.setTotalOPGravadas(String.valueOf(Util.redondearDouble(totalOPGravadas)));
        ventaRequest.setTotalGravadasConIGV(Util.redondearDouble(totalGravadasConIGV));

        ventaRequest.setEsFicticio(false);
        ventaRequest.setKeepNumber("false");

        return ventaRequest;
    }*/
}
