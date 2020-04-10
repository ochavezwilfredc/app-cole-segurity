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
import com.app.movilidad.Adapters.VehiculoListAdapter
import com.app.movilidad.Clases.ClsVehiculo
import com.app.movilidad.Clases.VAR
import com.app.movilidad.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.*

class VehiculosFragment : Fragment() {
    var sharedPref: SharedPreferences? = null

    var listaVehiculos : LinkedList<ClsVehiculo> = LinkedList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adaptador :VehiculoListAdapter ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val root = inflater.inflate(R.layout.vehiculos_list, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarVehiculos()
        }
        adaptador = VehiculoListAdapter(requireActivity(), listaVehiculos)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarVehiculos()

        return root
    }

    fun buscarVehiculos(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val params = JSONObject()
        val chofer_id= sharedPref?.getString(VAR.PREF_ID_USUARIO , "-1")
        params.put("chofer_id", chofer_id)
        Log.e("myerror", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("chofer_vehiculos_asignados"), params,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        val vehiculos = response.getJSONArray("datos")
                        listaVehiculos.clear()
                        for (i in 0 until vehiculos.length()) {
                            val json = vehiculos.getJSONObject(i)
                                val vehi = ClsVehiculo(
                                    json.getString("placa"),
                                    json.getString("marca"),
                                    json.getString("placa"),
                                    json.getString("fecha_inicio"),
                                    json.getString("fecha_fin"),
                                    json.getBoolean("vehiculo_activo")
                            )
                            listaVehiculos.add(vehi)

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
                    Toasty.error(requireActivity(), "NO SE ENCONTRARON VEHICULOS ASIGNADOS.", Toast.LENGTH_SHORT, true).show()
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