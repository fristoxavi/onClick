package com.example.fooddonation.post

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.databinding.FragmentPostBinding
import com.example.fooddonation.home.HomeActivity
import com.example.fooddonation.utils.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PostFragment : Fragment() {
    lateinit var binding: FragmentPostBinding
    lateinit var viewModel: PostViewModel
    private lateinit var databaseReference: DatabaseReference
    var database = FirebaseDatabase.getInstance()
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()
        setUpObserve()
        //val pro = binding.an
        binding.add.setOnClickListener {
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
        var contentResolver = activity?.contentResolver
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

    private fun setUpBinding() {
        val application = Application()
        viewModel = ViewModelProvider(requireActivity())[(PostViewModel::class.java)]
        binding.postVm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setUpObserve() {
        viewModel.getClicked().observe(requireActivity()) {
            startActivity(Intent(requireContext(), HomeActivity::class.java))

        }

        viewModel.getError().observe(requireActivity()) {
            Common.materialalertdialog(
                context = requireContext(),
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

    private fun uploadImage() {
        storage = FirebaseStorage.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageRef = storage.reference
        if (filePath != null) {
            val productName = binding.productName.text.toString()
            val quantity = binding.itemQuantity.text.toString()
            val place = binding.itemPlace.text.toString()
            val date = getCurrentTimeAndDate()
            val root = database.getReference("Posts").child(productName)
            databaseReference =
                FirebaseDatabase.getInstance().getReference("Posts").child(productName)
            val hashMap: HashMap<String, String> = HashMap()
            hashMap.put("userId", firebaseUser.uid)
            hashMap.put("date", date)
            hashMap.put("userEmail", firebaseUser.email.toString())
            hashMap.put("itemName", productName)
            hashMap.put("itemQuantity", quantity)
            hashMap.put("place", place)
            root.setValue(hashMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                val ref: StorageReference = storageRef.child("Posts" + UUID.randomUUID().toString())
                ref.putFile(filePath!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            Log.e("TAG", "uploadImage:$downloadUrl ")
                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap.put("postImage", downloadUrl)
                            databaseReference.updateChildren(hashMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Image Added Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.animation.visibility = View.GONE
                                    clear()

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to Add Image: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }


                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Failed" + it.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }

            }
        }
    }

    private fun getCurrentTimeAndDate(): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("EEE MMM dd HH:mm yyyy")
        return format.format(calendar.time)
    }
    private fun clear(){
      binding.productName.text?.clear()
        binding.itemQuantity.text?.clear()
        binding.itemPlace.text?.clear()

    }
}


