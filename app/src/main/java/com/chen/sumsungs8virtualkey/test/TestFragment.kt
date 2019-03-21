package com.chen.sumsungs8virtualkey.test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.chen.sumsungs8virtualkey.R
import com.chen.sumsungs8virtualkey.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TestFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TestFragment : BaseFragment() {
    override fun getTitleText(): Int {
        return R.string.test_fragment_title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = TestFragment()

    }
}
