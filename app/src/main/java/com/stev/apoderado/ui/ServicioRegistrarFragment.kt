package com.stev.apoderado.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.stev.apoderado.Clases.*
import com.stev.apoderado.MainActivity
import com.stev.apoderado.R
import es.dmoral.toasty.Toasty
import org.angmarch.views.NiceSpinner
import org.json.JSONObject
import java.util.*


class ServicioRegistrarFragment : Fragment() {

    var fechaInicio: String = ""
    var fechaFin: String = ""
    var horaInicio: String = ""
    var horaFin: String = ""

    var sharedPref: SharedPreferences? = null
    var lastClick: Long = 0
    var btnRegistrar:Button ? = null
    var comboTurno:NiceSpinner? = null
    var comboHijos:NiceSpinner? = null
    var comboGrado:NiceSpinner? = null
    var comboSeccion:NiceSpinner? = null
    var comboColegio:NiceSpinner? = null
    var comboEmpresa:NiceSpinner? = null

    var idapoderado:Int = -1
    var listaAlumnnos =  LinkedList<ClsAlumno>()
    var listaColegios =  LinkedList<ClsColegio>()
    var listaColegiosStr = LinkedList<String>()
    var listaEmpresa =  LinkedList<ClsEmpresaResumen>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.registrar_servicio, container, false)

        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )

        val alfabeto = "abcdefghijklmnopqrstuvwxyz".toCharArray()

        idapoderado = sharedPref?.getString(VAR.PREF_ID_USUARIO, "0")!!.toInt()
        comboHijos = root.findViewById(R.id.sp_hijos)
        comboTurno = root.findViewById(R.id.sp_turno)
        comboGrado = root.findViewById(R.id.sp_grado)
        comboSeccion = root.findViewById(R.id.sp_seccion)
        comboColegio = root.findViewById(R.id.sp_colegio)
        comboEmpresa = root.findViewById(R.id.sp_empresa)

        val datasetHijos: List<String> = LinkedList(Arrays.asList("--Seleccione Alumno--"))
        val datasetColegio: List<String> = LinkedList(Arrays.asList("--Seleccione Colegio--"))

        val datasetTurno: List<String> = LinkedList(Arrays.asList("Mañana", "Tarde"))
        val datasetGrado: List<String> = LinkedList(Arrays.asList("1ro", "2do","3ro","4to","5to","6to"))

        val datasetSeccion: LinkedList<String> = LinkedList()
        for (i in alfabeto.indices) {
            datasetSeccion.add(alfabeto[i].toString().toUpperCase())
        }

        comboSeccion?.attachDataSource(datasetSeccion)
        comboColegio?.attachDataSource(datasetColegio)
        comboGrado?.attachDataSource(datasetGrado)
        comboTurno?.attachDataSource(datasetTurno)
        comboHijos?.isEnabled = false
        comboColegio?.isEnabled = false
        comboEmpresa?.isEnabled = false


        comboHijos?.attachDataSource(datasetHijos)

        val btnServicioRegistrar: Button = root.findViewById(R.id.btnRegistrarHijo)
        btnServicioRegistrar.setOnClickListener {
            val navHostFragment: NavHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.nav_hijo)
        }


        buscarAlumnos()
        buscarColegios()

        comboColegio?.setOnSpinnerItemSelectedListener {
                parent, view, position, id ->
            if(listaColegios.size > 0){
                val ind = listaColegiosStr.indexOf(parent.getItemAtPosition(position))
                val idcolegio = listaColegios[ind].id
                buscarEmpresas(idcolegio)
                Log.e("error", "posicion colego"+ position.toString())
            }
        }

        val txtFechaInicio = root.findViewById<EditText>(R.id.fecha_inicio)

        txtFechaInicio.setOnClickListener{
            val newFragment = MainActivity.DatePickerGeneralFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaInicio = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFechaInicio.setError(null)

                    }catch (ex: java.lang.Exception){

                    }
                    txtFechaInicio.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "datePicker1")
        }

        val txtFechaFin = root.findViewById<EditText>(R.id.fecha_fin)
        val c2 = Calendar.getInstance()
        c2.set(Calendar.YEAR, c2.get(Calendar.YEAR) +1)

        txtFechaFin.setOnClickListener{
            val newFragment = MainActivity.DatePickerGeneralFragment.newInstance(
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val dia =  day.toString().padStart(2, '0')
                    val mes = (month + 1).toString().padStart(2,'0')
                    val selectedDate = dia+ " / " + mes + " / " + year
                    fechaFin = year.toString() +"-"+ mes +"-" + dia
                    try {
                        txtFechaFin.setError(null)

                    }catch (ex: java.lang.Exception){

                    }
                    txtFechaFin.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "datePicker2")
        }


        val txtHoraInicio = root.findViewById<EditText>(R.id.hora_inicio)

        txtHoraInicio.setOnClickListener{
            val newFragment = MainActivity.TimePickerFragment.newInstance(
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    val hora =  hourOfDay.toString().padStart(2, '0')
                    val min =  minute.toString().padStart(2, '0')

                    val selectedDate = "$hora : $min"
                    horaInicio = "$hora:$min"
                    try {
                        txtHoraInicio.setError(null)
                    }catch (ex: java.lang.Exception){
                    }
                    txtHoraInicio.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "timepicker1")
        }
        val txtHoraFin = root.findViewById<EditText>(R.id.hora_fin)

        txtHoraFin.setOnClickListener{
            val newFragment = MainActivity.TimePickerFragment.newInstance(
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    val hora =  hourOfDay.toString().padStart(2, '0')
                    val min =  minute.toString().padStart(2, '0')

                    val selectedDate = "$hora : $min"
                    horaFin = "$hora:$min"
                    try {
                        txtHoraFin.setError(null)
                    }catch (ex: java.lang.Exception){
                    }
                    txtHoraFin.setText(selectedDate)
                })
            newFragment.show(requireFragmentManager(), "timepicker2")
        }

        val btnRegistrar:Button = root.findViewById(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
                var valido = false
                if(listaAlumnnos.isEmpty()){
                    Toasty.warning(requireActivity(), "Registre a su hijo", Toast.LENGTH_SHORT, true).show()
                }
                else if(txtFechaInicio.text.isEmpty()){
                    Toasty.warning(requireActivity(), "Seleccione Fecha de Inicio", Toast.LENGTH_SHORT, true).show()
                }else if(txtFechaFin.text.isEmpty()){
                    Toasty.warning(requireActivity(), "Seleccione Fecha de Fin", Toast.LENGTH_SHORT, true).show()
                }else if(txtHoraInicio.text.isEmpty()){
                    Toasty.warning(requireActivity(), "Seleccione Hora de Entrada", Toast.LENGTH_SHORT, true).show()
                }else if(txtHoraFin.text.isEmpty()){
                    Toasty.warning(requireActivity(), "Seleccione Hora de Salida", Toast.LENGTH_SHORT, true).show()
                }else{
                    valido = true
                }

                if(valido){

                    val params = JSONObject()
                    params.put("turno", datasetTurno.get(comboTurno!!.selectedIndex))
                    params.put("grado", datasetGrado.get(comboGrado!!.selectedIndex))
                    params.put("seccion", datasetSeccion.get(comboSeccion!!.selectedIndex))
                    params.put("hora_entrada", horaInicio)
                    params.put("hora_salida", horaFin)
                    params.put("persona_id", listaAlumnnos.get(comboHijos!!.selectedIndex).id)
                    params.put("colegio_id",  listaColegios.get(comboColegio!!.selectedIndex).id)
                    params.put("empresa_id",  listaEmpresa.get(comboEmpresa!!.selectedIndex).id)
                    params.put("fecha_inicio", fechaInicio)
                    params.put("fecha_fin", fechaFin)


                    registrar(params)
                }
            }
            lastClick = SystemClock.elapsedRealtime()
        }
        return root

    }

    fun buscarAlumnos(){

        val params = JSONObject()
        params.put("apoderado_id", idapoderado)
        Log.e("error", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
           Method.POST, VAR.url("alumnos_list"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")
                    if(response.getInt("estado") == 200 ){
                        comboHijos?.isEnabled  = true

                        val datos = response.getJSONArray("datos")

                        val dataset = LinkedList<String>()
                        listaAlumnnos.clear()
                        for (i in 0 until datos.length()) {
                            val json = datos.getJSONObject(i)
                            val p = ClsAlumno( json.getInt("id"),
                                json.getString("nombre_completo"),
                                json.getString("documento_identidad"),
                                json.getString("direccion"),
                                json.getString("celular")
                            )
                            dataset.add(p.toString())
                            listaAlumnnos.add(p)
                        }


                        comboHijos?.attachDataSource(dataset)



                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                    }
                }
            }, Response.ErrorListener{
                try {
                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                }catch (ex: Exception){
                    Log.e("error", ex.message)
                    Toasty.error(requireActivity(), "Debe Registrar el Alumno", Toast.LENGTH_LONG, true).show()
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


    fun buscarColegios(){

        val params = JSONObject()
        params.put("colegio_id", 0)
        Log.e("error", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("colegio_list"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")
                    if(response.getInt("estado") == 200 ){
                        comboColegio?.isEnabled  = true
                        val datos = response.getJSONArray("datos")

                        val dataset = LinkedList<String>()
                        listaColegios.clear()
                        for (i in 0 until datos.length()) {
                            val json = datos.getJSONObject(i)
                            val p = ClsColegio( json.getInt("id"),
                                json.getString("nombre")
                            )
                            dataset.add(p.toString())
                            listaColegios.add(p)
                        }
                        listaColegiosStr.clear()
                        listaColegiosStr.addAll(dataset)

                        if(listaColegios.size >0){
                            buscarEmpresas(listaColegios[0].id)
                        }


                        comboColegio?.attachDataSource(dataset)


                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                    }
                }
            }, Response.ErrorListener{
                try {
                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                }catch (ex: Exception){
                    Log.e("error", ex.message)
                    Toasty.error(requireActivity(), "Debe Registrar el Alumno", Toast.LENGTH_LONG, true).show()
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

    fun buscarEmpresas(idColegio:Int){
        val params = JSONObject()
        params.put("colegio_id", idColegio)
        Log.e("error", params.toString())
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("empresa_list_colegio"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")
                    if(response.getInt("estado") == 200 ){
                        comboEmpresa?.isEnabled  = true
                        val datos = response.getJSONArray("datos")
                        val dataset = LinkedList<String>()
                        listaEmpresa.clear()
                        for (i in 0 until datos.length()) {
                            val json = datos.getJSONObject(i)
                            val p = ClsEmpresaResumen( json.getInt("id"),
                                json.getString("empresa")
                            )

                            dataset.add(p.toString())
                            listaEmpresa.add(p)
                        }
                        comboEmpresa?.attachDataSource(dataset)


                    }else{
                        Toasty.error(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                    }
                }
            }, Response.ErrorListener{
                try {
                    val nr = it.networkResponse
                    val r = String(nr.data)
                    val response=  JSONObject(r)
                    Toasty.warning(requireActivity(),  response.getString("mensaje"), Toast.LENGTH_SHORT, true).show()
                }catch (ex: Exception){
                    Log.e("error", ex.message)
                    Toasty.error(requireActivity(), "Debe Registrar el Alumno", Toast.LENGTH_LONG, true).show()
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


    fun registrar(params:JSONObject){
        btnRegistrar?.isEnabled = false

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, VAR.url("alumno_reference"), params,
            Response.Listener { response ->
                if(response!=null){
                    val msg = response.getString("mensaje")

                    if(response.getInt("estado") == 200 ){
                        Toasty.success(requireActivity(), msg, Toast.LENGTH_SHORT, true).show()
                      //  activity?.onBackPressed()
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
                }catch (ex: java.lang.Exception){
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