package mobg5.g55019.mobg5_project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE mail LIKE :mailUser LIMIT 1")
    fun findByName(mailUser: String): User

    @Update
    fun update(user: User)

    @Insert
    fun insert(user: User)
}