package com.example.chattutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.chattutorial.databinding.ActivityMainBinding
import com.getstream.sdk.chat.StreamChat
import com.getstream.sdk.chat.enums.Filters.*
import com.getstream.sdk.chat.rest.User
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel
import java.util.*


/**
 * This activity shows a list of channels
 */
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        val UserID = "paranoid-android"
        // setup the client using the example API key
        // normal you would call init in your Application class and not the activity
        StreamChat.init("qk4nn7rpcn75", this.applicationContext)
        val client = StreamChat.getInstance(this.application)
        val extraData = HashMap<String, Any>()
        extraData["name"] = "Paranoid Android"
        extraData["image"] = "https://bit.ly/2TIt8NR"
        val currentUser = User(UserID, extraData)
        // User token is typically provided by your server when the user authenticates
        client.setUser(
            currentUser,
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoicGFyYW5vaWQtYW5kcm9pZCJ9.zrhwtQei0wNyvDsX8kBNctvLVg7-OQLH1oB4oc0tc5c"
        )

        // we're using data binding in this example
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this

        // most the business logic for chat is handled in the ChannelListViewModel view model
        val viewModel = ViewModelProviders.of(this).get(ChannelListViewModel::class.java)
        binding.viewModel = viewModel
        binding.channelList.setViewModel(viewModel, this)

        // query all channels of type messaging
        val filter = and(eq("type", "messaging"), `in`("members", UserID))
        viewModel.setChannelFilter(filter)

        // click handlers for clicking a user avatar or channel
        binding.channelList.setOnChannelClickListener({ channel ->
            // open the channel activity
            val intent = ChannelActivity.newIntent(this, channel)
            startActivity(intent)
        })
        binding.channelList.setOnUserClickListener({ user ->
            // open your user profile
        })

    }

}