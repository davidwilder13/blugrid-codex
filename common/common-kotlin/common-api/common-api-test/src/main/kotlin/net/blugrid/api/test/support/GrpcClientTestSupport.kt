package net.blugrid.api.test.support

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.kotlin.AbstractCoroutineStub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.blugrid.api.logging.logger

interface GrpcClientTestSupport {
    val grpcHost: String get() = System.getProperty("grpc.test.host", "127.0.0.1")
    val grpcPort: Int get() = System.getProperty("grpc.test.port")?.toIntOrNull() ?: 50051

    val log get() = logger()

    val channel: ManagedChannel
        get() = ManagedChannelBuilder
            .forAddress(grpcHost, grpcPort)
            .usePlaintext()
            .build()

    fun <T : AbstractCoroutineStub<T>> createStub(factory: (ManagedChannel) -> T): T = factory(channel)

    fun <T> runGrpcTest(name: String = "anonymous", block: suspend CoroutineScope.() -> T): T = runBlocking {
        try {
            log.info("▶️  Running gRPC test: $name")
            block()
        } catch (ex: Throwable) {
            log.error("❌ gRPC test '$name' failed: ${ex.message}", ex)
            throw ex
        }
    }
}
