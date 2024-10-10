package com.imax.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.imax.app.ui.login.LoginActivity;
import com.imax.app.utils.Security;
import com.securepreferences.SecurePreferences;

public class App extends Application {
    /*IMPORTANTE para que se pueda cargar vectores en versiones anteriores*/
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public final String TAG = getClass().getName();

    private final String PREF_TOKEN = "pref_token";
    private final String PREF_TOKEN_TYPE = "pref_tokenType";
    private final String PREF_TOKEN_EXPIRES = "pref_tokenExpires";
    private final String PREF_SERIE_USUARIO = "pref_serieUsuario";
    private final String PREF_SESION_OPEN = "pref_sesionOpen";
    private final String PREF_DEVICE_ADDRESS = "pref_deviceAddress";
    private final String PREF_RUBRO_EMP = "pref_rubro_empre";
    private final String PREF_LAST_SYNCRO = "pref_last_syncro";
    private final String PREF_IS_ADMIN = "pref_is_admin";
    private final String PREF_ES_CONTINGENCIA = "pref_es_contingencia";
    private final String PREF_ICONO_PREF = "pref_icono_emp";
    private final String PREF_IGV_COMPANY = "pref_igvCompany";
    private final String PREF_ID_PUNTO_VENTA = "pref_id_punto_venta";
    private final String PREF_ID_USUARIO = "pref_idUsuario";

    private String pref_token;
    private String pref_tokenExpires;
    private String pref_tokenType;
    private String pref_serieUsuario;
    private String pref_deviceAddress;
    private boolean pref_sessionOpen;
    private long pref_lastSyncro;
    private boolean pref_isAdmin;
    private boolean pref_esContingencia;
    private String pref_rubro_empre;
    private String pref_icono_emp;
    private String pref_igvCompany;
    private int pref_idPuntoVenta;
    private int pref_idUsuario;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        // Normal SharedPreferences
        // preferences = getSharedPreferences("preferencesXMS", MODE_PRIVATE);

        // Secure SharedPreferences
        preferences = new SecurePreferences(this, BuildConfig.SECRET_KEY, "preferencesXMS");
        SecurePreferences.setLoggingEnabled(true);

        pref_token              = preferences.getString(PREF_TOKEN, "");
        pref_tokenType          = preferences.getString(PREF_TOKEN_TYPE, "");
        pref_tokenExpires       = preferences.getString(PREF_TOKEN_EXPIRES, "");
        pref_serieUsuario       = preferences.getString(PREF_SERIE_USUARIO, "");
        pref_sessionOpen        = preferences.getBoolean(PREF_SESION_OPEN, false);
        pref_deviceAddress      = preferences.getString(PREF_DEVICE_ADDRESS,"");
        pref_lastSyncro         = preferences.getLong(PREF_LAST_SYNCRO,0);
        pref_isAdmin            = preferences.getBoolean(PREF_IS_ADMIN,false);
        pref_esContingencia     = preferences.getBoolean(PREF_ES_CONTINGENCIA,false);
        pref_rubro_empre        = preferences.getString(PREF_RUBRO_EMP,"");
        pref_icono_emp          = preferences.getString(PREF_ICONO_PREF,"");
        pref_igvCompany         = preferences.getString(PREF_IGV_COMPANY,"0.18"); //IGV DEFAULT
        pref_idUsuario          = preferences.getInt(PREF_ID_USUARIO, 0);

        pref_idPuntoVenta       = preferences.getInt(PREF_ID_PUNTO_VENTA,11);

    }

    //region SAVE DATA
    public void saveData(String KEY, String value){
        editor = preferences.edit();
        editor.putString(KEY, value);
        editor.commit();
    }

    public void saveData(String KEY, int value){
        editor = preferences.edit();
        editor.putInt(KEY, value);
        editor.commit();
    }

    public void saveData(String KEY, long value){
        editor = preferences.edit();
        editor.putLong(KEY, value);
        editor.commit();
    }

    public void saveData(String KEY, boolean value){
        editor = preferences.edit();
        editor.putBoolean(KEY, value);
        editor.commit();
    }
    //endregion

    public void reLogin(Context context) {
        deleteToken();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ((Activity)context).finish();
    }

    public void deleteToken(){
        setPref_sessionOpen(false);
        setPref_tokenType("");
        setPref_token("");
        setPref_tokenExpires("");
    }

    public String getAuthorization() {
        /*if (pref_token.equals(""))
            accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImZjZGE5NGViZDI4MjJhZjEzYjYzZTNjNjA2NDY4MWEzYzU3NmNiODQ5NTVjNTAwMDg1OGNlOWEyNjZhNWZhYWYwZDZkZjhkOGZhYTc4YjcyIn0.eyJhdWQiOiIxIiwianRpIjoiZmNkYTk0ZWJkMjgyMmFmMTNiNjNlM2M2MDY0NjgxYTNjNTc2Y2I4NDk1NWM1MDAwODU4Y2U5YTI2NmE1ZmFhZjBkNmRmOGQ4ZmFhNzhiNzIiLCJpYXQiOjE1MzA2NTU1MDAsIm5iZiI6MTUzMDY1NTUwMCwiZXhwIjoxNTYyMTkxNTAwLCJzdWIiOiI3Nzg1Iiwic2NvcGVzIjpbXX0.XXGDMq4r5iWt8t-vM7JxRtWOS3XyHu6v_SolsQjfXxdfbe4MOdhTNaD8fRTnbPPWmSAUx7HQb1ykMUHJ7eT0pGsCoSsCuSxOF0Dvl2uuUFtIibTq8IcDH8LD_lVav-FJMbq1dsG0D2pbO0X9kJviKGxce47mqe8QMDHgRC3peknu44kccXkcgHT3NKWvkZi-qGMZYXyUKVhWwWz_w-jeut5Mi3sbQr7fhjPln5GEV0k2_q9kzICttuLXWWDp9zWdd87zhYFeCmGYE_BNHG-JV_Hdm9CBuGynBxbOk0wyit0XdVRbyZobpQzzjQSHS_CiErJiTIW-ELJG9oLzIshKXcY7ZGmJdHnTAXMEVJjnXCxvRQILYiBEG3iG6HWEQTO-thWtmqvIFq3ZrXyMERzvVrF26-uOhqy-Dw7Fj4c5azG0RBUVpTlgRPJGn_IbW-TIyAqZRG-WdmsoMxQc196vmoqURfWyzt_eNz8e1VxyVO_xQxuaI6vb8B0wXuzQrnLmFnSWHFyoFrWA9Lm-8nvVLEuA0PV5VW_yl3r0ypj_8nTz4uxvANirtGHax0XwIaz3mbJ-d-d9CldAaP1-2Zdh_XlDci8JHavGVNBMECSlNlTMZ9_ZZpRvPGrJf8BwH96irvXcFl4F3NuXAb1ATlu_xlgrhmwEkMNl-15irwLwXhk";*/
        return getPref_tokenType()+" "+getPref_token();
    }

    public void logOut(Context context){
        setPref_sessionOpen(false);
        //deleteToken();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ((Activity)context).finish();
    }

    public String getPref_token() {
        if (!pref_token.isEmpty())
            return Security.desencriptar(this, pref_token);
        else
            return pref_token;
    }

    public void setPref_token(String pref_token) {
        pref_token = Security.encriptar(this, pref_token);
        this.pref_token = pref_token;
        saveData(PREF_TOKEN, pref_token);
    }

    public String getPref_tokenType() {
        return Security.desencriptar(this, pref_tokenType);
    }

    public void setPref_tokenType(String pref_tokenType) {
        pref_tokenType = Security.encriptar(this, pref_tokenType);
        this.pref_tokenType = pref_tokenType;
        saveData(PREF_TOKEN_TYPE, pref_tokenType);
    }

    public int getPref_idPuntoVenta() {
        return pref_idPuntoVenta;
    }

    public void setPref_idPuntoVenta(int pref_idPuntoVenta) {
        this.pref_idPuntoVenta = pref_idPuntoVenta;
        saveData(PREF_ID_PUNTO_VENTA, pref_idPuntoVenta);
    }

    public void setPref_tokenExpires(String pref_tokenExpires) {
        this.pref_tokenExpires = pref_tokenExpires;
        saveData(PREF_TOKEN_EXPIRES, pref_tokenExpires);
    }

    public boolean isPref_sessionOpen() {
        return pref_sessionOpen;
    }

    public void setPref_sessionOpen(boolean pref_sessionOpen) {
        this.pref_sessionOpen = pref_sessionOpen;
        saveData(PREF_SESION_OPEN, pref_sessionOpen);
    }

    public int getPref_idUsuario() {
        return pref_idUsuario;
    }

    public void setPref_idUsuario(int pref_idUsuario) {
        this.pref_idUsuario = pref_idUsuario;
        saveData(PREF_ID_USUARIO, pref_idUsuario);
    }

    public String getPref_serieUsuario() {
        return pref_serieUsuario;
    }

    public void setPref_serieUsuario(String pref_serieUsuario) {
        this.pref_serieUsuario = pref_serieUsuario;
        saveData(PREF_SERIE_USUARIO, pref_serieUsuario);
    }

    public String getPref_deviceAddress() {
        return pref_deviceAddress;
    }

    public void setPref_deviceAddress(String pref_deviceAddress) {
        this.pref_deviceAddress = pref_deviceAddress;
        saveData(PREF_DEVICE_ADDRESS,pref_deviceAddress);
    }


    public String getPref_rubro_empre() {
        return pref_rubro_empre;
    }

    public void setPref_rubro_empre(String pref_rubro_empresa) {
        this.pref_rubro_empre = pref_rubro_empresa;
        saveData(PREF_RUBRO_EMP, pref_rubro_empresa);
    }

    public String getPref_icono_emp() {
        return pref_icono_emp;
    }

    public void setPref_icono_emp(String preficono) {
        this.pref_icono_emp = preficono;
        saveData(PREF_ICONO_PREF, preficono);
    }

    public String getPref_igvCompany() {
        return pref_igvCompany;
    }

    public void setPref_igvCompany(String pref_igvCompany) {
        this.pref_igvCompany = pref_igvCompany;
        saveData(PREF_IGV_COMPANY, pref_igvCompany);
    }

    public long getPref_lastSyncro() {
        return pref_lastSyncro;
    }

    public void setPref_lastSyncro(long pref_lastSyncro) {
        this.pref_lastSyncro = pref_lastSyncro;
        saveData(PREF_LAST_SYNCRO, pref_lastSyncro);
    }

    public boolean getPref_isAdmin(){
        return pref_isAdmin;
    }

    public void setPref_isAdmin(boolean isAdmin){
        this.pref_isAdmin = isAdmin;
        saveData(PREF_IS_ADMIN, isAdmin);
    }

    public boolean getPref_esContingencia() {
        return pref_esContingencia;
    }

    public void setPref_esContingencia(boolean pref_esContingencia) {
        this.pref_esContingencia = pref_esContingencia;
        saveData(PREF_ES_CONTINGENCIA, pref_esContingencia);
    }
}
