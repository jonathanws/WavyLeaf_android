package com.towson.wavyleaf;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.actionbarsherlock.app.SherlockExpandableListActivity;

public class HelpExpanded extends SherlockExpandableListActivity implements OnChildClickListener {

	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		ExpandableListView expandbleLis = getExpandableListView();
		expandbleLis.setDividerHeight(0);
		expandbleLis.setGroupIndicator(null);
		expandbleLis.setClickable(true);

		setGroupData();
		setChildGroupData();

		HelpAdapter mNewAdapter = new HelpAdapter(groupItem, childItem);
		mNewAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		getExpandableListView().setAdapter(mNewAdapter);
		expandbleLis.setOnChildClickListener(this);
	}

	public void setGroupData() {
		groupItem.add("How this works");
		groupItem.add("Identification");
		groupItem.add("FAQ");
	}
	
	public void setChildGroupData() {
		
		Resources res = getResources();
		
		// How this works
		ArrayList<String> child = new ArrayList<String>();
		child.add(res.getString(R.string.layout_help_how_first) + "\n\n"
				+ res.getString(R.string.layout_help_how_second) + "\n\n"
				+ res.getString(R.string.layout_help_how_third));
		childItem.add(child);

		// Identification
		child = new ArrayList<String>();
		child.add(res.getString(R.string.layout_help_identify_first) + "\n\n"
				+ res.getString(R.string.layout_help_identify_second) + "\n\n"
				+ res.getString(R.string.layout_help_identify_third) + "\n\n"
				+ res.getString(R.string.layout_help_identify_fourth));
		childItem.add(child);
		
		// FAQ
		child = new ArrayList<String>();
		child.add(res.getString(R.string.layout_help_faq_first_question) + "\n\n"
				+ res.getString(R.string.layout_help_faq_first_answer) + "\n\n"
				+ res.getString(R.string.layout_help_faq_second_question) + "\n\n"
				+ res.getString(R.string.layout_help_faq_second_answer) + "\n\n"
				+ res.getString(R.string.layout_help_faq_third_question) + "\n\n"
				+ res.getString(R.string.layout_help_faq_third_answer) + "\n\n"
				+ res.getString(R.string.layout_help_faq_fourth_question) + "\n\n"
				+ res.getString(R.string.layout_help_faq_fourth_answer));
		childItem.add(child);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//		Toast.makeText(HelpExpanded.this, "Clicked On Child", Toast.LENGTH_SHORT).show();
		return true;
	}
}