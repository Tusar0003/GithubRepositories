package com.bs23.githubrepositories.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.clearDecorations() {
    if (itemDecorationCount > 0) {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }
}

@BindingAdapter("itemDecoration")
fun RecyclerView.addDividerItemDecorator(@RecyclerView.Orientation orientation: Int) {
    addItemDecoration(DividerItemDecoration(context, orientation))
}
