package com.imax.app.models;

public class UnidadMedidaModel {
    private String idUnidadMedida;
    private String descripcion;

    public UnidadMedidaModel() {
    }

    public UnidadMedidaModel(String idUnidadMedida, String descripcion) {
        this.idUnidadMedida = idUnidadMedida;
        this.descripcion = descripcion;
    }

    public String getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(String idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
