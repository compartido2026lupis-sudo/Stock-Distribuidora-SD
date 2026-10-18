package com.stockdistribuidorasa.sd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistorialActivity : AppCompatActivity() {

    private val listaMovimientos = mutableListOf<Movimiento>()
    private lateinit var adapter: MovimientoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val rvMovimientos =
            findViewById<RecyclerView>(R.id.rvMovimientos)

        rvMovimientos.layoutManager =
            LinearLayoutManager(this)

        adapter = MovimientoAdapter(listaMovimientos)

        rvMovimientos.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        cargarMovimientos()
    }

    private fun cargarMovimientos() {

        val db = FirebaseFirestore.getInstance()

        db.collection("movimientos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documentos ->

                listaMovimientos.clear()

                for (documento in documentos) {

                    val movimiento =
                        documento.toObject(Movimiento::class.java)

                    listaMovimientos.add(movimiento)
                }

                adapter.notifyDataSetChanged()
            }
    }
}