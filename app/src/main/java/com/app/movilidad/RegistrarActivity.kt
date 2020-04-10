package com.app.movilidad

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.InputFilter
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.movilidad.Clases.VAR
import com.app.movilidad.Clases.Validar
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class RegistrarActivity : AppCompatActivity() {
    var fechaNacimiento: String = ""
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var btnRegistrar:Button ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_apoderado)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        val txtDNI = findViewById<EditText>(R.id.dni)
        val txtNombre = findViewById<EditText>(R.id.nombre_completo)
        txtNombre.filters += InputFilter.AllCaps()
        val txtTelefono = findViewById<EditText>(R.id.telefono)
        val txtDireccion = findViewById<EditText>(R.id.direccion)
        val sexoMasculino = findViewById<RadioButton>(R.id.sexoMasculino)
        val txtFechaNacimiento = findViewById<EditText>(R.id.fecha_nacimiento)
        txtFechaNacimiento.setOnClickListener{
            val newFragment = DatePickerRegistrarFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dia =  day.toString().padStart(2, '0')
                val mes = (month + 1).toString().padStart(2,'0')
                val selectedDate = dia+ " / " + mes + " / " + year
                fechaNacimiento = year.toString() +"-"+ mes +"-" + dia
                try {
                    txtFechaNacimiento.setError(null)

                }catch (ex:Exception){

                }
                txtFechaNacimiento.setText(selectedDate)
            })
            newFragment.show(supportFragmentManager, "datePicker")
        }
        btnRegistrar?.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){

                val sexo = if(sexoMasculino.isChecked) "M" else "F"

                var valido = false
                if(Validar.vacio(txtDNI)) {
                    Validar.txtErr(txtDNI, "Ingrese Número de DNI")
                }else if( ! Validar.strSize(txtDNI,   8)) {
                    Validar.txtErr(txtDNI,  "El número de DNI debe tener 8 digitos")
                }else if(Validar.vacio(txtNombre)) {
                    Validar.txtErr(txtNombre, "Ingrese Nombre")
                }else if(Validar.vacio(txtDireccion)){
                    Validar.txtErr(txtDireccion, "Ingrese dirección" )
                } else if(Validar.vacio(txtTelefono)){
                        Validar.txtErr(txtTelefono, "Ingrese celular" )
                }else if(Validar.vacio(txtFechaNacimiento)){
                    Validar.txtErr(txtFechaNacimiento, "Ingrese fecha nacimiento" )
                }
                else{
                    valido = true
                }
                if(valido){
                    val jsonObject = JSONObject()
                    jsonObject.put("operation", "Nuevo")
                    jsonObject.put("nombre_completo", txtNombre.text.trim().toString())
                    jsonObject.put("documento_identidad", txtDNI.text.trim().toString())
                    jsonObject.put("celular", txtTelefono.text.trim().toString())
                    jsonObject.put("direccion", txtDireccion.text.trim().toString())
                    jsonObject.put("estado", "A")
                    jsonObject.put("fecha_nacimiento", fechaNacimiento)
                    jsonObject.put("apoderado_id", 0)
                    jsonObject.put("empresa_id", 0)
                    jsonObject.put("sexo", sexo)
                    jsonObject.put("es_usuario", true)
                    jsonObject.put("rol_id", 4)
                    registrar(jsonObject)
                }else{
                    Toasty.error(applicationContext, "Complete correctamente los campos.", Toast.LENGTH_SHORT, true).show()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }
        
        val btnCancelar:Button = findViewById(R.id.btnLogin)
        btnCancelar.setOnClickListener { 
            this.finish()
        }
    }

    fun registrar(params:JSONObject){
        btnRegistrar?.isEnabled = false

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("register_person"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){
                        Toasty.success(applicationContext, msg, Toast.LENGTH_SHORT, true).show()
                        btnRegistrar?.isEnabled = true
                        finish()

                    }else{
                        Toasty.error(applicationContext, msg, Toast.LENGTH_SHORT, true).show()
                        btnRegistrar?.isEnabled = true

                    }
                }
            }, Response.ErrorListener{
                try {

                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(applicationContext,  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                    btnRegistrar?.isEnabled = true
                }catch (ex: Exception){
                    btnRegistrar?.isEnabled = true
                    Toasty.error(applicationContext, "Error de conexión", Toast.LENGTH_SHORT, true).show()
                }

            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    class DatePickerRegistrarFragment : DialogFragment() {

        private var listener: DatePickerDialog.OnDateSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog  = DatePickerDialog(requireActivity(), listener, year, month, day)
            datePickerDialog.datePicker.maxDate = Date().time
            return datePickerDialog

        }

        companion object {
            fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerRegistrarFragment {
                val fragment = DatePickerRegistrarFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }

}


