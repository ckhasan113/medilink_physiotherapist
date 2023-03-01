package com.example.physiotherapist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.physiotherapist.R;
import com.example.physiotherapist.pojo.PackageDetails;

import java.util.List;

public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.PackageListAdapterViewHolder> {

    private Context context;
    private List<PackageDetails> packageDetails;

    private PackageListAdapterListener listener;

    public PackageListAdapter(Context context, List<PackageDetails> packageDetails) {
        this.context = context;
        this.packageDetails = packageDetails;
        listener = (PackageListAdapterListener) context;
    }

    @NonNull
    @Override
    public PackageListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.package_list_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PackageListAdapterViewHolder holder, int position) {

        final PackageDetails details = packageDetails.get(position);


        holder.packageName.setText(details.getPackageName());
        holder.price.setText(details.getPackagePrice());
        holder.startTime.setText(details.getStartTime());
        holder.endTime.setText(details.getEndTime());

        if (details.getStartTime().equals("Select")){
            holder.timeShow.setVisibility(View.INVISIBLE);
        }else {
            holder.timeShow.setVisibility(View.VISIBLE);
        }

        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPackageDetails(details);
            }
        });

    }

    @Override
    public int getItemCount() {
        return packageDetails.size();
    }

    class PackageListAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView packageName, price, startTime, endTime;

        private LinearLayout getDetailsLayout, timeShow;

        public PackageListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            getDetailsLayout = itemView.findViewById(R.id.getPackageDetails);

            packageName = itemView.findViewById(R.id.packageName);
            price = itemView.findViewById(R.id.price);
            startTime = itemView.findViewById(R.id.start);
            endTime = itemView.findViewById(R.id.end);
            timeShow = itemView.findViewById(R.id.time);
        }
    }

    public interface PackageListAdapterListener{
        void onPackageDetails(PackageDetails packageDetails);
    }
}
