//package net.blugrid.platform.serialization.config
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import io.micronaut.context.event.BeanCreatedEvent
//import io.micronaut.context.event.BeanCreatedEventListener
//import jakarta.inject.Singleton
//
//@Singleton
//open class ObjectMapperBeanEventListener(
//    private val objectMapperFactory: CustomObjectMapperFactory
//) : BeanCreatedEventListener<ObjectMapper> {
//
//    override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
//        return objectMapperFactory.objectMapper(null, null)
//    }
//}
