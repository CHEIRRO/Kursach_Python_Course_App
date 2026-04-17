package com.example.kursach_course.main_chapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kursach_course.R
import com.example.kursach_course.Topic

class RoadmapAdapter(
    private val topics: List<Topic>,
    private val completedTopics: Set<Int>, // set topicId пройденных тем
    private val onItemClick: (Topic) -> Unit
) : RecyclerView.Adapter<RoadmapAdapter.RoadmapViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadmapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_roadmap, parent, false)
        return RoadmapViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoadmapViewHolder, position: Int) {
        val topic = topics[position]
        val isCompleted = completedTopics.contains(topic.topicId)
        holder.bind(topic, position + 1, isCompleted, onItemClick)

        // Скрываем линию-соединитель для последнего элемента
        if (position == topics.size - 1) {
            holder.viewConnector.visibility = View.GONE
        } else {
            holder.viewConnector.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = topics.size

    class RoadmapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tvTopicNumber)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTopicTitle)
        //val ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)
        val viewConnector: View = itemView.findViewById(R.id.viewConnector)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // нужно добавить id в item_roadmap

        fun bind(topic: Topic, number: Int, isCompleted: Boolean, clickListener: (Topic) -> Unit) {
            tvNumber.text = number.toString()
            tvTitle.text = topic.title
            //ivStatus.setImageResource(
                //if (isCompleted) R.mipmap.ic_check else R.mipmap.ic_lock
            //)
            cardView.setOnClickListener { clickListener(topic) }
        }
    }
}

