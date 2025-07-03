package net.blugrid.api.core.organisation.grpc

import com.google.protobuf.Empty
import net.blugrid.api.common.grpc.Direction
import net.blugrid.api.common.grpc.order
import net.blugrid.api.common.grpc.sort
import net.blugrid.api.core.organisation.grpc.OrganisationStateServiceGrpcKt.OrganisationStateServiceCoroutineStub
import net.blugrid.api.core.organisation.grpc.assertion.assert
import net.blugrid.api.test.support.BaseGrpcIntegTest
import net.blugrid.api.test.support.grpc
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


@DisplayName("OrganisationGrpcService")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganisationGrpcServiceIntegTest : BaseGrpcIntegTest() {

    private val stub: OrganisationStateServiceCoroutineStub by lazy {
        createStub(::OrganisationStateServiceCoroutineStub)
    }

    @Test
    fun `create Organisation`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 123
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                created.assert(parentOrganisationId = 123)
            }
        }
    }

    @Test
    fun `get Organisation by ID`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 1001
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val fetched = getById(
                    organisationRequestById { id = created.id }
                ) { req -> stub.getById(req) }

                fetched.assert(
                    parentOrganisationId = 1001,
                    effectiveTimestamp = LocalDateTime.parse("2025-01-01T00:00:00")
                )
            }
        }
    }

    @Test
    fun `get Organisation by ID optional`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 3001
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val found = getById(
                    organisationRequestById { id = created.id }
                ) { req -> stub.getByIdOptional(req) }

                assertTrue(found.exists, "Expected exists = true for created organisation")
                found.organisation.assert(
                    parentOrganisationId = 3001,
                    effectiveTimestamp = LocalDateTime.parse("2025-01-01T00:00:00")
                )

                val missing = getById(
                    organisationRequestById { id = 999999L }
                ) { req -> stub.getByIdOptional(req) }

                assertFalse(missing.exists, "Expected exists = false for non-existent ID")
            }
        }
    }

    @Test
    fun `get Organisation by UUID`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 1002
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val fetched = getById(
                    organisationRequestByUuid { uuid = created.uuid }
                ) { req -> stub.getByUuid(req) }

                fetched.assert(
                    parentOrganisationId = 1002,
                    effectiveTimestamp = LocalDateTime.parse("2025-01-01T00:00:00")
                )
            }
        }
    }

    @Test
    fun `get Organisation by UUID optional`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 3002
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val found = getById(
                    organisationRequestByUuid { uuid = created.uuid }
                ) { req -> stub.getByUuidOptional(req) }

                assertTrue(found.exists, "Expected exists = true for valid UUID")
                found.organisation.assert(
                    parentOrganisationId = 3002,
                    effectiveTimestamp = LocalDateTime.parse("2025-01-01T00:00:00")
                )

                val missing = getById(
                    organisationRequestByUuid { uuid = UUID.randomUUID().toString() }
                ) { req -> stub.getByUuidOptional(req) }

                assertFalse(missing.exists, "Expected exists = false for missing UUID")
            }
        }
    }

    @Test
    fun `update Organisation`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 1010
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val updated = update(
                    organisationUpdateRequest {
                        id = created.id
                        uuid = created.uuid
                        parentOrganisationId = 2020
                        effectiveTimestamp = "2026-06-01T12:00:00"
                    }
                ) { req -> stub.update(req) }

                updated.assert(
                    id = created.id,
                    uuid = UUID.fromString(created.uuid),
                    parentOrganisationId = 2020,
                    effectiveTimestamp = LocalDateTime.parse("2026-06-01T12:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
        }
    }

    @Test
    fun `get all Organisations`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 1004
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                val response = getById(Empty.getDefaultInstance()) { stub.getAll(it) }

                assertTrue(
                    response.organisationsList.any { it.id == created.id },
                    "Expected created organisation with ID ${created.id} to appear in getAll list"
                )
            }
        }
    }

    @Test
    fun `get paged Organisations`() {
        runGrpcTest {
            grpc(stub) {
                // Create 3 organisations
                (1..3).forEach {
                    create(
                        organisationCreateRequest {
                            parentOrganisationId = (409 + it).toLong()
                            effectiveTimestamp = "2025-01-01T00:00:00"
                        }
                    ) { req -> stub.create(req) }
                }

                val sort = sort {
                    sorted = true
                    orderBy += order {
                        property = "id"
                        direction = Direction.ASC
                        ignoreCase = false
                    }
                }

                val response = getById(
                    organisationPageRequest {
                        page = 0
                        size = 2
                        this.sort = sort
                    }
                ) { req -> stub.getPage(req) }

                assertEquals(2, response.organisationsCount)
                assertTrue(response.totalElements >= 3)
                assertEquals(0, response.page)
            }
        }
    }


    @Test
    fun `delete Organisation`() {
        runGrpcTest {
            grpc(stub) {
                val created = create(
                    organisationCreateRequest {
                        parentOrganisationId = 1111
                        effectiveTimestamp = "2025-01-01T00:00:00"
                    }
                ) { req -> stub.create(req) }

                delete(organisationDeleteRequest { id = created.id }) { req -> stub.delete(req) }

                val thrown = runCatching {
                    getById(organisationRequestById { id = created.id }) { req -> stub.getById(req) }
                }.exceptionOrNull()

                assertNotNull(thrown)
                assertTrue(thrown!!.message!!.contains("NOT_FOUND"))
            }
        }
    }
}
