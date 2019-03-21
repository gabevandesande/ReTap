package edu.ucsc.retap.retap.compose.view

import android.text.Spannable
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.di.ActivityScope
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * View module for the compose functionality. See ComposeActivity for more details.
 */
@ActivityScope
class ComposeViewModule @Inject constructor(
        private val root: View
) {
    enum class Event {
        DOT_CLICK,
        DASH_CLICK,
        SPACE_CLICK,
        DISPLAY_TEXT_CLICK,
        DISPLAY_TEXT_LONG_PRESS
    }

    private val eventSubject = PublishSubject.create<Event>()
    private val textView = root.findViewById<TextView>(R.id.text_view)

    init {
        setUpButtons()
        setUpTextView()
    }

    private fun setUpButtons() {
        val dot = root.findViewById<View>(R.id.dot)
        val dash = root.findViewById<View>(R.id.dash)
        val space = root.findViewById<View>(R.id.space)

        dot.setOnClickListener {
            eventSubject.onNext(Event.DOT_CLICK)
        }

        dash.setOnClickListener {
            eventSubject.onNext(Event.DASH_CLICK)
        }

        space.setOnClickListener {
            eventSubject.onNext(Event.SPACE_CLICK)
        }
    }

    private fun setUpTextView() {
        textView.setOnClickListener {
            eventSubject.onNext(Event.DISPLAY_TEXT_CLICK)
        }
        textView.setOnLongClickListener {
            eventSubject.onNext(Event.DISPLAY_TEXT_LONG_PRESS)
            true
        }
        textView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Updates the display area for the compose experience.
     * @param formattedText a formatted spannable representing the text to display
     */
    fun updateText(formattedText: Spannable) {
        textView.setText(formattedText, TextView.BufferType.SPANNABLE)
    }

    /**
     * @return an observable emitting events from the view including click events
     */
    fun observeEvents(): Observable<Event> = eventSubject
}
