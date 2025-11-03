package ru.netology.nmedia

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.room.Room
import ru.netology.nmedia.db.AppDb

class NMediaApplication : Application() {

//    val database: AppDb by lazy {
//        Room.databaseBuilder(this, AppDb::class.java, "app.db")
//            .allowMainThreadQueries()
//            .fallbackToDestructiveMigration(false)  // Можно поменять на true для автосброса при изменении версии
//            .build()
//    }

        val database by lazy { AppDb.getInstance(this) }


//    override fun onCreate() {
//        super.onCreate()
//        // Сброс БД при каждом запуске (для тестирования)
//        Log.d("DB_Debug", "Starting clearAndRecreate ALWAYS")
//        AppDb.clearAndRecreate(this)  // Это очистить и пересоздаст БД заново
//    }

    override fun onCreate() {
        super.onCreate()
        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            // сбрасываем БД
            // Log.d("DB_Debug", "Start clearAndRecreate")
            // AppDb.clearAndRecreate(this)
            Log.d("DB_Debug", "Prohibition of Start clearAndRecreate")
        }
    }
}
