package com.bs23.githubrepositories.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bs23.githubrepositories.R
import com.bs23.githubrepositories.databinding.FragmentHomeBinding
import com.bs23.githubrepositories.utils.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchAndRepeatWithViewLifecycle {
            launch {  }
        }
    }
}