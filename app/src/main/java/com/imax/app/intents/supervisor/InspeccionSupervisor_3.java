package com.imax.app.intents.supervisor;

import java.io.Serializable;

public class InspeccionSupervisor_3 implements Serializable {

    private String torre;
    private String de;
    private String a;
    private String descripcion;

    public InspeccionSupervisor_3(String torre, String de, String a, String descripcion) {
        this.torre = torre;
        this.de = de;
        this.a = a;
        this.descripcion = descripcion;
    }

    public String getTorre() {
        return torre;
    }

    public void setTorre(String torre) {
        this.torre = torre;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
