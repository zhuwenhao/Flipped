package com.zhuwenhao.flipped.movie.fragment

import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration
import com.trello.rxlifecycle2.android.FragmentEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseLazyFragment
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.movie.DouBanMovieApi
import com.zhuwenhao.flipped.movie.adapter.ComingSoonAdapter
import com.zhuwenhao.flipped.movie.entity.Movie
import com.zhuwenhao.flipped.movie.entity.Subject
import com.zhuwenhao.flipped.view.CustomLoadMoreView
import kotlinx.android.synthetic.main.fragment_coming_soon.*

class ComingSoonFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): ComingSoonFragment {
            return ComingSoonFragment()
        }
    }

    private var currentPage: Int = 0
    private val pageSize: Int = 20

    private lateinit var adapter: ComingSoonAdapter
    private val comingSoonList: List<Subject> = ArrayList()

    override fun provideLayoutId(): Int {
        return R.layout.fragment_coming_soon
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getComingSoon(true)
        }

        adapter = ComingSoonAdapter(comingSoonList)
        adapter.setLoadMoreView(CustomLoadMoreView())
        adapter.setOnLoadMoreListener({
            getComingSoon(false)
        }, recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerView.addItemDecoration(PinnedHeaderItemDecoration.Builder(Subject.TYPE_HEADER)
                .disableHeaderClick(false)
                .enableDivider(true)
                .setDividerId(R.drawable.item_decoration_h_1)
                .create())
        recyclerView.adapter = adapter
    }

    override fun initData() {
        getComingSoon(true)
    }

    private fun getComingSoon(isRefresh: Boolean) {
        if (isRefresh) {
            adapter.setEnableLoadMore(false)
        } else {
            swipeRefreshLayout.isEnabled = false
        }

        RetrofitFactory.newInstance(Constants.DOU_BAN_MOVIE_API_URL).create(DouBanMovieApi::class.java)
                .getComingSoon("上海", if (isRefresh) 0 else (currentPage + 1) * pageSize, pageSize)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : RxObserver<Movie>() {
                    override fun onSuccess(t: Movie) {
                        for (subject in t.subjects) {
                            subject.type = 2
                        }

                        if (isRefresh) {
                            currentPage = 0

                            swipeRefreshLayout.isRefreshing = false

                            adapter.setNewData(t.subjects)
                            adapter.setEnableLoadMore(true)
                        } else {
                            currentPage++

                            swipeRefreshLayout.isEnabled = true

                            adapter.addData(t.subjects)

                            if ((currentPage + 1) * pageSize >= t.total) {
                                adapter.loadMoreEnd()
                            } else {
                                adapter.loadMoreComplete()
                            }
                        }
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                        if (isRefresh) {
                            swipeRefreshLayout.isRefreshing = false
                            adapter.setEnableLoadMore(true)
                        } else {
                            swipeRefreshLayout.isEnabled = true
                            adapter.loadMoreFail()
                        }
                    }
                })
    }
}