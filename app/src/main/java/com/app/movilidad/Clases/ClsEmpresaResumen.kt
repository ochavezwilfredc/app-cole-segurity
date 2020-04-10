package com.app.movilidad.Clases

data class ClsEmpresaResumen(
    val id:Int, val nombre:String ) {

    override fun toString(): String {
        return nombre
    }
}