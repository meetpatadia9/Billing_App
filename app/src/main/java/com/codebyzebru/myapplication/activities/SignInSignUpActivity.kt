package com.codebyzebru.myapplication.activities

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.codebyzebru.myapplication.databinding.ActivitySignInSignUpBinding
import com.codebyzebru.myapplication.fragments.FirstFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class SignInSignUpActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivitySignInSignUpBinding

    private var isConnected: Boolean = true
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameMain, FirstFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
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

    @Suppress("DEPRECATION")
    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            noInternet()
            snackBar = Snackbar.make(findViewById(R.id.signInSignUp), "Connection loss", Snackbar.LENGTH_LONG)

            val view = snackBar?.view
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.setBackgroundTint(resources.getColor(R.color.color4))
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