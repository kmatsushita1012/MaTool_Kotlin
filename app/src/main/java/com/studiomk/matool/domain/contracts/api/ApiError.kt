package com.studiomk.matool.domain.contracts.api


sealed class ApiError : Exception() {
    data class Network(override val message: String) : ApiError()
    data class Encoding(override val message: String) : ApiError()
    data class Decoding(override val message: String) : ApiError()
    data class Unauthorized(override val message: String) : ApiError()
    data class Unknown(override val message: String) : ApiError()

    val localizedDescription: String
        get() = when (this) {
            is Network -> "通信中に問題が発生しました。 \n${message}"
            is Encoding -> "データの変換中に問題が発生しました。 \n${message}"
            is Decoding -> "受け取ったデータの読み取りに失敗しました。 \n${message}"
            is Unauthorized -> "ログインの有効期限が切れました。\nもう一度ログインしてください。 \n${message}"
            is Unknown -> "予期しないエラーが発生しました。 \n${message}"
        }
}