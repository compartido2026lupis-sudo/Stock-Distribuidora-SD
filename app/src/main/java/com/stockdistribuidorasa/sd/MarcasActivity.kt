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

class MarcasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var etNuevaMarca: EditText
    private lateinit var adapter: MarcaAdapter

    private val listaMarcas = mutableListOf<Marca>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcas)

        etNuevaMarca = findViewById(R.id.etNuevaMarca)

        val btnAgregar =
            findViewById<Button>(R.id.btnAgregarMarca)

        val rvMarcas =
            findViewById<RecyclerView>(R.id.rvMarcas)

        rvMarcas.layoutManager =
            LinearLayoutManager(this)

        adapter = MarcaAdapter(
            listaMarcas,
            onEditar = { marca ->
                mostrarDialogoEditar(marca)
            },
            onEliminar = { marca ->
                confirmarEliminar(marca)
            }
        )

        rvMarcas.adapter = adapter

        btnAgregar.setOnClickListener {
            agregarMarca()
        }

        cargarMarcas()
    }

    private fun cargarMarcas() {

        db.collection("marcas")
            .get()
            .addOnSuccessListener { documentos ->

                listaMarcas.clear()

                for (documento in documentos) {

                    val nombre =
                        documento.getString("nombre") ?: ""

                    listaMarcas.add(
                        Marca(
                            id = documento.id,
                            nombre = nombre
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun agregarMarca() {

        val nombre =
            etNuevaMarca.text.toString().trim()

        if (nombre.isEmpty()) {

            Toast.makeText(
                this,
                "Escribe una marca",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        db.collection("marcas")
            .whereEqualTo("nombre", nombre)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (!documentos.isEmpty) {

                    Toast.makeText(
                        this,
                        "Esa marca ya existe",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val datos = hashMapOf(
                    "nombre" to nombre
                )

                db.collection("marcas")
                    .add(datos)
                    .addOnSuccessListener {

                        etNuevaMarca.text.clear()

                        Toast.makeText(
                            this,
                            "Marca agregada",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarMarcas()
                    }
            }
    }

    private fun mostrarDialogoEditar(
        marca: Marca
    ) {

        val input = EditText(this)
        input.setText(marca.nombre)

        AlertDialog.Builder(this)
            .setTitle("Editar marca")
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

                db.collection("marcas")
                    .whereEqualTo("nombre", nuevoNombre)
                    .get()
                    .addOnSuccessListener { documentos ->

                        val existeOtra =
                            documentos.documents.any {
                                it.id != marca.id
                            }

                        if (existeOtra) {

                            Toast.makeText(
                                this,
                                "Ya existe una marca con ese nombre",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            db.collection("marcas")
                                .document(marca.id)
                                .update(
                                    "nombre",
                                    nuevoNombre
                                )
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "Marca actualizada",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    cargarMarcas()
                                }
                        }
                    }
            }
            .show()
    }

    private fun confirmarEliminar(
        marca: Marca
    ) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar marca")
            .setMessage(
                "¿Deseas eliminar ${marca.nombre}?"
            )
            .setNegativeButton(
                "Cancelar",
                null
            )
            .setPositiveButton("Eliminar") { _, _ ->

                db.collection("marcas")
                    .document(marca.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Marca eliminada",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarMarcas()
                    }
            }
            .show()
    }
}