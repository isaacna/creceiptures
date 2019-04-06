package com.example.creceiptures.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.creceiptures.R
import com.example.creceiptures.fragment.SigninFragment
import com.example.creceiptures.fragment.SignupFragment

class AccountPagerAdapter(private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment {
        return if (p0 == 0) {
            SignupFragment(context)
        } else {
            SigninFragment(context)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.create_account)
        } else {
            context.getString(R.string.sign_in)
        }
    }
}