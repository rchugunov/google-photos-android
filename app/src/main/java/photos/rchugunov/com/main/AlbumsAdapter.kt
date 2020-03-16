package photos.rchugunov.com.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.photos.types.proto.Album
import kotlinx.android.synthetic.main.view_album.view.*
import photos.rchugunov.com.R

class AlbumsAdapter : RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder>() {

    var albumsList: List<Album> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_album, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.title.text = albumsList[position].title
    }

    class AlbumViewHolder(itemView: View, val title: TextView = itemView.title) :
        RecyclerView.ViewHolder(itemView)
}