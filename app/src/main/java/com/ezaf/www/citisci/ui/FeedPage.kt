package com.ezaf.www.citisci.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezaf.www.citisci.R
import com.ezaf.www.citisci.ui.MainActivity.Companion.localDbHandler
import com.ezaf.www.citisci.utils.adapter.FeedPageAdapter
import com.ezaf.www.citisci.utils.viewmodel.FeedPageViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.VerboseLevel


open class FeedPage : Fragment() {

    open lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = FeedPage()
    }

    private lateinit var viewModel: FeedPageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.feed_page_fragment, container, false)
        setupRecycler(rootView)
        return rootView
    }

    @SuppressLint("CheckResult")
    open fun setupRecycler(rootView: View) {
        var fn = Throwable().stackTrace[0].methodName
        Observable.fromCallable {
            localDbHandler.experimentDao().getAllExp()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recyclerView = rootView.findViewById(R.id.feedPageRecyclerView)
                    recyclerView.run {
                        layoutManager = LinearLayoutManager(context)

                        //set click listener for item clicked in list
                        val itemOnClick: (Int,Experiment) -> Unit = { position, exp ->
//                            this.adapter!!.notifyDataSetChanged()
                            SharedDataHelper.focusedExp = exp
                            val nextAction = FeedPageDirections.nextAction()
                            Navigation.findNavController(rootView).navigate(nextAction)
                            Logger.log(VerboseLevel.INFO, "$fn: called.\n clicked item no. $position")

                        }

                        adapter = FeedPageAdapter(it, context, itemOnClick)
                        addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
                        runLayoutAnimation(this)
                }
        }
        //TODO: dispose

    }

    protected fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FeedPageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
