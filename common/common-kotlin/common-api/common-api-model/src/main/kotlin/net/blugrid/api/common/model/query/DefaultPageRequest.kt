package net.blugrid.api.common.model.query

import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Default page request create")
data class DefaultPageRequest(
    @Schema(description = "The page number to retrieve, starting from 0.", example = "0")
    val page: Int,

    @Schema(description = "The number of records to retrieve per page.", example = "20")
    private val size: Int,

    @Schema(description = "The sorting criteria to apply to the page request.", example = "Sort.by('name').ascending()")
    private val sort: Sort? = Sort.unsorted()
) : Pageable {

    init {
        require(size >= 0) { "Page size must not be less than zero!" }
        require(page >= 0) { "Page index must not be less than zero!" }
    }

    override fun getNumber(): Int = page
    override fun next(): Pageable = DefaultPageRequest(page + 1, size, getSort())
    override fun getSort(): Sort = sort ?: Sort.unsorted()
    override fun getSize(): Int = size
    override fun getOffset(): Long = page.toLong() * size.toLong()
    fun hasPrevious(): Boolean = page > 0
    fun first(): Pageable = DefaultPageRequest(0, page, sort)
    override fun previous(): Pageable = if (page == 0) this else DefaultPageRequest(page - 1, size, sort)
    fun previousOrFirst(): Pageable = if (hasPrevious()) previous() else first()
}
