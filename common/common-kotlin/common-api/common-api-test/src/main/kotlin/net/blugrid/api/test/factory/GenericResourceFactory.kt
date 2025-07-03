package net.blugrid.api.test.factory

interface GenericResourceFactory<T> {

    fun create(): T

    fun update(source: T, update: T): T
}
