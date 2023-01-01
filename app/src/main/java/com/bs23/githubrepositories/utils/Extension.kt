package com.ahoy.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

fun Fragment.navigate(direction: NavDirections) = this.findNavController().navigate(direction)
fun Fragment.navigateUp() = this.findNavController().navigateUp()

fun Fragment.showDialog(
    cancelable: Boolean = true, cancelableTouchOutside: Boolean = true,
    builderFunction: AlertDialog.Builder.() -> Any
) {
    this.requireContext().showDialog(cancelable, cancelableTouchOutside, builderFunction)
}

fun Context.showDialog(
    cancelable: Boolean = true, cancelableTouchOutside: Boolean = true,
    builderFunction: AlertDialog.Builder.() -> Any
) {
    val builder = AlertDialog.Builder(this)
    builder.builderFunction()
    val dialog = builder.create()
    dialog.setCancelable(cancelable)
    dialog.setCanceledOnTouchOutside(cancelableTouchOutside)
    dialog.show()
}

fun AlertDialog.Builder.positiveButton(
    text: String,
    handleClick: (i: Int) -> Unit = {}
) {
    this.setPositiveButton(text, { dialogInterface, i -> handleClick(i) })
}

fun AlertDialog.Builder.negativeButton(
    text: String,
    handleClick: (i: Int) -> Unit = {}
) {
    this.setNegativeButton(text, { dialogInterface, i -> handleClick(i) })
}

fun AlertDialog.Builder.neutralButton(text: String, handleClick: (i: Int) -> Unit = {}) {
    this.setNeutralButton(text, { dialogInterface, i -> handleClick(i) })
}

fun Fragment.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.fromParts("package", requireContext().packageName, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.toastLong(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

fun Context.toastLong(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val View.layoutInflater: LayoutInflater
    get() = this.context.layoutInflater
