package com.app.movilidad.Clases

data class ClsAlumno(
    val id:Int, val nombre:String,val doc:String,val  direccion:String,
                     val celular:String) {

    override fun toString(): String {
        return nombre
    }
}