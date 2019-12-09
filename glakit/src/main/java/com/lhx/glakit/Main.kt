package com.lhx.glakit

import java.util.*

class Main {

    companion object {

        fun checkInRange(date: MyDate, first: MyDate, last: MyDate): Boolean {
            return date in DateRange(first, last)
        }


        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello!")

            checkInRange(MyDate(1990, 12, 12), MyDate(1990, 12, 12), MyDate(1990, 12, 12))
        }
    }

    data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {


        override fun compareTo(other: MyDate): Int{
            if(this.year > other.year){
                return 1
            }else{
                if(this.year == other.year){
                    if(this.month > other.month){
                        return 1
                    }else if(this.month == other.month && this.dayOfMonth > other.dayOfMonth){
                        return 1
                    }
                }
            }
            return -1
        }
    }

    class DateRange(val start: MyDate, val endInclusive: MyDate): Iterable<MyDate>{
       operator fun contains(d: MyDate): Boolean{
            if(d.year < start.year || d.year > endInclusive.year) return false
            if(d.year == start.year){
                if(d.month < start.month) return false

                if(d.month == start.month && d.dayOfMonth < start.dayOfMonth) return false
            }

            if(d.year == endInclusive.year){
                if(d.month > endInclusive.month) return false

                if(d.month == endInclusive.month && d.dayOfMonth > endInclusive.dayOfMonth) return false
            }

            return true
        }

        var currentDate: MyDate = start

        /**
         * Returns an iterator over the elements of this object.
         */
        override fun iterator(): Iterator<MyDate> {
            return object : Iterator<MyDate>{
                /**
                 * Returns `true` if the iteration has more elements.
                 */
                override fun hasNext(): Boolean {
                    return when{
                        currentDate.year != endInclusive.year -> endInclusive.year - currentDate.year > 0
                        currentDate.month != endInclusive.month -> endInclusive.month - currentDate.month > 0
                        else -> endInclusive.dayOfMonth - currentDate.dayOfMonth > 0
                    }
                }

                /**
                 * Returns the next element in the iteration.
                 */
                override fun next(): MyDate {
                    var year = currentDate.year
                    var month = currentDate.month
                    var dayOfMonth = currentDate.dayOfMonth

                    if(dayOfMonth < 30){
                        dayOfMonth ++
                    }else{
                        dayOfMonth = 1
                        if(month < 12){
                            month ++
                        }else{
                            month = 1
                            year ++
                        }
                    }

                    currentDate = MyDate(year, month, dayOfMonth)
                    return currentDate
                }
            }
        }
    }
}