package org.d3ifcool.hystorms.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage
import org.d3ifcool.hystorms.data.Constant
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.util.ViewState
import java.io.File

class AuthRepository constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRef: CollectionReference,
    private val storageRef: FirebaseStorage
) {
    val authenticatedUser: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()
    val profileUploadedUser: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()
    val savedUser: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()

    val authenticatedUid: MutableLiveData<DataOrException<String, Exception>> = MutableLiveData()
    val loggedInUser: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()

    val resetPasswordResult: MutableLiveData<DataOrException<String, Exception>> = MutableLiveData()

    val viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NOTHING)

    private fun setState(viewStateIn: ViewState) {
        viewState.value = viewStateIn
    }

    fun resetState() {
        viewState.value = ViewState.NOTHING
    }

    fun firebaseSignIn(
        email: String,
        pass: String
    ): DataOrException<String, Exception> {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<String, Exception> = DataOrException()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    dataOrException.data = firebaseUser.uid
                    authenticatedUid.value = dataOrException
                    resetPasswordResult.value = dataOrException
                }
            } else {
                setState(ViewState.ERROR)
                task.exception?.let {
                    dataOrException.exception = it
                    resetPasswordResult.value = dataOrException
                }
            }
        }
        return dataOrException
    }

    fun resetPassword(
        email: String
    ) {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<String, Exception> = DataOrException()
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                dataOrException.data = "Email berhasil dikirim"
                setState(ViewState.SUCCESS)
            } else {
                setState(ViewState.ERROR)
                task.exception?.let {
                    dataOrException.exception = it
                }
            }
        }
    }

    fun firebaseRegister(
        user: User,
        pass: String
    ) {
        setState(ViewState.LOADING)
        var dataOrException: DataOrException<User, Exception>
        firebaseAuth.createUserWithEmailAndPassword(user.email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) user.uid = firebaseUser.uid
                    dataOrException = DataOrException(data = user)
                    authenticatedUser.value = dataOrException
                } else {
                    setState(ViewState.ERROR)
                    task.exception?.let {
                        dataOrException = DataOrException(exception = it)
                        authenticatedUser.value = dataOrException
                    }
                }
            }
    }

    fun firebaseUploadProfilePicture(
        file: File,
        user: User
    ) {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        val fileRef = storageRef.reference.child(
            "${Constant.PROFILE_PICTURE}/${file.name}"
        )
        val uploadTask = fileRef.putFile(Uri.fromFile(file))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    dataOrException.exception = it
                }
                profileUploadedUser.value = dataOrException
                setState(ViewState.ERROR)
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                user.photoUrl = downloadUrl
                dataOrException.data = user
                profileUploadedUser.value = dataOrException
            } else {
                task.exception?.let {
                    dataOrException.exception = it
                    profileUploadedUser.value = dataOrException
                }
                setState(ViewState.ERROR)
            }
        }
    }

    fun getUser(uid: String) {
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        userRef.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result!!
                if (snapshot.exists()) {
                    dataOrException.data = snapshot.toObject(User::class.java)
                    loggedInUser.value = dataOrException
                    setState(ViewState.SUCCESS)
                }
            } else {
                setState(ViewState.ERROR)
                task.exception?.let {
                    dataOrException.exception = it
                    loggedInUser.value = dataOrException
                }
            }
        }
    }

    fun setUser(user: User) {
        setState(ViewState.LOADING)
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        val uidRef: DocumentReference = userRef.document(user.uid)
        uidRef.set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                dataOrException.data = user
                savedUser.value = dataOrException
                setState(ViewState.SUCCESS)
            } else {
                task.exception?.let {
                    dataOrException.exception = it
                    savedUser.value = dataOrException
                }
                setState(ViewState.ERROR)
            }
        }
    }

    fun resetData() {
        authenticatedUser.value = DataOrException()
        savedUser.value = DataOrException()
        profileUploadedUser.value = DataOrException()
    }
}