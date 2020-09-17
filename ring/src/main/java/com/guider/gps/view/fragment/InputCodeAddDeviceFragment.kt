package com.guider.gps.view.fragment

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.StringUtil
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.guider.gps.view.activity.AddNewDeviceActivity
import com.guider.health.apilib.BuildConfig
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
                if (StringUtil.isNotBlankAndEmpty(s.toString())) {
                    deleteEdit.visibility = View.VISIBLE
                } else deleteEdit.visibility = View.GONE
            }

        })
        deleteEdit.setOnClickListener(this)
        enterTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            deleteEdit -> {
                inputEdit.setText("")
            }
            enterTv -> {
                if (StringUtil.isEmpty(inputEdit.text.toString())) {
                    showToast(mActivity.resources.getString(R.string.app_device_code_empty))
                    return
                }
                val tempCode = "863659040064551"
                if (!BuildConfig.DEBUG && inputEdit.text?.length != tempCode.length){
                    ToastUtil.showCenter(mActivity,
                            mActivity.resources.getString(R.string.app_incorrect_format))
                    return
                }
                val deviceCode = inputEdit.text.toString()
                (mActivity as AddNewDeviceActivity).bindNewDeviceWithAccount(deviceCode)
            }
        }
    }
}