package com.sales.storeapp.data.api.request;

import java.util.List;

public class OrderRequest {
    private String idNumero;                // generas
    private String fecha;            // generas
    private String fechaDeVenc;      // front
    private String fechaDeEntrega;   // front
    private int idCliente;                  // front
    private String direcc;                  // front - id
    private int idCond;                     // front - id
    private int idVendedor;                 // front - id
    private int idCobrador;                 // front - preguntar
    private int idAlmacen;                  // front - id
    private String moneda;                  // front - id - soles
    private double tipoDeCambio;        // front - id
    private double subtotal;            // front
    private double descuento;           // 0.00
    private double total;               // front
    private int idUsuario;                  // front
    private String estado;                  // generas
    private int idDistrito;                 // front
    private String codUbigeo;               // preguntar
    private List<OrderItemRequest> orderDetalle; // front


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

    public String getFechaDeVenc() {
        return fechaDeVenc;
    }

    public void setFechaDeVenc(String fechaDeVenc) {
        this.fechaDeVenc = fechaDeVenc;
    }

    public String getFechaDeEntrega() {
        return fechaDeEntrega;
    }

    public void setFechaDeEntrega(String fechaDeEntrega) {
        this.fechaDeEntrega = fechaDeEntrega;
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

    public double getTipoDeCambio() {
        return tipoDeCambio;
    }

    public void setTipoDeCambio(double tipoDeCambio) {
        this.tipoDeCambio = tipoDeCambio;
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

    public int getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(int idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getCodUbigeo() {
        return codUbigeo;
    }

    public void setCodUbigeo(String codUbigeo) {
        this.codUbigeo = codUbigeo;
    }

    public List<OrderItemRequest> getOrderDetalle() {
        return orderDetalle;
    }

    public void setOrderDetalle(List<OrderItemRequest> orderDetalle) {
        this.orderDetalle = orderDetalle;
    }
}
