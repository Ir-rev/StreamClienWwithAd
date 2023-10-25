package com.example.streamclienwithad.player.dirty_hacks

import java.lang.ref.WeakReference

/**
 * Класс держатель для предоставление роллов для яндекс рекламы
 */
class RollTimeHolder private constructor() {

    private val midRollTimeList = mutableSetOf<Long>()

    fun addTimeToRollTimeList(time: Long) {
        synchronized(midRollTimeList) {
            midRollTimeList.add(time)
        }
    }

    fun getFirstTime(): Long? {
        return synchronized(midRollTimeList) {
            val time = midRollTimeList.firstOrNull()
            midRollTimeList.remove(time)
            time
        }
    }

    companion object {

        private var instance: WeakReference<RollTimeHolder>? = null

        fun getInstance(): RollTimeHolder {
            synchronized(this) {
                val currentInstance = instance?.get() ?: RollTimeHolder()
                instance = WeakReference(currentInstance)
                return currentInstance
            }
        }
    }
}