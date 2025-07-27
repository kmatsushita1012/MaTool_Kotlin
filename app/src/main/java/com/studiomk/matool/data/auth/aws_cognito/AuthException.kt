package com.studiomk.matool.data.auth.aws_cognito

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.exceptions.service.CodeMismatchException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidParameterException
import com.amplifyframework.auth.cognito.exceptions.service.LimitExceededException
import com.amplifyframework.auth.cognito.exceptions.service.TooManyRequestsException
import com.amplifyframework.auth.cognito.exceptions.service.UserNotConfirmedException
import com.amplifyframework.auth.cognito.exceptions.service.UserNotFoundException
import com.amplifyframework.auth.cognito.exceptions.service.UsernameExistsException
import com.amplifyframework.auth.exceptions.NotAuthorizedException


val AuthException.localizedMessageJa: String
    get() {
        val causeMessage = this.cause?.message?.lowercase() ?: ""
        return when (this) {
            is NotAuthorizedException -> {
                when {
                    causeMessage.contains("incorrect username or password") ->
                        "ユーザー名またはパスワードが間違っています。"
                    else -> "認証に失敗しました。"
                }
            }
            is InvalidParameterException -> {
                when {
                    causeMessage.contains("missing required parameter username") ->
                        "ユーザー名を入力してください。"
                    causeMessage.contains("no registered/verified email") ||
                            causeMessage.contains("no registered/verified phone_number") ->
                        "このアカウントはメールが未認証です。認証を完了してください。"
                    else -> "入力された情報に誤りがあります。"
                }
            }
            is UserNotConfirmedException ->
                "アカウントが確認されていません。メールを確認してください。"
            is UserNotFoundException ->
                "ユーザーが存在しません。"
            is UsernameExistsException ->
                "このユーザー名はすでに使用されています。"
            is CodeMismatchException ->
                "確認コードが正しくありません。"
            is LimitExceededException ->
                "操作が制限を超えました。時間をおいて再試行してください。"
            is TooManyRequestsException ->
                "短時間に操作しすぎました。"
            else ->
                "認証エラーが発生しました：${this.message ?: "詳細不明"}"
        }
    }
