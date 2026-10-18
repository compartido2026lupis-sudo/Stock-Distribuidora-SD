package com.stockdistribuidorasa.sd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MarcaAdapter(
    private val marcas: List<Marca>,
    private val onEditar: (Marca) -> Unit,
    private val onEliminar: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    class MarcaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreMarca)
        val btnEditar: Button = view.findViewById(R.id.btnEditarMarca)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarMarca)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarcaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marca, parent, false)

        return MarcaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarcaViewHolder,
        position: Int
    ) {

        val marca = marcas[position]

        holder.tvNombre.text = marca.nombre

        holder.btnEditar.setOnClickListener {
            onEditar(marca)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(marca)
        }
    }

    override fun getItemCount(): Int = marcas.size
}