package com.imax.app.intents.supervisor;

public class RegistroTabla {
    private String descripcion;
    private double presupuesto;
    private double avance;

    public RegistroTabla(String descripcion, double presupuesto, double avance) {
        this.descripcion = descripcion;
        this.presupuesto = presupuesto;
        this.avance = avance;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public double getAvance() {
        return avance;
    }

    public void setAvance(double avance) {
        this.avance = avance;
    }
}
