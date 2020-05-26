package com.example.serviceexam.repositories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serviceexam.R
import com.example.serviceexam.repositories.network.Properties
import com.example.serviceexam.showDialog
import kotlinx.android.synthetic.main.fragment_list_repositories.*

/**
 * A simple [Fragment] subclass.
 */
class ListRepositoriesFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomAdapter
    private val list: MutableList<Properties> = ArrayList()
    private var recyclerView: RecyclerView? = null

    private lateinit var listRepositoriesViewModel: ListRepositoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listRepositoriesViewModel =
            ViewModelProvider(this).get(ListRepositoriesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list_repositories, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE

        onCreateComponent()

        initView()

        listRepositoriesViewModel.getRepositoriesProperties()

        registerObservers()
        registerListeners()
    }

    private fun registerListeners() {
        adapter.onClick = {
            Bundle().apply { putParcelable("DataUser", it) }.also {
                setFragmentResult("Data", it)
            }
            findNavController().navigate(ListRepositoriesFragmentDirections.navigateToDetailItem())
        }
    }

    private fun registerObservers() {
        listRepositoriesViewModel.listRepositories.observe(viewLifecycleOwner, Observer {
            loading.visibility = View.INVISIBLE
            adapter.addItems(it)
        })

        listRepositoriesViewModel.onError.observe(viewLifecycleOwner, Observer {
            showDialog(
                requireContext(),
                "Error",
                "Ha ocurrido un error",
                "Ok",
                { findNavController().navigateUp() })
        })
    }

    private fun initView() {
        initializeRecyclerView()
    }

    private fun onCreateComponent() {
        adapter = CustomAdapter(requireContext())
    }

    private fun initializeRecyclerView() {
        recyclerView = view?.findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
    }

}
