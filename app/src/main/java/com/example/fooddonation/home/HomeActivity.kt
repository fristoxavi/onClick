package com.example.fooddonation.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.R
import com.example.fooddonation.databinding.ActivityHomeBinding
import com.example.fooddonation.home.fragments.Homefragment
import com.example.fooddonation.post.PostFragment
import com.example.fooddonation.profile.ProfileActivity
import com.example.fooddonation.services.ServicesActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityHomeBinding
    private  lateinit var viewModel:HomeViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      setUpBinding()
        setUpFragments()
        replaceFragment(Homefragment())
        setUpNavigationDrawer()
        navigationClicks()
    }
    private fun setUpBinding(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        viewModel = ViewModelProvider(this)[(HomeViewModel::class.java)]
        binding.homeVm = viewModel
        binding.lifecycleOwner = this

    }
    private fun setUpFragments() {
        binding.GetAllFrameLayout.setOnItemSelectedListener {
            when (it) {
                R.id.home -> replaceFragment(Homefragment())
                R.id.add  ->replaceFragment(PostFragment())
                R.id.profile -> startActivity(Intent(this,ProfileActivity::class.java))

                else -> {

                }
            }

            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }
    private fun setUpNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout,0,0)
        drawerLayout.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }
    private fun navigationClicks() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.profile -> {
                    startActivity(Intent(this,ProfileActivity::class.java))
                    true
                }
                R.id.about -> {
                    callBottomSheet()
                    true
                }
                R.id.services -> {
                    startActivity(Intent(this,ServicesActivity::class.java))
                    true
                }

                else -> {
                    false
                }
            }
        }

    }
    private fun callBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.about_bottom_sheet, null)
        val text = view.findViewById<TextView>(R.id.textView)
        val ok = view.findViewById<AppCompatButton>(R.id.ok_button)
        ok.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }


}