package com.example.aplicativosorteiokotlin

import android.app.AlertDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var layoutPessoas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MyDatabaseHelper(this)
        layoutPessoas = findViewById(R.id.layoutPessoas)

        atualizarLista()

        val fabAdicionar = findViewById<Button>(R.id.fabAdicionar)

        fabAdicionar.setOnClickListener {
            abrirDialogCadastro()
        }

    }

    private fun abrirDialogCadastro() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val editNome = EditText(this).apply {
            hint = "Nome"
        }

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

        val editSexo = EditText(this).apply {
            hint = "Sexo"
        }

        layout.addView(editNome)
        layout.addView(editIdade)
        layout.addView(editEmail)
        layout.addView(editCpf)
        layout.addView(editSexo)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nova Pessoa")
            .setView(layout)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val botao = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            botao.setOnClickListener {
                val nome = editNome.text.toString().trim()
                val idadeStr = editIdade.text.toString().trim()
                val email = editEmail.text.toString().trim()
                val cpf = editCpf.text.toString().trim()
                val sexo = editSexo.text.toString().trim()

                when {
                    nome.isEmpty() -> {
                        editNome.error = "Digite um nome"
                    }
                    idadeStr.toIntOrNull() == null -> {
                        editIdade.error = "Idade inválida"
                    }
                    email.isEmpty() -> {
                        editEmail.error = "Digite um email"
                    }
                    cpf.isEmpty() -> {
                        editCpf.error = "Digite um CPF"
                    }
                    sexo.isEmpty() -> {
                        editSexo.error = "Digite o sexo"
                    }
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

    private fun atualizarLista() {
        val pessoas = dbHelper.listarPessoas()
        layoutPessoas.removeAllViews()

        if (pessoas.isEmpty()) {
            val textoVazio = TextView(this).apply {
                text = "Nenhuma pessoa cadastrada."
                textSize = 16f
                setPadding(0, 16, 0, 0)
            }
            layoutPessoas.addView(textoVazio)
            return
        }

        pessoas.forEach { pessoa ->
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
            }

            val texto = TextView(this).apply {
                // Supondo que 'pessoa' tenha as propriedades 'nome' e 'idade'
                text = "Nome: ${pessoa.nome}, Idade: ${pessoa.idade}"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val btnEditar = Button(this).apply {
                text = "✏️"
                setOnClickListener {
                    // Agora estamos passando todos os parâmetros necessários para edição
                    abrirDialogEdicao(
                        pessoa.nome,
                        pessoa.idade,
                        pessoa.email,
                        pessoa.cpf,
                        pessoa.sexo
                    )
                }
            }

            val btnDeletar = Button(this).apply {
                text = "🗑️"
                setOnClickListener {
                    // Ao deletar, passamos 'nome' de pessoa, que é único
                    dbHelper.deletarPessoa(pessoa.nome)
                    atualizarLista()
                }
            }

            itemLayout.addView(texto)
            itemLayout.addView(btnEditar)
            itemLayout.addView(btnDeletar)
            layoutPessoas.addView(itemLayout)
        }
    }


        private fun abrirDialogEdicao(nomeAtual: String, idadeAtual: Int, emailAtual: String, cpfAtual: String, sexoAtual: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        // Adicionando campos para edição
        val editNome = EditText(this).apply {
            hint = "Nome"
            setText(nomeAtual)
        }

        val editIdade = EditText(this).apply {
            hint = "Idade"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(idadeAtual.toString())
        }

        val editEmail = EditText(this).apply {
            hint = "Email"
            setText(emailAtual)
        }

        val editCpf = EditText(this).apply {
            hint = "CPF"
            setText(cpfAtual)
        }

        val editSexo = EditText(this).apply {
            hint = "Sexo"
            setText(sexoAtual)
        }

        layout.addView(editNome)
        layout.addView(editIdade)
        layout.addView(editEmail)
        layout.addView(editCpf)
        layout.addView(editSexo)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Pessoa")
            .setView(layout)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val botao = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            botao.setOnClickListener {
                val novoNome = editNome.text.toString().trim()
                val novaIdadeStr = editIdade.text.toString().trim()
                val novoEmail = editEmail.text.toString().trim()
                val novoCpf = editCpf.text.toString().trim()
                val novoSexo = editSexo.text.toString().trim()

                when {
                    novoNome.isEmpty() -> {
                        editNome.error = "Digite um nome"
                    }
                    novaIdadeStr.toIntOrNull() == null -> {
                        editIdade.error = "Idade inválida"
                    }
                    novoEmail.isEmpty() -> {
                        editEmail.error = "Digite um email"
                    }
                    novoCpf.isEmpty() -> {
                        editCpf.error = "Digite um CPF"
                    }
                    novoSexo.isEmpty() -> {
                        editSexo.error = "Digite o sexo"
                    }
                    else -> {
                        val novaIdade = novaIdadeStr.toInt()
                        // Atualizando pessoa com os novos dados
                        if (dbHelper.atualizarPessoa(nomeAtual, novoNome, novaIdade, novoEmail, novoCpf, novoSexo)) {
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
