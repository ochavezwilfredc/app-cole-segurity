package com.app.movilidad.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.app.movilidad.Clases.ClsServicioChofer
import com.app.movilidad.Clases.VAR
import com.app.movilidad.MainActivity
import com.app.movilidad.R

class ServicioChoferListAdapter(val act : Context, val list: List<ClsServicioChofer>)
    : RecyclerView.Adapter<ServicioChoferListAdapter.ServicioChoferViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioChoferViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ServicioChoferViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ServicioChoferViewHolder, position: Int) {
        val item:ClsServicioChofer = list[position]
        holder.bind(act, item)
    }

    override fun getItemCount(): Int = list.size


    class ServicioChoferViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_servicio, parent, false)) {

        var nTitulo: TextView? =null
        var nEstado: TextView? =null

        init {
            nTitulo = itemView.findViewById(R.id.titulo)
            nEstado = itemView.findViewById(R.id.estado)

        }

        fun bind(act: Context,  item: ClsServicioChofer) {
            nTitulo?.text =  "SERVICIO #"+ item.codigo
            nEstado?.text = item.vigencia


            var btnVerRutas:Button? = itemView.findViewById(R.id.btnRuta)
            var btnHistorial:Button? = itemView.findViewById(R.id.btnHistorial)

            val activity:MainActivity = act as MainActivity
            val sharedPref = activity?.getSharedPreferences(
                VAR.PREF_NAME,
                VAR.PRIVATE_MODE)
            sharedPref.edit().putInt(VAR.PREF_ID_SERVICIO, item.id).apply()
            btnVerRutas?.setOnClickListener {
                val navHostFragment: NavHostFragment =
                    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate(R.id.nav_servicios_ruta_hoy)
            }
            btnHistorial?.setOnClickListener {
                val navHostFragment: NavHostFragment =
                    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate(R.id.nav_servicios_ruta_historial)
            }


            /*
            if(item.vigencia == "Vigente"){
                btnVerRutas?.visibility = View.VISIBLE
            }else{
                btnVerRutas?.visibility = View.GONE
            }

             */


        }

    }
}