package org.d3ifcool.hystorms.extension

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.d3ifcool.hystorms.constant.Action
import org.d3ifcool.hystorms.model.Device
import org.d3ifcool.hystorms.model.QueryResponse
import org.d3ifcool.hystorms.state.DataState
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirebaseExtension {
    suspend fun <T> Task<T>.await(): DataState<T> {
        if (isComplete) {
            val e = exception
            return if (e == null) {
                if (isCanceled)
                    DataState.Canceled(CancellationException("Task $this was cancelled normally."))
                else DataState.Success(result as T)

            } else {
                DataState.Error(e)
            }
        }
        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener {
                val e = exception
                if (e == null) {
                    if (isCanceled) cont.cancel() else cont.resume(DataState.Success(result as T))
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun Query.asFlow(): Flow<QuerySnapshot> {
        return callbackFlow {
            val callback = addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    close(ex)
                } else {
                    trySend(querySnapshot!!)
                }
            }
            awaitClose {
                callback.remove()
            }
        }
    }

    suspend fun Query.awaitRealtime() = suspendCancellableCoroutine<QueryResponse> { continuation ->
        addSnapshotListener { value, error ->
            if (error == null && continuation.isActive)
                continuation.resume(QueryResponse(value, null))
            else if (error != null && continuation.isActive)
                continuation.resume(QueryResponse(null, error))
        }
    }
}