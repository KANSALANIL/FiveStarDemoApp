package com.fivestarmind.android.enum

enum class EnumHelper {
    INSTANCE;

    /**
     * This will return [Enum] constant out of provided [Enum] values
     * with the specified id.
     *
     * @param id     the id of the constant to return.
     * @param values the [Enum] constants of specified type.
     * @return the [Enum] constant.
     */
    fun <T : IdentifierType<S>, S> valueOf(id: S?, values: Array<T>): T? {
        require(values[0].javaClass.isEnum) { "Values provided to scan is not an Enum" }

        var type: T? = null
        var i = 0

        while (i < values.size && type == null) {
            if (values[i].id == id)
                type = values[i]

            i++
        }

        return type
    }

}