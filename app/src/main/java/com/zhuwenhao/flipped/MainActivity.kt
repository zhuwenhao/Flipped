package com.zhuwenhao.flipped

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.zhuwenhao.flipped.bandwagon.activity.BandwagonActivity
import com.zhuwenhao.flipped.base.BaseActivity
import com.zhuwenhao.flipped.ext.getDefaultSp
import com.zhuwenhao.flipped.ext.getStringX
import com.zhuwenhao.flipped.ext.putString
import com.zhuwenhao.flipped.movie.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movie_subject_list.*

class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener {

    companion object {
        private const val DRAWER_BANDWAGON = 1L
    }

    private lateinit var drawer: Drawer
    private lateinit var menuCity: MenuItem

    private lateinit var inTheatersFragment: InTheatersFragment

    override fun provideLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        drawer = DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(false)
                .withHeader(R.layout.layout_drawer_header)
                .withHeaderDivider(false)
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_BANDWAGON)
                                .withName(R.string.bandwagon)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_bandwagon))
                                .withIconTintingEnabled(true)
                                .withSelectable(false)
                )
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(this)
                .build()

        val titles = ArrayList<String>()
        titles.add(getString(R.string.movie_in_theaters))
        titles.add(getString(R.string.movie_coming_soon))
        titles.add(getString(R.string.movie_top250))
        titles.add(getString(R.string.movie_weekly))
        titles.add(getString(R.string.movie_us_box))
        for (title in titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title))
        }

        inTheatersFragment = InTheatersFragment.newInstance()
        val comingSoonFragment = ComingSoonFragment.newInstance()
        val top250Fragment = Top250Fragment.newInstance()
        val weeklyFragment = RankingFragment.newInstance(1)
        val usBoxFragment = RankingFragment.newInstance(2)

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(inTheatersFragment)
        fragmentList.add(comingSoonFragment)
        fragmentList.add(top250Fragment)
        fragmentList.add(weeklyFragment)
        fragmentList.add(usBoxFragment)

        val pagerAdapter = MoviePagerAdapter(supportFragmentManager, fragmentList, titles)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = fragmentList.size
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                menuCity.isVisible = tab.position == 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> inTheatersFragment.scrollToTop()
                    1 -> comingSoonFragment.scrollToTop()
                    2 -> top250Fragment.scrollToTop()
                    3 -> weeklyFragment.scrollToTop()
                    4 -> usBoxFragment.scrollToTop()
                }
            }
        })

        createDynamicShortcuts()
    }

    override fun initData() {

    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
        when (drawerItem.identifier) {
            DRAWER_BANDWAGON -> startActivity(Intent(this, BandwagonActivity::class.java))
        }
        Handler().post { drawer.closeDrawer() }
        return false
    }

    private fun createDynamicShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1)
            return

        val shortcutManager = getSystemService(ShortcutManager::class.java)

        val bandwagon = ShortcutInfo.Builder(this, "bandwagon")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_bandwagon_shortcut))
                .setDisabledMessage(getString(R.string.bandwagon))
                .setLongLabel(getString(R.string.bandwagon))
                .setShortLabel(getString(R.string.bandwagon))
                .setIntent(Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, BandwagonActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                .build()
        shortcutManager.dynamicShortcuts = arrayListOf(bandwagon)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuCity = menu.findItem(R.id.menu_city)
        menuCity.title = getDefaultSp().getString(Constants.SP_KEY_LAST_MOVIE_CITY, menuCity.title.toString())
        for (i in 0 until menuCity.subMenu.size()) {
            if (menuCity.title == menuCity.subMenu.getItem(i).title) {
                menuCity.subMenu.getItem(i).isChecked = true
                break
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_beijing, R.id.menu_shanghai, R.id.menu_guangzhou, R.id.menu_shenzhen,
            R.id.menu_shaoyang -> {
                if (inTheatersFragment.isFirst
                        || (inTheatersFragment.adapter.data.isNotEmpty() && item.title == getDefaultSp().getStringX(Constants.SP_KEY_LAST_MOVIE_CITY))
                        || inTheatersFragment.swipeRefreshLayout.isRefreshing) {
                    return true
                }

                item.isChecked = true
                menuCity.title = item.title
                getDefaultSp().putString(Constants.SP_KEY_LAST_MOVIE_CITY, item.title.toString())
                inTheatersFragment.refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen)
            drawer.closeDrawer()
        else
            super.onBackPressed()
    }
}