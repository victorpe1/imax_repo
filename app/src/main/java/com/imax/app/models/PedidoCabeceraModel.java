package com.imax.app.models;

import java.io.Serializable;

public class PedidoCabeceraModel implements Serializable {

    private String id_numero;
    private String id_cliente;
    private String nombre;
    private String id_usuario;
    private String fecha;
    private String direcc;
    private String id_vendedor;
    private String nombrePersonal;
    private String id_almacen;
    private String nombreAlmacen;
    private double subtotal;
    private double descuento;
    private double total;
    private String codubigeo;
    private String observacion;
    private double total_opgratuito;
    private double total_opexonerado;
    private String condicion;

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getId_numero() {
        return id_numero;
    }

    public void setId_numero(String id_numero) {
        this.id_numero = id_numero;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDirecc() {
        return direcc;
    }

    public void setDirecc(String direcc) {
        this.direcc = direcc;
    }

    public String getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(String id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public String getNombrePersonal() {
        return nombrePersonal;
    }

    public void setNombrePersonal(String nombrePersonal) {
        this.nombrePersonal = nombrePersonal;
    }

    public String getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(String id_almacen) {
        this.id_almacen = id_almacen;
    }

    public String getNombreAlmacen() {
        return nombreAlmacen;
    }

    public void setNombreAlmacen(String nombreAlmacen) {
        this.nombreAlmacen = nombreAlmacen;
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

    public String getCodubigeo() {
        return codubigeo;
    }

    public void setCodubigeo(String codubigeo) {
        this.codubigeo = codubigeo;
    }

    public double getTotal_opgratuito() {
        return total_opgratuito;
    }

    public void setTotal_opgratuito(double total_opgratuito) {
        this.total_opgratuito = total_opgratuito;
    }

    public double getTotal_opexonerado() {
        return total_opexonerado;
    }

    public void setTotal_opexonerado(double total_opexonerado) {
        this.total_opexonerado = total_opexonerado;
    }
}
