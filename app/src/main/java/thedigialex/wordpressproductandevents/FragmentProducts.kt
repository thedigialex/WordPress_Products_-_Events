package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class FragmentProducts(private val headerController: HeaderController) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_products, container, false)
        headerController.updateActivityTitle("Products")
        createView(rootView)
        return rootView
    }
    fun createView(rootView: View) {
        val slotHolder: LinearLayout = rootView.findViewById(R.id.SlotHolder)
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.product_slot, slotHolder, false)
        slotHolder.addView(view)
    }

}