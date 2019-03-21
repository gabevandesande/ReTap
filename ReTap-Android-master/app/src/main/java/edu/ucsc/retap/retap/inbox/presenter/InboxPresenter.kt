package edu.ucsc.retap.retap.inbox.presenter

import android.app.Activity
import android.os.Handler
import edu.ucsc.retap.retap.common.AppNavigator
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.common.Constants.IO_SCHEDULER
import edu.ucsc.retap.retap.common.Constants.MAIN_SCHEDULER
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.messages.adapter.MessagesAdapter
import edu.ucsc.retap.retap.messages.data.MessagesSource
import edu.ucsc.retap.retap.messages.model.Message
import edu.ucsc.retap.retap.messages.view.MessagesViewModule
import edu.ucsc.retap.retap.permissions.PermissionsInteractor
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

/**
 * Presenter for the inbox functionality. See InboxActivity for details.
 */
@ActivityScope
class InboxPresenter @Inject constructor(
        val activity: Activity,
        private val appNavigator: AppNavigator,
        private val mainThreadHandler: Handler,
        private val permissionsInteractor: PermissionsInteractor,
        private val messageViewModule: MessagesViewModule,
        private val messagesAdapter: MessagesAdapter,
        private val messagesSource: MessagesSource,
        @Named(IO_SCHEDULER) private val ioScheduler: Scheduler,
        @Named(MAIN_SCHEDULER) private val mainScheduler: Scheduler
) : BasePresenter {

    companion object {
        private const val AUTOMATICALLY_SELECT_DELAY_MS = 2000L
    }

    private val compositeDisposable = CompositeDisposable()

    override fun startPresenting() {
        observeViewEvents()
        messageViewModule.showLoading()
        compositeDisposable.add(
                permissionsInteractor
                        .observePermissionEvents()
                        .observeOn(mainScheduler)
                        .subscribe {
                            if (!messagesAdapter.items.isEmpty()) {
                                messageViewModule.hideLoading()
                            }
                            loadMessages()
                        }
        )
    }

    override fun stopPresenting() {
        compositeDisposable.clear()
    }

    override fun onActionButtonClicked() = appNavigator.navigateToCompose()

    override fun onVolumeUp() {
        val newIndex = maxOf(-1, messagesAdapter.selectedItemIndex - 1)
        setItemIndex(newIndex)
        startNavigateToConversationTimer()
    }

    override fun onVolumeDown() {
        val newIndex = minOf(messagesAdapter.items.size - 1, messagesAdapter.selectedItemIndex + 1)
        setItemIndex(newIndex)
        startNavigateToConversationTimer()
    }

    private fun observeViewEvents() {
        compositeDisposable.add(
                messagesAdapter.observeItemClick()
                        .doOnNext {
                            setItemIndex(it)
                            appNavigator.navigateToConversation(messagesAdapter.items[it].contact)
                        }
                        .subscribe()
        )
    }

    private fun loadMessages() {
        compositeDisposable.add(
                messagesSource.getMessages()
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnSuccess {
                            val items = it
                                    .distinctBy {
                                        it.contact.phoneNumber
                                    }

                            messagesAdapter.items = items
                            messageViewModule.hideLoading()
                        }
                        .subscribe()
        )
    }

    private fun setItemIndex(index: Int) {
        messagesAdapter.selectedItemIndex = index
    }

    private fun startNavigateToConversationTimer() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        val message = currentSelectedItem() ?: return
        mainThreadHandler.postDelayed({
            appNavigator.navigateToConversation(message.contact)
        },
                AUTOMATICALLY_SELECT_DELAY_MS
        )
    }

    private fun currentSelectedItem(): Message? {
        val selectedIndex = messagesAdapter.selectedItemIndex
        if (selectedIndex < 0 || selectedIndex >= messagesAdapter.itemCount) {
            return null
        }
        return messagesAdapter.items[selectedIndex]
    }
}
