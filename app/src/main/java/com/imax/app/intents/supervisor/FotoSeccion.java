package com.imax.app.intents.supervisor;

import java.util.List;

public class FotoSeccion {
    private String torre;
    private String piso;
    private List<String> fotos; // Lista de rutas de fotos

    public FotoSeccion(String torre, String piso, List<String> fotos) {
        this.torre = torre;
        this.piso = piso;
        this.fotos = fotos;
    }

    // Getters y setters
    public String getTorre() {
        return torre;
    }

    public void setTorre(String torre) {
        this.torre = torre;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
