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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.backlogtracker.JSONclasses.SearchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SearchFragment extends Fragment {

    // Holds the view for the fragment
    private View view;
    // Holds the EditText from the layout
    private EditText searchField;
    // Holds the TextView from the layout
    private TextView message;
    // Holds the RecyclerView for the fragment
    private RecyclerView recyclerView;
    // Holds the SearchResultsRecyclerViewAdapter for the fragment
    private SearchResultsRecyclerViewAdapter searchResultsRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        // Get layout components and stores them for use
        Toolbar toolbar = view.findViewById(R.id.search_toolbar);
        searchField = view.findViewById(R.id.search_field);
        Button searchButton = view.findViewById(R.id.search_confirm);
        recyclerView = view.findViewById(R.id.results);
        message = view.findViewById(R.id.message);

        // Sets up the onClickListener for the searchButton
        searchButton.setOnClickListener(v -> loadSearch(searchField.getText().toString()));

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
                // Navigates the user back to the mainActivityFragment
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_mainActivityFragment);
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

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();
        // Gets the searchTitle from passed in arguments
        String searchTitle = SearchFragmentArgs.fromBundle(getArguments()).getSearchingTitle();
        // Sets the searchField to the searchTitle
        searchField.setText(searchTitle);
        // Create a new SearchResultsRecyclerViewAdapter
        searchResultsRecyclerViewAdapter = new SearchResultsRecyclerViewAdapter(new ArrayList<>());
        int columnCount = searchResultsRecyclerViewAdapter.getItemCount();
        // Sets the type of layout fragment
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        // Finishes setting up recyclerView
        recyclerView.setAdapter(searchResultsRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);
        // If searchTitle is not empty
        if(!searchTitle.isEmpty())
        {
            loadSearch(searchTitle);
        }

    }

    /**
     * Loads the searchResultsRecyclerViewAdapter with results given from the GiantBomb API
     * based on searchTitle
     * @param searchTitle: The title the user want to search for
     */
    private void loadSearch(String searchTitle)
    {
        // Displays loading message
        message.setText(R.string.loading);
        message.setVisibility(View.VISIBLE);
        // Create a new APIAccess class
        APIAccess task = new APIAccess();
        AtomicReference<List<SearchResults>> returnList = new AtomicReference<>(new ArrayList<>());
        new Thread(() -> {
            // Fetches the List<SearchResults> from the API
            returnList.set(task.fetchResults(searchTitle));

            requireActivity().runOnUiThread(() -> {
                // Adds the games to the searchResultsRecyclerViewAdapter
                searchResultsRecyclerViewAdapter.addItems(returnList.get());
                // If no games where returned
                if(returnList.get().isEmpty())
                {
                    // Display now results message
                    message.setText(R.string.no_results);
                }
                else
                {
                    // Remove message and completely hide the message layout element
                    message.setText("");
                    message.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}