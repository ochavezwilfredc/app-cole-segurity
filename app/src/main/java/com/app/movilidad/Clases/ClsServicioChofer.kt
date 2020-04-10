package com.app.movilidad.Clases

data class ClsServicioChofer(
    val id:Int, val codigo:String, val vigencia:String ) {


    override fun toString(): String {
        return codigo
    }
}