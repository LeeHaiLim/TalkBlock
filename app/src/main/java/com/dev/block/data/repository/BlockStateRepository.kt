package com.dev.block.data.repository

import com.dev.block.R
import com.dev.block.data.BlockException
import com.dev.block.data.datasource.DataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BlockStateRepository @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) {
    fun storeBlockState(isOn: Boolean): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeBlockState(isOn))
        }.catch {
            throw BlockException.DataStoreFailException(R.string.block_state)
        }
    }

    fun getBlockState(): Flow<Boolean> {
        return dataStoreHelper.getBlockState().map { it ?: false }
    }
}