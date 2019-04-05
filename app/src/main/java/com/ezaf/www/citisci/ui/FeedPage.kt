package com.ezaf.www.citisci.ui

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
import com.ezaf.www.citisci.utils.ExpAdapter
import com.ezaf.www.citisci.utils.viewmodel.FeedPageViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import androidx.recyclerview.widget.DividerItemDecoration
import android.view.animation.AnimationUtils


class FeedPage : Fragment() {

    lateinit var recyclerView: RecyclerView

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

    private fun setupRecycler(rootView: View) {

        Observable.fromCallable {
            localDbHandler.experimentDao().getAllExp()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recyclerView = rootView.findViewById(R.id.feedPageRecyclerView)
                    recyclerView.run {
                        layoutManager = LinearLayoutManager(context)
                        adapter = ExpAdapter(it, context)
                        addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
                        runLayoutAnimation(this)
                }
        }
        //TODO: dispose

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





//        btn_feed_page.setOnClickListener {
//            Navigation.findNavController(it).navigate(R.id.destination_my_experiments)
//        }

        //get safe arg from activity: (add args and rebuild)
//        arguments?.let{
//            val safeArgs = MyExperimentsArgs.fromBundle(it)
//            someTextView.text = safeArgs.field
//        }
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
