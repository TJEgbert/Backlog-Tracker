package com.example.backlogtracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backlogtracker.JSONclasses.SearchResults;

import java.util.List;

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.ViewHolder> {
    // Holds the list of SearchResults
    public List<SearchResults> results;

    /**
     * Construct sets the results with the passed in parameter
     * @param results: A list of SearchResults
     */
    public SearchResultsRecyclerViewAdapter(List<SearchResults> results) {
        this.results = results;
    }

    /**
     * Adds new searchResults to this.courses
     *
     * @param results: List of searchResults
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addItems(List<SearchResults> results) {
        // Clears and adds all courses
        this.results.clear();
        this.results.addAll(results);
        // Notifies that course list has changed
        notifyDataSetChanged();
    }

    /**
     * Creates the card the displays searchResult information
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Holds the view
        public View view;
        // Holds the TextView components from the layout
        public TextView txtName;

        /**
         * Constructor for ViewHolder and sets the TextView components
         *
         * @param itemView: View related to the card layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtName = view.findViewById(R.id.search_name);
        }
    }

    @NonNull
    @Override
    public SearchResultsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerViewAdapter.ViewHolder holder, int position) {
        // Gets the searchResult for the passed position
        final SearchResults result = results.get(position);

        // If the course exists
        if (result != null) {
            // Sets UI elements
            holder.txtName.setText(result.getName());
        }

        // Sets the OnClickListener
        holder.view.setOnClickListener(v -> {
            //  result is not null
            assert result != null;
            // Creates a new action to navigate to the SearchCreateFragment
            var action = SearchFragmentDirections.actionSearchFragmentToSearchCreateFragment(result.getSearchTitle());
            // Sets up the argument that needs to passed to the next fragment
            action.setGuid(result.getGuid());
            // Navigates to the next fragment
            Navigation.findNavController(v).navigate(action);
        });
    }

    /**
     * Get the number of objects that are in results
     * @return the number of objects in results
     */
    @Override
    public int getItemCount() {
        return results.size();
    }
}
