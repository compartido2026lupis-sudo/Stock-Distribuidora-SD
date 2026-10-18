package com.stockdistribuidorasa.sd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(
    private val categorias: List<Categoria>,
    private val onEditar: (Categoria) -> Unit,
    private val onEliminar: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvNombre: TextView =
            view.findViewById(R.id.tvNombreCategoria)

        val btnEditar: Button =
            view.findViewById(R.id.btnEditarCategoria)

        val btnEliminar: Button =
            view.findViewById(R.id.btnEliminarCategoria)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)

        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoriaViewHolder,
        position: Int
    ) {

        val categoria = categorias[position]

        holder.tvNombre.text = categoria.nombre

        holder.btnEditar.setOnClickListener {
            onEditar(categoria)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(categoria)
        }
    }

    override fun getItemCount(): Int = categorias.size
}