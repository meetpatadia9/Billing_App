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
import com.codebyzebru.myapplication.dataclasses.NewUserEntryDataClass
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_registrastion.*
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var isConnected: Boolean = true
    private var snackBar: Snackbar? = null
    private lateinit var progress: SpotsDialog
    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
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

        progress = SpotsDialog(this, R.style.Custom)
        progress.setTitle("Please Wait!!")

        val fullName: EditText = reg_edtxt_name
        val email: EditText = reg_edtxt_email
        val phoneNumber: EditText = reg_edtxt_contact
        val password1: EditText = reg_create_password
        val password2: EditText = reg_reEnter_password

        //  DISABLING TOOLBAR/ACTIONBAR
        supportActionBar?.hide()

	    //  REGISTERING BROADCAST RECEIVER FOR INTERNET CONNECTIVITY
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        //  INITIALIZING FIREBASE-AUTH and DATABASE-REFERENCE
        auth = Firebase.auth
        database = Firebase.database.reference

        //  LOGIN PAGE
        findViewById<TextView>(R.id.reg_txt_alreadyUser).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //  REGISTER
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            if (fullName.text.toString() == "") {
                password1.error = "Empty Field"
            }
            else if (email.text.toString() == "") {
                password1.error = "Empty Field"
            }
            else if (password1.text.toString() == ""){
                password1.error = "Empty Field"
            }
            else if (password1.length() <= 6) {
                password2.error = "Password length mast be more than 6 characters"
            }
            else if (password2.text.toString() == "") {
                password1.error = "Empty Field"
            }
            else if (email.text.toString() == "") {
                password1.error = "Empty Field"
            }
            else if(!isValidString(email.text.toString())) {
                email.error = "Invalid Email!"
            }
            else if (phoneNumber.text.toString() == "") {
                phoneNumber.error = "Empty Field"
            }
            else if (phoneNumber.length() != 10) {
                phoneNumber.error = "Invalid Phone Number"
            }
            else if (password1.text.toString() != password2.text.toString()) {
                password2.error = "Different Password"
            }
            else if (password1.text.toString() == password2.text.toString()) {          //ACCEPT CONDITION
                progress.show()
                val mail = email.text.toString()
                val pass = password1.text.toString()

                auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                addNewUser()
                                updateUI()
                                progress.dismiss()
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(this, "Something went wrong! Please try again!!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }

    // ADDING USER DETAILS INTO FIREBASE
    private fun addNewUser() {
        val fullName: EditText = reg_edtxt_name
        val email: EditText = reg_edtxt_email
        val phoneNumber: EditText = reg_edtxt_contact
        val password1: EditText = reg_create_password
        val radioGroup: RadioGroup = reg_rg
        val radioButton: RadioButton?

        val selectedID = radioGroup.checkedRadioButtonId
        radioButton = findViewById(selectedID)

        val registerUserData = NewUserEntryDataClass(
            fullName = fullName.text.toString().trim(),
            email = email.text.toString().trim(),
            contact = phoneNumber.text.toString().trim(),
            gender = radioButton?.text.toString(),
            password = password1.text.toString().trim()
        )

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("Users").child(userID).setValue(registerUserData)
    }

    //  INTENT
    private fun updateUI() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    //  CHECKING ENTERED EMAIL VALIDITY
    private fun isValidString(str: String): Boolean{
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