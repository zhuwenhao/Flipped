package com.zhuwenhao.flipped.movie.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.magiepooh.recycleritemdecoration.ItemDecorations
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.trello.rxlifecycle2.android.FragmentEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseLazyFragment
import com.zhuwenhao.flipped.ext.getDefaultSp
import com.zhuwenhao.flipped.ext.getStringX
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.movie.DouBanMovieApi
import com.zhuwenhao.flipped.movie.adapter.InTheatersAdapter
import com.zhuwenhao.flipped.movie.entity.Movie
import com.zhuwenhao.flipped.movie.entity.Subject
import com.zhuwenhao.flipped.util.StringUtils
import com.zhuwenhao.flipped.view.CustomLoadMoreView
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import kotlinx.android.synthetic.main.fragment_in_theaters.*

class InTheatersFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): InTheatersFragment {
            return InTheatersFragment()
        }
    }

    private var currentPage: Int = 0
    private val pageSize: Int = 20
    private var isFirst: Boolean = true

    private lateinit var adapter: InTheatersAdapter

    private lateinit var loadService: LoadService<Any>

    override fun provideLayoutId(): Int {
        return R.layout.fragment_in_theaters
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getInTheaters(true)
        }
        swipeRefreshLayout.isEnabled = false

        loadService = LoadSir.getDefault().register(recyclerView)

        adapter = InTheatersAdapter()
        adapter.setOnItemClickListener { adapter, _, position ->
            try {
                val subject = adapter.data[position] as Subject
                startActivity(Intent(Constants.DOU_BAN_ACTION, Uri.parse("${Constants.DOU_BAN_SUBJECT_URL}${subject.id}/?from=showing")))
            } catch (e: ActivityNotFoundException) {
            }
        }
        adapter.setLoadMoreView(CustomLoadMoreView())
        adapter.setOnLoadMoreListener({
            getInTheaters(false)
        }, recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerView.addItemDecoration(ItemDecorations.vertical(mContext).type(0, R.drawable.item_decoration_h_1).create())
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
}