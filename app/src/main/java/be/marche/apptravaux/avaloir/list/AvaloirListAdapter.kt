package be.marche.apptravaux.avaloir.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import timber.log.Timber

class AvaloirListAdapter internal constructor(

    private val listener: AvaloirListAdapterListener?
) : RecyclerView.Adapter<AvaloirListAdapter.AvaloirViewHolder>(), View.OnClickListener {

    interface AvaloirListAdapterListener {
        fun onAvaloirSelected(avaloir: Avaloir)
    }

    private var avaloirs = emptyList<Avaloir>() // Cached copy of words

    inner class AvaloirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewAvaloir = itemView.findViewById<CardView>(R.id.cardViewAvaloir)!!
        val avaloirLocationView: TextView = itemView.findViewById(R.id.avaloirLocationView)
        val avaloirPhoto = itemView.findViewById<ImageView>(R.id.avaloirPhotoView)
        val avaloirRue = itemView.findViewById<TextView>(R.id.avaloirRueView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaloirViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avaloir_list_item, parent, false)
        return AvaloirViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvaloirViewHolder, position: Int) {
        with(holder) {
            val avaloir = avaloirs[position]
            cardViewAvaloir.setOnClickListener(this@AvaloirListAdapter)
            cardViewAvaloir.tag = avaloir
            avaloirLocationView.text = holder.itemView.getContext().getString(
                R.string.avaloir_location,
                avaloir.latitude.toString(),
                avaloir.longitude.toString()
            )
            avaloirRue.text = avaloir.rue
            if (avaloir.imageUrl != null) {
                Picasso.get()
                    .load(avaloir.imageUrl)
                    .placeholder(R.drawable.ic_photo_library)
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
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