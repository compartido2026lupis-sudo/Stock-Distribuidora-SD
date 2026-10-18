package com.stockdistribuidorasa.sd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnEscanearQR = findViewById<Button>(R.id.btnEscanearQR)
        val btnInventario = findViewById<Button>(R.id.btnInventario)
        val btnRegistrarProducto =
            findViewById<Button>(R.id.btnRegistrarProducto)
        val btnHistorial = findViewById<Button>(R.id.btnHistorial)
        val btnCatalogos = findViewById<Button>(R.id.btnCatalogos)

        // -------------------------
        // INVENTARIO
        // -------------------------

        btnCatalogos.setOnClickListener {

            val intent = Intent(
                this,
                CatalogosActivity::class.java
            )

            startActivity(intent)
        }



        btnInventario.setOnClickListener {

            val intent = Intent(
                this,
                InventarioActivity::class.java
            )

            startActivity(intent)
        }

        // -------------------------
        // REGISTRAR PRODUCTO
        // -------------------------

        btnRegistrarProducto.setOnClickListener {

            val intent = Intent(
                this,
                RegistrarProductoActivity::class.java
            )

            startActivity(intent)
        }

        // -------------------------
        // HISTORIAL
        // -------------------------

        btnHistorial.setOnClickListener {

            val intent = Intent(
                this,
                HistorialActivity::class.java
            )

            startActivity(intent)
        }

        // -------------------------
        // ESCANEAR QR
        // -------------------------

        val opcionesEscaner =
            GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build()

        val scanner =
            GmsBarcodeScanning.getClient(
                this,
                opcionesEscaner
            )

        btnEscanearQR.setOnClickListener {

            scanner.startScan()
                .addOnSuccessListener { barcode ->

                    val codigo = barcode.rawValue

                    if (codigo.isNullOrEmpty()) {

                        Toast.makeText(
                            this,
                            "Código QR inválido",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addOnSuccessListener
                    }

                    buscarProductoPorCodigo(codigo)
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
    }

    private fun buscarProductoPorCodigo(codigo: String) {

        val db = FirebaseFirestore.getInstance()

        db.collection("productos")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (documentos.isEmpty) {

                    Toast.makeText(
                        this,
                        "Producto no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val documento =
                    documentos.documents[0]

                val intent = Intent(
                    this,
                    DetalleProductoActivity::class.java
                )

                intent.putExtra(
                    "nombre",
                    documento.getString("nombre") ?: ""
                )

                intent.putExtra(
                    "marca",
                    documento.getString("marca") ?: ""
                )

                intent.putExtra(
                    "categoria",
                    documento.getString("categoria") ?: ""
                )

                intent.putExtra(
                    "presentacion",
                    documento.getString("presentacion") ?: ""
                )

                intent.putExtra(
                    "existencia",
                    documento.getLong("existencia") ?: 0L
                )

                intent.putExtra(
                    "codigo",
                    documento.getString("codigo") ?: ""
                )

                startActivity(intent)
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al buscar el producto",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }


}