package com.example.chattutorial

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getstream.sdk.chat.view.Dialog.MoreActionDialog
import com.getstream.sdk.chat.view.Dialog.ReactionDialog
import android.content.Intent
import android.content.pm.PackageManager
import com.getstream.sdk.chat.viewmodel.ChannelViewModel
import com.getstream.sdk.chat.viewmodel.ChannelViewModelFactory
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import com.getstream.sdk.chat.StreamChat
import com.example.chattutorial.databinding.ActivityChannelBinding
import com.getstream.sdk.chat.model.Attachment
import com.getstream.sdk.chat.model.Channel
import com.getstream.sdk.chat.rest.Message
import com.getstream.sdk.chat.utils.Constant
import com.getstream.sdk.chat.utils.PermissionChecker
import com.getstream.sdk.chat.view.MessageInputView
import com.getstream.sdk.chat.view.MessageListView


/**
 * Show the messages for a channel
 */
class ChannelActivity : AppCompatActivity(), MessageListView.MessageClickListener,
    MessageListView.MessageLongClickListener, MessageListView.AttachmentClickListener,
    MessageInputView.OpenCameraViewListener {

    internal val TAG = ChannelActivity::class.java.simpleName

    private var viewModel: ChannelViewModel? = null
    private var binding: ActivityChannelBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // receive the intent and create a channel object
        val intent = intent
        val channelType = intent.getStringExtra(EXTRA_CHANNEL_TYPE)
        val channelID = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val client = StreamChat.getInstance(application)

        // we're using data binding in this example
        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel)
        // most the business logic of the chat is handled in the ChannelViewModel view model
        binding!!.lifecycleOwner = this

        var channel = client.getChannelByCid("$channelType:$channelID")
        if (channel == null)
            channel = client.channel(channelType, channelID)
        viewModel = ViewModelProviders.of(
            this,
            ChannelViewModelFactory(this.application, channel)
        ).get(ChannelViewModel::class.java)

        // set listeners
        binding!!.messageList.setMessageClickListener(this)
        binding!!.messageList.setMessageLongClickListener(this)
        binding!!.messageList.setAttachmentClickListener(this)
        binding!!.messageInput.setOpenCameraViewListener(this)

        // connect the view model
        binding!!.viewModel = viewModel
        binding!!.channelHeader.setViewModel(viewModel, this)
        binding!!.messageList.setViewModel(viewModel!!, this)
        binding!!.messageInput.setViewModel(viewModel, this)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding!!.messageInput.progressCapturedMedia(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constant.PERMISSIONS_REQUEST) {
            var granted = true
            for (grantResult in grantResults)
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
            if (!granted) PermissionChecker.showRationalDialog(this, null)
        }
    }

    override fun openCameraView(intent: Intent, REQUEST_CODE: Int) {
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onMessageClick(message: Message, position: Int) {
        val reactionDialog = ReactionDialog(
            this,
            viewModel!!.channel,
            message,
            position,
            binding!!.messageList,
            binding!!.messageList.style
        )
        reactionDialog.show()
    }

    override fun onMessageLongClick(message: Message) {
        val moreActionDialog = MoreActionDialog(
            this,
            viewModel!!.channel,
            message,
            binding!!.messageList.style
        )
        moreActionDialog.show()
    }

    override fun onAttachmentClick(message: Message, attachment: Attachment) {
        binding!!.messageList.showAttachment(message, attachment)

    }

    companion object {

        private val EXTRA_CHANNEL_TYPE = "com.example.chattutorial.CHANNEL_TYPE"
        private val EXTRA_CHANNEL_ID = "com.example.chattutorial.CHANNEL_ID"

        fun newIntent(context: Context, channel: Channel): Intent {
            val intent = Intent(context, ChannelActivity::class.java)
            intent.putExtra(EXTRA_CHANNEL_TYPE, channel.type)
            intent.putExtra(EXTRA_CHANNEL_ID, channel.id)
            return intent
        }
    }

}