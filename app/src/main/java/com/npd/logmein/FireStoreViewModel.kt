package com.npd.logmein

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.npd.logmein.obj.Item

class FireStoreViewModel : ViewModel() {

    val items = MutableLiveData<ArrayList<Item>>()

    fun fetchItem() {
        FirebaseFirestore.getInstance().collection("items").addSnapshotListener { value, _ ->
            value?.let {
                val list = ArrayList<Item>()
                for (item in it) {
                    list.add(item.toObject(Item::class.java))
                }
                items.value = list
            }
        }
    }

}