package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*


@Composable fun <T> ustate(init: T): MutableState<T> = remember { mutableStateOf(init) }

@Composable fun <T> ustates(vararg inits: T): List<MutableState<T>> = inits.map { ustate(it) }
