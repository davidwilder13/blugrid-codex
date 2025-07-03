package net.blugrid.api.common.exception

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Base api error interface")
interface APIError {

    @get: [JsonProperty("statusCode")]
    val statusCode: String

    @get: [JsonProperty("code")]
    val code: String

    @get: [JsonProperty("message")]
    val message: String

    @get: [JsonIgnore]
    val headers: Map<String, String>?

    @get: [JsonProperty("details")]
    val details: List<Any>?

    @get: [JsonProperty("_links")]
    val links: Map<String, Link>?
}

data class Link(
    val href: String,
    val templated: Boolean = false
)

@Schema(description = "Default API error")
data class DefaultAPIError(
    override val statusCode: String,
    override val code: String,
    override val message: String,
    override val headers: Map<String, String>? = null,
    override val links: Map<String, Link>? = null,
    override val details: List<Any>? = null
) : APIError

@Schema(description = "OAuth error")
data class OAuthError(

    override val statusCode: String,
    override val code: String,
    override val message: String,

    val errorDescription: String? = null,

    override val headers: Map<String, String>? = null,
    override val links: Map<String, Link>?,
    override val details: List<Any>? = null
) : APIError

@Schema(description = "API Error detail")
data class APIErrorDetail(

    @JsonProperty("field")
    val field: String? = null,

    @JsonProperty("index")
    val index: Int? = null,

    @JsonProperty("message")
    val message: String,
)

@Schema(description = "API validation detail")
data class APIErrorValidationDetail(

    @JsonProperty("field")
    val field: String? = null,

    @JsonProperty("value")
    val value: String? = null,

    @JsonProperty("message")
    val message: String,
)
