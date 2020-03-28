package com.example.chattutorial;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.chattutorial.databinding.ActivityChannelBinding;
import com.getstream.sdk.chat.StreamChat;
import com.getstream.sdk.chat.model.Channel;
import com.getstream.sdk.chat.rest.core.Client;
import com.getstream.sdk.chat.utils.Constant;
import com.getstream.sdk.chat.utils.PermissionChecker;
import com.getstream.sdk.chat.view.MessageInputView;
import com.getstream.sdk.chat.viewmodel.ChannelViewModel;
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory;

import androidx.lifecycle.MutableLiveData;
import com.getstream.sdk.chat.model.Event;
import com.getstream.sdk.chat.rest.core.ChatChannelEventHandler;
import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.events.ChatEvent;
import io.getstream.chat.android.client.utils.observable.Subscription;

/**
 * Show the messages for a channel
 */
public class ChannelActivity extends AppCompatActivity
        implements MessageInputView.PermissionRequestListener {

    private ChannelViewModel viewModel;
    private ActivityChannelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive the intent and create a channel object
        Intent intent = getIntent();
        String channelType = intent.getStringExtra(MainActivity.EXTRA_CHANNEL_TYPE);
        String channelId = intent.getStringExtra(MainActivity.EXTRA_CHANNEL_ID);
        ChatClient client = ChatClient.Companion.instance();

        // we're using data binding in this example
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel);
        // most the business logic of the chat is handled in the ChannelViewModel view model
        binding.setLifecycleOwner(this);

        ChannelViewModelFactory viewModelFactory = new ChannelViewModelFactory(this.getApplication(), channelType, channelId);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChannelViewModel.class);

        // connect the view model
        binding.setViewModel(viewModel);
        binding.messageList.setViewHolderFactory(new MyMessageViewHolderFactory());

        MutableLiveData<List<String>> currentlyTyping = new MutableLiveData<>(new ArrayList<>());
        // TODO: how to listen to events in java?
        client.events().subscribe((ChatEvent event) -> {

        });
        currentlyTyping.observe(this, users -> {
            String typing = "nobody is typing";
            if (!users.isEmpty()) {
                typing = "typing: " + TextUtils.join(", ", users);
            }
            binding.setTyping(typing);
        });

        binding.messageList.setViewModel(viewModel, this);
        binding.messageInput.setViewModel(viewModel, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If you are using own MessageInputView please comment this line.
        binding.messageInput.captureMedia(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If you are using own MessageInputView please comment this line.
        binding.messageInput.permissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void openPermissionRequest() {
        PermissionChecker.permissionCheck(this, null);
    }
}