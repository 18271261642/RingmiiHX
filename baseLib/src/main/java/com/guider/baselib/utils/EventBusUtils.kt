package com.guider.baselib.utils

import org.greenrobot.eventbus.EventBus

/**
 * Description TODO
 * Author HJR36
 * Date 2018/4/13 16:43
 */
object EventBusUtils {

    fun register(subscriber :Any){
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber)
        }
    }

    fun unregister(subscriber: Any) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber)
        }
    }

    fun sendEvent(event: EventBusEvent<Any>) {
        EventBus.getDefault().post(event)
    }

    fun sendStickyEvent(event: EventBusEvent<Any>) {
        EventBus.getDefault().postSticky(event)
    }

    fun clearStickyEvent(event: Any) {
        EventBus.getDefault().removeStickyEvent(event)
    }
}