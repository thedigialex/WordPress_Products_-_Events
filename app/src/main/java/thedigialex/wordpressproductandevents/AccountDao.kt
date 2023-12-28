package thedigialex.wordpressproductandevents

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao {
    @Insert
    fun insert(account: Account)

    @Update
    fun update(account: Account)

    @Delete
    fun delete(account: Account)

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): List<Account>
    @Query("SELECT * FROM accounts WHERE username = :username LIMIT 1")
    fun findAccountByUsername(username: String): Account?
}