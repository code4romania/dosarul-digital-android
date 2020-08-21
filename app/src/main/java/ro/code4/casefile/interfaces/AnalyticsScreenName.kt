package ro.code4.casefile.interfaces

import androidx.annotation.StringRes

interface AnalyticsScreenName {
    @get:ExcludeFromCodeCoverage
    @get:StringRes
    val screenName: Int
}
