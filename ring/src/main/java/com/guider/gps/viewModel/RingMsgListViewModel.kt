package com.guider.gps.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trello.rxlifecycle4.LifecycleTransformer

/**
 * @Package: com.guider.gps.viewModel
 * @ClassName: RingMsgListViewModel
 * @Description: 消息页面的viewModel
 * @Author: hjr
 * @CreateDate: 2020/9/15 11:06
 * Copyright (C), 1998-2020, GuiderTechnology
 */
class RingMsgListViewModel :ViewModel(){

    var abnormalMsgUndoObservable = MutableLiveData<String>()
    var abnormalMsgUndoErrorObservable: MutableLiveData<String> = MutableLiveData()

    fun getAbnormalMsgUndoNum(life: LifecycleTransformer<Result<String>>,
                              client: String, version: String){
//        module.validateVersion(life,client,version,{
//            validateVersionObservable.value =it
//        },{
//            validateVersionErrorObservable.value =it
//        })
    }
}