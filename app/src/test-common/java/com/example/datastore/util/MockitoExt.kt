package com.example.datastore.util

import org.mockito.Mockito


/**
 * a kotlin friendly mock that handles generics
 */
inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

inline fun <reified T> any(): T = Mockito.any(T::class.java)