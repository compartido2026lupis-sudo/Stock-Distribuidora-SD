package com.stockdistribuidorasa.sd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var etNuevaCategoria: EditText
    private lateinit var adapter: CategoriaAdapter

    private val listaCategorias = mutableListOf<Categoria>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        etNuevaCategoria =
            findViewById(R.id.etNuevaCategoria)

        val btnAgregar =
            findViewById<Button>(R.id.btnAgregarCategoria)

        val rvCategorias =
            findViewById<RecyclerView>(R.id.rvCategorias)

        rvCategorias.layoutManager =
            LinearLayoutManager(this)

        adapter = CategoriaAdapter(
            listaCategorias,
            onEditar = { categoria ->
                mostrarDialogoEditar(categoria)
            },
            onEliminar = { categoria ->
                confirmarEliminar(categoria)
            }
        )

        rvCategorias.adapter = adapter

        btnAgregar.setOnClickListener {
            agregarCategoria()
        }

        cargarCategorias()
    }

    private fun cargarCategorias() {

        db.collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->

                listaCategorias.clear()

                for (documento in documentos) {

                    val nombre =
                        documento.getString("nombre") ?: ""

                    listaCategorias.add(
                        Categoria(
                            id = documento.id,
                            nombre = nombre
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun agregarCategoria() {

        val nombre =
            etNuevaCategoria.text.toString().trim()

        if (nombre.isEmpty()) {

            Toast.makeText(
                this,
                "Escribe una categoría",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        db.collection("categorias")
            .whereEqualTo("nombre", nombre)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (!documentos.isEmpty) {

                    Toast.makeText(
                        this,
                        "Esa categoría ya existe",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val datos = hashMapOf(
                    "nombre" to nombre
                )

                db.collection("categorias")
                    .add(datos)
                    .addOnSuccessListener {

                        etNuevaCategoria.text.clear()

                        Toast.makeText(
                            this,
                            "Categoría agregada",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarCategorias()
                    }
            }
    }

    private fun mostrarDialogoEditar(
        categoria: Categoria
    ) {

        val input = EditText(this)
        input.setText(categoria.nombre)

        AlertDialog.Builder(this)
            .setTitle("Editar categoría")
            .setView(input)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar") { _, _ ->

                val nuevoNombre =
                    input.text.toString().trim()

                if (nuevoNombre.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Escribe un nombre",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                db.collection("categorias")
                    .whereEqualTo("nombre", nuevoNombre)
                    .get()
                    .addOnSuccessListener { documentos ->

                        val existeOtra =
                            documentos.documents.any {
                                it.id != categoria.id
                            }

                        if (existeOtra) {

                            Toast.makeText(
                                this,
                                "Ya existe una categoría con ese nombre",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            db.collection("categorias")
                                .document(categoria.id)
                                .update(
                                    "nombre",
                                    nuevoNombre
                                )
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Categoría actualizada",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    cargarCategorias()
                                }
                        }
                    }
            }
            .show()
    }

    private fun confirmarEliminar(
        categoria: Categoria
    ) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar categoría")
            .setMessage(
                "¿Deseas eliminar ${categoria.nombre}?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton("Eliminar") { _, _ ->

                db.collection("categorias")
                    .document(categoria.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Categoría eliminada",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarCategorias()
                    }
            }
            .show()
    }
}