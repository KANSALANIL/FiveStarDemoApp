package com.fivestarmind.android.mvp.activity

import android.app.ActivityGroup
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_home_tabs.tabHost


class HomeTabsActivity : ActivityGroup() {
    var TAG: String = "HomeTabsActivity"
    var homeActivity: Intent? = null
    var color: String? = null
    var mTabHosts: TabHost? = null
    var backPressed: Int = 0
    var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tabs)

        setupTabHost()

    }

    fun setupTabHost() {
        mTabHosts = tabHost
        mTabHosts!!.setup(this.localActivityManager)
        mTabHosts!!.currentTab = 0

        // TabChangedListener
        mTabHosts!!.setOnTabChangedListener { tabId ->

            showBottomTabs()

        }

        mTabHosts!!.tabWidget.dividerDrawable = null
        color = resources.getString(0 + R.color.color_tab_text)

        // Intents for bottom tab activity
        homeActivity = Intent(applicationContext, HomeActivity::class.java)
        val bookmarkActivity = Intent(applicationContext, BookmarksAllCategory::class.java)
        //   val chatUserListActivity = Intent(applicationContext, ChatUserListActivity::class.java)
        val gymActivity = Intent(applicationContext, GymActivity::class.java)
        val filtereActivity = Intent(applicationContext, FilterActivity::class.java)

        setNewTab(
            this,
            mTabHosts!!,
            "tab1",
            R.string.home,
            R.drawable.ic_home_tab,
            homeActivity!!,
            color!!
        )

        setNewTab(
            this,
            mTabHosts!!,
            "tab2",
            R.string.gym_tab,
            R.drawable.ic_gym_tab,
            gymActivity,
            color!!
        )

        setNewTab(
            this,
            mTabHosts!!,
            "tab3",
            R.string.discover_tab,
            R.drawable.ic_discover_tab,
            filtereActivity,
            color!!
        )

        /*   setNewTab(
                   this,
                   mTabHosts!!,
                   "tab4",
                   R.string.chat,
                   R.drawable.ic_chat_tab,
                   chatUserListActivity,
                   color!!
               )*/

        setNewTab(
            this,
            mTabHosts!!,
            "tab5",
            R.string.book_marks,
            R.drawable.ic_bookmark_tab,
            bookmarkActivity,
            color!!
        )




    }

    private fun addTab(tabHost: TabHost, name: String, indicator: String, className: Class<*>) {
        val tabSpec = tabHost.newTabSpec(name)
        tabSpec.setIndicator(indicator)
        val intent = Intent(this, className)
        tabSpec.setContent(intent)
        tabHost.addTab(tabSpec)
    }

    fun setNewTab(
        context: Context,
        tabHost: TabHost,
        tag: String,
        title: Int,
        icon: Int,
        contentID: Intent,
        color: String
    ) {
        val tabSpec = tabHost.newTabSpec(tag)
        tabSpec.setIndicator(
            getTabIndicator(
                tabHost.context,
                title,
                icon,
                color
            )
        ) // new function to inject our own tab layout
        tabSpec.setContent(contentID)
        tabHost.addTab(tabSpec)

    }

    // Set Image and text
    fun getTabIndicator(context: Context, title: Int, icon: Int, color: String): View? {

        val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.poppins_regular)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        //or to support all versions use
        //or to support all versions use
        val view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null)
        val iv = view.findViewById<View>(R.id.imageView) as ImageView
        iv.setImageResource(icon)
        val tv = view.findViewById<View>(R.id.textView) as TextView
        tv.typeface = typeface

        tv.setTextColor(Color.parseColor(color))
        tv.setText(title)
        return view
    }

    /**
     * Set Text and image in tabs
     */
    fun showBottomTabs() {
        val color = resources.getString(0 + R.color.color_tab_text)
        val color1 = resources.getString(0 + R.color.dark_brownish_red)
        val icona = intArrayOf(
            R.drawable.ic_home_tab,
            R.drawable.ic_gym_tab,
            R.drawable.ic_discover_tab,
            // R.drawable.ic_chat_tab,
            R.drawable.ic_bookmark_tab

        )
        val widget = tabHost.tabWidget
        for (i in 0 until widget.childCount) {

            val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resources.getFont(R.font.poppins_bold)
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val typeface2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resources.getFont(R.font.poppins_medium)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val v = widget.getChildAt(i)

            // Look for the title view to ensure this is an indicator and not a divider.
            val tv = v.findViewById<View>(R.id.textView) as TextView
            val iv = v.findViewById<View>(R.id.imageView) as ImageView
            /*  val ivNotificationBadge = v.findViewById<View>(R.id.iv_notification_badge) as ImageView
              ivNotificationBadge.visibility = View.GONE
              if (tabHost.currentTab == 3 && tv.text.toString()
                      .equals(resources.getString(R.string.inbox), ignoreCase = true)
              ) {
                  if (localSharedPreferences.getSharedPreferencesInt(Constants.isUnreadCount) > 0) {
                      if (ivNotificationBadge.visibility == View.GONE) {
                          ivNotificationBadge.visibility = View.VISIBLE
                      }
                  } else {
                      ivNotificationBadge.visibility = View.GONE
                  }
              } else if (localSharedPreferences.getSharedPreferencesInt(Constants.isUnreadCount) > 0
                  && tv.text.toString().equals(resources.getString(R.string.inbox), ignoreCase = true)
              ) {
                  if (ivNotificationBadge.visibility == View.GONE) {
                      ivNotificationBadge.visibility = View.VISIBLE
                  }
              } else {
                  ivNotificationBadge.visibility = View.GONE
              }*/
            if (tv == null) {
                continue
            } else {
                if (i == mTabHosts!!.currentTab) {
                    println("tabHost.getCurrentTab() " + mTabHosts!!.currentTab)
                    tv.setTextColor(Color.parseColor(color1))
                    tv.typeface = typeface

                    iv.setImageResource(icona[i])
                    iv.setColorFilter(Color.parseColor(color1))
                } else {
                    tv.setTextColor(Color.parseColor(color))
                    tv.typeface = typeface2

                    iv.setImageResource(icona[i])
                    iv.setColorFilter(Color.parseColor(color))
                }
            }
        }
    }

    override fun onBackPressed() {

        if (mTabHosts!!.currentTab != 0) {
            mTabHosts!!.currentTab = 0

        } else {
            super.onBackPressed()

        }

    }

}