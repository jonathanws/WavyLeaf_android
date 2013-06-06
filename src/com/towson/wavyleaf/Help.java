package com.towson.wavyleaf;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Help extends SherlockActivity {
	
	protected WebView wv;
	protected Context ctx = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.layout_help);
		init();
	}
	
	protected void init() {
		wv = (WebView) this.findViewById(R.id.help_webview);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setBuiltInZoomControls(true);
		
		wv.setWebViewClient(new WebViewClient() {
			
        	@Override
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        		
        	}
        	
        	public boolean shouldOverrideUrlLoading(WebView v, String url) {
        		v.loadUrl(url);
        		return true;
        	}

        	@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				setTitle("Loading...");
			}
        	
        	@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				setTitle("Help");
			}
        
		});
		
		Resources res = this.getResources();
        wv.loadUrl(res.getString(R.string.website));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        		Intent mainIntent = new Intent(this, Main.class);
        		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        		startActivity(mainIntent);
        		finish();
        		return true;
        }
		return super.onOptionsItemSelected(item);
	}

}
