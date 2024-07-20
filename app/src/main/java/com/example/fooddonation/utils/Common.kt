package com.example.fooddonation.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.example.fooddonation.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Common {

    companion object {
        var alertDialog: AlertDialog? = null
//            fun isInternetAvailable():Boolean{
//                val result: Boolean
//                val connectivityManager = context.getSystemService(
//                    Context.CONNECTIVITY_SERVICE
//                ) as ConnectivityManager
//                val networkCapabilities = connectivityManager.activeNetwork ?: return false
//                val actNw =
//                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
//                result = when {
//                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                    else -> false
//                }
//
//                return result
//            }

        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPassword(pass: String): Boolean {
            return pass.length in 6..16
        }

        fun materialalertdialog(
            context: Context, title: String, msg: String, postxt: String,
            negtxt: String, posclick: (DialogInterface, Int) -> Unit,
            negclick: ((DialogInterface, Int) -> Unit)?
        ) {
            if (alertDialog != null) {
                alertDialog?.cancel()
            }

            if (context is Activity) {
                if (!context.isFinishing) {
                    val builder =
                        MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded)
                    builder.setTitle(title)
                    builder.setCancelable(false)
                    builder.setMessage(msg)
                    if (postxt != "") {
                        builder.setPositiveButton(postxt, posclick)
                    }

                    if (negtxt != "") {
                        builder.setNegativeButton(negtxt, negclick)
                    }
                    alertDialog = builder.create()
                    alertDialog?.show()
                }
            }
        }
    }
}
