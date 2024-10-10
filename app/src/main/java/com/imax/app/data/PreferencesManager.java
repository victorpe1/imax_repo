package com.imax.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class PreferencesManager {
    //region
    public final String SHARED_PREFERENCES_FILE = "preferencesXMS";
    private final String PREF_SERVICIO = "pref_servicio";
    private final String PREF_SERVICIO_FACTURACION = "pref_servicioFacturacion";
    private final String PREF_TOKEN = "pref_token";
    private final String PREF_TOKEN_TYPE = "pref_tokenType";
    private final String PREF_TOKEN_EXPIRES = "pref_tokenExpires";
    private final String PREF_ID_USUARIO = "pref_idUsuario";
    private final String PREF_ID_BODEGA_CLIENTE = "pref_idBodegaCliente";
    private final String PREF_ID_CLIENTE_MAGIC = "pref_idClienteMagic";
    private final String PREF_SERIE_USUARIO = "pref_serieUsuario";
    private final String PREF_SESION_OPEN = "pref_sesionOpen";
    private final String PREF_DEVICE_ADDRESS = "pref_deviceAddress";
    private final String PREF_LAST_SYNCRO = "pref_last_syncro";
    private final String PREF_ID_PUNTO_VENTA = "pref_id_punto_venta";
    private final String PREF_SERIE_FACTURA = "pref_serie_factura";
    private final String PREF_SERIE_BOLETA = "pref_serie_boleta";
    private final String PREF_IS_ADMIN = "pref_is_admin";
    private final String PREF_ES_CONTINGENCIA = "pref_es_contingencia";

    /* Lista de Preferencias que podrán ser cambiadas desde Configuración */
    final String SETTINGS_VALIDAR_STOCK = "settings_validarStock";
    final String SETTINGS_STOCK_EN_LINEA = "settings_stockEnLinea";
    final String SETTINGS_PRODUCTOS_SIN_PRECIO = "settings_productoSinPrecio";
    final String SETTINGS_PREVENTA_EN_LINEA = "settings_preventaEnLinea";
    /* ---------------------------------------------------------------------*/
    //endregion
    private static PreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    private PreferencesManager(Context context) {
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public <T> void putData(String key, T value) {
        if (value instanceof String) {
            sharedPreferences.edit().putString(key, (String) value).apply();
        } else if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).apply();
        } else if (value instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) value).apply();
        } else if (value instanceof Float) {
            sharedPreferences.edit().putFloat(key, (Float) value).apply();
        } else if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
        } else {
            sharedPreferences.edit().putString(key, String.valueOf(value)).apply();
        }
    }

    @SuppressWarnings(value = "unchecked")
    public <T> T getData(String key, T defValue) {
        if (defValue instanceof String) {
            return (T) sharedPreferences.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return (T) ((Integer) sharedPreferences.getInt(key, (Integer) defValue));
        } else if (defValue instanceof Long) {
            return (T) ((Long) sharedPreferences.getLong(key, (Long) defValue));
        } else if (defValue instanceof Float) {
            return (T) ((Float) sharedPreferences.getFloat(key, (Float) defValue));
        } else if (defValue instanceof Boolean) {
            return (T) ((Boolean) sharedPreferences.getBoolean(key, (Boolean) defValue));
        } else {
            return (T) sharedPreferences.getString(key, (String) defValue);
        }
    }

    public void setObject(String key, Object object) {
        String serializedValue = "";
        if (object != null) {
            serializedValue = new Gson().toJson(object);
        }
        sharedPreferences.edit().putString(key, serializedValue).apply();
    }

    public <T> T getObject(String key, Class<T> classReference) {
        String json = sharedPreferences.getString(key, "");
        if (!json.isEmpty()) {
            try {
                return new Gson().fromJson(json, classReference);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void clearData() {
        sharedPreferences.edit().clear().apply();
    }
}
