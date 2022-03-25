package my.edu.utar.mypassword;

import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SQLiteAdapter sqLiteAdapter;
    CategoryAdapter categoryAdapter;
    ListView categoryList;
    ArrayList<Data> categoryData;
    String cname;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
        sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.openToRead();
        categoryData = sqLiteAdapter.queryAll("CATEGORY");
        sqLiteAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.passwordFragment, R.id.categoryFragment, R.id.toolsFragment, R.id.settingsFragment).build();

        Toolbar toolbar = view.findViewById(R.id.categoryToolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        toolbar.inflateMenu(R.menu.add_category_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.category_add, null, false);
                TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.add_category_name_layout);
                TextInputEditText addCategoryName = (TextInputEditText) dialogView.findViewById(R.id.add_category_name);
                AppCompatButton confirmAddCategory = (AppCompatButton) dialogView.findViewById(R.id.confirm_add_btn);
                AppCompatButton closeAddCategory = (AppCompatButton) dialogView.findViewById(R.id.close_add_btn);
                builder.setView(dialogView).setCancelable(false);
                AlertDialog alertDialog = builder.create();
                addCategoryName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 0 && charSequence.length() < textInputLayout.getCounterMaxLength()) {
                            textInputLayout.setError(null);
                        } else if (charSequence.length() > textInputLayout.getCounterMaxLength()) {
                            textInputLayout.setError("Exceed max length");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                confirmAddCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cname = addCategoryName.getText().toString();
                        if (cname.isEmpty() || cname.trim().isEmpty()) {
                            textInputLayout.setError("Required");
                        } else if (cname.length() <= textInputLayout.getCounterMaxLength()) {

                            Category nc = new Category(cname);
                            sqLiteAdapter.openToWrite();
                            sqLiteAdapter.insert(nc);
                            sqLiteAdapter.close();
                            alertDialog.dismiss();

                            sqLiteAdapter.openToRead();
                            categoryData = sqLiteAdapter.queryAll("CATEGORY");
                            sqLiteAdapter.close();

                            categoryAdapter = new CategoryAdapter(getActivity(), categoryData);
                            categoryList.setAdapter(categoryAdapter);
                        }
                    }
                });
                closeAddCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                return false;
            }
        });

        categoryList = view.findViewById(R.id.categoryList);
        categoryAdapter = new CategoryAdapter(getContext(), categoryData);
        categoryList.setAdapter(categoryAdapter);
    }

    class CategoryAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<Data> categoryData;

        public CategoryAdapter(@NonNull Context c, ArrayList<Data> categoryData) {
            super(c, android.R.layout.simple_list_item_1, new String[categoryData.size()]);
            this.context = c;
            this.categoryData = categoryData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) LayoutInflater.from(getActivity());
            View categoryEntry = layoutInflater.inflate(R.layout.entry_category, null, false);
            TextView categoryName = (TextView) categoryEntry.findViewById(R.id.categoryName);
            ImageButton categoryEdit = (ImageButton) categoryEntry.findViewById(R.id.categoryEdit);
            ImageButton categoryDelete = (ImageButton) categoryEntry.findViewById(R.id.categoryDelete);

            Category c = (Category) categoryData.get(position);
            categoryName.setText(c.getCategoryName());
            categoryEdit.setTag(c.getCategoryID());
            categoryEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.category_edit, null, false);

                    sqLiteAdapter.openToRead();
                    ArrayList<Data> tempcname = sqLiteAdapter.queryWhere("CATEGORY", "cid = ?", new String[]{((Integer) view.getTag()).toString()});
                    sqLiteAdapter.close();
                    ((TextInputEditText) dialogView.findViewById(R.id.ori_category_name)).setText(((Category)tempcname.get(0)).getCategoryName());
                    TextInputLayout textInputLayout = (TextInputLayout) dialogView.findViewById(R.id.edit_category_name_layout);
                    TextInputEditText editCategoryName = (TextInputEditText) dialogView.findViewById(R.id.edit_category_name);
                    AppCompatButton confirmEditCategory = (AppCompatButton) dialogView.findViewById(R.id.confirm_edit_btn);
                    AppCompatButton closeEditCategory = (AppCompatButton) dialogView.findViewById(R.id.close_edit_btn);
                    builder.setView(dialogView).setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    editCategoryName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (charSequence.length() > 0 && charSequence.length() < textInputLayout.getCounterMaxLength()) {
                                textInputLayout.setError(null);
                            } else if (charSequence.length() > textInputLayout.getCounterMaxLength()) {
                                textInputLayout.setError("Exceed max length");
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    confirmEditCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cname = editCategoryName.getText().toString();
                            if (cname.isEmpty() || cname.trim().isEmpty()) {
                                textInputLayout.setError("Required");
                            }
                            else if (cname.length() <= textInputLayout.getCounterMaxLength()) {
                                Category nc = new Category(cname);
                                sqLiteAdapter.openToWrite();
                                sqLiteAdapter.updateWhere(nc, "cid = ?", new String[]{((Integer) view.getTag()).toString()});
                                sqLiteAdapter.close();
                                alertDialog.dismiss();

                                sqLiteAdapter.openToRead();
                                categoryData = sqLiteAdapter.queryAll("CATEGORY");
                                sqLiteAdapter.close();

                                categoryAdapter = new CategoryAdapter(getActivity(), categoryData);
                                categoryList.setAdapter(categoryAdapter);
                            }
                        }
                    });
                    closeEditCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
            categoryDelete.setTag(c.getCategoryID());
            categoryDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sqLiteAdapter.openToWrite();
                    sqLiteAdapter.deleteWhere("Category", "cid = ?", new String[]{((Integer) view.getTag()).toString()});
                    sqLiteAdapter.close();

                    sqLiteAdapter.openToRead();
                    categoryData = sqLiteAdapter.queryAll("CATEGORY");
                    sqLiteAdapter.close();

                    categoryAdapter = new CategoryAdapter(getActivity(), categoryData);
                    categoryList.setAdapter(categoryAdapter);
                }
            });

            return categoryEntry;
        }
    }
}