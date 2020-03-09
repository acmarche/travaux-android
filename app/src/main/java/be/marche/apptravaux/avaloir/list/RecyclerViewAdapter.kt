package be.marche.apptravaux.avaloir.list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import java.util.*

class RecyclerViewAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<RecyclerViewAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var avaloirs = emptyList<Avaloir>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.messageView)
        val circleText : TextView = itemView.findViewById(R.id.tc_circle)
        val circle : CardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.avaloir_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = avaloirs[position]
        holder.wordItemView.text = current.latitude.toString()
        holder.circleText.text = holder.wordItemView.text.substring(0,1).capitalize()
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        holder.circle.setBackgroundColor(color)
    }

    internal fun setAvaloirs(avaloirs: List<Avaloir>) {
        this.avaloirs = avaloirs
        notifyDataSetChanged()
    }

    override fun getItemCount() = avaloirs.size
}