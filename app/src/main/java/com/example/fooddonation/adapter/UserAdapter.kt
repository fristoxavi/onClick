package com.example.fooddonation.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddonation.R
import com.example.fooddonation.chat.ChatActivity
import com.example.fooddonation.map.MapActivity
import com.example.fooddonation.model.PostModel
import com.example.fooddonation.model.UserModel
import com.example.fooddonation.utils.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val context: Context,
    private val usersList: List<PostModel>,
    private val clickListener: MenuClickListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val place: TextView = itemView.findViewById(R.id.item_place)
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val itemName: TextView = itemView.findViewById(R.id.item_Name)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val date: TextView = itemView.findViewById(R.id.date)
        val postName: TextView = itemView.findViewById(R.id.posted_by)
        val location :ImageView = itemView.findViewById(R.id.mapImage)
        val delete:ImageView = itemView.findViewById(R.id.item_delete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.users_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = usersList[position]
        holder.itemName.text = users.itemName
        holder.place.text = users.place
        holder.quantity.text = "Quantity ${users.itemQuantity}"
       holder.date.text = users.date
        holder.postName.text = "Posted By ${users.userEmail}"


        val contentUri = Uri.parse(users.postImage)
        Glide.with(context).asBitmap()
            .load(contentUri)
            .placeholder(R.drawable.donate) // Added an error placeholder in case the image fails to load
            .into(holder.image)
//        Log.e("ProfileImageDoctor", "onBindViewHolder: ${diseases.ProfileImage}", )

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId",users.userId)
            intent.putExtra("userName",users.userEmail)
            intent.putExtra("userImage",users.postImage)
            context.startActivity(intent)
        }
        holder.location.setOnClickListener {
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra("place",users.place)
            context.startActivity(intent)
        }
         val userEmail = firebase.email
        if (usersList[position].userEmail == userEmail) {
            holder.delete.visibility = View.VISIBLE
        } else {
            holder.delete.visibility = View.GONE
        }
        holder.delete.setOnClickListener{

        val popupMenu = PopupMenu(context, holder.delete)
        popupMenu.inflate(R.menu.edit_menu)


        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    Common.materialalertdialog(context,
                        "Delete Menu",
                        "Are you sure you want to delete this menu",
                        "Delete",
                        "Cancel",
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.cancel()
                            clickListener.onClick(usersList[position], "delete")
                        }) { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.cancel()
                    }

                }
            }

            true


        }

        popupMenu.show()
    }


}
    class MenuClickListener(val clickListener: (data: PostModel, s: String) -> Unit) {
        fun onClick(data: PostModel, s: String) {
            clickListener(data, s)
        }
    }

    override fun getItemCount() = usersList.size
}