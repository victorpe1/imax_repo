package com.sales.storeapp.models;

import java.io.Serializable;

public class PedidoCabeceraModel implements Serializable {
    public static final String ESTADO_ANULADO = "A";
    public static final String ESTADO_GENERADO = "G";
    public static final String ESTADO_FACTURADO = "F";
    public static final String ESTADO_MODIFICADO = "M";
    public static final String ESTADO_ERROR = "E";

    public static final String FLAG_PENDIENTE = "P";
    public static final String FLAG_ENVIADO = "E";

    public static final String ESTADO_COPIA_ITEM = "Z";

    public static final String ID_TIPO_DOCUMENTO_BOLETA = "03";
    public static final String ID_TIPO_DOCUMENTO_FACTURA = "01";
    public static final String ID_TIPO_DOCUMENTO_TICKET = "12";

    public static final String DEFAULT_CLIENT_DOC = "00000000";
    public static String DEFAULT_CLIENT_NAME = "PUBLICO GENERAL";

    //public String DEFAULT_CLIENT_NAME = "PUBLICO GENERAL";

    private String numeroPedido;
    private String idCliente;
    private int idUsuario;
    private String fechaPedido;
    private String idFormaPago;
    private String observacion;
    private double importeTotal;
    private String estado;
    private String flag;
    private String serieDocumento;
    private String numeroDocumento;
    private String hash;
    private String idTipoDocumento;
    private String idMoneda;
    private String fechaEmision;
    private String horaEmision;

    private String direccion;
    private String razonSocial;
    private String rucDni;
    private String formaPago;
    private String moneda;
    private String tipoDocumentoReceptor;
    private String correoReceptor;
    private int tipoCorreo;
    private String qr_text;
    private String medioPago;
    private String formaPagoGlosa;

    public String getFormaPagoGlosa() {
        return formaPagoGlosa;
    }

    public void setFormaPagoGlosa(String formaPagoGlosa) {
        this.formaPagoGlosa = formaPagoGlosa;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getQr_text() {
        return qr_text;
    }

    public void setQr_text(String qr_text) {
        this.qr_text = qr_text;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(String idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRucDni() {
        return rucDni;
    }

    public void setRucDni(String rucDni) {
        this.rucDni = rucDni;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(String idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(String idMoneda) {
        this.idMoneda = idMoneda;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getHoraEmision() {
        return horaEmision;
    }

    public void setHoraEmision(String horaEmision) {
        this.horaEmision = horaEmision;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoDocumentoReceptor() {
        return tipoDocumentoReceptor;
    }

    public void setTipoDocumentoReceptor(String tipoDocumentoReceptor) {
        this.tipoDocumentoReceptor = tipoDocumentoReceptor;
    }

    public String getCorreoReceptor() {
        return correoReceptor;
    }

    public void setCorreoReceptor(String correoReceptor) {
        this.correoReceptor = correoReceptor;
    }

    public int getTipoCorreo() {
        return tipoCorreo;
    }

    public void setTipoCorreo(int tipoCorreo) {
        this.tipoCorreo = tipoCorreo;
    }
}
