package be.marche.apptravaux.avaloir.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.*

class AvaloirListAdapter internal constructor(

    private val listener: AvaloirListAdapterListener?
) : RecyclerView.Adapter<AvaloirListAdapter.WordViewHolder>(), View.OnClickListener {

    interface AvaloirListAdapterListener {
        fun onAvaloirSelected(avaloir: Avaloir)
    }

    private var avaloirs = emptyList<Avaloir>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewAvaloir = itemView.findViewById<CardView>(R.id.cardViewAvaloir)!!
        val avaloirLocationView: TextView = itemView.findViewById(R.id.avaloirLocationView)
        val avaloirPhoto = itemView.findViewById<ImageView>(R.id.avaloirPhotoView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avaloir_list_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        with(holder) {
            val avaloir = avaloirs[position]
            cardViewAvaloir.setOnClickListener(this@AvaloirListAdapter)
            cardViewAvaloir.tag = avaloir
            holder.avaloirLocationView.text =
                avaloir.latitude.toString() + "," + avaloir.latitude.toString()
            if (avaloir.imageUrl != null && avaloir.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(avaloir.imageUrl)
                    .placeholder(R.drawable.ic_photo_library)
                    .into(avaloirPhoto)
            }
        }
    }

    internal fun setAvaloirs(avaloirs: List<Avaloir>) {
        this.avaloirs = avaloirs
        notifyDataSetChanged()
    }

    override fun getItemCount() = avaloirs.size

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cardViewAvaloir -> listener?.onAvaloirSelected(view.tag as Avaloir)
        }
    }
}