package com.dev.block.data.repository

import com.dev.block.R
import com.dev.block.data.BlockException
import com.dev.block.data.datasource.DataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import javax.inject.Inject

class PasswordRepository @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) {
    fun storeHashedPassword(pw: String): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storePassword(sha256(pw)))
        }.catch {
            throw BlockException.DataStoreFailException(R.string.pw)
        }
    }

    fun isPasswordMatch(pw: String): Flow<Boolean> {
        return flow {
            emit(dataStoreHelper.getPassword().firstOrNull()?.equals(sha256(pw)) ?: true)
        }
    }

    fun isPasswordValid(pw: String): Boolean {
        return pw.matches(PW_PATTERN.toRegex())
    }

    fun generatePassword(): String {
        val length = (PW_SIZE_MIN..PW_SIZE_MAX).random()
        val pwBuilder = StringBuilder()
        repeat(length) {
            pwBuilder.append(
                when (val chars = PwChars.entries.random()) {
                    PwChars.PW_CHARS_LOWER -> chars.chars.random()
                    PwChars.PW_CHARS_UPPER -> chars.chars.random()
                    PwChars.PW_CHARS_NUMBER -> chars.chars.random()
                }
            )
        }
        return pwBuilder.toString()
    }

    private fun sha256(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val PW_SIZE_MIN = 8
        const val PW_SIZE_MAX = 14
        const val PW_PATTERN = "^[a-zA-Z0-9]{$PW_SIZE_MIN,$PW_SIZE_MAX}$"

        private enum class PwChars(val chars: CharRange) {
            PW_CHARS_LOWER(('a'..'z')),
            PW_CHARS_UPPER(('A'..'Z')),
            PW_CHARS_NUMBER(('0'..'9'))
        }
    }
}