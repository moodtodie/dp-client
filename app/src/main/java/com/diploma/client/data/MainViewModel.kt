package com.diploma.client.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainDb: MainDb
) : ViewModel() {

    fun getItems(): Flow<List<Item>> = mainDb.daoItem.getAll()

    fun findItem(uuid :UUID? = null , barcode: String? = null): Item? {
        return when {
            uuid != null -> mainDb.daoItem.findByUuid(uuid)
            barcode != null -> mainDb.daoItem.findByBarcode(barcode)
            else -> null
        }
    }

    suspend fun insertItem(item: Item) {
        mainDb.daoItem.insert(item)
    }

    suspend fun updateItem(item: Item) {
        mainDb.daoItem.update(item)
    }

    suspend fun deleteItem(item: Item) {
        mainDb.daoItem.delete(item)
    }

    fun getUsers(): Flow<List<User>> = mainDb.daoUser.getAll()

    fun findUser(uuid :UUID? = null , username: String? = null): User? {
        return when {
            uuid != null -> mainDb.daoUser.findByUUID(uuid)
            username != null -> mainDb.daoUser.findByUsername(username)
            else -> null
        }
    }

    suspend fun insertUser(user: User) {
        mainDb.daoUser.insert(user)
    }

    suspend fun updateUser(user: User) {
        mainDb.daoUser.update(user)
    }

    suspend fun deleteUser(user: User) {
        mainDb.daoUser.delete(user)
    }
}