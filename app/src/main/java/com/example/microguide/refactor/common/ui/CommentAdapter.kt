package com.example.microguide.refactor.common.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.microguide.data.model.CommentModel
import com.example.microguide.databinding.ItemCommentBinding

class CommentAdapter : RecyclerView.Adapter<RefViewHolder>() {

    private var list: List<CommentModel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun updateComments(comments: List<CommentModel>) {
        list = comments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(inflater, parent, false)

        return RefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RefViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}

class RefViewHolder(
    private val binding: ItemCommentBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentModel) {
        binding.name.text = comment.name
        binding.body.text = comment.body
    }
}