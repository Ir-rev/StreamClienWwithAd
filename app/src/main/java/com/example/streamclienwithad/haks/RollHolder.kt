package com.example.streamclienwithad.haks

import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.video.playback.model.VideoAd
import java.lang.ref.WeakReference

/**
 * Класс держатель для предоставление роллов для яндекс рекламы
 */
class RollHolder private constructor() {

    /** Список подготовленых мидролов */
    private val loadedPreRollList = mutableSetOf<VideoAd>()

    /** Список подготовленых мидролов */
    private val loadedMidRollList = mutableSetOf<VideoAd>()

    /** Костыль со списком имен прероллов, нужен для того что бы случайно не запустить мидролл */
    private val preRollsAdNamesList = mutableListOf<String>()

    /** подготовлен ли преролл */
    fun isPrerollPrepared(): Boolean = loadedPreRollList.isNotEmpty()

    /** подготовлен ли мидрол */
    fun isMidrollPrepared(): Boolean = loadedMidRollList.isNotEmpty()

    /** получить преролл */
    fun getPreRollOrNull(): VideoAd? {
        synchronized(loadedPreRollList) {
            val preroll = loadedPreRollList.firstOrNull()
            loadedPreRollList.remove(preroll)
            return preroll
        }
    }

    /** получить мидролл */
    fun getMidRollOrNull(): VideoAd? {
        synchronized(loadedMidRollList) {
            val midRoll = loadedMidRollList.firstOrNull()
            loadedMidRollList.remove(midRoll)
            return midRoll
        }
    }

    /** Добавляет ролл в список загруженных роллов */
    fun addRollToLoadedList(videoAd: VideoAd) {
        if (isPreRollNameListConstainsThisAd(videoAd)) {
            loadedPreRollList.add(videoAd)
        } else {
            loadedMidRollList.add(videoAd)
        }
    }

    /**
     * Костыль для добавления имен в списко имен прероллов,
     * нужен для того что бы случайно не запустить мидролл
     */
    fun addPreRollNamesToList(ad: InstreamAd) {
        preRollsAdNamesList.addAll(ad.getPreRolls().map { it.toString() })
    }

    /** Очистка имен преролов */
    fun clearPreRollNamesToList() {
        preRollsAdNamesList.clear()
    }

    /** Проверить прерол это или нет */
    fun isPreRoll(videoAd: VideoAd): Boolean {
        return loadedPreRollList.contains(videoAd)
    }

    /** проверка что реклама является преролом */
    private fun isPreRollNameListConstainsThisAd(videoAd: VideoAd): Boolean {
        preRollsAdNamesList.forEachIndexed { index, name ->
            if (videoAd.toString().contains(name)) {
                return true
            }
        }
        return false
    }


    /** Очистить холер */
    fun dispose() {
        loadedPreRollList.clear()
        loadedMidRollList.clear()
        preRollsAdNamesList.clear()
    }

    companion object {

        private var instance: WeakReference<RollHolder>? = null

        fun getInstance(): RollHolder {
            synchronized(this) {
                val currentInstance = instance?.get() ?: RollHolder()
                instance = WeakReference(currentInstance)
                return currentInstance
            }
        }
    }
}