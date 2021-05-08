package com.example.videolang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class HomepageActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

//        val bottomBar = findViewById<SmoothBottomBar>(R.id.bottomBar)

        val incommingMessages = findViewById<RecyclerView>(R.id.recycler_view_incommingmessage)
        incommingMessages.adapter = adapter
        incommingMessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"Successfull")
            val intent = Intent(this,Chats::class.java)

            val row = item as Messages
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForMessages()

        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        fab.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }
    fetchCurrentUser()

    checkUserLoggedIn()
    }
    class Messages(val chatMessage: Chats.ChatMessage): Item<GroupieViewHolder>(){
        var chatPartnerUser: User? = null

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.incoming_mesaage_textview).text = chatMessage.text
            val chatPartnerId: String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerId = chatMessage.toId
            }else{
                chatPartnerId = chatMessage.fromId
            }

            val userRef = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.findViewById<TextView>(R.id.incoming_username).text = chatPartnerUser?.firstName + " " + chatPartnerUser?.lastName
                    val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.sender_profile_picture)
                    Picasso.get().load(chatPartnerUser?.profilePictureUri).into(targetImageView)
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })

        }
        override fun getLayout(): Int {
            return R.layout.incomming_messages_row
        }
    }

    val chatMap = HashMap<String, Chats.ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        chatMap.values.forEach{
            adapter.add(Messages(it))
        }
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/incomingMessages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatMessage = snapshot.getValue(Chats.ChatMessage::class.java) ?: return
                chatMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(Chats.ChatMessage::class.java) ?: return
                chatMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.firstName}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
private fun checkUserLoggedIn(){
    val uid = FirebaseAuth.getInstance().uid
    if(uid == null){
        val intent =  Intent(this,RegistrationPage::class.java)
        startActivity(intent)
    }
}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

