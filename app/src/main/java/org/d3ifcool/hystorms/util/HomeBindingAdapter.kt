package org.d3ifcool.hystorms.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import org.d3ifcool.hystorms.R

@BindingAdapter("bindWeatherShimmer")
fun bindWeatherShimmer(shimmer: ShimmerFrameLayout, viewState: ViewState?) {
    when (viewState) {
        ViewState.NOTHING -> {
        }
        ViewState.ERROR, ViewState.SUCCESS -> {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
        }
        ViewState.LOADING -> {
            shimmer.startShimmer()
        }
    }
}

@BindingAdapter("bindVisibilityWeatherContainer")
fun bindVisibilityWeatherContainer(view: View, viewState: ViewState?) {
    when (viewState) {
        ViewState.ERROR, ViewState.SUCCESS -> {
            view.visibility = View.VISIBLE
        }
        ViewState.NOTHING, ViewState.LOADING -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("bindBackgroundWeatherContainer")
fun bindBackgroundWeatherContainer(view: View, idMain: Int) {
    when (idMain) {
        200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> view.setBackgroundResource(R.drawable.il_thunderstorm)
        300, 301, 302, 310, 311, 312, 313, 314, 321 -> view.setBackgroundResource(
            R.drawable.il_rain
        )
        520, 521, 522, 531 -> view.setBackgroundResource(R.drawable.il_shower_rain)
        800 -> view.setBackgroundResource(R.drawable.il_sunny)
        801 -> view.setBackgroundResource(R.drawable.il_few_clouds)
        802 -> view.setBackgroundResource(R.drawable.il_scattered_cloud)
        803, 804 -> view.setBackgroundResource(R.drawable.il_broken_cloud)
    }
}

@BindingAdapter("bindMainTempText")
fun bindMainTempText(textView: TextView, temp: Long) {
    textView.text = textView.context.getString(R.string.text_temp, temp)
}

@BindingAdapter("bindFeelsLikeTempText")
fun bindFeelsLikeTempText(textView: TextView, temp: Long) {
    textView.text = textView.context.getString(R.string.text_feels_like, temp)
}

@BindingAdapter("bindHumidityText")
fun bindHumidityText(textView: TextView, humidity: Long) {
    textView.text = textView.context.getString(R.string.text_humidity, humidity)
}

@BindingAdapter("bindWindSpeedText")
fun bindWindSpeedText(textView: TextView, windSpeed: Long) {
    textView.text = textView.context.getString(R.string.text_wind_speed, windSpeed)
}