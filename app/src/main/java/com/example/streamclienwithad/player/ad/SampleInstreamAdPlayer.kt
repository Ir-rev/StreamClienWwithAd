/*
 * This file is a part of the Yandex Advertising Network
 *
 * Version for Android (C) 2022 YANDEX
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at https://legal.yandex.com/partner_ch/
 */

package com.example.streamclienwithad.player.ad

import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.streamclienwithad.haks.RollHolder
import com.example.streamclienwithad.haks.VideoAdPlayerCallbacks
import com.example.streamclienwithad.haks.YandexInStreamAdPlayerCallbacks
import com.example.streamclienwithad.player.SamplePlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.video.playback.model.VideoAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@UnstableApi
class SampleInstreamAdPlayer(
    private val exoPlayerView: PlayerView,
    private val yandexInStreamAdPlayerCallbacks: YandexInStreamAdPlayerCallbacks,
) : InstreamAdPlayer, SamplePlayer, VideoAdPlayerCallbacks {

    private val adPlayers = mutableMapOf<VideoAd, SampleVideoAdPlayer>()

    private var currentVideoAd: VideoAd? = null
    private var adPlayerListener: InstreamAdPlayerListener? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** Держатель для предоставление роллов */
    private val rollHolder: RollHolder = RollHolder.getInstance()

    override fun setInstreamAdPlayerListener(instreamAdPlayerListener: InstreamAdPlayerListener?) {
        adPlayerListener = instreamAdPlayerListener
        adPlayers.values.forEach { it.setInstreamAdPlayerListener(instreamAdPlayerListener) }
    }

    override fun prepareAd(videoAd: VideoAd) {
        val videoAdPlayer = SampleVideoAdPlayer(videoAd, exoPlayerView, this)
        adPlayers[videoAd] = videoAdPlayer

        videoAdPlayer.setInstreamAdPlayerListener(adPlayerListener)
        videoAdPlayer.prepareAd()
    }

    override fun playAd(videoAd: VideoAd) {
        return
    }

    /** старт воспроизведение преролла */
    fun playPreRoll() {
        val videoAd = rollHolder.getPreRollOrNull() ?: return
        scope.launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                currentVideoAd = videoAd
                adPlayers[videoAd]?.playAd()
                adPlayerListener?.onAdStarted(videoAd)
                adPlayerListener?.onAdResumed(videoAd)
            }
        }
    }

    /** старт воспроизведение мидрола */
    fun playMidRoll() {
        val videoAd = rollHolder.getMidRollOrNull() ?: return
        currentVideoAd = videoAd
        adPlayers[videoAd]?.playAd()
    }

    override fun pauseAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.pauseAd()
    }

    override fun resumeAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.resumeAd()
    }

    override fun stopAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.stopAd()
    }

    override fun skipAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.skipAd()
    }

    override fun setVolume(videoAd: VideoAd, volume: Float) {
        adPlayers[videoAd]?.setVolume(volume)
    }

    override fun getVolume(videoAd: VideoAd): Float {
        return adPlayers[videoAd]?.getVolume() ?: DEFAULT_VOLUME
    }

    override fun getAdDuration(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adDuration ?: 0
    }

    override fun getAdPosition(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adPosition ?: 0
    }

    override fun isPlayingAd(videoAd: VideoAd): Boolean {
        return adPlayers[videoAd]?.isPlayingAd ?: false
    }

    override fun releaseAd(videoAd: VideoAd) {
//        if (videoAd == currentVideoAd) {
//            currentVideoAd = null
//        }
//
//        adPlayers[videoAd]?.let { videoAdPlayer ->
//            videoAdPlayer.setInstreamAdPlayerListener(null)
//            videoAdPlayer.releaseAd()
//        }
//
//        adPlayers.remove(videoAd)
    }

    fun release() {
        adPlayers.values.forEach(SampleVideoAdPlayer::releaseAd)
        currentVideoAd = null
    }

    override fun isPlaying(): Boolean {
        return adPlayers[currentVideoAd]?.isPlayingAd ?: false
    }

    override fun resume() {
        adPlayers[currentVideoAd]?.resumeAd()
    }

    override fun pause() {
        adPlayers[currentVideoAd]?.pauseAd()
    }

    private var isFirstPrerollCalled = false
    private var lastAd: VideoAd? = null

    override fun onAdLoaded(videoAd: VideoAd) {
        rollHolder.addRollToLoadedList(videoAd)
        if (rollHolder.isPreRoll(videoAd) && !isFirstPrerollCalled) {
            isFirstPrerollCalled = true
            yandexInStreamAdPlayerCallbacks.onPrerollLoaded()
        } else {
            // TODO
        }
    }

    override fun onAdEndedOrSkip(videoAd: VideoAd) {
        if (lastAd == videoAd) return
        lastAd = videoAd
        val preRoll = rollHolder.isPrerollPrepared()
        if (preRoll) {
            playPreRoll()
        } else {
            scope.launch {
                delay(10000)
                withContext(Dispatchers.Main) {
                    playMidRoll()
                }
            }
        }
    }

    private companion object {
        private const val DEFAULT_VOLUME = 0f
    }
}
