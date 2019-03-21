package edu.ucsc.retap.retap.messages.di

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import dagger.Module
import dagger.Provides
import edu.ucsc.retap.retap.R
import edu.ucsc.retap.retap.common.Constants.IO_SCHEDULER
import edu.ucsc.retap.retap.common.Constants.MAIN_SCHEDULER
import edu.ucsc.retap.retap.common.di.ActivityScope
import edu.ucsc.retap.retap.messages.adapter.MessagesAdapter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

/**
 * Module for providing dependencies to MessagesComponent.
 */
@Module
open class MessagesModule {

    @ActivityScope
    @Provides
    fun provideHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }

    @ActivityScope
    @Provides
    fun provideLayoutInflater(activity: Activity): LayoutInflater {
        return LayoutInflater.from(activity)
    }

    @ActivityScope
    @Provides
    @Named(IO_SCHEDULER)
    fun provideIOScheduler(): Scheduler {
        return Schedulers.io()
    }

    @ActivityScope
    @Provides
    @Named(MAIN_SCHEDULER)
    fun provideMainScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @ActivityScope
    @Provides
    fun provideAdapter(
            activity: Activity,
            rootView: View,
            layoutInflater: LayoutInflater,
            @LayoutRes itemLayoutId: Int
    ): MessagesAdapter {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.messages_view)
        val adapter = MessagesAdapter(layoutInflater, itemLayoutId)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        return adapter
    }
}
