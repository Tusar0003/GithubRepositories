package com.bs23.githubrepositories.view.fragment.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bs23.githubrepositories.R
import com.bs23.githubrepositories.databinding.FragmentDetailsBinding
import com.bs23.githubrepositories.model.Items
import com.bs23.githubrepositories.utils.getFormattedDate
import com.bs23.githubrepositories.utils.navigateUp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )

        arguments.let {
            it?.getSerializable("item").let { item ->
                binding.item = item as Items?
                binding.updatedDateTextView.text = item?.updatedAt?.getFormattedDate()
            }
        }

        binding.cancelImageView.setOnClickListener {
            navigateUp()
        }

        return binding.root
    }
}