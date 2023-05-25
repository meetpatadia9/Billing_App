package com.codebyzebru.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.activities.ProfileActivity
import com.codebyzebru.myapplication.activities.SignInSignUpActivity
import com.codebyzebru.myapplication.databinding.FragmentCredentialsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class CredentialsFragment : Fragment() {

    lateinit var binding: FragmentCredentialsBinding

    lateinit var auth: FirebaseAuth
    lateinit var userID: String

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
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentCredentialsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = binding.cpEdtxtEmail
        val newPassword = binding.cpEdtxtPassword
        val conformPassword = binding.cpEdtxtConformPass

        binding.btnSignIn.setOnClickListener {
            if (newPassword.text.toString() == "") {
                stopProgressbar()
                binding.createPassTIL1.helperText = "Required*"
            }
            else if (conformPassword.text.toString() == "") {
                stopProgressbar()
                binding.createPassTIL2.helperText = "Required*"
            }
            else if (newPassword.text.toString() != conformPassword.text.toString()) {
                stopProgressbar()
                binding.createPassTIL3.helperText = "Required*"
            }
            else if(!isValidString(binding.cpEdtxtEmail.text.toString())) {
                stopProgressbar()
                binding.createPassTIL1.helperText = "Enter valid Email"
            }
            else if (newPassword.text.toString() == conformPassword.text.toString()) {      //ACCEPT CONDITION
                startProgressbar()
                savePassword()
                signUp(email.text.toString().trim(), newPassword.text.toString().trim())
            }
        }
    }

    //  CHECKING ENTERED EMAIL VALIDITY
    private fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun savePassword() {
        FirebaseDatabase.getInstance().getReference("Users/$userID")
            .child("password").setValue(binding.cpEdtxtPassword.text.toString())
            .addOnCompleteListener {
                updateUI()
            }
    }

    private fun startProgressbar() {
        binding.progressbarCredential.visibility = View.VISIBLE
        binding.btnSignIn.visibility = View.GONE
    }

    private fun stopProgressbar() {
        binding.progressbarCredential.visibility = View.GONE
        binding.btnSignIn.visibility = View.VISIBLE
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    stopProgressbar()
                    updateUI()
                }
            }
            .addOnFailureListener {
                stopProgressbar()
                Log.e("Failed to Sign-In", it.message.toString())
            }
    }

    private fun updateUI() {
        startActivity(
            Intent(requireContext(), ProfileActivity::class.java)
                .putExtra("email", binding.cpEdtxtEmail.text.toString().trim())
                .putExtra("password", binding.cpEdtxtPassword.text.toString().trim())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

}