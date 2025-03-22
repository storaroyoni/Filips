package com.filips.health.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.filips.health.R
import com.filips.health.databinding.FragmentForumBinding
import com.filips.health.databinding.ItemForumPostBinding
import com.filips.health.model.ForumPost
import com.filips.health.model.HealthStats
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class ForumFragment : Fragment() {
    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCreatePost: FloatingActionButton
    private lateinit var adapter: ForumAdapter
    private val posts = mutableListOf<ForumPost>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        fabCreatePost = binding.fabCreatePost

        setupRecyclerView()
        setupClickListeners()
        loadInitialPosts()
        observeNewPosts()
    }

    private fun observeNewPosts() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ForumPost>("new_post")?.observe(
            viewLifecycleOwner
        ) { post ->
            post?.let {
                addNewPost(it)
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<ForumPost>("new_post")
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ForumAdapter(posts) { post ->
            // Navigate to details
            val intent = Intent(context, PostDetailsActivity::class.java).apply {
                putExtra("POST_ID", post.id)
                putExtra("TITLE", post.title)
                putExtra("DESCRIPTION", post.description)
                putExtra("AUTHOR", post.authorName)
                putExtra("STEPS", post.healthStats.steps)
                putExtra("HEART_RATE", post.healthStats.heartRate)
                putExtra("SLEEP_HOURS", post.healthStats.sleepHours)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@ForumFragment.adapter
        }
    }

    private fun setupClickListeners() {
        fabCreatePost.setOnClickListener {
            findNavController().navigate(R.id.action_forum_to_create_post)
        }
    }

    private fun addNewPost(post: ForumPost) {
        posts.add(0, post)
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    private fun loadInitialPosts() {
        posts.clear()
        posts.addAll(getSamplePosts())
        adapter.notifyDataSetChanged()
    }

    private fun getSamplePosts(): List<ForumPost> {
        return listOf(
            ForumPost(
                id = UUID.randomUUID().toString(),
                title = "Today's Workout Achievement",
                description = "Had a great cardio session! Check out my stats:",
                isAnonymous = true,
                authorName = "Anonymous User 1",
                timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                healthStats = HealthStats(
                    steps = "12,500",
                    heartRate = "75 bpm",
                    sleepHours = "7.5 hrs"
                )
            ),
            ForumPost(
                id = UUID.randomUUID().toString(),
                title = "Personal Best in Steps!",
                description = "Finally reached my step goal today!",
                isAnonymous = true,
                authorName = "Anonymous User 2",
                timestamp = System.currentTimeMillis() - 18000000, // 5 hours ago
                healthStats = HealthStats(
                    steps = "15,000",
                    heartRate = "68 bpm",
                    sleepHours = "8 hrs"
                )
            ),
            ForumPost(
                id = UUID.randomUUID().toString(),
                title = "Recovery Day Stats",
                description = "Taking it easy today but still staying active",
                isAnonymous = true,
                authorName = "Anonymous User 3",
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                healthStats = HealthStats(
                    steps = "8,000",
                    heartRate = "62 bpm",
                    sleepHours = "9 hrs"
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ForumAdapter(
    private val posts: List<ForumPost>,
    private val onPostClick: (ForumPost) -> Unit
) : RecyclerView.Adapter<ForumAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemForumPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: ForumPost) {
            binding.apply {
                titleTextView.text = post.title
                contentTextView.text = post.description
                authorTextView.text = post.authorName
                dateTextView.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(Date(post.timestamp))
                
                stepsTextView.text = post.healthStats.steps
                heartRateTextView.text = post.healthStats.heartRate
                sleepHoursTextView.text = post.healthStats.sleepHours

                root.setOnClickListener { onPostClick(post) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForumPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
} 