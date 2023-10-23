package com.example.streamclienwithad.player.content

import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener

@UnstableApi
class ContentVideoPlayerListener(
    private val contentVideoPlayer: ContentVideoPlayer,
) : VideoPlayerListener {

    override fun onVideoPrepared() {
        contentVideoPlayer.resumeVideo()
        Log.d("checkResult", "onVideoPrepared: ")
    }

    override fun onVideoCompleted() {
        Log.d("checkResult", "onVideoCompleted: ")
    }

    override fun onVideoPaused() {
        Log.d("checkResult", "onVideoPaused: ")
    }

    override fun onVideoError() {
        Log.d("checkResult", "onVideoError: ")
    }

    override fun onVideoResumed() {
        Log.d("checkResult", "onVideoResumed: ")
    }
}
