package com.grup.aws

import com.grup.exceptions.ImageUploadException
import com.grup.getEnvVar
import com.grup.models.User
import com.grup.repositories.AWS_IMAGES_API_KEY
import com.grup.repositories.AWS_IMAGES_API_URL
import com.grup.repositories.AWS_IMAGES_BUCKET_NAME
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Images : KoinComponent {
    private val client: HttpClient by inject()

    suspend fun uploadProfilePicture(user: User, pfp: ByteArray): String {
        val response: HttpResponse = client.put(
            "${getEnvVar("AWS_IMAGES_API_URL")}/pfp_${user.getId()}.png"
        ) {
            contentType(ContentType.Image.PNG)
            header("X-Api-Key", getEnvVar("AWS_IMAGES_API_KEY"))
            setBody(
                ByteArrayContent(
                    bytes = pfp,
                    contentType = ContentType.Image.PNG
                )
            )
        }
        if (response.status.value !in 200..299) {
            throw ImageUploadException(response.bodyAsText())
        }
        return getProfilePictureURI(user.getId())
    }

    fun getProfilePictureURI(userId: String) =
        "https://$AWS_IMAGES_BUCKET_NAME.s3.amazonaws.com/pfp_$userId.png"
}