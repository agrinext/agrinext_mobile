package org.agrinext.agrimobile.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthenticatorActivity
import io.frappe.android.CallbackAsync.AuthReqCallback
import io.frappe.android.Controllers.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject


class MainActivity : BaseCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal lateinit var mAccountManager: AccountManager
    internal lateinit var accounts: Array <Account>
    val ACCOUNT_TYPE = "ACCOUNT_TYPE"
    val TAG = "AgriNext"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fireUp()
    }

    override fun onRestart() {
        super.onRestart()
        fireUp()
    }

    override fun onResume() {
        super.onResume()
        fireUp()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.list_view, menu)
        setupProfilePhoto()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_market -> {
                setupFragment(MarketActivity())
            }
            R.id.nav_sellers -> {
                setupFragment(UsersActivity())
            }
            /*
            R.id.nav_my_profile -> {
                setupFragment(UserProfile())
            }
            */
            R.id.nav_my_produce -> {
                setupFragment(ProduceActivity())
            }
            R.id.nav_invite -> {
                shareInvite()
            }
            R.id.nav_locations -> {
                setupFragment(LocationActivity())
            }
            R.id.nav_items -> {
                setupFragment(ItemActivity())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setupFragment(fragment:Fragment) {
        linearLayoutDesktop.visibility = View.GONE
        var fragmentManager = getSupportFragmentManager()
        var ft = fragmentManager.beginTransaction()
        ft.replace(R.id.screen_area, fragment)
        ft.commit()
    }

    fun shareInvite() {
        var shareData = getString(R.string.shareData) + " https://agrinext.org"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareData)
        sendIntent.type = "text/plain"
        startActivity(createChooser(sendIntent, "Share"))
    }

    fun fireUp() {
        mAccountManager = AccountManager.get(this)
        accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)

        val desktop_text = findViewById<TextView>(R.id.desktop_text)
        val linearLayoutDesktop = findViewById<LinearLayout>(R.id.linearLayoutDesktop)

        accounts = mAccountManager.getAccountsByType(BuildConfig.APPLICATION_ID)
        if (accounts.size == 1) {
            setSupportActionBar(toolbar)

            val toggle = ActionBarDrawerToggle(
                    this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()

            nav_view.setNavigationItemSelectedListener(this)
            desktop_text.setText(R.string.welcome)
            linearLayoutDesktop.onClick {
                toast(R.string.app_name)
            }
        } else {
            val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()
            toggle.isDrawerIndicatorEnabled = false

            desktop_text.setText(R.string.tapToSignIn)
            linearLayoutDesktop.onClick {
                startActivity<AuthenticatorActivity>(
                        ACCOUNT_TYPE to BuildConfig.APPLICATION_ID
                )
            }
        }
    }

    private fun setupProfilePhoto() {
        navHeaderLinearLayout.setOnClickListener {
            setupFragment(UserProfile())
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        var picture = ""
        val request = OAuthRequest(Verb.GET, frappeClient?.getServerURL() + getString(R.string.openIDEndpoint))
        val callback = object : AuthReqCallback {
            override fun onErrorResponse(error: String) {
                Log.d("responseError", error)
            }

            override fun onSuccessResponse(result: String) {
                val jsonResponse = JSONObject(result)
                picture = jsonResponse.getString("picture")
                val uri = Uri.parse(picture)
                (ivProfileImageView as SimpleDraweeView).imageURI = uri
                userFullName.setText(jsonResponse.getString("name"))
                userEmailAddress.setText(jsonResponse.getString("email"))
            }
        }
        frappeClient?.executeRequest(request, callback)
    }
}
