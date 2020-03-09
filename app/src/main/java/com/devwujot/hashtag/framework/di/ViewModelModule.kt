package com.devwujot.hashtag.framework.di

import com.devwujot.hashtag.framework.viewModel.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeActivityViewModel(get()) }
    viewModel { SignupViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { TweetViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { HomeFragmentViewModel(get()) }
    viewModel { MyActivityViewModel(get()) }
}