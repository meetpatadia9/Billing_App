package com.codebyzebru.myapplication.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class CreateNewPasswordActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var isConnected: Boolean = true
    private var snackBar : Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_password)

        //  REGISTERING BROADCAST RECEIVER FOR INTERNET CONNECTIVITY
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        val newPassword = findViewById<EditText>(R.id.cpf_edtxt_newPassword)
        val conformPassword = findViewById<EditText>(R.id.cpf_edtxt_conformPass)

        //  CREATE NEW PASSWORD
        findViewById<Button>(R.id.btnCreateNewPassword).setOnClickListener {
            if (newPassword.text.toString() == "") {
                newPassword.error = "Empty Field"
            }
            else if (conformPassword.text.toString() == "") {
                newPassword.error = "Empty Field"
            }
            else if (newPassword.text.toString() != conformPassword.text.toString()) {
                newPassword.error = "Different Password"
            }
            else if (newPassword.text.toString() == conformPassword.text.toString()) {      //ACCEPT CONDITION
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
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
            snackBar = Snackbar.make(findViewById(R.id.layout_createNewPassword), "Connection loss", Snackbar.LENGTH_LONG)

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