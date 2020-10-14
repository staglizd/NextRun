package com.example.nextrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nextrun.R
import com.example.nextrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run) {

    private val viewModel: MainViewModel by viewModels()

}