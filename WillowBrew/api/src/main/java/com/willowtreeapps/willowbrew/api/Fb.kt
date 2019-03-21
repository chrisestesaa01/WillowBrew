package com.willowtreeapps.willowbrew.api

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class Fb {

    fun foo() {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

    }
}