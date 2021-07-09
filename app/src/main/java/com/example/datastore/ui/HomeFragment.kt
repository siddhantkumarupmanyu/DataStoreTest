package com.example.datastore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.datastore.R
import com.example.datastore.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args by navArgs<HomeFragmentArgs>()

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.homeUsername.text = args.username

        homeViewModel.messages.observe(viewLifecycleOwner) {
            binding.messagesTextview.text = getString(R.string.new_messages).format(it)
        }

        binding.generateMessages.setOnClickListener {
            homeViewModel.generateMessages()
        }

        binding.logout.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionLogout())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}