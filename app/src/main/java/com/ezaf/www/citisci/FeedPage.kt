package com.ezaf.www.citisci

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.feed_page_fragment.*


class FeedPage : Fragment() {

    companion object {
        fun newInstance() = FeedPage()
    }

    private lateinit var viewModel: FeedPageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_feed_page.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.destination_my_experiments)
        }

        //get safe arg from activity: (add args and rebuild)
//        arguments?.let{
//            val safeArgs = MyExperimentsArgs.fromBundle(it)
//            someTextView.text = safeArgs.field
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FeedPageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
