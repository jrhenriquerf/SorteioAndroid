package br.aula.agenda.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.aula.agenda.SORTEIOS_DB_NAME
import br.aula.agenda.SORTEIOS_TABLE_NAME
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.*

class BancoDadosHelper(context: Context) :
        ManagedSQLiteOpenHelper(ctx = context,
                name = SORTEIOS_DB_NAME, version = 1) {

    private val scriptSQLCreate = arrayOf(
            "INSERT INTO $SORTEIOS_TABLE_NAME VALUES(1,'Ricardo Oliveira', 1,'Ganhar carro BMW', '14/01/2021 21:25:00', 'Jair Rangel,Joao Paulo,Carlos Cubas,Ricardo Oliveira,Jose Fernando');",
            "INSERT INTO $SORTEIOS_TABLE_NAME VALUES(2,'Joao Paulo', 1,'Ganhar iphone 15', '14/01/2021 22:25:00', 'Jair Rangel,Joao Paulo,Carlos Cubas,Ricardo Oliveira,Jose Fernando');",
            "INSERT INTO $SORTEIOS_TABLE_NAME VALUES(3,'Carlos Cubas', 1,'Ganhar caixão', '14/01/2021 20:00:00', 'Jair Rangel,Joao Paulo,Carlos Cubas,Ricardo Oliveira,Jose Fernando');",
            "INSERT INTO $SORTEIOS_TABLE_NAME VALUES(4,'Jair Rangel', 1,'Ganhar moto', '14/01/2021 18:40:00', 'Jair Rangel,Joao Paulo,Carlos Cubas,Ricardo Oliveira,Jose Fernando');")

    //singleton da classe
    companion object {
        private var instance: BancoDadosHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): BancoDadosHelper {
            if (instance == null) {
                instance = BancoDadosHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Criação de tabelas
        db.createTable(SORTEIOS_TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "vencedor" to TEXT,
                "qtd" to INTEGER,
                "nome" to TEXT,
                "data" to TEXT,
                "participantes" to TEXT
        )

        // insere dados iniciais na tabela
        scriptSQLCreate.forEach {sql ->
            db.execSQL(sql)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(SORTEIOS_TABLE_NAME, true)
        onCreate(db)
    }
}

val Context.database: BancoDadosHelper get() = BancoDadosHelper.getInstance(getApplicationContext())