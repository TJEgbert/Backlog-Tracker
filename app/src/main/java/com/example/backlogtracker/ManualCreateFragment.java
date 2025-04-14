package com.example.backlogtracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import com.example.backlogtracker.db.AppDatabase;
import com.example.backlogtracker.db.Game;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ManualCreateFragment extends Fragment {

    // Holds the view for the fragment
    private View view;
    // Holds the TextInputEditTexts from the layout
    private TextInputEditText txtTitle, txtDeveloper, txtPublisher, txtPlatform, txtGenres;
    // Holds the Spinners from the layout
    private Spinner spinnerFormat, spinnerRating;
    // Holds the game to be edited/created
    private Game game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_manual_create, container, false);

        // Get layout components and stores them for use
        Toolbar toolbar = view.findViewById(R.id.new_game_toolbar);
        TextView fragmentTitle = view.findViewById(R.id.manual_create_title);
        txtTitle = view.findViewById(R.id.new_title);
        txtDeveloper = view.findViewById(R.id.new_developer);
        txtPublisher = view.findViewById(R.id.new_publisher);
        txtPlatform = view.findViewById(R.id.new_platform);
        txtGenres = view.findViewById(R.id.new_genres);
        spinnerFormat = view.findViewById(R.id.new_format);
        spinnerRating = view.findViewById(R.id.new_rating);

        // Sets up the ArrayAdapter for the spinnerFormat
        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.formats_array,
                R.layout.spinner_item
        );
        // Sets the drop down item for the formatAdapter and adds it to spinnerFormat
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormat.setAdapter(formatAdapter);

        // Sets up the ArrayAdapter for the spinnerRating
        ArrayAdapter<CharSequence> ratingAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.rating_array,
                R.layout.spinner_item
        );
        // Sets the drop down item for the ratingAdapter and adds it the spinnerRating
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(ratingAdapter);

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
                            // If there is no game
                            if(game == null)
                            {
                                // Create a new game
                                Game tempGame = new Game(
                                        Integer.parseInt(spinnerRating.getSelectedItem().toString()),
                                        spinnerFormat.getSelectedItem().toString(),
                                        Objects.requireNonNull(txtGenres.getText()).toString(),
                                        Objects.requireNonNull(txtPlatform.getText()).toString(),
                                        Objects.requireNonNull(txtPublisher.getText()).toString(),
                                        Objects.requireNonNull(txtDeveloper.getText()).toString(),
                                        Objects.requireNonNull(txtTitle.getText()).toString()

                                );
                                // Adds it to the database
                                AppDatabase.getInstance(getContext())
                                        .gameDAO()
                                        .insert(tempGame);
                            }
                            else
                            {
                                // If there is a game
                                // Update game object
                                game.setTitle(Objects.requireNonNull(txtTitle.getText()).toString());
                                game.setDeveloper(Objects.requireNonNull(txtDeveloper.getText()).toString());
                                game.setPlatform(Objects.requireNonNull(txtPlatform.getText()).toString());
                                game.setPublisher(Objects.requireNonNull(txtPublisher.getText()).toString());
                                game.setGenres(Objects.requireNonNull(txtGenres.getText()).toString());
                                game.setFormat(spinnerFormat.getSelectedItem().toString());
                                game.setRating(Integer.parseInt(spinnerRating.getSelectedItem().toString()));

                                // Update it in the database
                                AppDatabase.getInstance(getContext())
                                        .gameDAO()
                                        .update(game);
                            }
                        }).start();
                        // Navigates to the correct fragments
                        NavigateBack();
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
                    // Navigates to the correct fragments
                    NavigateBack();
                }
                return true;
            }

        };

        // Gets the MenuHost
        MenuHost host = requireActivity();
        // Finishes setting up menu
        host.addMenuProvider(provider, getViewLifecycleOwner());
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Gets the game_id from the passed in arguments
        int game_id = ManualCreateFragmentArgs.fromBundle(getArguments()).getGameId();
        // If value was passed in
        if(game_id != 0)
        {
            // Updates the title of the game
            fragmentTitle.setText(R.string.edit_game);
            // Gets the game and updates the UI elements
            new Thread(() -> {
                game = AppDatabase.getInstance(getContext())
                        .gameDAO()
                        .getByID(game_id);

                requireActivity().runOnUiThread(() -> {
                    txtTitle.setText(game.getTitle());
                    txtDeveloper.setText(game.getDeveloper());
                    txtPublisher.setText(game.getPublisher());
                    txtPlatform.setText(game.getPlatform());
                    txtGenres.setText(game.getGenres());
                    spinnerFormat.setSelection(formatAdapter.getPosition(game.getFormat()));
                    spinnerRating.setSelection(ratingAdapter.getPosition(String.valueOf(game.getRating())));
                });
            }).start();
        }

        return view;
    }

    /**
     * Navigates the user to correct fragment based in the fragment they came from
     */
    private void NavigateBack()
    {
        // Get the name of the fragment
        String fType = ManualCreateFragmentArgs.fromBundle(getArguments()).getFType();
        // If the user came from MainActivityFragment
        if(Objects.equals(fType, "main"))
        {
            // Navigates the user back the MainActivityFragment
            Navigation.findNavController(view).navigate(R.id.action_manualCreateFragment_to_mainActivityFragment);
        }
        else
        {
            // Creates a new action to navigate to the FilterRankFragment
            var action = ManualCreateFragmentDirections.actionManualCreateFragmentToFilterRankFragment();
            // Sets up the arguments that needs to passed to the next fragment
            action.setFilterBy(DetailsFragmentArgs.fromBundle(getArguments()).getFilterBy());
            action.setFilterOn(DetailsFragmentArgs.fromBundle(getArguments()).getFilterOn());
            // Navigates to the next fragment
            Navigation.findNavController(view).navigate(action);
        }
    }

    /**
     * Checks if all the TextInputEditText filled or not
     * @return True: if all are filled, False if any are empty
     */
    private boolean CheckFields()
    {
        String title = Objects.requireNonNull(txtTitle.getText()).toString();
        String developer = Objects.requireNonNull(txtDeveloper.getText()).toString();
        String publisher = Objects.requireNonNull(txtPublisher.getText()).toString();
        String platform = Objects.requireNonNull(txtPlatform.getText()).toString();
        String genres = Objects.requireNonNull(txtGenres.getText()).toString();

        return !title.isEmpty() && !developer.isEmpty() && !publisher.isEmpty() && !platform.isEmpty()
                && !genres.isEmpty();
    }

}