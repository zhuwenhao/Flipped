package com.zhuwenhao.flipped.bandwagon.activity

import android.widget.Toast
import com.trello.rxlifecycle2.android.ActivityEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.bandwagon.BandwagonApi
import com.zhuwenhao.flipped.bandwagon.entity.BandwagonInfo
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.util.StringUtils
import kotlinx.android.synthetic.main.activity_bandwagon_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.joda.time.DateTime

class BandwagonDetailActivity : BaseSubActivity() {

    override fun provideLayoutId(): Int {
        return R.layout.activity_bandwagon_detail
    }

    override fun initView() {
        setSupportActionBar(toolbar)
    }

    override fun initData() {
        RetrofitFactory.newInstance(Constants.BANDWAGON_API_URL).create(BandwagonApi::class.java)
                .getBandwagonInfo("", "")
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : RxObserver<BandwagonInfo>() {
                    override fun onSuccess(t: BandwagonInfo) {
                        if (t.error == 0) {
                            textHostname.text = t.hostname
                            textVmType.text = t.vmType.toUpperCase()
                            textNodeLocation.text = t.nodeLocation
                            textOs.text = StringUtils.firstLetterToUpper(t.os)
                            textIpAddresses.text = StringUtils.formatIpAddresses(t.ipAddresses)
                            textSshPort.text = t.sshPort
                            textStatus.text = StringUtils.firstLetterToUpper(t.status)

                            textCpuLoad.text = if (t.vmType == "kvm") getString(R.string.cpu_load_info_kvm, t.loadAverage) else getString(R.string.cpu_load_info_ovz, t.vzStatus.npRoc, t.vzStatus.loadAverage)

                            textUsedRam.text = StringUtils.formatByte(t.usedRam)
                            textTotalRam.text = StringUtils.formatByte(t.totalRam)
                            pbRam.progress = t.ramPercent

                            textUsedSwap.text = StringUtils.formatByte(t.usedSwap)
                            textTotalSwap.text = StringUtils.formatByte(t.totalSwap)
                            pbSwap.progress = t.swapPercent

                            textUsedDisk.text = StringUtils.formatByte(t.usedDisk)
                            textTotalDisk.text = StringUtils.formatByte(t.totalDisk)
                            pbDisk.progress = t.diskPercent

                            textUsedData.text = StringUtils.formatByte(t.usedData)
                            textTotalData.text = StringUtils.formatByte(t.totalData)
                            pbData.progress = t.dataPercent

                            textResets.text = getString(R.string.resets, DateTime(t.dataNextReset * 1000).toString("yyyy-MM-dd"))

                            if (toolbar.menu.size() == 0) {
                                toolbar.inflateMenu(R.menu.menu_bandwagon_detail)
                            }
                        }
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}