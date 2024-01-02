package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FragmentEvents(private val headerController: HeaderController) : Fragment() {
    private var rootView: View? = null
    private var slotHolder: LinearLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_events, container, false)
        fetchEvents()
        return rootView
    }
    override fun onResume() {
        super.onResume()
        headerController.updateActivityTitle("Events")
    }
    private fun createView(event: Event) {
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.event_slot, slotHolder, false)
        val eventImageView: ImageView = view.findViewById(R.id.eventImage)
        val eventNameView: TextView = view.findViewById(R.id.eventName)
        val eventDatesView: TextView = view.findViewById(R.id.eventDates)
        eventNameView.text = event.name
        eventDatesView.text = "${event.startDate} - ${event.endDate}"
        Glide.with(requireContext())
            .load(event.imageUrl)
            .into(eventImageView)
        slotHolder?.addView(view)

    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchEvents() {
        slotHolder = rootView?.findViewById(R.id.SlotHolder)
        val eventService = EventManagerService(requireContext(), getString(R.string.eventConsumerKey), getString(R.string.eventConsumerSecret))
        val progressBar: ProgressBar = rootView!!.findViewById(R.id.progressBar)
        GlobalScope.launch(Dispatchers.Main) {
            val eventsJson = withContext(Dispatchers.IO) {
                eventService.getEvents()
            }
            val jsonArray = JSONArray(eventsJson)
            val eventList = mutableListOf<Event>()
            for (i in 0 until jsonArray.length()) {
                val eventObject: JSONObject = jsonArray.getJSONObject(i)

                Log.d("EventJSON", eventObject.toString())
                val eventName = eventObject.getString("name")
                val eventImageUrl = eventObject.getString("images")
                val metaData = eventObject.getJSONObject("meta_data")
                val eventStartDate = metaData.optString("_event_start_date", "defaultStartDate")
                val eventEndDate = metaData.optString("_event_end_date", "defaultEndDate")
                val event = Event(eventName, eventImageUrl, eventStartDate, eventEndDate)
                eventList.add(event)
                createView(event)
            }
            progressBar.visibility = View.GONE
        }
    }
}