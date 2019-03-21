package edu.ucsc.retap.retap.messages.presenter

import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.common.Constants.IO_SCHEDULER
import edu.ucsc.retap.retap.common.Constants.MAIN_SCHEDULER
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.messages.adapter.MessagesAdapter
import edu.ucsc.retap.retap.messages.data.MessagesSource
import edu.ucsc.retap.retap.messages.view.MessagesViewModule
import edu.ucsc.retap.retap.morse.MorseInteractor
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

/**
 * Presenter for displaying a list of messages in a conversation. See MessagesActivity for more details.
 */
@ActivityScope
class MessagesPresenter @Inject constructor(
        private val messagesViewModule: MessagesViewModule,
        private val messagesAdapter: MessagesAdapter,
        private val messagesSource: MessagesSource,
        private val morseInteractor: MorseInteractor,
        @Named(IO_SCHEDULER) private val ioScheduler: Scheduler,
        @Named(MAIN_SCHEDULER) private val mainScheduler: Scheduler
) : BasePresenter {

    private val compositeDisposable = CompositeDisposable()

    override fun startPresenting() {
        if (messagesAdapter.items.isEmpty()) {
            messagesViewModule.showLoading()
        }
        loadMessages()
        observeSourceChanges()
        observeViewEvents()
    }

    override fun stopPresenting() {
        compositeDisposable.clear()
        morseInteractor.stop()
    }

    private fun loadMessages() {
        compositeDisposable.add(
                messagesSource.getMessages()
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnSuccess {
                            messagesAdapter.items = it
                            messagesViewModule.hideLoading()
                            it.firstOrNull() ?: return@doOnSuccess
                            if (messagesAdapter.selectedItemIndex < 0) {
                                setItemIndex(0)
                            }
                        }
                        .subscribe()
        )
    }

    private fun observeSourceChanges() {
        compositeDisposable.add(
                messagesSource.sourceChangedObservable()
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnNext {
                            morseInteractor.stop()
                            loadMessages()
                        }
                        .subscribe()
        )
    }

    private fun observeViewEvents() {
        compositeDisposable.add(
                messagesAdapter.observeItemClick()
                        .doOnNext {
                            setItemIndex(it)
                        }
                        .subscribe()
        )
    }

    override fun onVolumeUp() {
        val newIndex = maxOf(-1, messagesAdapter.selectedItemIndex - 1)
        setItemIndex(newIndex)
    }

    override fun onVolumeDown() {
        val newIndex = minOf(messagesAdapter.items.size - 1, messagesAdapter.selectedItemIndex + 1)
        setItemIndex(maxOf(0, newIndex))
    }

    private fun setItemIndex(index: Int) {
        if (index < 0) {
            morseInteractor.stop()
        } else if (messagesAdapter.items.size > index) {
            morseInteractor.vibrate(messagesAdapter.items[index])
        }
        messagesAdapter.selectedItemIndex = index
    }
}
