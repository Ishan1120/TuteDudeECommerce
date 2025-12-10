package com.tutedude.ecommerce.data.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.tutedude.ecommerce.data.auth.AuthRepositoryImpl
import com.tutedude.ecommerce.data.local.AppDatabase
import com.tutedude.ecommerce.data.local.ProductDao
import com.tutedude.ecommerce.data.repository.ProductRepositoryImpl
import com.tutedude.ecommerce.domain.repository.AuthRepository
import com.tutedude.ecommerce.domain.repository.ProductRepository
import com.tutedude.ecommerce.domain.repository.UserRepository
import com.tutedude.ecommerce.domain.repository.RecommendationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db").build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideProductRepository(
        db: AppDatabase,
        firestore: com.google.firebase.firestore.FirebaseFirestore,
        storage: com.google.firebase.storage.FirebaseStorage,
        @ApplicationContext ctx: Context
    ): ProductRepository = ProductRepositoryImpl(db.productDao(), firestore, storage, ctx)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): com.google.firebase.firestore.FirebaseFirestore =
        com.google.firebase.firestore.FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorage(): com.google.firebase.storage.FirebaseStorage =
        com.google.firebase.storage.FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: com.google.firebase.firestore.FirebaseFirestore
    ): UserRepository =
        com.tutedude.ecommerce.data.user.UserRepositoryImpl(firestore)

    // ✅ FIXED FUNCTION
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userRepository: UserRepository
    ): AuthRepository = AuthRepositoryImpl(auth, userRepository)

    @Provides
    @Singleton
    fun provideRetrofit(): retrofit2.Retrofit =
        retrofit2.Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideFakeStoreApi(
        retrofit: retrofit2.Retrofit
    ): com.tutedude.ecommerce.data.remote.FakeStoreApi =
        retrofit.create(com.tutedude.ecommerce.data.remote.FakeStoreApi::class.java)

    @Provides
    @Singleton
    fun provideRecommendationRepository(
        api: com.tutedude.ecommerce.data.remote.FakeStoreApi
    ): RecommendationRepository =
        com.tutedude.ecommerce.data.recommendation.RecommendationRepositoryImpl(api)
}
