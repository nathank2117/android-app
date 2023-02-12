package com.appisoft.iperkz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.appisoft.iperkz.activity.data.LoginDataSource;
import com.appisoft.iperkz.activity.data.LoginRepository;
import com.appisoft.iperkz.activity.ui.login.LoginViewModel;
import com.appisoft.iperkz.activity.ui.login.LoginViewModelFactory;
import com.appisoft.iperkz.adapter.BottomNavigationHandler;
import com.appisoft.iperkz.adapter.MenuListAdapter;
import com.appisoft.iperkz.callback.ImageLoadCallBack;
import com.appisoft.iperkz.callback.MenuListServiceRequestCallback;
import com.appisoft.iperkz.data.Data;
import com.appisoft.iperkz.engine.Cronet;
import com.appisoft.iperkz.entity.AppSettings;
import com.appisoft.iperkz.entity.MenuFilterCriteria;
import com.appisoft.iperkz.entity.Setting;
import com.appisoft.iperkz.entity.UserLocation;
//import com.appisoft.iperkz.fragement.InProgressDialog;
import com.appisoft.perkz.DisplayMessageActivity;
import com.appisoft.perkz.MyUrlRequestCallback;
import com.appisoft.perkz.R;
import com.appisoft.perkz.entity.DailySpecial;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MenuDetailsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LoginViewModel loginViewModel;
    public LoginViewModel getLoginViewModel() {
        return loginViewModel;
    }
    public static final String LOCATIONPREFERENCES = "LocPrefs" ;
    public static final String userPreviousLocation = "nameKey";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    UserLocation userLocation;

    private int selectedMealType= -1;

    String[] breakfastSubmenu = new String[] {"ALL", "Egg Platter", "Omelettes", "Beverages"};
    String[] lunchSubmenu = new String[] {"All","Egg Platter", "Omelettes", "Appetizers", "Salad",
                        "Sandwiches", "Chineese", "Indian", "Beverages"};
    String[] dinnerSubmenu = new String[] {"All"};
    String[] alldaySubmenu = new String[] {"All","Egg Platter", "Omelettes", "Appetizers", "Salad",
            "Sandwiches", "Chineese", "Indian", "Beverages"};
    private LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource());
    private BottomNavigationView topNavigationView;
    private ArrayList<Integer> mtTypeId = new ArrayList();
    private String mealType = "";
    private String wallpaper;
    //InProgressDialog builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // builder = new InProgressDialog();
       // builder.setCancelable(false);
        breakfastSubmenu = loginRepository.getCustomerEntity().getStore().getBreakfastCategories();
        lunchSubmenu = loginRepository.getCustomerEntity().getStore().getLunchCategories();
        dinnerSubmenu = loginRepository.getCustomerEntity().getStore().getDinnerCategories();
        alldaySubmenu = loginRepository.getCustomerEntity().getStore().getAllDayCategories();

        //Menu Labels
        String  breakfast  = loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeName();
        mtTypeId.add(loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeId());
        String  lunch  = loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeName();
        mtTypeId.add(loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeId());
        String  allday  = loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeName();
        mtTypeId.add(loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeId());


        setContentView(R.layout.activity_menu_details);
        selectedMealType= R.id.topNavigationBreakfastMenuId;
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);


        sharedpreferences = getSharedPreferences(LOCATIONPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        String userlocationJson = sharedpreferences.getString("USERLOCATION", null);
        ObjectMapper mapper1 = new ObjectMapper();

        try {
            if (userlocationJson != null)
                userLocation = mapper1.readValue(userlocationJson, UserLocation.class);


        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView storeTitle = (TextView) findViewById(R.id.toolbar_title);
        try {
            String title = loginRepository.getCustomerEntity().getStore().getStoreName() + "  ";
            storeTitle.setText(title);

        } catch (Exception e){

        }


        ImageView infoView = (ImageView)findViewById(R.id.infoView);
        MenuDetailsActivity menuDetailsActivity = this;
        infoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StoreDetailsDialogatMenu(loginRepository.getCustomerEntity().getStore());
                newFragment.setCancelable(false);
                newFragment.show((menuDetailsActivity).getSupportFragmentManager(), "missiles");
            }
        });


/*
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new StoreDetailsDialog(((RegistrationNewActivity)context), store);
                    newFragment.setCancelable(false);
                    newFragment.show(((RegistrationNewActivity)context).getSupportFragmentManager(), "missiles");
                }
            });*/


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        topNavigationView = (BottomNavigationView)findViewById(R.id.topNavigationView);
        topNavigationView.setOnNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationHandler(this));

        //call the service to get the Data
        Intent intent = getIntent();
        String mealTypeId = intent.getStringExtra(DisplayMessageActivity.MEAL_TYPE);




       /* Bitmap myImage = getBitmapFromURL(wallpaper);
        Drawable dr = new BitmapDrawable(myImage);
        ConstraintLayout linearLayout = (ConstraintLayout) findViewById(R.id.menudetailslt);
        linearLayout.setBackground(dr);*/



        if(mealTypeId.equals("1")){
            mealType = Data.BREAKFAST;
        }
        if(mealTypeId.equals("2")){
            mealType = Data.LUNCH;
        }

        if(mealTypeId.equals("3")){
            mealType = Data.DINNER;
        }
        if(mealTypeId.equals("4")){
            mealType = Data.ALL_DAY;
        }




        Menu bottomNavigationMenu = topNavigationView.getMenu();
        topNavigationView.getMenu().getItem(0).setTitle(breakfast);
        topNavigationView.getMenu().getItem(1).setTitle(lunch);
        topNavigationView.getMenu().getItem(2).setTitle(allday);

        //set icons

        String bf_image=loginRepository.getCustomerEntity().getStore().getMealTypes().get(0).getMealTypeIcon();
        StrictMode.ThreadPolicy policy0 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy0);
        try {
            URL url = new URL(bf_image);
            Drawable bf_icon = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream)url.getContent()));
            topNavigationView.getMenu().getItem(0).setIcon(bf_icon);
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }

        String lun_image=loginRepository.getCustomerEntity().getStore().getMealTypes().get(1).getMealTypeIcon();
        StrictMode.ThreadPolicy policy1 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy1);
        try {
            URL url = new URL(lun_image);
            Drawable lun_icon = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream)url.getContent()));
            topNavigationView.getMenu().getItem(1).setIcon(lun_icon);
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }

        String alld_image=loginRepository.getCustomerEntity().getStore().getMealTypes().get(2).getMealTypeIcon();
        StrictMode.ThreadPolicy policy2 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy2);
        try {
            URL url = new URL(alld_image);
            Drawable alld_icon = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream)url.getContent()));
            topNavigationView.getMenu().getItem(2).setIcon(alld_icon);
        } catch (IOException e) {
            //Log.e(TAG, e.getMessage());
        }



        if (mealTypeId.equalsIgnoreCase(mtTypeId.get(0).toString())) {
            topNavigationView.setSelectedItemId(R.id.topNavigationBreakfastMenuId);
        } else if (mealTypeId.equalsIgnoreCase(mtTypeId.get(1).toString())) {
            topNavigationView.setSelectedItemId(R.id.topNavigationLunchMenuId);
        }else if (mealTypeId.equalsIgnoreCase(mtTypeId.get(2).toString())) {
            topNavigationView.setSelectedItemId(R.id.topNavigationAlldayMenuId);
        }

      //  Menu bottomNavigationMenu = myBottomNavigationMenu.getMenu();
        /*if (mealType.equalsIgnoreCase(Data.BREAKFAST)) {
            topNavigationView.setSelectedItemId(R.id.topNavigationBreakfastMenuId);
        } else if (mealType.equalsIgnoreCase(Data.LUNCH)) {
            topNavigationView.setSelectedItemId(R.id.topNavigationLunchMenuId);
        }else if (mealType.equalsIgnoreCase(Data.ALL_DAY)) {
            topNavigationView.setSelectedItemId(R.id.topNavigationAlldayMenuId);
         }*/

    // topNavigationView.setSelectedItemId(bottomNavigationMenu.getItem(0).getItemId());

      //  getDataFromServer(mealType);

        String appsettingsJson = sharedpreferences.getString("APPSETTINGS", null);
        ObjectMapper mapper11 = new ObjectMapper();
        AppSettings appSettings = null;
        try {
            if (appsettingsJson != null)
                appSettings = mapper11.readValue(appsettingsJson, AppSettings.class);


        } catch (IOException e) {
            e.printStackTrace();
        }


        for(Setting setting : appSettings.getSettings()){

            if(setting.getKey().equals("main-background")){
                wallpaper = setting.getValue();
            }
        }

        StrictMode.ThreadPolicy policy10 = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy10);
        try {
            URL url = new URL(wallpaper);
            Drawable backgroundImge = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeStream((InputStream) url.getContent()));
            ConstraintLayout linearLayout = findViewById(R.id.menudetailslt);
            linearLayout.setBackground(backgroundImge);


        }
        catch(IOException io){
        io.printStackTrace();
        }

        EditText searchEditText = (EditText) findViewById(R.id.search);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mAdapter!=null)
                    ((MenuListAdapter) mAdapter).getFilter().filter(s.toString());
            }
        };
        searchEditText.addTextChangedListener(afterTextChangedListener);

        LinearLayout linearLayout = findViewById(R.id.vcartfragment);
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
        }
        else{
            linearLayout.setBackgroundColor(Color.GRAY);
        }

        Data.getInstance().recalculateTotalCostWithoutSaving();


    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    /*
     if (R.id.topNavigationBreakfastMenuId == menuItem.getItemId()) {
            Toast.makeText(this, " Breakfast Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationBreakfastMenuId;
            replaceSubmenu(breakfastSubmenu, viewGrp, null);
            getDataFromServer(Data.BREAKFAST);

        }
        if (R.id.topNavigationLunchMenuId == menuItem.getItemId()) {
            Toast.makeText(this, " Lunch Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationLunchMenuId;
            replaceSubmenu(lunchSubmenu, viewGrp, null);
            getDataFromServer(Data.LUNCH);
        }
        if (R.id.topNavigationAlldayMenuId == menuItem.getItemId()) {
            Toast.makeText(this, " All Day Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationAlldayMenuId;
            replaceSubmenu(alldaySubmenu, viewGrp, null);
            getDataFromServer(Data.ALL_DAY);
        }
     */


    private void getDataFromServer(String mealType) {
        boolean isInCache = false;
        /*
        ViewGroup viewGrp = (ViewGroup) findViewById(R.id.horizontalScrollContainerView);
        if ( mealType != null) {
            if (mealType.equalsIgnoreCase(Data.BREAKFAST)) {

             //   bottomNavigationMenu.
       //         topNavigationView.setSelectedItemId(R.id.topNavigationBreakfastMenuId);
                replaceSubmenu(breakfastSubmenu, viewGrp, null);
                if (Data.getInstance().getBreakfastList() != null) {
                    isInCache = true;
                    mAdapter = new MenuListAdapter(Data.getInstance().getBreakfastList());
                    recyclerView.setAdapter(mAdapter);
                    return;
                }
            }
            if (mealType.equalsIgnoreCase(Data.LUNCH)) {

                replaceSubmenu(lunchSubmenu, viewGrp, null);
                if (Data.getInstance().getLunchList() != null) {
                    isInCache = true;
                    mAdapter = new MenuListAdapter(Data.getInstance().getLunchList());
                    recyclerView.setAdapter(mAdapter);
                    return;
                }
            }
            if (mealType.equalsIgnoreCase(Data.ALL_DAY)) {
           //     topNavigationView.setSelectedItemId(R.id.topNavigationAlldayMenuId);
                replaceSubmenu(alldaySubmenu, viewGrp, null);
                if (Data.getInstance().getAllDayList() != null) {
                    isInCache = true;
                    mAdapter = new MenuListAdapter(Data.getInstance().getAllDayList());
                    recyclerView.setAdapter(mAdapter);
                    return;
                }
            }
        }
        */
        if (isInCache == false ) {
            //nathan
           // showCaryEmptyMessage();
            //builder.show(getSupportFragmentManager(), "test");
            CronetEngine cronetEngine = Cronet.getCronetEngine(this);
            MenuListServiceRequestCallback menuCallback = new MenuListServiceRequestCallback(recyclerView, this);
            MenuFilterCriteria criteria = new MenuFilterCriteria();

            criteria.setMealType(mealType);
            menuCallback.setFilterCriteria(criteria);
            UrlRequest.Callback callback = menuCallback;

            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                    Data.SERVER_URL + "/api/menu/" + loginRepository.getCustomerEntity().getStoreId() , callback, executor);

            UrlRequest request = requestBuilder.build();
            request.start();
        }
    }
    private void replaceSubmenu(String[] values, ViewGroup viewGrp, String selectedItem) {
        viewGrp.removeAllViews();
        if (selectedItem == null) {
            if (values.length>0) {
                selectedItem = values[0];
            }
        }


            for (int i=0,j=0; i<values.length; i++) {
                ViewGroup grp = (ViewGroup) getLayoutInflater().inflate(R.layout.textview_submenu, viewGrp);
                TextView textView = (TextView) grp.getChildAt(j);
                if (selectedItem != null && selectedItem.equalsIgnoreCase(values[i]) ) {
                    textView.setTextColor(Color.parseColor("#3F51B5"));
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                }
                j=j+2;
                textView.setText(values[i]);
                if (i < values.length -1) { //NOT last item
                   // textView.setText(values[i]);
                    getLayoutInflater().inflate(R.layout.spacer_submenu, viewGrp);
                }
            }
    }
/*
    private void showCaryEmptyMessage() {

        builder.setCancelable(false);
        builder.setMessage("Loading...");

        // Create the AlertDialog object and return it
        builder.show();
    }
    */
    public   boolean onNavigationItemSelected( MenuItem menuItem) {

        ViewGroup viewGrp = (ViewGroup) findViewById(R.id.horizontalScrollContainerView);
       /*
        mAdapter = new MenuListAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
        */
        if (R.id.topNavigationBreakfastMenuId == menuItem.getItemId()) {
            //Toast.makeText(this, " Breakfast Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationBreakfastMenuId;

            if(mtTypeId.get(0)==1) {
                replaceSubmenu(breakfastSubmenu, viewGrp, null);
                getDataFromServer(Data.BREAKFAST);
            }
            else if(mtTypeId.get(0)==2) {
                replaceSubmenu(lunchSubmenu, viewGrp, null);
                getDataFromServer(Data.LUNCH);
            }
            else if(mtTypeId.get(0)==3) {
                replaceSubmenu(dinnerSubmenu, viewGrp, null);
                getDataFromServer(Data.DINNER);
            }
            else if(mtTypeId.get(0)==4) {
                replaceSubmenu(alldaySubmenu, viewGrp, null);
                getDataFromServer(Data.ALL_DAY);
            }

        }
        if (R.id.topNavigationLunchMenuId == menuItem.getItemId()) {
            //Toast.makeText(this, " Lunch Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationLunchMenuId;

            //getDataFromServer(Data.LUNCH);
            if(mtTypeId.get(1)==1) {
                replaceSubmenu(breakfastSubmenu, viewGrp, null);
                getDataFromServer(Data.BREAKFAST);
            }
            else if(mtTypeId.get(1)==2) {
                replaceSubmenu(lunchSubmenu, viewGrp, null);
                getDataFromServer(Data.LUNCH);
            }
            else if(mtTypeId.get(1)==3) {
                replaceSubmenu(dinnerSubmenu, viewGrp, null);
                getDataFromServer(Data.DINNER);
            }
            else if(mtTypeId.get(1)==4) {
                getDataFromServer(Data.ALL_DAY);
            }
        }
        if (R.id.topNavigationAlldayMenuId == menuItem.getItemId()) {
            //Toast.makeText(this, " All Day Clicked", Toast.LENGTH_SHORT).show();
            selectedMealType = R.id.topNavigationAlldayMenuId;

            //getDataFromServer(Data.ALL_DAY);
            if(mtTypeId.get(2)==1) {
                replaceSubmenu(breakfastSubmenu, viewGrp, null);
                getDataFromServer(Data.BREAKFAST);
            }
            else if(mtTypeId.get(2)==2) {
                replaceSubmenu(lunchSubmenu, viewGrp, null);
                getDataFromServer(Data.LUNCH);
            }
            else if(mtTypeId.get(2)==3) {
                replaceSubmenu(dinnerSubmenu, viewGrp, null);
                getDataFromServer(Data.DINNER);
            }
            else if(mtTypeId.get(2)==4) {
                replaceSubmenu(alldaySubmenu, viewGrp, null);
                getDataFromServer(Data.ALL_DAY);
            }
        }


        return true;
    }

    public void onClick(View v) {
        TextView view = (TextView) v;
        String[] menuCategoryList  = new String[0];
       /*
       String[]  = new String[] {"Egg Platter", "Omelettes", "Beverages"};
    String[] lunchSubmenu = new String[] {"Appetizers", "Salad", "Sandwiches", "Chineese", "Indian", "Beverages"};
    String[] alldaySubmenu
        */
        //Toast.makeText(this, "Text Clicked: " + view.getText(), Toast.LENGTH_SHORT).show();
        if (selectedMealType == R.id.topNavigationBreakfastMenuId) {
            if(mtTypeId.get(0)==1) {
                menuCategoryList = breakfastSubmenu;
            }
            else if(mtTypeId.get(0)==2) {
                menuCategoryList = lunchSubmenu;
            }
            else if(mtTypeId.get(0)==3) {
                menuCategoryList = dinnerSubmenu;
            }
            else if(mtTypeId.get(0)==4) {
                menuCategoryList = alldaySubmenu;
            }

        }
        if (selectedMealType == R.id.topNavigationLunchMenuId) {
            if(mtTypeId.get(1)==1) {
                menuCategoryList = breakfastSubmenu;
            }
            else if(mtTypeId.get(1)==2) {
                menuCategoryList = lunchSubmenu;
            }
            else if(mtTypeId.get(1)==3) {
                menuCategoryList = dinnerSubmenu;
            }
            else if(mtTypeId.get(1)==4) {
                menuCategoryList = alldaySubmenu;
            }
        }
        if (selectedMealType == R.id.topNavigationAlldayMenuId) {
            if(mtTypeId.get(2)==1) {
                menuCategoryList = breakfastSubmenu;
            }
            else if(mtTypeId.get(2)==2) {
                menuCategoryList = lunchSubmenu;
            }
            else if(mtTypeId.get(2)==3) {
                menuCategoryList = dinnerSubmenu;
            }
            else if(mtTypeId.get(2)==4) {
                menuCategoryList = alldaySubmenu;
            }
        }
        ViewGroup viewGrp = (ViewGroup) findViewById(R.id.horizontalScrollContainerView);
        String clickedMenuCategory = view.getText().toString();
        replaceSubmenu(menuCategoryList, viewGrp,clickedMenuCategory);
        view.setTextColor(Color.parseColor("#3F51B5"));
        view.setTypeface(view.getTypeface(), Typeface.BOLD);

        ((MenuListAdapter) mAdapter).getFilter().filter(view.getText());
        /*

        for (String menuCategory : menuCategoryList) {
            if (menuCategory.trim().equalsIgnoreCase(clickedMenuCategory.trim())) {
               // view.setTextColor(Color.parseColor("#3F51B5"));
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                view.setTextColor(Color.RED);
            } else {
                view.setTextColor(Color.BLACK);
                view.setTypeface(view.getTypeface(), Typeface.NORMAL);
            }
        }
        */
    }

    public void showCartDetails(View view) {
        if(!Data.getInstance().getTotalCost().equals("$ 0.00")) {
            Intent intent = new Intent(this, ViewCartActivity.class);
            intent.putExtra("wallPaper", wallpaper);
            LinearLayout linearLayout = findViewById(R.id.vcartfragment);
            linearLayout.setBackgroundColor(Color.parseColor("#3F51B5"));
            startActivity(intent);
        }else{

            return;
        }
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            System.out.println(" VIDYA " + imageUrl);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Menu mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) mOptionsMenu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        // Associate searchable configuration with the SearchView
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }



        return super.onOptionsItemSelected(item);
    }


}
