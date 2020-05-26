package com.example.chattutorial;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.chattutorial.databinding.ActivityChannelBinding;
import com.getstream.sdk.chat.utils.PermissionChecker;
import com.getstream.sdk.chat.view.MessageInputView;
import com.getstream.sdk.chat.viewmodel.ChannelViewModel;
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.controllers.ChannelController;
import io.getstream.chat.android.client.events.ChatEvent;
import io.getstream.chat.android.client.events.TypingStartEvent;
import io.getstream.chat.android.client.events.TypingStopEvent;
import io.getstream.chat.android.client.models.User;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ChannelActivity extends AppCompatActivity
        implements MessageInputView.PermissionRequestListener {

    private ChannelViewModel viewModel;
    private ActivityChannelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive the intent and create a channel object
        Intent intent = getIntent();
        String channelType = intent.getStringExtra(ChannelListActivity.EXTRA_CHANNEL_TYPE);
        String channelId = intent.getStringExtra(ChannelListActivity.EXTRA_CHANNEL_ID);
        ChatClient client = ChatClient.instance();
        ChannelController channel = client.channel(channelType, channelId);

        // we're using data binding in this example
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel);
        // most the business logic of the chat is handled in the ChannelViewModel view model
        binding.setLifecycleOwner(this);

        initViewModel(channelType, channelId);
    }

    private void initViewModel(String channelType, String channelId) {

        ChannelViewModelFactory viewModelFactory = new ChannelViewModelFactory(this.getApplication(), channelType, channelId);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChannelViewModel.class);
        ChatClient client = ChatClient.instance();
        ChannelController channelController = client.channel(channelType, channelId);
        LifecycleOwner lifecycleOwner = this;

        viewModel.getInitialized().observe(this, channel -> {

            // connect the view model
            binding.setViewModel(viewModel);
            binding.messageList.setViewHolderFactory(new CustomMessageViewHolderFactory());

            MutableLiveData<List<String>> currentlyTyping = new MutableLiveData<>(new ArrayList<>());
            channelController.events().subscribe(new Function1<ChatEvent, Unit>() {
                @Override
                public Unit invoke(ChatEvent event) {
                    User user = event.getUser();
                    String name = (String) user.getExtraData().get("name");

                    if (event instanceof TypingStartEvent) {
                        List<String> typingCopy = currentlyTyping.getValue();
                        if (!typingCopy.contains(name)) {
                            typingCopy.add(name);
                        }
                        currentlyTyping.postValue(typingCopy);
                    } else if (event instanceof TypingStopEvent) {
                        List<String> typingCopy = currentlyTyping.getValue();
                        typingCopy.remove(name);
                        currentlyTyping.postValue(typingCopy);
                    }
                    return null;
                }
            });
            currentlyTyping.observe(lifecycleOwner, users -> {
                String typing = "nobody is typing";
                if (!users.isEmpty()) {
                    typing = "typing: " + TextUtils.join(", ", users);
                }
                binding.setTyping(typing);
            });

            //binding.channelHeader.setViewModel(viewModel, this)
            binding.messageList.setViewModel(viewModel, lifecycleOwner);
            binding.messageInput.setViewModel(viewModel, lifecycleOwner);
        });
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