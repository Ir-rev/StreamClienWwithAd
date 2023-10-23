/*
 * This file is a part of the Yandex Advertising Network
 *
 * Version for Android (C) 2022 YANDEX
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at https://legal.yandex.com/partner_ch/
 */

package com.example.streamclienwithad.player.content

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.ui.PlayerView
import com.example.streamclienwithad.player.SamplePlayer
import com.example.streamclienwithad.player.creator.MediaSourceCreator
import com.yandex.mobile.ads.instream.player.content.VideoPlayer
import com.yandex.mobile.ads.instream.player.content.VideoPlayerListener

@UnstableApi
class ContentVideoPlayer(
    private val dashUrl: String,
    private val exoPlayerView: PlayerView
) : VideoPlayer, SamplePlayer {

    private val context = exoPlayerView.context
    private var exoPlayer: SimpleExoPlayer? = null

    private var videoPlayerListener: VideoPlayerListener? = null

    override fun isPlaying() = exoPlayer!!.isPlaying

    override fun resume() {
        exoPlayer!!.playWhenReady = true
    }

    override fun pause() {
        exoPlayer!!.playWhenReady = false
    }

    override fun prepareVideo() {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayer"))
        val uri = Uri.parse(dashUrl)
        val mediaItem = MediaItem.Builder().apply {
            setUri(uri)
            setMediaId("2")
        }.build()
        val dashMediaSource = DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        val trackSelector: TrackSelector = DefaultTrackSelector(context)

        val newPlayer =
            SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()
        exoPlayer = newPlayer
        newPlayer.apply {
            playWhenReady = true
            addListener(ContentPlayerPrepareListener())
            prepare(dashMediaSource)
        }
        exoPlayer!!.addListener(ContentPlayerEventsListener())
    }

    private var value = 0L

    override fun getVideoPosition(): Long {
        val currentValue = value
        value += 100
        return currentValue
    }

    override fun getVideoDuration() = exoPlayer!!.duration

    override fun getVolume() = exoPlayer!!.volume

    override fun pauseVideo() {
        exoPlayerView.useController = false
        pause()
    }

    override fun resumeVideo() {
        exoPlayerView.player = exoPlayer
        exoPlayerView.useController = true
        resume()
    }

    override fun setVideoPlayerListener(playerListener: VideoPlayerListener?) {
        videoPlayerListener = playerListener
    }

    fun release() {
        exoPlayer!!.release()
    }

    private inner class ContentPlayerEventsListener : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                videoPlayerListener?.onVideoResumed()
            } else {
                videoPlayerListener?.onVideoPaused()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                onVideoCompleted()
            }
        }

        private fun onVideoCompleted() {
            exoPlayerView.useController = false
            videoPlayerListener?.onVideoCompleted()
        }

        override fun onPlayerError(error: PlaybackException) {
            videoPlayerListener?.onVideoError()
        }
    }

    private inner class ContentPlayerPrepareListener : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                videoPlayerListener?.onVideoPrepared()
                exoPlayer!!.removeListener(this)
            }
        }
    }
}
