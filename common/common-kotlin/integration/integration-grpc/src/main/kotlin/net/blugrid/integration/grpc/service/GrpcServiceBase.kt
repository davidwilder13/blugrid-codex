package net.blugrid.integration.grpc.service

import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import com.google.rpc.Status
import io.grpc.protobuf.StatusProto
import net.blugrid.integration.grpc.model.exception.ConstraintViolationDetail
import net.blugrid.integration.grpc.model.exception.InvalidRequestException
import net.blugrid.integration.grpc.model.exception.NotFoundException

abstract class GrpcServiceBase {

    protected suspend fun <T> safe(call: suspend () -> T): T =
        try {
            call()
        } catch (ex: InvalidRequestException) {
            throw StatusProto.toStatusRuntimeException(
                buildBadRequestStatus("Validation failed", ex.violations)
            )
        } catch (ex: NotFoundException) {
            throw StatusProto.toStatusRuntimeException(
                Status.newBuilder()
                    .setCode(Code.NOT_FOUND_VALUE)
                    .setMessage(ex.message)
                    .build()
            )
        } catch (ex: Exception) {
            throw StatusProto.toStatusRuntimeException(
                Status.newBuilder()
                    .setCode(Code.INTERNAL_VALUE)
                    .setMessage("Unexpected error occurred")
                    .build()
            )
        }
}

fun buildBadRequestStatus(message: String, violations: List<ConstraintViolationDetail>): Status {
    val badRequest = BadRequest.newBuilder().apply {
        violations.forEach {
            addFieldViolations(
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.field)
                    .setDescription(it.description)
                    .build()
            )
        }
    }.build()

    return Status.newBuilder()
        .setCode(Code.INVALID_ARGUMENT_VALUE)
        .setMessage(message)
        .addDetails(Any.pack(badRequest))
        .build()
}
