package com.zhuwenhao.flipped.movie

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.trello.rxlifecycle3.android.FragmentEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseLazyFragment
import com.zhuwenhao.flipped.ext.dpToPx
import com.zhuwenhao.flipped.ext.getDefaultSp
import com.zhuwenhao.flipped.ext.getStringX
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.util.StringUtils
import com.zhuwenhao.flipped.view.CustomLoadMoreView
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import kotlinx.android.synthetic.main.fragment_movie_subject_list.*

class InTheatersFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): InTheatersFragment {
            return InTheatersFragment()
        }
    }

    private var currentPage: Int = 0
    private val pageSize: Int = 20
    var isFirst: Boolean = true

    lateinit var adapter: InTheatersAdapter

    private lateinit var loadService: LoadService<Any>

    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        getInTheaters(true)
    }

    override fun provideLayoutId(): Int {
        return R.layout.fragment_movie_subject_list
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener)
        swipeRefreshLayout.isEnabled = false

        loadService = LoadSir.getDefault().register(recyclerView)

        adapter = InTheatersAdapter()
        adapter.setOnItemClickListener { adapter, _, position ->
            val subject = adapter.data[position] as Subject
            val intent = Intent(mContext, MovieDetailActivity::class.java)
            intent.putExtra("id", subject.id)
            startActivity(intent)
        }
        adapter.setLoadMoreView(CustomLoadMoreView())
        adapter.setOnLoadMoreListener({
            getInTheaters(false)
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
        getInTheaters(true)
    }

    private fun getInTheaters(isRefresh: Boolean) {
        if (isRefresh) {
            adapter.setEnableLoadMore(false)
        } else {
            swipeRefreshLayout.isEnabled = false
        }

        RetrofitFactory.newInstance(Constants.DOU_BAN_MOVIE_API_URL).create(DouBanMovieApi::class.java)
                .getInTheaters(mContext.getDefaultSp().getStringX(Constants.SP_KEY_LAST_MOVIE_CITY, getString(R.string.shanghai)), if (isRefresh) 0 else (currentPage + 1) * pageSize, pageSize)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : RxObserver<Movie>() {
                    override fun onSuccess(t: Movie) {
                        for (subject in t.subjects) {
                            subject.mainlandDateTime = StringUtils.getMainlandDateTime(StringUtils.getMainlandDate(subject.pubDates))
                        }

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

    fun refresh() {
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            onRefreshListener.onRefresh()
        }
    }
}