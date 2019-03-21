package edu.ucsc.retap.retap.common

/**
 * Defines an interface for presenters within the app. Presenters should handle when the activity starts, stops, and
 * when the action button is clicked.
 */
interface BasePresenter {
    companion object {
        val NONE = object : BasePresenter {}
    }

    /**
     * Called when the activity starts.
     */
    fun startPresenting() {}

    /**
     * Called when the activity stops.
     */
    fun stopPresenting() {}

    /**
     * Called when the activity's action button is clicked.
     */
    fun onActionButtonClicked() {}

    /**
     * Called when the volume up button on the device is clicked.
     */
    fun onVolumeUp() {}

    /**
     * Called when the volume down button on the device is clicked.
     */
    fun onVolumeDown() {}
}
