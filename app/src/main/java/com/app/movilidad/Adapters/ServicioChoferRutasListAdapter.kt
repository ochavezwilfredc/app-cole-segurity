package com.app.movilidad.Adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.movilidad.Clases.ClsServicioRutas
import com.app.movilidad.Clases.VAR
import com.app.movilidad.MainActivity
import com.app.movilidad.R
import com.app.movilidad.ui.ServicioRutaDialogFragment
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class ServicioChoferRutasListAdapter(val act : Context, val list: List<ClsServicioRutas>)
    : RecyclerView.Adapter<ServicioChoferRutasListAdapter.ServicioChoferRutasViewHolder>() {

    val requestQueue =  Volley.newRequestQueue(act)

    val sharedPref = act?.getSharedPreferences(
        VAR.PREF_NAME,
        VAR.PRIVATE_MODE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioChoferRutasViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ServicioChoferRutasViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ServicioChoferRutasViewHolder, position: Int) {
        val item = list[position]
        item.position = position + 1
        holder.bind(act, item)
        holder.cbEntrada?.setOnCheckedChangeListener { buttonView, isChecked ->
            val params = JSONObject()
            params.put("ruta_servicio_id", item.id)
            val request : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, VAR.url("chofer_rutas_hora_llegada"), params,
                Response.Listener { response ->
                    if(response!=null){
                        val estado = response.getInt("estado")
                        val mensaje = response.getString("mensaje")
                        if(estado == 200){
                            Toasty.success(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                            holder.cbEntrada?.isEnabled = false
                        }else{
                            Toasty.warning(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                        }
                    }

                },
                Response.ErrorListener{
                    try {
                        Toasty.error(act, "NO SE PUDO CAMBIAR LA HORA DE ENTRAD.", Toast.LENGTH_SHORT, true).show()
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


            requestQueue?.add(request)
        }
        holder.cbSalida?.setOnCheckedChangeListener { buttonView, isChecked ->

            val params = JSONObject()
            params.put("ruta_servicio_id", item.id)
            val request : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, VAR.url("chofer_rutas_hora_salida"), params,
                Response.Listener { response ->
                    if(response!=null){
                        val estado = response.getInt("estado")
                        val mensaje = response.getString("mensaje")
                        if(estado == 200){
                            Toasty.success(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                            holder.cbSalida?.isEnabled = false
                        }else{
                            Toasty.warning(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                        }
                    }

                },
                Response.ErrorListener{
                    try {
                        Toasty.error(act, "NO SE PUDO CAMBIAR LA HORA DE SALIDA. ", Toast.LENGTH_SHORT, true).show()
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


            requestQueue?.add(request)
        }
        holder.btnObservacion?.setOnClickListener {
            val txtObser = EditText(act)
            txtObser.setText(item.observacion)
            val show = AlertDialog.Builder(act)
                .setTitle("Agregar Observación")
                .setMessage("Ingrese Observación")
                .setView(txtObser)
                .setPositiveButton(
                    "Guardar"
                ) { dialog, whichButton ->
                    val params = JSONObject()
                    params.put("ruta_servicio_id", item.id)
                    params.put("observacion",txtObser.text.trim())

                    val request : JsonObjectRequest = object : JsonObjectRequest(
                        Method.POST, VAR.url("chofer_rutas_observacion"), params,
                        Response.Listener { response ->
                            if(response!=null){
                                val estado = response.getInt("estado")
                                val mensaje = response.getString("mensaje")
                                if(estado == 200){
                                    item.observacion = txtObser.text.trim().toString()
                                    Toasty.success(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                                    notifyItemChanged(position)
                                }else{
                                    Toasty.warning(act, mensaje   , Toast.LENGTH_SHORT, true).show()
                                }
                            }

                        },
                        Response.ErrorListener{
                            try {
                                Toasty.error(act, "NO SE PUDO AGREGAR OBSERVACION.", Toast.LENGTH_SHORT, true).show()
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


                    requestQueue?.add(request)
                }
                .setNegativeButton("Cancelar"
                ) { dialog, whichButton ->

                }
                .show()
        }
        holder.btnMapa?.setOnClickListener {

            val main : MainActivity = act as MainActivity

            val dialogFragment = ServicioRutaDialogFragment()
            val bundle = Bundle()
            bundle.putInt("servicioid", item.servicio_id)

            bundle.putDouble("longitud", item.longitud_colegio)
            bundle.putDouble("latitud", item.latitud_colegio)
            dialogFragment.arguments = bundle
            val ft =  main.supportFragmentManager.beginTransaction()
            val prev = main.supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null)
            {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "dialog")
        }
    }

    override fun getItemCount(): Int = list.size


    class ServicioChoferRutasViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_servicio_chofer_rutas, parent, false)) {

        var nTitulo: TextView? =null
        var nAlumno: TextView? =null
        var nDireccionAlumno: TextView? =null
        var nDireccionColegio: TextView? =null
        var nObservacion: TextView? =null
        var nContObservacion: LinearLayout? =null
        var btnObservacion:Button? =null
        var nEntrada: TextView? =null
        var nSalida: TextView? =null
        var cbEntrada:CheckBox? = null
        var cbSalida:CheckBox? = null
        var btnMapa:ImageView? = null


        init {
            cbEntrada = itemView.findViewById<CheckBox>(R.id.cbentrada)
            cbSalida = itemView.findViewById<CheckBox>(R.id.cbsalida)
            btnObservacion = itemView.findViewById(R.id.btnObservacion)
            nTitulo = itemView.findViewById(R.id.titulo)
            nAlumno = itemView.findViewById(R.id.alumno)
            nDireccionAlumno = itemView.findViewById(R.id.direccion_alumno)
            nDireccionColegio = itemView.findViewById(R.id.direccion_colegio)
            nEntrada = itemView.findViewById(R.id.entrada)
            nSalida = itemView.findViewById(R.id.salida)
            nObservacion = itemView.findViewById(R.id.observacion)
            nContObservacion = itemView.findViewById(R.id.contenedorObservacion)
            btnMapa = itemView.findViewById(R.id.btnMapa)

        }



        fun bind(act: Context,  item: ClsServicioRutas) {



            if(item.observacion.trim().isEmpty()){
               nContObservacion?.visibility = View.INVISIBLE
            }

            nObservacion?.text = item.observacion

            nTitulo?.text =  "RUTA #"+ item.position
            nAlumno?.text = item.alumno
            nDireccionColegio?.text = item.direccion_colegio
            nDireccionAlumno?.text = item.direccion_alumno
            val formatter = SimpleDateFormat("hh:mm a")
            val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            try {
                val entrada = formatter.format(parser.parse("2018-12-14T"+item.hora_entrada))
                nEntrada?.text = entrada

            }catch (ex:Exception){
                nEntrada?.text = "-"
            }
            try {
                val salida = formatter.format(parser.parse("2018-12-14T"+item.hora_salida))
                nSalida?.text = salida

            }catch (ex:Exception){
                nSalida?.text = "-"
            }

            if(!item.mostrarCheckbox){
                btnObservacion?.visibility = View.GONE
                cbEntrada?.visibility = View.GONE
                cbSalida?.visibility = View.GONE
            }


        }

    }
}