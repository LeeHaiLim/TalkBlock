package com.dev.block.data

import androidx.annotation.StringRes
import com.dev.block.R

sealed class BlockException(@StringRes vararg val args: Int) : Exception() {
    class MailSendFailException(@StringRes resId: Int = R.string.email_send_failed) : BlockException(resId)
    class NoRegisteredEmailException(@StringRes resId: Int = R.string.email_not_registered) : BlockException(resId)
    class DataStoreFailException(@StringRes resId1: Int, @StringRes resId2: Int = R.string.data_store_failed) : BlockException(resId1, resId2)
}