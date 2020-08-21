package ro.code4.casefile.adapters.helper

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingScreen(@DrawableRes val imageResId: Int,
                            val titleResId: Int,
                            @StringRes val descriptionResId: Int)
