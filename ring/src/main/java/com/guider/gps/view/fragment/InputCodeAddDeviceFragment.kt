package com.guider.gps.view.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.StringUtils
import com.guider.gps.R
import kotlinx.android.synthetic.main.fragment_input_code_add_device.*

class InputCodeAddDeviceFragment : BaseFragment() {

    companion object {
        fun newInstance() = InputCodeAddDeviceFragment()
    }

    override val layoutRes: Int
        get() = R.layout.fragment_input_code_add_device

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        inputEdit.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (StringUtils.isNotBlankAndEmpty(s.toString())) {
                    deleteEdit.visibility = View.VISIBLE
                } else deleteEdit.visibility = View.GONE
            }

        })
        deleteEdit.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            deleteEdit -> {
                inputEdit.setText("")
            }
        }
    }
}