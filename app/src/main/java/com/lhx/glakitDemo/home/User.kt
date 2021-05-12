package com.lhx.glakitDemo.home

import com.lhx.glakit.properties.BaseObservable
import com.lhx.glakit.properties.ObservableProperty

class User: BaseObservable() {

    var title by ObservableProperty<String?>(null, this)
    var subtitle by ObservableProperty<String?>(null, this)
}