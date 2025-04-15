package com.example.aplicativosorteiokotlin

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PessoaAdapter
    private lateinit var searchEditText: EditText
    private var listaOriginal = listOf<Pessoa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MyDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PessoaAdapter(
            listOf(),
            { pessoa -> abrirDialogEdicao(pessoa) },
            { pessoa ->
                dbHelper.deletarPessoa(pessoa.nome)
                atualizarLista()
            }
        )
        recyclerView.adapter = adapter

        atualizarLista()

        val fabAdicionar = findViewById<Button>(R.id.fabAdicionar)
        fabAdicionar.setOnClickListener {
            abrirDialogCadastro()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filtrada = listaOriginal.filter { it.nome.contains(query, ignoreCase = true) }
                adapter.atualizarLista(filtrada)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun atualizarLista() {
        listaOriginal = dbHelper.listarPessoas()
        adapter.atualizarLista(listaOriginal)
    }

    private fun abrirDialogCadastro() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val editNome = EditText(this).apply { hint = "Nome" }
        val editIdade = EditText(this).apply {
            hint = "Idade"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val editEmail = EditText(this).apply {
            hint = "Email"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val editCpf = EditText(this).apply {
            hint = "CPF"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val editSexo = EditText(this).apply { hint = "Sexo" }

        layout.apply {
            addView(editNome)
            addView(editIdade)
            addView(editEmail)
            addView(editCpf)
            addView(editSexo)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nova Pessoa")
            .setView(layout)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nome = editNome.text.toString().trim()
                val idadeStr = editIdade.text.toString().trim()
                val email = editEmail.text.toString().trim()
                val cpf = editCpf.text.toString().trim()
                val sexo = editSexo.text.toString().trim()

                when {
                    nome.isEmpty() -> editNome.error = "Digite um nome"
                    idadeStr.toIntOrNull() == null -> editIdade.error = "Idade inválida"
                    email.isEmpty() -> editEmail.error = "Digite um email"
                    cpf.isEmpty() -> editCpf.error = "Digite um CPF"
                    sexo.isEmpty() -> editSexo.error = "Digite o sexo"
                    else -> {
                        val idade = idadeStr.toInt()
                        if (dbHelper.inserirPessoa(nome, idade, email, cpf, sexo)) {
                            Toast.makeText(this, "Pessoa salva com sucesso!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            atualizarLista()
                        } else {
                            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun abrirDialogEdicao(pessoa: Pessoa) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val editNome = EditText(this).apply {
            hint = "Nome"
            setText(pessoa.nome)
        }
        val editIdade = EditText(this).apply {
            hint = "Idade"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(pessoa.idade.toString())
        }
        val editEmail = EditText(this).apply {
            hint = "Email"
            setText(pessoa.email)
        }
        val editCpf = EditText(this).apply {
            hint = "CPF"
            setText(pessoa.cpf)
        }
        val editSexo = EditText(this).apply {
            hint = "Sexo"
            setText(pessoa.sexo)
        }

        layout.apply {
            addView(editNome)
            addView(editIdade)
            addView(editEmail)
            addView(editCpf)
            addView(editSexo)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Pessoa")
            .setView(layout)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val novoNome = editNome.text.toString().trim()
                val novaIdadeStr = editIdade.text.toString().trim()
                val novoEmail = editEmail.text.toString().trim()
                val novoCpf = editCpf.text.toString().trim()
                val novoSexo = editSexo.text.toString().trim()

                when {
                    novoNome.isEmpty() -> editNome.error = "Digite um nome"
                    novaIdadeStr.toIntOrNull() == null -> editIdade.error = "Idade inválida"
                    novoEmail.isEmpty() -> editEmail.error = "Digite um email"
                    novoCpf.isEmpty() -> editCpf.error = "Digite um CPF"
                    novoSexo.isEmpty() -> editSexo.error = "Digite o sexo"
                    else -> {
                        val novaIdade = novaIdadeStr.toInt()
                        if (dbHelper.atualizarPessoa(
                                pessoa.nome, novoNome, novaIdade, novoEmail, novoCpf, novoSexo
                            )
                        ) {
                            Toast.makeText(this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            atualizarLista()
                        } else {
                            Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }
}
