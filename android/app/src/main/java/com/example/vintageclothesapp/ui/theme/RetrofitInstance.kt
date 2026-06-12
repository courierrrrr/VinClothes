package com.example.vintageclothesapp

import android.content.Context
import android.util.Base64
import com.example.vintageclothesapp.auth.AuthInterceptor
import com.example.vintageclothesapp.auth.LoginRequest
import com.example.vintageclothesapp.auth.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

data class ClothingItemDto(
    val id: Int? = null,
    val title: String = "",
    val brand: String = "",
    val size: String = "",
    val decade: String = "",
    val clothing_type: String = "",
    val description: String = "",
    val video_url: String = "",
    val average_rating: Double = 0.0,
    val image: String? = null
)

data class RatingRequest(val stars: Int)

interface ClothingApiService {
    @GET("api/clothes/")
    suspend fun getClothes(
        @Query("clothing_type") type: String? = null,
        @Query("decade") decade: String? = null,
        @Query("size") size: String? = null,
        @Query("brand") brand: String? = null
    ): List<ClothingItemDto>

    @GET("api/clothes/{id}/")
    suspend fun getClothingById(@Path("id") id: Int): ClothingItemDto

    @POST("api/clothes/")
    suspend fun addClothing(@Body item: ClothingItemDto): ClothingItemDto

    @PUT("api/clothes/{id}/")
    suspend fun updateClothing(
        @Path("id") id: Int,
        @Body item: ClothingItemDto
    ): ClothingItemDto

    @DELETE("api/clothes/{id}/")
    suspend fun deleteClothing(@Path("id") id: Int)

    @POST("api/clothes/{id}/rate/")
    suspend fun rateClothing(
        @Path("id") id: Int,
        @Body rating: RatingRequest
    ): ClothingItemDto

    @POST("api/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://pantazidg.pythonanywhere.com/"

    private val gson = GsonBuilder()
        .registerTypeAdapter(ByteArray::class.java, object : JsonDeserializer<ByteArray> {
            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): ByteArray {
                return Base64.decode(json.asString, Base64.DEFAULT)
            }
        })
        .create()

    fun buildApi(context: Context): ClothingApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ClothingApiService::class.java)
    }
}