package thedigialex.wordpressproductandevents

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity  : AppCompatActivity() {
    private var loggedInAccount: Account? = null
    private val cart: MutableList<Product> = mutableListOf()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val username = intent.getStringExtra("USERNAME")

        if (username != null) {
            val db = AppDatabase.getDatabase(this)
            val accountDao = db.accountDao()
            GlobalScope.launch(Dispatchers.IO) {
                loggedInAccount = accountDao.findAccountByUsername(username)
                loggedInAccount?.let {
                    withContext(Dispatchers.Main) {
                        setUpHeaderAndFooter(it)
                    }
                }
            }
        }
    }
    private fun setUpHeaderAndFooter(account: Account){
        val headerController = HeaderController(findViewById(R.id.header), findViewById(R.id.cartView), cart)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val vpAdapter = VPAdapter(supportFragmentManager, lifecycle)
        vpAdapter.addFragment(FragmentProducts(headerController, cart), "Products")
        vpAdapter.addFragment(FragmentEvents(headerController), "Events")
        vpAdapter.addFragment(FragmentAccount(headerController, this, account), "Account")
        viewPager.adapter = vpAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            val tabView = LayoutInflater.from(tabLayout.context)
                .inflate(R.layout.tab_layout, tabLayout, false) as TextView
            tabView.text = vpAdapter.getPageTitle(position)
            tab.setCustomView(tabView)
        }.attach()
    }
}