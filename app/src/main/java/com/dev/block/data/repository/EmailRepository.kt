package com.dev.block.data.repository

import com.dev.block.R
import com.dev.block.data.BlockException
import com.dev.block.data.datasource.DataStoreHelper
import com.dev.block.data.network.EmailSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepository @Inject constructor(
    private val emailSender: EmailSender,
    private val dataStoreHelper: DataStoreHelper
) {
    fun sendEmail(
        to: String,
        title: String,
        content: String,
        contentDescription: String,
        extraDescription: String
    ): Flow<Unit> {
        return flow {
            emit(
                emailSender.sendEmail(
                    to = to,
                    title = title,
                    content = content,
                    contentDescription = contentDescription,
                    extraDescription = extraDescription
                )
            )
        }.catch {e ->
            e.printStackTrace()
            throw BlockException.MailSendFailException()
        }
    }

    fun sendEmail(
        title: String,
        content: String,
        contentDescription: String,
        extraDescription: String
    ): Flow<Unit> {
        return flow {
            val to = dataStoreHelper.getEmail().first()
                ?: throw BlockException.NoRegisteredEmailException()
            emit(
                emailSender.sendEmail(
                    to = to,
                    title = title,
                    content = content,
                    contentDescription = contentDescription,
                    extraDescription = extraDescription
                )
            )
        }.catch { e ->
            if (e !is BlockException.NoRegisteredEmailException)
                throw BlockException.MailSendFailException()
            else throw e
        }
    }

    fun storeEmail(email: String): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeEmail(email))
        }.catch {
            throw BlockException.DataStoreFailException(R.string.email)
        }
    }

    fun containsEmail(): Flow<Boolean> {
        return dataStoreHelper.getEmail().map { email ->
            !email.isNullOrBlank()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return email.length <= EMAIL_SIZE_MAX && emailPattern.matcher(email).matches()
    }

    companion object {
        const val EMAIL_SIZE_MAX = 254
    }
}