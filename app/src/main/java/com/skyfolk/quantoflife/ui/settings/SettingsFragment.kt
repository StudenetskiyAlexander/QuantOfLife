package com.skyfolk.quantoflife.ui.settings

import android.app.Activity
import android.app.DownloadManager
import android.app.TimePickerDialog
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.databinding.SettingsFragmentBinding
import com.skyfolk.quantoflife.ui.onboarding.OnBoardingActivity
import com.skyfolk.quantoflife.utils.showConfirmDialog
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Calendar


class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)

        viewModel.toastState.observe(viewLifecycleOwner) { toast ->
            val text =
                when (toast) {
                    is SettingsFragmentToast.ImportComplete -> getString(
                        toast.textResourceId,
                        toast.eventsImported,
                        toast.eventsTypeImported
                    )
                    else -> getString(toast.textResourceId)
                }
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        viewModel.dayStartTime.observe(viewLifecycleOwner) {
            val hour: String =
                if (it[Calendar.HOUR_OF_DAY] < 10) "0" + it[Calendar.HOUR_OF_DAY] else it[Calendar.HOUR_OF_DAY].toString()
            val minute: String =
                if (it[Calendar.MINUTE] < 10) "0" + it[Calendar.MINUTE] else it[Calendar.MINUTE].toString()
            binding.startHour.text =
                resources.getString(R.string.settings_set_day_start_time_current, hour, minute)
        }

        viewModel.downloadFile.observe(viewLifecycleOwner) { file ->
            val downloadManager =
                requireContext().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.addCompletedDownload(
                file.name,
                file.name,
                true,
                "text/plain",
                file.path,
                file.length(),
                true
            )
        }

        binding.clearDbButton.setOnClickListener {
            requireContext().showConfirmDialog(
                title = resources.getString(R.string.clear_database_title),
                message = resources.getString(R.string.clear_database_message),
                positiveButtonTitle = resources.getString(R.string.delete),
                negativeButtonTitle = resources.getString(R.string.cancel)
            ) {
                viewModel.clearDatabase()
            }
        }

        binding.clearEventsButton.setOnClickListener {
            requireContext().showConfirmDialog(
                title = resources.getString(R.string.clear_events_title),
                message = resources.getString(R.string.clear_events_message),
                positiveButtonTitle = resources.getString(R.string.delete),
                negativeButtonTitle = resources.getString(R.string.cancel)
            ) {
                viewModel.clearEvents()
            }
        }

        binding.exportDbButton.setOnClickListener {

            val exportRealmPATH: File? = requireContext().getExternalFilesDir(null)
            val exportRealmFileName = "qol_backup.realm"
            val file = File(exportRealmPATH, exportRealmFileName)
            viewModel.saveDBToFile(file)

            if (file.exists()) {
                val intentShareFile = Intent(Intent.ACTION_SEND)
                intentShareFile.type = "application/pdf"
                val bmpUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.skyfolk.quantoflife.fileprovider",
                    file
                )
                intentShareFile.putExtra(Intent.EXTRA_STREAM, bmpUri)
                intentShareFile.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject))
                intentShareFile.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(
                        R.string.export_text,
                        System.currentTimeMillis().toDateWithoutHourAndMinutes()
                    )
                )

                startActivity(
                    Intent.createChooser(
                        intentShareFile,
                        getString(R.string.export_title)
                    )
                )
            }
        }
        binding.importDbButton.setOnClickListener {
            openFile()
//            viewModel.importAllEventsAndQuantsFromFile(requireContext())
        }

        binding.submitCategoryNamesButton.setOnClickListener {
            startActivity(Intent(requireContext(), OnBoardingActivity::class.java))
        }

        binding.submitStartHour.setOnClickListener {
            TimePickerDialog(requireContext(), onTimeSelected, 0, 0, true)
                .show()
        }

        return binding.root
    }

    val pickFileReqCode = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == pickFileReqCode && resultCode == Activity.RESULT_OK) {
            if (resultData != null && resultData.data != null) {
                viewModel.importAllEventsAndQuantsFromFile(requireContext(), resultData.data!!)
            } else {
//                    Log.d("File uri not found {}")
            }
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
        }

        startActivityForResult(intent, pickFileReqCode)
    }

    private val onTimeSelected = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        val result = ((hourOfDay * 60 * 60 * 1000) + (minute * 60 * 1000)).toLong()
        viewModel.setStartDayTime(result)
    }

    sealed class SettingsFragmentToast(val textResourceId: Int) {
        object DatabaseCleared : SettingsFragmentToast(R.string.settings_database_cleared)
        object EventsCleared : SettingsFragmentToast(R.string.settings_events_cleared)
        object DatabaseExported : SettingsFragmentToast(R.string.settings_database_exported)
        data class ImportComplete(val eventsImported: Int, val eventsTypeImported: Int) :
            SettingsFragmentToast(R.string.settings_import_result)
    }
}