package com.example.serviceexam.maps

import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream


class Worker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val path = "location"
    private val ref = FirebaseDatabase.getInstance().getReference(path)
    private var id = 0
    private val gsonPretty = GsonBuilder().setPrettyPrinting().create()


    override fun doWork(): Result {
        val appContext = applicationContext

        val locations = inputData.getString("Locations")
        val latitude = inputData.getString("Latitude")
        val longitude = inputData.getString("Longitude")
        val list = inputData.getStringArray("List")

        makeStatusNotification("Worker started", appContext)
        sleep()

        return try {
            if (locations.isNullOrEmpty()) {
                Timber.e("Wrong location")
                throw IllegalArgumentException("Invalid input")
            }

            //Codigo necesario para subir a firebase

            //ref.child(id.inc().toString()).push().setValue(latitude)
            //ref.child(id.inc().toString()).push().setValue(longitude)

            if (list != null) {
                save(list)
            }

            Result.success()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }

    private fun save(locations: Array<String>) {
        val root: String = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/saved_location")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val file = File(myDir, "Tracker")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            val listToSave = gsonPretty.toJson(locations)
            out.write(listToSave.toByteArray())
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}