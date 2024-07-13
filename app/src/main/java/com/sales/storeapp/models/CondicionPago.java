package com.sales.storeapp.models;

public class CondicionPago {

    private int idCondicion;
    private String nombre;
    private int dias;
    private String tipo;

    public int getIdCondicion() {
        return idCondicion;
    }

    public void setIdCondicion(int idCondicion) {
        this.idCondicion = idCondicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
