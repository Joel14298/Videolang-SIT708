package com.example.videolang

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class Chats : AppCompatActivity() {

    var toUser: User? =null

    companion object{
        val TAG = "Chatlog"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        val recycle = findViewById<RecyclerView>(R.id.chat_recyclerview)
        recycle.adapter =adapter
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        if (toUser != null) {
            supportActionBar?.title = toUser!!.firstName + " " + toUser!!.lastName
        }
        listenForMessages()
        val send = findViewById<Button>(R.id.chat_send_button)
        send.setOnClickListener {
            Log.d(TAG,"Attempt to send message")
            performSendMessage()
        }
    }
    private fun  listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatmessage = snapshot.getValue(ChatMessage::class.java)

                if(chatmessage != null){
                    Log.d(TAG,chatmessage.text)

                    if(chatmessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = HomepageActivity.currentUser
                        if(currentUser != null){
                            adapter.add(ChatItem2(chatmessage.text, currentUser))
                        }
                    } else{
                        if(toUser != null){
                            adapter.add(ChatItem(chatmessage.text, toUser!!))
                        }
                    }
                }
                val endChat = findViewById<RecyclerView>(R.id.chat_recyclerview).smoothScrollToPosition(adapter.itemCount -1)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    class ChatMessage(val id: String,val text: String, val fromId: String, val toId: String, val timestamp: Long){
        constructor(): this("","","","",-1)
    }
    private fun performSendMessage(){
        val chatEditText = findViewById<EditText>(R.id.chat_edittext).text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

        if(fromId == null)return
        if(toId == null)return

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, chatEditText, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
                val clearText = findViewById<EditText>(R.id.chat_edittext)
                clearText.setText(" ")
                val recycle = findViewById<RecyclerView>(R.id.chat_recyclerview).scrollToPosition(adapter.itemCount -1)
            }
        toReference.setValue(chatMessage)

      val messagesRef = FirebaseDatabase.getInstance().getReference("/incomingMessages/$fromId/$toId")
        messagesRef.setValue(chatMessage)

        val messagesToRef = FirebaseDatabase.getInstance().getReference("/incomingMessages/$toId/$fromId")
        messagesToRef.setValue(chatMessage)
    }
}
class ChatItem(val text:String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.findViewById<TextView>(R.id.textview_from_row).text = text

        val uri = user.profilePictureUri
        val targetUri = viewHolder.itemView.findViewById<ImageView>(R.id.imageView1)
        Picasso.get().load(uri).into(targetUri)

    }
    override fun getLayout(): Int {
    return R.layout.chat_dummy2
    }
}

class ChatItem2(val text:String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.itemView.findViewById<TextView>(R.id.textview_to_row).text =  text

        val uri = user.profilePictureUri
        val targetUri = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(uri).into(targetUri)
    }

    override fun getLayout(): Int {
        return R.layout.chat_dummy
    }
}