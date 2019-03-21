package edu.ucsc.retap.retap.reminders.view

import android.content.Context
import android.support.v7.app.AlertDialog
import android.widget.EditText
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.di.ActivityScope
import javax.inject.Inject

/**
 * View module for the reminders feature.
 */
@ActivityScope
class RemindersViewModule @Inject constructor(
        private val context: Context,
        private val composeDelegate: ComposeDelegate
) {
    interface ComposeDelegate {
        fun onCompose(text: String)
    }

    /**
     * Shows the dialog allowing users to compose a new reminder.
     */
    fun showComposeDialog() {
        val resources = context.resources
        val contentEditText = EditText(context)
        val dialog = AlertDialog.Builder(context)
                .setTitle(resources.getString(R.string.add_reminder))
                .setMessage(resources.getString(R.string.add_reminder_prompt))
                .setView(contentEditText)
                .setPositiveButton(resources.getString(R.string.add_button)) { _, _ ->
                    composeDelegate.onCompose(contentEditText.text.toString())
                }
                .setNegativeButton(resources.getString(R.string.cancel_button), null)
                .create()
        dialog.show()
    }
}
