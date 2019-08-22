package com.example.chattutorial

import android.view.ViewGroup

import com.getstream.sdk.chat.BaseAttachmentViewHolder
import com.getstream.sdk.chat.adapter.AttachmentListItemAdapter
import com.getstream.sdk.chat.adapter.MessageViewHolderFactory
import com.getstream.sdk.chat.model.Attachment
import com.getstream.sdk.chat.rest.Message

class MyMessageViewHolderFactory : MessageViewHolderFactory() {

    override fun getAttachmentViewType(
        message: Message?,
        mine: Boolean?,
        position: MessageViewHolderFactory.Position?,
        attachments: List<Attachment>?,
        attachment: Attachment
    ): Int {
        return if (attachment.imageURL != null && attachment.imageURL.indexOf("imgur") != -1) {
            IMGUR_TYPE
        } else super.getAttachmentViewType(message, mine, position, attachments, attachment)
    }

    override fun createAttachmentViewHolder(
        adapter: AttachmentListItemAdapter,
        parent: ViewGroup,
        viewType: Int
    ): BaseAttachmentViewHolder {
        val holder: BaseAttachmentViewHolder
        if (viewType == IMGUR_TYPE) {
            holder = AttachmentViewHolderImgur(R.layout.list_item_attach_imgur, parent)
        } else {
            holder = super.createAttachmentViewHolder(adapter, parent, viewType)
        }


        return holder
    }

    companion object {
        private val IMGUR_TYPE = 0
    }
}