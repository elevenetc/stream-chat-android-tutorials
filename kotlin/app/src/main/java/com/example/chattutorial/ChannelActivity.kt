package com.example.chattutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chattutorial.databinding.ActivityChannelBinding
import com.getstream.sdk.chat.StreamChat
import com.getstream.sdk.chat.model.Channel
import com.getstream.sdk.chat.viewmodel.ChannelViewModel
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory
import com.getstream.sdk.chat.model.Event;
import com.getstream.sdk.chat.rest.core.ChatChannelEventHandler;
import java.util.ArrayList


/**
 * Show the messages for a channel
 *
 */
class ChannelActivity : AppCompatActivity() {

    private var viewModel: ChannelViewModel? = null
    private var binding: ActivityChannelBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // receive the intent and create a channel object
        val intent = intent
        val channelType = intent.getStringExtra(EXTRA_CHANNEL_TYPE)
        val channelID = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val client = StreamChat.getInstance(application)

        // we're using data binding in this example
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)
        // most the business logic of the chat is handled in the ChannelViewModel view model
        binding!!.lifecycleOwner = this

        var channel = client.channel(channelType, channelID)
        val viewModelFactory = ChannelViewModelFactory(application, channel)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ChannelViewModel::class.java)



        // connect the view model
        binding!!.viewModel = viewModel
        val factory = MyMessageViewHolderFactory()
        binding!!.messageList.setViewHolderFactory(factory)
        binding!!.messageList.setViewModel(viewModel!!, this)
        binding!!.messageInput.setViewModel(viewModel, this)

        val currentlyTyping = MutableLiveData<List<String>>(ArrayList())
        channel.addEventHandler(object : ChatChannelEventHandler() {
            override fun onTypingStart(event: Event) {
                val typingCopy : MutableList<String>? = currentlyTyping.value!!.toMutableList()
                if (!typingCopy!!.contains(event.getUser().getName())) {
                    typingCopy.add(event.getUser().getName())
                }
                currentlyTyping.postValue(typingCopy)
            }

            override fun onTypingStop(event: Event) {
                val typingCopy : MutableList<String>? = currentlyTyping.value!!.toMutableList()
                typingCopy!!.remove(event.getUser().getName())
                currentlyTyping.postValue(typingCopy)
            }
        })

        val typingObserver = Observer<List<String>> { users ->
            var typing: String = "nobody is typing"
            if (!users.isEmpty()) {
                typing = "typing: " + users.joinToString(", ")
            }
            binding!!.setTyping(typing)
        }
        currentlyTyping.observe(this,typingObserver)

    }


    companion object {

        private val EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE"
        private val EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID"

        fun newIntent(context: Context, channel: Channel): Intent {
            val intent = Intent(context, ChannelActivity::class.java)
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.type)
            intent.putExtra(EXTRA_CHANNEL_ID, channel.id)
            return intent
        }
    }

}