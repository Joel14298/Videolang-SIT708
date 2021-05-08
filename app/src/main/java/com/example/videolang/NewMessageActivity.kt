 package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView


 class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message_acitivity)

        supportActionBar?.title = "Select User"
        fetchData()
    }
     companion object{
         val USER_KEY = "USER_KEY"
     }

    private fun fetchData(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
//          val adapter = GroupAdapter<GroupieViewHolder>()
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuFile = findViewById<RecyclerView>(R.id.recycleview_newmessage)
                val adapter = GroupAdapter<GroupieViewHolder>()
                menuFile.adapter = adapter
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }

                    Log.d("NewMessageActivity",it.toString())
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context,Chats::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    viewHolder.itemView.findViewById<TextView>(R.id.username_data_textview).text = user.firstName + " " +user.lastName
        Picasso.get().load(user.profilePictureUri).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_userdata))
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
