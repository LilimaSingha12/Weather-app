package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//ff6f1e3aff159a14504a0f3d3482cb0e
class MainActivity : AppCompatActivity() {
private val binding: ActivityMainBinding by lazy{
    ActivityMainBinding.inflate(layoutInflater)
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Silchar")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        //val service: GitHubService = retrofit.create(GitHubService::class.java)
        val response =  retrofit.getWeatherData(cityName,"ff6f1e3aff159a14504a0f3d3482cb0e","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature = responseBody.main.temp.toString();
                    binding.temperature.text = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:  "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: $maxTemp °C"
                    binding.mintemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text =date()
                        binding.cityname.text = "$cityName"

                    changeAccordingToWeather(condition)
                //Log.d("TAG","onResponse : $temperature")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeAccordingToWeather(conditions: String) {
        when(conditions){
            "Partly CLouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            "Light Rain", "Moderate Rain", "Drizzle", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): CharSequence? {
        val sdf = SimpleDateFormat("dd MM YYYY", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}