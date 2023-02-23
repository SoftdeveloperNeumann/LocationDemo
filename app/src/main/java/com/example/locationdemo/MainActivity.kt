package com.example.locationdemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.locationdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var manager: LocationManager
    var provider: String? = null
    val REQUEST_CODE = 321

    val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            binding.tvOutput.append("Neue Location -> Breite: ${location.latitude}, Länge: ${location.longitude}\n")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        } else {
            doIt()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CODE == requestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            doIt()
        }
    }

    override fun onResume() {
        super.onResume()
        val criteria = Criteria().apply {
            powerRequirement = Criteria.POWER_LOW
            accuracy = Criteria.ACCURACY_FINE
        }
        provider = manager.getBestProvider(criteria, true)

        binding.tvOutput.append("\nVerwendet wird: $provider\n")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(provider!!, 10000, 0F, locationListener)
        }
    }

    override fun onPause() {
        super.onPause()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            manager.removeUpdates(locationListener)
        }
    }

    private fun doIt() {
        val providers = manager.allProviders
        providers.forEach { name ->
            val provider = manager.getProvider(name)
            with(binding.tvOutput) {
                append("Name: $name --> isEnabled: ${manager.isProviderEnabled(name)}\n")
                append("requires Cell: ${provider?.requiresCell()}\n")
                append("requires Network: ${provider?.requiresNetwork()}\n")
                append("requires Sattelite: ${provider?.requiresSatellite()}\n")
            }
        }

        val criteria = Criteria().apply {
            powerRequirement = Criteria.POWER_LOW
            accuracy = Criteria.ACCURACY_FINE
        }
        provider = manager.getBestProvider(criteria, true)

        binding.tvOutput.append("\nVerwendet wird: $provider\n")

//        val location = Location(provider)
//            location.longitude = Location.convert("49:12")
//            location.latitude = Location.convert("11:5")


    }
}