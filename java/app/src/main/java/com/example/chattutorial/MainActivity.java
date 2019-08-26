package com.example.chattutorial;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.chattutorial.databinding.ActivityMainBinding;
import com.getstream.sdk.chat.StreamChat;
import com.getstream.sdk.chat.enums.FilterObject;
import com.getstream.sdk.chat.rest.User;
import com.getstream.sdk.chat.rest.core.Client;
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel;

import java.util.HashMap;

import static com.getstream.sdk.chat.enums.Filters.in;


/**
 * This activity shows a list of channels
 */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE";
    public static final String EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        // setup the client using the example API key
        // normal you would call init in your Application class and not the activity
        StreamChat.init("qk4nn7rpcn75", this.getApplicationContext());
        Client client = StreamChat.getInstance(this.getApplication());
        HashMap<String, Object> extraData = new HashMap<>();
        extraData.put("name", "Paranoid Android");
        extraData.put("image", "https://bit.ly/2TIt8NR");
        User currentUser = new User("paranoid-android", extraData);
        // User token is typically provided by your server when the user authenticates
        client.setUser(currentUser, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoicGFyYW5vaWQtYW5kcm9pZCJ9.zrhwtQei0wNyvDsX8kBNctvLVg7-OQLH1oB4oc0tc5c");

        // we're using data binding in this example
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // most the business logic for chat is handled in the ChannelListViewModel view model
        ChannelListViewModel viewModel = ViewModelProviders.of(this).get(ChannelListViewModel.class);
        binding.setViewModel(viewModel);
        binding.channelList.setViewModel(viewModel, this);

        // query all channels of type messaging
        FilterObject filter = in("type", "messaging");
        viewModel.setChannelFilter(filter);

        // click handlers for clicking a user avatar or channel
        binding.channelList.setOnChannelClickListener(channel -> {
            // open the channel activity
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.getType());
            intent.putExtra(EXTRA_CHANNEL_ID, channel.getId());
            startActivity(intent);
        });
        binding.channelList.setOnUserClickListener(user -> {
            // open your user profile
        });

    }
}