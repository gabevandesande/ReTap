package edu.ucsc.retap.retap.compose

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.LayoutRes
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.BaseActivity
import edu.ucsc.retap.retap.common.BasePresenter
import edu.ucsc.retap.retap.compose.di.DaggerComposeComponent
import edu.ucsc.retap.retap.compose.presenter.ComposePresenter
import javax.inject.Inject

/**
 * Handles composing text in Morse code. It has a display area at the top that displays what the user has typed and
 * three buttons at the bottom that allow the user to enter a dot, dash, or a space. The user can tap the display
 * area to remove a character and long press it to switch between displaying plain text and morse code. When the user
 * has finished entering text, they can share what they have typed through the system's share sheet.
 *
 * Text will be converted into plain text before being sent.
 */
class ComposeActivity : BaseActivity() {
    // Dagger will automatically create everything with @Inject.
    @Inject lateinit var presenter: ComposePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use Dagger to automatically create the presenter.
        // Pass in components that aren't defined in our project and add them to the Builder definition.
        DaggerComposeComponent.builder()
                .activity(this)
                .rootView(findViewById(R.id.root))
                .build()
                .inject(this)
    }

    override fun actionButtonDrawable(): Drawable? = resources.getDrawable(R.drawable.ic_share, null)

    override fun isActionButtonVisible(): Boolean = true

    @LayoutRes
    override fun layoutId(): Int = R.layout.activity_compose

    override fun toolbarTitleText(): String = resources.getString(R.string.compose)

    override fun presenter(): BasePresenter = presenter
}
