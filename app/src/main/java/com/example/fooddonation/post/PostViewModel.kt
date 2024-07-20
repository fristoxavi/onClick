package com.example.fooddonation.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class PostViewModel: ViewModel() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
     var itemPlace = MutableLiveData<String>()
    var itemQuantity =  MutableLiveData<String>()
    var itemName =  MutableLiveData<String>()
    val clickOption = MutableLiveData<Int>()
    private var errorMsg = MutableLiveData<String>()
    init {

//        itemPlace.value = ""
//        itemQuantity.value = ""
//        itemName.value = ""
    }

    fun onClick(s: Int) {
        clickOption.value = s
    }

    fun addItems() {

//        val itemName = itemName.value?.trim() ?: ""
//        val itemQuantity = itemQuantity.value?.trim() ?: ""
//        val itemPlace = itemPlace.value?.trim() ?: ""
//
//        when {
//            itemName.isEmpty() -> errorMsg.value = "Please Enter your FoodName"
//            itemQuantity.isEmpty() -> errorMsg.value = "Please Enter your P"
//            itemPlace.isEmpty() -> errorMsg.value = "Please Enter your ProductName"
//            else -> {
//          //  addItems(itemName,itemQuantity,itemPlace)
//            }

        //}
    }
    private  fun addItems(name:String,quality:String,place:String) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
          val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("itemName", name)
        hashMap.put("itemQuantity", quality)
        hashMap.put("place", place)
        hashMap.put("addPost", "True")
        databaseReference.updateChildren(hashMap as Map<String, Any>)
            .addOnSuccessListener {
                clickOption.value = 1
            }
            .addOnFailureListener { e ->
                Log.e("Error", "addItems:$e ", )
            }
    }




    fun getError(): LiveData<String> = errorMsg
    fun getClicked(): MutableLiveData<Int> = clickOption
}