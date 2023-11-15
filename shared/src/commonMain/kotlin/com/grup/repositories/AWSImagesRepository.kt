package com.grup.repositories

import com.grup.exceptions.ImageUploadException
import com.grup.interfaces.IImagesRepository
import com.grup.models.User
import com.grup.other.AWS_IMAGES_API_KEY
import com.grup.other.AWS_IMAGES_API_URL
import com.grup.other.AWS_IMAGES_BUCKET_NAME
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AWSImagesRepository : KoinComponent, IImagesRepository {
    private val client: HttpClient by inject()

    override suspend fun uploadProfilePicture(user: User, pfp: ByteArray): String {
        if (pfp.isNotEmpty()) {
            val response: HttpResponse = client.put(
                "$AWS_IMAGES_API_URL/pfp_${user.id}.png"
            ) {
                contentType(ContentType.Image.PNG)
                header("X-Api-Key", AWS_IMAGES_API_KEY)
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
            return "https://$AWS_IMAGES_BUCKET_NAME.s3.amazonaws.com/pfp_${user.id}.png"
        }
        return ""
    }
}