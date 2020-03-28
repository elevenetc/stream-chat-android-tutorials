package com.example.chattutorial;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.getstream.sdk.chat.adapter.BaseAttachmentViewHolder;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.getstream.sdk.chat.utils.roundedImageView.PorterShapeImageView;
import com.getstream.sdk.chat.view.MessageListView;
import com.bumptech.glide.Glide;
import com.getstream.sdk.chat.view.MessageListViewStyle;


public class AttachmentViewHolderImgur extends BaseAttachmentViewHolder {
    private PorterShapeImageView iv_media_thumb;

    AttachmentViewHolderImgur(int resId, ViewGroup parent) {
        super(resId, parent);

        iv_media_thumb = itemView.findViewById(R.id.iv_media_thumb);
    }

    @Override
    public void bind(@NonNull Context context, @NonNull MessageListItem messageListItem, @NonNull io.getstream.chat.android.client.models.Message message, @NonNull io.getstream.chat.android.client.models.Attachment attachment, @NonNull MessageListViewStyle style, @NonNull MessageListView.BubbleHelper bubbleHelper, @Nullable MessageListView.AttachmentClickListener clickListener, @Nullable MessageListView.MessageLongClickListener longClickListener) {
        Drawable background = bubbleHelper.getDrawableForAttachment(messageListItem.getMessage(), messageListItem.isMine(), messageListItem.getPositions(), attachment);
        iv_media_thumb.setShape(context, background);

        Glide.with(context)
                .load(attachment.getThumbUrl())
                .into(iv_media_thumb);
    }

}