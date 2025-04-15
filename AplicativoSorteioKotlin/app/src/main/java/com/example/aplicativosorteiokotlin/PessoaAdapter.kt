package com.example.aplicativosorteiokotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PessoaAdapter(
    private var pessoas: List<Pessoa>,
    private val onEditarClick: (Pessoa) -> Unit,
    private val onDeletarClick: (Pessoa) -> Unit
) : RecyclerView.Adapter<PessoaAdapter.PessoaViewHolder>() {

    class PessoaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtInfo: TextView = itemView.findViewById(R.id.txtInfo)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnDeletar: Button = itemView.findViewById(R.id.btnDeletar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PessoaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pessoa, parent, false)
        return PessoaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PessoaViewHolder, position: Int) {
        val pessoa = pessoas[position]
        holder.txtInfo.text = "Nome: ${pessoa.nome}, Idade: ${pessoa.idade}"
        holder.btnEditar.setOnClickListener { onEditarClick(pessoa) }
        holder.btnDeletar.setOnClickListener { onDeletarClick(pessoa) }
    }

    override fun getItemCount(): Int = pessoas.size

    fun atualizarLista(novaLista: List<Pessoa>) {
        pessoas = novaLista
        notifyDataSetChanged()
    }
}
