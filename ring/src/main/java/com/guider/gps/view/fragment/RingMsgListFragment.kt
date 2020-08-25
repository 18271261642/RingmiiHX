package com.guider.gps.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.base.BaseFragment
import com.guider.gps.R
import com.guider.gps.adapter.AbnormalMsgListAdapter
import com.guider.gps.adapter.HealthCareMsgListAdapter

class RingMsgListFragment : BaseFragment() {

    private val ARG_STR = "type"

    companion object {
        fun newInstance(text: String) = RingMsgListFragment().apply {
            arguments = Bundle().apply { putString(ARG_STR, text) }
            TAG = text
        }
    }

    private lateinit var msgListRv: RecyclerView
    private var msgList = arrayListOf<String>()
    private lateinit var abnormalAdapter: AbnormalMsgListAdapter
    private lateinit var careAdapter: HealthCareMsgListAdapter

    override val layoutRes: Int
        get() = R.layout.fragment_ring_msg_list

    override fun initView(rootView: View) {
        msgListRv = rootView.findViewById(R.id.msgListRv)
        msgListRv.layoutManager = LinearLayoutManager(mActivity)
    }

    override fun initLogic() {
        arguments?.takeIf { it.containsKey(ARG_STR) }?.apply {
            when (getString(ARG_STR)) {
                resources.getString(R.string.app_msg_error_notify) -> {
                    abnormalAdapter = AbnormalMsgListAdapter(mActivity,
                            arrayListOf("11:05", "10:16"))
                    msgListRv.adapter = abnormalAdapter
                }
                resources.getString(R.string.app_msg_care_info) -> {
                    careAdapter = HealthCareMsgListAdapter(mActivity, arrayListOf("8-13 16:30"))
                    msgListRv.adapter = careAdapter
                }
                resources.getString(R.string.app_msg_system_info) -> {
                    abnormalAdapter = AbnormalMsgListAdapter(mActivity, arrayListOf("10:16"))
                    msgListRv.adapter = abnormalAdapter
                }
            }
        }

    }
}