package com.imax.app.data.api.request;

import android.text.TextUtils;

import com.imax.app.intents.AntesInspeccion;
import com.imax.app.intents.CaracteristicasEdificacion;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.intents.DespuesInspeccion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class InspeccionRequest implements Serializable {

    // Datos de Inspección
    private String numInspeccion;
    private String modalidad;
    private String inscripcion;
    private String fecha;
    private String hora;
    private String contacto;
    private String latitud;
    private String longitud;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;

    // Características Generales
    private String tipoInmueble;
    private String otros;
    private String usosInmueble;
    private String comentarios;
    private String recibeInmueble;
    private String nPisos;
    private String distribucion;
    private String referencia;
    private String deposito;
    private String estacionamiento;
    private String depto;

    // Características de Edificación
    private String otroEstructura;
    private String otroAlbañeria;
    private String otroPisos;
    private String otroPuertas;
    private String otroVentana;
    private String otroMampara;
    private String otroCocina;
    private String otroBaño;
    private String otroSanitarias;
    private String otroElectricas;
    private String tipoPuerta;
    private String materialPuerta;
    private String sistemaPuerta;
    private String marcoVentana;
    private String vidrioVentana;
    private String sistemaVentana;
    private String marcoMampara;
    private String vidrioMampara;
    private String sistemaMampara;
    private String muebleCocina;
    private String muebleCocina2;
    private String tablero;
    private String lavaderos;
    private String sanitarioTipo;
    private String sanitarioColor;
    private String sanitario;
    private String iss;
    private String iiee;
    private String sistemaIncendio;  // "Tiene" o "No tiene"
    private String estructura;
    private String muros;
    private String revestimiento;
    private String pisos;
    private String pisosCocina;
    private String paredesCocina;
    private String pisosBaños;
    private String paredesBaño;

    // Características de Infraestructura
    private String infraestructura_comentario;

    // Despues Inspeccion
    private String cbCoincideInformacion;
    private String cbDocumentacionSITU;
    private String especificar;
    private String especificar2;
    private List<String> files;

    // Despues Inspeccion
    private String observacion;
    private String base64Firma;

    public  InspeccionRequest(AntesInspeccion antesInspeccion,
                             CaracteristicasGenerales caracteristicasGenerales,
                             CaracteristicasEdificacion caracteristicasEdificacion,
                             String infraestructura_comentario,
                             DespuesInspeccion despuesInspeccion,
                             String observacion,
                             String base64Firma) {

        // Datos de Inspección
        this.numInspeccion = antesInspeccion.getNumInspeccion();
        this.modalidad = antesInspeccion.getModalidad();
        this.inscripcion = antesInspeccion.getInscripcion();
        this.fecha = antesInspeccion.getFecha();
        this.hora = antesInspeccion.getHora();
        this.contacto = antesInspeccion.getContacto();
        this.latitud = antesInspeccion.getLatitud();
        this.longitud = antesInspeccion.getLongitud();
        this.direccion = antesInspeccion.getDireccion();
        this.distrito = antesInspeccion.getDistrito();
        this.provincia = antesInspeccion.getProvincia();
        this.departamento = antesInspeccion.getDepartamento();

        // Características Generales
        this.tipoInmueble = caracteristicasGenerales.getTipoInmueble();
        this.otros = caracteristicasGenerales.getOtros();

        List<String> usosSeleccionados = new ArrayList<>();
        if (caracteristicasGenerales.isCbVivienda()) usosSeleccionados.add("Vivienda");
        if (caracteristicasGenerales.isCbComercio()) usosSeleccionados.add("Comercio");
        if (caracteristicasGenerales.isCbIndustria()) usosSeleccionados.add("Industria");
        if (caracteristicasGenerales.isCbEducativo()) usosSeleccionados.add("Educativo");
        if (caracteristicasGenerales.isCbOther()) usosSeleccionados.add("Otro");
        String textoResultado = TextUtils.join(", ", usosSeleccionados);
        this.usosInmueble = textoResultado;

        this.comentarios = caracteristicasGenerales.getComentarios();
        this.recibeInmueble = caracteristicasGenerales.getRecibeInmueble();
        this.nPisos = caracteristicasGenerales.getNPisos();
        this.distribucion = caracteristicasGenerales.getDistribucion();
        this.referencia = caracteristicasGenerales.getReferencia();
        this.deposito = caracteristicasGenerales.getDeposito();
        this.estacionamiento = caracteristicasGenerales.getEstacionamiento();
        this.depto = caracteristicasGenerales.getDepto();

        // Características de Edificación
        this.otroEstructura = caracteristicasEdificacion.getOtroEstructura();
        this.otroAlbañeria = caracteristicasEdificacion.getOtroAlbañeria();
        this.otroPisos = caracteristicasEdificacion.getOtroPisos();
        this.otroPuertas = caracteristicasEdificacion.getOtroPuertas();
        this.otroVentana = caracteristicasEdificacion.getOtroVentana();
        this.otroMampara = caracteristicasEdificacion.getOtroMampara();
        this.otroCocina = caracteristicasEdificacion.getOtroCocina();
        this.otroBaño = caracteristicasEdificacion.getOtroBaño();
        this.otroSanitarias = caracteristicasEdificacion.getOtroSanitarias();
        this.otroElectricas = caracteristicasEdificacion.getOtroElectricas();
        this.tipoPuerta = caracteristicasEdificacion.getTipoPuerta();
        this.materialPuerta = caracteristicasEdificacion.getMaterialPuerta();
        this.sistemaPuerta = caracteristicasEdificacion.getSistemaPuerta();
        this.marcoVentana = caracteristicasEdificacion.getMarcoVentana();
        this.vidrioVentana = caracteristicasEdificacion.getVidrioVentana();
        this.sistemaVentana = caracteristicasEdificacion.getSistemaVentana();
        this.marcoMampara = caracteristicasEdificacion.getMarcoMampara();
        this.vidrioMampara = caracteristicasEdificacion.getVidrioMampara();
        this.sistemaMampara = caracteristicasEdificacion.getSistemaMampara();
        this.muebleCocina = caracteristicasEdificacion.getMuebleCocina();
        this.muebleCocina2 = caracteristicasEdificacion.getMuebleCocina2();
        this.tablero = caracteristicasEdificacion.getTablero();
        this.lavaderos = caracteristicasEdificacion.getLavaderos();
        this.sanitarioTipo = caracteristicasEdificacion.getSanitarioTipo();
        this.sanitarioColor = caracteristicasEdificacion.getSanitarioColor();
        this.sanitario = caracteristicasEdificacion.getSanitario();
        this.iss = caracteristicasEdificacion.getIss();
        this.iiee = caracteristicasEdificacion.getIiee();
        this.sistemaIncendio = caracteristicasEdificacion.getSistemaIncendio();
        this.estructura = caracteristicasEdificacion.getEstructura();
        this.muros = caracteristicasEdificacion.getMuros();
        this.revestimiento = caracteristicasEdificacion.getRevestimiento();
        this.pisos = caracteristicasEdificacion.getPisos();
        this.pisosCocina = caracteristicasEdificacion.getPisosCocina();
        this.paredesCocina = caracteristicasEdificacion.getParedesCocina();
        this.pisosBaños = caracteristicasEdificacion.getPisosBaños();
        this.paredesBaño = caracteristicasEdificacion.getParedesBaño();

        // Características de Infraestructura
        this.infraestructura_comentario = infraestructura_comentario;

        // Después de la Inspección
        this.cbCoincideInformacion = despuesInspeccion.isTiene() ? "SI" : "NO";
        this.cbDocumentacionSITU = despuesInspeccion.isTiene2() ? "SI" : "NO";
        this.especificar = despuesInspeccion.getEspecificar();
        this.especificar2 = despuesInspeccion.getEspecificar2();
        this.files = despuesInspeccion.getFiles();

        // Observaciones y firma
        this.observacion = observacion;
        this.base64Firma = base64Firma;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    // Constructor vacío
    public InspeccionRequest() {}

    public String getNumInspeccion() {
        return numInspeccion;
    }

    public void setNumInspeccion(String numInspeccion) {
        this.numInspeccion = numInspeccion;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(String inscripcion) {
        this.inscripcion = inscripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
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

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getOtros() {
        return otros;
    }

    public void setOtros(String otros) {
        this.otros = otros;
    }

    public String getUsosInmueble() {
        return usosInmueble;
    }

    public void setUsosInmueble(String usosInmueble) {
        this.usosInmueble = usosInmueble;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getRecibeInmueble() {
        return recibeInmueble;
    }

    public void setRecibeInmueble(String recibeInmueble) {
        this.recibeInmueble = recibeInmueble;
    }

    public String getnPisos() {
        return nPisos;
    }

    public void setnPisos(String nPisos) {
        this.nPisos = nPisos;
    }

    public String getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(String distribucion) {
        this.distribucion = distribucion;
    }

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEstacionamiento() {
        return estacionamiento;
    }

    public void setEstacionamiento(String estacionamiento) {
        this.estacionamiento = estacionamiento;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getOtroEstructura() {
        return otroEstructura;
    }

    public void setOtroEstructura(String otroEstructura) {
        this.otroEstructura = otroEstructura;
    }

    public String getOtroAlbañeria() {
        return otroAlbañeria;
    }

    public void setOtroAlbañeria(String otroAlbañeria) {
        this.otroAlbañeria = otroAlbañeria;
    }

    public String getOtroPisos() {
        return otroPisos;
    }

    public void setOtroPisos(String otroPisos) {
        this.otroPisos = otroPisos;
    }

    public String getOtroPuertas() {
        return otroPuertas;
    }

    public void setOtroPuertas(String otroPuertas) {
        this.otroPuertas = otroPuertas;
    }

    public String getOtroVentana() {
        return otroVentana;
    }

    public void setOtroVentana(String otroVentana) {
        this.otroVentana = otroVentana;
    }

    public String getOtroMampara() {
        return otroMampara;
    }

    public void setOtroMampara(String otroMampara) {
        this.otroMampara = otroMampara;
    }

    public String getOtroCocina() {
        return otroCocina;
    }

    public void setOtroCocina(String otroCocina) {
        this.otroCocina = otroCocina;
    }

    public String getOtroBaño() {
        return otroBaño;
    }

    public void setOtroBaño(String otroBaño) {
        this.otroBaño = otroBaño;
    }

    public String getOtroSanitarias() {
        return otroSanitarias;
    }

    public void setOtroSanitarias(String otroSanitarias) {
        this.otroSanitarias = otroSanitarias;
    }

    public String getOtroElectricas() {
        return otroElectricas;
    }

    public void setOtroElectricas(String otroElectricas) {
        this.otroElectricas = otroElectricas;
    }

    public String getTipoPuerta() {
        return tipoPuerta;
    }

    public void setTipoPuerta(String tipoPuerta) {
        this.tipoPuerta = tipoPuerta;
    }

    public String getMaterialPuerta() {
        return materialPuerta;
    }

    public void setMaterialPuerta(String materialPuerta) {
        this.materialPuerta = materialPuerta;
    }

    public String getSistemaPuerta() {
        return sistemaPuerta;
    }

    public void setSistemaPuerta(String sistemaPuerta) {
        this.sistemaPuerta = sistemaPuerta;
    }

    public String getMarcoVentana() {
        return marcoVentana;
    }

    public void setMarcoVentana(String marcoVentana) {
        this.marcoVentana = marcoVentana;
    }

    public String getVidrioVentana() {
        return vidrioVentana;
    }

    public void setVidrioVentana(String vidrioVentana) {
        this.vidrioVentana = vidrioVentana;
    }

    public String getSistemaVentana() {
        return sistemaVentana;
    }

    public void setSistemaVentana(String sistemaVentana) {
        this.sistemaVentana = sistemaVentana;
    }

    public String getMarcoMampara() {
        return marcoMampara;
    }

    public void setMarcoMampara(String marcoMampara) {
        this.marcoMampara = marcoMampara;
    }

    public String getVidrioMampara() {
        return vidrioMampara;
    }

    public void setVidrioMampara(String vidrioMampara) {
        this.vidrioMampara = vidrioMampara;
    }

    public String getSistemaMampara() {
        return sistemaMampara;
    }

    public void setSistemaMampara(String sistemaMampara) {
        this.sistemaMampara = sistemaMampara;
    }

    public String getMuebleCocina() {
        return muebleCocina;
    }

    public void setMuebleCocina(String muebleCocina) {
        this.muebleCocina = muebleCocina;
    }

    public String getMuebleCocina2() {
        return muebleCocina2;
    }

    public void setMuebleCocina2(String muebleCocina2) {
        this.muebleCocina2 = muebleCocina2;
    }

    public String getTablero() {
        return tablero;
    }

    public void setTablero(String tablero) {
        this.tablero = tablero;
    }

    public String getLavaderos() {
        return lavaderos;
    }

    public void setLavaderos(String lavaderos) {
        this.lavaderos = lavaderos;
    }

    public String getSanitarioTipo() {
        return sanitarioTipo;
    }

    public void setSanitarioTipo(String sanitarioTipo) {
        this.sanitarioTipo = sanitarioTipo;
    }

    public String getSanitarioColor() {
        return sanitarioColor;
    }

    public void setSanitarioColor(String sanitarioColor) {
        this.sanitarioColor = sanitarioColor;
    }

    public String getSanitario() {
        return sanitario;
    }

    public void setSanitario(String sanitario) {
        this.sanitario = sanitario;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getIiee() {
        return iiee;
    }

    public void setIiee(String iiee) {
        this.iiee = iiee;
    }

    public String getSistemaIncendio() {
        return sistemaIncendio;
    }

    public void setSistemaIncendio(String sistemaIncendio) {
        this.sistemaIncendio = sistemaIncendio;
    }

    public String getEstructura() {
        return estructura;
    }

    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }

    public String getMuros() {
        return muros;
    }

    public void setMuros(String muros) {
        this.muros = muros;
    }

    public String getRevestimiento() {
        return revestimiento;
    }

    public void setRevestimiento(String revestimiento) {
        this.revestimiento = revestimiento;
    }

    public String getPisos() {
        return pisos;
    }

    public void setPisos(String pisos) {
        this.pisos = pisos;
    }

    public String getPisosCocina() {
        return pisosCocina;
    }

    public void setPisosCocina(String pisosCocina) {
        this.pisosCocina = pisosCocina;
    }

    public String getParedesCocina() {
        return paredesCocina;
    }

    public void setParedesCocina(String paredesCocina) {
        this.paredesCocina = paredesCocina;
    }

    public String getPisosBaños() {
        return pisosBaños;
    }

    public void setPisosBaños(String pisosBaños) {
        this.pisosBaños = pisosBaños;
    }

    public String getParedesBaño() {
        return paredesBaño;
    }

    public void setParedesBaño(String paredesBaño) {
        this.paredesBaño = paredesBaño;
    }

    public String getInfraestructura_comentario() {
        return infraestructura_comentario;
    }

    public void setInfraestructura_comentario(String infraestructura_comentario) {
        this.infraestructura_comentario = infraestructura_comentario;
    }

    public String getCbCoincideInformacion() {
        return cbCoincideInformacion;
    }

    public void setCbCoincideInformacion(String cbCoincideInformacion) {
        this.cbCoincideInformacion = cbCoincideInformacion;
    }

    public String getCbDocumentacionSITU() {
        return cbDocumentacionSITU;
    }

    public void setCbDocumentacionSITU(String cbDocumentacionSITU) {
        this.cbDocumentacionSITU = cbDocumentacionSITU;
    }

    public String getEspecificar() {
        return especificar;
    }

    public void setEspecificar(String especificar) {
        this.especificar = especificar;
    }

    public String getEspecificar2() {
        return especificar2;
    }

    public void setEspecificar2(String especificar2) {
        this.especificar2 = especificar2;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getObs() {
        return observacion;
    }

    public void setObs(String obs) {
        this.observacion = obs;
    }

    public String getBase64Firma() {
        return base64Firma;
    }

    public void setBase64Firma(String base64Firma) {
        this.base64Firma = base64Firma;
    }
}
