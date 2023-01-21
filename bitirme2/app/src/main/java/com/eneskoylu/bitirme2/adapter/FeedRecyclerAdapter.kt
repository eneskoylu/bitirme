package com.eneskoylu.bitirme2.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eneskoylu.bitirme2.databinding.RecyclerRowBinding
import com.eneskoylu.bitirme2.model.Adverta
import com.squareup.picasso.Picasso


class FeedRecyclerAdapter(val advertList : ArrayList<Adverta>) : RecyclerView.Adapter<FeedRecyclerAdapter.AdvertHolder>(){

    class AdvertHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertHolder {

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdvertHolder(binding)


    }

    override fun onBindViewHolder(holder: AdvertHolder, position: Int) {

        holder.binding.recyclerEmailText.text = advertList.get(position).email
        holder.binding.recyclerCommentText.text = advertList.get(position).comment
        holder.binding.recyclerCityText.text = advertList.get(position).city
        holder.binding.recyclerDistrictText.text = advertList.get(position).district
        holder.binding.recyclerTransmissionText.text = advertList.get(position).transmission
        holder.binding.recyclerAddressText.text = advertList.get(position).address
        Picasso.get().load(advertList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return advertList.size

    }

}
