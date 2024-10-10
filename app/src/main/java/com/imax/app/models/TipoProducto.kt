package com.imax.app.models

enum class TipoProducto(val code: String) {
    VENTA("V"),
    BONIFICACION("B"),
    PUBLICIDAD("P"),
    SERVICIO("S");

    companion object {
        fun findByCode(code: String) = values().find { it.code == code }
    }
}