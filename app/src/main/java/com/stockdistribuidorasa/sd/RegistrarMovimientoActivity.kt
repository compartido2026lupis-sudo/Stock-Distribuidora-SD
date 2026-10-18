package com.stockdistribuidorasa.sd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarMovimientoActivity : AppCompatActivity() {

    private lateinit var tvExistencia: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_movimiento)

        val tvProducto =
            findViewById<TextView>(R.id.tvProductoMovimiento)

        val tvPresentacion =
            findViewById<TextView>(R.id.tvPresentacionMovimiento)

        tvExistencia =
            findViewById(R.id.tvExistenciaMovimiento)

        val rgTipo =
            findViewById<RadioGroup>(R.id.rgTipoMovimiento)

        val rbEntrada =
            findViewById<RadioButton>(R.id.rbEntrada)

        val rbSalida =
            findViewById<RadioButton>(R.id.rbSalida)

        val etCantidad =
            findViewById<EditText>(R.id.etCantidadMovimiento)

        val btnConfirmar =
            findViewById<Button>(R.id.btnConfirmarMovimiento)

        val nombre =
            intent.getStringExtra("nombre") ?: ""

        val presentacion =
            intent.getStringExtra("presentacion") ?: ""

        val codigo =
            intent.getStringExtra("codigo") ?: ""

        val tipoInicial =
            intent.getStringExtra("tipo") ?: ""

        tvProducto.text = nombre
        tvPresentacion.text = "Presentación: $presentacion"

        if (tipoInicial == "entrada") {
            rbEntrada.isChecked = true
        } else if (tipoInicial == "salida") {
            rbSalida.isChecked = true
        }

        val db = FirebaseFirestore.getInstance()

        // Obtener existencia REAL desde Firestore al abrir
        cargarExistenciaActual(db, codigo)

        btnConfirmar.setOnClickListener {

            val cantidadTexto =
                etCantidad.text.toString().trim()

            if (cantidadTexto.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresa una cantidad",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val cantidad =
                cantidadTexto.toLongOrNull()

            if (cantidad == null || cantidad <= 0) {

                Toast.makeText(
                    this,
                    "Cantidad inválida",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val tipo = when (rgTipo.checkedRadioButtonId) {

                R.id.rbEntrada -> "entrada"

                R.id.rbSalida -> "salida"

                else -> ""
            }

            if (tipo.isEmpty()) {

                Toast.makeText(
                    this,
                    "Selecciona entrada o salida",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Buscar producto por código
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

                    val referenciaProducto =
                        documentos.documents[0].reference

                    val referenciaMovimiento =
                        db.collection("movimientos").document()

                    // TRANSACCIÓN
                    db.runTransaction { transaction ->

                        val snapshot =
                            transaction.get(referenciaProducto)

                        val existenciaActual =
                            snapshot.getLong("existencia") ?: 0L

                        if (
                            tipo == "salida" &&
                            cantidad > existenciaActual
                        ) {
                            throw Exception(
                                "No hay suficientes existencias"
                            )
                        }

                        val nuevaExistencia =
                            if (tipo == "entrada") {

                                existenciaActual + cantidad

                            } else {

                                existenciaActual - cantidad
                            }

                        // Actualizar producto
                        transaction.update(
                            referenciaProducto,
                            "existencia",
                            nuevaExistencia
                        )

                        // Registrar movimiento
                        val movimiento = hashMapOf(
                            "producto" to nombre,
                            "codigo" to codigo,
                            "tipo" to tipo,
                            "cantidad" to cantidad,
                            "fecha" to Timestamp.now()
                        )

                        transaction.set(
                            referenciaMovimiento,
                            movimiento
                        )

                        nuevaExistencia
                    }
                        .addOnSuccessListener { nuevaExistencia ->

                            tvExistencia.text =
                                "Existencia actual: $nuevaExistencia unidades"

                            etCantidad.text.clear()

                            Toast.makeText(
                                this,
                                "Movimiento registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { error ->

                            Toast.makeText(
                                this,
                                error.message
                                    ?: "Error al registrar movimiento",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
    }

    private fun cargarExistenciaActual(
        db: FirebaseFirestore,
        codigo: String
    ) {

        db.collection("productos")
            .whereEqualTo("codigo", codigo)
            .limit(1)
            .get()
            .addOnSuccessListener { documentos ->

                if (!documentos.isEmpty) {

                    val existencia =
                        documentos.documents[0]
                            .getLong("existencia") ?: 0L

                    tvExistencia.text =
                        "Existencia actual: $existencia unidades"
                }
            }
    }
}