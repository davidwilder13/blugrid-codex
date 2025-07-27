package net.blugrid.platform.testing.support

object DockerHostIpSupport {

    const val ENV_KEY = "DOCKER_HOST_IP"
    const val DEFAULT_IP = "10.200.10.1"

    val ip: String by lazy {
        System.getenv(ENV_KEY)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_IP
    }

    val url: String get() = "http://$ip"

    fun toEnv(): Map<String, String> = mapOf(
        ENV_KEY to ip,
    )
}
