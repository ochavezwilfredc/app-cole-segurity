package com.app.movilidad.ui

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import com.app.movilidad.Clases.VAR
import com.app.movilidad.Clases.Validar
import com.app.movilidad.MainActivity
import com.app.movilidad.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

class PerfilFragment : Fragment() {

    var fechaNacimiento: String = ""
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var btnRegistrar:Button ? = null
    var txtNombre:EditText? = null
    var txtDNI:EditText? = null
    var txtDireccion:EditText? = null
    var txtTelefono:EditText? = null
    var txtPass:EditText ? =null
    var txtFechaNacimiento:EditText? = null
    var idpersona = -1
    var sexoMasculino:RadioButton?= null
    var sexoFemenino:RadioButton?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.registrar_apoderado, container, false)

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)

        val txtTitulo :TextView = root.findViewById(R.id.txttitulo)
        txtTitulo.text = "Editar Perfil"

        val datos = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        btnRegistrar = root.findViewById(R.id.btnRegistrar)
         txtDNI = root.findViewById<EditText>(R.id.dni)
         txtNombre = root.findViewById<EditText>(R.id.nombre_completo)
        txtNombre!!.filters += InputFilter.AllCaps()
         txtTelefono = root.findViewById<EditText>(R.id.telefono)
         txtDireccion = root.findViewById<EditText>(R.id.direccion)
         sexoMasculino = root.findViewById<RadioButton>(R.id.sexoMasculino)
        sexoFemenino = root.findViewById<RadioButton>(R.id.sexoFemenino)

        txtPass = root.findViewById<EditText>(R.id.password)
         txtFechaNacimiento = root.findViewById<EditText>(R.id.fecha_nacimiento)


        val  contenedorPass:TextInputLayout  = root.findViewById(R.id.til_password)

        /*
            Cagar Datos

         */
        val d = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if (d!=""){
            val datos = JSONObject(d)
            idpersona = datos.getInt("id")
            txtNombre?.setText(datos.getString("persona"))
            txtDNI?.setText( datos.getString("documento_identidad"))
            txtDireccion?.setText(datos.getString("direccion"))
            txtTelefono?.setText( datos.getString("celular"))

        }
        val contenedorCambioPass = root.findViewById<LinearLayout>(R.id.contenedorCambioPass)
        contenedorCambioPass.visibility = View.VISIBLE

        val cambiarPass:Switch = root.findViewById(R.id.cambiarPass)
        cambiarPass.setOnCheckedChangeListener { buttonView, isChecked ->
            txtPass?.setText("")
            contenedorPass.visibility = if(isChecked) View.VISIBLE else View.GONE
        }
        if(datos!="") {
            val datos = JSONObject(datos)
            txtNombre!!.setText(datos.getString("persona"))
            txtDNI!!.setText( datos.getString("documento_identidad"))
            txtDireccion!!.setText(datos.getString("direccion"))
            txtTelefono?.setText( datos.getString("celular"))
            txtNombre!!.setText(datos.getString("persona"))
        }

        txtFechaNacimiento!!.setOnClickListener{
            val newFragment = MainActivity.DatePickerFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaNacimiento = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFechaNacimiento!!.setError(null)

                    }catch (ex: Exception){

                    }
                    txtFechaNacimiento!!.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "datePicker")
        }

        btnRegistrar?.text = "ACTUALIZAR"
        btnRegistrar?.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){

                val sexo = if(sexoMasculino!!.isChecked) "M" else "F"

                var valido = false
                if(Validar.vacio(txtDNI)) {
                    Validar.txtErr(txtDNI, "Ingrese Número de DNI")
                }else if( ! Validar.strSize(txtDNI,   8)) {
                    Validar.txtErr(txtDNI,  "El número de DNI debe tener 8 digitos")
                }else if(Validar.vacio(txtNombre)) {
                    Validar.txtErr(txtNombre, "Ingrese Nombre")
                }else if(Validar.vacio(txtDireccion)){
                    Validar.txtErr(txtDireccion, "Ingrese dirección" )
                }else if(Validar.vacio(txtFechaNacimiento)){
                    Validar.txtErr(txtFechaNacimiento, "Ingrese fecha nacimiento" )
                }
                else if( cambiarPass.isChecked && Validar.vacio(txtPass!!)){
                    Validar.txtErr(txtPass, "Ingrese contraseña" )
                }else if(cambiarPass.isChecked && Validar.strMenorA(txtPass!!,6)){
                    Validar.txtErr(txtPass, "Mínimo 6 caracteres" )
                }
                else{
                    valido = true
                }
                if(valido){


                    val datos = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                    if(datos!=""){
                        val persona = JSONObject(datos)
                        val personaid = persona.optString("id", "")
                        val jsonObject = JSONObject()
                        jsonObject.put("nombre_completo", txtNombre!!.text.trim().toString())
                        jsonObject.put("documento_identidad", txtDNI!!.text.trim().toString())
                        jsonObject.put("celular", txtTelefono!!.text.trim().toString())
                        jsonObject.put("direccion", txtDireccion!!.text.trim().toString())
                        jsonObject.put("fecha_nacimiento", fechaNacimiento)
                        jsonObject.put("persona_id", personaid)
                        jsonObject.put("sexo", sexo)
                        var param = 0
                        if(cambiarPass.isChecked) param = 1
                        jsonObject.put("param", param)
                        jsonObject.put("password", txtPass!!.text.trim().toString())
                        actualizar(jsonObject)
                    }
                }else{
                    Toasty.error(requireActivity(), "Complete correctamente los campos.", Toast.LENGTH_SHORT, true).show()
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }

        val btnCancelar: Button = root.findViewById(R.id.btnLogin)
        btnCancelar.setOnClickListener {
            activity?.onBackPressed()

        }
        buscar()
        return root
    }

    fun actualizar(params:JSONObject){
        btnRegistrar?.isEnabled = false

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("persona_perfil_update"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){
                        Toasty.success(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                        buscar()
                        btnRegistrar?.isEnabled = true
                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                        btnRegistrar?.isEnabled = true
                    }
                }
            }, Response.ErrorListener{
                try {

                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                    btnRegistrar?.isEnabled = true
                }catch (ex: Exception){
                    btnRegistrar?.isEnabled = true
                    Toasty.error(requireActivity(), "Error de conexión", Toast.LENGTH_SHORT, true).show()
                }

            })
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(request)
    }


    fun buscar(){
        btnRegistrar?.isEnabled = false

        val params = JSONObject()
        params.put("id", idpersona)

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, VAR.url("persona_read"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){


                        val datos = response.getJSONObject("datos")

                        txtNombre!!.setText(datos.getString("nombre_completo"))
                        txtDNI!!.setText( datos.getString("documento_identidad"))
                        txtDireccion!!.setText(datos.getString("direccion"))
                        txtTelefono?.setText( datos.getString("celular"))
                        if(datos.getString("sexo")=="M") sexoMasculino?.isChecked = true
                        else sexoFemenino?.isChecked = true
                        val fecha = datos.getString("fecha_nacimiento")
                        val arrFecha = fecha.split("-")
                        val f = arrFecha[2] + " / "+ arrFecha[1] + " / "+ arrFecha[0]

                        fechaNacimiento = fecha
                        txtFechaNacimiento?.setText(f)

                        btnRegistrar?.isEnabled = true
                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                        btnRegistrar?.isEnabled = true
                    }
                }
            }, Response.ErrorListener{
                try {

                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                    btnRegistrar?.isEnabled = true
                }catch (ex: Exception){
                    btnRegistrar?.isEnabled = true
                    Toasty.error(requireActivity(), "Error de conexión", Toast.LENGTH_SHORT, true).show()
                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(request)
    }



}