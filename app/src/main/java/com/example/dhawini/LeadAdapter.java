package com.example.dhawini;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.LeadViewHolder> {

    private final List<Lead> leads;

    public LeadAdapter(List<Lead> leads) {
        this.leads = leads;
    }

    @NonNull
    @Override
    public LeadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lead, parent, false);
        return new LeadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadViewHolder holder, int position) {
        Lead lead = leads.get(position);
        holder.bind(lead);
    }

    @Override
    public int getItemCount() {
        return leads.size();
    }

    public void updateLeads(List<Lead> newLeads) {
        final List<Lead> oldLeads = new ArrayList<>(this.leads);
        final LeadDiffCallback diffCallback = new LeadDiffCallback(oldLeads, newLeads);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.leads.clear();
        this.leads.addAll(newLeads);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class LeadViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeadName, tvLeadStatus, tvLeadPriority;

        public LeadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLeadName = itemView.findViewById(R.id.tv_lead_name);
            tvLeadStatus = itemView.findViewById(R.id.tv_lead_status);
            tvLeadPriority = itemView.findViewById(R.id.tv_lead_priority);
        }

        void bind(Lead lead) {
            tvLeadName.setText(lead.name);
            tvLeadStatus.setText(lead.status);
            tvLeadPriority.setText(lead.priority);
        }
    }

    private static class LeadDiffCallback extends DiffUtil.Callback {
        private final List<Lead> oldLeadList;
        private final List<Lead> newLeadList;

        public LeadDiffCallback(List<Lead> oldLeadList, List<Lead> newLeadList) {
            this.oldLeadList = oldLeadList;
            this.newLeadList = newLeadList;
        }

        @Override
        public int getOldListSize() {
            return oldLeadList.size();
        }

        @Override
        public int getNewListSize() {
            return newLeadList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // In a real app, you'd use a unique ID. Here we assume the name is unique.
            return Objects.equals(oldLeadList.get(oldItemPosition).name, newLeadList.get(newItemPosition).name);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            final Lead oldLead = oldLeadList.get(oldItemPosition);
            final Lead newLead = newLeadList.get(newItemPosition);

            return Objects.equals(oldLead.name, newLead.name) &&
                   Objects.equals(oldLead.status, newLead.status) &&
                   Objects.equals(oldLead.priority, newLead.priority);
        }
    }
}
