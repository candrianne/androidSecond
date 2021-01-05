package com.example.androidtests.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtests.R
import com.example.androidtests.models.NetworkError
import com.example.androidtests.models.User
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference
import com.example.androidtests.viewModels.UserViewModel

class Ranking : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter : RankingAdapter
    private lateinit var visibleLayout : ConstraintLayout
    private lateinit var errorImageView : ImageView
    private lateinit var progressBar : ProgressBar
    private lateinit var userRank : TextView
    private lateinit var userName : TextView
    private lateinit var userPoints : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_ranking, container, false)
        visibleLayout = view.findViewById(R.id.visibleLayout)
        errorImageView = view.findViewById(R.id.errorImageView)
        progressBar = view.findViewById(R.id.rankingProgressBar)
        userRank = view.findViewById(R.id.yourRankInputTextView)
        userName = view.findViewById(R.id.yourRankNameInputTextView)
        userPoints = view.findViewById(R.id.yourPointsInputTextView)

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        recyclerView = view.findViewById(R.id.rankingRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = RankingAdapter()
        recyclerView.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner, Observer { displayRanking(it)})
        viewModel.error.observe(viewLifecycleOwner, Observer { displayErrorScreen(it) })

        sendRequest()
        return view
    }

    private fun sendRequest() {
        viewModel.getAllUsers()
        visibleLayout.visibility = View.GONE
        errorImageView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun displayRanking(users : List<User>) {
        visibleLayout.visibility = View.VISIBLE
        errorImageView.visibility = View.GONE
        progressBar.visibility = View.GONE
        val logedInUser = SaveSharedPreference.getLogedInUser(context)
        userRank.text = (users.indexOfFirst { user -> user.id == logedInUser.id}).plus(1).toString()
        userName.text = logedInUser.firstName
        userPoints.text = logedInUser.score.toString()
        adapter.setUsers(users)
    }

    private fun displayErrorScreen(error: NetworkError?) {
        progressBar.visibility = View.GONE
        if (error == null) {
            errorImageView.visibility = View.GONE
            visibleLayout.visibility = View.VISIBLE
            return
        }
        errorImageView.visibility = View.VISIBLE
        visibleLayout.visibility = View.GONE
        errorImageView.setImageDrawable(resources.getDrawable(error.errorDrawable,
                requireActivity().theme))
    }

    private class RankingAdapter : RecyclerView.Adapter<RankingViewHolder>() {
        var usersList : MutableList<User?>? = mutableListOf()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ranking_item_layout, parent,false) as LinearLayout
            return RankingViewHolder(v)
        }

        override fun getItemCount(): Int {
            return usersList?.size ?: 0
        }

        override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
            val user : User? = usersList?.get(position)
            holder.rank.text = (usersList?.indexOf(user)?.plus(1)).toString()
            holder.name.text = user?.firstName
            holder.points.text = user?.score.toString()
        }

        fun setUsers(users : List<User?>) {
            usersList?.clear()
            usersList?.addAll(users)
            notifyDataSetChanged()
        }
    }

    private class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rank : TextView
        val name : TextView
        val points : TextView

        init {
            rank = itemView.findViewById(R.id.rankTextView)
            name = itemView.findViewById(R.id.rankNameTextView)
            points = itemView.findViewById(R.id.userPointsTextView)
        }
    }
}