package com.imax.app.managers;

public class TablesHelper {

    public static final class xms_asignacion {
        public static final String table = "xms_asignacion";

        public static final String id = "id";
        public static final String address = "address";
        public static final String coordinator_id = "coordinator_id";
        public static final String coordinator_name = "coordinator_name";
        public static final String custom_field_1 = "custom_field_1";
        public static final String custom_field_19 = "custom_field_19";
        public static final String custom_field_2 = "custom_field_2";
        public static final String custom_field_3 = "custom_field_3";
        public static final String inspection_date = "inspection_date";
        public static final String inspector_id = "inspector_id";
        public static final String inspector_name = "inspector_name";
        public static final String motive_id = "motive_id";
        public static final String motive_name = "motive_name";
        public static final String name = "name";
        public static final String number = "number";
        public static final String partner_latitude = "partner_latitude";
        public static final String partner_longitude = "partner_longitude";
        public static final String type_id = "type_id";
        public static final String type_name = "type_name";
    }

    public static final class xms_usuario {
        public static final String table = "xms_usuario";

        public static final String id = "id";
        public static final String nombre = "nombre";
        public static final String celular = "celular";
        public static final String correo = "correo";
        public static final String clave = "clave";
        public static final String tipo_usuario = "tipo_usuario";
        public static final String serie = "serie";
        public static final String id_bodega_cliente = "id_bodega_cliente";
        public static final String admin = "admin";
        public static final String usuario = "usuario";
    }
    public static final class xms_producto {
        public static final String table = "xms_producto";

        public static final String id = "id";
        public static final String descripcion = "descripcion";
        public static final String id_unidad_menor = "id_unidad_menor";
        public static final String id_unidad_mayor = "id_unidad_mayor";
        public static final String factor_conversion = "factor_conversion";
        public static final String tipo_producto = "tipo_producto";
        public static final String id_marca = "id_marca";
        public static final String imagen = "imagen";
        public static final String codigo = "codigo";
        public static final String codigo_sku = "codigo_sku";


        public static final String id_producto_libre = "id_producto_libre";
        public static final String flgProductoLibre = "flgProductoLibre";

        public static final String precio_min = "precio_min";

    }
    public static final class xms_producto_detalle {
        public static final String table = "xms_producto_detalle";

        public static final String id_producto = "id_producto";
        public static final String tipo = "tipo";
        public static final String natural = "natural";
        public static final String porcentaje = "porcentaje";
    }
    public static final class xms_configuracion_usuario {
        public static final String table = "xms_configuracion_usuario";
        public static final String identificador = "identificador";
        public static final String valor = "valor";

        public static final String Configuracion_correoSoporte = "correoSoporte";
        public static final String Fecha = "fecha";
        public static final String MaximoPedido = "maximoPedido";
        public static final String UnidadVentaMayor = "unidadVentaMayor";
        public static final String IdPoliticaMinorista = "idPoliticaMinorista";
        public static final String PreventaEnLinea = "preventaEnLinea";
        public static final String PorcentajeIGV = "porcentajeIGV";
        public static final String Direccion = "direccion";
        public static final String IdClienteGeneral = "idClienteGeneral";
        public static final String LimitePercepcion = "limitePercepcion";
        public static final String AfectoPercepcion = "afectoPercepcion";
        public static final String UrlTracking = "urlTracking";
    }
    public static final class xms_forma_pago {
        public static final String table = "xms_forma_pago";
        public static final String id = "id";
        public static final String descripcion = "descripcion";
    }

    public static final class xms_tcambio{
        public static final String table = "xms_tcambio";

        public static final String fecha = "fecha";
        public static final String libre = "libre";
        public static final String compra = "compra";
        public static final String venta = "venta";
    }

    public static final class xms_unidad_medida {
        public static final String table = "xms_unidad_medida";

        public static final String id = "id";
        public static final String descripcion = "descripcion";
        public static final String comercial = "comercial";
        public static final String sunat = "sunat";
    }

    public static final class xms_medidas {
        public static final String table = "xms_medidas";

        public static final String id_medida = "id_medida";
        public static final String nombre = "nombre";
        public static final String factor_medida = "factor_medida";
        public static final String id_unidad = "id_unidad";
        public static final String factor_conversion = "factor_conversion";
        public static final String nume_orde = "nume_orde";
        public static final String codigo = "codigo";
        public static final String med_sunat = "med_sunat";
    }

    public static final class xms_marcas {
        public static final String table = "xms_marcas";

        public static final String id = "id";
        public static final String descripcion = "descripcion";
    }

    public static final class xml_inspeccion {
        public static final String table = "xml_inspeccion";

        // Columnas de la tabla 'inspeccion'
        public static final String id = "id";
        public static final String numInspeccion = "numInspeccion";
        public static final String modalidad = "modalidad";
        public static final String inscripcion = "inscripcion";
        public static final String fecha = "fecha";
        public static final String hora = "hora";
        public static final String contacto = "contacto";
        public static final String latitud = "latitud";
        public static final String longitud = "longitud";
        public static final String direccion = "direccion";
        public static final String distrito = "distrito";
        public static final String provincia = "provincia";
        public static final String departamento = "departamento";
        public static final String tipoInmueble = "tipoInmueble";
        public static final String otros = "otros";
        public static final String usosInmueble = "usosInmueble";
        public static final String comentarios = "comentarios";
        public static final String recibeInmueble = "recibeInmueble";
        public static final String nPisos = "nPisos";
        public static final String distribucion = "distribucion";
        public static final String referencia = "referencia";
        public static final String deposito = "deposito";
        public static final String estacionamiento = "estacionamiento";
        public static final String depto = "depto";
        public static final String otroEstructura = "otroEstructura";
        public static final String otroAlbañeria = "otroAlbañeria";
        public static final String otroPisos = "otroPisos";
        public static final String otroPuertas = "otroPuertas";
        public static final String otroVentana = "otroVentana";
        public static final String otroMampara = "otroMampara";
        public static final String otroCocina = "otroCocina";
        public static final String otroBaño = "otroBaño";
        public static final String otroSanitarias = "otroSanitarias";
        public static final String otroElectricas = "otroElectricas";
        public static final String tipoPuerta = "tipoPuerta";
        public static final String materialPuerta = "materialPuerta";
        public static final String sistemaPuerta = "sistemaPuerta";
        public static final String marcoVentana = "marcoVentana";
        public static final String vidrioVentana = "vidrioVentana";
        public static final String sistemaVentana = "sistemaVentana";
        public static final String marcoMampara = "marcoMampara";
        public static final String vidrioMampara = "vidrioMampara";
        public static final String sistemaMampara = "sistemaMampara";
        public static final String muebleCocina = "muebleCocina";
        public static final String muebleCocina2 = "muebleCocina2";
        public static final String tablero = "tablero";
        public static final String lavaderos = "lavaderos";
        public static final String sanitarioTipo = "sanitarioTipo";
        public static final String sanitarioColor = "sanitarioColor";
        public static final String sanitario = "sanitario";
        public static final String iss = "iss";
        public static final String iiee = "iiee";
        public static final String sistemaIncendio = "sistemaIncendio";
        public static final String estructura = "estructura";
        public static final String muros = "muros";
        public static final String revestimiento = "revestimiento";
        public static final String pisos = "pisos";
        public static final String pisosCocina = "pisosCocina";
        public static final String paredesCocina = "paredesCocina";
        public static final String pisosBaños = "pisosBaños";
        public static final String paredesBaño = "paredesBaño";
        public static final String infraestructura_comentario = "infraestructura_comentario";
        public static final String cbCoincideInformacion = "cbCoincideInformacion";
        public static final String cbDocumentacionSITU = "cbDocumentacionSITU";
        public static final String especificar = "especificar";
        public static final String especificar2 = "especificar2";
        public static final String files = "files"; // Para guardar la lista en formato JSON
        public static final String observacion = "observacion";
        public static final String base64Firma = "base64Firma";
        public static final String estado = "estado"; // Para marcar si fue enviado o no
    }

    public static final class xml_inspeccion_fotografia {
        public static final String table = "xml_inspeccion_fotografia";

        public static final String id = "id";
        public static final String numInspeccion = "numInspeccion";
        public static final String foto_array_1 = "foto_array_1";
        public static final String fotos_array_adjunto_1 = "fotos_array_adjunto_1";
        public static final String foto_array_2 = "foto_array_2";
        public static final String fotos_array_adjunto_2 = "fotos_array_adjunto_2";
        public static final String foto_array_3 = "foto_array_3";
        public static final String fotos_array_adjunto_3 = "fotos_array_adjunto_3";
        public static final String foto_array_4 = "foto_array_4";
        public static final String fotos_array_adjunto_4 = "fotos_array_adjunto_4";
        public static final String fotos_array_adjunto_5 = "fotos_array_adjunto_5";

        public static final String estado = "estado";
    }


    public static final class xms_cliente {
        public static final String table = "xms_cliente";

        public static final String id = "id";
        public static final String ruc_dni = "ruc_dni";
        public static final String razon_social = "razon_social";
        public static final String direccion = "direccion";
        public static final String tipo_documento = "tipo_documento";
        public static final String ubigeo = "ubigeo";
        public static final String urbanizacion = "urbanizacion";
        public static final String provincia = "provincia";
        public static final String departamento = "departamento";
        public static final String distrito = "distrito";
        public static final String codigoPais = "codigoPais";
        public static final String correo = "correo";
    }

    public static final class xms_personal {
        public static final String table = "xms_personal";

        public static final String id_personal = "id_personal";
        public static final String nombre = "nombre";
        public static final String domicilio = "domicilio";
        public static final String id_distrito = "id_distrito";
        public static final String telefonos = "telefonos";
        public static final String dni = "dni";
        public static final String ruc = "ruc";
        public static final String pcomision = "pcomision";
        public static final String id_ocupacion = "id_ocupacion";
        public static final String fechanac = "fechanac";
        public static final String sexo = "sexo";
        public static final String activo = "activo";
    }


    public static final class xms_pedido_cabecera {
        public static final String table = "xms_pedido_cabecera";

        public static final String numero_pedido = "numero_pedido";
        public static final String id_cliente = "id_cliente";
        public static final String id_usuario = "id_usuario";
        public static final String fecha_pedido = "fecha_pedido";
        public static final String id_forma_pago = "id_forma_pago";
        public static final String observacion = "observacion";
        public static final String importe_total = "importe_total";
        public static final String estado = "estado";
        public static final String flag = "flag";
        public static final String serie_documento = "serie_documento";
        public static final String numero_documento = "numero_documento";
        public static final String hash = "hash";
        public static final String pdf_base64 = "pdf_base64";
        public static final String cadena_qr = "cadena_qr";

        public static final String tipoDocIdentidadReceptor = "tipoDocIdentidadReceptor";
        public static final String numeroDocIdentidadReceptor = "numeroDocIdentidadReceptor";
        public static final String razonSocialReceptor = "razonSocialReceptor";
        public static final String codigoPaisReceptor = "codigoPaisReceptor";
        public static final String departamentoReceptor = "departamentoReceptor";
        public static final String provinciaReceptor = "provinciaReceptor";
        public static final String distritoReceptor = "distritoReceptor";
        public static final String ubigeoReceptor = "ubigeoReceptor";
        public static final String urbanizacionReceptor = "urbanizacionReceptor";
        public static final String direccionReceptor = "direccionReceptor";
        public static final String fechaEmision = "fechaEmision";
        public static final String horaEmision = "horaEmision";
        public static final String tipoMoneda = "tipoMoneda";
        public static final String idTipoDocumento = "idTipoDocumento";
        public static final String correoReceptor = "correoReceptor";
        public static final String tipoCorreo = "tipoCorreo";
        public static final String medioPago = "medioPago";
        public static final String fecha_pedido_glosa = "fecha_pedido_glosa";
    }
    public static final class xms_pedido_detalle {
        public static final String table = "xms_pedido_detalle";


        public static final String order_item = "order_item";
        public static final String numero_pedido = "numero_pedido";
        public static final String id_producto = "id_producto";
        public static final String tipo_producto = "tipo_producto";
        public static final String precio_bruto = "precio_bruto";
        public static final String cantidad = "cantidad";
        public static final String precio_neto = "precio_neto";
        public static final String id_unidad_medida = "id_unidad_medida";
        public static final String percepcion = "percepcion";
        public static final String ISC = "ISC";
        public static final String item = "item";
    }
    public static final class xms_ubigeo_departamento {
        public static final String table = "xms_ubigeo_departamento";
        public static final String id = "id";
        public static final String nombre = "nombre";
    }
    public static final class xms_ubigeo_provincia {
        public static final String table = "xms_ubigeo_provincia";
        public static final String id_departamento = "id_departamento";
        public static final String id_provincia = "id_provincia";
        public static final String nombre = "nombre";
    }
    public static final class xms_ubigeo_distrito {
        public static final String table = "xms_ubigeo_distrito";
        public static final String id_departamento = "id_departamento";
        public static final String id_provincia = "id_provincia";
        public static final String id_distrito = "id_distrito";
        public static final String nombre = "nombre";
    }

    public static final class xms_distritos {
        public static final String table = "xms_distritos";

        public static final String id_distrito = "id_distrito";
        public static final String nombre = "nombre";
        public static final String id_provincia = "id_provincia";
        public static final String cont_distsunat = "cont_distsunat";
        public static final String codubigeo = "codubigeo";
    }


    public static final class xms_empresa {
        public static final String table = "xms_empresa";

        public static final String id = "id";
        public static final String ruc = "ruc";
        public static final String razon_social = "razon_social";
    }
    public static final class xms_lista_precio {
        public static final String table = "xms_lista_precio";

        public static final String id = "id";
        public static final String codigo = "codigo";
        public static final String descripcion = "descripcion";
    }
    public static final class xms_lista_precio_producto {
        public static final String table = "xms_lista_precio_producto";

        public static final String id_lista = "id_lista";
        public static final String id_producto = "id_producto";
        public static final String valor_venta = "valor_venta";
        public static final String precio_venta = "precio_venta";
    }
    public static final class xms_almacenes {
        public static final String table = "xms_almacenes";

        public static final String id_almacen = "id_almacen";
        public static final String nombre = "nombre";
        public static final String descripcion = "ubicacion";
    }

    public static final class xms_condiciones {
        public static final String table = "xms_condiciones";

        public static final String id_condicion = "id_condicion";
        public static final String nombre = "nombre";
        public static final String dias = "dias";
        public static final String tipo = "tipo";
    }

    public static final class xms_order {
        public static final String table = "xms_order";

        public static final String id_numero = "id_numero";
        public static final String nombre = "nombre";
        public static final String fecha = "fecha";
        public static final String fechaDeVenc = "fechadevenc";
        public static final String fechaDeEntrega = "fechadeentrega";
        public static final String id_cliente = "id_cliente";
        public static final String direcc = "direcc";
        public static final String id_cond = "id_cond";
        public static final String id_vendedor = "id_vendedor";
        public static final String id_cobrador = "id_cobrador";
        public static final String id_transportista = "id_transportista";
        public static final String id_almacen = "id_almacen";
        public static final String moneda = "moneda";
        public static final String tipodecambio = "tipodecambio";
        public static final String subtotal = "subtotal";
        public static final String descuento = "descuento";
        public static final String total = "total";
        public static final String subtotalaltipcam = "subtotalaltipcam";
        public static final String descuentoaltipcam = "descuentoaltipcam";
        public static final String totalaltipcam = "totalaltipcam";
        public static final String id_usuario = "id_usuario";
        public static final String estado = "estado";
        public static final String comi = "comi";
        public static final String id_precioxzona = "id_precioxzona";
        public static final String facturado = "facturado";
        public static final String factura = "factura";
        public static final String id_num_alt = "id_num_alt";
        public static final String id_distrito = "id_distrito";
        public static final String email = "email";
        public static final String codubigeo = "codubigeo";
        public static final String ordcom = "ordcom";
        public static final String id_motirech = "id_motirech";
        public static final String id_ruta = "id_ruta";
        public static final String id_coordinador = "id_coordinador";
        public static final String observacion = "observacion";
        public static final String total_opexonerado = "total_opexonerado";
        public static final String total_opgratuito = "total_opgratuito";
        public static final String typeDocument = "typeDocument";
    }

    public static final class xms_order_detail {
        public static final String table = "xms_order_detail";

        public static final String id_numero = "id_numero";
        public static final String id_producto = "id_producto";
        public static final String moneda = "moneda";
        public static final String tipoDeCambio = "tipodecambio";
        public static final String precioUnit = "preciounit";
        public static final String cantidad = "cantidad";
        public static final String monto = "monto";
        public static final String precioUnitAlTipCam = "preciounitaltipcam";
        public static final String montoAlTipCam = "montoaltipcam";
        public static final String pcomi = "pcomi";
        public static final String comi = "comi";
        public static final String mone_cost = "mone_cost";
        public static final String costo = "costo";
        public static final String cantidad_promocion = "cantidad_promocion";
        public static final String flag_promocion = "flag_promocion";
        public static final String cantidad2 = "cantidad2";
        public static final String id_medida = "id_medida";
        public static final String order_item = "order_item";
        public static final String tipotributo = "tipotributo";
        public static final String bonificacion = "bonificacion";
        public static final String precunitgrat = "precunitgrat";
        public static final String montograt = "montograt";

    }

    public static final class xms_product {
        public static final String table = "xms_product";

        public static final String id_producto = "id_producto";

        public static final String nombre = "nombre";
        public static final String facuni = "facuni";
        public static final String id_medida = "id_medida";
        public static final String id_unidad = "id_unidad";
        public static final String moneda = "moneda";
        public static final String kardex = "kardex";
        public static final String series = "series";
        public static final String stockmin = "stockmin";
        public static final String stockmax = "stockmax";
        public static final String prevtaigv = "prevtaigv";
        public static final String preciovtamen = "preciovtamen";
        public static final String preciovtamay = "preciovtamay";
        public static final String mone_cost = "mone_cost";
        public static final String costo = "costo";
        public static final String peso = "peso";
        public static final String codigo = "codigo";
        public static final String unidad = "unidad";
        public static final String id_marca = "id_marca";
        public static final String tipo_atributo = "tipo_atributo";
    }

    public static final class xms_client {
        public static final String table = "xms_client";

        public static final String id_cliente = "id_cliente";
        public static final String nombre = "nombre";
        public static final String representante = "representante";
        public static final String domicilio = "domicilio";
        public static final String id_distrito = "id_distrito";
        public static final String local = "local";
        public static final String ocupacion = "ocupacion";
        public static final String telefonos = "telefonos";
        public static final String fax = "fax";
        public static final String ruc = "ruc";
        public static final String dni = "dni";
        public static final String moneda = "moneda";
        public static final String id_tienda = "id_tienda";
        public static final String id_vendedor = "id_vendedor";
        public static final String id_cobrador = "id_cobrador";
        public static final String activo = "activo";
        public static final String direcc1 = "direcc1";
        public static final String direcc2 = "direcc2";
        public static final String direcc3 = "direcc3";
        public static final String direcc4 = "direcc4";
        public static final String id_distrito_direcc1 = "id_distrito_direcc1";
        public static final String id_distrito_direcc2 = "id_distrito_direcc2";
        public static final String id_distrito_direcc3 = "id_distrito_direcc3";
        public static final String id_distrito_direcc4 = "id_distrito_direcc4";
        public static final String email = "email";
        public static final String telefono2 = "telefono2";
        public static final String telefono3 = "telefono3";
        public static final String telefono4 = "telefono4";
        public static final String codubigeo = "codubigeo";
        public static final String email1 = "email1";
        public static final String email2 = "email2";
        public static final String email3 = "email3";
        public static final String email4 = "email4";
        public static final String codubigeo1 = "codubigeo1";
        public static final String codubigeo2 = "codubigeo2";
        public static final String codubigeo3 = "codubigeo3";
        public static final String codubigeo4 = "codubigeo4";
        public static final String nacionalidad = "nacionalidad";
        public static final String carnet_extranjeria = "carnet_extranjeria";
    }

}
