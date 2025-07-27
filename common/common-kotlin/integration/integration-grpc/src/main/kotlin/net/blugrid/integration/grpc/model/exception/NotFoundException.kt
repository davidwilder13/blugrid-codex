package net.blugrid.integration.grpc.model.exception

class NotFoundException(
    val resourceType: String,
    val resourceId: String,
    message: String? = "$resourceType with ID $resourceId not found"
) : RuntimeException(message)
