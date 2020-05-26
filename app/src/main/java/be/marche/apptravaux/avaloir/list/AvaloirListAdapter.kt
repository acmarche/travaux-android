package be.marche.apptravaux.avaloir.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.databinding.AvaloirListItemBinding
import coil.api.load

class AvaloirListAdapter internal constructor(

    private val listener: AvaloirListAdapterListener?
) : RecyclerView.Adapter<AvaloirListAdapter.AvaloirViewHolder>(), View.OnClickListener {

    interface AvaloirListAdapterListener {
        fun onAvaloirSelected(avaloir: Avaloir)
    }

    private var avaloirs = emptyList<Avaloir>() // Cached copy of words

    inner class AvaloirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AvaloirListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaloirViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.avaloir_list_item, parent, false)
        return AvaloirViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvaloirViewHolder, position: Int) {
        with(holder) {
            val avaloir = avaloirs[position]
            binding.avaloirCardView.setOnClickListener(this@AvaloirListAdapter)
            binding.avaloirCardView.tag = avaloir

            val builder = StringBuilder()
            builder.append(avaloir.rue)
            if (avaloir.numero != null) {
                builder.append(' ')
                builder.append(avaloir.numero)
            }
            binding.avaloirRueView.text = builder.toString()

            binding.avaloirLocaliteView.text = avaloir.localite
            if (avaloir.imageUrl != null) {
                binding.avaloirPhotoView.load(avaloir.imageUrl) {
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