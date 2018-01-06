package org.agrinext.agrimobile.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.mntechnique.otpmobileauth.auth.AuthenticatorActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.agrinext.agrimobile.Android.BaseCompatActivity
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.R
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (data != null){
            if (requestCode == 1 && resultCode == Activity.RESULT_OK){
                if (!data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME).isNullOrEmpty()){
                    for(a in accounts){
                        if(a.name.equals(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))){
                            Log.d(TAG, a.name)
                        }
                    }
                } else {
                    finish()
                }
            }
        } else if (data === null) {
            toast(R.string.somethingWrong)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
        // menuInflater.inflate(R.menu.main, menu)
        return false
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
                startActivity(Intent(this, ListingActivity::class.java))
            }
            R.id.nav_sellers -> {
                startActivity(Intent(this, ListingActivity::class.java))
            }
            R.id.nav_my_profile -> {
                startActivity(Intent(this, UserProfile::class.java))
            }
            R.id.nav_my_produce -> {
                startActivity(Intent(this, ProduceActivity::class.java))
            }
            R.id.nav_invite -> {
                share("https://agrinext.org")
            }
            R.id.nav_locations -> {
                startActivity(Intent(this, ListingActivity::class.java))
            }
            R.id.nav_items -> {
                startActivity(Intent(this, ListingActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
        } else {
            desktop_text.setText(R.string.tapToSignIn)
            linearLayoutDesktop.onClick {
                startActivity<AuthenticatorActivity>(
                        ACCOUNT_TYPE to BuildConfig.APPLICATION_ID
                )
            }
        }
    }
}
