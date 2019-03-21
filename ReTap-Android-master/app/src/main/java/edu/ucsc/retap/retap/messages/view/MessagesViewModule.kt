package edu.ucsc.retap.retap.messages.view

import android.graphics.PorterDuff
import android.view.View
import android.widget.ProgressBar
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.di.ActivityScope
import javax.inject.Inject

/**
 * The view module for the messages list.
 */
@ActivityScope
class MessagesViewModule @Inject constructor(rootView: View) {
    private val loading = rootView.findViewById<View>(R.id.loading)

    init {
        val progressBarTintColor = rootView.resources.getColor(R.color.purpleColor)
        rootView.findViewById<ProgressBar>(R.id.progress_bar).indeterminateDrawable
                .setColorFilter(progressBarTintColor, PorterDuff.Mode.SRC_IN)
    }

    fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loading.visibility = View.GONE
    }
}