package com.example.videolang.activites

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.videolang.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class VideocallActivity : AppCompatActivity() {
//    companion object{
//        val USER_KEY = "USER_KEY"
//
//    }
     var toUser: User? =null
//        val fromId = FirebaseAuth.getInstance().uid
//         val toId = toUser?.uid

//val firebaseRef = Firebase.database.getReference("/videocall")
//
//    val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
//    val requestCode = 1
//    var isAudio = true
//    var isVideo= true
//    var isPeerConnected = false
//    val webView: WebView = findViewById<WebView>(R.id.webView)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)

        supportActionBar?.title = "Select User"
        fetchData()

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)


    }
//        private fun askPermission() {
//        ActivityCompat.requestPermissions(this,permissions,requestCode)
//    }

//    private fun isPermissionsGranted(): Boolean {
//        permissions.forEach {
//            if(ActivityCompat.checkSelfPermission(this,it) != PackageManager.PERMISSION_GRANTED)
//                return false
//        }
//        return true
//    }


    private fun fetchData() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
//        val fromId =  FirebaseAuth.getInstance().uid

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val callMenuItem = findViewById<RecyclerView>(R.id.recycleview_videocall)
                val adapter = GroupAdapter<GroupieViewHolder>()
                callMenuItem.adapter = adapter
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                    Log.d("CallActivity",it.toString())
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

//                    if(isPermissionsGranted()){
//                        askPermission()
//                    }
//                    setupWebView()
//                    sendCallRequest()
////                    val intent = Intent(view.context,PeerCallActivity::class.java)
////                    intent.putExtra(USER_KEY,userItem.user)
////                    startActivity(intent)
////                    finish()
//                    val toggleAudio = findViewById<ImageView>(R.id.toggleAudio)
//                    val toggleVideo = findViewById<ImageView>(R.id.toggleVideo)
//
//                    toggleAudio.setOnClickListener{
//                        isAudio =! isAudio
//                        callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
//                        toggleAudio.setImageResource(if(isAudio) R.drawable.ic_mic else R.drawable.ic_mic_off)
//                    }
//                    toggleVideo.setOnClickListener {
//                        isVideo =! isVideo
//                        callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
//                        toggleAudio.setImageResource(if(isVideo) R.drawable.ic_videocam else R.drawable.ic_videocam_off)
//                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

//    private fun sendCallRequest() {
//       if(isPeerConnected == true){
//           Toast.makeText(this,"Not Connected", Toast.LENGTH_SHORT).show()
//           return
//       }
//        if (toId != null) {
//            firebaseRef.child(toId).child("incoming").setValue(fromId)
//            firebaseRef.child(toId).child("isAvailable").addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.value.toString() == "true"){
//                        listenForConId()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
//        }
//    }
//
//    private fun listenForConId() {
//        if (toId != null) {
//            firebaseRef.child(toId).child("connId").addValueEventListener(object:ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.value == null){
//                        return
//                    }
////                    switchToControls()
////                    callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//        }
//    }

//    private fun setupWebView() {
////        val webView = findViewById<WebView>(R.id.webView)
//        webView.webChromeClient = object: WebChromeClient(){
//            override fun onPermissionRequest(request: PermissionRequest?) {
//                request?.grant(request.resources)
//                webView.settings.javaScriptEnabled = true
//                webView.settings.mediaPlaybackRequiresUserGesture = false
//
//                webView.addJavascriptInterface(JavascriptInterface(this@VideocallActivity),"Android")
//
//                loadVideoCall()
//            }
//        }
//    }
//    private fun loadVideoCall() {
//        val filePath = "file:android_asset/call.html"
//
//        webView.loadUrl(filePath)
//
//
//        webView.webViewClient = object : WebViewClient(){
//            override fun onPageFinished(view: WebView?, url: String?) {
////                super.onPageFinished(view, url)
//                initializePeer()
//            }
//        }
//
//    }
//    private fun initializePeer() {
//        val fromId =  FirebaseAuth.getInstance().uid
//        val toId = toUser?.uid
//        var uniqueId = getUniqueId()
//
//        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
//
////        val ref = FirebaseDatabase.getInstance().getReference("/videocall/")
////            .addValueEventListener(object: ValueEventListener{
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    onCallRequest(snapshot.value as? String)
////                }
////
////                override fun onCancelled(error: DatabaseError) {
////
////                }
////
////            })
//        if(toId != null) {
//            if (fromId != null) {
//                firebaseRef.child(fromId).child("incoming")
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            onCallRequest(snapshot.value as? String)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })
//            }
//        }
//    }

//    private var uniqueId = ""
//
//    private fun onCallRequest(caller: Any?) {
//        if(caller == null) return
//        val fromId =  FirebaseAuth.getInstance().uid
//        val callLayout = findViewById<RelativeLayout>(R.id.callLayout)
//        val incommingCaller = findViewById<TextView>(R.id.incommingCall)
//        callLayout.visibility = View.VISIBLE
//        incommingCaller.text = "$caller is calling.."
//        val acceptButton = findViewById<ImageView>(R.id.acceptCall)
//        acceptButton.setOnClickListener {
//            if (fromId != null) {
//
//                firebaseRef.child(fromId).child("connId").setValue(uniqueId)
//            }
//            if (fromId != null) {
//                firebaseRef.child(fromId).child("isAvailable").setValue(true)
//            }
//
//
//            callLayout.visibility = View.GONE
//            switchToControls()
//        }
//        val rejectButton = findViewById<ImageView>(R.id.rejectCall)
//        rejectButton.setOnClickListener {
//            if (fromId != null) {
//                firebaseRef.child(fromId).child("incoming").setValue(null)
//            }
//        }
//    }
//    private fun switchToControls() {
//        val callControlLayout = findViewById<LinearLayout>(R.id.callControllLayout)
//        callControlLayout.visibility = View.VISIBLE
//    }
//
//    private fun getUniqueId(): String{
//        return UUID.randomUUID().toString()
//    }
//
//    private fun callJavascriptFunction(functionString: String){
//        webView.post{webView.evaluateJavascript(functionString,null)}
//    }

//    fun onPeerConnected(){
//        val isPeerConnected = true
//    }

/*    override fun onBackPressed() {
        finish()
    }*/
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (fromId != null) {
//            firebaseRef.child(fromId).setValue(null)
//            webView.loadUrl("about:blank")
//        }
    }




