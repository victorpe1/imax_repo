package com.imax.app.intents;

import java.io.Serializable;

public class AntesInspeccion implements Serializable {
    private String numInspeccion;
    private String modalidad;
    private String inscripcion;
    private String fecha;
    private String hora;
    private String contacto;
    private String latitud;
    private String longitud;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;

    public AntesInspeccion(String numInspeccion, String modalidad, String inscripcion, String fecha, String hora, String contacto, String latitud, String longitud, String direccion, String distrito, String provincia, String departamento) {
        this.numInspeccion = numInspeccion;
        this.modalidad = modalidad;
        this.inscripcion = inscripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.contacto = contacto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.distrito = distrito;
        this.provincia = provincia;
        this.departamento = departamento;
    }
    public String getNumInspeccion() {
        return numInspeccion;
    }

    public void setNumInspeccion(String numInspeccion) {
        this.numInspeccion = numInspeccion;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(String inscripcion) {
        this.inscripcion = inscripcion;
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
