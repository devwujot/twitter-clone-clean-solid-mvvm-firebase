package com.devwujot.hashtag.framework.di

import com.devwujot.hashtag.framework.utility.FormValidator
import org.koin.dsl.module

val formValidatorModule = module {
    single { FormValidator(get()) }
}