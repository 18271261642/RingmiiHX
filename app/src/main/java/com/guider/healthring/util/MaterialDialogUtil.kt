package com.guider.healthring.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItems

/**
 * materialDialog的java中转工具类
 */
object MaterialDialogUtil {

    fun showDialog(activity: AppCompatActivity, titleId: Int,
                   content: Int, positiveText: Int, negativeText: Int,
                   positiveClick: () -> Unit) {
        MaterialDialog(activity).show {
            title(titleId)
            message(content)
            positiveButton(positiveText) {
                it.dismiss()
                positiveClick()
            }
            negativeButton(negativeText) {
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    fun showDialog(fragment: Fragment, titleId: Int,
                   content: Int, positiveText: Int, negativeText: Int,
                   positiveClick: () -> Unit) {
        MaterialDialog(fragment.activity!!.baseContext).show {
            title(titleId)
            message(content)
            positiveButton(positiveText) {
                it.dismiss()
                positiveClick()
            }
            negativeButton(negativeText) {
                it.dismiss()
            }
            lifecycleOwner(fragment.activity)
        }
    }

    fun showDialog(activity: AppCompatActivity, titleId: Int,
                   content: String, positiveText: Int, negativeText: Int,
                   positiveClick: () -> Unit) {
        MaterialDialog(activity).show {
            title(titleId)
            message(text = content)
            positiveButton(positiveText) {
                it.dismiss()
                positiveClick()
            }
            negativeButton(negativeText) {
                it.dismiss()
            }
            lifecycleOwner(activity)
        }
    }

    fun showDialogWithItems(activity: AppCompatActivity, titleId: Int,
                            itemsArray: Int, negativeText: Int,
                            itemsCallbackSingleChoice: (Int) -> Unit) {
        MaterialDialog(activity).show {
            title(titleId)
            listItems(itemsArray, waitForPositiveButton = false) { _, index, _ ->
                itemsCallbackSingleChoice.invoke(index)
            }
            negativeButton(negativeText)
            lifecycleOwner(activity)
        }
    }
}