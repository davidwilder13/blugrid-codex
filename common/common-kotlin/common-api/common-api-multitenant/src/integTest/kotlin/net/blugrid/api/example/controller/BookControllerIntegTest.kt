package net.blugrid.api.example.controller

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.micronaut.data.model.Sort.Order
import io.micronaut.data.model.Sort.Order.Direction.ASC
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import net.blugrid.api.BaseMultitenantIntegTest
import net.blugrid.api.example.factory.createBook
import net.blugrid.api.example.mapping.BookMapper
import net.blugrid.api.example.model.Book
import net.blugrid.api.test.security.TestApplicationContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Book")
class BookControllerIntegTest(
    private val mapper: BookMapper,
    @Client("/") override val client: HttpClient
) : BaseMultitenantIntegTest(
    baseUri = BookController.PATH,
    client = client
) {

    @BeforeEach
    fun setup() {
        TestApplicationContext.configureTenantApplicationContext()
    }

    @Test
    fun `create Book`() {
        assertCreate(
            createPayload = createBook(),
            responseType = Book::class.java
        )
    }

    @Test
    fun `update Book`() {
        val initialBook = assertCreate(createBook(), Book::class.java)
        val update = mapper.resourceToUpdate(initialBook.copy(name = "new name"))

        assertUpdate(
            updatePayload = update,
            responseType = Book::class.java
        )
    }

    @Test
    fun `get Book by Id`() {
        assertGetById(
            createPayload = createBook(),
            responseType = Book::class.java
        )
    }

    @Test
    fun `get Book Page`() {
        assertGetPage(
            resources = listOf(createBook(), createBook(), createBook()),
            pageable = Pageable.from(1, 2, Sort.of(Order("name", ASC, false))),
            responseType = Book::class.java
        )
    }

    @Test
    fun `delete Book`() {
        assertDelete(
            createPayload = createBook(),
            responseType = Book::class.java
        )
    }
}
