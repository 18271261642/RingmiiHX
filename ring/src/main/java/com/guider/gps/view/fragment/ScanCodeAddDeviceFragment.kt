package com.guider.gps.view.fragment

import android.view.View
import com.guider.baselib.base.BaseFragment
import com.guider.gps.R

class ScanCodeAddDeviceFragment : BaseFragment() {

    companion object {
        fun newInstance() = ScanCodeAddDeviceFragment()
    }

    override val layoutRes: Int
        get() = R.layout.fragment_scan_code_add_device

    override fun initView(rootView: View) {

    }

    override fun initLogic() {

    }
}