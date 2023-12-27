package thedigialex.wordpressproductandevents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    private val fragmentArrayList = ArrayList<Fragment>()
    private val fragmentTitle = ArrayList<String>()
    override fun createFragment(position: Int): Fragment {
        return fragmentArrayList[position]
    }
    override fun getItemCount(): Int {
        return fragmentArrayList.size
    }
    fun addFragment(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        fragmentTitle.add(title)
    }
    fun getPageTitle(position: Int): CharSequence {
        return fragmentTitle[position]
    }
}