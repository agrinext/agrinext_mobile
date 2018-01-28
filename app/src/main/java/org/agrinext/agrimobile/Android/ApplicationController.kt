package org.agrinext.agrimobile.Android

import android.app.Application
import android.content.Context
import com.android.volley.Network
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.HttpStack
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import com.mntechnique.otpmobileauth.auth.RetrieveAuthTokenTask
import org.agrinext.agrimobile.BuildConfig
import org.acra.*
import org.acra.annotation.*
import org.acra.config.*
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import org.json.JSONObject
import org.agrinext.agrimobile.R
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig


/**
 * Created by revant on 31/12/17.
 */

@AcraCore(buildConfigClass = BuildConfig::class)
@AcraNotification(
        resText = R.string.notification_text,
        resTitle = R.string.notification_title,
        resChannelName = R.string.app_name)
class ApplicationController : Application() {
    internal var activityVisible = false
    lateinit var mRequestQueue: RequestQueue
    lateinit var mSerialRequestQueue: RequestQueue
    var MAX_SERIAL_THREAD_POOL_SIZE = 1
    val MAX_CACHE_SIZE = 2 * 1024 * 1024 //2 MB
    var builder: CoreConfigurationBuilder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        val diskCacheConfig = DiskCacheConfig.newBuilder(this).build()
        val config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build()
        Fresco.initialize(this, config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        val app = this
        val frappeClient = FrappeClient(this)
        val serverUrl = frappeClient.getServerURL()

        val getAccessTokenCallback = object : AuthReqCallback {
            override fun onSuccessResponse(result: String) {
                val bearerToken = JSONObject(result)
                var headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${bearerToken.getString("access_token")}")
                setupBuilder(headers, serverUrl)
                ACRA.init(app, builder!!)

            }
            override fun onErrorResponse(error: String) {
                var headers = HashMap<String, String>()
                val guest_api_key = getString(R.string.guest_api_key)
                headers.put("X-API-KEY", guest_api_key)
                setupBuilder(headers, serverUrl)
                ACRA.init(app, builder!!)
            }
        }

        RetrieveAuthTokenTask(this, getAccessTokenCallback).execute()
    }

    fun setupBuilder(headers: HashMap<String,String>,
                     serverUrl:String) {
        builder = CoreConfigurationBuilder(this)
        builder?.setBuildConfigClass(BuildConfig::class.java)
                ?.setReportFormat(StringFormat.JSON)
                ?.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder::class.java)
                ?.setUri("$serverUrl/api/method/agrinext.api.report_error")
                ?.setHttpMethod(HttpSender.Method.POST)
                ?.setHttpHeaders(headers)
                ?.setEnabled(true)
    }

    fun getRequestQueue(): RequestQueue {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }

        return mRequestQueue
    }

    fun getSerialRequestQueue(): RequestQueue {
        if (mSerialRequestQueue == null) {
            mSerialRequestQueue = prepareSerialRequestQueue(applicationContext)
            mSerialRequestQueue.start()
        }
        return mSerialRequestQueue
    }

    fun prepareSerialRequestQueue(context: Context): RequestQueue {
        val cache = DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE)
        val network = getNetwork()
        return RequestQueue(cache, network, MAX_SERIAL_THREAD_POOL_SIZE)
    }

    fun getNetwork(): Network {
        val stack: HttpStack
        val userAgent = "volley/0"
        stack = HurlStack()
        return BasicNetwork(stack)
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    fun isActivityAvailable() : Boolean{
        return activityVisible
    }

    fun activityResumed() {
        activityVisible = true
    }

    fun activityPaused() {
        activityVisible = false
    }

    companion object {
        @get:Synchronized
        var instance: ApplicationController? = null
            private set
    }
}
