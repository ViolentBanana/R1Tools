package com.chen.r1.introduce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chen.r1.R
import com.chen.r1.base.BaseFragment


class IntroduceFragment : BaseFragment() {
    override fun getTitleText(): Int {
        return R.string.introduce_fragment_title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){}

    companion object {

        @JvmStatic
        fun newInstance() =
                IntroduceFragment()
    }
}
