package com.studiomk.matool.core.others

fun <T> List<T>.add(element: T): List<T> = this + element

fun <T> List<T>.insert(index: Int, element: T): List<T> {
    require(index in 0..size) { "Index out of bounds" }
    val result = ArrayList<T>(size + 1)
    for (i in 0 until index) result.add(this[i])
    result.add(element)
    for (i in index until size) result.add(this[i])
    return result
}

fun <T> List<T>.set(index: Int, element: T): List<T> {
    require(index in 0 until size) { "Index out of bounds" }
    return mapIndexed { i, v -> if (i == index) element else v }
}

fun <T> List<T>.delete(index: Int): List<T> {
    require(index in 0 until size) { "Index out of bounds" }
    val result = ArrayList<T>(size - 1)
    for (i in 0 until size) {
        if (i != index) result.add(this[i])
    }
    return result
}

fun <T> List<T>.remove(element: T): List<T> {
    val idx = indexOf(element)
    return if (idx == -1) this else delete(idx)
}

fun <T> List<T>.removeIf(predicate: (T) -> Boolean): List<T> {
    val idx = indexOfFirst(predicate)
    return if (idx == -1) this else delete(idx)
}

fun <T, ID> List<T>.replace(newElement: T): List<T> where T : Identifiable<ID> {
    val idx = indexOfFirst { it.id == newElement.id }
    return if (idx == -1) this else set(idx, newElement)
}

fun <T, ID> List<T>.find(id: ID): T? where T : Identifiable<ID> {
    return firstOrNull { it.id == id }
}

fun <T, ID> List<T>.firstIndex(id: ID): Int where T : Identifiable<ID> {
    return indexOfFirst { it.id == id }
}


