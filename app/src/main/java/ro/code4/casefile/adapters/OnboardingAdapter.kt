package ro.code4.casefile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_onboarding_tutorial.view.*
import ro.code4.casefile.R
import ro.code4.casefile.adapters.helper.OnboardingScreen
import ro.code4.casefile.adapters.helper.ViewHolder
import ro.code4.casefile.helper.toHtml
import java.util.*

class OnboardingAdapter(
    private val context: Context,
    private val screens: ArrayList<OnboardingScreen>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewResId = R.layout.item_onboarding_tutorial
        return ViewHolder(LayoutInflater.from(context).inflate(viewResId, parent, false))
    }

    override fun getItemCount(): Int = screens.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val screen = screens[position]
        holder.itemView.descriptionOnboarding.text =
            context.getString(screen.descriptionResId)
                .toHtml()
        with(screen) {
            holder.itemView.titleOnboarding.text = context.getString(titleResId)
            holder.itemView.icOnboarding.setImageResource(imageResId)
        }
    }

    interface OnLanguageChangedListener {
        fun onLanguageChanged(locale: Locale)
    }
}
