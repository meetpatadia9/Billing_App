package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.adapters.SignInSignUpAdapter
import com.codebyzebru.myapplication.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //  Inflate the layout for this fragment
        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInSignUpAdapter = SignInSignUpAdapter(childFragmentManager)
        signInSignUpAdapter.apply {
            addFragment(PhoneAuthFragment(), "Sign In")
            addFragment(SignUpFragment(), "Sign Up")
        }

        binding.tabLayoutSignUpSignIn.apply {
            setupWithViewPager(binding.viewPagerSignInSignUp)
            setSelectedTabIndicator(R.drawable.line_87)
        }

        binding.viewPagerSignInSignUp.apply {
            adapter = signInSignUpAdapter
            currentItem = 0
        }
    }
}