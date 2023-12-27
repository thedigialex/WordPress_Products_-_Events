package thedigialex.wordpressproductandevents

import android.view.View
import android.widget.TextView

class HeaderController(rootView: View) {

    private val activityTitleTextView: TextView = rootView.findViewById(R.id.activityTitle)

    fun updateActivityTitle(newTitle: String) {
        activityTitleTextView.text = newTitle
    }
}