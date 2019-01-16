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
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.zhuwenhao.flipped.bandwagon.activity.BandwagonActivity
import com.zhuwenhao.flipped.base.BaseActivity
import com.zhuwenhao.flipped.ext.getDefaultSp
import com.zhuwenhao.flipped.ext.putString
import com.zhuwenhao.flipped.movie.adapter.MoviePagerAdapter
import com.zhuwenhao.flipped.movie.fragment.ComingSoonFragment
import com.zhuwenhao.flipped.movie.fragment.InTheatersFragment
import com.zhuwenhao.flipped.rss.activity.RssActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener {

    companion object {
        private const val DRAWER_BANDWAGON = 1L
        private const val DRAWER_RSS = 2L
    }

    private lateinit var drawer: Drawer
    private lateinit var menuCity: MenuItem

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
                .addDrawerItems(
                        PrimaryDrawerItem()
                                .withIdentifier(DRAWER_RSS)
                                .withName(R.string.rss)
                                .withIcon(AppCompatResources.getDrawable(this, R.drawable.ic_rss))
                                .withIconTintingEnabled(true)
                                .withSelectable(false)
                )
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(this)
                .build()

        val titles = ArrayList<String>()
        titles.add(getString(R.string.movie_in_theaters))
        titles.add(getString(R.string.movie_coming_soon))
        for (title in titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title))
        }

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(InTheatersFragment.newInstance())
        fragmentList.add(ComingSoonFragment.newInstance())

        val pagerAdapter = MoviePagerAdapter(supportFragmentManager, fragmentList, titles)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        createDynamicShortcuts()
    }

    override fun initData() {

    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
        when (drawerItem.identifier) {
            DRAWER_BANDWAGON -> startActivity(Intent(this, BandwagonActivity::class.java))
            DRAWER_RSS -> startActivity(Intent(this, RssActivity::class.java))
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
        val rss = ShortcutInfo.Builder(this, "rss")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_rss_shortcut))
                .setDisabledMessage(getString(R.string.rss))
                .setLongLabel(getString(R.string.rss))
                .setShortLabel(getString(R.string.rss))
                .setIntent(Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, RssActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                .build()
        shortcutManager.dynamicShortcuts = arrayListOf(bandwagon, rss)
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
                item.isChecked = true
                menuCity.title = item.title
                getDefaultSp().putString(Constants.SP_KEY_LAST_MOVIE_CITY, item.title.toString())
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