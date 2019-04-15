package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.creceiptures.R


@SuppressLint("ValidFragment")
class LeaderboardFragment(context: Context) : Fragment() {

    private var parentContext: Context = context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragment = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val fragmentAdapter = LeaderboardPagerAdapter(activity!!.supportFragmentManager, parentContext)
        val viewpager = fragment.findViewById<ViewPager>(R.id.viewpager_main)
        viewpager.adapter = fragmentAdapter

        fragment.findViewById<TabLayout>(R.id.tabs_main).setupWithViewPager(viewpager)
        // Inflate the layout for this fragment
        return fragment
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    // straight from Shook
    class LeaderboardPagerAdapter(fm: FragmentManager, context: Context) : FragmentPagerAdapter(fm) {

        private val parentContext = context

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    LeaderboardTotalFragment(parentContext)
                }
                1 -> {
                    LeaderboardValueFragment(parentContext)
                }
                else -> {
                    LeaderboardQuantityFragment(parentContext)
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Total petCoin"
                1 -> "Strongest pet"
                else -> {
                    return "Most pets"
                }
            }
        }
    }

}
