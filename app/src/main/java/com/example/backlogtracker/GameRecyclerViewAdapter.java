package com.example.backlogtracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backlogtracker.db.Game;

import java.util.List;
import java.util.Objects;

public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.ViewHolder> {
    // Holds the list of games
    public final List<Game> games;
    // The name of the currentFragment
    public String currentFragment;
    // Holds the possible filterOn
    public String filterOn;
    // Holds the possible filterBy
    public String filterBy;

    /**
     * Constructor the sets all attributes to the passed in parameters
     * @param games: A list of Game objects
     * @param fragmentName: The name of the parent fragment
     * @param filterOn: The filterOn that's gets passed in
     * @param filterBy: The filterBy that's gets passed in
     */
    public GameRecyclerViewAdapter(List<Game> games, String fragmentName, String filterOn, String filterBy)
    {
        this.games = games;
        currentFragment = fragmentName;
        this.filterOn = filterOn;
        this.filterBy = filterBy;
    }

    /**
     * Clears games and add the new list to it
     * @param games: The new list Game object to be added
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addItems(List<Game> games)
    {
        this.games.clear();
        this.games.addAll(games);
        notifyDataSetChanged();
    }
    /**
     * Creates the card the displays game information
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Holds the view
        public View view;
        // Holds the course to display
        public Game game;
        // Holds the TextView components from the layout
        public TextView txtTitle, txtPlatform, txtRating;

        /**
         * Constructor for ViewHolder and sets the TextView components
         *
         * @param itemView: View related to the card layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtTitle = view.findViewById(R.id.r_title);
            txtPlatform = view.findViewById(R.id.r_platform);
            txtRating = view.findViewById(R.id.r_rating);
        }
    }

    @NonNull
    @Override
    public GameRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameRecyclerViewAdapter.ViewHolder holder, int position) {
        // Gets the game from the position from games
        final Game game = games.get(position);

        // If the game is not null
        if(game != null)
        {
            // Sets text elements
            holder.txtTitle.setText(game.getTitle());
            holder.txtPlatform.setText(game.getPlatform());
            holder.txtRating.setText(String.valueOf(game.getRating()));
        }

        // Sets the onClickListener for the card
        holder.view.setOnClickListener(v -> {
            // If the game is not null
            assert game != null;
            // Gets games id
            int game_id = game.getId();
            // If the current fragment is the MainActivityFragment
            if(Objects.equals(currentFragment, "main"))
            {
                // Creates a new action to navigate to the DetailsFragment
                var action = MainActivityFragmentDirections.actionMainActivityFragmentToDetailsFragment(currentFragment);
                // Sets up the argument that needs to passed to the next fragment
                action.setGameId(game_id);
                // Navigates to the next fragment
                Navigation.findNavController(v).navigate(action);
            }
            else
            {
                // If the current fragment is FilterRankFragment
                // Creates a new action to navigate to the DetailsFragments
                var action = FilterRankFragmentDirections.actionFilterRankFragmentToDetailsFragment(currentFragment);
                // Sets up the arguments that need to passed to the next fragment
                action.setGameId(game_id);
                action.setFilterBy(filterBy);
                action.setFilterOn(filterOn);
                // Navigates to the next fragment
                Navigation.findNavController(v).navigate(action);
            }
        });

    }

    /**
     * Get the number of objects that are in games
     * @return the number of objects in games
     */
    @Override
    public int getItemCount() {
        return games.size();
    }
}
