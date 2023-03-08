package com.codebyzebru.myapplication.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var isConnected: Boolean = true
    private var snackBar: Snackbar? = null
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z\\d+._%\\-]{1,256}" +            //  \\d == 0 to 9
                "@" +
                "[a-zA-Z\\d][a-zA-Z\\d\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z\\d][a-zA-Z\\d\\-]{0,25}" +
                ")+"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrastion)

        //  DISABLING TOOLBAR/ACTIONBAR
        supportActionBar?.hide()

	    //  REGISTERING BROADCAST RECEIVER FOR INTERNET CONNECTIVITY
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        val fullname = findViewById<EditText>(R.id.reg_edtxt_name)
        val username = findViewById<EditText>(R.id.reg_edtxt_username)
        val password1 = findViewById<EditText>(R.id.reg_create_password)
        val password2 = findViewById<EditText>(R.id.reg_reEnter_password)
        val email = findViewById<EditText>(R.id.reg_edtxt_email)
        val phoneNumber = findViewById<EditText>(R.id.reg_edtxt_contact)

        //  LOGIN PAGE
        findViewById<TextView>(R.id.reg_txt_alreadyUser).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //  REGISTER
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            if (fullname.text.toString() == "") {
                password1.setError("Empty Field")
            }
            else if (email.text.toString() == "") {
                password1.setError("Empty Field")
            }
            else if (password1.text.toString() == ""){
                password1.setError("Empty Field")
            }
            else if (password1.length() <= 8 || password1.length() >= 12) {
                password2.setError("Password length mast be between 8 to 12 characters")
            }
            else if (password2.text.toString() == "") {
                password1.setError("Empty Field")
            }
            else if (email.text.toString() == "") {
                password1.setError("Empty Field")
            }
            else if(!isValidString(email.text.toString())) {
                email.setError("Invalid Email!")
            }
            else if (phoneNumber.text.toString() == "") {
                phoneNumber.setError("Empty Field")
            }
            else if (phoneNumber.length() != 10) {
                phoneNumber.setError("Invalid Phone Number")
            }
            else if (password1.text.toString() != password2.text.toString()) {
                password2.setError("Different Password")
            }
            else if (password1.text.toString() == password2.text.toString()) {          //ACCEPT CONDITION
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("username", username.text.toString())
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

    }

    //  CHECKING ENTERED EMAIL VALIDITY
    fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    /*
            CHECKING FOR ACTIVE INTERNET CONNECTION
    */
    private fun noInternet() {
        isConnected = false
    }

    private fun internetConnected() {
        isConnected = true
    }

    override fun onNetworkConnectionChange(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            noInternet()
            snackBar = Snackbar.make(findViewById(R.id.layout_registration), "Connection loss", Snackbar.LENGTH_LONG)

            val view = snackBar?.view
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.setBackgroundTint(resources.getColor(R.color.red))
            val params = view?.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackBar?.show()
        }
        else {
            internetConnected()
            snackBar?.dismiss()
        }
    }

}