package com.zhuwenhao.flipped

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.support.v7.content.res.AppCompatResources
import android.view.View
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.zhuwenhao.flipped.bandwagon.activity.BandwagonActivity
import com.zhuwenhao.flipped.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener {

    companion object {
        private const val DRAWER_BANDWAGON = 1L
    }

    private lateinit var drawer: Drawer

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
        titles.add(getString(R.string.in_theaters))
        titles.add(getString(R.string.movie_coming_soon))
        for (title in titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title))
        }

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
        shortcutManager.dynamicShortcuts = Arrays.asList(bandwagon)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen)
            drawer.closeDrawer()
        else
            super.onBackPressed()
    }
}