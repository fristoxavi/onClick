package com.example.fooddonation.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel:ViewModel() {

    lateinit var mAuth: FirebaseAuth
    var database = FirebaseDatabase.getInstance()
    var root = database.getReference("Users")
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    val email = MutableLiveData<String>()
    private var errorMsg = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val clickOption = MutableLiveData<Int>()
    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
    private fun String.isValidPassword() = this.length in 6..16


    init {

        email.value = ""
        password.value = ""
    }

    fun onClick(s: Int) {
        clickOption.value = s
    }

    fun validate() {

        val mail = email.value?.trim() ?: ""
        val pass = password.value?.trim() ?: ""
        when {
            mail.isEmpty() -> errorMsg.value = "Please Enter your Email Address"
            !mail.isValidEmail() -> errorMsg.value = "Please enter a valid Email Address"
            pass.isEmpty() -> errorMsg.value = "Please enter your password"
            !pass.isValidPassword() -> errorMsg.value = "Password length should be between 6 and 16"
            else -> {
                callLogin(mail,pass)
            }

        }
    }

    private fun callLogin(mail: String, pass: String) {
        mAuth = FirebaseAuth.getInstance()
        coroutineScope.launch {
            mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    clickOption.value = 1
                } else {
                    Log.e("Faillur", "validate: $it.exception ",)

                }
            }
        }
    }



    fun getError(): LiveData<String> = errorMsg
    fun getClicked(): MutableLiveData<Int> = clickOption
}

