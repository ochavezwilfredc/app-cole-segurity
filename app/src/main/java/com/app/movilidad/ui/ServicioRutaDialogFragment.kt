package com.app.movilidad.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.app.movilidad.Clases.ClsLocalizacion
import com.app.movilidad.Clases.DirectionsJSONParser
import com.app.movilidad.Clases.VAR
import com.app.movilidad.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList


class ServicioRutaDialogFragment : DialogFragment() {


    var ok = true
    var loc: ClsLocalizacion? = null
    var sharedPref: SharedPreferences? = null
    var servicio_id:Int = -1
    var points: ArrayList<LatLng>? = null
    var destino: LatLng? = null
    var posicion:LatLng? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        val v = inflater.inflate(R.layout.dialog_example, container, false)
        val act: AppCompatActivity = requireActivity() as AppCompatActivity
        loc = ClsLocalizacion(act)
        val mapFragment: SupportMapFragment = childFragmentManager?.findFragmentById(R.id.frg) as SupportMapFragment
        mapFragment.getMapAsync(loc)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            try {
                if(arguments!=null){
                    val lat = requireArguments().getDouble("latitud")
                    val long = requireArguments().getDouble("longitud")
                    servicio_id = requireArguments().getInt("servicioid")
                    posicion = ClsLocalizacion.lastLatLong!!
                    destino = LatLng(lat,long)
                    dibujarRuta(posicion!!,destino!!, null)
                }
            }catch (ex:Exception){
                Log.e("myerror", ex.message)
            }
        }, 2000)

    }

    override fun onDestroyView() {
        ok = false
        super.onDestroyView()
        val f = requireFragmentManager().findFragmentById(R.id.frg)
        if (f != null) requireFragmentManager().beginTransaction().remove(f).commit()
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setGravity(Gravity.CENTER)
    }

    fun urlMapsApi(origin: LatLng, dest: LatLng): String {

        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        val key = "key=" + getString(R.string.api_google)

        val parameters = str_origin + "&" + str_dest + "&" + key

        val output = "json?language=es&"

        val url = "https://maps.googleapis.com/maps/api/directions/$output$parameters";
        Log.e("error", url.toString())

        return url
    }

    fun dibujarRuta(origin: LatLng, dest: LatLng, txtTiempoEstimado: TextView?) {
        val url = urlMapsApi(origin, dest)
        loc?.gmap!!.clear()
        loc?.agregarMarcador(loc!!.markerChofer(origin))
        loc?.agregarMarcador(loc!!.markerDestino(dest))

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener { response ->
                var routes: List<List<java.util.HashMap<String, String>>>? = null

                try {

                    val parser = DirectionsJSONParser()
                    routes = parser.parse(response)
                    if (txtTiempoEstimado != null) txtTiempoEstimado.text =
                        "Llega en " + parser.parseTiempo()
                    //tiempoEstimado = parser.tiempoMin

                    var lineOptions: PolylineOptions? = null

                    for (i in routes.indices) {
                        points = ArrayList()
                        lineOptions = PolylineOptions()

                        // Fetching i-th route
                        val path = routes.get(i)

                        // Fetching all the points in i-th route
                        for (j in path.indices) {
                            val point = path.get(j)
                            val lat = java.lang.Double.parseDouble(point.get("lat")!!)
                            val lng = java.lang.Double.parseDouble(point.get("lng")!!)
                            val position = LatLng(lat, lng)
                            points!!.add(position)
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points)
                        lineOptions.width(8f)
                        lineOptions.color(Color.RED)
                    }

                    loc?.gmap?.addPolyline(lineOptions)

                    GlobalScope.launch { // launch a new coroutine in background and continue
                      enviarPosicion()
                    }

                    // cardViewInfo?.visibility = View.VISIBLE


                } catch (e: Exception) {
                    Toast.makeText(context, "Error de estimación", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }


            },
            Response.ErrorListener {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }) {}


        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }
    private suspend fun enviarPosicion() {
        points?.forEach {
            if(ok){
                posicion = it
                withContext(Dispatchers.Main) {
                    loc!!.markerOrigen?.position = posicion
                }
                actualizarPosicion()
                delay(1500)
            }
        }
    }

    fun actualizarPosicion(){
        val params = JSONObject()
        params.put("servicio_id",  servicio_id)
        params.put("latitud_actual",  posicion!!.latitude)
        params.put("longitud_actual",   posicion!!.longitude)
        Log.e("error", params.toString())

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("position_update"),params,
            Response.Listener { response ->
                if(response!=null){
                    Log.e("error", "posicion actualizada")
                }
            },
            Response.ErrorListener{
                try {
                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toast.makeText(context,  response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }catch (ex: Exception){
                    ex.printStackTrace()
                    Toast.makeText(context,  "Error de conexión", Toast.LENGTH_SHORT).show()
                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> =HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(request)
    }
}
