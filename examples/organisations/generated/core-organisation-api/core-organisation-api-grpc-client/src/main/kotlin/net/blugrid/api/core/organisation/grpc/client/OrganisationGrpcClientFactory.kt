package net.blugrid.api.core.organisation.grpc.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton
import net.blugrid.api.core.organisation.grpc.OrganisationStateServiceGrpcKt

@Factory
class OrganisationGrpcClientFactory {

    /**
     * Manual override, for explicit use in dev or test bootstrap.
     */
    fun create(host: String, port: Int): OrganisationGrpcClient {
        val channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build()

        val stub = OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub(channel)
        return OrganisationGrpcClient(stub)
    }

    /**
     * Stub resolved via service discovery.
     * Used by Micronaut @GrpcChannel injection.
     */
    @Singleton
    @Primary
    fun organisationStub(
        @GrpcChannel("organisation-grpc") channel: ManagedChannel
    ): OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub {
        return OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub(channel)
    }

    /**
     * gRPC client that uses discovery-injected stub.
     */
    @Singleton
    @Primary
    fun organisationGrpcClient(
        stub: OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub
    ): OrganisationGrpcClient {
        return OrganisationGrpcClient(stub)
    }
}
