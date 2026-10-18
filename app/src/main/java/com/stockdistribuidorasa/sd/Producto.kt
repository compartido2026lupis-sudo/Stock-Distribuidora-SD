package com.stockdistribuidorasa.sd

data class Producto(
    val nombre: String = "",
    val marca: String = "",
    val categoria: String = "",
    val presentacion: String = "",
    val existencia: Long = 0,
    val codigo: String = "",
    val imagenUrl: String = ""
)