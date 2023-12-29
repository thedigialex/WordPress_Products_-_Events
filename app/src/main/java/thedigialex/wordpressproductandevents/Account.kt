package thedigialex.wordpressproductandevents

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    var token: String,
    val name: String,
    var loggedIn: Boolean
)
