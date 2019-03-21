package edu.ucsc.retap.retap.reminders.repository

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.Constants.PREF_REMINDERS
import edu.ucsc.retap.retap.common.di.ApplicationScope
import edu.ucsc.retap.retap.contacts.model.Contact
import edu.ucsc.retap.retap.messages.data.MessagesSource
import edu.ucsc.retap.retap.messages.model.Message
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.forEach

/**
 * Handles adding and removing reminders. Also includes functionality for persistence by storing the reminders as
 * JSON in shared preferences.
 */
@ApplicationScope
class RemindersRepository @Inject constructor(private val context: Context) : MessagesSource {
    companion object {
        private const val EMPTY_CONTACT_FIELD = ""
    }

    private val messages = ArrayList<Message>()
    private val sourceChangedPublishSubject = PublishSubject.create<MessagesSource.Event>()
    private val gson = Gson()

    init {
        loadFromSharedPreferences()
    }

    override fun getMessages(): Single<List<Message>> =
            Single.just(messages)

    override fun sourceChangedObservable(): Observable<MessagesSource.Event> =
            sourceChangedPublishSubject

    /**
     * Adds a reminder.
     * @param content the content of the reminder
     */
    fun addReminder(content: String) {
        val formattedTime = SimpleDateFormat.getDateTimeInstance().format(Date())
        messages.add(Message(Contact(EMPTY_CONTACT_FIELD, formattedTime), content, System.currentTimeMillis()))
        sourceChangedPublishSubject.onNext(MessagesSource.Event.SourceChanged)
        saveToSharedPreferences()
    }

    /**
     * Deletes a reminder.
     * @param index the index of the reminder to delete
     */
    fun deleteReminder(index: Int) {
        messages.removeAt(index)
        sourceChangedPublishSubject.onNext(MessagesSource.Event.SourceChanged)
        saveToSharedPreferences()
    }

    private fun loadFromSharedPreferences() {
        val remindersJson = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_REMINDERS, "")

        if (remindersJson.isEmpty()) {
            addReminder(context.resources.getString(R.string.reminders_prompt))
            return
        }

        try {
            val messageArray = gson.fromJson(remindersJson, Array<Message>::class.java)
            messageArray ?: return
            messageArray.forEach {
                messages.add(it)
            }
        } catch (e: JsonSyntaxException) {
            return
        }
    }

    private fun saveToSharedPreferences() {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_REMINDERS, gson.toJson(messages))
                .apply()
    }
}
