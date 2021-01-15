package br.aula.agenda.db

import android.content.Context
import br.aula.agenda.SORTEIOS_TABLE_NAME
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.*
import timber.log.Timber

class SorteioRepository(val context: Context) {

    fun isSorteio(telefone: String) : Boolean = context.database.use {
        select(SORTEIOS_TABLE_NAME, "count(*) as total")
                .whereArgs("nome = {nome}","nome" to telefone)
        .parseSingle(object: MapRowParser<Boolean> {
            override fun parseRow(columns: Map<String, Any?>): Boolean {
                val total = columns.getValue("total")
                return total.toString().toInt() > 0;
            }
        })
    }

    fun findAll() : ArrayList<Sorteio> = context.database.use {
        val sorteios = ArrayList<Sorteio>()

        select(SORTEIOS_TABLE_NAME, "id", "vencedor", "qtd", "nome", "data", "participantes")
                .parseList(object: MapRowParser<List<Sorteio>> {
                    override fun parseRow(columns: Map<String, Any?>): List<Sorteio> {
                        val id = columns.getValue("id")
                        val vencedor = columns.getValue("vencedor")
                        val qtd = columns.getValue("qtd")
                        val nome = columns.getValue("nome")
                        val data = columns.getValue("data")
                        val participantes = columns.getValue("participantes")

                        val sorteio = Sorteio(
                                id.toString()?.toInt(),
                                vencedor?.toString(),
                                qtd.toString()?.toInt(),
                                nome?.toString(),
                                data?.toString(),
                                participantes?.toString())
                        sorteios.add(sorteio)
                        return sorteios
                    }
                })

        sorteios
    }

    fun create(sorteio: Sorteio) = context.database.use {
        insert(SORTEIOS_TABLE_NAME,
                "vencedor" to sorteio.vencedor,
                "nome" to sorteio.nome,
                "qtd" to sorteio.qtd,
                "data" to sorteio.data,
                "participantes" to sorteio.participantes)
    }

    fun update(sorteio: Sorteio) = context.database.use {
        val updateResult = update(SORTEIOS_TABLE_NAME,
                "vencedor" to sorteio.vencedor,
                "nome" to sorteio.nome,
                "qtd" to sorteio.qtd,
                "data" to sorteio.data,
                "participantes" to sorteio.participantes)
                .whereArgs("id = {id}","id" to sorteio.id).exec()

        Timber.d("Update result code is $updateResult")
    }

    fun delete(id: Int) = context.database.use {
        delete(SORTEIOS_TABLE_NAME, whereClause = "id = {sorteioId}", args = *arrayOf("sorteioId" to id))
    }
}