package br.aula.agenda.db

import java.io.Serializable

data class Sorteio(
        var id: Int = 0,
        var vencedor: String? = null,
        var qtd: Int? = null,
        var nome: String? = null,
        var data: String? = null,
        var participantes: String? = null) : Serializable {

    override fun toString(): String {
        return nome.toString()
    }
}