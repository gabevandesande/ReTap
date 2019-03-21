package edu.ucsc.retap.retap.messages.adapter

import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.messages.model.Message
import edu.ucsc.retap.retap.messages.view.MessageItemViewHolder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Adapter for Message models to RecyclerView items
 *
 * The resulting item's layout is specified on construction of the MessagesAdapter, making it easily reusable.
 * This adapter is used both in the inbox and viewing conversations.
 */
@ActivityScope
class MessagesAdapter @Inject constructor(
        private val layoutInflater: LayoutInflater,
        @LayoutRes private val messageLayoutId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val DOUBLE_TAP_TIME_THRESHOLD_MS = 250L
    }

    private val itemClickPublishSubject = PublishSubject.create<Int>()
    private val itemDoubleTapPublishSubject = PublishSubject.create<Int>()
    private val itemsInPlaintextMode = HashSet<Message>()

    var items: List<Message> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItemIndex = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val itemView = layoutInflater.inflate(messageLayoutId, parent, false)
        return MessageItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val messageItemViewHolder = viewHolder as MessageItemViewHolder
        messageItemViewHolder.setSenderText(item.contact.displayName ?: item.contact.phoneNumber)
        messageItemViewHolder.setSelected(selectedItemIndex == position)

        if (itemsInPlaintextMode.contains(item)) {
            messageItemViewHolder.showAsPlaintext(item.contents)
        } else {
            messageItemViewHolder.showAsMorseCode(item.contents)
        }

        setClickListener(messageItemViewHolder)
        setLongClickListener(item, messageItemViewHolder)
    }

    private fun setClickListener(messageItemViewHolder: MessageItemViewHolder) {
        messageItemViewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            private var lastClickTimeMs = 0L
            private val handler = Handler(Looper.getMainLooper())

            override fun onClick(v: View?) {
                if (System.currentTimeMillis() - lastClickTimeMs < DOUBLE_TAP_TIME_THRESHOLD_MS) {
                    handler.removeCallbacksAndMessages(null)
                    itemDoubleTapPublishSubject.onNext(messageItemViewHolder.adapterPosition)
                    lastClickTimeMs = 0L
                } else {
                    lastClickTimeMs = System.currentTimeMillis()
                    handler.postDelayed({
                        itemClickPublishSubject.onNext(messageItemViewHolder.adapterPosition)
                    }, DOUBLE_TAP_TIME_THRESHOLD_MS)
                }
            }
        })
    }

    private fun setLongClickListener(item: Message, messageItemViewHolder: MessageItemViewHolder) {
        messageItemViewHolder.itemView.setOnLongClickListener {
            if (itemsInPlaintextMode.contains(item)) {
                messageItemViewHolder.showAsMorseCode(item.contents)
                itemsInPlaintextMode.remove(item)
            } else {
                messageItemViewHolder.showAsPlaintext(item.contents)
                itemsInPlaintextMode.add(item)
            }
        }
    }

    fun observeItemClick(): Observable<Int> = itemClickPublishSubject

    fun observeItemDoubleTap(): Observable<Int> = itemDoubleTapPublishSubject
}
