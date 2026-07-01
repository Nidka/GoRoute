package com.helkore.rutas.android.feature.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await

class FirebaseEmailAuthService(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun registerAndSendVerification(email: String, password: String) {
        runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        }.recoverCatching { error ->
            if (error is FirebaseAuthUserCollisionException) {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            } else {
                throw error
            }
        }

        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("No se pudo crear la cuenta de Firebase")

        currentUser.sendEmailVerification().await()
        firebaseAuth.signOut()
    }

    suspend fun requireVerifiedEmail(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (error: FirebaseAuthInvalidUserException) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseAuth.currentUser
                ?.sendEmailVerification()
                ?.await()
                ?: throw IllegalStateException("No se pudo crear la cuenta de Firebase")
            firebaseAuth.signOut()
            throw IllegalStateException("Te enviamos un correo para verificar tu cuenta")
        }

        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("No se pudo iniciar sesión en Firebase")

        currentUser.reload().await()
        if (!currentUser.isEmailVerified) {
            currentUser.sendEmailVerification().await()
            firebaseAuth.signOut()
            throw IllegalStateException("Debes verificar tu correo antes de iniciar sesión. Te reenviamos el correo de verificación")
        }

        firebaseAuth.signOut()
    }
}