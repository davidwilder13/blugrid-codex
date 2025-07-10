package net.blugrid.api.test.factory.security

import net.blugrid.api.security.model.AuthenticatedUser
import net.blugrid.api.test.factory.base.BaseFactory
import net.blugrid.api.test.factory.base.RandomizableFactory
import net.blugrid.api.test.factory.base.ScenarioFactory
import net.blugrid.api.test.generator.random
import net.blugrid.api.test.generator.randomEmail
import net.blugrid.api.test.generator.randomFirstName
import net.blugrid.api.test.generator.randomId
import net.blugrid.api.test.generator.randomLastName
import net.blugrid.api.test.generator.randomName

object AuthenticatedUserFactory : BaseFactory<AuthenticatedUser>,
    RandomizableFactory<AuthenticatedUser>,
    ScenarioFactory<AuthenticatedUser> {

    override fun createDefault(): AuthenticatedUser = create()

    fun create(
        userIdentityId: String = Long.randomId().toString(),
        email: String = String.randomEmail(),
        providerId: String = "auth0",
        displayName: String? = String.randomName(),
        emailVerified: Boolean? = true,
        partyId: String? = Long.randomId().toString(),
        tenantId: String? = null,
        nickName: String? = null,
        givenName: String? = String.randomFirstName(),
        familyName: String? = String.randomLastName(),
        pictureUrl: String? = null
    ): AuthenticatedUser = AuthenticatedUser(
        userIdentityId = userIdentityId,
        email = email,
        providerId = providerId,
        displayName = displayName,
        emailVerified = emailVerified,
        partyId = partyId,
        tenantId = tenantId,
        nickName = nickName,
        givenName = givenName,
        familyName = familyName,
        pictureUrl = pictureUrl
    )

    fun createVerified(
        userIdentityId: String = Long.randomId().toString(),
        email: String = String.randomEmail()
    ) = create(
        userIdentityId = userIdentityId,
        email = email,
        emailVerified = true
    )

    fun createUnverified(
        userIdentityId: String = Long.randomId().toString(),
        email: String = String.randomEmail()
    ) = create(
        userIdentityId = userIdentityId,
        email = email,
        emailVerified = false
    )

    fun createWithTenant(
        tenantId: String,
        userIdentityId: String = Long.randomId().toString()
    ) = create(
        userIdentityId = userIdentityId,
        tenantId = tenantId
    )

    fun createWithParty(
        partyId: String,
        userIdentityId: String = Long.randomId().toString()
    ) = create(
        userIdentityId = userIdentityId,
        partyId = partyId
    )

    fun createMinimal(
        userIdentityId: String = Long.randomId().toString(),
        email: String = String.randomEmail()
    ) = create(
        userIdentityId = userIdentityId,
        email = email,
        displayName = null,
        emailVerified = null,
        partyId = null,
        tenantId = null,
        nickName = null,
        givenName = null,
        familyName = null,
        pictureUrl = null
    )

    override fun createRandom(): AuthenticatedUser = create(
        userIdentityId = Long.randomId().toString(),
        email = String.randomEmail(),
        providerId = listOf("auth0", "google", "microsoft", "okta").random(),
        displayName = if (Boolean.random()) String.randomName() else null,
        emailVerified = if (Boolean.random()) Boolean.random() else null,
        partyId = if (Boolean.random()) Long.randomId().toString() else null,
        tenantId = if (Boolean.random()) Long.randomId().toString() else null,
        nickName = if (Boolean.random()) String.randomName() else null,
        givenName = if (Boolean.random()) String.randomFirstName() else null,
        familyName = if (Boolean.random()) String.randomLastName() else null,
        pictureUrl = if (Boolean.random()) "https://example.com/avatar/${Long.randomId()}.jpg" else null
    )

    override fun createForScenario(scenario: String): AuthenticatedUser = when (scenario) {
        "minimal" -> createMinimal()
        "verified" -> createVerified()
        "unverified" -> createUnverified()
        "with-tenant" -> createWithTenant(tenantId = Long.randomId().toString())
        "with-party" -> createWithParty(partyId = Long.randomId().toString())
        "full" -> create(
            displayName = String.randomName(),
            emailVerified = true,
            partyId = Long.randomId().toString(),
            tenantId = Long.randomId().toString(),
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
     * Builder DSL for AuthenticatedUser
     */
    class Builder : BaseFactory.Builder<AuthenticatedUser> {
        private var userIdentityId: String? = null
        private var email: String? = null
        private var providerId: String? = null
        private var displayName: String? = null
        private var emailVerified: Boolean? = null
        private var partyId: String? = null
        private var tenantId: String? = null
        private var nickName: String? = null
        private var givenName: String? = null
        private var familyName: String? = null
        private var pictureUrl: String? = null

        fun userIdentityId(userIdentityId: String) = apply { this.userIdentityId = userIdentityId }
        fun userIdentityId(userIdentityId: Long) = apply { this.userIdentityId = userIdentityId.toString() }
        fun email(email: String) = apply { this.email = email }
        fun providerId(providerId: String) = apply { this.providerId = providerId }
        fun displayName(displayName: String?) = apply { this.displayName = displayName }
        fun emailVerified(emailVerified: Boolean?) = apply { this.emailVerified = emailVerified }
        fun partyId(partyId: String?) = apply { this.partyId = partyId }
        fun partyId(partyId: Long?) = apply { this.partyId = partyId?.toString() }
        fun tenantId(tenantId: String?) = apply { this.tenantId = tenantId }
        fun tenantId(tenantId: Long?) = apply { this.tenantId = tenantId?.toString() }
        fun nickName(nickName: String?) = apply { this.nickName = nickName }
        fun givenName(givenName: String?) = apply { this.givenName = givenName }
        fun familyName(familyName: String?) = apply { this.familyName = familyName }
        fun pictureUrl(pictureUrl: String?) = apply { this.pictureUrl = pictureUrl }

        // Convenience methods
        fun randomUserIdentityId() = apply { this.userIdentityId = Long.randomId().toString() }
        fun randomEmail() = apply { this.email = String.randomEmail() }
        fun verified() = apply { this.emailVerified = true }
        fun unverified() = apply { this.emailVerified = false }
        fun withoutEmail() = apply { this.emailVerified = null }
        fun withoutParty() = apply { this.partyId = null }
        fun withoutTenant() = apply { this.tenantId = null }
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
            withoutTenant()
            withoutPicture()
            this.emailVerified = null
        }

        fun verifiedUser() = apply {
            verified()
            randomFullName()
            randomEmail()
        }

        fun corporateUser(tenantId: String) = apply {
            this.tenantId = tenantId
            verified()
            randomFullName()
            this.email = "${String.randomEmail()}@corporate.com"
            this.providerId = "okta"
        }

        fun socialUser() = apply {
            verified()
            randomFullName()
            randomNickName()
            randomPictureUrl()
            this.providerId = listOf("google", "facebook", "twitter").random()
        }

        override fun build(): AuthenticatedUser = AuthenticatedUser(
            userIdentityId = userIdentityId ?: Long.randomId().toString(),
            email = email ?: String.randomEmail(),
            providerId = providerId ?: "auth0",
            displayName = displayName,
            emailVerified = emailVerified,
            partyId = partyId,
            tenantId = tenantId,
            nickName = nickName,
            givenName = givenName,
            familyName = familyName,
            pictureUrl = pictureUrl
        )
    }

    override fun build(block: BaseFactory.Builder<AuthenticatedUser>.() -> Unit): AuthenticatedUser =
        Builder().apply(block).build()
}

