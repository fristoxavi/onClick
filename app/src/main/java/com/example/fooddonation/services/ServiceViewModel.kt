package com.example.fooddonation.services

import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fooddonation.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class ServiceViewModel:ViewModel() {
   val sName = MutableLiveData<String>()
   val pPhoneNumber = MutableLiveData<String>()
   val desc = MutableLiveData<String>()
   private var errorMsg = MutableLiveData<String>()
   private val clickOption = MutableLiveData<Int>()
   private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
   private fun String.isValidPhoneNumber() = this.length in 8..10

   init {


        sName.value = ""
         desc.value = ""
         pPhoneNumber.value = ""




   }
   fun validate() {
      val service = sName.value?.toString()
      val description = desc.value?.toString()
      val phone = pPhoneNumber.value?.toString()

      when {
         service!!.isEmpty() -> errorMsg.value = "Please enter  Service Name"
         description!!.isEmpty() -> errorMsg.value = "Please Enter Description"
         phone!!.isEmpty() -> errorMsg.value = "Please Enter your Mobile Number"
         !phone.isValidPhoneNumber() -> errorMsg.value = "The Phone Number is incorrect"
         else -> {
            clickOption.value= 1
         }
      }


   }
 fun getError(): LiveData<String> = errorMsg
   fun getClicked(): MutableLiveData<Int> = clickOption
}