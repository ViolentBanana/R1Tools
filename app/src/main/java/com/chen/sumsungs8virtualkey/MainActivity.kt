package com.chen.sumsungs8virtualkey

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.chen.sumsungs8virtualkey.base.BaseFragment
import com.chen.sumsungs8virtualkey.gesture.GestureFragment
import com.chen.sumsungs8virtualkey.introduce.IntroduceFragment
import com.chen.sumsungs8virtualkey.service.VirtualKeyService
import com.chen.sumsungs8virtualkey.test.TestFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.navigation_content.*
import java.util.*
import android.view.SubMenu


const val REQUEST_OVERLAY = 100


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBar()

        initActionBar()

        initNavigationView()

        initTabView()

    }

    private fun initNavigationView() {
        if (nv_main_navigation != null) {
            setupDrawerContent(nv_main_navigation)

            val menu = nv_main_navigation.menu

            val topChannelMenu = menu.addSubMenu("关于")
            topChannelMenu.add("版本：v" + BuildConfig.VERSION_NAME)
            topChannelMenu.add("时间：" + BuildConfig.BUILD_TIME)
        }
    }

    private fun initActionBar() {
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)

    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            dl_main_drawer.closeDrawers()
            true
        }
    }


    private fun initToolBar() {
        setSupportActionBar(toolbar)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_overaction, menu)
//        getMenuInflater().inflate(R.menu.drawer_view, menu);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dl_main_drawer.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal var mFrags: MutableList<BaseFragment> = ArrayList()


    private fun initTabView() {

        mFrags.add(GestureFragment.newInstance())
        mFrags.add(TestFragment.newInstance())
        mFrags.add(IntroduceFragment.newInstance())


        vp_content.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                return mFrags.size

            }

            override fun getItem(position: Int): Fragment {
                return mFrags[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return getString(mFrags[position].getTitleText())
            }

        }

        vp_content.offscreenPageLimit = mFrags.size
        tl_tabs.setupWithViewPager(vp_content)


        tl_tabs.setSelectedTabIndicatorColor(resources.getColor(R.color.tabIndicatorColor))
        tl_tabs.setPadding(0, 0, 0, 5)
//        tl_tabs.setTabsFromPagerAdapter(adapter)
        tl_tabs.setTabTextColors(resources.getColor(R.color.tabTextColorUnselect), resources.getColor(R.color.tabTextColorSelect))

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    if (VirtualKeyService.isRunning)
//                        VirtualKeyService.service!!.createView()
                    else
                        Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                            .LENGTH_SHORT).show()
                }
            }
        }
    }
}
