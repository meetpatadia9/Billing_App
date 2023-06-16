@file:Suppress("PrivatePropertyName", "DEPRECATION")

package com.codebyzebru.myapplication.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.databinding.FragmentSignInBinding
import com.codebyzebru.myapplication.databinding.ToastErrorBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.loginEdtxtEmail
        val password = binding.loginEdtxtPassword

        //  JUMP TO CREDENTIAL PAGE
        binding.loginGetCredentials.setOnClickListener {
            getCredentials()
        }

        email.addTextChangedListener(emailFieldValidation)
        password.addTextChangedListener(passwordFieldValidation)

        //  EMAIL-PASSWORD LOGIN
        binding.btnLogin.setOnClickListener {
            if (email.text.toString() == "") {
                stopProgressbar()
                binding.til1.helperText = "Required*"
            }
            else if(!isValidString(email.text.toString())) {
                binding.til1.helperText = "Enter valid Email"
            }
            else if (password.text.toString() == "") {
                stopProgressbar()
                binding.til2.helperText = "Required*"
            }
            else if (email.text.toString() != "" && password.text.toString() != ""){   //ACCEPTING CONDITION
                startProgressbar()
                signIn(email.text.toString().trim(), password.text.toString().trim())
            }
        }

        //  GOOGLE LOGIN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.loginGoogle.setOnClickListener {
            binding.progressbarSignIn.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.GONE
            signInWithGoogle()
        }
    }

    //  EMAIL TEXT-WATCHER
    private val emailFieldValidation: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding.til1.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //  isEmpty
            if (binding.loginEdtxtEmail.text.toString().trim().isNotEmpty()) {
                binding.til1.helperText = ""
            }
            else {
                binding.til1.helperText = "Required*"
            }

            //  isValid
            if(!isValidString(binding.loginEdtxtEmail.text.toString())) {
                binding.til1.helperText = "Enter valid Email"
            }
            else {
                binding.til1.helperText = ""
            }
        }

        override fun afterTextChanged(s: Editable?) {
            //  isEmpty
            if (binding.loginEdtxtEmail.text.toString().trim().isNotEmpty()) {
                binding.til1.helperText = ""
            }
            else {
                binding.til1.helperText = "Required*"
            }

            //  isValid
            if(!isValidString(binding.loginEdtxtEmail.text.toString())) {
                binding.til1.helperText = "Enter valid Email"
            }
            else {
                binding.til1.helperText = ""
            }
        }
    }

    //  PASSWORD TEXT-WATCHER
    private val passwordFieldValidation: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding.til2.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.loginEdtxtPassword.text.toString().trim().isNotEmpty()) {
                binding.til2.helperText = ""
            }
            else {
                binding.til2.helperText = "Required*"
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (binding.loginEdtxtPassword.text.toString().trim().isNotEmpty()) {
                binding.til2.helperText = ""
            }
            else {
                binding.til2.helperText = "Required*"
            }
        }
    }

    //  CHECKING ENTERED EMAIL VALIDITY
    private fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    stopProgressbar()
                    updateUI()
                }
            }
            .addOnFailureListener {
                stopProgressbar()
                redToast(it.message.toString())
            }
    }

    private fun redToast(message: String) {
        val toastBinding = ToastErrorBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = message
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun startProgressbar() {
        binding.progressbarSignIn.visibility = View.VISIBLE
        binding.btnLogin.visibility = View.GONE
        binding.txtOR.visibility = View.GONE
        binding.loginGoogle.visibility = View.GONE
    }

    private fun stopProgressbar() {
        binding.progressbarSignIn.visibility = View.GONE
        binding.btnLogin.visibility = View.VISIBLE
        binding.txtOR.visibility = View.VISIBLE
        binding.loginGoogle.visibility = View.VISIBLE
    }

    private fun getCredentials() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameMain, CredentialsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credentials).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.progressbarSignIn.visibility = View.GONE
                        binding.btnLogin.visibility = View.VISIBLE
                        updateUI()
                    }
                    else {
                        Log.e("ACCOUNT FAIL", it.exception.toString())
                        Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else {
            Log.e("TASK FAIL", task.exception.toString())
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //  INTENT
    private fun updateUI() {
        startActivity(
            Intent(requireContext(), HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)   // all of the other activities on top of it will be closed
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)    // activity will become the start of a new task on this history stack
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)  // activity becomes the new root of an otherwise empty task, and any old activities are finished
        )
        requireActivity().finish()
    }

    //  ONE-TIME LOGIN FOR FIREBASE
    override fun onStart() {
        super.onStart()
        val signedInUser = auth.currentUser
        if (signedInUser != null) {
            updateUI()
        }
    }

}