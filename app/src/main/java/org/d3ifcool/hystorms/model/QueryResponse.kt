package org.d3ifcool.hystorms.model

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

data class QueryResponse(val packet: QuerySnapshot?, val error: FirebaseFirestoreException?)

