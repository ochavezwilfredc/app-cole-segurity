package com.stev.apoderado.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.stev.apoderado.Clases.ClsServicioAceptacion
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.MainActivity
import com.stev.apoderado.R

class ServicioAceptacionListAdapter(val act : Context, val list: List<ClsServicioAceptacion>)
    : RecyclerView.Adapter<ServicioAceptacionListAdapter.ServicioAceptacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioAceptacionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ServicioAceptacionViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ServicioAceptacionViewHolder, position: Int) {
        val item:ClsServicioAceptacion = list[position]
        holder.bind(act, item, position +1)
    }

    override fun getItemCount(): Int = list.size


    class ServicioAceptacionViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_servicio_aceptacion, parent, false)) {

        var nTitulo: TextView? =null
        var nAlumno: TextView? =null
        var nColegio: TextView? =null
        var nEmpresa: TextView? =null
        var nLlegada: TextView? =null
        var nSalida: TextView? =null
        var btnHistorial: Button? = null
        var btnRuta: Button? = null
        var nSolicitud:TextView ? = null
        var nVigencia:TextView ? = null
        init {
            nTitulo = itemView.findViewById(R.id.titulo)
            nAlumno = itemView.findViewById(R.id.alumno)
            nColegio = itemView.findViewById(R.id.colegio)
            nEmpresa = itemView.findViewById(R.id.empresa)
            nLlegada = itemView.findViewById(R.id.llegada)
            nSalida = itemView.findViewById(R.id.salida)
            btnHistorial  = itemView.findViewById(R.id.btnHistorial)
            btnRuta = itemView.findViewById(R.id.btnRuta)
            nSolicitud = itemView.findViewById(R.id.solicitud)
            nVigencia = itemView.findViewById(R.id.vigencia)

        }

        fun bind(act: Context,  item: ClsServicioAceptacion, i:Int) {

            nTitulo?.text = "SERVICIO #$i"
            nAlumno?.text = item.alumno
            nColegio?.text = item.colegio
            nEmpresa?.text = item.empresa
            nLlegada?.text =  item.horaentrada
            nSalida?.text =  item.horasalida
            nVigencia?.text = item.vigencia
            nSolicitud?.text = item.solicitud
            //Log.e("error", item.toString())
            if(item.solicitud != "Aceptado" && item.vigencia == "Vigente"){
                btnRuta?.visibility = View.VISIBLE
            }
            else if(item.solicitud == "Aceptado" && item.vigencia != "Vigente"){
                btnHistorial?.visibility = View.VISIBLE
            }
            val activity:MainActivity = act as MainActivity

            val sharedPref = activity.getSharedPreferences(
                VAR.PREF_NAME,
                VAR.PRIVATE_MODE)

            val navHostFragment: NavHostFragment =
                activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            btnRuta?.setOnClickListener{
                sharedPref?.edit {
                    putInt("id", item.id)
                    putBoolean("ruta", true)
                }
                navHostFragment.navController.navigate(R.id.nav_ruta)
            }
            btnHistorial?.setOnClickListener{
                sharedPref?.edit {
                    putInt("id", item.id)
                }
                navHostFragment.navController.navigate(R.id.nav_servicio_historial)
            }
            /*
            btnRuta?.visibility = View.VISIBLE
            btnHistorial?.visibility = View.VISIBLE
            */


        }
    }
}