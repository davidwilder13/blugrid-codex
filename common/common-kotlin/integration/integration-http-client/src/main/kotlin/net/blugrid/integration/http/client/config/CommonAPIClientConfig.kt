//package net.blugrid.integration.http.client.config
//
//import io.micronaut.context.annotation.Bean
//import io.micronaut.context.annotation.Factory
//
//@Factory
//class CommonAPIClientConfig {
//
//    @Bean
//    fun errorDecoder(objectMapper: ObjectMapper) = APIErrorDecoder(
//        objectMapper = objectMapper,
//        convertAPIError = APIErrorConverter::convert
//    )
//}
//
//// Domain-specific configs can extend or use the common config
//@Factory
//class OrganisationAPIClientConfig : CommonAPIClientConfig()
//
//@Factory
//class UserAPIClientConfig : CommonAPIClientConfig()
//
//@Factory
//class PropertyAPIClientConfig : CommonAPIClientConfig()
