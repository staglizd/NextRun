package com.example.nextrun.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nextrun.R
import com.example.nextrun.other.Constants
import com.example.nextrun.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

class RegisterFragment: Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().bottomAppBar.performHide()
        requireActivity().fabMain.hide()

        group_loading_register.visibility = View.INVISIBLE

        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        btnRegister.setOnClickListener {
            val email = etMail.text.toString()
            val password = etPassword.text.toString()

            (activity as MainActivity?)!!.registerUser(email, password)
        }
    }

}