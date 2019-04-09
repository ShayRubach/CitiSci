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
import com.ezaf.www.citisci.utils.adapter.FeedPageAdapter
import com.ezaf.www.citisci.utils.viewmodel.FeedPageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.animation.AnimationUtils
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.ezaf.www.citisci.data.exp.Experiment
import com.ezaf.www.citisci.data.exp.SharedDataHelper
import com.ezaf.www.citisci.utils.Logger
import com.ezaf.www.citisci.utils.ParserUtil
import com.ezaf.www.citisci.utils.VerboseLevel
import com.ezaf.www.citisci.utils.db.RemoteDbHandler
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class FeedPage : Fragment() {

    open lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: FeedPageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.feed_page_fragment, container, false)
        setupRecycler(rootView, R.id.feedPageRecyclerView)
        return rootView
    }

    @SuppressLint("CheckResult")
    open fun setupRecycler(rootView: View, recyclerId: Int) {
        var fn = Throwable().stackTrace[0].methodName

        if(SharedDataHelper.listOfAllExp.isEmpty()) {
            RemoteDbHandler.getAllExp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it.enqueue(object : Callback<JsonElement> {
                            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                                ParserUtil.jsonToExpList(response.body().toString(), SharedDataHelper.listOfAllExp)
                                Logger.log(VerboseLevel.INFO, "$fn: got experiments.")
                                setupRecyclerProperties(rootView, SharedDataHelper.listOfAllExp, recyclerId)
                            }

                            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                                Logger.log(VerboseLevel.INFO, "$fn: failed to get experiments.")
                            }
                        })
                    }
        }
        else{
            setupRecyclerProperties(rootView, SharedDataHelper.listOfAllExp, recyclerId)
        }
    }

    open fun setupRecyclerProperties(rootView: View, expList: List<Experiment>, recyclerId: Int){
        var fn = Throwable().stackTrace[0].methodName

        recyclerView = rootView.findViewById(recyclerId)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)

            //set click listener for item clicked in list
            val itemOnClick: (Int, Experiment, NavDirections) -> Unit = { position, exp, nextAction ->
                //                            this.adapter!!.notifyDataSetChanged()
                SharedDataHelper.focusedExp = exp
                Navigation.findNavController(rootView).navigate(nextAction)
                Logger.log(VerboseLevel.INFO, "$fn: called.\n clicked item no. $position , exp=$exp")

            }

            attachAdapter(recyclerView, expList, itemOnClick)
            addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
            runLayoutAnimation(this)
        }
    }

    open fun attachAdapter(recycler: RecyclerView, expList: List<Experiment>, itemOnClick: (Int, Experiment, NavDirections) -> Unit) {
        recycler.adapter = FeedPageAdapter(expList, recycler.context, itemOnClick)
    }


    private fun runLayoutAnimation(recyclerView: RecyclerView) {
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
