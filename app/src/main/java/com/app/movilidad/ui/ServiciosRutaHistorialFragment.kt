package com.app.movilidad.ui

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.app.movilidad.RegistrarActivity
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.*

class ServiciosRutaHistorialFragment : Fragment() {
    var sharedPref: SharedPreferences? = null
    var listaServicios: LinkedList<ClsServicioRutas> = LinkedList()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adaptador :ServicioChoferRutasListAdapter ? = null
    var fechaSeleccionada:String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val root = inflater.inflate(R.layout.servicio_ruta_historial, container, false)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        swipeRefreshLayout = root?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            if(fechaSeleccionada!="") buscarServicios()
        }
        adaptador = ServicioChoferRutasListAdapter(requireActivity(), listaServicios)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        val txtFecha = root.findViewById<EditText>(R.id.fecha)
        txtFecha.setOnClickListener{
            val newFragment = RegistrarActivity.DatePickerRegistrarFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaSeleccionada = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFecha.setError(null)
                    }catch (ex: java.lang.Exception){
                    }
                    txtFecha.setText(selectedDate)
                    buscarServicios()
                })
            newFragment.show(requireActivity().supportFragmentManager, "dp1451")
        }
        return root
    }

    fun buscarServicios(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val params = JSONObject()
        val chofer_id= sharedPref?.getString(VAR.PREF_ID_USUARIO , "-1")
        val servicio_id= sharedPref?.getInt(VAR.PREF_ID_SERVICIO , -1)
        params.put("servicio_id", servicio_id)
        params.put("chofer_id", chofer_id)
        params.put("fecha_inicio", fechaSeleccionada)
        params.put("fecha_fin", fechaSeleccionada)
        Log.e("myerror", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("chofer_rutas_rango_fechas"), params,
            Response.Listener { response ->
                if(response!=null){
                    val estado = response.getInt("estado")
                    val mensaje = response.getString("mensaje")

                    listaServicios.clear()
                    if(estado == 200){
                        // loadingDialog?.dismiss()
                        val servicios = response.getJSONArray("datos")
                        for (i in 0 until servicios.length()) {
                            val json = servicios.getJSONObject(i)
                            var observ = ""
                            if(!json.isNull("observacion")){
                                observ = json.getString("observacion")
                            }
                            val serv = ClsServicioRutas(
                                -1,
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

                            serv.mostrarCheckbox = false
                            listaServicios.add(serv)

                        }
                        adaptador?.notifyDataSetChanged()

                    }else{
                        adaptador?.notifyDataSetChanged()
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