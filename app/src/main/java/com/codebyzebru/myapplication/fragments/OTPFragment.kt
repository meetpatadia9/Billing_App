package com.codebyzebru.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.activities.ProfileActivity
import com.codebyzebru.myapplication.databinding.FragmentOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import `in`.aabhasjindal.otptextview.OTPListener

class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding

    private lateinit var storedVerificationID: String
    private lateinit var phoneNumber: String
    private lateinit var userEntered: String

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //  Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storedVerificationID = arguments?.getString("storedVerificationID")!!
        phoneNumber = arguments?.getString("phoneNumber")!!

        Log.d("storedVerificationID", storedVerificationID)
        Log.d("phoneNumber", phoneNumber)

        //  Get OTP number entered in `OTP view`
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String?) {
                userEntered = otp!!
            }
        }

        binding.btnVerify.setOnClickListener {
            startProgressbar()

            if (userEntered.isNotEmpty()) {
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationID, userEntered)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener {
                        startActivity(
                            Intent(requireContext(), ProfileActivity::class.java)
                                .putExtra("phoneNum", "+91$phoneNumber")
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)   // all of the other activities on top of it will be closed
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)    // activity will become the start of a new task on this history stack
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)  // activity becomes the new root of an otherwise empty task, and any old activities are finished
                        )
                        requireActivity().finish()
                    }
            }
            else {
                stopProgressbar()
            }
        }
    }

    private fun startProgressbar() {
        binding.btnVerify.visibility = View.GONE
        binding.progressbarOtp.visibility = View.VISIBLE
    }

    private fun stopProgressbar() {
        binding.btnVerify.visibility = View.VISIBLE
        binding.progressbarOtp.visibility = View.GONE
    }

}