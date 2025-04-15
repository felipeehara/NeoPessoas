package com.example.aplicativosorteiokotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = MyDatabaseHelper(this) // Inicialização adicionada

        val editUsuario = findViewById<EditText>(R.id.editUsuario)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnIrCadastro = findViewById<Button>(R.id.btnIrCadastro)

        btnLogin.setOnClickListener {
            try {
                val usuario = editUsuario.text.toString().trim()
                val senha = editSenha.text.toString().trim()

                when {
                    usuario.isEmpty() || senha.isEmpty() -> {
                        Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                    dbHelper.verificarLogin(usuario, senha) -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Erro no login: ${e.message}")
                Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
            }
        }

        btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}