package com.app.movilidad.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.movilidad.Adapters.ServicioChoferRutasListAdapter
import com.app.movilidad.Clases.*
import com.app.movilidad.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.*

class ServiciosRutasHoyFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var listaServicios: LinkedList<ClsServicioRutas> = LinkedList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adaptador :ServicioChoferRutasListAdapter ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val root = inflater.inflate(R.layout.servicio_ruta_list, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarServicios()
        }
        adaptador = ServicioChoferRutasListAdapter(requireActivity(), listaServicios)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarServicios()

        return root
    }

    fun buscarServicios(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val params = JSONObject()
        val chofer_id= sharedPref?.getString(VAR.PREF_ID_USUARIO , "-1")
        val servicio_id= sharedPref?.getInt(VAR.PREF_ID_SERVICIO , -1)

        params.put("servicio_id", servicio_id)
        params.put("chofer_id", chofer_id)
        Log.e("myerror", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("chofer_rutas_hoy"), params,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        val servicios = response.getJSONArray("datos")
                        listaServicios.clear()
                        for (i in 0 until servicios.length()) {
                            val json = servicios.getJSONObject(i)
                            var observ = ""
                            if(!json.isNull("observacion")){
                                observ = json.getString("observacion")
                            }
                            val serv = ClsServicioRutas(
                                json.getInt("ruta_servicio_id"),
                                json.getString("code"),
                                json.getString("alumno"),
                                json.getString("direccion_alumno"),
                                json.getString("direccion_colegio"),
                                json.getString("hora_entrada"),
                                json.getString("hora_salida"),
                                json.getDouble("latitud_colegio"),
                                json.getDouble("longitud_colegio"),
                                observ
                            )

                            serv.servicio_id = servicio_id!!

                            listaServicios.add(serv)
                        }
                        adaptador?.notifyDataSetChanged()

                    }else{
                        Toasty.warning(requireActivity(), mensaje   , Toast.LENGTH_SHORT, true).show()
                    }

                    swipeRefreshLayout?.isRefreshing = false
                }

            },
            Response.ErrorListener{
                try {
                    //PROCESAR_AGREGAR = true
                    //loadingDialog?.dismiss()
                    swipeRefreshLayout?.isRefreshing = false
                    Toasty.error(requireActivity(), "NO SE ENCONTRARON SERVICIOS ASIGNADOS.", Toast.LENGTH_SHORT, true).show()
                    Log.e("myerror",  (it.message).toString())
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