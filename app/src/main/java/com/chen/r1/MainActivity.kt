package com.chen.r1

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chen.r1.base.BaseFragment
import com.chen.r1.gesture.GestureFragment
import com.chen.r1.gesture.GestureWindowHelper
import com.chen.r1.introduce.IntroduceFragment
import com.chen.r1.test.TestFragment
import com.chen.r1.utils.CUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.dl_main_drawer
import kotlinx.android.synthetic.main.activity_main.nv_main_navigation
import kotlinx.android.synthetic.main.navigation_content.tl_tabs
import kotlinx.android.synthetic.main.navigation_content.toolbar
import kotlinx.android.synthetic.main.navigation_content.vp_content


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
        for (i in 0 until mFrags.size) {
            tl_tabs.getTabAt(i)!!.
                    setCustomView(
                            getTabView(i))
        }
        tl_tabs.setSelectedTabIndicatorColor(resources.getColor(R.color.tabIndicatorColor))
        tl_tabs.setPadding(0, 0, 0, 5)
//        tl_tabs.setTabsFromPagerAdapter(adapter)
        tl_tabs.setTabTextColors(resources.getColor(R.color.tabTextColorSelect), resources.getColor(R.color.tabTextColorSelect))

        tl_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                changeTabNormal(tab)   //Tab失去焦点
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTabSelect(tab)   //Tab获取焦点
            }
        })
    }

    private fun getTabView(index: Int): LinearLayout {
        //自定义View布局
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_rank_tab, null) as LinearLayout
        val title = view.findViewById(R.id.tab_title) as TextView
//        val iv = view.findViewById(R.id.iv) as ImageView
        title.setText(getString(mFrags[index].getTitleText()))
        if (index != 0) {
            view.setAlpha(0.5f)
            title.setTextColor(resources.getColor(R.color.tabTextColorUnselect))

//            if (index == 0) {
//                iv.setImageResource(R.drawable.rank_01)
//            } else {
//                iv.setImageResource(R.drawable.rank_03)
//            }
        } else {
//            iv.setImageResource(R.drawable.rank_02)
            view.setScaleX(1.1f)
            view.setScaleY(1.1f)
            title.setTextColor(resources.getColor(R.color.tabTextColorSelect))
        }
        return view
    }

    /**
     * 改变TabLayout的View到选中状态
     * 使用属性动画改编Tab中View的状态
     */
    private fun changeTabSelect(tab: TabLayout.Tab?) {

        if (tab != null) {
            val view = tab.customView as LinearLayout
            val text = view.getChildAt(0) as TextView
            text.setTextColor(resources.getColor(R.color.tabTextColorSelect))
            val anim = ObjectAnimator
                    .ofFloat(view as View, "scaleX", 1.0f, 1.1f)
                    .setDuration(200)
            anim.start()
            anim.addUpdateListener { animation ->
                val cVal = animation.animatedValue as Float
                view!!.alpha = 0.5f + (cVal - 1f) * (0.5f / 0.1f)
                view.scaleX = cVal
                view.scaleY = cVal
            }
        }
    }

    /**
     * 改变TabLayout的View到未选中状态
     */
    private fun changeTabNormal(tab: TabLayout.Tab?) {
        if (tab != null) {
//            val view = tab.customView
            val view = tab.customView as LinearLayout
            val text = view.getChildAt(0) as TextView
            text.setTextColor(resources.getColor(R.color.tabTextColorSelect))
            val anim = ObjectAnimator
                    .ofFloat(view, "scaleX", 1.0f, 0.9f)
                    .setDuration(200)
            anim.start()
            anim.addUpdateListener { animation ->
                val cVal = animation.animatedValue as Float
                view!!.alpha = 1f - (1f - cVal) * (0.5f / 0.1f)
                view.scaleX = cVal
                view.scaleY = cVal
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    if (CUtils.isServiceRunning(this))
                        GestureWindowHelper.createView()
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
