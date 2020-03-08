package com.stev.apoderado.ui

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
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.Clases.Validar
import com.stev.apoderado.MainActivity
import com.stev.apoderado.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.lang.Exception

class HijoFragment : Fragment() {

    var fechaNacimiento: String = ""
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var btnRegistrar:Button ? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.registrar_apoderado, container, false)

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)


        val datos = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        btnRegistrar = root.findViewById(R.id.btnRegistrar)
        val txtDNI = root.findViewById<EditText>(R.id.dni)
        val txtNombre = root.findViewById<EditText>(R.id.nombre_completo)
        txtNombre.filters += InputFilter.AllCaps()
        val txtTelefono = root.findViewById<EditText>(R.id.telefono)
        val txtDireccion = root.findViewById<EditText>(R.id.direccion)
        val sexoMasculino = root.findViewById<RadioButton>(R.id.sexoMasculino)
        val txtPass = root.findViewById<EditText>(R.id.password)
        val txtFechaNacimiento = root.findViewById<EditText>(R.id.fecha_nacimiento)
        if(datos!="") {
            val apoderado = JSONObject(datos)
            txtDireccion.setText(apoderado.optString("direccion", ""))
        }

        txtFechaNacimiento.setOnClickListener{
            val newFragment = MainActivity.DatePickerFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaNacimiento = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFechaNacimiento.setError(null)

                    }catch (ex: Exception){

                    }
                    txtFechaNacimiento.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "datePicker")
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
                }else if(Validar.vacio(txtFechaNacimiento)){
                    Validar.txtErr(txtFechaNacimiento, "Ingrese fecha nacimiento" )
                }
                else{
                    valido = true
                }
                if(valido){

                    val datos = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
                    if(datos!=""){
                        val apoderado = JSONObject(datos)
                        val id_apoderado = apoderado.optString("id", "")
                        val jsonObject = JSONObject()
                        jsonObject.put("operation", "Nuevo")
                        jsonObject.put("nombre_completo", txtNombre.text.trim().toString())
                        jsonObject.put("documento_identidad", txtDNI.text.trim().toString())
                        jsonObject.put("celular", txtTelefono.text.trim().toString())
                        jsonObject.put("direccion", txtDireccion.text.trim().toString())
                        jsonObject.put("estado", "A")
                        jsonObject.put("fecha_nacimiento", fechaNacimiento)
                        jsonObject.put("apoderado_id", id_apoderado)
                        jsonObject.put("empresa_id", 0)
                        jsonObject.put("sexo", sexo)
                        jsonObject.put("es_usuario", 0)
                        jsonObject.put("rol_id", 5)
                        registrar(jsonObject)
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
        return root
    }

    fun registrar(params:JSONObject){
        btnRegistrar?.isEnabled = false

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("register_person"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){
                        Toasty.success(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                        activity?.onBackPressed()
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

}