package net.blugrid.platform.testing.factory

interface GenericResourceFactory<T> {

    fun create(): T

    fun update(source: T, update: T): T
}
