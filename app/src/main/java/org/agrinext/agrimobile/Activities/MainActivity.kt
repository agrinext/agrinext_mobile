package org.agrinext.agrimobile.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.mntechnique.otpmobileauth.auth.AuthenticatorActivity
import io.frappe.android.Controllers.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.Fragments.*
import org.agrinext.agrimobile.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import io.frappe.android.CallbackAsync.AuthReqCallback
import io.frappe.android.Frappe.FrappeClient
import org.json.JSONObject

class MainActivity : BaseCompatActivity() {
    internal lateinit var mAccountManager: AccountManager
    internal lateinit var accounts: Array<Account>
    var current_position: Int = 0
    val ACCOUNT_TYPE = "ACCOUNT_TYPE"
    val TAG = "AgriNext"

    companion object {
        val LOCATION = "LOCATION"
    }

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

    override fun onPause() {
        super.onPause()
        current_position = bottom_navigation.currentItem
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.list_view, menu)
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

    fun setupFragment(fragment: Fragment) {
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
        Log.d("Account size", accounts.size.toString())
        if (accounts.size == 1) {

            setSupportActionBar(toolbar)
            getSupportActionBar()!!.setDisplayShowTitleEnabled(false);
            setupBottomNavigation()
            setupLocationSpinner()

        } else {

            desktop_text.setText(R.string.tapToSignIn)
            linearLayoutDesktop.onClick {
                startActivity<AuthenticatorActivity>(
                        ACCOUNT_TYPE to BuildConfig.APPLICATION_ID
                )
            }
        }
    }

    fun setupBottomNavigation() {
        val bottomNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation

        if(bottomNavigation.itemsCount == 0) {
            // Create items
            val market = AHBottomNavigationItem("Market", R.drawable.ic_group_work, R.color.colorFrappe)
            val my_produce = AHBottomNavigationItem("My Produce", R.drawable.ic_filter_vintage, R.color.colorFrappe)
            val sellers = AHBottomNavigationItem("Sellers", R.drawable.ic_group, R.color.colorFrappe)
            val profile = AHBottomNavigationItem("Profile", R.drawable.ic_group, R.color.colorFrappe)

            // Add items
            bottomNavigation.addItem(market)
            bottomNavigation.addItem(my_produce)
            bottomNavigation.addItem(sellers)
            bottomNavigation.addItem(profile)

            // Set background color
            bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#cfd1d3"))

            // Change colors
            bottomNavigation.setAccentColor(Color.parseColor("#1b5e20"))
            bottomNavigation.setInactiveColor(Color.parseColor("#747474"))

            // Set listeners
            bottomNavigation.setOnTabSelectedListener { position, wasSelected ->
                when (position) {
                    0 ->
                        setupFragment(MarketActivity())
                    1 ->
                        setupFragment(ProduceActivity())
                    2 ->
                        setupFragment(UsersActivity())
                    3 ->
                        setupFragment(UserProfile())
                }
                true
            }
        }

        // Setting the very 1st item as home screen.
        bottomNavigation.setCurrentItem(current_position);
        bottomNavigation.setBehaviorTranslationEnabled(false);

    }

    fun setupLocationSpinner() {
        var locationSpinner = findViewById<Spinner>(R.id.locationSpinner)
        val bottomNavigation = findViewById<View>(R.id.bottom_navigation) as AHBottomNavigation

        var arrayData = ArrayList<String>()
        val sharedPref = this.getSharedPreferences(LOCATION, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val request = FrappeClient(this).get_all(
                doctype = "Location"
        )

        val responseCallback = object : AuthReqCallback {
            override fun onSuccessResponse(result: String) {
                val response = JSONObject(result).getJSONArray("data")

                // JSON Array from frappe's listing
                for (i in 0 until response.length()) {
                    arrayData.add(response.getJSONObject(i).get("name").toString())
                }

                var locationAdapter = ArrayAdapter<String>(this@MainActivity, R.layout.spinner_item, arrayData)
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                locationSpinner.adapter = locationAdapter

                if (sharedPref.contains("location")) {
                    val spinnerPosition = locationAdapter.getPosition(sharedPref.getString("location", ""))
                    locationSpinner.setSelection(spinnerPosition)
                } else {
                    val lang = locationSpinner.getSelectedItem().toString()
                    editor.putString("location", lang).apply()
                }
            }

            override fun onErrorResponse(error: String) {
                Toast.makeText(this@MainActivity, io.frappe.android.R.string.somethingWrong, Toast.LENGTH_SHORT).show()
            }
        }

        FrappeClient(this).executeRequest(request, responseCallback)

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                editor.putString("location", selectedItem).apply()
                bottomNavigation.setCurrentItem(bottom_navigation.currentItem);
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
    }
}
