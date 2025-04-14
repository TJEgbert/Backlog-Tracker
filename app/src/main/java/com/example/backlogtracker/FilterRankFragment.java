package com.example.backlogtracker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.backlogtracker.db.AppDatabase;
import com.example.backlogtracker.db.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FilterRankFragment extends Fragment {

    // Holds the view for the fragment
    private View view;
    // Holds the GameRecyclerViewAdapter for the fragment
    private GameRecyclerViewAdapter filterGameRecyclerViewAdapter;
    // Holds the passed in filterOn and filterBy values
    private String filterOn, filterBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filter_rank, container, false);

        // Get layout components and stores them for use
        TextView fragmentTitle = view.findViewById(R.id.search_rank_title);
        RecyclerView recyclerView = view.findViewById(R.id.filter_rank_recycler);
        Toolbar toolbar = view.findViewById(R.id.filter_rank_toolbar);

        // Get the passed values from arguments
        filterOn = FilterRankFragmentArgs.fromBundle(getArguments()).getFilterOn();
        filterBy = FilterRankFragmentArgs.fromBundle(getArguments()).getFilterBy();

        // Create a new CourseRecyclerViewAdapter
        filterGameRecyclerViewAdapter = new GameRecyclerViewAdapter(new ArrayList<>(),
                "filterRank", filterOn, filterBy);

        Context context = getContext();
        // Get the number of games in the list
        int columnCount = filterGameRecyclerViewAdapter.getItemCount();

        // Sets the type of layout fragment
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        // Finishes setting up recyclerView
        recyclerView.setAdapter(filterGameRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);

        // If filterOn is an empty String
        if(filterOn.isEmpty())
        {
            // Updates the title of the fragment
            fragmentTitle.setText(R.string.order_by_rank);
            // Get the list of game in descending order by rating
            new Thread(() -> {
                List<Game> returnList = AppDatabase.getInstance(getContext())
                        .gameDAO()
                        .getAllDescByRank();
                // Adds the list of game to filterGameRecyclerViewAdapter to show the results to the user
                requireActivity().runOnUiThread(() -> filterGameRecyclerViewAdapter.addItems(returnList));
            }).start();
        }
        else
        {
            /*
            * Create a thread to get the list of filtered games based on passed in by filterOn and
            * filterBy from the database.
            *
            * Genres a special case it will get all the games and search through the genre string
            * to find if it contains the filterOn.
            *
            * Then adds list of games to filterGameRecyclerViewAdapter to show the results to the user.
            */
            new Thread(() -> {
                List<Game> returnList = new ArrayList<>();
                switch(Objects.requireNonNull(filterOn))
                {
                    case "Developer":
                        returnList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getGamesByDeveloper(filterBy);
                        break;
                    case "Publisher":
                        returnList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getGamesByPublisher(filterBy);
                        break;
                    case "Platform":
                        returnList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getGamesByPlatform(filterBy);
                        break;
                    case "Genre":
                        List<Game> fullList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getAllList();

                        for (Game game: fullList)
                        {
                            if(game.getGenres().toLowerCase().contains(filterBy.toLowerCase()))
                            {
                                returnList.add(game);
                            }
                        }
                    break;
                    case "Format":
                        returnList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getGamesByFormat(filterBy);
                    break;
                    case "Rating":
                        returnList = AppDatabase.getInstance(getContext())
                                .gameDAO()
                                .getGamesByRating(Integer.parseInt(filterBy));
                        break;
                }
                List<Game> finalReturnList = returnList;
                requireActivity().runOnUiThread(() -> filterGameRecyclerViewAdapter.addItems(finalReturnList));
            }).start();

        }

        // Sets up the tool bar as an actionbar and adds the return button
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        // Sets up the menu provider
        MenuProvider provider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Clears the menu and inflates the menu specific to the fragment
                menu.clear();
                requireActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // If the back button is pressed
                // If the user came from the FilterGames
                if(!filterBy.isEmpty())
                {
                    // Creates a new action to navigate to the FilterRankFragmentToFilterGames
                    var action = FilterRankFragmentDirections.actionFilterRankFragmentToFilterGames();
                    // Sets up the arguments that need to passed to the next fragment
                    action.setFilterOn(filterOn);
                    action.setFilterBy(filterBy);
                    // Navigates to the next fragment
                    Navigation.findNavController(view).navigate(action);
                }
                else
                {
                    // If the user came from the MainActivityFragment
                    // Navigates back to the MainActivityFragment
                    Navigation.findNavController(view).navigate(R.id.action_filterRankFragment_to_mainActivityFragment);
                }
                return true;
            }
        };

        // Gets the MenuHost
        MenuHost host = requireActivity();
        // Finishes setting up the menu
        host.addMenuProvider(provider, getViewLifecycleOwner());
        actionBar.setDisplayHomeAsUpEnabled(true);

        return view;
    }

}