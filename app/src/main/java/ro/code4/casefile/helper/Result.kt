package ro.code4.casefile.helper

/**
 * .:.:.:. Created by @henrikhorbovyi on 13/10/19 .:.:.:.
 */
sealed class Result<out T, out U> {
    class Failure<out U>(val error: U, val message: String = "") : Result<Nothing, U>()
    class Success<out T>(val data: T? = null) : Result<T, Nothing>()
    object Loading : Result<Nothing, Nothing>()

    fun handle(
        onSuccess: (T?) -> Unit = {},
        onFailure: (U) -> Unit = {},
        onLoading: () -> Unit = {}
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
            is Loading -> onLoading()
        }
    }
}
