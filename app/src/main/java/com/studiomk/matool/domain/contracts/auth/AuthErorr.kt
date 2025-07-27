package com.studiomk.matool.domain.contracts.auth

sealed class AuthError(override val message: String) : Exception() {
    class Network(message: String) : AuthError(message)
    class Encoding(message: String) : AuthError(message)
    class Decoding(message: String) : AuthError(message)
    class Unknown(message: String) : AuthError(message)

    val localizedDescription: String
        get() = when (this) {
            is Network -> "Network Error: $message"
            is Encoding -> "Encoding Error: $message"
            is Decoding -> "Decoding Error: $message"
            is Unknown -> message
        }
}