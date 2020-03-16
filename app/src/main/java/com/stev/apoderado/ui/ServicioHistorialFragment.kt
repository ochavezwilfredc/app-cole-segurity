package com.stev.apoderado.ui

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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
import com.stev.apoderado.Clases.ClsLocalizacion
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.R
import com.stev.apoderado.RegistrarActivity
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.HashMap

class ServicioHistorialFragment : Fragment() {
    var sharedPref: SharedPreferences? = null
    var fechaNacimiento:String = ""
    var servicio_id = -1
    var loc : ClsLocalizacion? =null
    var txtChofer: TextView ? = null
    var txtMarca: TextView ? = null
    var txtPlaca: TextView ? = null
    var txtLlegada: TextView ? = null
    var txtSalida: TextView ? = null
    var nContendor:LinearLayout ? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        servicio_id = sharedPref?.getInt("id", -1)!!
        val root = inflater.inflate(R.layout.servicio_historial, container, false)
        txtChofer = root.findViewById(R.id.chofer)
        txtMarca = root.findViewById(R.id.marca)
        txtPlaca = root.findViewById(R.id.placa)
        txtLlegada = root.findViewById(R.id.llegada)
        txtSalida = root.findViewById(R.id.salida)
        val acti = activity as AppCompatActivity
        loc = ClsLocalizacion(acti)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(loc)
        nContendor = root.findViewById(R.id.contenedor)
        nContendor?.visibility = View.INVISIBLE
        val txtFecha = root.findViewById<EditText>(R.id.fecha)

        txtFecha.setOnClickListener{
            val newFragment = RegistrarActivity.DatePickerRegistrarFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaNacimiento = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFecha.setError(null)
                    }catch (ex: java.lang.Exception){
                    }
                    txtFecha.setText(selectedDate)
                    buscarHistorial()
                })
            newFragment.show(requireActivity().supportFragmentManager, "dp1451")
        }
        return root
    }


    fun buscarHistorial(){
        val params = JSONObject()
        params.put("servicio_detalle_id", servicio_id)
        params.put("fecha", fechaNacimiento)

        Log.e("error", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("servicio_ruta_historial"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")
                    if(response.getInt("estado") == 200 ){
                        val datos =  response.getJSONObject("datos")
                        txtChofer?.text = datos.getString("chofer")
                        txtPlaca?.text = datos.getString("placa")
                        txtMarca?.text = datos.getString("marca")
                        txtLlegada?.text = datos.getString("hora_llegada_real")
                        txtSalida?.text = datos.getString("hora_salida_real")
                        val latitud = datos.getDouble("latitud")
                        val longitud = datos.getDouble("longitud")
                        val pos = LatLng(latitud,longitud)
                        loc?.addMarker(pos)

                        nContendor?.visibility = View.VISIBLE
                    }else{
                        nContendor?.visibility = View.INVISIBLE
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                    }
                }
            }, Response.ErrorListener{
                try {
                    nContendor?.visibility = View.INVISIBLE

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