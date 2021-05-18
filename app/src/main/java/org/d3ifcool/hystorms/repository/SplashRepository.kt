package org.d3ifcool.hystorms.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import org.d3ifcool.hystorms.model.DataOrException
import org.d3ifcool.hystorms.model.User

class SplashRepository constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRef: CollectionReference
) {
    val user: MutableLiveData<DataOrException<User, Exception>> = MutableLiveData()

    fun checkIfUserIsAuthenticatedInFirebase(): Boolean = firebaseAuth.currentUser != null

    fun getFirebaseUid(): String? {
        val user = firebaseAuth.currentUser
        return user?.uid
    }

    fun getUserDataFromFirestore(uid: String) {
        val dataOrException: DataOrException<User, Exception> = DataOrException()
        userRef.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userDoc = task.result!!
                if (userDoc.exists()) {
                    dataOrException.data = userDoc.toObject(User::class.java)
                    user.value = dataOrException
                }
            } else {
                task.exception?.let {
                    dataOrException.exception = it
                    user.value = dataOrException
                }
            }
        }
    }
}