package photos.rchugunov.com

import io.reactivex.Completable
import okhttp3.*
import okio.IOException
import org.json.JSONException
import org.json.JSONObject


class OAuth2Repository(
    private val tokenProvider: TokenProvider
) {
    fun getAccessToken(
        authCode: String,
        clientId: String,
        clientSecret: String
    ) = Completable.create { emitter ->
        try {
            val client = OkHttpClient()
            val requestBody: RequestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .build()
            val request: Request = Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    emitter.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body ?: return
                        val jsonObject = JSONObject(body.string())
                        tokenProvider.saveToken(jsonObject.get("access_token").toString())
                        tokenProvider.saveRefreshToken(jsonObject.get("refresh_token").toString())
                        emitter.onComplete()
                    } catch (e: JSONException) {
                        emitter.onError(e)
                    }
                }
            })
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    fun refreshToken(
        clientId: String,
        clientSecret: String
    ) = Completable.create { emitter ->
        try {
            val client = OkHttpClient()
            val requestBody: RequestBody = FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add(
                    "refresh_token",
                    tokenProvider.getRefreshToken() ?: throw IllegalArgumentException()
                )
                .add("grant_type", "refresh_token")
                .build()
            val request: Request = Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    emitter.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body ?: return
                        val jsonObject = JSONObject(body.string())
                        tokenProvider.saveToken(jsonObject.get("access_token").toString())
                        emitter.onComplete()
                    } catch (e: JSONException) {
                        emitter.onError(e)
                    }
                }
            })
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}