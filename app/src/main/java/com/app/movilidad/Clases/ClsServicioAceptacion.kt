package com.app.movilidad.Clases

data class ClsServicioAceptacion(
    val id:Int, val alumno:String,val empresa:String,val  colegio:String,
                     val horaentrada:String, val horasalida:String, val solicitud:String, val vigencia:String ) {
    override fun toString(): String {
        return "ClsServicioAceptacion(id=$id, alumno='$alumno', empresa='$empresa', colegio='$colegio', horaentrada='$horaentrada', horasalida='$horasalida', solicitud='$solicitud', vigencia='$vigencia')"
    }
}