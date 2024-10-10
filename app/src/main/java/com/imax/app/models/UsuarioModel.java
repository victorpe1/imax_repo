package com.imax.app.models;

public class UsuarioModel {
    public static final String TIPO_USUARIO_PERSONAL = "P";
    public static final String TIPO_USUARIO_NEGOCIO = "N";
    public static final int ROL_ADMINISTRADOR = 66;
    public static final int ROL_VENDEDOR = 99;
    public static final String ROL_CODIGO_ADMINISTRADOR = "ADM";
    public static final String ROL_CODIGO_VENDEDOR = "MOV";
    public static final String ROL_CODIGO_VENDEDOR_2 = "VTA";

    private int idUsuario;
    private String nombre;
    private String razonSocial;
    private String telefono;
    private String correo;
    private String clave;
    private String ruc;
    private String idBodegaCliente;
    private String serie;
    private String tipoUsuario;
    private String latitud;
    private String longitud;
    private String direccion;
    private String idClienteMagic;
    private boolean admin;
    private String usuario;

    private String tipoDocumentoEmisor;
    private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;




    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getIdBodegaCliente() {
        return idBodegaCliente;
    }

    public void setIdBodegaCliente(String idBodegaCliente) {
        this.idBodegaCliente = idBodegaCliente;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdClienteMagic() {
        return idClienteMagic;
    }

    public void setIdClienteMagic(String idClienteMagic) {
        this.idClienteMagic = idClienteMagic;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getTipoDocumentoEmisor() {
        return tipoDocumentoEmisor;
    }

    public void setTipoDocumentoEmisor(String tipoDocumentoEmisor) {
        this.tipoDocumentoEmisor = tipoDocumentoEmisor;
    }
}
