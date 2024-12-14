package com.imax.app.intents.supervisor;

import java.io.Serializable;
import java.util.List;

public class InspeccionSupervisor_5 implements Serializable {

    private List<String> calificacionCalidad;
    private List<String>  radioCheckCalidad;
    private String promedioCalidad;

    public List<String> getCalificacionCalidad() {
        return calificacionCalidad;
    }

    public void setCalificacionCalidad(List<String> calificacionCalidad) {
        this.calificacionCalidad = calificacionCalidad;
    }

    public List<String> getRadioCheckCalidad() {
        return radioCheckCalidad;
    }

    public void setRadioCheckCalidad(List<String> radioCheckCalidad) {
        this.radioCheckCalidad = radioCheckCalidad;
    }

    public String getPromedioCalidad() {
        return promedioCalidad;
    }

    public void setPromedioCalidad(String promedioCalidad) {
        this.promedioCalidad = promedioCalidad;
    }
}
