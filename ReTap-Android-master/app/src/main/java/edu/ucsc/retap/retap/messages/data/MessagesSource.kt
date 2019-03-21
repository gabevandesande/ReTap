package edu.ucsc.retap.retap.messages.data

import edu.ucsc.retap.retap.messages.model.Message
import io.reactivex.Observable
import io.reactivex.Single

interface MessagesSource {
    enum class Event {
        SourceChanged
    }

    fun getMessages(): Single<List<Message>>

    fun sourceChangedObservable(): Observable<Event>
}
