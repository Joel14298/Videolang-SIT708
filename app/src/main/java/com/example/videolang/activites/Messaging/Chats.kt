package com.example.videolang.activites.Messaging

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.videolang.R
import com.example.videolang.activites.NewMessageActivity
import com.example.videolang.activites.User
import com.example.videolang.activites.Views.HomepageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*

class Chats : AppCompatActivity(){

 private lateinit var mTTS: TextToSpeech

    var toUser: User? =null

    companion object{
        val TAG = "Chatlog"
        private const val REQUEST_CODE_STT = 1
        val SPEECH_CONVERTED_TAG = "STT"
    }


    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        val recycle = findViewById<RecyclerView>(R.id.chat_recyclerview)
        recycle.adapter = adapter
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        if (toUser != null) {
            supportActionBar?.title = toUser!!.firstName + " " + toUser!!.lastName
        }
        listenForMessages()
//        listenForVoiceMessages(getString(res))

        val send = findViewById<Button>(R.id.chat_send_button)
        send.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage()
        }

        val recordButton = findViewById<Button>(R.id.recordButton)
        recordButton.setOnClickListener {

            val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            sttIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
            try {
                startActivityForResult(sttIntent, REQUEST_CODE_STT)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val resultText: EditText = findViewById<EditText>(R.id.chat_edittext)
        when (requestCode) {
            REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
//                        resultText.setText(recognizedText)
                        val speechConvertedText = recognizedText
                        Log.d(SPEECH_CONVERTED_TAG,"Result: $speechConvertedText")
                        translate(speechConvertedText)
                    }
                }
            }
        }
    }

    fun translate(tranlateFromText: String){
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
        val englishGermanTranslator = Translation.getClient(options)
        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
                Log.d("TRANSLATE_TEXT","Language Downloaded")
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                Log.e("TRANSLATE_TEXT","Language model downloading failed ")
                // ...
            }
        englishGermanTranslator.translate(tranlateFromText)
            .addOnSuccessListener { translatedText ->
                runOnUiThread{
                    val resultText = translatedText
                    sendVoiceMessage(resultText)
                    listenForVoiceMessages(resultText)
                }
            }
            .addOnFailureListener {
                Log.e("Error","Error in translation")
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

    fun listenForVoiceMessages(resultText: String) {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/userVoiceMessages/$fromId/$toId")



        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val voiceMessage = snapshot.getValue(VoiceMessage::class.java)
//                val buttonSpeak = findViewById<Button>(R.id.playAudioButton)

                if (voiceMessage != null) {
                    Log.d(TAG, voiceMessage.text)

                    if(voiceMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = HomepageActivity.currentUser ?: return
                        adapter.add(ChatItem3(voiceMessage.text, currentUser, mTTS.speak(resultText,TextToSpeech.QUEUE_FLUSH, null)))
                    } else{
                        adapter.add(ChatItem4(voiceMessage.text, toUser!!))
                    }
                }
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

        mTTS = TextToSpeech(this){status ->
            if(status == TextToSpeech.SUCCESS){
                val result = mTTS.setLanguage(Locale.GERMAN)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("Error","Error")
                }

            }
            fun speak(){

            }

        }
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

    class VoiceMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long){
        constructor() : this("","","","",-1)
    }

    fun sendVoiceMessage(translatedText: String) {
        Log.d("TAG", "converted to String from Task<String> : $translatedText")
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

        if(fromId == null) return
        if(toId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/userVoiceMessages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/userVoiceMessages/$toId/$fromId").push()

        val VoiceMessage = VoiceMessage(reference.key!!, translatedText, fromId,toId,System.currentTimeMillis()/1000)

        reference.setValue(VoiceMessage)
            .addOnSuccessListener {
                Log.d("LOG","Created New DB: ${reference.key}")
                val recycle = findViewById<RecyclerView>(R.id.chat_recyclerview)
                recycle.scrollToPosition(adapter.itemCount -1)
            }

        toReference.setValue(VoiceMessage)

        val latestFromMessageRef = FirebaseDatabase.getInstance().getReference("/incomingVideoMessages/$fromId/$toId")
        latestFromMessageRef.setValue(VoiceMessage)

        val latestToMessageRef = FirebaseDatabase.getInstance().getReference("/incomingVideoMessages/$toId/$fromId")
        latestToMessageRef.setValue(VoiceMessage)
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

class ChatItem3(val text: String, val user: User, speak: Int): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
       viewHolder.itemView.findViewById<TextView>(R.id.translatedText).text =  text
//        val translatedText = text
//        val tts = viewHolder.item.
            viewHolder.itemView.findViewById<Button>(R.id.playAudioButton)
        val uri = user.profilePictureUri
        val targetUri = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(uri).into(targetUri)
    }

    override fun getLayout(): Int {
        return R.layout.chat_dummy3
    }
}

class ChatItem4(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.translatedText).text =  text
        val translatedLanguageText = text
        viewHolder.itemView.findViewById<Button>(R.id.playAudioButton).setOnClickListener {
            Log.d("Testing","Testing")
        }

        val uri = user.profilePictureUri
        val targetUri = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(uri).into(targetUri)
    }

    override fun getLayout(): Int {
        return R.layout.chat_dummy4
    }
}
