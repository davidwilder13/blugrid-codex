package net.blugrid.platform.testing.support

import aws.sdk.kotlin.services.ecr.EcrClient
import aws.sdk.kotlin.services.ecr.model.GetAuthorizationTokenRequest
import java.util.Base64

object EcrDockerLoginHelper {

    suspend fun loginToEcr(region: String = "ap-southeast-2") {
        val ecrClient = EcrClient { this.region = region }

        val response = ecrClient.getAuthorizationToken(GetAuthorizationTokenRequest {})
        val authData = response.authorizationData?.firstOrNull()
            ?: throw IllegalStateException("No ECR authorization data received")

        val decoded = String(Base64.getDecoder().decode(authData.authorizationToken))
        val (username, password) = decoded.split(":")

        val registry = authData.proxyEndpoint?.removePrefix("https://")
            ?: throw IllegalStateException("Missing proxy endpoint in ECR response")

        println("🔐 Logging in to Docker registry: $registry")
        val process = ProcessBuilder(
            "docker", "login",
            "--username", username,
            "--password", password,
            registry
        ).inheritIO().start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("❌ Docker login to ECR failed with exit code $exitCode")
        }

        println("✅ Docker login successful")
    }
}
