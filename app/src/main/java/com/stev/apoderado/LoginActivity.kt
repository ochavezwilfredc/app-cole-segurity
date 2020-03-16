package com.stev.apoderado

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.stev.apoderado.Clases.VAR
import com.stev.apoderado.Clases.Validar
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var txtCorreo:EditText ? = null
    var txtContrasenia: EditText ? = null
    var btnIngresar:Button ? = null
    var cbGuardar:CheckBox ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        btnIngresar = findViewById(R.id.btnIngresar)
        val btnRegistrar:Button = findViewById(R.id.btnRegistrar)
        cbGuardar = findViewById(R.id.guardar)
        txtCorreo = findViewById(R.id.dni)
        txtContrasenia = findViewById(R.id.password)
        txtCorreo?.setText( sharedPref?.getString(VAR.PREF_DNI,"" )!!.toString())
        txtContrasenia?.setText( sharedPref?.getString(VAR.PREF_PASS,"" )!!.toString())

        btnIngresar?.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000) {
                if(validarForm()){
                    val dni = txtCorreo?.text.toString()
                    val contra = txtContrasenia?.text.toString()
                    acceder(dni, contra)
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }

        btnRegistrar.setOnClickListener {
            val intent = Intent(applicationContext, RegistrarActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
           // finish()
        }
    }
    fun validarForm():Boolean{

        if(Validar.vacio(txtCorreo)){
            Validar.txtErr(txtCorreo, "Ingrese su dni" )
        }else if(Validar.vacio(txtContrasenia)){
            Validar.txtErr(txtContrasenia, "Ingrese su contraseña" )
        }
        else return true
        return false
    }

    fun acceder(dni:String, pass:String){
        btnIngresar?.isEnabled = false

        val params = HashMap<String,String>()
        params["p_dni"] = dni
        params["p_clave"] = pass

        val parameters = JSONObject(params as Map<String, String>)

        val request = JsonObjectRequest(
            Request.Method.POST, VAR.url("uservalidar"),parameters,
            Response.Listener { response ->

                if(response!=null){

                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){
                        val datos = response.getJSONObject("datos")
                        sharedPref?.edit {
                            putString(VAR.PREF_TOKEN, datos.getString("token"))
                            putString(VAR.PREF_DATA_USUARIO, datos.toString())
                            putString(VAR.PREF_DNI, dni)
                            putString(VAR.PREF_PASS, "")
                            putString(VAR.PREF_ID_USUARIO, datos.getString("id"))

                        }


                        if(cbGuardar!!.isChecked ){
                            sharedPref?.edit {
                                putString(VAR.PREF_PASS, pass)
                            }
                        }

                        Toasty.success(applicationContext, msg, Toast.LENGTH_SHORT, true).show()
                        btnIngresar?.isEnabled = true

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    }else{

                        sharedPref?.edit {
                            putString(VAR.PREF_PASS, "")
                        }
                        Toasty.warning(applicationContext, msg, Toast.LENGTH_SHORT, true).show()
                        btnIngresar?.isEnabled = true

                    }


                }


            }, Response.ErrorListener{
                try {

                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(applicationContext,  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                    btnIngresar?.isEnabled = true
                }catch (ex: Exception){
                    btnIngresar?.isEnabled = true
                    Toasty.error(applicationContext, "Error de conexión", Toast.LENGTH_SHORT, true).show()
                }

            })
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

}


