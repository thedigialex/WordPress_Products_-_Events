package thedigialex.wordpressproductandevents

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAccount(private val headerController: HeaderController, private val context: Context, private val account: Account) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_account, container, false)
        val logOutButton = rootView.findViewById<Button>(R.id.logOutButton)
        logOutButton.setOnClickListener {
            logOut()
        }
        return rootView
    }
    override fun onResume() {
        super.onResume()
        headerController.updateActivityTitle("Account")
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun logOut(){
        val db = AppDatabase.getDatabase(context)
        val accountDao = db.accountDao()
        GlobalScope.launch(Dispatchers.IO) {
            account.loggedIn = false
            CoroutineScope(Dispatchers.IO).launch {
                accountDao.update(account)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
