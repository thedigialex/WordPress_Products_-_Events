package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setUpFooterFragments()
    }
    private fun setUpFooterFragments(){
        val headerController = HeaderController(findViewById(R.id.header))
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val vpAdapter = VPAdapter(supportFragmentManager, lifecycle)
        vpAdapter.addFragment(FragmentProducts(headerController), "Products")
        vpAdapter.addFragment(FragmentAccount(headerController), "Account")
        vpAdapter.addFragment(FragmentEvents(headerController), "Events")
        viewPager.adapter = vpAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            val tabView = LayoutInflater.from(tabLayout.context)
                .inflate(R.layout.tab_layout, tabLayout, false) as TextView
            tabView.text = vpAdapter.getPageTitle(position)
            tab.setCustomView(tabView)
        }.attach()
    }
}