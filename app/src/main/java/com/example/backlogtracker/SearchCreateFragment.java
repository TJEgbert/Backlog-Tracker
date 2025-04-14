package com.example.backlogtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.backlogtracker.JSONclasses.GameResult;
import com.example.backlogtracker.db.AppDatabase;
import com.example.backlogtracker.db.Game;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;


public class SearchCreateFragment extends Fragment {

    // Holds the view for the fragment
    private View view;
    // Holds the TextViews from the layout
    private TextInputEditText txtTitle, txtGenres;
    // Holds the Spinners from the layout
    private Spinner spinnerDeveloper, spinnerPublisher, spinnerPlatform, spinnerFormat, spinnerRating;
    // Holds the GameResult object
    private GameResult gameResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_create, container, false);

        // Get layout components and stores them for use
        txtTitle = view.findViewById(R.id.search_title);
        txtGenres = view.findViewById(R.id.search_genres);
        spinnerDeveloper = view.findViewById(R.id.search_developer);
        spinnerPublisher = view.findViewById(R.id.search_publisher);
        spinnerPlatform = view.findViewById(R.id.search_platform);
        spinnerFormat = view.findViewById(R.id.search_format);
        spinnerRating = view.findViewById(R.id.search_rating);

        // Sets up the tool bar as an actionbar and adds the return button
        Toolbar toolbar = view.findViewById(R.id.new_game_search_toolbar);
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
                menuInflater.inflate(R.menu.menu_create_fragment, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // If the save button is clicked
                if (menuItem.getItemId() == R.id.menu_save)
                {
                    // Checks if all TextInputEditText are filled
                    if(CheckFields())
                    {
                        // Creates a thread
                        new Thread(() -> {
                            // Create a new game object from the entered in information
                            Game game = new Game(
                                    Integer.parseInt(spinnerRating.getSelectedItem().toString()),
                                    spinnerFormat.getSelectedItem().toString(),
                                    Objects.requireNonNull(txtGenres.getText()).toString(),
                                    spinnerPlatform.getSelectedItem().toString(),
                                    spinnerPublisher.getSelectedItem().toString(),
                                    spinnerDeveloper.getSelectedItem().toString(),
                                    Objects.requireNonNull(txtTitle.getText()).toString()

                            );

                            // Add the new game to the database
                            AppDatabase.getInstance(getContext())
                                    .gameDAO()
                                    .insert(game);

                            // Navigates the user back to the MainActivityFragment
                            requireActivity().runOnUiThread(() -> Navigation.findNavController(view).navigate(R.id.action_searchCreateFragment_to_mainActivityFragment));
                        }).start();
                    }
                    else
                    {
                        // If not all the TextInputEditText are filled
                        // Display toast to let the user know they are not field
                        int duration = Toast.LENGTH_SHORT;
                        Toast.makeText(requireContext(), "Please fill out all fields", duration).show();
                    }
                }
                else
                {
                    // If the back back button is pressed
                    // Creates a new action to navigate to the SearchFragment
                    var action = SearchCreateFragmentDirections.actionSearchCreateFragmentToSearchFragment();
                    // Sets up the arguments that needs to passed to the next fragment
                    action.setSearchingTitle(SearchCreateFragmentArgs.fromBundle(getArguments()).getSearchTitle());
                    Navigation.findNavController(view).navigate(action);
                }
                return true;
            }
        };

        // Finishes setting up menu
        MenuHost host = requireActivity();
        host.addMenuProvider(provider, getViewLifecycleOwner());
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Gets the guid from passed in arguments
        String guid = SearchCreateFragmentArgs.fromBundle(getArguments()).getGuid();
        if(!guid.isEmpty())
        {
            // Create a new APIAccess class
            APIAccess task = new APIAccess();
            // Creates a thread
            new Thread(() -> {
                // Sets the gameResults object from the return GiantBomb API
                gameResult = task.fetchGame(guid);

                // Updates the UI with the information from the gameResults
                requireActivity().runOnUiThread(() -> {
                    // Sets the game title
                    txtTitle.setText(gameResult.getTitle());

                    // Takes the list of genres and turns it into a string
                    StringBuilder genreList = getStringBuilder();
                    // Sets the genres
                    txtGenres.setText(genreList);
                    // Create ArrayAdapter and adds it the spinnerFormat
                    ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.formats_array,
                            R.layout.spinner_item
                    );
                    formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFormat.setAdapter(formatAdapter);

                    // Create ArrayAdapter and adds it the spinnerRating
                    ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.rating_array,
                            R.layout.spinner_item
                    );
                    ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRating.setAdapter(ratingAdapter);

                    // Creates ArrayAdapter and fills it with gameResults developers
                    ArrayAdapter<String> developerAdapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.spinner_item,
                            gameResult.getDevelopers()

                    );
                    // Adds the new ArrayAdapter to spinnerDeveloper
                    developerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDeveloper.setAdapter(developerAdapter);

                    // Creates ArrayAdapter and fills it with gameResults publishers
                    ArrayAdapter<String> publisherAdapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.spinner_item,
                            gameResult.getPublishers()

                    );
                    // Adds the new ArrayAdapter to spinnerPublisher
                    publisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPublisher.setAdapter(publisherAdapter);

                    // Creates ArrayAdapter and fills it with gameResults publishers
                    ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.spinner_item,
                            gameResult.getPlatforms()

                    );
                    // Adds the new ArrayAdapter to spinnerPlatform
                    platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlatform.setAdapter(platformAdapter);


                });
            }).start();
        }

        return view;
    }

    /**
     * Loops through the genres list building a string of genres
     * @return String of all genres in format of "genre1, genre2, ..., genreN"
     */
    @NonNull
    private StringBuilder getStringBuilder() {
        StringBuilder genreList = new StringBuilder();
        for (int i = 0; i < gameResult.getGenres().size(); i++)
        {
            if(i == gameResult.getGenres().size() -1)
            {
                genreList.append(gameResult.getGenres().get(i));
            }
            else
            {
                genreList.append(gameResult.getGenres().get(i)).append(", ");
            }
        }
        return genreList;
    }

    /**
     * Checks if all the TextInputEditText filled or not
     * @return True: if all are filled, False if any are empty
     */
    private boolean CheckFields()
    {
        String title = Objects.requireNonNull(txtTitle.getText()).toString();
        String genres = Objects.requireNonNull(txtGenres.getText()).toString();

        return !title.isEmpty() && !genres.isEmpty();
    }

}