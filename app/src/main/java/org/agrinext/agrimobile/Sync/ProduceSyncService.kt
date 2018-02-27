package org.agrinext.agrimobile.Sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.Nullable


class ProduceSyncService : Service() {
    /**
     * Lock use to synchronize instantiation of SyncAdapter.
     */
    private val LOCK = Any()
    private var syncAdapter: ProduceSync? = null


    override fun onCreate() {
        // SyncAdapter is not Thread-safe
        synchronized(LOCK) {
            // Instantiate our SyncAdapter
            syncAdapter = ProduceSync(this, false)
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        // Return our SyncAdapter's IBinder
        return syncAdapter!!.getSyncAdapterBinder()
    }
}