package com.app.movilidad.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.movilidad.Adapters.ServicioAceptacionListAdapter
import com.app.movilidad.Clases.ClsServicioAceptacion
import com.app.movilidad.Clases.VAR
import com.app.movilidad.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.*

class ServicioAceptacionFragment : Fragment() {


    var sharedPref: SharedPreferences? = null
    var listaServicios : LinkedList<ClsServicioAceptacion> = LinkedList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adaptador : ServicioAceptacionListAdapter? = null
    var idapoderado:Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val root = inflater.inflate(R.layout.servicio_aceptacion, container, false)
        idapoderado = sharedPref?.getString(VAR.PREF_ID_USUARIO, "0")!!.toInt()

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarServicios()
        }


        adaptador = ServicioAceptacionListAdapter(requireActivity(), listaServicios)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarServicios()

        return root
    }

    fun buscarServicios(){
        sharedPref?.edit {
            putBoolean("ruta", false)
        }
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true

        val params = JSONObject()
        params.put("apoderado_id", idapoderado)
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("servicio_aceptacion"), params,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        val datos = response.getJSONArray("datos")
                        listaServicios.clear()
                        for (i in 0 until datos.length()) {
                            val json = datos.getJSONObject(i)
                            val serv = ClsServicioAceptacion( json.getInt("servicio_detalle_id"),
                                json.getString("alumno"),
                                json.getString("empresa"),
                                json.getString("colegio"),
                                json.getString("hora_entrada"),
                                json.getString("hora_salida"),
                                json.getString("solicitud"),
                                json.getString("vigencia")
                            )
                            listaServicios.add(serv)

                        }

                        listaServicios.sortBy {
                            it.id
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