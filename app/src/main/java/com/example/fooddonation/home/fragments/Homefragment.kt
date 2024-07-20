package com.example.fooddonation.home.fragments

import android.app.Application
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.adapter.UserAdapter
import com.example.fooddonation.databinding.FragmentHomefragmentBinding
import com.example.fooddonation.home.HomeViewModel
import com.example.fooddonation.model.PostModel
import com.example.fooddonation.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Homefragment : Fragment() {
    lateinit var binding: FragmentHomefragmentBinding
    private lateinit var  viewModel:HomeViewModel
    private var adapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentHomefragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBinding()
        setUpRecycler()
        val pro = binding.animationView
    }
    private fun  setUpBinding(){
        val application = Application()
        viewModel = ViewModelProvider(this)[(HomeViewModel::class.java)]
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
                    getPosts(newText)
                }
                return true
            }
        })

        getPosts()
    }
    private fun getPosts(query: String? = null){
        val pro = binding.animationView
        pro.visibility = View.VISIBLE // Show the progress bar
        pro.startAnimation()
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val userid = firebase.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<PostModel>()
                for (child in snapshot.children) {
                    val users = child.getValue(PostModel::class.java)

                    if (users != null) {
                        if (query == null || users.itemName?.contains(
                                query,
                                ignoreCase = true
                            ) == true
                        ) {
                            usersList.add(users)
                        }
                    }
                    println("$usersList")


                }

                val adapter = UserAdapter(requireContext(), usersList,UserAdapter.MenuClickListener {
                        data, s ->
                    when(s){
                        "delete"->{
                            data.itemName?.let { deleteItem ->
                              deleteItem(deleteItem)
                            }
                        }
                    }
                })
                binding.recyler.adapter = adapter
                pro.stopAnimation()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ErrorMsg", "onCancelled: $error")
            }

        })

    }
    private fun deleteItem(item:String){
            // Get reference to the database
            val database = FirebaseDatabase.getInstance()

            // Get reference to the node you want to delete
            val nodeReference = database.reference.child("Posts").child(item) // Assuming "posts" is your main node

            // Remove the node
            nodeReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Deleted SuccessFully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    print(it.message)
                }
        }

}

