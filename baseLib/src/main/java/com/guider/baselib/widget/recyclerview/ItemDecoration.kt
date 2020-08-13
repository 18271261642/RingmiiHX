package com.guider.baselib.widget.recyclerview

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View


/**
 * @className: ItemDecoration
 * @desc: RecyclerView 间距适配
 * @author: zyy
 * @date: 2019/6/20 15:20
 * @company: joinUTech
 * @leader: ke
 */
class GridItemDecoration(private val spanCount: Int, private val spacing: Int/*(px)*/,
                         private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
//        super.getItemOffsets(outRect, view, parent, state)

        // item position
        val position = parent.getChildAdapterPosition(view)
        // item column
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * spacing / spanCount
            // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

class ListItemDecoration(private val spacing: Int/*(px)*/) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
//        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position > 0) {
            outRect.top = spacing
        }
    }
}