package com.codebyzebru.myapplication.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var isConnected: Boolean = true
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email: EditText = findViewById(R.id.login_edtxt_email)
        val password: EditText = findViewById(R.id.login_edtxt_password)

        //  DISABLING TOOLBAR/ACTIONBAR
        supportActionBar?.hide()

        //  REGISTERING BROADCAST RECEIVER FOR INTERNET CONNECTIVITY
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        //  INITIALIZING FIREBASE-AUTH and DATABASE-REFERENCE
        auth = Firebase.auth
        database = Firebase.database.reference

        //  REGISTRATION PAGE
        findViewById<TextView>(R.id.login_txt_newUser).setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        //  FORGOT PASSWORD
        findViewById<TextView>(R.id.login_txt_forgotPassword).setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        //  LOGIN
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            if (email.text.toString() == "") {
                email.error = "Enter Username"
            }
            else if (password.text.toString() == "") {
                password.error = "Enter Password"
            }
            else if (email.text.toString() != "" && password.text.toString() != "" && !isConnected) {
                Toast.makeText(this, "OOPS!!!! No Internet!!!!!", Toast.LENGTH_SHORT).show()
            }
            else {          //ACCEPT CONDITION
                val mail = email.text.toString()
                val pass = password.text.toString()

                auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(this, "Email or Password incorrect!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //  INTENT
    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    //  ONE-TIME LOGIN FOR FIREBASE
    override fun onStart() {
        super.onStart()
        val signedInUser = auth.currentUser
        if (signedInUser != null) {
            updateUI(signedInUser)
        }
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
            snackBar = Snackbar.make(findViewById(R.id.layout_login), "Connection loss", Snackbar.LENGTH_LONG)

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