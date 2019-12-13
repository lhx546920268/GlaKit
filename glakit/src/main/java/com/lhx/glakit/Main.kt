package com.lhx.glakit

import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KProperty

class Main {

    data class Product(val price: Int)
    data class Order(val products: List<Product>, val isDelivered: Boolean)
    data class Customer(val orders: List<Order>)

    data class Result<T, R: MutableCollection<T>>(val result1: R, val result2: R)


    fun <T, R: MutableCollection<T>> Collection<T>.partitionTo(list1: R, list2: R,
                                                  p: (T) -> Boolean): Result<T, R> {

        val (l1, l2) = this.partition(p)

        list1.addAll(l1)
        list2.addAll(l2)

        return Result(list1, list2)
    }

    class IntTransformer: (Int) -> Int {
        override operator fun invoke(x: Int): Int = TODO()
    }

    class Delegate(var value: Int) {

        fun tr(tr:(str: String) -> Unit){
            val a = IntTransformer()

        }

        fun td(td:(str: String) -> Unit){

        }

        fun text(str: String){

        }

        fun <T> par(co: Collection<T>){

        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {

            arrayOf(1).fold(1){pre, cur ->

                return pre + cur
            }

            tr {
                td {
                    text("Product")
                }
                td {
                    text("Popularity")
                }
            }

            return value
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            this.value = value
        }
    }



    companion object {


        fun buildString(build: StringBuilder.() -> Unit): String {
            val stringBuilder = StringBuilder()
            stringBuilder.build()
            return stringBuilder.toString()
        }


        fun <T> T.myApply(f: T.() -> Unit): T {
            f()
            return this
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello!")

        }



    }

}