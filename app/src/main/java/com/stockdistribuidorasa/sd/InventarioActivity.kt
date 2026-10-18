package com.stockdistribuidorasa.sd

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent

class InventarioActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        cargarProductos()
    }
    private lateinit var rvProductos: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private val listaProductos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        rvProductos = findViewById(R.id.rvProductos)

        rvProductos.layoutManager = LinearLayoutManager(this)

        productoAdapter = ProductoAdapter(listaProductos) { producto ->

            val intent = Intent(
                this,
                DetalleProductoActivity::class.java
            )

            intent.putExtra("nombre", producto.nombre)
            intent.putExtra("marca", producto.marca)
            intent.putExtra("categoria", producto.categoria)
            intent.putExtra("presentacion", producto.presentacion)
            intent.putExtra("existencia", producto.existencia)
            intent.putExtra("codigo", producto.codigo)

            startActivity(intent)
        }
        rvProductos.adapter = productoAdapter

        cargarProductos()
    }

    private fun cargarProductos() {

        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .get()
            .addOnSuccessListener { documentos ->

                listaProductos.clear()

                for (documento in documentos) {

                    val producto = documento.toObject(Producto::class.java)

                    listaProductos.add(producto)
                }

                productoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { error ->

                Log.e(
                    "FIRESTORE",
                    "Error al cargar productos",
                    error
                )
            }
    }
}