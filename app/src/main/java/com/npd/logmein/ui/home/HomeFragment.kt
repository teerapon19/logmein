package com.npd.logmein.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.npd.logmein.AuthViewModel
import com.npd.logmein.FireStoreViewModel
import com.npd.logmein.R
import com.npd.logmein.obj.Item
import com.npd.logmein.recyclerview.ItemAdapter

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerview: RecyclerView = root.findViewById(R.id.recycler_view)
        val fireStoreViewModel = ViewModelProvider(requireActivity()).get(FireStoreViewModel::class.java)
        fireStoreViewModel.fetchItem()
        fireStoreViewModel.items.observe(requireActivity(), Observer {
            recyclerview.adapter = ItemAdapter(it)
        })

        recyclerview.layoutManager = LinearLayoutManager(context)
        return root
    }
}