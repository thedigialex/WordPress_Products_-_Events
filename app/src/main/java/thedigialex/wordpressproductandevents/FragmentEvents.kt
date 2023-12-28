package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentEvents(private val headerController: HeaderController) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_events, container, false)
        return rootView
    }
    override fun onResume() {
        super.onResume()
        headerController.updateActivityTitle("Events")
    }
}