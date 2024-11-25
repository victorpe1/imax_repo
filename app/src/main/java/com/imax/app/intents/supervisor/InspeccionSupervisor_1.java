package com.imax.app.intents.supervisor;

import java.io.Serializable;

public class InspeccionSupervisor_1 implements Serializable {
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


    public InspeccionSupervisor_1(String numInspeccion, String fecha, String hora, String proyecto, String solicitante,
                                  String responsableObra, String cargo, String sotanos, String pisos, String mesas,
                                  String torres, String proyectoId, String solicitanteId) {
        this.numInspeccion = numInspeccion;
        this.fecha = fecha;
        this.hora = hora;
        this.proyecto = proyecto;
        this.solicitante = solicitante;
        this.responsableObra = responsableObra;
        this.cargo = cargo;
        this.sotanos = sotanos;
        this.pisos = pisos;
        this.mesas = mesas;
        this.torres = torres;
        this.proyectoId = proyectoId;
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
}
