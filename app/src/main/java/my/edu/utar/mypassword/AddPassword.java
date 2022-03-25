package my.edu.utar.mypassword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AddPassword extends BaseActivity {

    boolean back = false;

    SQLiteAdapter sqLiteAdapter;
    AddCategoryDropdownAdapter adapter;
    AutoCompleteTextView categoryTextView;
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<Data> categoryData;
    PasswordGenerator passwordGenerator;

    TextInputLayout titleLayout;
    TextInputLayout usernameLayout;
    TextInputLayout passwordLayout;
    TextInputLayout noteLayout;

    TextInputEditText titleInput;
    TextInputEditText usernameInput;
    TextInputEditText passwordInput;
    AutoCompleteTextView cidInput;
    TextInputEditText urlInput;
    TextInputEditText noteInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Password");

        sqLiteAdapter = new SQLiteAdapter(this);
        sqLiteAdapter.openToRead();
        categoryData = sqLiteAdapter.queryAll("CATEGORY");
        sqLiteAdapter.close();
        for (Data d: categoryData) {
            Category c = (Category) d;
            items.add(c.getCategoryName());
        }
        String[] itemsArr = items.toArray(new String[items.size()]);
        adapter = new AddCategoryDropdownAdapter(this, itemsArr, categoryData);
        categoryTextView = findViewById(R.id.add_password_category);
        categoryTextView.setAdapter(adapter);
        categoryTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryTextView.setTag(view.getTag());
            }
        });

        titleLayout = (TextInputLayout) findViewById(R.id.add_password_title_layout);
        usernameLayout = (TextInputLayout) findViewById(R.id.add_password_username_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.add_password_password_layout);
        noteLayout = (TextInputLayout) findViewById(R.id.add_password_note_layout);

        titleInput = (TextInputEditText) findViewById(R.id.add_password_title);
        usernameInput = (TextInputEditText) findViewById(R.id.add_password_username);
        passwordInput = (TextInputEditText) findViewById(R.id.add_password_password);
        cidInput = (AutoCompleteTextView) findViewById(R.id.add_password_category);
        urlInput = (TextInputEditText) findViewById(R.id.add_password_url);
        noteInput = (TextInputEditText) findViewById(R.id.add_password_note);

        titleLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleInput.getText().clear();
                titleLayout.setError(null);
            }
        });
        usernameLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameInput.getText().clear();
                usernameLayout.setError(null);
            }
        });
        noteLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteInput.getText().clear();
                noteLayout.setError(null);
            }
        });

        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && charSequence.length() < titleLayout.getCounterMaxLength()) {
                    titleLayout.setError(null);
                } else if (charSequence.length() > titleLayout.getCounterMaxLength()) {
                    titleLayout.setError("Exceed max length");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && charSequence.length() < usernameLayout.getCounterMaxLength()) {
                    usernameLayout.setError(null);
                } else if (charSequence.length() > usernameLayout.getCounterMaxLength()) {
                    usernameLayout.setError("Exceed max length");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && charSequence.length() < passwordLayout.getCounterMaxLength()) {
                    passwordLayout.setError(null);

                    passwordGenerator = new PasswordGenerator();
                    String pStrength = passwordGenerator.checkStrength(String.valueOf(charSequence));
                    passwordLayout.setHelperText(pStrength);
                    if (pStrength.equals("Strong")) {
                        passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.GREEN));
                    } else if (pStrength.equals("Moderate")) {
                        passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.BLUE));
                    } else if (pStrength.equals("Weak")){
                        passwordLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
                    }
                } else if (charSequence.length() > passwordLayout.getCounterMaxLength()) {
                    passwordLayout.setError("Exceed max length");
                    passwordLayout.setHelperText(null);
                } else {
                    passwordLayout.setHelperText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0 && charSequence.length() < noteLayout.getCounterMaxLength()) {
                    noteLayout.setError(null);
                } else if (charSequence.length() > noteLayout.getCounterMaxLength()) {
                    noteLayout.setError("Exceed max length");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Intent intent = getIntent();
        String createdPassword = intent.getStringExtra("createdPassword");
        if (createdPassword != null) {
            passwordInput.setText(createdPassword);
        }

        Button passwordGeneratorBtn = findViewById(R.id.add_password_generator_btn);
        passwordGeneratorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPassword.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.password_generator_use, viewGroup, false);
                TextInputEditText generatedPassword = (TextInputEditText) dialogView.findViewById(R.id.use_password_generated);
                TextView passwordStrength = (TextView) dialogView.findViewById(R.id.use_password_strength);
                AppCompatButton refreshPassword = (AppCompatButton) dialogView.findViewById(R.id.use_password_refresh);
                AppCompatButton copyPassword = (AppCompatButton) dialogView.findViewById(R.id.use_password_copy);
                AppCompatButton usePassword = (AppCompatButton) dialogView.findViewById(R.id.use_password_btn);
                AppCompatButton closePasswordGenerator = (AppCompatButton) dialogView.findViewById(R.id.close_use_btn);
                Slider passwordSlider = (Slider) dialogView.findViewById(R.id.use_password_slider);
                SwitchMaterial uppercaseSwitch = (SwitchMaterial) dialogView.findViewById(R.id.use_password_uppercase_switch);
                SwitchMaterial lowercaseSwitch = (SwitchMaterial) dialogView.findViewById(R.id.use_password_lowercase_switch);
                SwitchMaterial numbersSwitch = (SwitchMaterial) dialogView.findViewById(R.id.use_password_numbers_switch);
                SwitchMaterial symbolsSwitch = (SwitchMaterial) dialogView.findViewById(R.id.use_password_symbols_switch);
                builder.setView(dialogView).setCancelable(false);
                AlertDialog alertDialog = builder.create();
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
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("password", generatedPassword.getText());
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                    }
                });
                usePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        passwordInput.setText(generatedPassword.getText());
                        alertDialog.dismiss();
                        passwordInput.requestFocus();
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_password_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_save_password:

                String title = titleInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                Integer cid = (Integer) cidInput.getTag();
                String url = urlInput.getText().toString();
                String note = noteInput.getText().toString();

                if (title.isEmpty() || title.trim().isEmpty() || username.isEmpty() || username.trim().isEmpty() || password.isEmpty() || password.trim().isEmpty()) {
                    if (title.isEmpty() || title.trim().isEmpty()) {
                        titleLayout.setError("Required");
                    }
                    if (username.isEmpty() || username.trim().isEmpty()) {
                        usernameLayout.setError("Required");
                    }
                    if (password.isEmpty() || password.trim().isEmpty()) {
                        passwordLayout.setError("Required");
                    }
                } else if (title.length() <= titleLayout.getCounterMaxLength() && username.length() <= usernameLayout.getCounterMaxLength() && password.length() <= passwordLayout.getCounterMaxLength() && note.length() <= noteLayout.getCounterMaxLength()) {
                    Password np = null;
                    try {
                        np = new Password(title, username, AESCrypt.encrypt(password), cid, url, note);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sqLiteAdapter.openToWrite();
                    sqLiteAdapter.insert(np);
                    sqLiteAdapter.close();
                    onBackPressed();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!back) {
            PasswordFragment.getWeakInstance().pauseFlag = true;
            PasswordFragment.getWeakInstance().onPause();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back = true;
    }

    class AddCategoryDropdownAdapter extends ArrayAdapter<String> {

        Context context;
        String[] items;
        ArrayList<Data> categoryData;

        public AddCategoryDropdownAdapter(@NonNull Context c, String[] items, ArrayList<Data> categoryData) {
            super(c, R.layout.dropdown_list_item, items);
            this.context = c;
            this.items = items;
            this.categoryData = categoryData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Category c = (Category) categoryData.get(position);
            view.setTag(c.getCategoryID());
            return view;
        }
    }
}