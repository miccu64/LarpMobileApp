package com.example.inzynierka_apka

import Localization
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.timerTask

data class Params (val Id: String, val Latitude: Double, val Longitude: Double)

class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null
    //HTTPS nie zadziala na localhoscie - moze na zewnatrz pojdzie? pasobaloby xD
    //w AndroidManifest.xml jest dodana linia i moze nie dzialac cos przez nia
    private val server: String = "http://192.168.0.10:45455"
    private lateinit var hubConnection: HubConnection
    private var token: String? = null
    private var localization: Localization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.textView)
        hubConnection = HubConnectionBuilder.create("$server/gamehub").build()
        //TUTAAAAJ
        hubConnection.on(
            "ErrorMessage",
            { message: String -> //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                textView!!.text = "response :  " + message
                Log.d("TAG", message)},
            String::class.java
        )
        hubConnection.on(
            "SuccessMessage",
            { message: String -> //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                textView!!.text = "response :  " + message
                Log.d("TAG", message)},
                // },
            String::class.java
        )
        hubConnection.on(
            "SaveToken",
            { message: String -> //Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                token = message
                textView!!.text = "response :  " + message
                Log.d("TAG", message)

                print(token)},
            // },
            String::class.java
        )
        //ignores every certificate of SSL!!!!!!!!!!!!!!
        NukeSSLCerts.nuke()

        localization = Localization(this, this)


    }

    private fun <T> addToQueue(request: Request<T>) {
        QueueSingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun register(email: String, name: String, password: String) {
        hubConnection.invoke("RegisterNewUser", email, name, password)
    }

    private fun login(email: String, password: String) {
        hubConnection.invoke("Login", email, password)
    }

    private fun createRoom(roomName: String, password: String) {
        hubConnection.invoke("CreateRoom", roomName, password, token)
    }

    private fun joinRoom(roomName: String, password: String) {
        hubConnection.invoke("JoinRoom", roomName, password, token)
    }

    private fun updateLocation(roomName: String) {

        val latitude: Double = Localization.latitude // latitude
        val longitude: Double = Localization.longitude // latitude
        hubConnection.invoke("UpdateLocation", Localization.latitude, Localization.longitude, token)
    }

    fun showLocalization(view: View) {
        while (hubConnection.connectionState != HubConnectionState.CONNECTED) {
            hubConnection.start()
            sleep(1000L)
        }


        val resp = hubConnection.connectionState
        Log.d("TAG", resp.toString())

        //register("nowy2", "nowy2", "nowy2")
        login("nowy2", "nowy2")
        Timer().schedule(timerTask {
            //createRoom("idz3", "idz3")
            joinRoom("idz3", "idz3")
        }, 1000)
        updateLocation("aaa")





/*
        val par = Params("Dzialaj", latitude, longitude)
        if (hubConnection.connectionState == HubConnectionState.CONNECTED){
            //hubConnection.invoke("UpdateLocation", par, "aaa", 0)
            val res = hubConnection.invoke("CreateRoom", "aaa", "aaa", token)
            print(res)
        } else hubConnection.start()*/



    }

}