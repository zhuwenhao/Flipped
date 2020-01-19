package com.zhuwenhao.flipped.movie

import android.content.Intent
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
import com.zhuwenhao.flipped.view.CustomLoadMoreView
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import kotlinx.android.synthetic.main.fragment_movie_subject_list.*

class Top250Fragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): Top250Fragment {
            return Top250Fragment()
        }
    }

    private var currentPage: Int = 0
    private val pageSize: Int = 20
    private var isFirst: Boolean = true

    private lateinit var adapter: Top250Adapter

    private lateinit var loadService: LoadService<Any>

    override fun provideLayoutId(): Int {
        return R.layout.fragment_movie_subject_list
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getTop(true)
        }
        swipeRefreshLayout.isEnabled = false

        loadService = LoadSir.getDefault().register(recyclerView)

        adapter = Top250Adapter()
        adapter.setOnItemClickListener { adapter, _, position ->
            val subject = adapter.data[position] as Subject
            val intent = Intent(mContext, MovieDetailActivity::class.java)
            intent.putExtra("id", subject.id)
            startActivity(intent)
        }
        adapter.setLoadMoreView(CustomLoadMoreView())
        adapter.setOnLoadMoreListener({
            getTop(false)
        }, recyclerView)

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
        getTop(true)
    }

    private fun getTop(isRefresh: Boolean) {
        if (isRefresh) {
            adapter.setEnableLoadMore(false)
        } else {
            swipeRefreshLayout.isEnabled = false
        }

        RetrofitFactory.newInstance(Constants.DOU_BAN_MOVIE_API_URL).create(DouBanMovieApi::class.java)
                .getTop250(if (isRefresh) 0 else (currentPage + 1) * pageSize, pageSize)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : RxObserver<Movie>() {
                    override fun onSuccess(t: Movie) {
                        if (isRefresh) {
                            if (isFirst) {
                                isFirst = false
                                swipeRefreshLayout.isEnabled = true
                            }

                            currentPage = 0

                            swipeRefreshLayout.isRefreshing = false

                            adapter.setNewData(t.subjects)
                            if (adapter.itemCount < pageSize)
                                adapter.loadMoreEnd()
                            else
                                adapter.setEnableLoadMore(true)

                            if (t.subjects.isEmpty()) {
                                loadService.showCallback(EmptyCallback::class.java)
                            } else {
                                loadService.showSuccess()
                            }
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
                            if (isFirst) {
                                isFirst = false
                                swipeRefreshLayout.isEnabled = true
                                loadService.showCallback(ErrorCallback::class.java)
                            }
                            swipeRefreshLayout.isRefreshing = false
                            adapter.setEnableLoadMore(true)
                        } else {
                            swipeRefreshLayout.isEnabled = true
                            adapter.loadMoreFail()
                        }
                    }
                })
    }

    fun scrollToTop() {
        recyclerView.smoothScrollToPosition(0)
    }
}