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
import com.app.movilidad.Adapters.EmpresasListAdapter
import com.app.movilidad.Clases.ClsEmpresa
import com.app.movilidad.Clases.VAR
import com.app.movilidad.R
import es.dmoral.toasty.Toasty
import java.util.*

class EmpresaFragment : Fragment() {
    var sharedPref: SharedPreferences? = null

    var listaEmpresa : LinkedList<ClsEmpresa> = LinkedList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adaptador :EmpresasListAdapter ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val root = inflater.inflate(R.layout.empresas_list, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarEmpresas()
        }
        adaptador = EmpresasListAdapter(requireActivity(), listaEmpresa)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarEmpresas()

        return root
    }

    fun buscarEmpresas(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, VAR.url("empresas_list"), null,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        val empresa = response.getJSONArray("datos")
                        listaEmpresa.clear()
                        for (i in 0 until empresa.length()) {
                            val empresaJson = empresa.getJSONObject(i)
                            val emp = ClsEmpresa( empresaJson.getInt("id"),
                                empresaJson.getString("nombre_completo"),
                                empresaJson.getString("documento_identidad"),
                                empresaJson.getString("direccion"),
                                empresaJson.getString("celular"),
                                empresaJson.getDouble("valor"),
                                empresaJson.getDouble("porcentaje")

                            )
                            listaEmpresa.add(emp)

                        }

                        listaEmpresa.sortByDescending {
                            it.porcentaje
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