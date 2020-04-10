package com.app.movilidad.Clases

data class ClsServicioRutas(
    val id:Int, val code:String, val alumno:String, val direccion_alumno:String,
    val direccion_colegio: String ,val hora_entrada:String,val hora_salida:String,
    val latitud_colegio: Double, val longitud_colegio:Double, var observacion:String ) {

    var mostrarCheckbox = true
    var position = 0
    var servicio_id = -1

    override fun toString(): String {
        return code
    }
}