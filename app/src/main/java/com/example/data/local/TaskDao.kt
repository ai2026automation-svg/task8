package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueEpochMillis ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE type = 'RECURRING' ORDER BY dueEpochMillis ASC")
    fun getRecurringTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE type = 'ONE_TIME' ORDER BY dueEpochMillis ASC")
    fun getOneTimeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks WHERE task = :task AND doerName = :doer AND frequency = :freq AND LOWER(assignedByEmail) = LOWER(:assignerEmail)")
    suspend fun deleteRecurringSeries(task: String, doer: String, freq: String, assignerEmail: String): Int

    @Query("UPDATE tasks SET task = :newTask, doerName = :newDoer, designation = :newDesignation WHERE task = :oldTask AND doerName = :oldDoer AND frequency = :freq AND LOWER(assignedByEmail) = LOWER(:assignerEmail) AND (completedDate = '' OR completedDate IS NULL)")
    suspend fun updateRecurringSeries(oldTask: String, oldDoer: String, freq: String, assignerEmail: String, newTask: String, newDoer: String, newDesignation: String): Int

    @Query("DELETE FROM tasks WHERE type = :type")
    suspend fun clearTasksByType(type: String)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY rowNum ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET role = :role WHERE rowNum = :rowNum")
    suspend fun updateUserRole(rowNum: Int, role: String)

    @Query("DELETE FROM users WHERE rowNum = :rowNum")
    suspend fun deleteUserByRow(rowNum: Int)

    @Query("DELETE FROM users WHERE LOWER(email) = LOWER(:email)")
    suspend fun deleteUserByEmail(email: String)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
