package com.example.shoppinglist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.AddShopListItemUseCase
import com.example.shoppinglist.domain.EditItemShopListUseCase
import com.example.shoppinglist.domain.GetItemByIdUseCase
import com.example.shoppinglist.domain.ShopItem

class ShopItemViewModel : ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val addShopListItemUseCase = AddShopListItemUseCase(repository)
    private val editItemShopListUseCase = EditItemShopListUseCase(repository)
    private val getItemByIdUseCase = GetItemByIdUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName
    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    private val _isClose = MutableLiveData<Unit>()
    val isClose: LiveData<Unit>
        get() = _isClose

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValue = validateInput(name, count)
        if (fieldsValue) {
            val shopItem = ShopItem(name, count, true)
            addShopListItemUseCase.addShopListItem(shopItem)
            finishWork()
        }
    }

    fun getShopItem(shopItemId: Int) {
        val item = getItemByIdUseCase.getItemById(shopItemId)
        _shopItem.value = item
    }

    fun editShopItem(name: String?, count: String?) {
        val nameParse = parseName(name)
        val countParse = parseCount(count)
        val fieldsValue = validateInput(nameParse, countParse)
        if (fieldsValue) {
            _shopItem.value?.let {
                val item = it.copy(name = nameParse, count = countParse)
                editItemShopListUseCase.editItem(item)
                finishWork()
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.toInt() ?: 0
        } catch (e: java.lang.Exception) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork(){
        _isClose.value = Unit
    }
}