package edu.ucsc.retap.retap.messages.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import edu.ucsc.retap.retap.messages.adapter.MessagesAdapter
import edu.ucsc.retap.retap.messages.data.MessagesSource
import edu.ucsc.retap.retap.messages.model.Message
import edu.ucsc.retap.retap.messages.view.MessagesViewModule
import edu.ucsc.retap.retap.morse.MorseInteractor
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Test for MessagesPresenter
 */
class MessagesPresenterTest {
    companion object {
        private const val TEST_SELECTED_ITEM_INDEX = 1
    }

    @Mock private lateinit var messagesViewModule: MessagesViewModule
    @Mock private lateinit var messagesAdapter: MessagesAdapter
    @Mock private lateinit var messagesSource: MessagesSource
    @Mock private lateinit var morseInteractor: MorseInteractor

    private lateinit var ioScheduler: TestScheduler
    private lateinit var mainScheduler: TestScheduler
    private lateinit var messagesPresenter: MessagesPresenter

    private val messages: List<Message> = listOf(mock(), mock(), mock())

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(messagesSource.sourceChangedObservable()).thenReturn(Observable.empty())
        whenever(messagesSource.getMessages()).thenReturn(Single.just(messages))
        whenever(messagesAdapter.observeItemClick()).thenReturn(Observable.empty())
        whenever(messagesAdapter.selectedItemIndex).thenReturn(TEST_SELECTED_ITEM_INDEX)
        whenever(messagesAdapter.items).thenReturn(messages)

        ioScheduler = TestScheduler()
        mainScheduler = TestScheduler()

        messagesPresenter = MessagesPresenter(
                messagesViewModule,
                messagesAdapter,
                messagesSource,
                morseInteractor,
                ioScheduler,
                mainScheduler
        )
    }

    @Test
    fun startPresenting() {
        messagesPresenter.startPresenting()

        ioScheduler.triggerActions()
        mainScheduler.triggerActions()

        verify(messagesSource).getMessages()
        verify(messagesSource).sourceChangedObservable()
        verify(messagesAdapter).observeItemClick()
        verify(messagesAdapter).items = any()
        verify(messagesViewModule).hideLoading()
    }

    @Test
    fun stopPresenting() {
        messagesPresenter.stopPresenting()

        verify(morseInteractor).stop()
    }

    @Test
    fun onVolumeUp() {
        messagesPresenter.onVolumeUp()

        verify(messagesAdapter).selectedItemIndex = (TEST_SELECTED_ITEM_INDEX - 1)
    }

    @Test
    fun onVolumeDown() {
        messagesPresenter.onVolumeDown()

        verify(messagesAdapter).selectedItemIndex = (TEST_SELECTED_ITEM_INDEX + 1)
    }
}
