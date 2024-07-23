package com.skyfolk.quantoflife

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.skyfolk.quantoflife.db.DBInteractor
import com.skyfolk.quantoflife.db.EventsStorageInteractor
import com.skyfolk.quantoflife.db.GoalStorageInteractor
import com.skyfolk.quantoflife.db.IGoalStorageInteractor
import com.skyfolk.quantoflife.db.IQuantsStorageInteractor
import com.skyfolk.quantoflife.db.QuantsStorageInteractor
import com.skyfolk.quantoflife.import.ImportInteractor
import com.skyfolk.quantoflife.mapper.QuantBaseToCreateQuantTypeMapper
import com.skyfolk.quantoflife.mapper.TimeIntervalToPeriodInMillisMapper
import com.skyfolk.quantoflife.settings.SettingsInteractor
import com.skyfolk.quantoflife.ui.feeds.vm.FeedsViewModel
import com.skyfolk.quantoflife.ui.goals.GoalToPresentationMapper
import com.skyfolk.quantoflife.ui.now.NowViewModel
import com.skyfolk.quantoflife.ui.now.create.CreateEventViewModel
import com.skyfolk.quantoflife.ui.onboarding.OnBoardingViewModel
import com.skyfolk.quantoflife.ui.settings.SettingsViewModel
import com.skyfolk.quantoflife.ui.statistic.StatisticViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val mapperModule = module {
            single { TimeIntervalToPeriodInMillisMapper(get()) }
            single { QuantBaseToCreateQuantTypeMapper() }
            single { GoalToPresentationMapper(androidContext(), get(), get(), get(), get()) }
        }

        val storageModule = module {
            single { DBInteractor(get()) }
            single<IQuantsStorageInteractor> { QuantsStorageInteractor(get()) }
            single { EventsStorageInteractor(get()) }
            single<IGoalStorageInteractor> { GoalStorageInteractor(get()) }
            single { SettingsInteractor(androidContext()) }
            single<IDateTimeRepository> { DateTimeRepository() }
            single {
                ImportInteractor(
                    get(),
                    get(),
                    get(),
                    androidContext().resources.openRawResource(R.raw.qol_base)
                )
            }
        }

        val viewModelModule = module {
            viewModel { NowViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
            viewModel { SettingsViewModel(get(), get(), get(), get()) }
            viewModel { FeedsViewModel(get(), get(), get(), get()) }
            viewModel { StatisticViewModel(get(), get(), get(), get()) }
            viewModel { OnBoardingViewModel(get()) }
            viewModel { CreateEventViewModel(get(),get(),get()) }
        }

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(mapperModule, storageModule, viewModelModule))
        }

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
    }
}


