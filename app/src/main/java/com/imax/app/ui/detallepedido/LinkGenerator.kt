package com.imax.app.ui.detallepedido

import com.imax.app.BuildConfig


object LinkGenerator {

    /**
     * @param ruc RUC Emisor
     * @param tipoDocumento (01:Factura/03:Boleta)
     * @param serieDocumento
     * @param numeroDocumento
     * @param fechaEmision FECHA DE EMISION (YYYY-MM-DD)
     * @param importeTotal
     * @return https://easyfact.tk/see/server/consult/pdf?nde=20601610036&td=03&se=B001&nu=178977&fe=2020-01-02&am=76.5
     */
    fun create(ruc: String, tipoDocumento: String, serieDocumento: String, numeroDocumento: String, fechaEmision: String, importeTotal: Double): String {
        return BuildConfig.API_BASE_URL
                .plus("see/server/consult/pdf")
                .plus("?nde=$ruc")
                .plus("&td=$tipoDocumento")
                .plus("&se=$serieDocumento")
                .plus("&nu=$numeroDocumento")
                .plus("&fe=$fechaEmision")
                .plus("&am=$importeTotal")
    }
}