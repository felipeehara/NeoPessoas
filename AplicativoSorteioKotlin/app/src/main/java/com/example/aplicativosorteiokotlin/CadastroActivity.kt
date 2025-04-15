package com.example.aplicativosorteiokotlin

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        dbHelper = MyDatabaseHelper(this)

        val editUsuario = findViewById<EditText>(R.id.editUsuarioCadastro)
        val editSenha = findViewById<EditText>(R.id.editSenhaCadastro)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener {
            val usuario = editUsuario.text.toString().trim()  // Corrigido
            val senha = editSenha.text.toString().trim()     // Corrigido

            if (usuario.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.inserirUsuario(usuario, senha)) {
                Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuário já existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
