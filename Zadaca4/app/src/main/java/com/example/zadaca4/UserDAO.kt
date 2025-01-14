package com.example.zadaca4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) AND password = :password LIMIT 1")
    fun getUser(email: String, password: String): User?

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>
}
