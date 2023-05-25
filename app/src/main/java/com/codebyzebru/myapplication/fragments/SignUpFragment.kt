@file:Suppress("DEPRECATION")

package com.codebyzebru.myapplication.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.databinding.FragmentSignUpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private lateinit var phoneNo: String
    lateinit var auth: FirebaseAuth

    lateinit var storedVerificationID: String
    lateinit var resendingToken: ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regEdtxtPhone.addTextChangedListener(editTextPhone)

        binding.btnNext.setOnClickListener {
            startProgressbar()

            if (binding.regEdtxtPhone.text.toString().trim() == "") {
                stopProgressbar()
                binding.til1.helperText = "Required*"
            }
            else {
                startSignIn(binding.regEdtxtPhone.text.toString())
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(
                    Intent(requireContext(), ProfileFragment::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                requireActivity().finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressbarSignUp.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
                Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationID: String, token: ForceResendingToken) {
                super.onCodeSent(verificationID, token)

                storedVerificationID = verificationID
                resendingToken = token

                val bundle = Bundle()
                bundle.putString("storedVerificationID", storedVerificationID)
                bundle.putString("phoneNumber", binding.regEdtxtPhone.text.toString())

                val fragmentOTP = OTPFragment()
                fragmentOTP.arguments = bundle

                //  1) Remove current attached fragment
                if(requireActivity().supportFragmentManager.findFragmentById(R.id.frameMain) != null) {
                    requireActivity().supportFragmentManager.beginTransaction().
                        remove(requireActivity().supportFragmentManager.findFragmentById(R.id.frameMain)!!)
                        .commit()
                }

                //  2) Replace it with `OTPFragment()`
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameMain, fragmentOTP)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private val editTextPhone: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding.til1.helperText = ""
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            if (binding.regEdtxtPhone.text.toString().trim().isNotEmpty()) {
                binding.til1.helperText = ""
            }
            else
            {
                binding.til1.helperText = "Required*"
            }

        }

    }

    private fun startSignIn(number: String) {
        if (number.isNotEmpty()) {
            phoneNo = "+91$number"
            sendOTP(phoneNo)
        }
        else {
            stopProgressbar()
            redToast()
        }
    }

    private fun sendOTP(number: String) {
        val option = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(2L, TimeUnit.MINUTES)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun redToast() {
        val toast: Toast = Toast.makeText(requireContext(), "Please, Enter phone number to proceed!!", Toast.LENGTH_SHORT)
        val view = toast.view

        //Gets the actual oval background of the Toast then sets the colour filter
        view!!.background.setColorFilter(resources.getColor(R.color.color4), PorterDuff.Mode.SRC_IN)

        //Gets the TextView from the Toast so it can be edited
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(resources.getColor(R.color.white))

        toast.show()
        stopProgressbar()
    }

    private fun startProgressbar() {
        binding.progressbarSignUp.visibility = View.VISIBLE
        binding.btnNext.visibility = View.GONE
    }

    private fun stopProgressbar() {
        binding.progressbarSignUp.visibility = View.GONE
        binding.btnNext.visibility = View.VISIBLE
    }

}