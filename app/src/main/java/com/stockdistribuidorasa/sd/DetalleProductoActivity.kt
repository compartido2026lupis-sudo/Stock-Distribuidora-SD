package com.stockdistribuidorasa.sd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvMarca: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvPresentacion: TextView
    private lateinit var tvExistencia: TextView

    private lateinit var imgProducto: ImageView

    private var codigo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        tvNombre = findViewById(R.id.tvNombreDetalle)
        tvMarca = findViewById(R.id.tvMarcaDetalle)
        tvCategoria = findViewById(R.id.tvCategoriaDetalle)
        tvPresentacion = findViewById(R.id.tvPresentacionDetalle)
        tvExistencia = findViewById(R.id.tvExistenciaDetalle)
        imgProducto = findViewById(R.id.imgProductoDetalle)

        val btnEntrada = findViewById<Button>(R.id.btnRegistrarEntrada)
        val btnSalida = findViewById<Button>(R.id.btnRegistrarSalida)

        val btnEditar =
            findViewById<Button>(R.id.btnEditarProducto)

        val btnEliminar =
            findViewById<Button>(R.id.btnEliminarProducto)

        btnEditar.setOnClickListener {

            val intent = Intent(
                this,
                EditarProductoActivity::class.java
            )

            intent.putExtra("codigo", codigo)

            startActivity(intent)
        }

        btnEliminar.setOnClickListener {

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar este producto?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar") { _, _ ->

                    val db = FirebaseFirestore.getInstance()

                    db.collection("productos")
                        .whereEqualTo("codigo", codigo)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { documentos ->

                            if (!documentos.isEmpty) {

                                documentos.documents[0].reference
                                    .delete()
                                    .addOnSuccessListener {

                                        Toast.makeText(
                                            this,
                                            "Producto eliminado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        finish()
                                    }
                            }
                        }
                }
                .show()
        }

        codigo = intent.getStringExtra("codigo") ?: ""

        btnEntrada.setOnClickListener {
            abrirMovimiento("entrada")
        }

        btnSalida.setOnClickListener {
            abrirMovimiento("salida")
        }
    }

    override fun onResume() {
        super.onResume()

        cargarDatosActuales()
    }

    private fun cargarDatosActuales() {

        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (!documentos.isEmpty) {

                    val documento = documentos.documents[0]

                    val nombre = documento.getString("nombre") ?: ""
                    val marca = documento.getString("marca") ?: ""
                    val categoria = documento.getString("categoria") ?: ""
                    val presentacion = documento.getString("presentacion") ?: ""
                    val existencia = documento.getLong("existencia") ?: 0L
                    val imagenUrl = documento.getString("imagenUrl") ?: ""

                    if (imagenUrl.isNotEmpty()) {

                        Glide.with(this)
                            .load(imagenUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imgProducto)

                    } else {

                        imgProducto.setImageResource(
                            android.R.drawable.ic_menu_gallery
                        )
                    }

                    tvNombre.text = nombre
                    tvMarca.text = "Marca: $marca"
                    tvCategoria.text = "Categoría: $categoria"
                    tvPresentacion.text = "Presentación: $presentacion"
                    tvExistencia.text =
                        "Existencia actual: $existencia unidades"
                }
            }
    }

    private fun abrirMovimiento(tipo: String) {

        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (!documentos.isEmpty) {

                    val documento = documentos.documents[0]

                    val nombre = documento.getString("nombre") ?: ""
                    val presentacion =
                        documento.getString("presentacion") ?: ""

                    val existencia =
                        documento.getLong("existencia") ?: 0L

                    val intent = Intent(
                        this,
                        RegistrarMovimientoActivity::class.java
                    )

                    intent.putExtra("nombre", nombre)
                    intent.putExtra("presentacion", presentacion)
                    intent.putExtra("existencia", existencia)
                    intent.putExtra("codigo", codigo)
                    intent.putExtra("tipo", tipo)

                    startActivity(intent)
                }
            }
    }
}