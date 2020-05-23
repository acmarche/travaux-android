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
import coil.api.load

class AvaloirListAdapter internal constructor(

    private val listener: AvaloirListAdapterListener?
) : RecyclerView.Adapter<AvaloirListAdapter.AvaloirViewHolder>(), View.OnClickListener {

    interface AvaloirListAdapterListener {
        fun onAvaloirSelected(avaloir: Avaloir)
    }

    private var avaloirs = emptyList<Avaloir>() // Cached copy of words

    inner class AvaloirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avaloirCardView = itemView.findViewById<CardView>(R.id.avaloirCardView)!!
        val avaloirPhotoView = itemView.findViewById<ImageView>(R.id.avaloirPhotoView)
        val avaloirRueView = itemView.findViewById<TextView>(R.id.avaloirRueView)
        val avaloirLocaliteView = itemView.findViewById<TextView>(R.id.avaloirLocaliteView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaloirViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avaloir_list_item, parent, false)
        return AvaloirViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvaloirViewHolder, position: Int) {
        with(holder) {
            val avaloir = avaloirs[position]
            avaloirCardView.setOnClickListener(this@AvaloirListAdapter)
            avaloirCardView.tag = avaloir
            avaloirRueView.text = "${avaloir.rue}  ${avaloir.numero}"
            avaloirLocaliteView.text = avaloir.localite
            if (avaloir.imageUrl != null) {
                avaloirPhotoView.load(avaloir.imageUrl) {
                    placeholder(R.drawable.ic_photo_library)
                }
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
            R.id.avaloirCardView -> listener?.onAvaloirSelected(view.tag as Avaloir)
        }
    }
}