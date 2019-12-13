package com.lhx.glakit.properties

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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