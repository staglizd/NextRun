package com.example.nextrun.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nextrun.R
import com.example.nextrun.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        group_loading_login.visibility = View.INVISIBLE

        btnLogin.setOnClickListener {
            val email = etMail.text.toString()
            val password = etPassword.text.toString()

            (activity as MainActivity?)!!.loginUser(email, password)
        }

        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        btnGoogle.setOnClickListener {
            (activity as MainActivity?)!!.googleSignIn()
        }
    }

}