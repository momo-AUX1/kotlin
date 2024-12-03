package com.example.kotlin_apk

import com.squareup.moshi.Json

const val key = "e4009b8963dbfe389c28cb3b4d0c309e"

data class TmdbMovieResponse(
    val page: Int,
    val results: List<TmdbMovie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

data class TmdbMovie(
    val adult: Boolean,
    val backdrop_path: String?,
    val genre_ids: List<Int>,
    val id: Int,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double,
    val poster_path: String?,
    val release_date: String?,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class TmdbMovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String?,
    val release_date: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val credits: Credits?
)

data class Credits(
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)

data class CastMember(
    val cast_id: Int?,
    val character: String?,
    val credit_id: String,
    val gender: Int?,
    val id: Int,
    val name: String,
    val order: Int?,
    val profile_path: String?
)

data class CrewMember(
    val credit_id: String,
    val department: String?,
    val gender: Int?,
    val id: Int,
    val job: String?,
    val name: String,
    val profile_path: String?
)

data class TmdbSeriesResponse(
    val page: Int,
    val results: List<TmdbSeries>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

data class TmdbSeries(
    val id: Int,
    val name: String,
    val original_name: String,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val first_air_date: String?,
    val origin_country: List<String>?,
    val genre_ids: List<Int>?,
    val original_language: String?,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int
)

data class TmdbSeriesDetailResponse(
    val id: Int,
    val name: String,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val first_air_date: String?,
    val credits: Credits?
)

data class TmdbActorResponse(
    val page: Int,
    val results: List<TmdbActor>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

data class TmdbActor(
    val id: Int,
    val name: String,
    val profile_path: String?,
    val known_for_department: String?,
    val popularity: Double
)

data class TmdbActorDetailResponse(
    val id: Int,
    val name: String,
    val biography: String?,
    val profile_path: String?,
    val known_for_department: String?,
    val place_of_birth: String?,
    val birthday: String?,
    val deathday: String?
)
