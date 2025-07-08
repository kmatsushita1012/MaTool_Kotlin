package com.studiomk.matool.core.others

data class EditManager<T : Any> private constructor(
    val value: T,
    private val undoStack: List<T>,
    private val redoStack: List<T>
) {
    constructor(initial: T) : this(initial, emptyList(), emptyList())

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun apply(update: (T) -> T): EditManager<T> {
        return EditManager(
            value = update(value),
            undoStack = undoStack + value,
            redoStack = emptyList()
        )
    }

    fun undo(): EditManager<T> {
        return if (undoStack.isNotEmpty()) {
            EditManager(
                value = undoStack.last(),
                undoStack = undoStack.dropLast(1),
                redoStack = redoStack + value
            )
        } else this
    }

    fun redo(): EditManager<T> {
        return if (redoStack.isNotEmpty()) {
            EditManager(
                value = redoStack.last(),
                undoStack = undoStack + value,
                redoStack = redoStack.dropLast(1)
            )
        } else this
    }
}
