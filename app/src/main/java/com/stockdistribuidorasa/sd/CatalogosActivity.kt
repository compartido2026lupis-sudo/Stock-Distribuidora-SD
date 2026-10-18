package com.stockdistribuidorasa.sd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CatalogosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogos)

        val btnMarcas =
            findViewById<Button>(R.id.btnMarcas)

        val btnCategorias =
            findViewById<Button>(R.id.btnCategorias)

        btnMarcas.setOnClickListener {

            val intent = Intent(
                this,
                MarcasActivity::class.java
            )

            startActivity(intent)
        }

        btnCategorias.setOnClickListener {

            val intent = Intent(
                this,
                CategoriasActivity::class.java
            )

            startActivity(intent)
        }
    }
}