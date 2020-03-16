package com.stev.apoderado.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.stev.apoderado.Clases.ClsEmpresaResumen
import com.stev.apoderado.Clases.ClsLocalizacion
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.*

class ServicioRutaFragment : Fragment() {

    var loc : ClsLocalizacion? =null
    var sharedPref: SharedPreferences? = null
    var servicio_id:Int = -1

    var txtChofer:TextView ?= null
    var txtAuto:TextView ?= null

    var contadorCal = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.servicio_ruta, container, false)

        txtAuto = root.findViewById(R.id.auto)
        txtChofer = root.findViewById(R.id.chofer)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        servicio_id = sharedPref?.getInt("id", -1)!!
        val acti = activity as AppCompatActivity
        loc = ClsLocalizacion(acti)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(loc)

        buscarRuta()

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {

                val ok = sharedPref?.getBoolean("ruta", false)!!
                if(ok){
                    try {
                        buscarRuta()
                        mainHandler.postDelayed(this, 5000)
                    }catch (ex: java.lang.Exception){

                    }
                }

            }
        })

        return root
    }

    fun buscarRuta(){
        contadorCal ++
        val params = JSONObject()
        params.put("servicio_detalle_id", servicio_id)
        params.put("fecha", "2020-03-02")

        Log.e("error", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("servicio_ruta"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")
                    if(response.getInt("estado") == 200 ){
                        val datos =  response.getJSONObject("datos")
                        val latitud = datos.getDouble("latitud")
                        val longitud = datos.getDouble("longitud")
                        val chofer = datos.getString("chofer")
                        val marca  = datos.getString("marca")
                        val placa = datos.getString("placa")
                        val rutaid = datos.getInt("ruta_servicio_id")

                        val calificacion = datos.optBoolean("placa", false)
                        if( (calificacion|| contadorCal > 3) &&
                            sharedPref?.getBoolean("ruta" ,false)!!  ){
                            sharedPref?.edit {
                                putInt("rutaid", rutaid )
                                putBoolean("ruta", false )
                            }

                            val navHostFragment: NavHostFragment =
                                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                            navHostFragment.navController.navigate(R.id.nav_calificacion)

                        }
                        val pos = LatLng(latitud,longitud)
                        loc?.addMarker(pos)
                        txtChofer?.text = chofer
                        txtAuto?.text = "$marca $placa"

                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                    }
                }
            }, Response.ErrorListener{
                try {
                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                }catch (ex: Exception){
                    Log.e("error", ex.message)
                    Toasty.error(requireActivity(), "Error de conexión!", Toast.LENGTH_LONG, true).show()
                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(request)
    }
}