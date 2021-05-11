package com.lhx.glakit.properties

import androidx.annotation.CallSuper
import kotlin.reflect.KProperty

/**
 * 可观察的，用来监听属性值变化的
 */
interface Observable: ObservableProperty.Callback {

    //回调
    val callbacks: HashMap<Int, HashMap<String, Any>>

    /**
     * 添加观察者
     * @param byUser 为true时不会自动回调，需要调用notifyChange
     */
    fun addObserver(observer: Any, callback: ObservableProperty.Callback, name: String, byUser: Boolean = false) {
        addObserver(observer.hashCode(), if(byUser) CallbackEntity(callback) else callback, name)
    }

    fun addObserver(observer: Any, callback: ObservableProperty.Callback, names: Array<String>, byUser: Boolean = false) {
        val key = observer.hashCode()
        for (name in names) {
            addObserver(key, if(byUser) CallbackEntity(callback) else callback, name)
        }
    }

    private fun addObserver(key: Int, callback: Any, name: String) {
        var map = callbacks[key]
        if (map == null) {
            map = HashMap()
            callbacks[key] = map
        }
        map[name] = callback
    }

    /**
     * 移除观察者
     * @param name 监听的属性名称，如果为空，则移除这个观察者的所有监听属性
     */
    fun removeObserver(observer: Any, name: String? = null) {
        val key = observer.hashCode()
        if (name != null) {
            val map = callbacks[key]
            if (map != null) {
                map.remove(name)
                if (map.size == 0) {
                    callbacks.remove(key)
                }
            }
        } else {
            callbacks.remove(key)
        }
    }

    fun removeObserver(observer: Any, names: Array<String>) {
        val key = observer.hashCode()
        val map = callbacks[key]
        if (map != null) {
            for (name in names) {
                map.remove(name)
            }
            if (map.size == 0) {
                callbacks.remove(key)
            }
        }
    }

    //手动回调，只回调byUser = true，并且值改变过的
    fun notifyChange() {
        for ((_, map) in callbacks) {
            for ((_, entity) in map) {
                if (entity is CallbackEntity && entity.hasOldValue) {
                    entity.callback.onPropertyValueChange(entity.oldValue, entity.newValue, entity.property!!)
                    entity.reset()
                }
            }
        }
    }

    @CallSuper
    override fun onPropertyValueChange(oldValue: Any?, newValue: Any?, property: KProperty<*>) {
        for ((_, value) in callbacks) {
            val callback = value[property.name]

            if (callback is CallbackEntity) {
                //记录下来 后面只回调一次，防止多次改变触发多次回调
                callback.oldValue = oldValue
                callback.newValue = newValue
                callback.property = property
            } else if (callback is ObservableProperty.Callback) {
                callback.onPropertyValueChange(oldValue, newValue, property)
            }
        }
    }

    /**
     * 手动回调的实体
     */
    private class CallbackEntity(val callback: ObservableProperty.Callback) {

        //旧值
        private var _oldValue: Any? = null
        var oldValue: Any?
            get() = _oldValue
            set(value) {
                if(!hasOldValue){
                    hasOldValue = true
                    _oldValue = value
                }
            }

        //是否有旧值，旧值只设置一次
        var hasOldValue: Boolean = false

        //新值
        var newValue: Any? = null

        //关联的属性
        var property: KProperty<*>? = null

        //重置
        fun reset(){
            hasOldValue = false
            _oldValue = null
            newValue = null
            property = null
        }
    }
}