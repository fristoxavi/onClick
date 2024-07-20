package com.example.fooddonation.services

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.R
import com.example.fooddonation.adapter.ServiceAdapter
import com.example.fooddonation.adapter.UserAdapter
import com.example.fooddonation.databinding.ActivityMainBinding
import com.example.fooddonation.databinding.ActivityServicesBinding
import com.example.fooddonation.login.LoginActivity
import com.example.fooddonation.model.PostModel
import com.example.fooddonation.model.ServiceModel
import com.example.fooddonation.utils.Common
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class ServicesActivity : AppCompatActivity() {
    lateinit var binding: ActivityServicesBinding
    lateinit var viewModel: ServiceViewModel
    private var errorMsg = MutableLiveData<String>()
    private fun String.isValidPhoneNumber() = this.length in 8..10
    private lateinit var databaseReference: DatabaseReference
    var database = FirebaseDatabase.getInstance()
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
       // setUpObserver()
        setUpRecycler()
    }
    private fun setUpRecycler(){
        val pro = binding.animationView
        pro.visibility = View.VISIBLE // Show the progress bar
        pro.startAnimation()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getServices(newText)
                }
                return true
            }
        })

        getServices()
    }
    private fun getServices(query: String? = null){
        val pro = binding.animationView
        pro.visibility = View.VISIBLE // Show the progress bar
        pro.startAnimation()
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val userid = firebase.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Services")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<ServiceModel>()
                for (child in snapshot.children) {
                    val users = child.getValue(ServiceModel::class.java)

                    if (users != null) {
                        if (query == null || users.serviceName?.contains(
                                query,
                                ignoreCase = true
                            ) == true
                        ) {
                            usersList.add(users)
                        }
                    }
                    println("$usersList")


                }

                val adapter = ServiceAdapter(this@ServicesActivity, usersList)
                binding.recyler.adapter = adapter
                pro.stopAnimation()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ErrorMsg", "onCancelled: $error")
            }

        })


    }

    private fun setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_services)
        viewModel = ViewModelProvider(this)[(ServiceViewModel::class.java)]
        binding.serviceVm = viewModel
        binding.addServices.setOnClickListener {
            callBottomSheet()
        }
    }

    private fun setUpObserver() {
        viewModel.getClicked().observe(this) {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        }
        viewModel.getError().observe(this) {
            Common.materialalertdialog(
                context = this,
                title = "",
                msg = it,
                postxt = "OK",
                negtxt = "",
                posclick = { dialogInterface, _ ->
                    dialogInterface.cancel()
                },
                negclick = null
            )
        }
    }

    private fun callBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.service_bottom_sheet, null)
        val save = view.findViewById<AppCompatButton>(R.id.save_btn)
        val sName = view.findViewById<TextInputEditText>(R.id.serviceName)
        val desc = view.findViewById<TextInputEditText>(R.id.desc)
        val phone = view.findViewById<TextInputEditText>(R.id.phoneNumber)
        val place = view.findViewById<TextInputEditText>(R.id.place)
        fun String.isValidPhoneNumber() = this.length in 8..10


        save.setOnClickListener {
            val name = sName.text.toString()
            val description = desc.text.toString()
            val phoneNumber = phone.text.toString()
            val places = place.text.toString()


            when {
                name!!.isEmpty() -> errorMsg.value = "Please enter  Service Name"
                description!!.isEmpty() -> errorMsg.value = "Please Enter Description"
                phoneNumber!!.isEmpty() -> errorMsg.value = "Please Enter your Mobile Number"
                places!!.isEmpty() -> errorMsg.value = "Please Enter your Place"

                // !phoneNumber.isValidPhoneNumber() -> errorMsg.value = "The Phone Number is incorrect"
                else -> {
                    callService(name, description, phoneNumber, places)
                }
            }


        }
//            Common.materialalertdialog(
//                this,"","Pressed","Ok","Cancel",  posclick = { dialogInterface, _ ->
//                    dialogInterface.cancel()
//                },
//                negclick = null
//            )
//        }
        dialog.setContentView(view)
        dialog.show()
    }

    fun callService(name: String, des: String, phone: String, place: String) {
        storage = FirebaseStorage.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageRef = storage.reference
        val root = database.getReference("Services").child(name)
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Services").child(name)
        val date = getCurrentTimeAndDate()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("userId", firebaseUser.uid)
        hashMap.put("date", date)
        hashMap.put("userEmail", firebaseUser.email.toString())
        hashMap.put("serviceName", name)
        hashMap.put("phoneNumber", phone)
        hashMap.put("place", place)
        hashMap.put("description", des)
        root.setValue(hashMap).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Service Added SuccessFully", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getCurrentTimeAndDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EEE MMM dd HH:mm yyyy")
        return format.format(calendar.time)
    }
}