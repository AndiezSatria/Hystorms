package org.d3ifcool.hystorms.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User
import org.d3ifcool.hystorms.util.UserReference
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @UserReference private val userRef: CollectionReference
) {

    fun firebaseSignIn(
        email: String,
        pass: String
    ): MutableLiveData<DataOrException<User, Exception>> {
        val dataOrExceptionMutableLiveData: MutableLiveData<DataOrException<User, Exception>> =
            MutableLiveData()
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            var dataOrException: DataOrException<User, Exception> = DataOrException()
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    dataOrException = getUser(firebaseUser.uid)
                } else {
                    dataOrException.exception = task.exception
                }
                dataOrExceptionMutableLiveData.value = dataOrException
            }
        }
        return dataOrExceptionMutableLiveData
    }

    fun firebaseRegister(
        user: User,
        pass: String
    ): MutableLiveData<DataOrException<User, Exception>> {
        val dataOrExceptionMutableLiveData: MutableLiveData<DataOrException<User, Exception>> =
            MutableLiveData()
        firebaseAuth.createUserWithEmailAndPassword(user.email, pass)
            .addOnCompleteListener { task ->
                var dataOrException: DataOrException<User, Exception> = DataOrException()
                if (task.isSuccessful) {
                    dataOrException = setUser(user)
                } else {
                    dataOrException.exception = task.exception
                }
                dataOrExceptionMutableLiveData.value = dataOrException
            }
        return dataOrExceptionMutableLiveData
    }

    private fun getUser(uid: String): DataOrException<User, Exception> {
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        userRef.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result!!
                if (snapshot.exists()) {
                    dataOrException.data = snapshot.toObject(User::class.java)
                }
            } else {
                dataOrException.exception = task.exception
            }
        }
        return dataOrException
    }

    private fun setUser(user: User): DataOrException<User, Exception> {
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        val uidRef: DocumentReference = userRef.document(user.uid)
        uidRef.set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) dataOrException.data = user
            else dataOrException.exception = task.exception
        }

        return dataOrException
    }

}