package com.zhuwenhao.flipped.movie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.trello.rxlifecycle3.android.FragmentEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseLazyFragment
import com.zhuwenhao.flipped.ext.dpToPx
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import kotlinx.android.synthetic.main.fragment_movie_subject_list.*

class RankingFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(flag: Int): RankingFragment {
            val fragment = RankingFragment()
            val bundle = Bundle()
            bundle.putInt("flag", flag)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var isFirst: Boolean = true

    private lateinit var adapter: RankingAdapter

    private lateinit var loadService: LoadService<Any>

    override fun provideLayoutId(): Int {
        return R.layout.fragment_movie_subject_list
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getRanking()
        }
        swipeRefreshLayout.isEnabled = false

        loadService = LoadSir.getDefault().register(recyclerView)

        adapter = RankingAdapter()
        adapter.setOnItemClickListener { adapter, _, position ->
            val subject = adapter.data[position] as Ranking.RankingSubject
            val intent = Intent(mContext, MovieDetailActivity::class.java)
            intent.putExtra("id", subject.subject.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(mContext)
        RecyclerViewDivider.with(mContext)
                .color(ContextCompat.getColor(mContext, R.color.divider))
                .inset(mContext.dpToPx(100F), 0)
                .size(mContext.dpToPx(1F))
                .hideLastDivider()
                .build().addTo(recyclerView)
        recyclerView.adapter = adapter
    }

    override fun initData() {
        getRanking()
    }

    private fun getRanking() {
        val api = RetrofitFactory.newInstance(Constants.DOU_BAN_MOVIE_API_URL).create(DouBanMovieApi::class.java)
        val observable = when (requireArguments().getInt("flag", 1)) {
            1 -> api.getWeekly()
            2 -> api.getUsBox()
            else -> api.getWeekly()
        }
        observable.compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : RxObserver<Ranking>() {
                    override fun onSuccess(t: Ranking) {
                        if (isFirst) {
                            isFirst = false
                            swipeRefreshLayout.isEnabled = true
                        }

                        swipeRefreshLayout.isRefreshing = false

                        adapter.setList(t.subjects)

                        if (t.subjects.isEmpty()) {
                            loadService.showCallback(EmptyCallback::class.java)
                        } else {
                            loadService.showSuccess()
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

    fun scrollToTop() {
        recyclerView.smoothScrollToPosition(0)
    }
}