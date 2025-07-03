package net.blugrid.api.example.factory

import io.github.serpro69.kfaker.faker
import net.blugrid.api.example.model.Book
import net.blugrid.api.example.model.BookCreate
import java.util.UUID

val faker = faker {}

fun createBook(
    uuid: UUID? = null
): BookCreate =
    BookCreate(
        uuid = uuid ?: UUID.randomUUID(),
        name = faker.random.randomString(6)
    )

fun Book.update(name: String = faker.random.randomString(6)): Book {
    return this.copy(
        name = name
    )
}
