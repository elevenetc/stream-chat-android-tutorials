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
import com.getstream.sdk.chat.model.Event
import com.getstream.sdk.chat.rest.core.ChatChannelEventHandler
import com.getstream.sdk.chat.utils.PermissionChecker
import com.getstream.sdk.chat.view.MessageInputView.PermissionRequestListener
import com.getstream.sdk.chat.viewmodel.ChannelViewModel
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory
import java.util.*


/**
 * Show the messages for a channel
 *
 */
class ChannelActivity : AppCompatActivity(), PermissionRequestListener {

    private lateinit var binding: ActivityChannelBinding


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
        binding.lifecycleOwner = this

        val channel = client.channel(channelType, channelID)
        val viewModelFactory = ChannelViewModelFactory(application, channel)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ChannelViewModel::class.java)

        // connect the view model
        binding.viewModel = viewModel
        val factory = MyMessageViewHolderFactory()
        binding.messageList.setViewHolderFactory(factory)
        binding.messageList.setViewModel(viewModel, this)
        binding.messageInput.setViewModel(viewModel, this)
        // binding.channelHeader.setViewModel(viewModel, this)
        // If you are using own MessageInputView please comment this line.
        binding.messageInput.setPermissionRequestListener(this)

        val currentlyTyping = MutableLiveData<List<String>>(ArrayList())
        channel.addEventHandler(object : ChatChannelEventHandler() {
            override fun onTypingStart(event: Event) {
                val typing = currentlyTyping.value ?: listOf()
                val typingCopy : MutableList<String> = typing.toMutableList()
                if (typingCopy.contains(event.user.name).not()) {
                    typingCopy.add(event.user.name)
                }
                currentlyTyping.postValue(typingCopy)
            }

            override fun onTypingStop(event: Event) {
                val typing = currentlyTyping.value ?: listOf()
                val typingCopy : MutableList<String> = typing.toMutableList()
                typingCopy.remove(event.user.name)
                currentlyTyping.postValue(typingCopy)
            }
        })

        val typingObserver = Observer<List<String>> { users ->
            var typing = "nobody is typing"
            if (users.isNotEmpty()) {
                typing = "typing: " + users.joinToString(", ")
            }
            binding.typing = typing
        }
        currentlyTyping.observe(this,typingObserver)

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // If you are using own MessageInputView please comment this line.
        binding.messageInput.captureMedia(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) { // If you are using own MessageInputView please comment this line.
        binding.messageInput.permissionResult(requestCode, permissions, grantResults)
    }

    override fun openPermissionRequest() {
        PermissionChecker.permissionCheck(this, null)
        // If you are writing a Channel Screen in a Fragment, use the code below instead of the code above.
        // PermissionChecker.permissionCheck(getActivity(), this);
    }


    companion object {

        private const val EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE"
        private const val EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID"

        fun newIntent(context: Context, channel: Channel): Intent {
            val intent = Intent(context, ChannelActivity::class.java)
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.type)
            intent.putExtra(EXTRA_CHANNEL_ID, channel.id)
            return intent
        }
    }

}