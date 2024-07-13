package com.sales.storeapp.models;

public class OrderDetail {
    private String idNumber;
    private int idProduct;
    private String codigoProducto;
    private String producto;
    private String moneda;
    private double tipoCambio;
    private double precioUnit;
    private double cantidad;
    private double monto;
    private double peso;
    private double precioUnitTipoCambio;
    private double montoTipoCambio;
    private double pComi;
    private double comi;
    private String moneCost;
    private double costo;
    private double cantidadPromocion;
    private boolean flagPromocion;
    private int idMedida;
    private String medida;
    private String imagen;
    private int item;

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
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

    public double getPrecioUnitTipoCambio() {
        return precioUnitTipoCambio;
    }

    public void setPrecioUnitTipoCambio(double precioUnitTipoCambio) {
        this.precioUnitTipoCambio = precioUnitTipoCambio;
    }

    public double getMontoTipoCambio() {
        return montoTipoCambio;
    }

    public void setMontoTipoCambio(double montoTipoCambio) {
        this.montoTipoCambio = montoTipoCambio;
    }

    public double getpComi() {
        return pComi;
    }

    public void setpComi(double pComi) {
        this.pComi = pComi;
    }

    public double getComi() {
        return comi;
    }

    public void setComi(double comi) {
        this.comi = comi;
    }

    public String getMoneCost() {
        return moneCost;
    }

    public void setMoneCost(String moneCost) {
        this.moneCost = moneCost;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public double getCantidadPromocion() {
        return cantidadPromocion;
    }

    public void setCantidadPromocion(double cantidadPromocion) {
        this.cantidadPromocion = cantidadPromocion;
    }

    public boolean isFlagPromocion() {
        return flagPromocion;
    }

    public void setFlagPromocion(boolean flagPromocion) {
        this.flagPromocion = flagPromocion;
    }

    public int getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }
}
