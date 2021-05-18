package org.d3ifcool.hystorms

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import org.d3ifcool.hystorms.ui.auth.AuthActivity

class IntroductionActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransformer(AppIntroPageTransformerType.Fade)
        setProgressIndicator()
        isWizardMode = true
        addSlide(
            AppIntroFragment.newInstance(
                "Selamat Datang!",
                "Selamat datang di aplikasi Hystorms untuk pertama kalinya",
                imageDrawable = R.drawable.text_hystorms,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text),
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                "Mulai Hal yang Baru",
                "Mari mulai hijaukan sekitarmu dengan Hidroponik",
                imageDrawable = R.drawable.ic_slide_3,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                "Hystorms",
                "Hystorm adalah aplikasi monitoring tanaman Hidroponik. Hystorms membantu mengawasi dan mengelola tanaman Hidroponik dengan bantuan IoT Device",
                imageDrawable = R.drawable.ic_slide_2,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                "Media dan Kamera",
                "Hystorms memerlukan ijin untuk mengakses kamera dan media di dalam smartphone. Anda dapat memberi ijin pada lain waktu",
                imageDrawable = R.drawable.ic_slide_4,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                "Lokasi Pengguna",
                "Hystorms memerlukan ijin untuk mengetahui lokasimu.",
                imageDrawable = R.drawable.ic_slide_5,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text)
            )
        )
        addSlide(
            AppIntroFragment.newInstance(
                "Siap Menjelajah!",
                "Seluruh kebutuhan aplikasi sudah dipenuhi. Nikmati penjelajahan Hystorms!",
                imageDrawable = R.drawable.ic_slide_1,
                backgroundDrawable = R.drawable.bg_slide,
                descriptionColor = getColor(R.color.black),
                titleColor = getColor(R.color.green_text)
            )
        )
        askForPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 4, false
        )
        askForPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 5, true)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}