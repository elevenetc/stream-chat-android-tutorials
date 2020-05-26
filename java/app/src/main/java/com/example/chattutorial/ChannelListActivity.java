package com.example.chattutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.chattutorial.databinding.ActivityMainBinding;
import com.getstream.sdk.chat.Chat;
import com.getstream.sdk.chat.viewmodel.ChannelListViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.errors.ChatError;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.socket.InitConnectionListener;
import io.getstream.chat.android.client.utils.FilterObject;
import io.getstream.chat.android.livedata.ChatDomain;

public class ChannelListActivity extends AppCompatActivity {

    public static final String EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE";
    public static final String EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        User user = new User();
        user.setId("summer-brook-2");
        user.getExtraData().put("name", "Paranoid Android");
        user.getExtraData().put("image", "https://bit.ly/2TIt8NR");

        // setup the client using the example API key
        // normally you would call init in your Application class and not the activity
        Chat chat = new Chat.Builder("b67pax5b2wdq", getApplicationContext()).logLevel(
                ChatLogLevel.ALL).build();

        ChatClient client = chat.getClient();

        new ChatDomain.Builder(getApplicationContext(), client, user).build();

        // User token is typically provided by your server when the user authenticates
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoic3VtbWVyLWJyb29rLTIifQ.CzyOx8kgrc61qVbzWvhV1WD3KPEo5ZFZH-326hIdKz0";
        client.setUser(user, token, new InitConnectionListener() {
            @Override
            public void onSuccess(ConnectionData data) {
                Log.i("MainActivity", "setUser completed");
                initViewModel();
            }

            @Override
            public void onError(ChatError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "setUser onError");
            }
        });

    }

    private void initViewModel() {
        // we're using data binding in this example
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);

        ChannelListViewModel viewModel = new ViewModelProvider(this).get(ChannelListViewModel.class);
        FilterObject filter = Filters.INSTANCE.and(Filters.INSTANCE.eq("type", "messaging"));
        viewModel.setQuery(filter, null);

        // most the business logic for chat is handled in the ChannelListViewModel view model

        binding.setViewModel(viewModel);
        binding.channelList.setViewModel(viewModel, this);

        setCustomChannelItem(this, binding);
    }

    private void setCustomChannelItem(Context context, ActivityMainBinding binding){
        CustomChannelViewHolderFactory factory = new CustomChannelViewHolderFactory(channel -> ChannelListActivity.this.openChannel(channel), context);
        binding.channelList.setViewHolderFactory(factory);
    }

    private void openChannel(Channel channel){
        Intent intent = new Intent(ChannelListActivity.this, ChannelActivity.class);
        intent.putExtra(EXTRA_CHANNEL_TYPE, channel.getType());
        intent.putExtra(EXTRA_CHANNEL_ID, channel.getId());
        startActivity(intent);
    }
}