package com.example.chattutorial;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.chattutorial.databinding.ActivityMainBinding;
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel;

import io.getstream.chat.android.client.models.Filters;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.errors.ChatError;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.socket.InitConnectionListener;
import io.getstream.chat.android.client.utils.FilterObject;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE";
    public static final String EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // setup the client using the example API key
        // normally you would call init in your Application class and not the activity
        ChatClient client = new ChatClient.Builder("b67pax5b2wdq", getApplicationContext()).logLevel(
                ChatLogLevel.ALL).build();

        User user = new User("summer-brook-2");
        user.getExtraData().put("name", "Paranoid Android");
        user.getExtraData().put("image", "https://bit.ly/2TIt8NR");
        // User token is typically provided by your server when the user authenticates
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0";
        client.setUser(user, token, new InitConnectionListener() {
            @Override
            public void onSuccess(ConnectionData data) {
                Log.i("MainActivity", "setUser completed");
            }

            @Override
            public void onError(ChatError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "setUser onError");
            }
        });
        // we're using data binding in this example
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);

        // most the business logic for chat is handled in the ChannelListViewModel view model
        ChannelListViewModel viewModel = new ViewModelProvider(this).get(ChannelListViewModel.class);
        binding.setViewModel(viewModel);
        binding.channelList.setViewModel(viewModel, this);

        // query all channels of type messaging
        FilterObject filter = Filters.INSTANCE.and(Filters.INSTANCE.eq("type", "messaging"), Filters.INSTANCE.in("members", "twilight-lab-0"));
        viewModel.setChannelFilter(filter);

        // click handlers for clicking a user avatar or channel
        binding.channelList.setOnChannelClickListener(channel -> {
            // open the channel activity
            Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.getType());
            intent.putExtra(EXTRA_CHANNEL_ID, channel.getId());
            startActivity(intent);
        });
        binding.channelList.setOnUserClickListener(theUser -> {
            // open your user profile
        });
    }
}