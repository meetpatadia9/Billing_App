@file:Suppress("DEPRECATION")

package com.codebyzebru.myapplication.activities

import android.app.Dialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.codebyzebru.myapplication.databinding.ActivityHomeBinding
import com.codebyzebru.myapplication.databinding.NoInternetBinding
import com.codebyzebru.myapplication.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var binding: ActivityHomeBinding

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    lateinit var naviView: NavigationView
    private var snackBar: Snackbar? = null
    private var isConnected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  REGISTERING BROADCAST RECEIVER FOR INTERNET CONNECTIVITY
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        naviView = binding.homeNavigationView

        //  DRAWER LAYOUT
        drawerLayout = binding.homeDrawerLayout
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //  DEFAULT FRAGMENT
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.layout_home, HomeFragment(), "HOME").commit()
            title = "Home"
        }

        //  NAVIGATION-ITEM-SELECTED LISTENER
        naviView.setNavigationItemSelectedListener {
            it.isChecked = true
            when (it.itemId) {
                R.id.drawer_item_home -> replaceFragments(HomeFragment(), it.title.toString(), "HOME")
                R.id.drawer_item_bill -> replaceFragments(BillFragment(), it.title.toString(), "BILL")
                R.id.drawer_item_inventory -> replaceFragments(
                    InventoryFragment(),
                    it.title.toString(),
                    "INVENTORY"
                )
                R.id.drawer_item_parties -> replaceFragments(PartiesFragment(), it.title.toString(), "PARTIES")
                R.id.drawer_item_history -> replaceFragments(HistoryFragment(), it.title.toString(), "HISTORY")
                R.id.drawer_item_setting -> replaceFragments(SettingFragment(), it.title.toString(), "SETTING")
                else -> {
                    true
                }
            }
        }
    }

    private fun replaceFragments(fragment: Fragment, title: String, tag: String): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_home, fragment, tag)
            .addToBackStack(null)
            .commit()
        drawerLayout.closeDrawers()
        setTitle(title)
        return true
    }

    //  required to enable Option menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /*
            CHECKING FOR ACTIVE INTERNET CONNECTION
    */
    private fun noInternet() {
        isConnected = false
    }

    private fun internetConnected() {
        isConnected = true
    }

    override fun onNetworkConnectionChange(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            noInternet()
            snackBar = Snackbar.make(findViewById(R.id.layout_home), "Connection loss", Snackbar.LENGTH_LONG)

            val view = snackBar?.view
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.setBackgroundTint(resources.getColor(R.color.red))
            val params = view?.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackBar?.show()
        } else {
            snackBar?.dismiss()
            internetConnected()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity"))
    override fun onBackPressed() {
        super.onBackPressed()
        val myFragment: HomeFragment? = supportFragmentManager.findFragmentByTag("MY_FRAGMENT") as HomeFragment?
        if (myFragment != null && myFragment.isVisible) {
            finish()
        }
    }

}