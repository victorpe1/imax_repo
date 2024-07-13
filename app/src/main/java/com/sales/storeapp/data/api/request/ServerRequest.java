package com.sales.storeapp.data.api.request;

public class ServerRequest {
    private String idTipoVenta;
    private int idUsuario;
    private String condicionPago;
    private int idEmpresa;
    private String serie;
    private String source;
    private int idPuntoventa;
    private int numero;

    public String getIdTipoVenta() {
        return idTipoVenta;
    }

    public void setIdTipoVenta(String idTipoVenta) {
        this.idTipoVenta = idTipoVenta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getIdPuntoventa() {
        return idPuntoventa;
    }

    public void setIdPuntoventa(int idPuntoventa) {
        this.idPuntoventa = idPuntoventa;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
