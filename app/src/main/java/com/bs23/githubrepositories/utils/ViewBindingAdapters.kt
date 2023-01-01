package com.bs23.githubrepositories.utils

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bs23.githubrepositories.R
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat


@BindingAdapter("goneUnless")
fun View.goneUnless(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("loadImage")
fun loadImage(view: ImageView, imageUrl: String?) {
    Glide.with(view.context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_image_24)
        .into(view)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("bindDay")
fun TextView.bindDay(date: String?) {
    date?.let {
        text = try{
            val originalDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val d = originalDateFormat.parse(date)
            val expectedDateFormat = SimpleDateFormat("EEEE")
            val goal = expectedDateFormat.format(d!!)
            goal
        } catch (e : java.lang.Exception){
            date
        }
    }
}
