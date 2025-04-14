package com.example.backlogtracker;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import com.example.backlogtracker.db.AppDatabase;
import com.example.backlogtracker.db.Game;

import java.util.Objects;


public class DetailsFragment extends Fragment {

    // Holds the view for the fragment
    private View view;
    // Holds the TextViews from the layout
    private TextView txtTitle, txtDeveloper, txtPublisher, txtPlatform,
            txtGenres, txtFormat, txtRating;
    // Holds the Game object gotten from the database
    private Game game;
    // Holds the id used in the search of the database
    private int game_id;
    // Holds the name of the fragment that navigated to DetailsFragment
    private String fType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_details, container, false);

        // Get layout components and stores them for use
        Toolbar toolbar = view.findViewById(R.id.detail_toolbar);
        txtTitle = view.findViewById(R.id.detail_title);
        txtDeveloper = view.findViewById(R.id.detail_developer);
        txtPublisher = view.findViewById(R.id.detail_publisher);
        txtPlatform = view.findViewById(R.id.detail_platform);
        txtGenres = view.findViewById(R.id.detail_genres);
        txtFormat = view.findViewById(R.id.detail_format);
        txtRating = view.findViewById(R.id.detail_rating);

        // Get the the game_id and fType that get passed in from the prier fragment
        game_id = DetailsFragmentArgs.fromBundle(getArguments()).getGameId();
        fType = DetailsFragmentArgs.fromBundle(getArguments()).getFType();
        if(game_id != 0)
        {
            // Access the database and update layout components with the game data
            new Thread(() -> {
                game = AppDatabase.getInstance(getContext())
                        .gameDAO()
                        .getByID(game_id);

                txtTitle.setText(game.getTitle());
                txtDeveloper.setText(game.getDeveloper());
                txtPublisher.setText(game.getPublisher());
                txtPlatform.setText(game.getPlatform());
                txtGenres.setText(game.getGenres());
                txtFormat.setText(game.getFormat());
                txtRating.setText(String.valueOf(game.getRating()));
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
                requireActivity().getMenuInflater().inflate(R.menu.menu_details, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // If the edit menu button is pressed
                if(menuItem.getItemId() == R.id.menu_edit){
                    // Creates a new action to navigate to the ManualCreateFragment
                    var action = DetailsFragmentDirections.actionDetailsFragmentToManualCreateFragment();
                    // Sets up the arguments that need to passed to the next fragment
                    action.setFType(fType);
                    action.setGameId(game_id);
                    action.setFilterBy(DetailsFragmentArgs.fromBundle(getArguments()).getFilterBy());
                    action.setFilterOn(DetailsFragmentArgs.fromBundle(getArguments()).getFilterOn());
                    // Navigates to the next fragment
                    Navigation.findNavController(view).navigate(action);

                } else if (menuItem.getItemId() == R.id.menu_delete)
                {
                    // If the use clicks the delete button
                    // Creates a dialog and inflates dialog_delete_confirmation in a LinearLayout
                    Dialog dialog = new Dialog(requireContext());
                    LinearLayout baseLayout = new LinearLayout(requireContext());
                    View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_confirmation, baseLayout);
                    dialog.setContentView(dialogView);
                    // Gets the buttons on the dialog
                    Button yes = dialogView.findViewById(R.id.delete_yes);
                    Button no = dialogView.findViewById(R.id.delete_no);
                    // If yes is clicked
                    yes.setOnClickListener(v -> {
                        // Deletes game object from the database and dismiss the dialog
                        dialog.dismiss();
                        new Thread(() -> {
                                    AppDatabase.getInstance(getContext())
                                            .gameDAO()
                                            .delete(game);
                                requireActivity().runOnUiThread(DetailsFragment.this::NavigateBack);
                            }).start();
                    });
                    // If not is clicked it dismisses the dialog
                    no.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                }
                else
                {
                    NavigateBack();
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

    /**
     * Handles the navigation back back to correct fragment
     */
    private void NavigateBack()
    {
        // If the user came from MainActivityFragment
        if(Objects.equals(fType, "main"))
        {
            // Navigates back to MainActivityFragment
            Navigation.findNavController(view).navigate(R.id.action_detailsFragment_to_mainActivityFragment);
        }
        else
        {
            // If the user came from FilterRankFragment
            // Navigates back to FilterRankFragment and passes the filterBy and filterOn back to it
            var action = DetailsFragmentDirections.actionDetailsFragmentToFilterRankFragment();
            action.setFilterBy(DetailsFragmentArgs.fromBundle(getArguments()).getFilterBy());
            action.setFilterOn(DetailsFragmentArgs.fromBundle(getArguments()).getFilterOn());
            Navigation.findNavController(view).navigate(action);
        }
    }

}