package my.edu.utar.mypassword;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String MYPREFERENCES = "secret_shared_prefs" ;
    String MASTER = "Master";
    String HINT = "Hint";
    String AUTOLOCK = "AutoLock";
    SharedPreferences sharedpreferences;
    String masterKeyAlias;
    SharedPreferences.Editor editor;
    ListView settingsList;
    String[] settings = {"Change Master Password", "Auto-lock MyPassword"};

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedpreferences = EncryptedSharedPreferences.create(
                    MYPREFERENCES,
                    masterKeyAlias,
                    getActivity(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getContext().getTheme().applyStyle(R.style.Theme_MyPassword, true);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.passwordFragment, R.id.categoryFragment, R.id.toolsFragment, R.id.settingsFragment).build();

        Toolbar toolbar = view.findViewById(R.id.settingsToolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        settingsList = view.findViewById(R.id.settingsList);

        ArrayAdapter<String> settingsArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, settings);

        settingsList.setAdapter(settingsArrayAdapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder;
                View dialogView;
                AlertDialog alertDialog;

                switch (settings[i]) {
                    case "Change Master Password":
                        builder = new AlertDialog.Builder(getActivity());
                        dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.change_master_password, null, false);
                        TextInputLayout currentPasswordLayout = (TextInputLayout) dialogView.findViewById(R.id.enter_current_password_layout);
                        TextInputLayout newPasswordLayout = (TextInputLayout) dialogView.findViewById(R.id.enter_new_password_layout);
                        TextInputLayout confirmPasswordLayout = (TextInputLayout) dialogView.findViewById(R.id.confirm_new_password_layout);
                        TextInputEditText currentPasswordText = (TextInputEditText) dialogView.findViewById(R.id.enter_current_password);
                        TextInputEditText newPasswordText = (TextInputEditText) dialogView.findViewById(R.id.enter_new_password);
                        TextInputEditText confirmPasswordText = (TextInputEditText) dialogView.findViewById(R.id.confirm_new_password);
                        TextInputEditText newHintText = (TextInputEditText) dialogView.findViewById(R.id.new_hint);
                        AppCompatButton confirmChangeMasterPassword = (AppCompatButton) dialogView.findViewById(R.id.confirm_change_btn);
                        AppCompatButton closeChangeMasterPassword = (AppCompatButton) dialogView.findViewById(R.id.close_change_btn);
                        builder.setView(dialogView).setCancelable(false);
                        alertDialog = builder.create();
                        currentPasswordText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String p = currentPasswordText.getText().toString().trim();

                                if (!p.isEmpty()) {
                                    currentPasswordLayout.setErrorEnabled(false);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        TextWatcher textWatcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String p1 = newPasswordText.getText().toString().trim();
                                String p2 = confirmPasswordText.getText().toString().trim();

                                if (!p1.isEmpty()) {
                                    newPasswordLayout.setErrorEnabled(false);
                                }

                                if (!p2.isEmpty()) {
                                    confirmPasswordLayout.setErrorEnabled(false);
                                }

                                if (!p1.equals(p2)) {
                                    confirmPasswordLayout.setErrorEnabled(true);
                                    confirmPasswordLayout.setError("Password not match");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        };
                        newPasswordText.addTextChangedListener(textWatcher);
                        confirmPasswordText.addTextChangedListener(textWatcher);

                        confirmChangeMasterPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String currentPassword = currentPasswordText.getText().toString();
                                String newPassword = newPasswordText.getText().toString();
                                String confirmPassword = confirmPasswordText.getText().toString();
                                String newHint = newHintText.getText().toString();

                                if ((!currentPassword.isEmpty() && !currentPassword.trim().isEmpty()) && (!newPassword.isEmpty() && !newPassword.trim().isEmpty()) && (!confirmPassword.isEmpty() && !confirmPassword.trim().isEmpty())) {
                                    String master = sharedpreferences.getString(MASTER, "");
                                    if (currentPassword.equals(master)) {
                                        if (newPassword.equals(confirmPassword)) {
                                            editor = sharedpreferences.edit();
                                            editor.putString(MASTER, confirmPassword);
                                            editor.putString(HINT, newHint);
                                            editor.commit();
                                            alertDialog.dismiss();
                                            Intent intent = new Intent(getActivity(), Login.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getActivity(), "Password not match.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        currentPasswordLayout.setErrorEnabled(true);
                                        currentPasswordLayout.setError("Wrong password");
                                    }
                                } else {
                                    if (currentPassword.isEmpty() && currentPassword.trim().isEmpty()) {
                                        currentPasswordLayout.setErrorEnabled(true);
                                        currentPasswordLayout.setError("Required");
                                    }
                                    if (newPassword.isEmpty() && newPassword.trim().isEmpty()) {
                                        newPasswordLayout.setErrorEnabled(true);
                                        newPasswordLayout.setError("Required");
                                    }
                                    if (confirmPassword.isEmpty() && confirmPassword.trim().isEmpty()) {
                                        confirmPasswordLayout.setErrorEnabled(true);
                                        confirmPasswordLayout.setError("Required");
                                    }
                                }
                            }
                        });
                        closeChangeMasterPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        break;
                    case "Auto-lock MyPassword":
                        builder = new AlertDialog.Builder(getActivity());
                        dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.autolock, null, false);
                        RadioGroup autolockGroup = (RadioGroup) dialogView.findViewById(R.id.auto_lock_group);
                        AppCompatButton confirmAutoLock = (AppCompatButton) dialogView.findViewById(R.id.confirm_auto_lock_btn);
                        AppCompatButton closeAutoLock = (AppCompatButton) dialogView.findViewById(R.id.close_auto_lock_btn);
                        builder.setView(dialogView).setCancelable(false);
                        alertDialog = builder.create();
                        confirmAutoLock.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                RadioButton autolockRadio = (RadioButton) dialogView.findViewById(autolockGroup.getCheckedRadioButtonId());
                                String autolockPref = (String) autolockRadio.getTag();
                                editor = sharedpreferences.edit();
                                editor.putString(AUTOLOCK, autolockPref);
                                editor.commit();
                                alertDialog.dismiss();
                                ((MyPassword) getActivity().getApplication()).startUserSession();
                            }
                        });
                        closeAutoLock.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                            }
                        });

                        RadioButton rbtn = (RadioButton) dialogView.findViewWithTag(sharedpreferences.getString(AUTOLOCK, "never"));
                        autolockGroup.check(rbtn.getId());

                        alertDialog.show();
                        break;
                    default:
                        Toast.makeText(getContext(), settings[i], Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}