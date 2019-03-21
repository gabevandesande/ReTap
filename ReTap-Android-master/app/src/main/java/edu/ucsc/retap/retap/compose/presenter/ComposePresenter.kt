package edu.ucsc.retap.retap.compose.presenter

import android.app.Activity
import android.content.Intent
import android.text.SpannableString
import android.text.SpannableStringBuilder
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.compose.view.ComposeViewModule
import edu.ucsc.retap.retap.morse.MorseHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Presenter for the compose functionality. See ComposeActivity for more details.
 */
@ActivityScope
class ComposePresenter @Inject constructor(
        activity: Activity,
        private val viewModule: ComposeViewModule
) : BasePresenter {

    companion object {
        private const val PLAIN_TEXT = "text/plain"
    }

    // Weak reference to prevent the activity from being referenced after it is destroyed.
    private val activityRef = WeakReference<Activity>(activity)
    private val currentMorseText = SpannableStringBuilder()
    private val eventDisposable = CompositeDisposable()
    private var displayedAsPlaintext = false

    override fun startPresenting() {
        eventDisposable.add(
                viewModule
                        .observeEvents()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            handleViewEvent(it)
                        }
        )
    }

    override fun stopPresenting() {
        eventDisposable.clear()
    }

    override fun onActionButtonClicked() {
        val activity = activityRef.get() ?: return
        val text = MorseHelper.convertToText(currentMorseText.toString())
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = PLAIN_TEXT
        activity.startActivity(intent)
    }

    private fun handleViewEvent(event: ComposeViewModule.Event) {
        val activity = activityRef.get() ?: return
        when (event) {
            ComposeViewModule.Event.DOT_CLICK -> {
                // Append a dot to the text and refresh the display.
                MorseHelper.appendMorse(MorseHelper.DOT, activity, currentMorseText)
                displayCurrentTextAsMorse()
            }
            ComposeViewModule.Event.DASH_CLICK -> {
                // Append a dash to the text and refresh the display.
                MorseHelper.appendMorse(MorseHelper.DASH, activity, currentMorseText)
                displayCurrentTextAsMorse()
            }
            ComposeViewModule.Event.SPACE_CLICK -> {
                // Append a space to the text and refresh the display.
                MorseHelper.appendSpace(currentMorseText)
                displayCurrentTextAsMorse()
            }
            ComposeViewModule.Event.DISPLAY_TEXT_CLICK -> {
                // Delete a morse character and display the result as Morse.
                if (currentMorseText.isEmpty()) {
                    return
                }
                currentMorseText.delete(currentMorseText.length - 1, currentMorseText.length)
                displayCurrentTextAsMorse()
            }
            ComposeViewModule.Event.DISPLAY_TEXT_LONG_PRESS -> {
                // Switches between displaying the text as Morse and plaintext.
                if (displayedAsPlaintext) {
                    displayCurrentTextAsMorse()
                } else {
                    displayCurrentTextAsPlaintext()
                }
            }
        }
    }

    private fun displayCurrentTextAsMorse() {
        viewModule.updateText(currentMorseText)
        displayedAsPlaintext = false
    }

    private fun displayCurrentTextAsPlaintext() {
        val plaintext = MorseHelper.convertToText(currentMorseText.toString())
        viewModule.updateText(SpannableString(plaintext))
        displayedAsPlaintext = true
    }
}
