package com.imax.app.intents.supervisor;

import java.io.Serializable;
import java.util.List;

public class InspeccionSupervisor_4 implements Serializable {

    private String moneda;
    private String tipoMoneda;
    private String descripcion;
    private double totalesAvance;
    private double presupuesto;
    private List<RegistroTabla> detalles; // La lista de detalles (JSON)

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

    public List<RegistroTabla> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<RegistroTabla> detalles) {
        this.detalles = detalles;
    }
}



