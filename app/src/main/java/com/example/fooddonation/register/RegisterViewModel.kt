package com.example.fooddonation.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterViewModel:ViewModel() {
    lateinit var mAuth: FirebaseAuth
    var database = FirebaseDatabase.getInstance()
    var root = database.getReference("Users")
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    val pName = MutableLiveData<String>()
    val pEmail = MutableLiveData<String>()
    val isAnimationVisible = MutableLiveData<Boolean>()
    val radioGroup = MutableLiveData<String>()
    private val selectedValues = MutableLiveData<String>()
    private var errorMsg = MutableLiveData<String>()
    val pPassword = MutableLiveData<String>()
    val pPhoneNumber = MutableLiveData<String>()
    private val clickOption = MutableLiveData<Int>()
    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
    private fun String.isValidPhoneNumber() = this.length in 8..10
    private fun String.isValidPassword() = this.length in 6..16


    init {

        pName.value = ""
        pEmail.value = ""
        pPassword.value = ""
        pPhoneNumber.value = ""
        selectedValues.value = ""


    }

    fun insertPatient() {
        val name = pName.value?.toString()
        val email = pEmail.value?.toString()
        val password = pPassword.value?.toString()
        val phone = pPhoneNumber.value?.toString()
        val radio = selectedValues.value?.toString()


        when {
            name!!.isEmpty() -> errorMsg.value = "Please enter your Name"
            email!!.isEmpty() -> errorMsg.value = "Please Enter your Email Address"
            !email.isValidEmail() -> errorMsg.value = "Please enter a valid Email Address"
            password!!.isEmpty() -> errorMsg.value = "Please enter your password"
            !password.isValidPassword() -> errorMsg.value =
                "Password length should be between 6 and 16"
            phone!!.isEmpty() -> errorMsg.value = "Please Enter your Mobile Number"
            !phone.isValidPhoneNumber() -> errorMsg.value = "The Phone Number is incorrect"
            radio!!.isEmpty() -> {
                errorMsg.value = "Please Select  Gender"
            }

            else -> {
                insertUsers(email, password, name, phone, radio.toString())
                isAnimationVisible.value = true
            }


        }


    }

    private fun insertUsers(
        email: String,
        password: String,
        name: String,
        phone: String,
        radio: String
    ) {

        coroutineScope.launch {
            mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    insertPatient(name, phone, radio)
                } else {
                    Log.e("Faillur", "validate: $it.exception ")

                }
            }


        }
    }

    private fun insertPatient(name: String, phone: String, radio: String) {
        val user: FirebaseUser? = mAuth.currentUser
        val userId: String = user!!.uid
        val root = database.getReference("Users").child(userId)
        Log.e("RadioValues", "Gender:$radio")
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("UserId",userId)
        hashMap.put("UserName", name)
        hashMap.put("UserPhoneNumber", phone)
        hashMap.put("Gender", radio)
        hashMap.put("ProfileImage","")
        hashMap.put("latitude","")
        hashMap.put("longitude","")
        hashMap.put("itemName","")
        hashMap.put("itemQuantity","")
        hashMap.put("place","")
        hashMap.put("addPost","")

        root.setValue(hashMap).addOnCompleteListener {
            if (it.isSuccessful) {
                clickOption.value = 1
                isAnimationVisible.value = false
                pName.value = ""
                pEmail.value = ""
                pPassword.value = ""
                pPhoneNumber.value = ""
                selectedValues.value = ""
            }
        }

    }

    fun getError(): LiveData<String> = errorMsg
    fun getClicked(): MutableLiveData<Int> = clickOption
    fun animation():MutableLiveData<Boolean> = isAnimationVisible


    fun radio(selectedValue: String) {
        selectedValues.value = selectedValue

        Log.e("OnViewModel", "radio: $selectedValue")
    }
}
