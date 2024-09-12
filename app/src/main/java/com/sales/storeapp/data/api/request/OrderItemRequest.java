package com.sales.storeapp.data.api.request;

import java.math.BigDecimal;

public class OrderItemRequest {
    private String idNumero;           // generas
    private int idProducto;            // front
    private String moneda;             // front
    private double tipoDeCambio;   // front
    private double precioUnit;     // front
    private double cantidad;       // front
    private double monto;          // front
    private double precioUnitAlTipCam; // generas
    private double montoAlTipCam;
    private int idMedida;
    private double peso;
    private String tipotributo;
    private double montograt;
    private double precunitgrat;

    public double getMontograt() {
        return montograt;
    }

    public void setMontograt(double montograt) {
        this.montograt = montograt;
    }

    public double getPrecunitgrat() {
        return precunitgrat;
    }

    public void setPrecunitgrat(double precunitgrat) {
        this.precunitgrat = precunitgrat;
    }

    public String getTipotributo() {
        return tipotributo;
    }

    public void setTipotributo(String tipotributo) {
        this.tipotributo = tipotributo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getMontoAlTipCam() {
        return montoAlTipCam;
    }

    public void setMontoAlTipCam(double montoAlTipCam) {
        this.montoAlTipCam = montoAlTipCam;
    }

    public int getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public String getIdNumero() {
        return idNumero;
    }

    public void setIdNumero(String idNumero) {
        this.idNumero = idNumero;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
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

    public double getPrecioUnit() {
        return precioUnit;
    }

    public void setPrecioUnit(double precioUnit) {
        this.precioUnit = precioUnit;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getPrecioUnitAlTipCam() {
        return precioUnitAlTipCam;
    }

    public void setPrecioUnitAlTipCam(double precioUnitAlTipCam) {
        this.precioUnitAlTipCam = precioUnitAlTipCam;
    }
}
