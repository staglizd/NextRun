package com.example.nextrun.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nextrun.R
import com.example.nextrun.adapters.FeedAdapter
import com.example.nextrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feed.*

@AndroidEntryPoint
class FeedFragment: Fragment(R.layout.fragment_feed) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var feedAdapter: FeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() = rvRunsFeed.apply {
        feedAdapter = FeedAdapter()
        adapter = feedAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

}