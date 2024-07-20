package com.example.fooddonation.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddonation.R
import com.example.fooddonation.chat.ChatActivity
import com.example.fooddonation.map.MapActivity
import com.example.fooddonation.model.PostModel
import com.example.fooddonation.model.ServiceModel
import com.example.fooddonation.services.ServiceDetailActivity

class ServiceAdapter ( private val context: Context,
    private val usersList: List<ServiceModel>
    ) :
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      //  val place: TextView = itemView.findViewById(R.id.place)
        val itemName: TextView = itemView.findViewById(R.id.title)
        val date: TextView = itemView.findViewById(R.id.date)
        val desc: TextView = itemView.findViewById(R.id.desc)
        //val location : ImageView = itemView.findViewById(R.id.mapImage)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = usersList[position]
        holder.itemName.text = users.serviceName
      //  holder.place.text = users.place
        holder.date.text = users.date
        holder.desc.text = users.description


            holder.itemView.setOnClickListener {
                val intent = Intent(context, ServiceDetailActivity::class.java)
                intent.putExtra("userName",users.userEmail)
                intent.putExtra("serviceName",users.serviceName)
                intent.putExtra("place",users.place)
                intent.putExtra("phoneNumber",users.phoneNumber)
                intent.putExtra("userId",users.userId)
                context.startActivity(intent)
            }

    }


    override fun getItemCount() = usersList.size
}