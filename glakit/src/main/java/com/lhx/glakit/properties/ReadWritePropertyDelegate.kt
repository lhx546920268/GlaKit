package com.lhx.glakit.properties

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 属性代理，可监听值改变，只有值不同的时候才会回调
 */
class ReadWritePropertyDelegate<T>(var value: T, val observer: ((oldValue: T, newValue: T) -> Unit)? = null):
    ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if(this.value != value){
            val oldValue = this.value
            this.value = value

            if(observer != null){
                observer.invoke(oldValue, value)
            }
        }
    }
}