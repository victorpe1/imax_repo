package com.imax.app.data.api.request;

import com.imax.app.intents.supervisor.RegistroTabla;

import java.io.Serializable;
import java.util.List;

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

    //Inpeccion 3
    private String resumenNiveles;

    //Inpeccion 4
    private String moneda;
    private String tipoMoneda;
    private String descripcion;
    private double totalesAvance;
    private double presupuesto;
    private String detalles;

    //Inspeccion final
    private String fotosAdicional;

    //Inspeccion Calidad
    private String calificacionCalidad;
    private String radioCheckCalidad;
    private String promedioCalidad;
    private String observacionCalidad;

    //Inspeccion Seguridad
    private String calificacionSeguridad;
    private String radioCheckSeguridad;
    private String promedioSeguridad;
    private String observacionSeguridad;


    public String getObservacionCalidad() {
        return observacionCalidad;
    }

    public void setObservacionCalidad(String observacionCalidad) {
        this.observacionCalidad = observacionCalidad;
    }

    public String getObservacionSeguridad() {
        return observacionSeguridad;
    }

    public void setObservacionSeguridad(String observacionSeguridad) {
        this.observacionSeguridad = observacionSeguridad;
    }

    public String getCalificacionSeguridad() {
        return calificacionSeguridad;
    }

    public void setCalificacionSeguridad(String calificacionSeguridad) {
        this.calificacionSeguridad = calificacionSeguridad;
    }

    public String getRadioCheckSeguridad() {
        return radioCheckSeguridad;
    }

    public void setRadioCheckSeguridad(String radioCheckSeguridad) {
        this.radioCheckSeguridad = radioCheckSeguridad;
    }

    public String getPromedioSeguridad() {
        return promedioSeguridad;
    }

    public void setPromedioSeguridad(String promedioSeguridad) {
        this.promedioSeguridad = promedioSeguridad;
    }

    public String getCalificacionCalidad() {
        return calificacionCalidad;
    }

    public void setCalificacionCalidad(String calificacionCalidad) {
        this.calificacionCalidad = calificacionCalidad;
    }

    public String getRadioCheckCalidad() {
        return radioCheckCalidad;
    }

    public void setRadioCheckCalidad(String radioCheckCalidad) {
        this.radioCheckCalidad = radioCheckCalidad;
    }

    public String getPromedioCalidad() {
        return promedioCalidad;
    }

    public void setPromedioCalidad(String promedioCalidad) {
        this.promedioCalidad = promedioCalidad;
    }

    public String getFotosAdicional() {
        return fotosAdicional;
    }
    public void setFotosAdicional(String fotosAdicional) {
        this.fotosAdicional = fotosAdicional;
    }

    public String getResumenNiveles() {
        return resumenNiveles;
    }

    public void setResumenNiveles(String resumenNiveles) {
        this.resumenNiveles = resumenNiveles;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getTotalesAvance() {
        return totalesAvance;
    }

    public void setTotalesAvance(double totalesAvance) {
        this.totalesAvance = totalesAvance;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

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
