package com.example.backlogtracker;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.backlogtracker.db.AppDatabase;

import java.util.Arrays;
import java.util.List;

public class FilterGames extends Fragment {
    // Holds the view for the fragment
    private View view;
    // Holds the Spinner from the layout
    private Spinner filterBySpinner, filterOnSpinner;
    // Holds the ArrayAdapter for the filterBy spinner
    private ArrayAdapter<String> filterByAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filter_games, container, false);
        Toolbar toolbar = view.findViewById(R.id.filter_toolbar);

        // Get layout components and stores them for use
        filterOnSpinner = view.findViewById(R.id.filter_on);
        filterBySpinner = view.findViewById(R.id.filter_by);
        Button filter = view.findViewById(R.id.filter_confirm);

        // Sets up the OnClickListener for the filter button
        filter.setOnClickListener(v -> {
            // Creates an action to navigate to FilterRankFragment
            var action = FilterGamesDirections.actionFilterGamesToFilterRankFragment();
            // Sets up the arguments that need to passed to the next fragment
            action.setFilterOn(filterOnSpinner.getSelectedItem().toString());
            action.setFilterBy(filterBySpinner.getSelectedItem().toString());
            // Navigates to the next fragment
            Navigation.findNavController(v).navigate(action);
        });

        // Sets up the ArrayAdapter for the filterOnSpinner
        ArrayAdapter<CharSequence> filterOnAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_by_array,
                R.layout.spinner_item
        );
        // Sets the drop down item for the filterOnAdapter and adds it the filterOnSpinner
        filterOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterOnSpinner.setAdapter(filterOnAdapter);


        // Creates a thread and get the list of the unique developers from the database
        new Thread(() -> {
            List<String> returnList = AppDatabase
                    .getInstance(getContext())
                    .gameDAO()
                    .getDevelopers();
            // Create a new ArrayAdapter withe developerList
            requireActivity().runOnUiThread(() -> {
                filterByAdapter = new ArrayAdapter<>(
                        requireContext(),
                        R.layout.spinner_item,
                        returnList
                        );
                // Sets the drop down item for the filterByAdapter and adds it the filterBySpinner
                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filterBySpinner.setAdapter(filterByAdapter);
            });
        }).start();

        // Sets the listener for any time filterOnSpinner item is changed
        filterOnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Based on the item the user selected from the filterBy dropdown.
                 * The case statement will call the database get the unique values from the database.
                 *
                 * Genres is a special case and it will create List<Strings> from the R.array.genre_array
                 */
                switch (position) {
                    case 0:
                        new Thread(() -> {
                            List<String> returnList = AppDatabase
                                    .getInstance(getContext())
                                    .gameDAO()
                                    .getDevelopers();

                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        returnList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                    case 1:
                        new Thread(() -> {
                            List<String> returnList = AppDatabase
                                    .getInstance(getContext())
                                    .gameDAO()
                                    .getPublishers();

                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        returnList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                    case 2:
                        new Thread(() -> {
                            List<String> returnList = AppDatabase
                                    .getInstance(getContext())
                                    .gameDAO()
                                    .getPlatforms();

                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        returnList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                    case 3:
                        new Thread(() -> {
                            List<String> resultList = Arrays.asList(getResources().getStringArray(R.array.genre_array));
                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        resultList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                    case 4:
                        new Thread(() -> {
                            List<String> resultList = Arrays.asList(getResources().getStringArray(R.array.formats_array));
                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        resultList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                    case 5:
                        new Thread(() -> {
                            List<String> resultList = Arrays.asList(getResources().getStringArray(R.array.rating_array));
                            requireActivity().runOnUiThread(() -> {
                                filterByAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        R.layout.spinner_item,
                                        resultList
                                );
                                filterByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                filterBySpinner.setAdapter(filterByAdapter);
                            });
                        }).start();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Get the argument for filterOn and checks if its an empty String or not
        String filterOnArg = FilterGamesArgs.fromBundle(getArguments()).getFilterOn();
        if(!filterOnArg.isEmpty())
        {
            // Sets the spinner location to the passed in value
            filterOnSpinner.setSelection(filterOnAdapter.getPosition(filterOnArg));
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
                // If the back button is pressed navigates the suer back to the mainActivityFragment
                Navigation.findNavController(view).navigate(R.id.action_filterGames_to_mainActivityFragment);
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