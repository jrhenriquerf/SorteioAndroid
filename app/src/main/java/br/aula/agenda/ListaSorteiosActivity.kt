package br.aula.agenda

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu

import kotlinx.android.synthetic.main.activity_lista_sorteios.*
import android.widget.ArrayAdapter
import android.view.MenuItem
import android.content.Intent
import android.view.View
import br.aula.agenda.db.Sorteio
import br.aula.agenda.db.SorteioRepository
import android.widget.AdapterView
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import br.aula.agenda.broadcast.SMSReceiver
import android.support.v4.content.ContextCompat
import android.telephony.SmsMessage
import android.util.Log
import java.util.logging.Logger


class ListaSorteiosActivity : AppCompatActivity() {

    private var sorteios:ArrayList<Sorteio>? = null
    private var contatoSelecionado:Sorteio? = null
    var receiver: BroadcastReceiver? = null
    val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_sorteios)

        val myToolbar = toolbar
        myToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(myToolbar)

        lista.setOnItemClickListener { _, _, position, id ->
            val intent = Intent(this@ListaSorteiosActivity, SorteioActivity::class.java)
            intent.putExtra("sorteio", sorteios?.get(position))
            startActivity(intent)
        }

        lista.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapter, view, posicao, id ->
            contatoSelecionado = sorteios?.get(posicao)
            false
        }
        setupPermissions()
        configureReceiver()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Logger.getLogger(SmsMessage::class.java.name).warning("Permission RECEIVE SMS")
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        menuInflater.inflate(R.menu.menu_contato_contexto, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.excluir -> {
                AlertDialog.Builder(this@ListaSorteiosActivity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deletar")
                        .setMessage("Deseja mesmo deletar ?")
                        .setPositiveButton("Quero",
                                DialogInterface.OnClickListener { dialog, which ->
                                    SorteioRepository(this).delete(this.contatoSelecionado!!.id)
                                    carregaLista()
                                }).setNegativeButton("Nao", null).show()
                return false
            }

            R.id.share -> {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.type = "text/plain"
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "Assunto que será compartilhado")
                intentShare.putExtra(Intent.EXTRA_TEXT, "Texto que será compartilhado")
                startActivity(Intent.createChooser(intentShare, "Escolha como compartilhar"))
                return false
            }


            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        carregaLista()
        registerForContextMenu(lista);
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.novo -> {
                val intent = Intent(this, SorteioActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.sorteio -> {
                val intent = Intent(this, SorteioActivity::class.java)
                startActivity(intent)
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun carregaLista() {
        sorteios = SorteioRepository(this).findAll()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, sorteios)
        lista?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun configureReceiver() {
        val filter = IntentFilter()
        filter.addAction("br.aula.agenda.broadcast.SMSreceiver")
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        receiver = SMSReceiver()
        registerReceiver(receiver, filter)
    }

    private fun setupPermissions() {

        val list = listOf<String>(
                Manifest.permission.RECEIVE_SMS
        )

        ActivityCompat.requestPermissions(this,
                list.toTypedArray(), MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)

        if (permission != PackageManager.GET_SERVICES) {
            Log.i("aula", "Permission to record denied")
        }
    }


}
