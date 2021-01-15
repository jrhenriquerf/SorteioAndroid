package br.aula.agenda

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import br.aula.agenda.db.Sorteio
import kotlinx.android.synthetic.main.activity_sorteio.*
import java.text.SimpleDateFormat
import java.util.*
import br.aula.agenda.db.SorteioRepository
import android.provider.MediaStore
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.graphics.BitmapFactory
import java.io.*

class SorteioActivity : AppCompatActivity() {

    private var cal = Calendar.getInstance()
    private var datanascimento: Button? = null
    private var sorteio: Sorteio? = null
    private val localArquivoFoto: String? = null
    val REQUEST_IMAGE_CAPTURE = 1
    private var mCurrentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorteio)

        val myChildToolbar = toolbar_child
        setSupportActionBar(myChildToolbar)

        val ab = supportActionBar

        ab!!.setDisplayHomeAsUpEnabled(true)

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        btnSorteio?.setOnClickListener {
            val participantes = txtParticipantes?.text.toString().split(",").toMutableList()
            participantes.shuffle()
            var arrParticipantes = participantes.toTypedArray()

            val vencedor = arrParticipantes.get(0);

            nomecampeao?.setText(vencedor)

            sorteio?.vencedor =  vencedor
            sorteio?.nome = txtNome?.text.toString()
            sorteio?.qtd = 1
            sorteio?.data = cal.time.toString()
            sorteio?.participantes =  txtParticipantes?.text.toString()

            if(sorteio?.id == 0){
                SorteioRepository(this).create(sorteio!!)
            }else{
                SorteioRepository(this).update(sorteio!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        if(intent != null){
            if(intent.getSerializableExtra("sorteio") != null){
                sorteio = intent.getSerializableExtra("sorteio") as Sorteio
                txtNome?.setText(sorteio?.nome)
                txtParticipantes?.setText(sorteio?.participantes)
                txtQtd.setText(sorteio?.qtd.toString())
                nomecampeao.setText(sorteio?.vencedor)

//                if (sorteio?.data != null) {
//                    txtData.setText(dateFormatter?.format(Date(sorteio?.data!!)))
//                }else{
//                    txtData?.setText(dateFormatter?.format(Date()))
//                }
            }else{
                sorteio = Sorteio()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        datanascimento!!.text = sdf.format(cal.getTime())
    }

    // METODOS PARA TRABALHAR COM A CAMERA - INICIO
    private fun dispatchTakePictureIntentSimple() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
}





