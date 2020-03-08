package com.devwujot.hashtag.framework.di

import com.devwujot.hashtag.core.repository.FirebaseRepository
import com.devwujot.hashtag.framework.FirebaseDataSourceImpl
import org.koin.dsl.module

val repositoryModule = module {
    single { FirebaseRepository(FirebaseDataSourceImpl(get())) }
}