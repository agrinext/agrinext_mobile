package org.agrinext.agrimobile.Sync

import android.content.SyncResult
import android.content.ContentProviderClient
import android.os.Bundle
import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.util.Log

class ProduceSync : AbstractThreadedSyncAdapter {
    // private val manager: SomeManagerIUseToDoStuff

    constructor(context: Context, autoInitialize: Boolean) : super(context, autoInitialize) {
        //manager = SomeManagerIUseToDoStuff(context)
    }

    constructor(context: Context, autoInitialize: Boolean,
                allowParallelSyncs: Boolean) : super(context, autoInitialize, allowParallelSyncs) {
        //manager = ApolloBackendConfigurationManager(context)
    }

    override fun onPerformSync(account: Account, extras: Bundle, authority: String,
                               provider: ContentProviderClient, syncResult: SyncResult) {
        Log.i(TAG, "onPerformSync() was called")

        //manager.fetchDataFromServer()
    }

    companion object {
        private val TAG = ProduceSync::class.java.simpleName
    }
}