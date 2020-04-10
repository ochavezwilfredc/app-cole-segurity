package com.app.movilidad.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.movilidad.Clases.ClsVehiculo
import com.app.movilidad.R

class VehiculoListAdapter(val act : Context, val list: List<ClsVehiculo>)
    : RecyclerView.Adapter<VehiculoListAdapter.VehiculoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VehiculoViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val item:ClsVehiculo = list[position]
        holder.bind(act, item)
    }

    override fun getItemCount(): Int = list.size


    class VehiculoViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vehiculo, parent, false)) {

        var nTitulo: TextView? =null
        var nMarca: TextView? =null
        var nColor: TextView? =null
        var nInicio: TextView? =null
        var nFin: TextView? =null
        var nEstado: TextView? =null

        init {
            nTitulo = itemView.findViewById(R.id.titulo)
            nMarca = itemView.findViewById(R.id.marca)
            nColor = itemView.findViewById(R.id.color)
            nInicio = itemView.findViewById(R.id.inicio)
            nFin = itemView.findViewById(R.id.fin)
            nEstado = itemView.findViewById(R.id.estado)

        }

        fun bind(act: Context,  item: ClsVehiculo) {
            nTitulo?.text = item.placa
            nMarca?.text = item.marca
            nColor?.text = item.color
            nInicio?.text = item.inicio
            nFin?.text = item.fin
            if(item.estado){
                nEstado?.text = "ACTIVO"
            }else{
                nEstado?.text = "INACTIVO"
            }
        }

    }
}