package org.d3ifcool.hystorms.constant

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import org.d3ifcool.hystorms.model.Nutrition

class Action {
    companion object {
        fun showSnackBar(
            layout: View,
            content: String?,
            duration: Int,
            textAction: String = "",
            resId: Int? = null,
            listener: View.OnClickListener? = null
        ) {
            val snackbar = Snackbar.make(layout, content ?: "Error tidak diketahui", duration)
            if (listener != null) {
                when {
                    textAction != "" -> snackbar.setAction(textAction, listener)
                    resId != null -> snackbar.setAction(resId, listener)
                }
            }
            snackbar.run {
                addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
//                        EspressoIdlingResource.decrement()
                    }

                    override fun onShown(sb: Snackbar?) {
                        super.onShown(sb)
//                        EspressoIdlingResource.increment()
                    }
                })
                show()
            }
        }

        fun showLog(message: String) {
            Log.d(Constant.APP_DEBUG, message)
        }

        fun showToast(
            context: Context,
            text: String,
            length: Int = Toast.LENGTH_SHORT
        ) {
            Toast.makeText(context, text, length).show()
        }

        fun showDialog(
            title: String,
            desc: String,
            context: Context,
            confirmListener: SweetAlertDialog.OnSweetClickListener,
            confirmText: String = "",
            confirmResId: Int? = null,
            cancelListener: SweetAlertDialog.OnSweetClickListener? = null,
            cancelText: String = "",
            cancelResId: Int? = null,
            type: Int = SweetAlertDialog.WARNING_TYPE
        ) {
            val dialog = SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(desc)
            when {
                confirmText != "" -> dialog.setConfirmButton(confirmText, confirmListener)
                confirmResId != null -> dialog.setConfirmButton(confirmResId, confirmListener)
                else -> dialog.setConfirmButton("Ok", confirmListener)
            }
            if (cancelListener != null) {
                when {
                    cancelText != "" -> dialog.setCancelButton(confirmText, cancelListener)
                    cancelResId != null -> dialog.setCancelButton(cancelResId, cancelListener)
                    else -> dialog.setConfirmButton("Batal", cancelListener)
                }
            }
            dialog.show()
        }

        fun checkPermission(context: Context): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            return true
        }

        fun showAreYouSureDialog(
            title: String,
            desc: String,
            context: Context,
            confirmListener: DialogInterface.OnClickListener,
            confirmText: String = "",
            confirmResId: Int? = null,
            cancelListener: DialogInterface.OnClickListener? = null,
            cancelText: String = "",
            cancelResId: Int? = null,
        ): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(desc)
            when {
                confirmText != "" -> builder.setPositiveButton(confirmText, confirmListener)
                confirmResId != null -> builder.setPositiveButton(confirmResId, confirmListener)
                else -> builder.setPositiveButton("Ya", confirmListener)
            }
            if (cancelListener != null) {
                when {
                    cancelText != "" -> builder.setNegativeButton(cancelText, cancelListener)
                    cancelResId != null -> builder.setNegativeButton(cancelResId, cancelListener)
                    else -> builder.setNegativeButton("Batal", cancelListener)
                }
            }
            return builder
        }

        fun nutritionListContain(list: List<Nutrition>, name: String?): Boolean {
            return list.stream().anyMatch { nutrition ->
                nutrition.owner == name
            }
        }
    }
}