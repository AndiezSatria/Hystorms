package org.d3ifcool.hystorms.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import org.d3ifcool.hystorms.R

@BindingAdapter("weatherIconImage")
fun weatherIconImage(imageView: ImageView, idIcon: String?) {
    idIcon?.let {
        val url = "http://openweathermap.org/img/wn/${it}@2x.png"
        val uri = url.toUri().buildUpon().scheme("http").build()
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)

    }
}

@BindingAdapter("bindShimmer")
fun bindShimmer(shimmer: ShimmerFrameLayout, viewState: ViewState?) {
    when (viewState) {
        ViewState.NOTHING -> {
        }
        ViewState.ERROR, ViewState.SUCCESS -> {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
        }
        ViewState.LOADING -> {
            shimmer.visibility = View.VISIBLE
            shimmer.startShimmer()
        }
    }
}

@BindingAdapter("bindVisibilityDataContainer")
fun bindVisibilityDataContainer(view: View, viewState: ViewState?) {
    when (viewState) {
        ViewState.SUCCESS -> {
            view.visibility = View.VISIBLE
        }
        ViewState.ERROR, ViewState.NOTHING, ViewState.LOADING -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("bindVisibilityErrorWeather")
fun bindVisibilityErrorWeather(view: View, viewState: ViewState?) {
    when (viewState) {
        ViewState.SUCCESS, ViewState.LOADING, ViewState.NOTHING -> {
            view.visibility = View.GONE
        }
        ViewState.ERROR -> {
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("bindBackgroundWeatherContainer")
fun bindBackgroundWeatherContainer(view: View, idMain: Int) {
    when (idMain) {
        200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> view.background =
            ContextCompat.getDrawable(
                view.context,
                R.drawable.il_thunderstorm
            )

        500, 501, 502, 503, 504 -> view.background =
            ContextCompat.getDrawable(
                view.context,
                R.drawable.il_rain
            )

        520, 521, 522, 531, 300, 301, 302, 310, 311, 312, 313, 314, 321 -> view.background =
            ContextCompat.getDrawable(
                view.context,
                R.drawable.il_shower_rain
            )
        800 -> view.background = ContextCompat.getDrawable(
            view.context,
            R.drawable.il_sunny
        )
        801 -> view.background = ContextCompat.getDrawable(
            view.context,
            R.drawable.il_few_clouds
        )
        802 -> view.background = ContextCompat.getDrawable(
            view.context,
            R.drawable.il_scattered_cloud
        )
        803, 804 -> view.background = ContextCompat.getDrawable(
            view.context,
            R.drawable.il_broken_cloud
        )
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

@BindingAdapter("bindAmountText")
fun bindAmountText(textView: TextView, amount: Int) {
    textView.text = textView.context.getString(R.string.text_amount, amount)
}

@BindingAdapter("bindProfileUrl")
fun bindProfileUrl(imageView: ImageView, url: String?) {
    if (url != null) {
        val uri = url.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .load(R.drawable.ic_account)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    }
}