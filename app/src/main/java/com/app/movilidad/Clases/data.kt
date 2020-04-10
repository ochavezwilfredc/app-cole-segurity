package com.app.movilidad.Clases

import android.util.Patterns
import android.widget.EditText


class Validar {
    companion object {
        fun vacio(txt: EditText?): Boolean {
            return (txt!!.text.trim().isEmpty())
        }

        fun strMenorA(txt: EditText?, t: Int): Boolean {
            return (txt!!.text.trim().length < t)
        }


        fun strSize(txt: EditText?, t: Int): Boolean {
            return (txt!!.text.trim().length == t)
        }

        fun strEmail(txt: EditText?): Boolean {
            val email = getString(txt!!)
            val pat = Patterns.EMAIL_ADDRESS
            return pat.matcher(email).matches()

        }

        fun getString(txt: EditText?): String {
            return txt!!.text.trim().toString()
        }

        fun txtErr(txt: EditText?, err: String) {
            txt!!.error = err
            txt!!.requestFocus()
        }


    }
}

class VAR {
    companion object {
        val url: String = "http://192.168.18.172/route_ahp/route_ahp_api/webservice/"
        var ext: String = ".php"


        fun url(m: String): String {
            return url+ m + ext
        }


        val PRIVATE_MODE = 0
        val PREF_NAME = "apoderado-app"
        val PREF_TOKEN  = "token"
        val PREF_DNI  = "dni"
        val PREF_PASS  = "contrasenia"
        val PREF_DATA_USUARIO  = "datausuario"
        val PREF_ID_USUARIO  = "idusuario"
        val PREF_ID_SERVICIO  = "idservicio"

    }
}