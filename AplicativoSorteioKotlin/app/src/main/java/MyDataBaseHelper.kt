package com.example.aplicativosorteiokotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val DATABASE_NAME = "app_database.db"
private const val DATABASE_VERSION = 3

// Tabela de usuários
private const val TABLE_USERS = "usuarios"
private const val COLUMN_USER_ID = "id"
private const val COLUMN_USERNAME = "usuario"
private const val COLUMN_PASSWORD = "senha"

// Tabela de pessoas
private const val TABLE_PEOPLE = "pessoas"
private const val COLUMN_NAME = "nome"
private const val COLUMN_AGE = "idade"

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Criação da tabela de usuários
        val createUsersTable = """
        CREATE TABLE usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario TEXT,
            senha TEXT
        )
    """.trimIndent()
        db.execSQL(createUsersTable)

        // Criação da tabela de pessoas
        val createPeopleTable = """
        CREATE TABLE pessoas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT,
            idade INTEGER,
            email TEXT,
            cpf TEXT,
            sexo TEXT
        )
    """.trimIndent()
        db.execSQL(createPeopleTable)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Verifica se a coluna 'email' existe, se não, adiciona a coluna
            try {
                db.execSQL("ALTER TABLE pessoas ADD COLUMN email TEXT")
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Erro ao atualizar banco de dados: ${e.message}")
            }
        }
    }


    // Operações para usuários
    fun inserirUsuario(usuario: String, senha: String): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, usuario)
                put(COLUMN_PASSWORD, senha)
            }
            db.insert(TABLE_USERS, null, values) != -1L
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Erro ao inserir usuário: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    fun verificarLogin(usuario: String, senha: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(usuario, senha)
        )
        return try {
            cursor.count > 0
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Erro ao verificar login: ${e.message}")
            false
        } finally {
            cursor.close()
            db.close()
        }
    }

    // Operações para pessoas (mantidas do seu código original)
    fun inserirPessoa(nome: String, idade: Int, email: String, cpf: String, sexo: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", nome)
            put("idade", idade)
            put("email", email)
            put("cpf", cpf)
            put("sexo", sexo)
        }
        return db.insert("pessoas", null, values) > 0
    }


    fun listarPessoas(): List<Pessoa> {
        val lista = mutableListOf<Pessoa>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pessoas", null)
        if (cursor.moveToFirst()) {
            do {
                val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                val idade = cursor.getInt(cursor.getColumnIndexOrThrow("idade"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"))
                val sexo = cursor.getString(cursor.getColumnIndexOrThrow("sexo"))
                lista.add(Pessoa(nome, idade, email, cpf, sexo))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }


    fun deletarPessoa(nome: String): Boolean {
        val db = writableDatabase
        return db.delete("pessoas", "nome = ?", arrayOf(nome)) > 0
    }


    fun atualizarPessoa(nomeAntigo: String, novoNome: String, novaIdade: Int, email: String, cpf: String, sexo: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", novoNome)
            put("idade", novaIdade)
            put("email", email)
            put("cpf", cpf)
            put("sexo", sexo)
        }
        return db.update("pessoas", values, "nome = ?", arrayOf(nomeAntigo)) > 0
    }

}