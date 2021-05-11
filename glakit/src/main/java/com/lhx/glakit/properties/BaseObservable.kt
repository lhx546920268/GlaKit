package com.lhx.glakit.properties

/**
 * 基础可观察的对象
 */
class BaseObservable: Observable {

    override val callbacks: HashMap<Int, HashMap<String, Any>> by lazy { HashMap() }
}