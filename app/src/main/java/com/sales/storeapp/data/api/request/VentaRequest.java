package com.sales.storeapp.data.api.request;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class VentaRequest {
    private String fechaEmision;
    private String serieNumeroPedido;
    private String totalOPGravadas;
    private String totalOPExoneradas;
    private String totalOPNoGravadas;
    private String totalOPGratuitas;
    private String descuentosGlobales;
    private String totalAnticipos;
    private String sumatoriaISC;
    private String sumatoriaIGV;
    private String importeTotalVenta;
    private String totalDescuentos;
    private List<VentaItemRequest> items = new ArrayList<>();
    private double totalGravadasConIGV;
    private String tipoDocIdentidadReceptor;
    private String numeroDocIdentidadReceptor;
    private String razonSocialReceptor;
    private String correoReceptor;
    private String codigoPaisReceptor;
    private String departamentoReceptor;
    private String provinciaReceptor;
    private String distritoReceptor;
    private String ubigeoReceptor;
    private String urbanizacionReceptor;
    private String direccionReceptor;
    private String horaEmision;
    private String tipoMoneda;
    private ServerRequest server;
    private String numeroPedido;
    private String fechaPedido;
    private String tipoDocIdentidadEmisor;
    private String numeroDocIdentidadEmisor;
    private boolean esFicticio;
    private String keepNumber;
    private String serie;
    private int numero;
    private String version;
    private String codigoTipoOperacion;
    private int tipoCorreo;
    private boolean esContingencia;
    private String serieNumero;
    private String formaPago;
    private String medioPago;

    private String formaPagoGlosa;
    private String formaPagoMonto;

    @Expose(serialize = false, deserialize = false)
    private int idUsuario;
    @Expose(serialize = false, deserialize = false)
    private String condicionPago;
    @Expose(serialize = false, deserialize = false)
    private String tipoDocumento;

    public String getSerieNumeroPedido() {
        return serieNumeroPedido;
    }

    public void setSerieNumeroPedido(String serieNumeroPedido) {
        this.serieNumeroPedido = serieNumeroPedido;
    }

    public String getFormaPagoGlosa() {
        return formaPagoGlosa;
    }

    public void setFormaPagoGlosa(String formaPagoGlosa) {
        this.formaPagoGlosa = formaPagoGlosa;
    }

    public String getFormaPagoMonto() {
        return formaPagoMonto;
    }

    public void setFormaPagoMonto(String formaPagoMonto) {
        this.formaPagoMonto = formaPagoMonto;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTotalOPGravadas() {
        return totalOPGravadas;
    }

    public void setTotalOPGravadas(String totalOPGravadas) {
        this.totalOPGravadas = totalOPGravadas;
    }

    public String getTotalOPExoneradas() {
        return totalOPExoneradas;
    }

    public void setTotalOPExoneradas(String totalOPExoneradas) {
        this.totalOPExoneradas = totalOPExoneradas;
    }

    public String getTotalOPNoGravadas() {
        return totalOPNoGravadas;
    }

    public void setTotalOPNoGravadas(String totalOPNoGravadas) {
        this.totalOPNoGravadas = totalOPNoGravadas;
    }

    public String getTotalOPGratuitas() {
        return totalOPGratuitas;
    }

    public void setTotalOPGratuitas(String totalOPGratuitas) {
        this.totalOPGratuitas = totalOPGratuitas;
    }

    public String getDescuentosGlobales() {
        return descuentosGlobales;
    }

    public void setDescuentosGlobales(String descuentosGlobales) {
        this.descuentosGlobales = descuentosGlobales;
    }

    public String getTotalAnticipos() {
        return totalAnticipos;
    }

    public void setTotalAnticipos(String totalAnticipos) {
        this.totalAnticipos = totalAnticipos;
    }

    public String getSumatoriaISC() {
        return sumatoriaISC;
    }

    public void setSumatoriaISC(String sumatoriaISC) {
        this.sumatoriaISC = sumatoriaISC;
    }

    public String getSumatoriaIGV() {
        return sumatoriaIGV;
    }

    public void setSumatoriaIGV(String sumatoriaIGV) {
        this.sumatoriaIGV = sumatoriaIGV;
    }

    public String getImporteTotalVenta() {
        return importeTotalVenta;
    }

    public void setImporteTotalVenta(String importeTotalVenta) {
        this.importeTotalVenta = importeTotalVenta;
    }

    public String getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(String totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public List<VentaItemRequest> getItems() {
        return items;
    }

    public void setItems(List<VentaItemRequest> items) {
        this.items = items;
    }

    public double getTotalGravadasConIGV() {
        return totalGravadasConIGV;
    }

    public void setTotalGravadasConIGV(double totalGravadasConIGV) {
        this.totalGravadasConIGV = totalGravadasConIGV;
    }

    public String getTipoDocIdentidadReceptor() {
        return tipoDocIdentidadReceptor;
    }

    public void setTipoDocIdentidadReceptor(String tipoDocIdentidadReceptor) {
        this.tipoDocIdentidadReceptor = tipoDocIdentidadReceptor;
    }

    public String getNumeroDocIdentidadReceptor() {
        return numeroDocIdentidadReceptor;
    }

    public void setNumeroDocIdentidadReceptor(String numeroDocIdentidadReceptor) {
        this.numeroDocIdentidadReceptor = numeroDocIdentidadReceptor;
    }

    public String getRazonSocialReceptor() {
        return razonSocialReceptor;
    }

    public void setRazonSocialReceptor(String razonSocialReceptor) {
        this.razonSocialReceptor = razonSocialReceptor;
    }

    public String getCorreoReceptor() {
        return correoReceptor;
    }

    public void setCorreoReceptor(String correoReceptor) {
        this.correoReceptor = correoReceptor;
    }

    public String getCodigoPaisReceptor() {
        return codigoPaisReceptor;
    }

    public void setCodigoPaisReceptor(String codigoPaisReceptor) {
        this.codigoPaisReceptor = codigoPaisReceptor;
    }

    public String getDepartamentoReceptor() {
        return departamentoReceptor;
    }

    public void setDepartamentoReceptor(String departamentoReceptor) {
        this.departamentoReceptor = departamentoReceptor;
    }

    public String getProvinciaReceptor() {
        return provinciaReceptor;
    }

    public void setProvinciaReceptor(String provinciaReceptor) {
        this.provinciaReceptor = provinciaReceptor;
    }

    public String getDistritoReceptor() {
        return distritoReceptor;
    }

    public void setDistritoReceptor(String distritoReceptor) {
        this.distritoReceptor = distritoReceptor;
    }

    public String getUbigeoReceptor() {
        return ubigeoReceptor;
    }

    public void setUbigeoReceptor(String ubigeoReceptor) {
        this.ubigeoReceptor = ubigeoReceptor;
    }

    public String getUrbanizacionReceptor() {
        return urbanizacionReceptor;
    }

    public void setUrbanizacionReceptor(String urbanizacionReceptor) {
        this.urbanizacionReceptor = urbanizacionReceptor;
    }

    public String getDireccionReceptor() {
        return direccionReceptor;
    }

    public void setDireccionReceptor(String direccionReceptor) {
        this.direccionReceptor = direccionReceptor;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getHoraEmision() {
        return horaEmision;
    }

    public void setHoraEmision(String horaEmision) {
        this.horaEmision = horaEmision;
    }

    public ServerRequest getServer() {
        return server;
    }

    public void setServer(ServerRequest server) {
        this.server = server;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
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

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }


    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocIdentidadEmisor() {
        return numeroDocIdentidadEmisor;
    }

    public void setNumeroDocIdentidadEmisor(String numeroDocIdentidadEmisor) {
        this.numeroDocIdentidadEmisor = numeroDocIdentidadEmisor;
    }

    public String getTipoDocIdentidadEmisor() {
        return tipoDocIdentidadEmisor;
    }

    public void setTipoDocIdentidadEmisor(String tipoDocIdentidadEmisor) {
        this.tipoDocIdentidadEmisor = tipoDocIdentidadEmisor;
    }

    public boolean getEsFicticio() {
        return esFicticio;
    }

    public void setEsFicticio(boolean esFicticio) {
        this.esFicticio = esFicticio;
    }

    public String getKeepNumber() {
        return keepNumber;
    }

    public void setKeepNumber(String keepNumber) {
        this.keepNumber = keepNumber;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCodigoTipoOperacion() {
        return codigoTipoOperacion;
    }

    public void setCodigoTipoOperacion(String codigoTipoOperacion) {
        this.codigoTipoOperacion = codigoTipoOperacion;
    }

    public int getTipoCorreo() {
        return tipoCorreo;
    }

    public void setTipoCorreo(int tipoCorreo) {
        this.tipoCorreo = tipoCorreo;
    }

    public boolean esContingencia() {
        return esContingencia;
    }

    public void setEsContingencia(boolean esContingencia) {
        this.esContingencia = esContingencia;
    }

    public String getSerieNumero() {
        return serieNumero;
    }

    public void setSerieNumero(String serieNumero) {
        this.serieNumero = serieNumero;
    }
}
