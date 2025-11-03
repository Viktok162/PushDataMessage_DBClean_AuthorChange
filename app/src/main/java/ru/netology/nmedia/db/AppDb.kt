package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import android.util.Log


@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object{
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized (this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(context, AppDb::class.java, "app.db")
            .allowMainThreadQueries() // permission access database on the main thread
            .fallbackToDestructiveMigration(false) // delete database if change db structure
            .build()
            .also {
                Log.d("DB_Debug", "New database created")
            }

                // Метод для сброса БД
        fun clearAndRecreate(context: Context) {
            synchronized(this) {
                instance?.let { db ->
                    db.close()  // Закрываем соединение
                    Log.d("DB_Debug", "Database instance closed")
                }
                instance = null  // Сбрасываем ссылку
                Log.d("DB_Debug", "Database instance set to null")

                // Удаляем файл БД вручную
                val dbFile = context.getDatabasePath("app.db")
//                if (dbFile.exists()) {
//                    dbFile.delete()
//                }
                if (dbFile.exists()) {
                    val deleted = dbFile.delete()
                    Log.d("DB_Debug", "Main DB file deleted: $deleted")
                }

                // Удаляем сопутствующие файлы (shm и wal)

//                val shmFile = context.getDatabasePath("app.db-shm")
//                if (shmFile.exists()) shmFile.delete()
//                val walFile = context.getDatabasePath("app.db-wal")
//                if (walFile.exists()) walFile.delete()

                val shmFile = context.getDatabasePath("app.db-shm")
                if (shmFile.exists()) {
                    val shmDeleted = shmFile.delete()
                    Log.d("DB_Debug", "SHM file deleted: $shmDeleted")
                }
                val walFile = context.getDatabasePath("app.db-wal")
                if (walFile.exists()) {
                    val walDeleted = walFile.delete()
                    Log.d("DB_Debug", "WAL file deleted: $walDeleted")
                }
                Log.d("DB_Debug", "DB cleared and ready for recreation")
                // Пересоздаём БД (instance будет null, так что вызов getInstance() создаст новую)
            }
        }
    }
}
