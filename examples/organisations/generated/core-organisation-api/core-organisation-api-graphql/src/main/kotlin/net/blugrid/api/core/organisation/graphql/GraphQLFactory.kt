package net.blugrid.api.core.organisation.graphql

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.dataloader.DataLoaderRegistry

@Factory
class GraphQLFactory(
    private val organisationQueries: OrganisationQueries,
    private val organisationMutations: OrganisationMutations,
    private val organisationFederationResolver: OrganisationFederationResolver,
    private val organisationDataLoaderFactory: OrganisationDataLoaderFactory
) {

    @Bean
    @Singleton
    fun graphQLSchema(): GraphQLSchema {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("net.blugrid.api.core.organisation.graphql"),
            hooks = FederatedSchemaGeneratorHooks(
                resolvers = listOf(organisationFederationResolver)
            )
        )

        return toFederatedSchema(
            config = config,
            queries = listOf(TopLevelObject(organisationQueries)),
            mutations = listOf(TopLevelObject(organisationMutations))
        )
    }

    @Bean
    @Singleton
    fun graphQL(schema: GraphQLSchema): GraphQL {
        return GraphQL.newGraphQL(schema).build()
    }

    @Bean
    @Singleton
    fun dataLoaderRegistry(): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        organisationDataLoaderFactory.registerDataLoader(registry)
        return registry
    }
}
