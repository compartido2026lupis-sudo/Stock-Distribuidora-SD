package com.stockdistribuidorasa.sd

import com.google.firebase.Timestamp

data class Movimiento(
    val producto: String = "",
    val codigo: String = "",
    val tipo: String = "",
    val cantidad: Long = 0,
    val fecha: Timestamp? = null
)