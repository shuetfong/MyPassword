package my.edu.utar.mypassword;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public static WeakReference<BaseActivity> baseActivityWeakReference;

    SQLiteAdapter sqLiteAdapter;
    AutoCompleteTextView cFilter;
    ListView passwordList;
    CategoryFilterAdapter adapter;
    PasswordAdapter passwordAdapter;
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<Data> categoryData;
    ArrayList<Data> passwordData;
    ClipboardManager clipboardManager;

    public PasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordFragment newInstance(String param1, String param2) {
        PasswordFragment fragment = new PasswordFragment();
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

        baseActivityWeakReference = new WeakReference<BaseActivity>((BaseActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.passwordFragment, R.id.categoryFragment, R.id.toolsFragment, R.id.settingsFragment).build();

        Toolbar toolbar = view.findViewById(R.id.passwordToolbar);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        toolbar.inflateMenu(R.menu.add_password_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((BaseActivity) getActivity()).pauseFlag = false;
                Intent intent = new Intent(getActivity(), AddPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return false;
            }
        });

        setHasOptionsMenu(true);
        sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.openToRead();
        categoryData = sqLiteAdapter.queryAll("CATEGORY");
        passwordData = sqLiteAdapter.queryAll("PASSWORD");
        sqLiteAdapter.close();

        items.add("All");
        for (Data d: categoryData) {
            Category c = (Category) d;
            items.add(c.getCategoryName());
        }
        categoryData.add(0, null);
        String[] itemsArr = items.toArray(new String[items.size()]);
        adapter = new CategoryFilterAdapter(getContext(), itemsArr, categoryData);
        cFilter = view.findViewById(R.id.filter_category);
        cFilter.setAdapter(adapter);
        cFilter.setTag(-1);
        cFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cFilter.setTag(view.getTag());
                onResume();
            }
        });

        passwordList = view.findViewById(R.id.passwordList);
        passwordAdapter = new PasswordAdapter(getContext(), passwordData);
        passwordList.setAdapter(passwordAdapter);
        passwordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((BaseActivity) getActivity()).pauseFlag = false;
                Intent intent = new Intent(getActivity(), EditPassword.class);
                intent.putExtra("pid", (Integer) view.getTag());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        passwordList.setLongClickable(true);
        passwordList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                sqLiteAdapter.openToRead();
                Integer pid = (Integer) view.getTag();
                Password tp = (Password) sqLiteAdapter.queryWhere("PASSWORD", "pid = ?", new String[]{pid.toString()}).get(0);
                sqLiteAdapter.close();

                clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = null;
                try {
                    clipData = ClipData.newPlainText("password", AESCrypt.decrypt(tp.getPassword()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cFilter.getTag() != null) {
            sqLiteAdapter.openToRead();
            Integer filterID = (Integer) cFilter.getTag();
            if (filterID  == -1) {
                passwordData = sqLiteAdapter.queryAll("PASSWORD");
            } else {
                passwordData = sqLiteAdapter.queryWhere("PASSWORD", "cid = ?", new String[]{filterID.toString()});
            }
            passwordAdapter = new PasswordAdapter(getContext(), passwordData);
            passwordList.setAdapter(passwordAdapter);
            sqLiteAdapter.close();
        }
    }

    class CategoryFilterAdapter extends ArrayAdapter<String> {

        Context context;
        String[] items;
        ArrayList<Data> categoryData;

        public CategoryFilterAdapter(@NonNull Context c, String[] items, ArrayList<Data> categoryData) {
            super(c, R.layout.dropdown_list_item, items);
            this.context = c;
            this.items = items;
            this.categoryData = categoryData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (((TextView) view).getText().toString() == "All") {
                view.setTag(-1);
            } else {
                Category c = (Category) categoryData.get(position);
                view.setTag(c.getCategoryID());
            }
            return view;
        }
    }

    class PasswordAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<Data> passwordData;

        public PasswordAdapter(@NonNull Context c, ArrayList<Data> passwordData) {
            super(c, android.R.layout.simple_list_item_1, new String[passwordData.size()]);

            this.context = c;
            this.passwordData = passwordData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) LayoutInflater.from(getActivity());
            View passwordEntry = layoutInflater.inflate(R.layout.entry_password, null, false);
            TextView passwordTitle = (TextView) passwordEntry.findViewById(R.id.passwordTitle);
            TextView passwordUsername = (TextView) passwordEntry.findViewById(R.id.passwordUsername);
            ImageButton passwordMore = (ImageButton) passwordEntry.findViewById(R.id.passwordMore);

            Password p = (Password) passwordData.get(position);
            passwordEntry.setTag(p.getPasswordID());
            passwordMore.setTag(p.getPasswordID());
            passwordTitle.setText(p.getTitle());
            passwordUsername.setText(p.getUsername());
            passwordMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(passwordMore, position);
                }
            });

            return passwordEntry;
        }

        private void showPopupMenu(View view, int position) {
            PopupMenu popup = new PopupMenu(context, view, Gravity.END);
            MenuInflater inflater = popup.getMenuInflater();

            inflater.inflate(R.menu.password_context_menu, popup.getMenu());

            //set menu item click listener here
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, view));
            popup.show();
        }
    }

    public static BaseActivity getWeakInstance() {
        return baseActivityWeakReference.get();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;
        View view;

        MyMenuItemClickListener(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Integer pid = (Integer) view.getTag();
            Password tp;
            ClipData clipData = null;
            switch (menuItem.getItemId()) {
                case R.id.copyUsername:
                    sqLiteAdapter.openToRead();
                    tp = (Password) sqLiteAdapter.queryWhere("PASSWORD", "pid = ?", new String[]{pid.toString()}).get(0);
                    sqLiteAdapter.close();

                    clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipData = ClipData.newPlainText("username", tp.getUsername());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), "Username copied.", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.copyPassword:
                    sqLiteAdapter.openToRead();
                    tp = (Password) sqLiteAdapter.queryWhere("PASSWORD", "pid = ?", new String[]{pid.toString()}).get(0);
                    sqLiteAdapter.close();

                    clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    try {
                        clipData = ClipData.newPlainText("password", AESCrypt.decrypt(tp.getPassword()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(getContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.editPassword:
                    ((BaseActivity) getActivity()).pauseFlag = false;
                    Intent intent = new Intent(getActivity(), EditPassword.class);
                    intent.putExtra("pid", pid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    return true;
                case R.id.deletePassword:
                    sqLiteAdapter.openToWrite();
                    sqLiteAdapter.deleteWhere("PASSWORD", "pid = ?", new String[]{pid.toString()});
                    sqLiteAdapter.close();

                    onResume();
                    return true;
                default:
            }
            return false;
        }
    }
}