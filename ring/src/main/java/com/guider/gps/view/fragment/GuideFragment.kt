package com.guider.gps.view.fragment

import android.os.Bundle
import android.view.View
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.StringUtil
import com.guider.gps.R
import kotlinx.android.synthetic.main.fragment_guide.*

/**
 * @Package:        com.guider.gps.view.fragment
 * @ClassName:      GuideFragment
 * @Description:    引导页的fragment
 * @Author:         hjr
 * @CreateDate:     2020/8/27 16:39
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class GuideFragment : BaseFragment() {

    private val ARG_STR = "text"
    private var pageType = ""

    companion object {
        fun newInstance(text: String) = GuideFragment().apply {
            arguments = Bundle().apply { putString(ARG_STR, text) }
            TAG = text
        }
    }

    override val layoutRes: Int
        get() = R.layout.fragment_guide

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        arguments?.takeIf { it.containsKey(ARG_STR) }?.apply {
            if (StringUtil.isNotBlankAndEmpty(getString(ARG_STR)))
                pageType = getString(ARG_STR)!!
        }
        when (pageType) {
            "page1" -> {
                titleTv.text = mActivity.resources.getString(R.string.app_guide_page1_title)
                contentTv.text = mActivity.resources.getString(R.string.app_guide_page1_content)
                rootLayout.setImageResource(R.drawable.bg_guide1)
                guide1.visibility = View.VISIBLE
            }
            "page2" -> {
                titleTv.text = mActivity.resources.getString(R.string.app_guide_page2_title)
                contentTv.text = mActivity.resources.getString(R.string.app_guide_page2_content)
                rootLayout.setImageResource(R.drawable.bg_guide2)
                guide2.visibility = View.VISIBLE
            }
            "page3" -> {
                titleTv.text = mActivity.resources.getString(R.string.app_guide_page3_title)
                contentTv.text = mActivity.resources.getString(R.string.app_guide_page3_content)
                rootLayout.setImageResource(R.drawable.bg_guide3)
                guide3.visibility = View.VISIBLE
            }
        }
    }
}