package com.guider.baselib.utils

import android.annotation.SuppressLint
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import com.guider.baselib.bean.MobileRegularExp
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * 字符串相关工具类
 */
class StringUtil private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * 判断字符串是否为null或长度为0
         *
         * @param s 待校验字符串
         * @return `true`: 空<br></br> `false`: 不为空
         */
        fun isEmpty(s: String?): Boolean {
            return s == null || s.isEmpty() || "".equals(s, ignoreCase = true)
                    || "null".equals(s, ignoreCase = true)
        }

        /**
         * 判断字符串是否为null或全为空格
         *
         * @param s 待校验字符串
         * @return `true`: null或全空格<br></br> `false`: 不为null且不全空格
         */
        fun isSpace(s: String?): Boolean {
            return s == null || s.trim { it <= ' ' }.isEmpty()
        }

        /**
         * 判断两字符串是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
//        fun equals(a: CharSequence?, b: CharSequence?): Boolean {
//            if (a === b) return true
//            val length: Int
//            if (a != null && b != null && (length = a.length) == b.length) {
//                if (a is String && b is String) {
//                    return a == b
//                } else {
//                    for (i in 0 until length) {
//                        if (a[i] != b[i]) return false
//                    }
//                    return true
//                }
//            }
//            return false
//        }

        /**
         * 判断两字符串忽略大小写是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return `true`: 相等<br></br>`false`: 不相等
         */
        fun equalsIgnoreCase(a: String, b: String?): Boolean {
            return a === b || b != null && a.length == b.length && a.regionMatches(0, b, 0, b.length, ignoreCase = true)
        }

        /**
         * null转为长度为0的字符串
         *
         * @param s 待转字符串
         * @return s为null转为长度为0字符串，否则不改变
         */
        fun null2Length0(s: String?): String {
            return s ?: ""
        }

        /**
         * 返回字符串长度
         *
         * @param s 字符串
         * @return null返回0，其他返回自身长度
         */
        fun length(s: CharSequence?): Int {
            return s?.length ?: 0
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        fun upperFirstLetter(s: String): String? {
            return if (isEmpty(s) || !Character.isLowerCase(s[0])) s else (s[0].toInt() - 32).toChar().toString() + s.substring(1)
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        fun lowerFirstLetter(s: String): String? {
            return if (isEmpty(s) || !Character.isUpperCase(s[0])) {
                s
            } else (s[0].toInt() + 32).toChar().toString() + s.substring(1)
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        fun reverse(s: String): String {
            val len = length(s)
            if (len <= 1) return s
            val mid = len shr 1
            val chars = s.toCharArray()
            var c: Char
            for (i in 0 until mid) {
                c = chars[i]
                chars[i] = chars[len - i - 1]
                chars[len - i - 1] = c
            }
            return String(chars)
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        fun toDBC(s: String): String? {
            if (isEmpty(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i].toInt() == 12288) {
                    chars[i] = ' '
                } else if (chars[i].toInt() in 65281..65374) {
                    chars[i] = (chars[i].toInt() - 65248).toChar()
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        fun toSBC(s: String): String? {
            if (isEmpty(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                when {
                    chars[i] == ' ' -> chars[i] = 12288.toChar()
                    chars[i].toInt() in 33..126 -> chars[i] = (chars[i].toInt() + 65248).toChar()
                    else -> chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 检测是否为手机号
         */
        fun isPhoneNumber(number: String): Boolean {

            val regex = "^(1[3-9][0-9])\\d{8}$"
            val p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(number)
            return m.matches()
        }

        /**
         * 检测是否为URL网址
         */
        fun isWebUrl(url: String): Boolean {
            val regex = "((http[s]?|ftp)://[a-zA-Z0-9\\.\\-]+" +
                    "\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)" +
                    "|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})" +
                    "(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)"
            val p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(url)
            return m.matches()
        }

        /**
         * 验证是否是电话
         */
        fun isTelPhone(str: String): Boolean {
            val regex1 = "^[0][1-9]{2,3}-[0-9]{5,10}$"// 验证带区号的
            val regex2 = "^[1-9]{1}[0-9]{5,8}\$"// 验证没有区号的
            val p1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE)
            val p2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE)
            var b = false
            b = if (str.length > 9) {
                val m = p1.matcher(str)
                m.matches()
            } else {
                val m = p2.matcher(str)
                m.matches()
            }
            return b
        }

        //过滤特殊字符
        fun setEtFilter(et: EditText?) {
            if (et == null) {
                return
            }
            //表情过滤器
            val emojiFilter = InputFilter { source, _, _, _, _, _ ->
                val emoji = Pattern.compile(
                        "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                        Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)
                val emojiMatcher = emoji.matcher(source)
                if (emojiMatcher.find()) {
                    ""
                } else null
            }
            //特殊字符过滤器
            val specialCharFilter = InputFilter { source, _, _, _, _, _ ->
                val regexStr = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
                val pattern = Pattern.compile(regexStr)
                val matcher = pattern.matcher(source.toString())
                if (matcher.matches()) {
                    ""
                } else {
                    null
                }
            }

            et.filters = arrayOf(emojiFilter, specialCharFilter)
        }

        /**
         * 检测密码是否为6-16位大小写字母/数字/下划线
         */
        fun isPassword(password: String): Boolean {
            val regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z_]{6,16}$"
            val p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(password)
            return m.matches()
        }

        fun isNotBlankAndEmpty(str: String?): Boolean {
            return str != null && str.isNotBlank() && str != "" && str != "null" && str != "NULL"
        }

        fun isNotBlankAndEmpty(vararg strs: String?): Boolean {
            var flag: Boolean = true
            for (str in strs) {
                if (str == null || str == "" || str.isEmpty()) {
                    flag = false
                    break
                }
            }
            return flag
        }

        fun transPhoneInVilate(phone: String): String {
            val start = phone.substring(0, 3)
            val end = phone.substring(7, 11)
            return "$start****$end"
        }

        /**
         * 是否是车牌号
         * @param vehicleNumber
         * @return
         */
        fun IsVehicleNumber(vehicleNumber: String): Boolean {
            val express = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$"
            return Pattern.matches(express, vehicleNumber)
        }

        /**
         * 保留两位小数正则
         * @param number
         * @return
         */
        fun isOnlyPointNumber(number: String): Boolean {
            val pattern = Pattern.compile("^[1-9]\\d+\\.[0-9]{1,2}|0\\.[0-9]{1,2}|[1-9]\\d+|0$")
            val matcher = pattern.matcher(number)
            return matcher.matches()
        }

        /**
         * 生成唯一哈希值
         */
        @SuppressLint("SimpleDateFormat")
        fun generateHashValue(): String {
            var hashValue = ""
            val integerHashSet = hashSetOf<Int>()
            val random = Random()
            val randoms = random.nextInt(1000)
            if (!integerHashSet.contains(randoms)) {
                integerHashSet.add(randoms)
                val sdf = SimpleDateFormat("yyyyMMddHHmmssss")
                hashValue = sdf.format(Date()) + randoms.toString()//唯一哈希码
            }
            return hashValue
        }

        /**
         * 判断是不是数字
         * @param str
         * @return
         */
        fun isNumeric(str: String): Boolean {
            return if (isNotBlankAndEmpty(str)) {
                val pattern = Pattern.compile("[0-9]+")
                val isNum: Matcher = pattern.matcher(str)
                isNum.matches()
            } else {
                false
            }
        }

        /**
         * 获取单个文件的MD5值！
         *
         * @param file
         * @return
         */
        fun getFileMD5(file: File): String {
            if (!file.isFile) {
                return ""
            }
            val digest: MessageDigest?
            val `in`: FileInputStream?
            val buffer = ByteArray(1024)
            var len: Int
            try {
                digest = MessageDigest.getInstance("MD5")
                `in` = FileInputStream(file)
                while (`in`.read(buffer, 0, 1024).also { len = it } != -1) {
                    digest.update(buffer, 0, len)
                }
                `in`.close()
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
            val bigInt = BigInteger(1, digest.digest())
            return bigInt.toString(16)
        }

        /**
         *  字节数B转化为KB、MB、GB的方法
         */
        fun getPrintSize(size: Long): String? {
            //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
            var size = size
            size = if (size < 1024) {
                return size.toString() + "B"
            } else {
                size / 1024
            }
            //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
            //因为还没有到达要使用另一个单位的时候
            //接下去以此类推
            size = if (size < 1024) {
                return size.toString() + "KB"
            } else {
                size / 1024
            }
            return if (size < 1024) { //因为如果以MB为单位的话，要保留最后1位小数，
                //因此，把此数乘以100之后再取余
                size *= 100
                (size / 100).toString() + "." + (size % 100).toString() + "MB"
            } else { //否则如果要以GB为单位的，先除于1024再作同样的处理
                size = size * 100 / 1024
                (size / 100).toString() + "." + (size % 100).toString() + "GB"
            }
        }


        /**
         * 设置字符串中多个不同关键字的颜色（颜色统一, 无点击事件）
         *
         * @param content 目标字符串
         * @param keyStrs 关键字集合
         * @param color   单一的颜色值
         * @return
         */
        fun setSpanColorStr(content: String, keyStrs: List<String>, color: Int): SpannableString {
            val spannableString = SpannableString(content)
            if (!keyStrs.isNullOrEmpty()) {
                for (i in keyStrs.indices) {
                    val keyStr = keyStrs[i]
                    if (content.contains(keyStr)) {
                        var startNew = 0
                        var startOld = 0
                        var temp = content
                        while (temp.contains(keyStr)) {
                            spannableString.setSpan(
                                    object : ClickableSpan() {
                                        override fun updateDrawState(ds: TextPaint) {
                                            super.updateDrawState(ds)
                                            ds.color = color
                                            ds.isUnderlineText = false
                                        }

                                        override fun onClick(widget: View) {}
                                    }, startOld + temp.indexOf(keyStr),
                                    startOld + temp.indexOf(keyStr) + keyStr.length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            startNew = temp.indexOf(keyStr) + keyStr.length
                            startOld += startNew
                            temp = temp.substring(startNew)
                        }
                    }
                }
            }
            return spannableString
        }


        /**
         * 过滤所有以html标签
         */
        private const val REG_HTML = "<([^>]*)>"

        /**
         * @param htmlStr
         * @return 删除Html标签
         */
        fun delHTMLTag(htmlStr: String): String {
            val p_html = Pattern.compile(REG_HTML, Pattern.CASE_INSENSITIVE)
            val m_html = p_html.matcher(htmlStr)
            // 过滤html标签
            val returnStr = m_html.replaceAll("")
            return returnStr
        }

        fun isMobileNumber(nationalCode: String?, mobileNumber: String?): Boolean {
            var isMobileNumber = false
            for (regularExp in MobileRegularExp.values()) {
                val pattern = Pattern.compile(regularExp.regularExp)
                val matcher = pattern.matcher(StringBuilder().append("+${nationalCode}")
                        .append(mobileNumber).toString())
                if (matcher.matches()) {
                    isMobileNumber = true
                    // 枚举中把最常用的国际区号排在前面可以减少校验开销
                    break
                }
            }
            return isMobileNumber
        }
    }
}