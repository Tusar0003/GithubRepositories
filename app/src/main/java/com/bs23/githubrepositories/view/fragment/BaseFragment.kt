package com.bs23.githubrepositories.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bs23.githubrepositories.utils.autoCleared


abstract class BaseFragment<T : ViewDataBinding> constructor(@LayoutRes private val mContentLayoutId: Int) : Fragment() {

    var binding by autoCleared<T>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, mContentLayoutId, container, false)
        binding.root.filterTouchesWhenObscured = true
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }
}