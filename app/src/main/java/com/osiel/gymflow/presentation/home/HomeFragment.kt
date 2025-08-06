package com.osiel.gymflow.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.osiel.gymflow.databinding.FragmentHomeBinding
import com.osiel.gymflow.presentation.adapter.SectionHeaderAdapter
import com.osiel.gymflow.presentation.adapter.WorkoutAdapter
import com.osiel.gymflow.presentation.auth.LoginActivity
import com.osiel.gymflow.presentation.viewmodel.AuthViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModel()
    private val workoutViewModel: WorkoutViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        observeWorkouts()
    }

    private fun observeWorkouts() {
        viewLifecycleOwner.lifecycleScope.launch {
            workoutViewModel.userWorkouts.collectLatest { userList ->
                workoutViewModel.suggestedWorkouts.collectLatest { suggestedList ->

                    val adapters = mutableListOf<androidx.recyclerview.widget.RecyclerView.Adapter<*>>()

                    if (userList.isNotEmpty()) {
                        adapters.add(SectionHeaderAdapter("Meus treinos"))
                        adapters.add(WorkoutAdapter(userList))
                    }

                    if (suggestedList.isNotEmpty()) {
                        adapters.add(SectionHeaderAdapter("Sugestão de treinos"))
                        adapters.add(WorkoutAdapter(suggestedList))
                    }

                    binding.workoutRecycler.adapter = ConcatAdapter(adapters)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
