package org.agrinext.agrimobile.Databases

import android.net.Uri
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider
import org.agrinext.agrimobile.BuildConfig
import com.raizlabs.android.dbflow.annotation.provider.ContentUri
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint





@ContentProvider(authority = ProduceDatabase.AUTHORITY,
        database = ProduceDatabase::class,
                baseContentUri = ProduceDatabase.BASE_CONTENT_URI)
@Database(name = ProduceDatabase.NAME, version = ProduceDatabase.VERSION)
class ProduceDatabase {
    companion object {
        const val NAME = "ProduceDatabase"
        const val VERSION = 1
        const val AUTHORITY = "org.agrinext.agrimobile.sync"
        const val BASE_CONTENT_URI = "content://"
    }

    @TableEndpoint(name = UserProviderModel.ENDPOINT, contentProvider = ProduceDatabase::class)
    class UserProviderModel {

        companion object {
            const val ENDPOINT = "Produce"
        }

        private fun buildUri(vararg paths: String): Uri {
            val builder = Uri.parse(ProduceDatabase.BASE_CONTENT_URI + ProduceDatabase.AUTHORITY).buildUpon()
            for (path in paths) {
                builder.appendPath(path)
            }
            return builder.build()
        }

        @ContentUri(path = UserProviderModel.ENDPOINT, type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        val CONTENT_URI = buildUri(ENDPOINT)
    }

}
