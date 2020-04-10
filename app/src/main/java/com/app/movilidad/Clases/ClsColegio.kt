package com.app.movilidad.Clases

data class ClsColegio(
    val id:Int, val nombre:String ) {


    override fun toString(): String {
        return nombre
    }
}