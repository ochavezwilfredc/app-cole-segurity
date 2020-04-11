package com.app.movilidad.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.movilidad.Clases.ClsEmpresaReporte
import com.app.movilidad.R

class EmpresasListAdapter(val act : Context, val list: List<ClsEmpresaReporte>)
    : RecyclerView.Adapter<EmpresasListAdapter.EmpresaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EmpresaViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val item:ClsEmpresaReporte = list[position]
        holder.bind(act, item)
    }

    override fun getItemCount(): Int = list.size


    class EmpresaViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_empresa, parent, false)) {

        var nTitulo: TextView? =null
        var porcCalidad: TextView? =null
        var porcPrecio: TextView? =null
        var porcPuntualidad: TextView? =null
        var porcAntiguedad: TextView? =null
        var porcValor: TextView? =null
        var cbCedula:CheckBox? = null
        var porcTarjeta: TextView? =null
        var porcCredencial: TextView? =null
        var porcBrevete: TextView? =null

        var pbCalidad: ProgressBar? =null
        var pbPrecio: ProgressBar? =null
        var pbPuntualidad: ProgressBar? =null
        var pbAntiguedad: ProgressBar? =null
        var pbValor: ProgressBar? =null
        var pbCredencial: ProgressBar? =null
        var pbTarjeta: ProgressBar? =null
        var pbBrevete: ProgressBar? =null

        init {
            nTitulo = itemView.findViewById(R.id.titulo)
            porcCalidad = itemView.findViewById(R.id.porcCalidad)
            porcPrecio = itemView.findViewById(R.id.porcPrecio)
            porcPuntualidad = itemView.findViewById(R.id.porcPuntualidad)
            porcAntiguedad = itemView.findViewById(R.id.porcAntiguedad)
            porcValor = itemView.findViewById(R.id.porcValor)
            porcTarjeta = itemView.findViewById(R.id.porcTarjeta)
            porcCredencial = itemView.findViewById(R.id.porcCredencial)
            porcBrevete = itemView.findViewById(R.id.porcBrevete)
            cbCedula = itemView.findViewById(R.id.cbCedula)

            pbCalidad = itemView.findViewById(R.id.pbCalidad)
            pbPrecio = itemView.findViewById(R.id.pbPrecio)
            pbPuntualidad = itemView.findViewById(R.id.pbPuntualidad)
            pbAntiguedad = itemView.findViewById(R.id.pbAntiguedad)
            pbValor = itemView.findViewById(R.id.pbValor)
            pbTarjeta = itemView.findViewById(R.id.pbTarjeta)
            pbBrevete = itemView.findViewById(R.id.pbBrevete)
            pbCredencial = itemView.findViewById(R.id.pbCredencial)


        }

        fun bind(act: Context,  item: ClsEmpresaReporte) {
            nTitulo?.text = item.nombre
            porcCalidad?.text = String.format("%.2f", item.calidad) + " %"
            porcPrecio?.text = String.format("%.2f", item.precio) + " %"
            porcPuntualidad?.text = String.format("%.2f", item.puntualidad) + " %"
            porcAntiguedad?.text = String.format("%.2f", item.antiguedad) + " %"
            porcValor?.text = String.format("%.2f", item.valor) + " %"
            porcTarjeta?.text = String.format("%.2f", item.tarjeta_circulacion) + " %"
            porcCredencial?.text = String.format("%.2f", item.credencial) + " %"
            porcBrevete?.text = String.format("%.2f", item.brevete) + " %"
            cbCedula?.isChecked = item.cedula

            pbCalidad?.progress = item.calidad.toInt()
            pbPrecio?.progress = item.precio.toInt()
            pbPuntualidad?.progress = item.puntualidad.toInt()
            pbAntiguedad?.progress = item.antiguedad.toInt()
            pbValor?.progress = item.valor.toInt()
            pbTarjeta?.progress = item.tarjeta_circulacion.toInt()
            pbCredencial?.progress = item.credencial.toInt()
            pbBrevete?.progress = item.brevete.toInt()

        }

    }
}