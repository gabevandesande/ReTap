package edu.ucsc.retap.retap.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import edu.ucsc.retap.retap.common.di.ActivityScope
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Facilitates getting app permissions from the user.
 */
@ActivityScope
class PermissionsInteractor @Inject constructor(
        private val activity: Activity
) {
    companion object {
        private const val SMS_PERMISSION_CODE = 1
    }

    enum class Event {
        PERMISSIONS_GRANTED
    }

    private val permissionsGrantedEventSubject = BehaviorSubject.create<Event>()

    init {
        if (arePermissionsGranted()) {
            permissionsGrantedEventSubject.onNext(Event.PERMISSIONS_GRANTED)
        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_CONTACTS
                    ), SMS_PERMISSION_CODE
            )
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED
    }

    fun onRequestPermissionsResult(
            requestCode: Int,
            grantResults: IntArray
    ) {
        when (requestCode) {
            SMS_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGrantedEventSubject.onNext(Event.PERMISSIONS_GRANTED)
                }
                return
            }
        }
    }

    fun observePermissionEvents(): Observable<Event> = permissionsGrantedEventSubject
}
