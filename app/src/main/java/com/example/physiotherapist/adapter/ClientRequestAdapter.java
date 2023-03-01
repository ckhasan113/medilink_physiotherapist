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
import com.example.physiotherapist.pojo.ClientRequest;

import java.util.List;

public class ClientRequestAdapter extends RecyclerView.Adapter<ClientRequestAdapter.ClientRequestAdapterViewHolder> {

    private Context context;
    private List<ClientRequest> clientRequestList;

    private ClientRequestAdapterListener listener;

    public ClientRequestAdapter(Context context, List<ClientRequest> clientRequestList) {
        this.context = context;
        this.clientRequestList = clientRequestList;
        listener = (ClientRequestAdapterListener) context;
    }

    @NonNull
    @Override
    public ClientRequestAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClientRequestAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.request_list_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRequestAdapterViewHolder holder, int position) {

        final ClientRequest request = clientRequestList.get(position);

        holder.clientName.setText(request.getPatientDetails().getFirstName()+" "+request.getPatientDetails().getLastName());
        holder.packageName.setText(request.getPackageDetails().getPackageName());
        holder.date.setText(request.getBookingDate());

        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRequestDetails(request);
            }
        });

    }

    @Override
    public int getItemCount() {
        return clientRequestList.size();
    }

    class ClientRequestAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView packageName, clientName, date;

        private LinearLayout getDetailsLayout;

        public ClientRequestAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            getDetailsLayout = itemView.findViewById(R.id.getRequestDetails);

            clientName = itemView.findViewById(R.id.clientName);
            packageName = itemView.findViewById(R.id.clientPackageName);
            date = itemView.findViewById(R.id.issueDate);
        }
    }

    public interface ClientRequestAdapterListener{
        void onRequestDetails(ClientRequest cr);
    }
}
