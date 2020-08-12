package com.guider.health.common.views

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.widget.EditText


/**
 * EditText 限制输入整数和小数 的位数
 * 默认 整数位无限制，小数位 最多2位
 */
class DecimalInputTextWatcher : TextWatcher {
    private var editText: EditText
    private var decimalDigits // 小数的位数
            : Int
    private var integerDigits // 整数的位数
            = 0

    constructor(editText: EditText) {
        this.editText = editText
        decimalDigits = DEFAULT_DECIMAL_DIGITS
    }

    constructor(editText: EditText, decimalDigits: Int) {
        this.editText = editText
        if (decimalDigits <= 0) throw RuntimeException("decimalDigits must > 0")
        this.decimalDigits = decimalDigits
    }

    constructor(editText: EditText, integerDigits: Int, decimalDigits: Int) {
        this.editText = editText
        if (integerDigits <= 0) throw RuntimeException("integerDigits must > 0")
        if (decimalDigits <= 0) throw RuntimeException("decimalDigits must > 0")
        this.decimalDigits = decimalDigits
        this.integerDigits = integerDigits
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
        var s = editable.toString()
        editText.removeTextChangedListener(this)
        if (s.contains(".")) {
            if (integerDigits > 0) {
                editText.filters = arrayOf<InputFilter>(LengthFilter(
                        integerDigits + decimalDigits + 1))
            }
            if (s.length - 1 - s.indexOf(".") > decimalDigits) {
                s = s.substring(0,
                        s.indexOf(".") + decimalDigits + 1)
                editable.replace(0, editable.length, s.trim { it <= ' ' }) //不输入超出位数的数字
            }
        } else {
            if (integerDigits > 0) {
                editText.filters = arrayOf<InputFilter>(LengthFilter(integerDigits + 1))
                if (s.length > integerDigits) {
                    s = s.substring(0, integerDigits)
                    editable.replace(0, editable.length, s.trim { it <= ' ' })
                }
            }
        }
        if (s.trim { it <= ' ' } == ".") { //小数点开头，小数点前补0
            s = "0$s"
            editable.replace(0, editable.length, s.trim { it <= ' ' })
        }
        if (s.startsWith("0") && s.trim { it <= ' ' }.length > 1) { //多个0开头，只输入一个0
            if (s.substring(1, 2) != ".") {
                editable.replace(0, editable.length, "0")
            }
        }
        editText.addTextChangedListener(this)
    }

    companion object {
        private const val DEFAULT_DECIMAL_DIGITS = 2 //默认 小数的位数  2 位
    }
}