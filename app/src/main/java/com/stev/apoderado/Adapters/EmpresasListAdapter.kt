package com.stev.apoderado.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stev.apoderado.Clases.ClsEmpresa
import com.stev.apoderado.R

class EmpresasListAdapter(val act : Context, val list: List<ClsEmpresa>)
    : RecyclerView.Adapter<EmpresasListAdapter.EmpresaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EmpresaViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val item:ClsEmpresa = list[position]
        holder.bind(act, item)
    }

    override fun getItemCount(): Int = list.size


    class EmpresaViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_empresa, parent, false)) {

        var nTitulo: TextView? =null
        var nDoc: TextView? =null
        var nTelefono: TextView? =null
        var nDireccion: TextView? =null
        var nValor: TextView? =null

        init {
            nTitulo = itemView.findViewById(R.id.titulo)
            nDoc = itemView.findViewById(R.id.documento)
            nTelefono = itemView.findViewById(R.id.telefono)
            nDireccion = itemView.findViewById(R.id.direccion)
            nValor = itemView.findViewById(R.id.valor)

        }

        fun bind(act: Context,  item: ClsEmpresa) {
            nTitulo?.text = item.nombre
            nDoc?.text = item.doc
            nTelefono?.text = item.celular
            nDireccion?.text = item.direccion
            nValor?.text = item.valor.toString()

        }

    }
}