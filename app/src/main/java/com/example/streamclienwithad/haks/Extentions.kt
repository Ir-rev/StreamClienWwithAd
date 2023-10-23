package com.example.streamclienwithad.haks

import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdBreak

private const val PRE_ROLL_KEY = "preroll"

internal fun InstreamAd.getPreRolls(): MutableList<InstreamAdBreak> {
    return adBreaks.filter { it.type == PRE_ROLL_KEY }.toMutableList()
}