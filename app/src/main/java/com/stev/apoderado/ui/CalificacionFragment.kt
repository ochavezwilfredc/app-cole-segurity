package com.stev.apoderado.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.stev.apoderado.Clases.ClsServicioAceptacion
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.HashMap

class CalificacionFragment : Fragment() {
    var barraPuntaje: AppCompatRatingBar? = null
    var sharedPref: SharedPreferences? = null
    var rutaid:Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.servicio_calificacion, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        sharedPref?.edit {
            putBoolean("ruta", false)
        }
        rutaid = sharedPref?.getInt("rutaid",0)!!
        val btnFinalizar: Button = root.findViewById(R.id.btnFinalizar)
        barraPuntaje = root?.findViewById(R.id.ratingBar)
        btnFinalizar.setOnClickListener {
            finalizarServicio()
        }
        return root
    }

    fun finalizarServicio(){


        val params = JSONObject()
        params.put("ruta_servicio_id", rutaid)
        params.put("calificacion", barraPuntaje?.rating!!.toInt())

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("servicio_ruta_calificacion"), params,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        Toasty.success(requireActivity(), mensaje   , Toast.LENGTH_SHORT, true).show()
                        activity?.onBackPressed()

                    }else{
                        Toasty.warning(requireActivity(), mensaje   , Toast.LENGTH_SHORT, true).show()
                    }

                }

            },
            Response.ErrorListener{
                try {
                    //PROCESAR_AGREGAR = true
                    //loadingDialog?.dismiss()
                    Toasty.error(requireActivity(), "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())

                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }


        val requestQueue =  Volley.newRequestQueue(requireActivity())
        requestQueue?.add(request)
    }

}