package com.imax.app.models;

import java.io.Serializable;

public class Order implements Serializable {

    public static String DEFAULT_CLIENT_NAME = "PUBLICO GENERAL";
    public static final String FLAG_PENDIENTE = "P";
    public static final String FLAG_ENVIADO = "E";

    private String idNumero;
    private String fecha;
    private String fechaVencimiento;
    private String fechaEntrega;
    private int idCliente;
    private String direcc;
    private int idCond;
    private int idVendedor;
    private int idCobrador;
    private int idTransportista;
    private int idAlmacen;
    private String moneda;
    private double tipoCambio;
    private double subtotal;
    private double descuento;
    private double total;
    private double subTotalTipoCambio;
    private double descuentoTipoCambio;
    private double totalTipoCambio;
    private int idUsuario;
    private String estado;
    private double comi;
    private int idPrecioxzona;
    private String facturado;
    private String factura;
    private String idNumAlt;
    private int idDistrito;
    private String email;
    private String codubigeo;
    private String ordcom;
    private int idMotirech;
    private int idRuta;
    private int idCoordinador;
    private String observacion;
    private String total_opgratuito;
    private String total_opexonerado;
    private String typeDocument;

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getTotal_opgratuito() {
        return total_opgratuito;
    }

    public void setTotal_opgratuito(String total_opgratuito) {
        this.total_opgratuito = total_opgratuito;
    }

    public String getTotal_opexonerado() {
        return total_opexonerado;
    }

    public void setTotal_opexonerado(String total_opexonerado) {
        this.total_opexonerado = total_opexonerado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getIdNumero() {
        return idNumero;
    }

    public void setIdNumero(String idNumero) {
        this.idNumero = idNumero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDirecc() {
        return direcc;
    }

    public void setDirecc(String direcc) {
        this.direcc = direcc;
    }

    public int getIdCond() {
        return idCond;
    }

    public void setIdCond(int idCond) {
        this.idCond = idCond;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdCobrador() {
        return idCobrador;
    }

    public void setIdCobrador(int idCobrador) {
        this.idCobrador = idCobrador;
    }

    public int getIdTransportista() {
        return idTransportista;
    }

    public void setIdTransportista(int idTransportista) {
        this.idTransportista = idTransportista;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public double getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubTotalTipoCambio() {
        return subTotalTipoCambio;
    }

    public void setSubTotalTipoCambio(double subTotalTipoCambio) {
        this.subTotalTipoCambio = subTotalTipoCambio;
    }

    public double getDescuentoTipoCambio() {
        return descuentoTipoCambio;
    }

    public void setDescuentoTipoCambio(double descuentoTipoCambio) {
        this.descuentoTipoCambio = descuentoTipoCambio;
    }

    public double getTotalTipoCambio() {
        return totalTipoCambio;
    }

    public void setTotalTipoCambio(double totalTipoCambio) {
        this.totalTipoCambio = totalTipoCambio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getComi() {
        return comi;
    }

    public void setComi(double comi) {
        this.comi = comi;
    }

    public int getIdPrecioxzona() {
        return idPrecioxzona;
    }

    public void setIdPrecioxzona(int idPrecioxzona) {
        this.idPrecioxzona = idPrecioxzona;
    }

    public String getFacturado() {
        return facturado;
    }

    public void setFacturado(String facturado) {
        this.facturado = facturado;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getIdNumAlt() {
        return idNumAlt;
    }

    public void setIdNumAlt(String idNumAlt) {
        this.idNumAlt = idNumAlt;
    }

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodubigeo() {
        return codubigeo;
    }

    public void setCodubigeo(String codubigeo) {
        this.codubigeo = codubigeo;
    }

    public String getOrdcom() {
        return ordcom;
    }

    public void setOrdcom(String ordcom) {
        this.ordcom = ordcom;
    }

    public int getIdMotirech() {
        return idMotirech;
    }

    public void setIdMotirech(int idMotirech) {
        this.idMotirech = idMotirech;
    }

    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public int getIdCoordinador() {
        return idCoordinador;
    }

    public void setIdCoordinador(int idCoordinador) {
        this.idCoordinador = idCoordinador;
    }
}
