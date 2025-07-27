package net.blugrid.common.model.pagination

/**
 * Singleton implementation for unpaged requests
 * Replaces: io.micronaut.data.model.Pageable.UNPAGED
 */
object UnpagedRequest : Pageable {
    override val number: Int = 0
    override val size: Int = Int.MAX_VALUE
    override val offset: Long = 0L
    override val sort: Sort = Sort.unsorted()
    override val isPaged: Boolean = false
    override val isUnpaged: Boolean = true

    override fun next(): Pageable = this
    override fun previous(): Pageable = this
    override fun first(): Pageable = this
    override fun withPage(pageNumber: Int): Pageable = this
    override fun withSize(pageSize: Int): Pageable = this
    override fun withSort(sort: Sort): Pageable = this

    override fun toString(): String = "UnpagedRequest"
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}
