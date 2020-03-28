package photos.rchugunov.com.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.photos.types.proto.Album
import kotlinx.android.synthetic.main.view_album.view.*
import photos.rchugunov.com.R

class AlbumsAdapter(
    private val clickCallback: (String) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {


    var albumsList: List<Album> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val viewHolder = AlbumViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_album, parent, false)
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val album = albumsList[position]
            clickCallback(album.id)
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumsList[position]
        holder.title.text = album.title

        Glide
            .with(holder.albumPhoto)
            .load(album.coverPhotoBaseUrl)
            .centerCrop()
            .into(holder.albumPhoto);
    }

    class AlbumViewHolder(
        itemView: View,
        val title: TextView = itemView.title,
        val albumPhoto: ImageView = itemView.albumPhoto
    ) : RecyclerView.ViewHolder(itemView)
}