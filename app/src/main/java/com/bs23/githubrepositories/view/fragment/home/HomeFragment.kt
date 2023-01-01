package com.bs23.githubrepositories.view.fragment.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.ahoy.weatherapp.utils.launchAndRepeatWithViewLifecycle
import com.ahoy.weatherapp.utils.positiveButton
import com.ahoy.weatherapp.utils.showDialog
import com.bs23.githubrepositories.R
import com.bs23.githubrepositories.databinding.FragmentHomeBinding
import com.bs23.githubrepositories.view.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.title = getString(R.string.home)
            it.elevation = 0F
        }
        binding.viewModel = viewModel

        launchAndRepeatWithViewLifecycle {
            launch {
                viewModel.message.collect {
                    showDialog {
                        setMessage(it)
                        positiveButton(getString(R.string.ok)) {}
                    }
                }
            }
        }
    }
}