package com.stockdistribuidorasa.sd

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class MovimientoAdapter(
    private val movimientos: List<Movimiento>
) : RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    class MovimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProducto: TextView =
            view.findViewById(R.id.tvProductoMovimientoItem)

        val tvTipo: TextView =
            view.findViewById(R.id.tvTipoMovimientoItem)

        val tvCantidad: TextView =
            view.findViewById(R.id.tvCantidadMovimientoItem)

        val tvFecha: TextView =
            view.findViewById(R.id.tvFechaMovimientoItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovimientoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)

        return MovimientoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MovimientoViewHolder,
        position: Int
    ) {

        val movimiento = movimientos[position]

        holder.tvProducto.text = movimiento.producto

        if (movimiento.tipo == "entrada") {
            holder.tvTipo.text = "+ Entrada"
            holder.tvTipo.setTextColor(Color.parseColor("#16A34A"))
        } else {
            holder.tvTipo.text = "- Salida"
            holder.tvTipo.setTextColor(Color.parseColor("#DC2626"))
        }

        holder.tvCantidad.text =
            "Cantidad: ${movimiento.cantidad} unidades"

        val fecha = movimiento.fecha?.toDate()

        if (fecha != null) {

            val formato =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            holder.tvFecha.text = formato.format(fecha)

        } else {

            holder.tvFecha.text = ""
        }
    }

    override fun getItemCount(): Int = movimientos.size
}