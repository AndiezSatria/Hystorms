package org.d3ifcool.hystorms.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.util.ProfilePicture
import org.d3ifcool.hystorms.util.UserReference
import org.d3ifcool.hystorms.util.ViewState
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

class AuthRepository constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRef: CollectionReference,
    private val profilePicRef: StorageReference
) {
    var viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)

    private fun setState(viewStateIn: ViewState) {
        viewState.value = viewStateIn
    }

    fun firebaseSignIn(
        email: String,
        pass: String
    ): DataOrException<User, Exception> {
        setState(ViewState.LOADING)
        var dataOrException: DataOrException<User, Exception> = DataOrException()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    dataOrException = getUser(firebaseUser.uid)
                }
            } else {
                dataOrException.exception = task.exception
            }
        }
        return dataOrException
    }

    fun firebaseRegister(
        user: User,
        pass: String,
        file: File?
    ): DataOrException<User, Exception> {
        setState(ViewState.LOADING)
        var dataOrException: DataOrException<User, Exception> = DataOrException()
        firebaseAuth.createUserWithEmailAndPassword(user.email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) user.uid = currentUser.uid
                    if (file != null) {
                        val downloadUrl: DataOrException<String, Exception> =
                            firebaseUploadProfilePicture(file)
                        if (downloadUrl.data != null) {
                            user.photoUrl = downloadUrl.data
                            dataOrException = setUser(user)
                        } else dataOrException.exception = downloadUrl.exception
                    } else dataOrException = setUser(user)
                } else {
                    dataOrException.exception = task.exception
                }
            }
        return dataOrException
    }

    private fun firebaseUploadProfilePicture(
        file: File
    ): DataOrException<String, Exception> {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<String, Exception> = DataOrException()
        val uploadTask = profilePicRef.putFile(Uri.fromFile(file))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                dataOrException.exception = task.exception
                setState(ViewState.ERROR)
            }
            profilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                dataOrException.data = task.result.toString()
                setState(ViewState.SUCCESS)
            } else {
                dataOrException.exception = task.exception
                setState(ViewState.ERROR)
            }
        }
        return dataOrException
    }

    private fun getUser(uid: String): DataOrException<User, Exception> {
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        userRef.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result!!
                if (snapshot.exists()) {
                    dataOrException.data = snapshot.toObject(User::class.java)
                    setState(ViewState.SUCCESS)
                }
            } else {
                dataOrException.exception = task.exception
                setState(ViewState.ERROR)
            }
        }
        return dataOrException
    }

    private fun setUser(user: User): DataOrException<User, Exception> {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        val uidRef: DocumentReference = userRef.document(user.uid)
        uidRef.set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) dataOrException.data = user
            else dataOrException.exception = task.exception
        }

        return dataOrException
    }

}