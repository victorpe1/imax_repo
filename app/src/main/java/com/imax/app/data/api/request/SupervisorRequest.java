package com.imax.app.data.api.request;

import java.io.Serializable;

public class SupervisorRequest implements Serializable {
    // Ispección 1
    private String numInspeccion;
    private String fecha;
    private String hora;
    private String proyecto;
    private String solicitante;
    private String responsableObra;
    private String cargo;
    private String sotanos;
    private String pisos;
    private String mesas;
    private String torres;
    private String proyectoId;
    private String solicitanteId;

    // Ispección 2
    private String listadoDocumentos;


    public String getListadoDocumentos() {
        return listadoDocumentos;
    }

    public void setListadoDocumentos(String listadoDocumentos) {
        this.listadoDocumentos = listadoDocumentos;
    }

    public SupervisorRequest() {
        this.numInspeccion = "";
    }

    public String getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(String proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(String solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public String getNumInspeccion() {
        return numInspeccion;
    }

    public void setNumInspeccion(String numInspeccion) {
        this.numInspeccion = numInspeccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getResponsableObra() {
        return responsableObra;
    }

    public void setResponsableObra(String responsableObra) {
        this.responsableObra = responsableObra;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSotanos() {
        return sotanos;
    }

    public void setSotanos(String sotanos) {
        this.sotanos = sotanos;
    }

    public String getPisos() {
        return pisos;
    }

    public void setPisos(String pisos) {
        this.pisos = pisos;
    }

    public String getMesas() {
        return mesas;
    }

    public void setMesas(String mesas) {
        this.mesas = mesas;
    }

    public String getTorres() {
        return torres;
    }

    public void setTorres(String torres) {
        this.torres = torres;
    }
}
