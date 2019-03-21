package edu.ucsc.retap.retap.common

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.inbox.InboxActivity
import edu.ucsc.retap.retap.reminders.RemindersActivity
import edu.ucsc.retap.retap.settings.SettingsActivity

/**
 * All activities should extend off of BaseActivity. It includes a toolbar with an action and handles navigation to
 * other activities via a navigation drawer. It also automatically calls activity lifecycle methods like onStart and
 * onStop to a base presenter supplied in presenter().
 *
 * Activities in this app should override layoutId instead of using setContentView and override the action button
 * methods to customize the toolbar at the top. Subclasses should also override presenter() if they have one.
 */
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        drawerLayout = findViewById(R.id.drawer_layout)
        LayoutInflater.from(this).inflate(layoutId(), findViewById(R.id.content_frame), true)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setUpToolbarAction()
        setUpToolbarText()
        setUpNavigationDrawer()
    }

    override fun onStart() {
        super.onStart()
        presenter().startPresenting()
    }

    override fun onStop() {
        super.onStop()
        presenter().stopPresenting()
    }

    private fun setUpToolbarAction() {
        val navigationAction: ImageView = findViewById(R.id.action_icon)
        if (isActionButtonVisible()) {
            navigationAction.visibility = View.VISIBLE
            navigationAction.setOnClickListener {
                onActionButtonClicked()
            }
            navigationAction.setImageDrawable(actionButtonDrawable())
        } else {
            navigationAction.visibility = View.GONE
        }
    }

    private fun setUpToolbarText() {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = toolbarTitleText()
    }

    private fun setUpNavigationDrawer() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_inbox -> {
                    if (this !is InboxActivity) {
                        val intent = Intent(this, InboxActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.nav_reminders -> {
                    if (this !is RemindersActivity) {
                        val intent = Intent(this, RemindersActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.nav_settings -> {
                    if (this !is SettingsActivity) {
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            true
        }
    }

    /**
     * Subclasses should override this to set their content view rather than using setContentView. This ensures that the
     * navigation drawer doesn't get removed.
     */
    @LayoutRes
    protected abstract fun layoutId(): Int

    /**
     * @return the toolbar's title
     */
    protected abstract fun toolbarTitleText(): String

    /**
     * @return the action button's drawable
     */
    protected open fun actionButtonDrawable(): Drawable? = null

    /**
     * @return true if this activity should have an action button
     */
    protected open fun isActionButtonVisible(): Boolean = false

    /**
     * Override this method to define behavior when the action button is clicked.
     */
    protected open fun onActionButtonClicked() {
        presenter().onActionButtonClicked()
    }

    /**
     * @return main top-level presenter for the activity. Can be null.
     */
    protected open fun presenter(): BasePresenter = BasePresenter.NONE

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                presenter().onVolumeDown()
                true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                presenter().onVolumeUp()
                true
            }
            else -> {
                super.onKeyDown(keyCode, event)
            }
        }
    }
}
