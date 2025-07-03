package net.blugrid.api.test.support

import io.grpc.kotlin.AbstractCoroutineStub

suspend fun <T : AbstractCoroutineStub<T>> grpc(
    stub: T,
    block: suspend GrpcTestDsl<T>.() -> Unit
) = GrpcTestDsl(stub).block()

class GrpcTestDsl<T : AbstractCoroutineStub<T>>(
    private val stub: T
) {
    suspend fun <Req, Res> create(
        request: Req,
        call: suspend T.(Req) -> Res
    ): Res = stub.call(request)

    suspend fun <Req, Res> getById(
        request: Req,
        call: suspend T.(Req) -> Res
    ): Res = stub.call(request)

    suspend fun <Req, Res> update(
        request: Req,
        call: suspend T.(Req) -> Res
    ): Res = stub.call(request)

    suspend fun <Req> delete(
        request: Req,
        call: suspend T.(Req) -> Unit
    ) = stub.call(request)
}
