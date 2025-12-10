package net.blugrid.api.core.organisation.graphql

import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.generator.scalars.ID
import graphql.schema.DataFetchingEnvironment
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import net.blugrid.api.core.organisation.graphql.model.OrganisationType
import net.blugrid.api.core.organisation.graphql.model.toGraphQL
import net.blugrid.api.core.organisation.grpc.client.OrganisationGrpcClient
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry

/**
 * Federation resolver for Organisation entity.
 * Handles entity resolution when other subgraphs reference Organisation by ID.
 */
@Singleton
class OrganisationFederationResolver(
    private val grpcClient: OrganisationGrpcClient
) : FederatedTypeSuspendResolver<OrganisationType> {

    override val typeName: String = "OrganisationType"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): OrganisationType? {
        val id = when (val rawId = representation["id"]) {
            is String -> rawId.toLongOrNull()
            is Number -> rawId.toLong()
            is ID -> rawId.value.toLongOrNull()
            else -> null
        } ?: return null

        val response = grpcClient.getByIdOptional(id)
        return if (response.exists) {
            response.organisation.toGraphQL()
        } else {
            null
        }
    }
}

/**
 * DataLoader for batching Organisation lookups.
 * Used to prevent N+1 queries when resolving Organisation references.
 */
@Singleton
class OrganisationDataLoaderFactory(
    private val grpcClient: OrganisationGrpcClient
) {
    companion object {
        const val DATA_LOADER_NAME = "OrganisationDataLoader"
    }

    fun createDataLoader(): DataLoader<Long, OrganisationType?> {
        val batchLoader = BatchLoader<Long, OrganisationType?> { ids ->
            CoroutineScope(Dispatchers.IO).future {
                val organisations = grpcClient.getByIds(ids.toList())
                    .map { it.toGraphQL() }
                    .associateBy { it.id.value.toLong() }

                // Return in same order as requested, with null for missing
                ids.map { organisations[it] }
            }
        }
        return DataLoaderFactory.newDataLoader(batchLoader)
    }

    fun registerDataLoader(registry: DataLoaderRegistry): DataLoaderRegistry {
        registry.register(DATA_LOADER_NAME, createDataLoader())
        return registry
    }
}
