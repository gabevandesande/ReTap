package edu.ucsc.retap.retap.messages.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.morse.MorseHelper

/**
 * Represents an item in the messages list.
 */
class MessageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val senderTextView = itemView.findViewById<TextView>(R.id.sender)
    private val contentTextView = itemView.findViewById<TextView>(R.id.body)
    private var inPlaintextMode = false

    fun setSenderText(sender: String) {
        inPlaintextMode = false
        senderTextView?.text = sender
    }

    fun showAsMorseCode(content: String) {
        contentTextView?.setText(MorseHelper.convertToMorse(itemView.context, content),
                TextView.BufferType.SPANNABLE)
    }

    fun showAsPlaintext(content: String) {
        contentTextView?.text = content
    }

    fun setSelected(isSelected: Boolean) {
        itemView.setBackgroundColor(if (isSelected) {
            itemView.resources.getColor(R.color.selectedColor)
        } else {
            itemView.resources.getColor(android.R.color.transparent)
        })
    }
}
