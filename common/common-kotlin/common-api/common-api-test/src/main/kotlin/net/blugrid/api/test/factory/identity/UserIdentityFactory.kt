package net.blugrid.api.test.factory.identity

import net.blugrid.api.security.model.AuthenticatedUserIdentity
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.*
import java.util.UUID

/**
 * Factory for creating UserIdentity instances via AuthenticatedUserIdentity implementation
 */
object UserIdentityFactory : BaseFactory<AuthenticatedUserIdentity>,
    RandomizableFactory<AuthenticatedUserIdentity>,
    ScenarioFactory<AuthenticatedUserIdentity> {

    override fun createDefault(): AuthenticatedUserIdentity = create()

    fun create(
        id: Long = Long.randomId(),
        uuid: UUID = UUID.randomUUID(),
        name: String = String.randomName(),
        email: String = String.randomEmail(),
        displayName: String? = String.randomName(),
        emailVerified: Boolean? = true,
        providerId: String = "auth0",
        partyId: Long? = null,
        nickName: String? = null,
        givenName: String? = String.randomFirstName(),
        familyName: String? = String.randomLastName(),
        pictureUrl: String? = null
    ): AuthenticatedUserIdentity = AuthenticatedUserIdentity(
        id = id,
        uuid = uuid,
        name = name,
        email = email,
        displayName = displayName,
        emailVerified = emailVerified,
        providerId = providerId,
        partyId = partyId,
        nickName = nickName,
        givenName = givenName,
        familyName = familyName,
        pictureUrl = pictureUrl
    )

    fun createVerified(
        id: Long = Long.randomId(),
        email: String = String.randomEmail()
    ) = create(
        id = id,
        email = email,
        emailVerified = true
    )

    fun createUnverified(
        id: Long = Long.randomId(),
        email: String = String.randomEmail()
    ) = create(
        id = id,
        email = email,
        emailVerified = false
    )

    fun createWithParty(
        id: Long = Long.randomId(),
        partyId: Long
    ) = create(
        id = id,
        partyId = partyId
    )

    fun createMinimal(
        id: Long = Long.randomId(),
        email: String = String.randomEmail()
    ) = create(
        id = id,
        email = email,
        displayName = null,
        emailVerified = null,
        partyId = null,
        nickName = null,
        givenName = null,
        familyName = null,
        pictureUrl = null
    )

    override fun createRandom(): AuthenticatedUserIdentity = create(
        id = Long.randomId(),
        uuid = UUID.randomUUID(),
        name = String.randomName(),
        email = String.randomEmail(),
        displayName = if (Boolean.random()) String.randomName() else null,
        emailVerified = if (Boolean.random()) Boolean.random() else null,
        providerId = listOf("auth0", "google", "microsoft", "okta").random(),
        partyId = if (Boolean.random()) Long.randomId() else null,
        nickName = if (Boolean.random()) String.randomName() else null,
        givenName = if (Boolean.random()) String.randomFirstName() else null,
        familyName = if (Boolean.random()) String.randomLastName() else null,
        pictureUrl = if (Boolean.random()) "https://example.com/avatar/${Long.randomId()}.jpg" else null
    )

    override fun createForScenario(scenario: String): AuthenticatedUserIdentity = when (scenario) {
        "minimal" -> createMinimal()
        "verified" -> createVerified()
        "unverified" -> createUnverified()
        "with-party" -> createWithParty(partyId = Long.randomId())
        "full" -> create(
            displayName = String.randomName(),
            emailVerified = true,
            partyId = Long.randomId(),
            nickName = String.randomName(),
            givenName = String.randomFirstName(),
            familyName = String.randomLastName(),
            pictureUrl = "https://example.com/avatar/${Long.randomId()}.jpg"
        )
        "google" -> create(providerId = "google")
        "microsoft" -> create(providerId = "microsoft")
        "okta" -> create(providerId = "okta")
        else -> createDefault()
    }

    /**
     * Builder DSL for AuthenticatedUserIdentity
     */
    class Builder : BaseFactory.Builder<AuthenticatedUserIdentity> {
        private var id: Long? = null
        private var uuid: UUID? = null
        private var name: String? = null
        private var email: String? = null
        private var displayName: String? = null
        private var emailVerified: Boolean? = null
        private var providerId: String? = null
        private var partyId: Long? = null
        private var nickName: String? = null
        private var givenName: String? = null
        private var familyName: String? = null
        private var pictureUrl: String? = null

        fun id(id: Long) = apply { this.id = id }
        fun uuid(uuid: UUID) = apply { this.uuid = uuid }
        fun uuid(uuid: String) = apply { this.uuid = UUID.fromString(uuid) }
        fun name(name: String) = apply { this.name = name }
        fun email(email: String) = apply { this.email = email }
        fun displayName(displayName: String?) = apply { this.displayName = displayName }
        fun emailVerified(emailVerified: Boolean?) = apply { this.emailVerified = emailVerified }
        fun providerId(providerId: String) = apply { this.providerId = providerId }
        fun partyId(partyId: Long?) = apply { this.partyId = partyId }
        fun nickName(nickName: String?) = apply { this.nickName = nickName }
        fun givenName(givenName: String?) = apply { this.givenName = givenName }
        fun familyName(familyName: String?) = apply { this.familyName = familyName }
        fun pictureUrl(pictureUrl: String?) = apply { this.pictureUrl = pictureUrl }

        // Convenience methods
        fun randomId() = apply { this.id = Long.randomId() }
        fun randomUuid() = apply { this.uuid = UUID.randomUUID() }
        fun randomName() = apply { this.name = String.randomName() }
        fun randomEmail() = apply { this.email = String.randomEmail() }
        fun verified() = apply { this.emailVerified = true }
        fun unverified() = apply { this.emailVerified = false }
        fun withoutEmail() = apply { this.emailVerified = null }
        fun withoutParty() = apply { this.partyId = null }
        fun withoutName() = apply {
            this.displayName = null
            this.nickName = null
            this.givenName = null
            this.familyName = null
        }
        fun withoutPicture() = apply { this.pictureUrl = null }

        // Provider convenience methods
        fun auth0Provider() = apply { this.providerId = "auth0" }
        fun googleProvider() = apply { this.providerId = "google" }
        fun microsoftProvider() = apply { this.providerId = "microsoft" }
        fun oktaProvider() = apply { this.providerId = "okta" }

        // Name convenience methods
        fun fullName(givenName: String, familyName: String) = apply {
            this.givenName = givenName
            this.familyName = familyName
            this.displayName = "$givenName $familyName"
            this.name = "${givenName.lowercase()}.${familyName.lowercase()}"
        }

        fun randomFullName() = apply {
            val firstName = String.randomFirstName()
            val lastName = String.randomLastName()
            fullName(firstName, lastName)
        }

        fun withNickName(nickName: String) = apply {
            this.nickName = nickName
            if (this.displayName == null) {
                this.displayName = nickName
            }
        }

        fun randomNickName() = apply {
            withNickName(String.randomName())
        }

        // Picture convenience methods
        fun randomPictureUrl() = apply {
            this.pictureUrl = "https://example.com/avatar/${Long.randomId()}.jpg"
        }

        fun gravatarUrl(email: String? = null) = apply {
            val emailToUse = email ?: this.email ?: String.randomEmail()
            this.pictureUrl = "https://www.gravatar.com/avatar/${emailToUse.hashCode()}"
        }

        // Scenario builders
        fun minimalUser() = apply {
            withoutName()
            withoutParty()
            withoutPicture()
            this.emailVerified = null
        }

        fun verifiedUser() = apply {
            verified()
            randomFullName()
            randomEmail()
        }

        fun corporateUser(domain: String = "corporate.com") = apply {
            val username = String.randomName()
            this.name = username
            this.email = "$username@$domain"
            verified()
            randomFullName()
            oktaProvider()
        }

        fun socialUser() = apply {
            verified()
            randomFullName()
            randomNickName()
            randomPictureUrl()
            this.providerId = listOf("google", "facebook", "twitter").random()
        }

        fun testUser() = apply {
            this.id = 99999L
            this.uuid = UUID.fromString("00000000-0000-0000-0000-000000099999")
            this.name = "testuser"
            this.email = "test@example.com"
            this.displayName = "Test User"
            verified()
            auth0Provider()
        }

        override fun build(): AuthenticatedUserIdentity = AuthenticatedUserIdentity(
            id = id ?: Long.randomId(),
            uuid = uuid ?: UUID.randomUUID(),
            name = name ?: String.randomName(),
            email = email ?: String.randomEmail(),
            displayName = displayName,
            emailVerified = emailVerified,
            providerId = providerId ?: "auth0",
            partyId = partyId,
            nickName = nickName,
            givenName = givenName,
            familyName = familyName,
            pictureUrl = pictureUrl
        )
    }

    override fun build(block: BaseFactory.Builder<AuthenticatedUserIdentity>.() -> Unit): AuthenticatedUserIdentity =
        Builder().apply(block).build()
}
