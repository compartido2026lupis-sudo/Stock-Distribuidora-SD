package com.stockdistribuidorasa.sd

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {

            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingresa correo y contraseña",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Inicio de sesión correcto",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this, InicioActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {

                        Toast.makeText(
                            this,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}