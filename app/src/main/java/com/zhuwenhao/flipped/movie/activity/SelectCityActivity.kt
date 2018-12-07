package com.zhuwenhao.flipped.movie.activity

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration
import com.trello.rxlifecycle2.android.ActivityEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.extension.getDefaultSp
import com.zhuwenhao.flipped.extension.putString
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.movie.adapter.SelectCityAdapter
import com.zhuwenhao.flipped.movie.entity.City
import com.zhuwenhao.flipped.movie.entity.SelectCity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_select_city.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.io.BufferedReader

class SelectCityActivity : BaseSubActivity() {

    private val cityList: MutableList<City> = ArrayList()
    private lateinit var adapter: SelectCityAdapter

    override fun provideLayoutId(): Int {
        return R.layout.activity_select_city
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        adapter = SelectCityAdapter(cityList)
        adapter.setOnItemClickListener { _, _, position ->
            getDefaultSp().putString(Constants.SP_KEY_LAST_MOVIE_CITY, cityList[position].name)
            setResult(Activity.RESULT_OK)
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerView.addItemDecoration(PinnedHeaderItemDecoration.Builder(City.TYPE_HEADER)
                .disableHeaderClick(true)
                .enableDivider(true)
                .setDividerId(R.drawable.item_decoration_h_1)
                .create())
        recyclerView.adapter = adapter
    }

    override fun initData() {
        getCityList()
    }

    private fun getCityList() {
        Observable.create<Int> { emitter ->
            val json = assets.open("city.json").bufferedReader().use(BufferedReader::readText)

            val cities = Gson().fromJson<List<SelectCity>>(json, object : TypeToken<List<SelectCity>>() {}.type)
            val lastMovieCity = getDefaultSp().getString(Constants.SP_KEY_LAST_MOVIE_CITY, getString(R.string.default_movie_city))

            cityList.clear()
            for (c in cities) {
                cityList.add(City(City.TYPE_HEADER, c.alphabet, "", false))
                for (city in c.cities) {
                    cityList.add(City(City.TYPE_DATA, "", city.name, lastMovieCity == city.name))
                }
            }

            emitter.onNext(1)
            emitter.onComplete()
        }.compose(RxSchedulers.io2Main()).compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Int) {
                        adapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }
}