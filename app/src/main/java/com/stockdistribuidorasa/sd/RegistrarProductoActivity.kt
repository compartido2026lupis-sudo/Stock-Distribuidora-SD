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
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class RegistrarProductoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_producto)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val spMarca = findViewById<Spinner>(R.id.spMarca)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val etPresentacion = findViewById<EditText>(R.id.etPresentacion)
        val etCantidad = findViewById<EditText>(R.id.etCantidad)
        val etCodigo = findViewById<EditText>(R.id.etCodigo)
        val etImagenUrl = findViewById<EditText>(R.id.etImagenUrl)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarProducto)
        val btnEscanearCodigo = findViewById<Button>(R.id.btnEscanearCodigo)

        val db = FirebaseFirestore.getInstance()

        val opcionesEscaner = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(this, opcionesEscaner)

        btnEscanearCodigo.setOnClickListener {

            scanner.startScan()
                .addOnSuccessListener { barcode ->

                    val valorQR = barcode.rawValue

                    if (!valorQR.isNullOrEmpty()) {
                        etCodigo.setText(valorQR)

                        Toast.makeText(
                            this,
                            "Código QR leído correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnCanceledListener {

                    Toast.makeText(
                        this,
                        "Escaneo cancelado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "No se pudo leer el código QR",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Adaptador personalizado para que el texto del Spinner se vea oscuro
        fun crearAdapter(lista: List<String>): ArrayAdapter<String> {

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
                        super.getView(position, convertView, parent) as TextView

                    view.setTextColor(Color.parseColor("#243B6B"))
                    view.textSize = 16f

                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {

                    val view =
                        super.getDropDownView(position, convertView, parent) as TextView

                    view.setTextColor(Color.parseColor("#243B6B"))
                    view.textSize = 16f

                    return view
                }
            }
        }

        // -------------------------
        // CARGAR MARCAS
        // -------------------------

        val listaMarcas = mutableListOf("Selecciona una marca")

        db.collection("marcas")
            .get()
            .addOnSuccessListener { documentos ->

                listaMarcas.clear()
                listaMarcas.add("Selecciona una marca")

                for (documento in documentos) {

                    val nombre = documento.getString("nombre")

                    if (!nombre.isNullOrBlank()) {
                        listaMarcas.add(nombre)
                    }
                }

                val adapterMarcas = crearAdapter(listaMarcas)

                adapterMarcas.setDropDownViewResource(
                    R.layout.item_spinner_dropdown
                )

                spMarca.adapter = adapterMarcas
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al cargar las marcas",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // -------------------------
        // CARGAR CATEGORÍAS
        // -------------------------

        val listaCategorias = mutableListOf("Selecciona una categoría")

        db.collection("categorias")
            .get()
            .addOnSuccessListener { documentos ->

                listaCategorias.clear()
                listaCategorias.add("Selecciona una categoría")

                for (documento in documentos) {

                    val nombre = documento.getString("nombre")

                    if (!nombre.isNullOrBlank()) {
                        listaCategorias.add(nombre)
                    }
                }

                val adapterCategorias = crearAdapter(listaCategorias)

                adapterCategorias.setDropDownViewResource(
                    R.layout.item_spinner_dropdown
                )

                spCategoria.adapter = adapterCategorias
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al cargar las categorías",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // -------------------------
        // GUARDAR PRODUCTO
        // -------------------------

        btnGuardar.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val marca = spMarca.selectedItem?.toString() ?: ""
            val categoria = spCategoria.selectedItem?.toString() ?: ""
            val presentacion = etPresentacion.text.toString().trim()
            val cantidadTexto = etCantidad.text.toString().trim()
            val codigo = etCodigo.text.toString().trim()
            val imagenUrl = etImagenUrl.text.toString().trim()

            if (
                nombre.isEmpty() ||
                marca.isEmpty() ||
                categoria.isEmpty() ||
                marca == "Selecciona una marca" ||
                categoria == "Selecciona una categoría" ||
                presentacion.isEmpty() ||
                cantidadTexto.isEmpty() ||
                codigo.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Completa los campos obligatorios",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val cantidad = cantidadTexto.toLongOrNull()

            if (cantidad == null || cantidad < 0) {

                Toast.makeText(
                    this,
                    "Cantidad inválida",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val producto = Producto(
                nombre = nombre,
                marca = marca,
                categoria = categoria,
                presentacion = presentacion,
                existencia = cantidad,
                codigo = codigo,
                imagenUrl = imagenUrl
            )

            db.collection("productos")
                .whereEqualTo("codigo", codigo)
                .limit(1)
                .get()
                .addOnSuccessListener { documentos ->

                    if (!documentos.isEmpty) {

                        Toast.makeText(
                            this,
                            "Ya existe un producto con ese código",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addOnSuccessListener
                    }

                    db.collection("productos")
                        .add(producto)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Producto registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                this,
                                "Error al registrar producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
    }
}