package com.example.kotlin_apk

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface TmdbApi {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(@Query("api_key") apiKey: String): TmdbMovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TmdbMovieDetailResponse

    @GET("trending/tv/week")
    suspend fun getTrendingSeries(@Query("api_key") apiKey: String): TmdbSeriesResponse

    @GET("tv/{tv_id}")
    suspend fun getSeriesDetails(
        @Path("tv_id") seriesId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "credits"
    ): TmdbSeriesDetailResponse

    @GET("person/{person_id}")
    suspend fun getActorDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): TmdbActorDetailResponse

    @GET("trending/person/week")
    suspend fun getTrendingActors(@Query("api_key") apiKey: String): TmdbActorResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbMovieResponse

    @GET("search/tv")
    suspend fun searchSeries(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbSeriesResponse

    @GET("search/person")
    suspend fun searchActors(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbActorResponse
}

object RetrofitInstance {
    val api: TmdbApi by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TmdbApi::class.java)
    }
}
