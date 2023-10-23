package com.example.streamclienwithad.haks

import com.yandex.mobile.ads.video.playback.model.VideoAd

/**
 * Колбек для плеера
 */
interface VideoAdPlayerCallbacks {

    /** Сообщает о том что реклама предзагружена и готова к воспроизведению */
    fun onAdLoaded(videoAd: VideoAd)

    /** Попробывать запустить новую рекламу */
    fun onAdEndedOrSkip(videoAd: VideoAd)
}