package org.d3ifcool.hystorms.ui.main.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.databinding.FragmentNotificationSettingBinding
import org.d3ifcool.hystorms.receiver.ScheduleAlarmReceiver
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class NotificationSettingFragment : Fragment(R.layout.fragment_notification_setting) {
    private lateinit var binding: FragmentNotificationSettingBinding

    @Inject
    lateinit var alarmReceiver: ScheduleAlarmReceiver
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationSettingBinding.bind(view)
        val navHostFragment = NavHostFragment.findNavController(this)
        val appBarConfiguration = AppBarConfiguration(navHostFragment.graph)
        binding.apply {
            appBar.toolbar.setupWithNavController(navHostFragment, appBarConfiguration)
            switchMaterial.isChecked =
                alarmReceiver.isAlarmSet(requireContext(), ScheduleAlarmReceiver.INT_ID_REPEATING)
            switchMaterial.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val startTime = Calendar.getInstance()
                    startTime.set(Calendar.HOUR_OF_DAY, 9)
                    startTime.set(Calendar.MINUTE, 30)
                    alarmReceiver.setDailyAlarm(
                        requireContext(),
                        startTime,
                        "Jangan lupakan Hidroponikmu dengan menggunakan Hystorms!",
                        "Pengingat Harian Hystorms"
                    )
                } else {
                    alarmReceiver.cancelAlarm(
                        requireContext(),
                        ScheduleAlarmReceiver.INT_ID_REPEATING,
                        "Pengingat Harian Hystorms"
                    )
                }
            }
        }
    }
}