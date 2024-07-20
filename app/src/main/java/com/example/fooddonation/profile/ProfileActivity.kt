package com.example.fooddonation.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fooddonation.R
import com.example.fooddonation.databinding.ActivityProfileBinding
import com.example.fooddonation.login.LoginActivity
import com.example.fooddonation.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding :ActivityProfileBinding
    private lateinit var viewModel:ProfileViewModel
    lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpProfile()
    }
    private fun setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewModel = ViewModelProvider(this)[(ProfileViewModel::class.java)]
        binding.profileVm = viewModel
        binding.lifecycleOwner = this
        binding.topAppBar.setNavigationOnClickListener{
            onBackPressed()
        }
        binding.logout.setOnClickListener {
            logOut()
        }
        binding.topAppBar.setNavigationOnClickListener {

            onBackPressed()
        }

    }

    private fun setUpProfile() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener {
            val image = binding.userImage
            val name = binding.username
            val email = binding.email

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                name.setText("${user?.UserName}")
                email.setText("${firebaseUser.email}")
                if (user?.ProfileImage == "") {
                    image.setImageResource(R.drawable.profile)
                }
                else{
                    Glide.with(this@ProfileActivity).load(user?.ProfileImage).into(image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
        binding.editPhoto.setOnClickListener {
            chooseImage()
        }
        binding.save.setOnClickListener {
            uploadImage()
            binding.animation.visibility = View.VISIBLE
        }

    }
    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.userImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if (filePath != null) {

            val ref: StorageReference = storageRef.child("UserProfile/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
//                    val hashMap:HashMap<String,String> = HashMap()
//                    hashMap.put("ProfileImage",filePath.toString())
//                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Log.e("TAG", "uploadImage:$downloadUrl ", )
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap.put("ProfileImage", downloadUrl)
                        databaseReference.updateChildren(hashMap as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(applicationContext, "Profile image updated", Toast.LENGTH_SHORT).show()
                                binding.animation.visibility = View.GONE
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(applicationContext, "Failed to update profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }


                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed" + it.message, Toast.LENGTH_SHORT)
                        .show()

                }

        }
    }
    private fun logOut(){

        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }



}
