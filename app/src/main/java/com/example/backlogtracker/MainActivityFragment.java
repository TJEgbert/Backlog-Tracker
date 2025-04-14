package com.example.backlogtracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    // Holds the GameRecyclerViewAdapter for the fragment
    private GameRecyclerViewAdapter gameRecyclerViewAdapter;
    // Holds the AllGameViewModel
    private AllGameViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        // Sets up the tool bar as an actionbar and adds the return button
        Toolbar toolbar = view.findViewById(R.id.title_bar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);

        MenuProvider provider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Clears the menu and inflates the menu specific to the fragment
                menu.clear();
                menuInflater.inflate(R.menu.main_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // If the user clicks the Filter button
                if (menuItem.getItemId() == R.id.filter)
                {
                    // Navigates the user to FilterGame fragment
                    Navigation.findNavController(view).navigate(R.id.action_mainActivityFragment_to_filterGames);
                    return true;
                }
                else if(menuItem.getItemId() == R.id.rank)
                {
                    // If the user clicks the Rank button
                    // Navigates the user to FilterRankFragment
                    Navigation.findNavController(view).navigate(R.id.action_mainActivityFragment_to_filterRankFragment);
                    return true;
                }
                return false;
            }
        };

        // Gets the floating button and set up an OnClickListener
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            // Creates a popMenu that will be displayed by the floatingActionButton
            PopupMenu popUp = new PopupMenu(requireContext(), fab);
            // Inflates the menu
            MenuInflater inflater1 = popUp.getMenuInflater();
            inflater1.inflate(R.menu.pop_up_menu, popUp.getMenu());
            // Sets up an onClickListener for the menu
            popUp.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.search_create)
                {
                    // Navigates the user to SearchFragment
                    Navigation.findNavController(view1).navigate(R.id.action_mainActivityFragment_to_searchFragment);

                }
                else if(item.getItemId() == R.id.manual_create)
                {
                    // Creates a new action to navigate to the ManualCreateFragment
                    var action = MainActivityFragmentDirections.actionMainActivityFragmentToManualCreateFragment();
                    // Sets up the argument that needs to passed to the next fragment
                    action.setFType("main");
                    // Navigates to the next fragment
                    Navigation.findNavController(view1).navigate(action);
                }
                return true;
            });
            popUp.show();
        });

        Context context = getContext();
        // Create a new GameRecyclerViewAdapter
        gameRecyclerViewAdapter = new GameRecyclerViewAdapter(new ArrayList<>(), "main", "", "");

        // Get the number of games in the list
        int columnCount = gameRecyclerViewAdapter.getItemCount();

        // Sets the type of layout fragment
        if (columnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        // Finishes setting up recyclerView
        recyclerView.setAdapter(gameRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);

        // Gets the view
        viewModel = new ViewModelProvider(this).get(AllGameViewModel.class);
        // Sets up an observer
        viewModel.getGameList(context).observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                // Adds the course courseRecyclerViewAdapter
                gameRecyclerViewAdapter.addItems(games);
            }
        });

        // Sets up the menu provider
        MenuHost host = requireActivity();
        // Finishes setting up the menu
        host.addMenuProvider(provider, getViewLifecycleOwner());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getGameList(requireContext()).observe(getViewLifecycleOwner(), games -> {
            if (games != null) {
                // Adds the games to gameRecyclerViewAdapter
                gameRecyclerViewAdapter.addItems(games);
            }
        });
    }



}
