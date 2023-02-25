package com.codebyzebru.myapplication.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.Layout.Alignment
import android.text.style.AlignmentSpan
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.adapters.PrefManager
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var prefManager: PrefManager
    private var isConnected: Boolean = true
    private var snackBar: Snackbar? = null
    private var fetchedUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        val username = findViewById<EditText>(R.id.login_edtxt_username)
        val password = findViewById<EditText>(R.id.login_edtxt_password)

        val bundle: Bundle? = intent.extras
        val displayData = bundle?.getString("username")
        username.setText(displayData)


        /*
                SharedPreferences for one-time login
        */
        prefManager = PrefManager(this)
        if (prefManager.isLogin()) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


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
            if (username.text.toString() == "") {
                username.error = "Enter Username"
            }
            else if (password.text.toString() == "") {
                password.error = "Enter Password"
            }
            else if (username.text.toString() == "TestUser" && password.text.toString() == "test123" && isConnected) {   //ACCEPT CONDITION
                prefManager.setLogin(true)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else if (username.text.toString() == "TestUser" && password.text.toString() == "test123" && !isConnected) {
                Toast.makeText(this, "OOPS!!!! No Internet!!!!!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Username or Password incorrect", Toast.LENGTH_SHORT).show()
            }
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

















//            val snackBarView = Snackbar.make(findViewById(R.id.layout_login), "SnackBar Message" , Snackbar.LENGTH_LONG)
//            val view = snackBarView.view
//            val params = view.layoutParams as FrameLayout.LayoutParams
//            params.gravity = Gravity.TOP
//            view.layoutParams = params
//            view.background = ContextCompat.getDrawable(this,R.drawable.baseline_signal_cellular_connected_no_internet_4_bar_24) // for custom background
//            snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
//            snackBarView.show()