package com.fivestarmind.android.enum

/**
 * Contract that will allow Types with id to have generic implementation.
 */
interface IdentifierType<T> {

    val id: T

}