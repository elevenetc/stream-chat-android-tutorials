package com.example.chattutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getstream.sdk.chat.adapter.BaseChannelListItemViewHolder;
import com.getstream.sdk.chat.adapter.ChannelItemPayloadDiff;
import com.getstream.sdk.chat.adapter.ChannelListItemAdapter;
import com.getstream.sdk.chat.adapter.ChannelViewHolderFactory;
import com.getstream.sdk.chat.view.ChannelListView;
import com.getstream.sdk.chat.view.ChannelListViewStyle;

import androidx.annotation.Nullable;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;

public class CustomChannelViewHolderFactory extends ChannelViewHolderFactory {

    private OpenChannelHandler handler;
    private Context context;

    public CustomChannelViewHolderFactory(OpenChannelHandler handler, Context context) {
        this.handler = handler;

        this.context = context;
    }

    @Override
    public BaseChannelListItemViewHolder createChannelViewHolder(ChannelListItemAdapter adapter, ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_list_item, parent, false);

        BaseChannelListItemViewHolder holder = new BaseChannelListItemViewHolder(view) {
            @Override
            public void bind(Context context, Channel channel, int position, @Nullable ChannelItemPayloadDiff payloads) {

                ImageView imageLastUser = view.findViewById(R.id.imageLastUser);
                TextView textChannelName = view.findViewById(R.id.textChannelName);
                TextView textLastMessageDate = view.findViewById(R.id.textLastMessageDate);
                TextView textLastMessage = view.findViewById(R.id.textLastMessage);
                TextView textLastUserName = view.findViewById(R.id.textLastUserName);
                CheckBox checkUserPresence = view.findViewById(R.id.checkUserPresence);

                Glide.with(context).load(channel.getExtraValue("image", "")).into(imageLastUser);

                textChannelName.setText( "Name:" + channel.getExtraValue("name", ""));

                if (!channel.getMessages().isEmpty()) {

                    Message lastMessage = channel.getMessages().get(0);
                    textLastMessage.setText("Message: " + lastMessage.getText());
                    textLastMessageDate.setText( "Message date:" + lastMessage.getCreatedAt().toString());
                    textLastUserName.setText( "User name:" + lastMessage.getUser().getExtraValue("name", ""));
                    checkUserPresence.setChecked(lastMessage.getUser().getOnline());
                }

                view.setOnClickListener(v -> handler.openChannel(channel));
            }

            @Override
            public void setStyle(ChannelListViewStyle style) {

            }

            @Override
            public void setUserClickListener(ChannelListView.UserClickListener l) {

            }

            @Override
            public void setChannelClickListener(ChannelListView.ChannelClickListener l) {

            }

            @Override
            public void setChannelLongClickListener(ChannelListView.ChannelClickListener l) {

            }
        };

        return holder;
    }

    interface OpenChannelHandler {
        void openChannel(Channel channel);
    }
}
