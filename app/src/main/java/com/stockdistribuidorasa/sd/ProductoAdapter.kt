package com.stockdistribuidorasa.sd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
class ProductoAdapter(
    private val productos: List<Producto>,
    private val onClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvMarca: TextView = view.findViewById(R.id.tvMarca)
        val tvPresentacion: TextView = view.findViewById(R.id.tvPresentacion)
        val tvExistencia: TextView = view.findViewById(R.id.tvExistencia)

        val imgProducto: ImageView =
            view.findViewById(R.id.imgProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)

        return ProductoViewHolder(view)

    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvMarca.text = "Marca: ${producto.marca}"
        holder.tvPresentacion.text = "Presentación: ${producto.presentacion}"
        holder.tvExistencia.text = "Existencia: ${producto.existencia}"

        if (producto.imagenUrl.isNotEmpty()) {

            Glide.with(holder.itemView.context)
                .load(producto.imagenUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imgProducto)

        } else {

            holder.imgProducto.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        holder.itemView.setOnClickListener {
            onClick(producto)
        }
    }

    override fun getItemCount(): Int = productos.size
}