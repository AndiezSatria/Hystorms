package org.d3ifcool.hystorms.state

sealed class DataState<out T> {
    class Loading<out T> : DataState<T>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    data class Canceled(val exception: Exception) : DataState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Loading<*> -> "Loading..."
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled[exception=$exception]"
        }
    }

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun error(exception: Exception) = Error(exception)
        fun canceled(exception: Exception) = Canceled(exception)
    }
}
