package my.edu.utar.mypassword;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToolsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView toolsList;
    String[] tools = {"Password Generator"};
    PasswordGenerator passwordGenerator;
    SQLiteAdapter sqLiteAdapter;
    ArrayList<Data> passwordData;

    public ToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolsFragment newInstance(String param1, String param2) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getContext().getTheme().applyStyle(R.style.Theme_MyPassword, true);
        return inflater.inflate(R.layout.fragment_tools, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.passwordFragment, R.id.categoryFragment, R.id.toolsFragment, R.id.settingsFragment).build();

        Toolbar toolbar = view.findViewById(R.id.toolsToolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        toolsList = view.findViewById(R.id.toolsList);

        ArrayAdapter<String> settingsArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, tools);

        toolsList.setAdapter(settingsArrayAdapter);

        toolsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder;
                View dialogView;
                AlertDialog alertDialog;
                switch (tools[i]) {
                    case "Password Generator":
                        builder = new AlertDialog.Builder(getActivity());
                        dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.password_generator_create, null, false);
                        TextInputEditText generatedPassword = (TextInputEditText) dialogView.findViewById(R.id.create_password_generated);
                        TextView passwordStrength = (TextView) dialogView.findViewById(R.id.create_password_strength);
                        AppCompatButton refreshPassword = (AppCompatButton) dialogView.findViewById(R.id.create_password_refresh);
                        AppCompatButton copyPassword = (AppCompatButton) dialogView.findViewById(R.id.create_password_copy);
                        AppCompatButton createPassword = (AppCompatButton) dialogView.findViewById(R.id.create_password_btn);
                        AppCompatButton closePasswordGenerator = (AppCompatButton) dialogView.findViewById(R.id.close_create_btn);
                        Slider passwordSlider = (Slider) dialogView.findViewById(R.id.create_password_slider);
                        SwitchMaterial uppercaseSwitch = (SwitchMaterial) dialogView.findViewById(R.id.create_password_uppercase_switch);
                        SwitchMaterial lowercaseSwitch = (SwitchMaterial) dialogView.findViewById(R.id.create_password_lowercase_switch);
                        SwitchMaterial numbersSwitch = (SwitchMaterial) dialogView.findViewById(R.id.create_password_numbers_switch);
                        SwitchMaterial symbolsSwitch = (SwitchMaterial) dialogView.findViewById(R.id.create_password_symbols_switch);
                        builder.setView(dialogView).setCancelable(false);
                        alertDialog = builder.create();
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!uppercaseSwitch.isChecked() && !lowercaseSwitch.isChecked() && !numbersSwitch.isChecked() && !symbolsSwitch.isChecked()) {
                                    uppercaseSwitch.setChecked(true);
                                }
                            }
                        };
                        uppercaseSwitch.setOnClickListener(onClickListener);
                        lowercaseSwitch.setOnClickListener(onClickListener);
                        numbersSwitch.setOnClickListener(onClickListener);
                        symbolsSwitch.setOnClickListener(onClickListener);
                        generatedPassword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String strength = passwordGenerator.checkStrength(String.valueOf(charSequence));
                                passwordStrength.setText(strength);
                                if (strength.equals("Strong")) {
                                    passwordStrength.setTextColor(Color.GREEN);
                                } else if (strength.equals("Moderate")) {
                                    passwordStrength.setTextColor(Color.BLUE);
                                } else if (strength.equals("Weak")){
                                    passwordStrength.setTextColor(Color.RED);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        refreshPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Integer length = Math.round(passwordSlider.getValue());
                                Boolean upper = uppercaseSwitch.isChecked();
                                Boolean lower = lowercaseSwitch.isChecked();
                                Boolean number = numbersSwitch.isChecked();
                                Boolean symbol = symbolsSwitch.isChecked();
                                passwordGenerator = new PasswordGenerator(length, upper, lower, number, symbol);
                                generatedPassword.setText(passwordGenerator.generate());
                            }
                        });
                        copyPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("password", generatedPassword.getText());
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(getContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        createPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((BaseActivity) getActivity()).pauseFlag = false;
                                Intent intent = new Intent(getActivity(), AddPassword.class);
                                intent.putExtra("createdPassword", generatedPassword.getText().toString());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }
                        });
                        closePasswordGenerator.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });

                        passwordGenerator = new PasswordGenerator();
                        String gp = passwordGenerator.generate();
                        generatedPassword.setText(gp);
                        alertDialog.show();
                        break;

                    default:
                        Toast.makeText(getContext(), tools[i], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}