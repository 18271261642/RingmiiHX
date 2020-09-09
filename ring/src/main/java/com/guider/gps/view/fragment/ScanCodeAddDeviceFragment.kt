package com.guider.gps.view.fragment

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.guider.baselib.base.BaseFragment
import com.guider.baselib.utils.PermissionUtils
import com.guider.baselib.utils.SCAN_CODE
import com.guider.feifeia3.utils.ToastUtil
import com.guider.gps.R
import com.king.zxing.CaptureActivity
import kotlinx.android.synthetic.main.fragment_scan_code_add_device.*

class ScanCodeAddDeviceFragment : BaseFragment() {

    val KEY_TITLE = "key_title"
    val KEY_IS_CONTINUOUS = "key_continuous_scan"

    companion object {
        fun newInstance() = ScanCodeAddDeviceFragment()
    }

    override val layoutRes: Int
        get() = R.layout.fragment_scan_code_add_device

    override fun initView(rootView: View) {

    }

    override fun initLogic() {
        scanCodeTv.setOnClickListener(this)
    }

    override fun onNoDoubleClick(v: View) {
        when (v) {
            scanCodeTv -> {
                val perms = arrayOf(Manifest.permission.CAMERA)
                PermissionUtils.requestPermissionFragment(this, perms, "照相机权限", {
                    //有权限
                    startScan(CaptureActivity::class.java,
                            mActivity.resources.getString(R.string.app_add_device_scan_code))
                }, {
                    ToastUtil.show(mActivity,
                            mActivity.resources.getString(R.string.app_request_permission_camera))
                })
            }
        }
    }

    /**
     * 扫码
     * @param cls
     * @param title
     */
    private fun startScan(cls: Class<*>, title: String) {
        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(
                mActivity, R.anim.`in`, R.anim.out)
        val intent = Intent(mActivity, cls)
        intent.putExtra(KEY_TITLE, title)
        intent.putExtra(KEY_IS_CONTINUOUS, false)
        ActivityCompat.startActivityForResult(mActivity, intent, SCAN_CODE, optionsCompat.toBundle())
    }
}