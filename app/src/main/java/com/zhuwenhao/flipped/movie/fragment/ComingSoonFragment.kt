package com.zhuwenhao.flipped.movie.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration
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
import com.zhuwenhao.flipped.movie.adapter.ComingSoonAdapter
import com.zhuwenhao.flipped.movie.entity.Movie
import com.zhuwenhao.flipped.movie.entity.Subject
import com.zhuwenhao.flipped.util.StringUtils
import com.zhuwenhao.flipped.view.CustomLoadMoreView
import com.zhuwenhao.flipped.view.callback.EmptyCallback
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_coming_soon.*

class ComingSoonFragment : BaseLazyFragment() {

    companion object {
        fun newInstance(): ComingSoonFragment {
            return ComingSoonFragment()
        }
    }

    private var currentPage: Int = 0
    private val pageSize: Int = 20
    private var isFirst: Boolean = true

    private lateinit var adapter: ComingSoonAdapter

    private lateinit var loadService: LoadService<Any>

    override fun provideLayoutId(): Int {
        return R.layout.fragment_coming_soon
    }

    override fun initView() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener {
            getComingSoon(true)
        }
        swipeRefreshLayout.isEnabled = false

        loadService = LoadSir.getDefault().register(recyclerView)

        adapter = ComingSoonAdapter(ArrayList())
        adapter.setOnItemClickListener { adapter, _, position ->
            try {
                val subject = adapter.data[position] as Subject
                startActivity(Intent(Constants.DOU_BAN_ACTION, Uri.parse("${Constants.DOU_BAN_SUBJECT_URL}${subject.id}/?from=showing")))
            } catch (e: ActivityNotFoundException) {
            }
        }
        adapter.setLoadMoreView(CustomLoadMoreView())
        adapter.setOnLoadMoreListener({
            getComingSoon(false)
        }, recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerView.addItemDecoration(PinnedHeaderItemDecoration.Builder(Subject.TYPE_HEADER)
                .disableHeaderClick(true)
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
                .getComingSoon(mContext.getDefaultSp().getStringX(Constants.SP_KEY_LAST_MOVIE_CITY, getString(R.string.shanghai)), if (isRefresh) 0 else (currentPage + 1) * pageSize, pageSize)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : RxObserver<Movie>() {
                    override fun onSuccess(t: Movie) {
                        formatComingSoon(isRefresh, t)
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

    private fun formatComingSoon(isRefresh: Boolean, movie: Movie) {
        Observable.create(ObservableOnSubscribe<List<Subject>> { emitter ->
            for (subject in movie.subjects) {
                subject.mainlandDate = StringUtils.getMainlandDate(subject.pubDates)
                subject.mainlandDateTime = StringUtils.getMainlandDateTime(subject.mainlandDate)
                subject.mainlandDateForHeader = StringUtils.getMainlandDateForHeader(subject.mainlandDate, subject.mainlandDateTime)
            }

            val subjectList: MutableList<Subject> = ArrayList()

            if (isRefresh) {
                for ((index, subject) in movie.subjects.withIndex()) {
                    if (index == 0 || movie.subjects[index - 1].mainlandDate != subject.mainlandDate) {
                        subjectList.add(Subject(Subject.TYPE_HEADER, subject.mainlandDate, subject.mainlandDateTime, subject.mainlandDateForHeader, subject))
                    }
                    subjectList.add(Subject(Subject.TYPE_DATA, subject.mainlandDate, subject.mainlandDateTime, subject.mainlandDateForHeader, subject))
                }
            } else {
                for ((index, subject) in movie.subjects.withIndex()) {
                    if ((index == 0 && adapter.data[adapter.data.size - 1].mainlandDate != subject.mainlandDate) ||
                            (index != 0 && movie.subjects[index - 1].mainlandDate != subject.mainlandDate)) {
                        subjectList.add(Subject(Subject.TYPE_HEADER, subject.mainlandDate, subject.mainlandDateTime, subject.mainlandDateForHeader, subject))
                    }
                    subjectList.add(Subject(Subject.TYPE_DATA, subject.mainlandDate, subject.mainlandDateTime, subject.mainlandDateForHeader, subject))
                }
            }

            emitter.onNext(subjectList)
            emitter.onComplete()
        }).compose(RxSchedulers.io2Main()).compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(object : Observer<List<Subject>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(subjectList: List<Subject>) {
                        if (isRefresh) {
                            if (isFirst) {
                                isFirst = false
                                swipeRefreshLayout.isEnabled = true
                            }

                            currentPage = 0

                            swipeRefreshLayout.isRefreshing = false

                            adapter.setNewData(subjectList)
                            if (adapter.itemCount < pageSize)
                                adapter.loadMoreEnd()
                            else
                                adapter.setEnableLoadMore(true)

                            if (subjectList.isEmpty()) {
                                loadService.showCallback(EmptyCallback::class.java)
                            } else {
                                loadService.showSuccess()
                            }
                        } else {
                            currentPage++

                            swipeRefreshLayout.isEnabled = true

                            adapter.addData(subjectList)

                            if ((currentPage + 1) * pageSize >= movie.total) {
                                adapter.loadMoreEnd()
                            } else {
                                adapter.loadMoreComplete()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
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