package com.example.streamclienwithad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.streamclienwithad.haks.RollHolder
import com.example.streamclienwithad.haks.YandexInStreamAdPlayerCallbacks
import com.example.streamclienwithad.player.ad.SampleInstreamAdPlayer
import com.example.streamclienwithad.player.content.ContentVideoPlayer
import com.example.streamclienwithad.player.SamplePlayer
import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdBinder
import com.yandex.mobile.ads.instream.InstreamAdListener
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration
import com.yandex.mobile.ads.instream.player.ad.InstreamAdView


@UnstableApi
class MainFragment : Fragment(), YandexInStreamAdPlayerCallbacks {

    private val eventLogger = InstreamAdEventLogger()

    private var instreamAdLoader: InstreamAdLoader? = null
    private var instreamAdBinder: InstreamAdBinder? = null
    private var activePlayer: SamplePlayer? = null
    private var instreamAdPlayer: SampleInstreamAdPlayer? = null
    private var contentVideoPlayer: ContentVideoPlayer? = null

    /** Держатель для предоставление роллов */
    private val rollHolder: RollHolder = RollHolder.getInstance()

    private lateinit var instreamAdView: InstreamAdView
    private lateinit var exoPlayerView: PlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoPlayerView = view.findViewById(R.id.exo_player_view)
        instreamAdView = view.findViewById(R.id.instream_ad_view)

        initPlayer()
    }

    private fun initPlayer() {
        val contentStreamUrl = DASH_URl
        contentVideoPlayer = ContentVideoPlayer(contentStreamUrl, exoPlayerView)
        instreamAdPlayer = SampleInstreamAdPlayer(exoPlayerView, this)
        loadInstreamAd()
    }

    private fun loadInstreamAd() {
        instreamAdLoader = InstreamAdLoader(requireContext())
        instreamAdLoader?.setInstreamAdLoadListener(eventLogger)

        // Replace demo Page ID with actual Page ID
        val configuration = InstreamAdRequestConfiguration.Builder(PAGE_ID).build()
        instreamAdLoader?.loadInstreamAd(requireContext(), configuration)
    }

    private fun showInstreamAd(instreamAd: InstreamAd) {
        rollHolder.clearPreRollNamesToList()
        rollHolder.addPreRollNamesToList(instreamAd)
        instreamAdBinder = InstreamAdBinder(
            requireContext(),
            instreamAd,
            checkNotNull(instreamAdPlayer),
            checkNotNull(contentVideoPlayer)
        )
        instreamAdBinder?.apply {
            setInstreamAdListener(eventLogger)
            bind(instreamAdView)
        }
    }

    inner class InstreamAdEventLogger : InstreamAdLoadListener, InstreamAdListener {

        override fun onInstreamAdLoaded(ad: InstreamAd) {
            showInstreamAd(ad)
        }

        override fun onInstreamAdFailedToLoad(error: String) {
            Log.d("checkResult", "Instream ad failed to load: $error")
        }

        override fun onInstreamAdCompleted() {
            Log.d("checkResult", "Instream ad completed")
        }

        override fun onInstreamAdPrepared() {
            Log.d("checkResult", "Instream ad prepared")
        }

        override fun onError(error: String) {
            Log.d("checkResult", "Instream ad error: $error")
        }
    }

    companion object {
        fun newInstance() = MainFragment()

        private const val PAGE_ID = "demo-instream-vmap-yandex"
        private const val DASH_URl = "https://storage.googleapis.com/shaka-demo-assets/tos-ttml/dash.mpd"
//        private const val DASH_URl =
//            "https://f83c5e82d54143de83503cbddaf2ef50.mediatailor.eu-west-1.amazonaws.com/v1/dash/cf6421621b389b384c1fd22e51603ee95db76ae0/usp-demo/k8s/live/stable/scte35.isml/.mpd?filter=%28type!=%22textstream%22%29"

    }

    override fun onPrerollLoaded() {
        instreamAdPlayer?.playPreRoll()
    }
}