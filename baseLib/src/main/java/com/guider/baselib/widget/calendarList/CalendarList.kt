package com.guider.baselib.widget.calendarList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.guider.baselib.R
import com.guider.baselib.utils.DateUtilKotlin.getYearAndMonthWithLanguageShow
import com.guider.baselib.widget.calendarList.CalendarList.CalendarAdapter.OnRecyclerviewItemClick
import java.text.SimpleDateFormat
import java.util.*


class CalendarList : FrameLayout {
    var recyclerView: RecyclerView? = null
    var adapter: CalendarAdapter? = null
    private var startDate //开始时间
            : DateBean? = null
    private var endDate //结束时间
            : DateBean? = null
    var onDateSelected //选中监听
            : OnDateSelected? = null
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        addView(LayoutInflater.from(context).inflate(R.layout.item_calendar, this,
                false))
        recyclerView = findViewById(R.id.recyclerView)
        adapter = CalendarAdapter()
        val gridLayoutManager = GridLayoutManager(context, 7)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(i: Int): Int {
                return if (DateBean.item_type_month == adapter!!.data[i].getItemType()) {
                    7
                } else {
                    1
                }
            }
        }
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = adapter
        adapter!!.data.addAll(days("", ""))

//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this,R.drawable.shape));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        val myItemD = MyItemD()
        recyclerView?.addItemDecoration(myItemD)
        adapter!!.onRecyclerviewItemClick = object : OnRecyclerviewItemClick {
            override fun onItemClick(v: View?, position: Int) {
                onClick(adapter!!.data[position])
            }
        }
    }

    private fun onClick(dateBean: DateBean) {
        if (dateBean.getItemType() == DateBean.item_type_month
                || TextUtils.isEmpty(dateBean.getDay())) {
            return
        }

        //如果没有选中开始日期则此次操作选中开始日期
        if (startDate == null) {
            startDate = dateBean
            dateBean.setItemState(DateBean.ITEM_STATE_BEGIN_DATE)
        } else if (endDate == null) {
            //如果选中了开始日期但没有选中结束日期，本次操作选中结束日期

            //如果当前点击的结束日期跟开始日期一致 则不做操作
            if (startDate === dateBean) {
            } else if (dateBean.getDate().time < startDate!!.getDate().time) {
                //当前点选的日期小于当前选中的开始日期 则本次操作重新选中开始日期
                startDate!!.setItemState(DateBean.ITEM_STATE_NORMAL)
                startDate = dateBean
                startDate!!.setItemState(DateBean.ITEM_STATE_BEGIN_DATE)
            } else {
                //选中结束日期
                endDate = dateBean
                endDate!!.setItemState(DateBean.ITEM_STATE_END_DATE)
                setState()
                if (onDateSelected != null) {
                    onDateSelected?.selected(simpleDateFormat.format(startDate!!.getDate()), simpleDateFormat.format(endDate!!.getDate()))
                }
            }
        } else if (startDate != null && endDate != null) {
            //结束日期和开始日期都已选中
            clearState()

            //重新选择开始日期,结束日期清除
            startDate!!.setItemState(DateBean.ITEM_STATE_NORMAL)
            startDate = dateBean
            startDate!!.setItemState(DateBean.ITEM_STATE_BEGIN_DATE)
            endDate!!.setItemState(DateBean.ITEM_STATE_NORMAL)
            endDate = null
        }
        adapter!!.notifyDataSetChanged()
    }

    //选中中间的日期
    private fun setState() {
        if (endDate != null && startDate != null) {
            var start = adapter!!.data.indexOf(startDate)
            start += 1
            val end = adapter!!.data.indexOf(endDate)
            while (start < end) {
                val dateBean = adapter!!.data[start]
                if (!TextUtils.isEmpty(dateBean.getDay())) {
                    dateBean.setItemState(DateBean.ITEM_STATE_SELECTED)
                }
                start++
            }
        }
    }

    //取消选中状态
    private fun clearState() {
        if (endDate != null && startDate != null) {
            var start = adapter!!.data.indexOf(startDate)
            start += 1
            val end = adapter!!.data.indexOf(endDate)
            while (start < end) {
                val dateBean = adapter!!.data[start]
                dateBean.setItemState(DateBean.ITEM_STATE_NORMAL)
                start++
            }
        }
    }

    //日历adapter
    class CalendarAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        @JvmField
        var data = ArrayList<DateBean>()
        var onRecyclerviewItemClick: OnRecyclerviewItemClick? = null
        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
            if (i == DateBean.item_type_day) {
                val rootView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_day, viewGroup, false)
                val dayViewHolder = DayViewHolder(rootView)
                dayViewHolder.itemView.setOnClickListener { v: View? ->
                    if (onRecyclerviewItemClick != null) {
                        onRecyclerviewItemClick!!.onItemClick(v,
                                dayViewHolder.layoutPosition)
                    }
                }
                return dayViewHolder
            } else {
                val rootView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_month, viewGroup, false)
                val monthViewHolder = MonthViewHolder(rootView)
                monthViewHolder.itemView.setOnClickListener { v: View? ->
                    if (onRecyclerviewItemClick != null) {
                        onRecyclerviewItemClick!!.onItemClick(v,
                                monthViewHolder.layoutPosition)
                    }
                }
                return monthViewHolder
            }
        }

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
            if (viewHolder is MonthViewHolder) {
                //获取年月日格式的当前日期
                val yearAndMonthWithLanguageShow = getYearAndMonthWithLanguageShow(data[i].getMonthStr())
                viewHolder.tv_month.text = yearAndMonthWithLanguageShow
            } else {
                val vh = viewHolder as DayViewHolder
                vh.tv_day.text = data[i].getDay()
                vh.tv_check_in_check_out.visibility = GONE
                val dateBean = data[i]
                //设置item状态
                vh.startBgIv.visibility = GONE
                if (dateBean.getItemState() == DateBean.ITEM_STATE_BEGIN_DATE
                        || dateBean.getItemState() == DateBean.ITEM_STATE_END_DATE) {
                    //开始日期或结束日期
                    vh.startBgIv.visibility = VISIBLE
                    vh.tv_day.setTextColor(Color.WHITE)
                    vh.tv_check_in_check_out.visibility = VISIBLE
                    if (dateBean.getItemState() == DateBean.ITEM_STATE_END_DATE) {
                        vh.tv_check_in_check_out.text = "结束"
                    } else {
                        vh.tv_check_in_check_out.text = "开始"
                    }
                } else if (dateBean.getItemState() == DateBean.ITEM_STATE_SELECTED) {
                    //选中状态
                    vh.itemView.setBackgroundColor(Color.parseColor("#f5f5f5"))
                    vh.tv_day.setTextColor(Color.parseColor("#333333"))
                } else {
                    //正常状态
                    vh.itemView.setBackgroundColor(Color.WHITE)
                    vh.tv_day.setTextColor(Color.parseColor("#999999"))
                }
                //未避免文字颜色覆盖，放置最后
                if (data[i].getDate() != null) {
                    val format = SimpleDateFormat("yyyy-MM-dd")
                    val time = format.format(data[i].getDate())
                    val currentTime = format.format(Calendar.getInstance().time)
                    if (time == currentTime) {
                        //如果是今天
                        if (dateBean.getItemState() == DateBean.ITEM_STATE_BEGIN_DATE
                                || dateBean.getItemState() == DateBean.ITEM_STATE_END_DATE) {
                            vh.tv_day.setTextColor(Color.WHITE)
                        } else vh.tv_day.setTextColor(Color.parseColor("#333333"))
                        vh.currentDayIv.visibility = VISIBLE
                    } else {
                        vh.currentDayIv.visibility = GONE
                    }
                } else {
                    vh.currentDayIv.visibility = GONE
                }
            }
        }



        override fun getItemViewType(position: Int): Int {
            return data[position].getItemType()
        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_day: TextView
            var tv_check_in_check_out: TextView
            var currentDayIv: ImageView
            val startBgIv: ImageView

            init {
                tv_day = itemView.findViewById(R.id.tv_day)
                tv_check_in_check_out = itemView.findViewById(R.id.tv_check_in_check_out)
                currentDayIv = itemView.findViewById(R.id.currentDayIv)
                startBgIv = itemView.findViewById(R.id.startBgIv)
            }
        }

        inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_month: TextView = itemView.findViewById(R.id.tv_month)

        }

        interface OnRecyclerviewItemClick {
            fun onItemClick(v: View?, position: Int)
        }
    }

    /**
     * 生成日历数据
     */
    private fun days(sDate: String, eDate: String): List<DateBean> {
        val dateBeans: MutableList<DateBean> = ArrayList()
        try {
            val calendar = Calendar.getInstance()
            //日期格式化
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatYYYYMM = SimpleDateFormat("yyyy年MM月")
            val currentDate = Date()
            //起始日期
            val date = Date()
            calendar.time = date
            //结束日期
            calendar.add(Calendar.MONTH, -6)
            var startDate: Date? = Date(calendar.timeInMillis)
            calendar.time = startDate
            //结束日期
            calendar.add(Calendar.MONTH, +6)
            var endDate = Date(calendar.timeInMillis)
            Log.d(TAG, "startDate:" + format.format(startDate) +
                    "----------endDate:" + format.format(endDate))

            //格式化开始日期和结束日期为 yyyy-mm-dd格式
            val endDateStr = format.format(endDate)
            endDate = format.parse(endDateStr)
            val startDateStr = format.format(startDate)
            startDate = format.parse(startDateStr)
            calendar.time = startDate
            Log.d(TAG, "startDateStr:" + startDateStr +
                    "---------endDate:" + format.format(endDate))
            Log.d(TAG, "endDateStr:" + endDateStr +
                    "---------endDate:" + format.format(endDate))
            calendar[Calendar.DAY_OF_MONTH] = 1
            val monthCalendar = Calendar.getInstance()


            //按月生成日历 每行7个 最多6行 42个
            //每一行有七个日期  日 一 二 三 四 五 六 的顺序
            while (calendar.timeInMillis <= endDate.time) {


                //月份item
                val monthDateBean = DateBean()
                monthDateBean.setDate(calendar.time)
                monthDateBean.setMonthStr(formatYYYYMM.format(monthDateBean.getDate()))
                monthDateBean.setItemType(DateBean.item_type_month)
                dateBeans.add(monthDateBean)

                //获取一个月结束的日期和开始日期
                monthCalendar.time = calendar.time
                monthCalendar[Calendar.DAY_OF_MONTH] = 1
                val startMonthDay = calendar.time
                monthCalendar.add(Calendar.MONTH, 1)
                monthCalendar.add(Calendar.DAY_OF_MONTH, -1)
                val endMonthDay = monthCalendar.time

                //重置为本月开始
                monthCalendar[Calendar.DAY_OF_MONTH] = 1
                Log.d(TAG, "月份的开始日期:" + format.format(startMonthDay) +
                        "---------结束日期:" + format.format(endMonthDay))
                while (monthCalendar.timeInMillis <= endMonthDay.time) {

                    //生成单个月的日历

                    //处理一个月开始的第一天
                    if (monthCalendar[Calendar.DAY_OF_MONTH] == 1) {
                        //看某个月第一天是周几
                        val weekDay = monthCalendar[Calendar.DAY_OF_WEEK]
                        when (weekDay) {
                            1 -> {
                            }
                            2 ->                                 //周一
                                addDatePlaceholder(dateBeans, 1, monthDateBean.getMonthStr())
                            3 ->                                 //周二
                                addDatePlaceholder(dateBeans, 2, monthDateBean.getMonthStr())
                            4 ->                                 //周三
                                addDatePlaceholder(dateBeans, 3, monthDateBean.getMonthStr())
                            5 ->                                 //周四
                                addDatePlaceholder(dateBeans, 4, monthDateBean.getMonthStr())
                            6 -> addDatePlaceholder(dateBeans, 5, monthDateBean.getMonthStr())
                            7 -> addDatePlaceholder(dateBeans, 6, monthDateBean.getMonthStr())
                        }
                    }

                    //生成某一天日期实体 日item
                    val dateBean = DateBean()
                    dateBean.setDate(monthCalendar.time)
                    dateBean.setDay(monthCalendar[Calendar.DAY_OF_MONTH].toString() + "")
                    dateBean.setMonthStr(monthDateBean.getMonthStr())
                    dateBeans.add(dateBean)

                    //处理一个月的最后一天
                    if (monthCalendar.timeInMillis == endMonthDay.time) {
                        //看某个月第一天是周几
                        val weekDay = monthCalendar[Calendar.DAY_OF_WEEK]
                        when (weekDay) {
                            1 ->                                 //周日
                                addDatePlaceholder(dateBeans, 6, monthDateBean.getMonthStr())
                            2 ->                                 //周一
                                addDatePlaceholder(dateBeans, 5, monthDateBean.getMonthStr())
                            3 ->                                 //周二
                                addDatePlaceholder(dateBeans, 4, monthDateBean.getMonthStr())
                            4 ->                                 //周三
                                addDatePlaceholder(dateBeans, 3, monthDateBean.getMonthStr())
                            5 ->                                 //周四
                                addDatePlaceholder(dateBeans, 2, monthDateBean.getMonthStr())
                            6 -> addDatePlaceholder(dateBeans, 1, monthDateBean.getMonthStr())
                        }
                    }

                    //天数加1
                    monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                Log.d(TAG, "日期" + format.format(calendar.time) + "----周几" + getWeekStr(calendar[Calendar.DAY_OF_WEEK].toString() + ""))
                //月份加1
                calendar.add(Calendar.MONTH, 1)
            }
        } catch (ex: Exception) {
        }
        return dateBeans
    }

    //添加空的日期占位
    private fun addDatePlaceholder(dateBeans: MutableList<DateBean>, count: Int, monthStr: String) {
        for (i in 0 until count) {
            val dateBean = DateBean()
            dateBean.setMonthStr(monthStr)
            dateBeans.add(dateBean)
        }
    }

    private fun getWeekStr(mWay: String): String {
        var value = mWay
        when (mWay) {
            "1" -> {
                value = "天"
            }
            "2" -> {
                value = "一"
            }
            "3" -> {
                value = "二"
            }
            "4" -> {
                value = "三"
            }
            "5" -> {
                value = "四"
            }
            "6" -> {
                value = "五"
            }
            "7" -> {
                value = "六"
            }
        }
        return value
    }

    interface OnDateSelected {
        fun selected(startDate: String?, endDate: String?)
    }

    fun setOnDateSelectedListener(onDateSelected: OnDateSelected?) {
        this.onDateSelected = onDateSelected
    }

    companion object {
        private const val TAG = "CalendarList_LOG"
    }
}