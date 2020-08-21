package ro.code4.casefile.interfaces

import androidx.annotation.LayoutRes

interface Layout {
    @get:ExcludeFromCodeCoverage
    @get:LayoutRes
    val layout: Int
}
