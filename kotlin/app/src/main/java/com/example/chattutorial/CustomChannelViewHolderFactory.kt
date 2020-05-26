package com.example.chattutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.getstream.sdk.chat.adapter.BaseChannelListItemViewHolder
import com.getstream.sdk.chat.adapter.ChannelItemPayloadDiff
import com.getstream.sdk.chat.adapter.ChannelListItemAdapter
import com.getstream.sdk.chat.adapter.ChannelViewHolderFactory
import com.getstream.sdk.chat.view.ChannelListView
import com.getstream.sdk.chat.view.ChannelListViewStyle
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.image
import io.getstream.chat.android.client.models.name
import kotlinx.android.synthetic.main.channel_list_item.view.*

class CustomChannelViewHolderFactory(
    val context: Context,
    val openChannel: (Channel) -> Unit
) : ChannelViewHolderFactory() {

    override fun createChannelViewHolder(
        adapter: ChannelListItemAdapter?,
        parent: ViewGroup?,
        viewType: Int
    ): BaseChannelListItemViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.channel_list_item, parent, false)

        val holder = object : BaseChannelListItemViewHolder(view) {
            override fun setChannelLongClickListener(l: ChannelListView.ChannelClickListener?) {

            }

            override fun setUserClickListener(l: ChannelListView.UserClickListener?) {

            }

            override fun setStyle(style: ChannelListViewStyle?) {

            }

            override fun setChannelClickListener(l: ChannelListView.ChannelClickListener) {

            }

            override fun bind(
                context: Context,
                channel: Channel,
                position: Int,
                payloads: ChannelItemPayloadDiff?
            ) {


                Glide.with(context).load(channel.image).into(view.imageLastUser)

                view.textChannelName.text = "Name:" + channel.name

                if (channel.messages.isNotEmpty()) {

                    val lastMessage = channel.messages.first()
                    view.textLastMessage.text = "Message: " + lastMessage.text
                    view.textLastMessageDate.text = "Message date:" + lastMessage.createdAt!!.toString()
                    view.textLastUserName.text = "User name:" + lastMessage.user.name
                    view.checkUserPresence.isChecked = lastMessage.user.online
                }

                view.setOnClickListener { openChannel(channel) }
            }

        }

        return holder
    }
}