package com.stockdistribuidorasa.sd

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spMarca: Spinner
    private lateinit var spCategoria: Spinner
    private lateinit var etPresentacion: EditText
    private lateinit var etCodigo: EditText
    private lateinit var etImagenUrl: EditText

    private val db = FirebaseFirestore.getInstance()

    private var documentoId: String = ""
    private var marcaActual: String = ""
    private var categoriaActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        etNombre = findViewById(R.id.etEditarNombre)
        spMarca = findViewById(R.id.spEditarMarca)
        spCategoria = findViewById(R.id.spEditarCategoria)
        etPresentacion = findViewById(R.id.etEditarPresentacion)
        etCodigo = findViewById(R.id.etEditarCodigo)
        etImagenUrl = findViewById(R.id.etEditarImagenUrl)

        val btnGuardar =
            findViewById<Button>(R.id.btnGuardarCambios)

        val codigoOriginal =
            intent.getStringExtra("codigo") ?: ""

        if (codigoOriginal.isEmpty()) {
            Toast.makeText(
                this,
                "No se encontró el código del producto",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        cargarProducto(codigoOriginal)

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun crearAdapter(
        lista: List<String>
    ): ArrayAdapter<String> {

        return object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            lista
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view =
                    super.getView(
                        position,
                        convertView,
                        parent
                    ) as TextView

                view.setTextColor(
                    Color.parseColor("#243B6B")
                )

                view.textSize = 16f

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view =
                    super.getDropDownView(
                        position,
                        convertView,
                        parent
                    ) as TextView

                view.setTextColor(
                    Color.parseColor("#243B6B")
                )

                view.setBackgroundColor(
                    Color.WHITE
                )

                view.textSize = 16f

                return view
            }
        }
    }

    private fun cargarProducto(
        codigoOriginal: String
    ) {

        db.collection("productos")
            .whereEqualTo("codigo", codigoOriginal)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (documentos.isEmpty) {

                    Toast.makeText(
                        this,
                        "Producto no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                val documento =
                    documentos.documents[0]

                documentoId = documento.id

                val nombre =
                    documento.getString("nombre") ?: ""

                marcaActual =
                    documento.getString("marca") ?: ""

                categoriaActual =
                    documento.getString("categoria") ?: ""

                val presentacion =
                    documento.getString("presentacion") ?: ""

                val codigo =
                    documento.getString("codigo") ?: ""

                val imagenUrl =
                    documento.getString("imagenUrl") ?: ""

                etNombre.setText(nombre)
                etPresentacion.setText(presentacion)
                etCodigo.setText(codigo)
                etImagenUrl.setText(imagenUrl)

                cargarMarcas()
                cargarCategorias()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al cargar producto",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun cargarMarcas() {

        db.collection("marcas")
            .get()
            .addOnSuccessListener { documentos ->

                val listaMarcas =
                    mutableListOf<String>()

                for (documento in documentos) {

                    val nombre =
                        documento.getString("nombre")

                    if (!nombre.isNullOrBlank()) {
                        listaMarcas.add(nombre)
                    }
                }

                val adapter =
                    crearAdapter(listaMarcas)

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spMarca.adapter = adapter

                val posicion =
                    listaMarcas.indexOf(marcaActual)

                if (posicion >= 0) {
                    spMarca.setSelection(posicion)
                }
            }
    }

    private fun cargarCategorias() {

        db.collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->

                val listaCategorias =
                    mutableListOf<String>()

                for (documento in documentos) {

                    val nombre =
                        documento.getString("nombre")

                    if (!nombre.isNullOrBlank()) {
                        listaCategorias.add(nombre)
                    }
                }

                val adapter =
                    crearAdapter(listaCategorias)

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spCategoria.adapter = adapter

                val posicion =
                    listaCategorias.indexOf(categoriaActual)

                if (posicion >= 0) {
                    spCategoria.setSelection(posicion)
                }
            }
    }

    private fun guardarCambios() {

        val nombre =
            etNombre.text.toString().trim()

        val marca =
            spMarca.selectedItem?.toString() ?: ""

        val categoria =
            spCategoria.selectedItem?.toString() ?: ""

        val presentacion =
            etPresentacion.text.toString().trim()

        val codigo =
            etCodigo.text.toString().trim()

        val imagenUrl =
            etImagenUrl.text.toString().trim()

        if (
            nombre.isEmpty() ||
            marca.isEmpty() ||
            categoria.isEmpty() ||
            presentacion.isEmpty() ||
            codigo.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Completa los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (documentoId.isEmpty()) {

            Toast.makeText(
                this,
                "No se encontró el producto",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val cambios = hashMapOf<String, Any>(
            "nombre" to nombre,
            "marca" to marca,
            "categoria" to categoria,
            "presentacion" to presentacion,
            "codigo" to codigo,
            "imagenUrl" to imagenUrl
        )

        db.collection("productos")
            .whereEqualTo("codigo", codigo)
            .get()
            .addOnSuccessListener { documentos ->

                val existeOtroProducto =
                    documentos.documents.any {
                        it.id != documentoId
                    }

                if (existeOtroProducto) {

                    Toast.makeText(
                        this,
                        "Ya existe otro producto con ese código",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val cambios = hashMapOf<String, Any>(
                    "nombre" to nombre,
                    "marca" to marca,
                    "categoria" to categoria,
                    "presentacion" to presentacion,
                    "codigo" to codigo,
                    "imagenUrl" to imagenUrl
                )

                db.collection("productos")
                    .document(documentoId)
                    .update(cambios)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Producto actualizado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Error al actualizar producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }
}