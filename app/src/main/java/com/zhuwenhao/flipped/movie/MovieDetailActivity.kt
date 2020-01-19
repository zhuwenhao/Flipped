package com.zhuwenhao.flipped.movie

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.Transport
import com.trello.rxlifecycle3.android.ActivityEvent
import com.zhuwenhao.flipped.Constants
import com.zhuwenhao.flipped.GlideApp
import com.zhuwenhao.flipped.R
import com.zhuwenhao.flipped.base.BaseSubActivity
import com.zhuwenhao.flipped.ext.containsElseFirst
import com.zhuwenhao.flipped.ext.dpToPx
import com.zhuwenhao.flipped.ext.joinNonEmpty
import com.zhuwenhao.flipped.ext.makeCubicGradientScrimDrawable
import com.zhuwenhao.flipped.http.RetrofitFactory
import com.zhuwenhao.flipped.http.RxObserver
import com.zhuwenhao.flipped.http.RxSchedulers
import com.zhuwenhao.flipped.view.callback.ErrorCallback
import com.zhuwenhao.flipped.view.callback.LoadingCallback
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlin.math.roundToInt

class MovieDetailActivity : BaseSubActivity() {

    private lateinit var commentAdapter: CommentAdapter

    override fun provideLayoutId(): Int {
        return R.layout.activity_movie_detail
    }

    override fun initView() {
        setSupportActionBar(toolbar)

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            supportActionBar?.setDisplayShowTitleEnabled(verticalOffset < 0 && appBarLayout.totalScrollRange + verticalOffset <= toolbar.height * 1.1)
        })
        appBarLayout.post {
            collapsingToolbarLayout.scrimVisibleHeightTrigger = (toolbar.bottom * 1.1).toInt()
        }
        viewScrim.background = makeCubicGradientScrimDrawable(Gravity.TOP, 0x66)

        initLoadSir(scrollView, Callback.OnReloadListener {
            loadService.showCallback(LoadingCallback::class.java)
            getMovieDetail()
        })
        val transport = Transport { _, view ->
            val layout = view as LinearLayout
            layout.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            layout.setPadding(0, dpToPx(68F), 0, 0)
        }
        loadService.setCallBack(LoadingCallback::class.java, transport)
        loadService.setCallBack(ErrorCallback::class.java, transport)

        commentAdapter = CommentAdapter()

        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = commentAdapter
    }

    override fun initData() {
        getMovieDetail()
    }

    private fun getMovieDetail() {
        RetrofitFactory.newInstance(Constants.DOU_BAN_MOVIE_API_URL).create(DouBanMovieApi::class.java)
                .getMovieDetail(intent.getStringExtra("id")!!)
                .compose(RxSchedulers.io2Main())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : RxObserver<MovieDetail>() {
                    override fun onSuccess(t: MovieDetail) {
                        supportActionBar?.title = t.title

                        if (t.trailers.isNotEmpty()) {
                            GlideApp.with(mContext).load(t.trailers.first().medium).into(imgBackdrop)
                            imgTrailerPlay.visibility = View.VISIBLE
                        } else if (t.photos.isNotEmpty()) {
                            GlideApp.with(mContext).load(t.photos.first().image).into(imgBackdrop)
                        }

                        GlideApp.with(mContext).load(t.images.large).into(imgCover)
                        textTitle.text = t.title
                        textOriginalTitleYear.text = String.format("%s(%s)", if (t.title == t.originalTitle) "" else "${t.originalTitle} ", t.year)
                        textCountriesGenresDurations.text = joinNonEmpty(" / ",
                                t.countries.joinToString(separator = " "),
                                t.genres.joinToString(separator = " "),
                                t.durations.joinToString(separator = " "))
                        textMainlandPubDate.text = t.pubDates.containsElseFirst(getString(R.string.mainland_china))

                        if (t.rating.average == 0F) {
                            layoutRating.visibility = View.GONE
                        } else {
                            layoutRating.visibility = View.VISIBLE

                            textRating.text = t.rating.average.toString()
                            ratingBar.rating = t.rating.average.roundToInt().toFloat() / 2
                            textRatingCount.text = t.ratingsCount.toString()

                            val maxRating = arrayListOf(t.rating.details.v1, t.rating.details.v2, t.rating.details.v3, t.rating.details.v4, t.rating.details.v5).max()
                            if (maxRating != null) {
                                var percentage = (t.rating.details.v5 / maxRating.toFloat() * 100).toInt()
                                pbRating5.progress = if (percentage == 0) ++percentage else percentage
                                percentage = (t.rating.details.v4 / maxRating.toFloat() * 100).toInt()
                                pbRating4.progress = if (percentage == 0) ++percentage else percentage
                                percentage = (t.rating.details.v3 / maxRating.toFloat() * 100).toInt()
                                pbRating3.progress = if (percentage == 0) ++percentage else percentage
                                percentage = (t.rating.details.v2 / maxRating.toFloat() * 100).toInt()
                                pbRating2.progress = if (percentage == 0) ++percentage else percentage
                                percentage = (t.rating.details.v1 / maxRating.toFloat() * 100).toInt()
                                pbRating1.progress = if (percentage == 0) ++percentage else percentage
                            }
                        }

                        textSummary.text = t.summary
                        layoutSummary.setOnClickListener {
                            val intent = Intent(mContext, MovieIntroductionActivity::class.java)
                            intent.putExtra("movie", t)
                            startActivity(intent)
                        }

                        commentAdapter.setNewData(t.popularComments)
                        layoutComment.visibility = if (t.popularComments.isEmpty()) View.GONE else View.VISIBLE

                        loadService.showSuccess()
                    }

                    override fun onFailure(e: Exception) {
                        Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                        loadService.showCallback(ErrorCallback::class.java)
                    }
                })
    }
}