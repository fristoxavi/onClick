package com.example.fooddonation.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.fooddonation.R
import com.example.fooddonation.databinding.ActivityRegisterBinding
import com.example.fooddonation.home.HomeActivity
import com.example.fooddonation.login.LoginActivity
import com.example.fooddonation.utils.Common

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
   private lateinit var viewModel:RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupObserver()
        setUpNavigation()
    }
    private fun setupBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this)[(RegisterViewModel::class.java)]
        binding.registerVm = viewModel
        binding.lifecycleOwner = this
        binding.saveBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }
    }

    private  fun setupObserver(){
        viewModel.getClicked().observe(this) {
            startActivity(Intent(this,HomeActivity::class.java))
            Toast.makeText(this, "Registration Completed", Toast.LENGTH_SHORT).show()
        }
        viewModel.animation().observe(this) {
       if (it == true) {
           binding.animation.visibility = View.VISIBLE
       } else {
           binding.animation.visibility = View.GONE
       }
        }
        viewModel.getError().observe(this){
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
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val selectedValue = selectedRadioButton.text.toString()
            viewModel.radioGroup.value = selectedValue
            viewModel.radio(selectedValue)
            // Log.d("RadioButton", "setupObserver:$selectedValue ")
        }
    }
    private fun setUpNavigation(){
        binding.appBarLayout.setNavigationOnClickListener{
            onBackPressed()
        }
    }


}
