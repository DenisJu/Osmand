/**
 * 
 */
package com.osmand.activities.search;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.osmand.OsmandSettings;
import com.osmand.PoiFilter;
import com.osmand.PoiFiltersHelper;
import com.osmand.R;
import com.osmand.activities.EditPOIFilterActivity;
import com.osmand.osm.LatLon;

/**
 * @author Maxim Frolov
 * 
 */
public class SearchPoiFilterActivity extends ListActivity {

	private Typeface typeFace;


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.searchpoilist);
		
		
		typeFace = Typeface.create((String)null, Typeface.ITALIC);
		
		// ListActivity has a ListView, which you can get with:
		ListView lv = getListView();

		// Then you can create a listener like so:
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				PoiFilter poi = ((AmenityAdapter) getListAdapter()).getItem(pos);
				showEditActivity(poi);
				return true;
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		List<PoiFilter> filters = new ArrayList<PoiFilter>(PoiFiltersHelper.getUserDefinedPoiFilters(this)) ;
		filters.addAll(PoiFiltersHelper.getOsmDefinedPoiFilters(this));
		setListAdapter(new AmenityAdapter(filters));
	}

	private void showEditActivity(PoiFilter poi) {
		if(!poi.isStandardFilter()) {
			Bundle bundle = new Bundle();
			Intent newIntent = new Intent(SearchPoiFilterActivity.this, EditPOIFilterActivity.class);
			// folder selected
			bundle.putString(EditPOIFilterActivity.AMENITY_FILTER, poi.getFilterId());
			newIntent.putExtras(bundle);
			startActivityForResult(newIntent, 0);
		}
	}
	public void onListItemClick(ListView parent, View v, int position, long id) {
		final PoiFilter filter = ((AmenityAdapter) getListAdapter()).getItem(position);
		if(filter.getFilterId().equals(PoiFilter.CUSTOM_FILTER_ID)){
			showEditActivity(filter);
			return;
		}
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setItems(new String[]{getString(R.string.search_nearby), getString(R.string.search_near_map)}, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Bundle bundle = new Bundle();
				Intent newIntent = new Intent(SearchPoiFilterActivity.this, SearchPOIActivity.class);
				bundle.putString(SearchPOIActivity.AMENITY_FILTER, filter.getFilterId());
				if(which == 1){
					LatLon last = OsmandSettings.getLastKnownMapLocation(SearchPoiFilterActivity.this);
					if(last != null){
						bundle.putDouble(SearchPOIActivity.SEARCH_LAT, last.getLatitude());
						bundle.putDouble(SearchPOIActivity.SEARCH_LON, last.getLongitude());
					}
					
				}
				newIntent.putExtras(bundle);
				startActivityForResult(newIntent, 0);
			}
		});
		b.show();
	}



	class AmenityAdapter extends ArrayAdapter<PoiFilter> {
		AmenityAdapter(List<PoiFilter> list) {
			super(SearchPoiFilterActivity.this, R.layout.searchpoi_list, list);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.searchpoifolder_list, parent, false);
			TextView label = (TextView) row.findViewById(R.id.folder_label);
			ImageView icon = (ImageView) row.findViewById(R.id.folder_icon);
			PoiFilter model = getItem(position);
			label.setText(model.getName());
			if(model.getFilterId().equals(PoiFilter.CUSTOM_FILTER_ID)){
				label.setTypeface(typeFace);
			}
			icon.setImageResource(model.isStandardFilter() ? R.drawable.folder : R.drawable.favorites);
			return (row);
		}

	}
}
