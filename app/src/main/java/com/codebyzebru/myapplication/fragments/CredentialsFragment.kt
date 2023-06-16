package com.codebyzebru.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.activities.ProfileActivity
import com.codebyzebru.myapplication.databinding.FragmentCredentialsBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class CredentialsFragment : Fragment() {

    lateinit var binding: FragmentCredentialsBinding

    lateinit var auth: FirebaseAuth

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
        //  Inflate the layout for this fragment
        binding = FragmentCredentialsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = binding.cpEdtxtEmail
        val newPassword = binding.cpEdtxtPassword
        val conformPassword = binding.cpEdtxtConformPass

        email.addTextChangedListener(textWatcher)
        newPassword.addTextChangedListener(textWatcher)
        conformPassword.addTextChangedListener(textWatcher)

        binding.btnSignIn.setOnClickListener {
            if (email.text.toString() == "") {
                stopProgressbar()
                binding.createPassTIL1.helperText = "Email is required"
            }
            else if (newPassword.text.toString() == "") {
                stopProgressbar()
                binding.createPassTIL2.helperText = "Password is required"
            }
            else if (conformPassword.text.toString() == "") {
                stopProgressbar()
                binding.createPassTIL3.helperText = "Conform password is required"
            }
            else if (newPassword.text.toString() != conformPassword.text.toString()) {
                stopProgressbar()
                binding.createPassTIL3.helperText = "Password must be same"
            }
            else if(!isValidString(binding.cpEdtxtEmail.text.toString())) {
                stopProgressbar()
                binding.createPassTIL1.helperText = "Enter valid Email"
            }
            else if (newPassword.text.toString() == conformPassword.text.toString()) {      //ACCEPT CONDITION
                startProgressbar()
                signUp(email.text.toString().trim(), newPassword.text.toString().trim())
            }
        }
    }

    //  CHECKING ENTERED EMAIL VALIDITY
    private fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
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

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding.createPassTIL1.helperText = ""
            binding.createPassTIL2.helperText = ""
            binding.createPassTIL3.helperText = ""

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (s.hashCode()) {
                binding.cpEdtxtEmail.text.hashCode() -> {
                    if (binding.cpEdtxtEmail.text.toString().trim() == "") {
                        binding.createPassTIL1.helperText = "Email is required"
                    }
                }
                binding.cpEdtxtEmail.text.hashCode() -> {
                    if (!isValidString(binding.cpEdtxtEmail.text.toString())) {
                        binding.createPassTIL1.helperText = "Enter valid email"
                    }
                }
                binding.cpEdtxtPassword.text.hashCode() -> {
                    if (binding.cpEdtxtPassword.text.toString().trim() == "") {
                        binding.createPassTIL2.helperText = "Password is required"
                    }
                }
                binding.cpEdtxtConformPass.text.hashCode() -> {
                    if (binding.cpEdtxtConformPass.text.toString().trim() == "") {
                        binding.createPassTIL3.helperText = "Conform password is required"
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (s.hashCode()) {
                binding.cpEdtxtEmail.text.hashCode() -> {
                    if (binding.cpEdtxtEmail.text.toString().trim() == "") {
                        binding.createPassTIL1.helperText = "Email is required"
                    }
                }
                binding.cpEdtxtEmail.text.hashCode() -> {
                    if (!isValidString(binding.cpEdtxtEmail.text.toString())) {
                        binding.createPassTIL1.helperText = "Enter valid email"
                    }
                }
                binding.cpEdtxtPassword.text.hashCode() -> {
                    if (binding.cpEdtxtPassword.text.toString().trim() == "") {
                        binding.createPassTIL2.helperText = "Enter password"
                    }
                }
                binding.cpEdtxtConformPass.text.hashCode() -> {
                    if (binding.cpEdtxtConformPass.text.toString().trim() == "") {
                        binding.createPassTIL3.helperText = "Enter conform password"
                    }
                }
            }
        }
    }
}