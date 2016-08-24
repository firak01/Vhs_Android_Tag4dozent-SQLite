package com.example.erstesprojekt;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	private ArrayList<Person> liste = new ArrayList<Person>();
	private DatabaseHelper dbHelper;
	
    @SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        } else {
        	liste = (ArrayList<Person>) savedInstanceState.getSerializable(KEY_PERSON_LIST);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    // TODO: Evtl. "Memoization" am Beispiel des ViewHolders demonstrieren

    private ViewHolder viewHolder;
    
    class ViewHolder {
    	EditText editVorname;
    	EditText editNachname;
    	EditText editAlter;
    	ListView listView;
    	
    	public ViewHolder() {
        	editVorname = (EditText) findViewById(R.id.edit_vorname);
        	editNachname = (EditText) findViewById(R.id.edit_nachname);
        	editAlter = (EditText) findViewById(R.id.edit_alter);
        	listView = (ListView) findViewById(R.id.list_view);
    	}    	
    }
    
    private void registerOnItemClick() {
    	viewHolder.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				// TODO: Sicherheitsabfrage
				
				DialogFragment df = new MeinDialogFragment();
				df.show(getSupportFragmentManager(), "MeinDialog");
				
				dbHelper.remove(id);				
				updateAdapter();
			}   		
    	});
    }
    
    public void buttonOkClicked(View view) {   	
    	String vorname = viewHolder.editVorname.getText().toString();
    	String nachname = viewHolder.editNachname.getText().toString();
    	int alter = Integer.parseInt(viewHolder.editAlter.getText().toString());
    	
//		String meldung = getString(R.string.greeting_text) + " " + vorname
//				+ " " + nachname + "! " + getString(R.string.alter_prefix_text)
//				+ " " + alter + " " + getString(R.string.alter_suffix_text) + "!";
    	
//    	Toast toast = Toast.makeText(getApplicationContext(), meldung, Toast.LENGTH_LONG);
//    	toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
//    	toast.show();

    	/*** Benutzerdefiniertes Toast, das auf einem XML-Layout basiert ***/
    	/*******************************************************************/
//    	LayoutInflater inflater = getLayoutInflater();
//    	View layout = inflater.inflate(R.layout.mein_toast_layout, null);
//
//    	TextView text = (TextView) layout.findViewById(R.id.text_mein_toast);
//    	text.setText(meldung);
//
//    	toast = new Toast(getApplicationContext());
//    	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//    	toast.setDuration(Toast.LENGTH_LONG);
//    	toast.setView(layout); // Zuordnung des Layouts zum Toast
//    	toast.show();    	
    	/*******************************************************************/
    	
    	Person p = new Person(vorname, nachname, alter);
    	liste.add(p);
		dbHelper.add(p);
		
		updateAdapter();
		
		//FGL: Hier wollte der Dozent wohl zeigen, dass man mit einem Intent auch andere Apps starten kann
		//**
		Uri webpage = Uri.parse("http://www.google.de");
		Intent intent = new Intent(Intent.ACTION_VIEW, webpage); //Startet den Browser, aber auf meinem Emulator klappt das nicht.		

		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
		
		for (ResolveInfo info : activities) {
			Log.v(ALC_TAG, info.activityInfo.name);
		}
		
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe) {
			startActivity(intent);
		}
		//**/	
    }
  
    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

	// TODO: Im Falle einer teuren Abfrage ist es sinnvoll den Cursor mit einem asynchronen Aufruf zu aktualisieren
    private void updateAdapter() {
        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {"vorname"};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, vorname FROM person ORDER BY lower(vorname)", null);
        
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this, 
                android.R.layout.simple_list_item_1, cursor,
                fromColumns, toViews, 0);
        viewHolder.listView.setAdapter(mAdapter);
    }

    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_PERSON_LIST, liste);
	}

	public final String ALC_TAG = "Activity Lifecycle";
	public final String KEY_PERSON_LIST = "KEY_PERSON_LIST";

	@Override
	protected void onStart() {
		super.onStart();		
		Log.d(ALC_TAG, "onStart");

        viewHolder = new ViewHolder();	        
        dbHelper = new DatabaseHelper(this.getApplicationContext());
        registerOnItemClick();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(ALC_TAG, "onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(ALC_TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(ALC_TAG, "onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(ALC_TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(ALC_TAG, "onRestart");
	}
	
	
	
	///////////////////////////
	
}
