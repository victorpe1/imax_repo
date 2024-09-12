package com.sales.storeapp.ui.pedido

import com.sales.storeapp.models.OrderDetail
import java.io.Serializable

class AgregarProductoArgument(
        val idProducto: Int,
        val codigoProducto: String,
        val descripcion: String,
        val precio: Double,
        val cantidad: Double,
        val peso: Double,
        val total: Double,
        val idMedida: Int,
        val idTipoAtributo: Int,
) : Serializable {
    fun toPedidoDetalleModel(numeroPedido: String): OrderDetail {
        val pedidoDetalleModel = OrderDetail()
        pedidoDetalleModel.idNumber = numeroPedido
        pedidoDetalleModel.idProduct = idProducto
        pedidoDetalleModel.codigoProducto = codigoProducto
        pedidoDetalleModel.precioUnit = precio
        pedidoDetalleModel.monto = total
        pedidoDetalleModel.cantidad = cantidad
        pedidoDetalleModel.peso = peso
        pedidoDetalleModel.producto = descripcion
        pedidoDetalleModel.idMedida = idMedida
        pedidoDetalleModel.moneda = "PEN"
        pedidoDetalleModel.tipotributo = idTipoAtributo

        return pedidoDetalleModel
    }
}