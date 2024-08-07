package com.jchc.practica2.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.jchc.practica2.R
import com.jchc.practica2.data.OrgRepository
import com.jchc.practica2.data.remote.RetrofitHelper
import com.jchc.practica2.data.remote.model.OrgDto
import com.jchc.practica2.databinding.ActivityMainBinding
import com.jchc.practica2.ui.fragments.OrgListFragment
import com.jchc.practica2.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var repository: OrgRepository
    private lateinit var retrofit: Retrofit

    private lateinit var binding: ActivityMainBinding

    //para la canción de fondo
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!isInternetAvailable(this)){
            showNoInternetDialog()
        } else

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrgListFragment())
                .commit()
        }

        retrofit = RetrofitHelper().getRetrofit()
        repository = OrgRepository(retrofit)

        lifecycleScope.launch{
            val call: Call<List<OrgDto>> = repository.getOrgs("org/org_list")
            //val call: Call<List<OrgDto>> = repository.getOrgs("v1/maps")

            call.enqueue(object: Callback<List<OrgDto>> {
                override fun onResponse(p0: Call<List<OrgDto>>, response: Response<List<OrgDto>>) {
                    //Respuesta del server
                    Log.d(Constants.LOGTAG, "Respuesta recibida: ${response.body()}")
                }

                override fun onFailure(p0: Call<List<OrgDto>>, error: Throwable) {
                    //manejo de error
                    Toast.makeText(
                        this@MainActivity,
                        "Error en la conexión: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (!this::mediaPlayer.isInitialized){
            mediaPlayer = MediaPlayer.create(this, R.raw.valsong )
        }
        if (mediaPlayer.isPlaying){
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

     private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        //builder.setTitle("No Internet Connection")
        builder.setTitle(getString(R.string.noInternet))
        //builder.setMessage("Please check your internet connection and try again.")
        builder.setMessage(getString(R.string.checkInternet))
        builder.setCancelable(false)
        builder.setPositiveButton("Exit") { dialog, which ->
            // Retry logic
            finish()
            restartApp()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun restartApp(){
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}