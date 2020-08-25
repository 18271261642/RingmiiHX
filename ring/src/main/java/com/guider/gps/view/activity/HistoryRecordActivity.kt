package com.guider.gps.view.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.guider.baselib.base.BaseActivity
import com.guider.gps.R
import com.guider.gps.adapter.HistoryRecordListAdapter
import kotlinx.android.synthetic.main.activity_history_record.*

/**
 * 历史记录
 */
class HistoryRecordActivity : BaseActivity() {

    override val contentViewResId: Int
        get() = R.layout.activity_history_record

    private lateinit var adapter: HistoryRecordListAdapter
    private var historyRecordList = arrayListOf<String>()

    override fun openEventBus(): Boolean {
        return false
    }

    override fun initImmersion() {
        setTitle(resources.getString(R.string.app_history_list))
        showBackButton(R.drawable.ic_back_white)
    }

    override fun initView() {
        historyRecordRv.layoutManager = LinearLayoutManager(this)
    }

    override fun initLogic() {
        historyRecordList.add("2020-08-15 15:07")
        historyRecordList.add("2020-08-16 16:07")
        historyRecordList.add("2020-08-16 17:07")
        adapter = HistoryRecordListAdapter(mContext!!, historyRecordList)
        historyRecordRv.adapter = adapter
    }

    override fun showToolBar(): Boolean {
        return true
    }
}