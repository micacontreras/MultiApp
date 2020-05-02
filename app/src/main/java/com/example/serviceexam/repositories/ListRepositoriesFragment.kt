package com.example.serviceexam.repositories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceexam.R
import com.example.serviceexam.repositories.network.Properties
import com.example.serviceexam.repositories.network.RepositoryApi
import com.example.serviceexam.showDialog
import kotlinx.android.synthetic.main.fragment_list_repositories.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class ListRepositoriesFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomAdapter
    private val list: MutableList<Properties> = ArrayList()
    private var recyclerView: RecyclerView? = null

    private var fragmentJob = Job()

    private val coroutineScope = CoroutineScope(
        fragmentJob + Dispatchers.Main
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_repositories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

        onCreateComponent()

        initView()

        adapter.onClick = {
            Bundle().apply { putParcelable("DataUser", it) }.also {
                setFragmentResult("Data", it)
            }
            findNavController().navigate(ListRepositoriesFragmentDirections.navigateToDetailItem())
        }
    }

    private fun initView() {
        initializeRecyclerView()
        getRepositoriesProperties()
    }

    private fun onCreateComponent() {
        adapter = CustomAdapter(requireContext(), list)
    }

    private fun initializeRecyclerView() {

        recyclerView = view?.findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
    }

    private fun getRepositoriesProperties() {
        coroutineScope.launch {
            val getPropertiesDeferred = RepositoryApi.retrofitService.getProperties()
            try {
                val listResult = getPropertiesDeferred.await()
                loading.visibility = View.INVISIBLE
                if (listResult.isNotEmpty()) {
                    listResult.forEach {
                        list.add(it)
                    }
                }
                adapter.addItems(list)
            } catch (e: Exception) {
                showDialog(
                    requireContext(),
                    "Error",
                    "Ha ocurrido un error",
                    "Ok",
                    { findNavController().navigateUp() })
            }

        }
    }

}
