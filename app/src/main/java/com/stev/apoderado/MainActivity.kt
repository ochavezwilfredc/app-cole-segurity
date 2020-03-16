package com.stev.apoderado

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.stev.apoderado.Clases.VAR
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var sharedPref: SharedPreferences? = null
    var txtNombre:TextView? = null
    var txtDNI:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        txtNombre = headerView.findViewById(R.id.nombre)
        txtDNI = headerView.findViewById(R.id.dni)

        actualizarDatos()
        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.nav_logout){

                sharedPref?.edit {
                    putString(VAR.PREF_TOKEN, "")
                }

                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }

        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_empresas,R.id.nav_servicio_registrar,  R.id.nav_servicio_aceptacion,
                R.id.nav_perfil, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
      //  navView.setNavigationItemSelectedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem)=
        item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    class DatePickerFragment : DialogFragment() {

        var listener: DatePickerDialog.OnDateSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog  = DatePickerDialog(requireActivity(), listener, year, month, day)
         //   datePickerDialog.datePicker.maxDate = Date().time
            return datePickerDialog
        }

        companion object {
            fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerFragment {
                val fragment = DatePickerFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }



    class DatePickerGeneralFragment : DialogFragment() {

        private var listener: DatePickerDialog.OnDateSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog  = DatePickerDialog(requireActivity(), listener, year, month, day)
            return datePickerDialog
        }

        companion object {
            fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerFragment {
                val fragment = DatePickerFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }

    class TimePickerFragment : DialogFragment() {

        private var listener: TimePickerDialog.OnTimeSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val hora = c.get(Calendar.HOUR_OF_DAY)
            val min = c.get(Calendar.MINUTE)
            val datePickerDialog  = TimePickerDialog(requireActivity(), listener, hora, min, true)
            return datePickerDialog
        }

        companion object {
            fun newInstance(listener: TimePickerDialog.OnTimeSetListener): TimePickerFragment {
                val fragment = TimePickerFragment()
                fragment.listener = listener
                return fragment
            }
        }
    }


    fun actualizarDatos(){
        val d = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if (d!=""){
            val datos = JSONObject(d)
            txtNombre?.text = datos.getString("persona")
            txtDNI?.text = datos.getString("documento_identidad")

        }
    }

}
