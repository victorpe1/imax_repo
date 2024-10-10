package com.imax.app.data.api.request;

public class VentaItemRequest {
    private int ordenItem;
    private int idProducto;
    private String codigoProductoItem;
    private String descripcionItem;
    private String unidadMedidaItem;
    private String unidadMedidaComercial;
    private double cantidadItem;
    private String precioUnitarioConIgv;
    private String valorUnitarioSinIgv;
    private int codigoAfectacionIGVItem;
    private String importeIGVItem;
    private String descuentoItem;
    private String valorVentaItem;
    private String montoTotalItem;
    private String codTipoPrecioVtaUnitarioItem;

    public int getOrdenItem() {
        return ordenItem;
    }

    public void setOrdenItem(int ordenItem) {
        this.ordenItem = ordenItem;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoProductoItem() {
        return codigoProductoItem;
    }

    public void setCodigoProductoItem(String codigoProductoItem) {
        this.codigoProductoItem = codigoProductoItem;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public String getUnidadMedidaItem() {
        return unidadMedidaItem;
    }

    public void setUnidadMedidaItem(String unidadMedidaItem) {
        this.unidadMedidaItem = unidadMedidaItem;
    }

    public double getCantidadItem() {
        return cantidadItem;
    }

    public void setCantidadItem(double cantidadItem) {
        this.cantidadItem = cantidadItem;
    }

    public String getPrecioUnitarioConIgv() {
        return precioUnitarioConIgv;
    }

    public void setPrecioUnitarioConIgv(String precioUnitarioConIgv) {
        this.precioUnitarioConIgv = precioUnitarioConIgv;
    }

    public String getValorUnitarioSinIgv() {
        return valorUnitarioSinIgv;
    }

    public void setValorUnitarioSinIgv(String valorUnitarioSinIgv) {
        this.valorUnitarioSinIgv = valorUnitarioSinIgv;
    }

    public int getCodigoAfectacionIGVItem() {
        return codigoAfectacionIGVItem;
    }

    public void setCodigoAfectacionIGVItem(int codigoAfectacionIGVItem) {
        this.codigoAfectacionIGVItem = codigoAfectacionIGVItem;
    }

    public String getImporteIGVItem() {
        return importeIGVItem;
    }

    public void setImporteIGVItem(String importeIGVItem) {
        this.importeIGVItem = importeIGVItem;
    }

    public String getDescuentoItem() {
        return descuentoItem;
    }

    public void setDescuentoItem(String descuentoItem) {
        this.descuentoItem = descuentoItem;
    }

    public String getValorVentaItem() {
        return valorVentaItem;
    }

    public void setValorVentaItem(String valorVentaItem) {
        this.valorVentaItem = valorVentaItem;
    }

    public String getMontoTotalItem() {
        return montoTotalItem;
    }

    public void setMontoTotalItem(String montoTotalItem) {
        this.montoTotalItem = montoTotalItem;
    }

    public String getUnidadMedidaComercial() {
        return unidadMedidaComercial;
    }

    public void setUnidadMedidaComercial(String unidadMedidaComercial) {
        this.unidadMedidaComercial = unidadMedidaComercial;
    }

    public String getCodTipoPrecioVtaUnitarioItem() {
        return codTipoPrecioVtaUnitarioItem;
    }

    public void setCodTipoPrecioVtaUnitarioItem(String codTipoPrecioVtaUnitarioItem) {
        this.codTipoPrecioVtaUnitarioItem = codTipoPrecioVtaUnitarioItem;
    }
}
