package com.example.kursach_course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView

class TopicsAdapter(
    private val topics: List<Topic>,
    private val onItemClick: (Topic) -> Unit
) : RecyclerView.Adapter<TopicsAdapter.TopicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic_button, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position], onItemClick)
    }

    override fun getItemCount() = topics.size

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: AppCompatButton = itemView.findViewById(R.id.btnTopic)
        fun bind(topic: Topic, clickListener: (Topic) -> Unit) {
            button.text = topic.title
            button.setOnClickListener { clickListener(topic) }
        }
    }
}