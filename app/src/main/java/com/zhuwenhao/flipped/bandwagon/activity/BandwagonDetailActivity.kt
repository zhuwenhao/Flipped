package com.zhuwenhao.flipped.bandwagon.activity

import android.view.View
import android.widget.Toast
import com.trello.rxlifecycle3.android.ActivityEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.Bandwagon
import com.zhuwenhao.flipped.bandwagon.BandwagonApi
import com.zhuwenhao.flipped.bandwagon.entity.BandwagonInfo
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.db.ObjectBox
import com.zhuwenhao.flipped.ext.formatByte
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.util.StringUtils
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_bandwagon_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.joda.time.DateTime

class BandwagonDetailActivity : BaseSubActivity() {

    private lateinit var bandwagon: Bandwagon

    private lateinit var bBox: Box<Bandwagon>

    private var isFirst: Boolean = true

    override fun provideLayoutId(): Int {
        return R.layout.activity_bandwagon_detail
    }

    override fun initView() {
        bandwagon = intent.getSerializableExtra("bandwagon") as Bandwagon
        toolbar.title = bandwagon.title
        setSupportActionBar(toolbar)

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getBandwagonInfo()
        }
        swipeRefreshLayout.isEnabled = false

        initLoadSir(scrollView)
    }

    override fun initData() {
        bBox = ObjectBox.boxStore.boxFor(Bandwagon::class.java)

        getBandwagonInfo()
    }

    private fun getBandwagonInfo() {
        RetrofitFactory.newInstance(Constants.BANDWAGON_API_URL).create(BandwagonApi::class.java)
                .getBandwagonInfo(bandwagon.veId, bandwagon.apiKey)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : RxObserver<BandwagonInfo>() {
                    override fun onSuccess(t: BandwagonInfo) {
                        if (isFirst) {
                            isFirst = false
                            swipeRefreshLayout.isEnabled = true
                        }

                        swipeRefreshLayout.isRefreshing = false

                        if (t.error == 0) {
                            textHostname.text = t.hostname
                            textVmType.text = t.vmType.toUpperCase()
                            textNodeLocation.text = t.nodeLocation
                            textOs.text = StringUtils.firstLetterToUpper(t.os)
                            textIpAddresses.text = StringUtils.formatIpAddresses(t.ipAddresses)
                            textSshPort.text = t.sshPort
                            textStatus.text = StringUtils.firstLetterToUpper(t.status)

                            textCpuLoad.text = if (t.vmType == "kvm") getString(R.string.cpu_load_info_kvm, t.loadAverage) else getString(R.string.cpu_load_info_ovz, t.vzStatus.npRoc, t.vzStatus.loadAverage)

                            textUsedRam.text = t.usedRam.formatByte()
                            textTotalRam.text = t.totalRam.formatByte()
                            pbRam.progress = t.ramPercent

                            textUsedSwap.text = t.usedSwap.formatByte()
                            textTotalSwap.text = t.totalSwap.formatByte()
                            pbSwap.progress = t.swapPercent

                            textUsedDisk.text = t.usedDisk.formatByte()
                            textTotalDisk.text = t.totalDisk.formatByte()
                            pbDisk.progress = t.diskPercent

                            textUsedData.text = t.usedData.formatByte()
                            textTotalData.text = t.totalData.formatByte()
                            pbData.progress = t.dataPercent

                            textResets.text = getString(R.string.resets, DateTime(t.dataNextReset * 1000).toString("yyyy-MM-dd"))

                            textDataMultiplier.visibility = if (t.monthlyDataMultiplier == 1.0) View.GONE else View.VISIBLE
                            textDataMultiplier.text = getString(R.string.data_multiplier, t.monthlyDataMultiplier)

                            if (toolbar.menu.size() == 0) {
                                toolbar.inflateMenu(R.menu.menu_bandwagon_detail)
                            }

                            bandwagon.ipAddresses = textIpAddresses.text.toString()
                            bandwagon.nodeLocation = textNodeLocation.text.toString()

                            bBox.put(bandwagon)

                            loadService.showSuccess()
                        } else {
                            Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
                            loadService.showCallback(ErrorCallback::class.java)
                        }
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                        if (isFirst) {
                            isFirst = false
                            swipeRefreshLayout.isEnabled = true
                            loadService.showCallback(ErrorCallback::class.java)
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                })
    }
}