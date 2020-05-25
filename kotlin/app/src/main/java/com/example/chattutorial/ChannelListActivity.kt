package com.example.chattutorial

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chattutorial.databinding.ActivityMainBinding
import com.getstream.sdk.chat.Chat
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters.eq
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.livedata.ChatDomain

class ChannelListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val user = User("summer-brook-2")
        user.extraData["name"] = "Paranoid Android"
        user.extraData["image"] = "https://bit.ly/2TIt8NR"

        // setup the client using the example API key
        // normally you would call init in your Application class and not the activity
        val chat = Chat.Builder("b67pax5b2wdq", this.applicationContext)
            .logLevel(ChatLogLevel.ALL)
            .build()

        val client = chat.client

        ChatDomain.Builder(applicationContext, client, user).build()


        // User token is typically provided by your server when the user authenticates
        val token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0"
        client.setUser(user, token, object : InitConnectionListener() {
            override fun onSuccess(data: ConnectionData) {
                Log.i("MainActivity", "setUser completed")
                initViewModel()
            }

            override fun onError(error: ChatError) {
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("MainActivity", "setUser onError")
            }
        })

        initViewModel()
    }

    private fun initViewModel() {
        // we're using data binding in this example
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this

        // most the business logic for chat is handled in the ChannelListViewModel view model
        val viewModel = ViewModelProvider(this).get(ChannelListViewModel::class.java)

        // query all channels of type messaging
        val filter = eq("type", "messaging")
        //val filter = and(eq("type", "messaging"), `in`("members", listOf(user.id)))
        viewModel.setQuery(filter, null)

        binding.viewModel = viewModel
        binding.channelList.setViewModel(viewModel, this)
        setCustomChannelItem(this, binding)
    }

    private fun setCustomChannelItem(context: Context, binding: ActivityMainBinding) {
        val factory = CustomChannelViewHolderFactory(context) { openChannel(it) }
        binding.channelList.setViewHolderFactory(factory)
    }

    private fun openChannel(channel: Channel) {
        val intent = ChannelActivity.newIntent(this, channel)
        startActivity(intent)
    }
}
