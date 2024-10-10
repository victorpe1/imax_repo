package com.imax.app.intents;

import java.io.Serializable;

public class CaracteristicasGenerales implements Serializable {

    private String tipoInmueble;
    private String otros;
    private boolean cbVivienda;
    private boolean cbComercio;
    private boolean cbIndustria;
    private boolean cbEducativo;
    private boolean cbOther;
    private String comentarios;
    private String recibeInmueble;
    private String nPisos;
    private String distribucion;
    private String referencia;
    private String deposito;
    private String estacionamiento;
    private String depto;

    public CaracteristicasGenerales(String tipoInmueble, String otros, boolean cbVivienda, boolean cbComercio,
                    boolean cbIndustria, boolean cbEducativo, boolean cbOther, String comentarios,
                    String recibeInmueble, String nPisos, String distribucion, String referencia,
                    String deposito, String estacionamiento, String depto) {
        this.tipoInmueble = tipoInmueble;
        this.otros = otros;
        this.cbVivienda = cbVivienda;
        this.cbComercio = cbComercio;
        this.cbIndustria = cbIndustria;
        this.cbEducativo = cbEducativo;
        this.cbOther = cbOther;
        this.comentarios = comentarios;
        this.recibeInmueble = recibeInmueble;
        this.nPisos = nPisos;
        this.distribucion = distribucion;
        this.referencia = referencia;
        this.deposito = deposito;
        this.estacionamiento = estacionamiento;
        this.depto = depto;
    }

    // Getters y setters
    public String getTipoInmueble() { return tipoInmueble; }
    public void setTipoInmueble(String tipoInmueble) { this.tipoInmueble = tipoInmueble; }

    public String getOtros() { return otros; }
    public void setOtros(String otros) { this.otros = otros; }

    public boolean isCbVivienda() { return cbVivienda; }
    public void setCbVivienda(boolean cbVivienda) { this.cbVivienda = cbVivienda; }

    public boolean isCbComercio() { return cbComercio; }
    public void setCbComercio(boolean cbComercio) { this.cbComercio = cbComercio; }

    public boolean isCbIndustria() { return cbIndustria; }
    public void setCbIndustria(boolean cbIndustria) { this.cbIndustria = cbIndustria; }

    public boolean isCbEducativo() { return cbEducativo; }
    public void setCbEducativo(boolean cbEducativo) { this.cbEducativo = cbEducativo; }

    public boolean isCbOther() { return cbOther; }
    public void setCbOther(boolean cbOther) { this.cbOther = cbOther; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public String getRecibeInmueble() { return recibeInmueble; }
    public void setRecibeInmueble(String recibeInmueble) { this.recibeInmueble = recibeInmueble; }

    public String getNPisos() { return nPisos; }
    public void setNPisos(String nPisos) { this.nPisos = nPisos; }

    public String getDistribucion() { return distribucion; }
    public void setDistribucion(String distribucion) { this.distribucion = distribucion; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDeposito() { return deposito; }
    public void setDeposito(String deposito) { this.deposito = deposito; }

    public String getEstacionamiento() { return estacionamiento; }
    public void setEstacionamiento(String estacionamiento) { this.estacionamiento = estacionamiento; }

    public String getDepto() { return depto; }
    public void setDepto(String depto) { this.depto = depto; }
}
