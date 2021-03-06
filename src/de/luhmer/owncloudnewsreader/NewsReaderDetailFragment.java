package de.luhmer.owncloudnewsreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

import de.luhmer.owncloudnewsreader.ListView.SubscriptionExpandableListAdapter;
import de.luhmer.owncloudnewsreader.cursor.NewsListCursorAdapter;
import de.luhmer.owncloudnewsreader.database.DatabaseConnection;
import de.luhmer.owncloudnewsreader.helper.MenuUtilsSherlockFragmentActivity;

/**
 * A fragment representing a single NewsReader detail screen. This fragment is
 * either contained in a {@link NewsReaderListActivity} in two-pane mode (on
 * tablets) or a {@link NewsReaderDetailActivity} on handsets.
 */
public class NewsReaderDetailFragment extends SherlockListFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	protected static final String TAG = "NewsReaderDetailFragment";

	DatabaseConnection dbConn;
	NewsListCursorAdapter lvAdapter;
	String idFeed;
	/**
	 * @return the idFeed
	 */
	public String getIdFeed() {
		return idFeed;
	}

	String idFolder;
	/**
	 * @return the idFolder
	 */
	public String getIdFolder() {
		return idFolder;
	}

	String titel;
	int lastItemPosition;
	
	ArrayList<Integer> databaseIdsOfItems;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public NewsReaderDetailFragment() {
		databaseIdsOfItems = new ArrayList<Integer>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
					
		}*/
		
		if (getArguments().containsKey(NewsReaderDetailActivity.SUBSCRIPTION_ID)) {
			idFeed = getArguments().getString(NewsReaderDetailActivity.SUBSCRIPTION_ID);
		}
		if (getArguments().containsKey(NewsReaderDetailActivity.TITEL)) {
			titel = getArguments().getString(NewsReaderDetailActivity.TITEL);
		}
		if (getArguments().containsKey(NewsReaderDetailActivity.FOLDER_ID)) {
			idFolder = getArguments().getString(NewsReaderDetailActivity.FOLDER_ID);
		}
		
		dbConn = new DatabaseConnection(getActivity());
			
		((SherlockFragmentActivity) getActivity()).getSupportActionBar().setTitle(titel);
		
		UpdateMenuItemsState();//Is called on Tablets and Smartphones but on Smartphones the menuItemDownloadMoreItems is null. So it will be ignored
		
		//getListView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				
		//lvAdapter = new Subscription_ListViewAdapter(this);
		UpdateCursor();
	}
	
	@SuppressWarnings("static-access")
	public void UpdateMenuItemsState()
	{
		MenuUtilsSherlockFragmentActivity mActivity = ((MenuUtilsSherlockFragmentActivity) getActivity());
		
		if(mActivity.getMenuItemDownloadMoreItems() != null)
		{
			if(idFolder.equals(SubscriptionExpandableListAdapter.ALL_UNREAD_ITEMS))
				mActivity.getMenuItemDownloadMoreItems().setEnabled(false);
			else
				mActivity.getMenuItemDownloadMoreItems().setEnabled(true);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(mPrefs.getBoolean(SettingsActivity.CB_MARK_AS_READ_WHILE_SCROLLING_STRING, false))
		{		
			getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
				
	            public void onScrollStateChanged(AbsListView view, int scrollState) {
	            	/*
	            	Log.d(TAG, "LOL" + scrollState);
	            	if(AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState)
	            	{
	            		
	            	}*/
	            }
	
	            
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	            	CheckBox cb = getCheckBoxAtPosition(0, view);
        			ChangeCheckBoxState(cb, true);
        			
	            	if((firstVisibleItem + visibleItemCount) == totalItemCount) {
	            		for (int i = firstVisibleItem + 1; i < firstVisibleItem + visibleItemCount; i++) {
	            			cb = getCheckBoxAtPosition(i - firstVisibleItem, view);
	            			ChangeCheckBoxState(cb, true);
	            		}
	            	}	            	
	            }
	            
	            /*
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	                for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
	                	
	                	if(lastItemPosition < firstVisibleItem)
	                	{
	                		lastItemPosition = firstVisibleItem;
	                		
	                		CheckBox cb = (CheckBox) view.findViewById(R.id.cb_lv_item_read);
	                		if(!cb.isChecked())
	                			cb.setChecked(true);
	                		
	                		//dbConn.
	                	}
	                	
	                    //Cursor cursor = (Cursor)view.getItemAtPosition(i);
	                    //long id = cursor.getLong(cursor.getColumnIndex(AlertsContract._ID));
	                    //String type = cursor.getString(cursor.getColumnIndex(AlertsContract.TYPE));
	                    //Log.d("VIEWED", "This is viewed "+ type + " id: " + id);
	                    //Log.d("VIEWED", "This is viewed "+ firstVisibleItem + " id: ");
	
	                    // here I can get the id and mark the item read
	                }
	            }*/
	            
	            
	        });
		}
		
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void ChangeCheckBoxState(CheckBox cb, boolean state)
	{
		if(cb != null)
			if(cb.isChecked() != state)
				cb.setChecked(state);
	}
	
	private CheckBox getCheckBoxAtPosition(int pos, AbsListView viewLV)
	{
		ListView lv = (ListView) viewLV;
		View view = (View) lv.getChildAt(pos);
		if(view != null)
			return (CheckBox) view.findViewById(R.id.cb_lv_item_read);
		else
			return null;
	}
	

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		lastItemPosition = -1;
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if(lvAdapter != null)
			lvAdapter.CloseDatabaseConnection();
		if(dbConn != null)
			dbConn.closeDatabase();
		super.onDestroy();
	}

	public void UpdateCursor()
	{
		try
		{
			Cursor cursor = getRightCusor(idFolder);
			
			databaseIdsOfItems.clear();
			if(cursor != null)
				while(cursor.moveToNext())
					databaseIdsOfItems.add(cursor.getInt(0));
			
			
			if(lvAdapter == null)
			{			
				lvAdapter = new NewsListCursorAdapter(getActivity(), cursor);
				setListAdapter(lvAdapter);
			}
			else
				lvAdapter.changeCursor(cursor);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

    public Cursor getRightCusor(String ID_FOLDER)
    {
    	SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	boolean onlyUnreadItems = mPrefs.getBoolean(SettingsActivity.CB_SHOWONLYUNREAD_STRING, false);
    	boolean onlyStarredItems = false;
    	if(ID_FOLDER != null)
    		if(ID_FOLDER.equals(SubscriptionExpandableListAdapter.ALL_STARRED_ITEMS))
    			onlyStarredItems = true;
    		
        if(idFeed != null)
            return dbConn.getAllItemsForFeed(idFeed, onlyUnreadItems, onlyStarredItems);
        else if(idFolder != null)
        {
        	if(idFolder.equals(SubscriptionExpandableListAdapter.ALL_STARRED_ITEMS))
        		onlyUnreadItems = false;
            return dbConn.getAllItemsForFolder(idFolder, onlyUnreadItems);
        }
        return null;
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_newsreader_detail, container, false);		
		return rootView;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
				
		Intent intentNewsDetailAct = new Intent(getActivity(), NewsDetailActivity.class);
		//if(idSubscription != null)
		//	intentNewsDetailAct.putExtra(NewsReaderDetailActivity.SUBSCRIPTION_ID, Long.valueOf(idSubscription));
		//else if(idFolder != null)
		//	intentNewsDetailAct.putExtra(NewsReaderDetailActivity.FOLDER_ID, Long.valueOf(idFolder));		
		
		//intentNewsDetailAct.putIntegerArrayListExtra(NewsDetailActivity.DATABASE_IDS_OF_ITEMS, databaseIdsOfItems);
		//Integer[] databaseIdsOfItemsArray = databaseIdsOfItems.toArray(new Integer[databaseIdsOfItems.size()]);
		intentNewsDetailAct.putIntegerArrayListExtra(NewsDetailActivity.DATABASE_IDS_OF_ITEMS, databaseIdsOfItems);
		
		intentNewsDetailAct.putExtra(NewsReaderDetailActivity.ITEM_ID, position);
		intentNewsDetailAct.putExtra(NewsReaderDetailActivity.TITEL, titel);
		startActivityForResult(intentNewsDetailAct, Activity.RESULT_CANCELED);
		
		super.onListItemClick(l, v, position, id);
	}

	public ArrayList<Integer> getDatabaseIdsOfItems() {
		return databaseIdsOfItems;
	}

}
