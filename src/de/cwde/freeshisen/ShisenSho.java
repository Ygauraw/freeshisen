package de.cwde.freeshisen;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ShisenSho extends Application {	
	private static ShisenSho instance = null; 
	private ShisenShoView view = null;
	public ShisenShoActivity activity = null;

	public Board board;
	public int[] boardSize=new int[2];
	public int difficulty=1; // 1=Easy, 2=Hard
	public int size=3; // 1=Small, 2=Medium, 3=Big
	public String tilesetid = "classic";
	public boolean gravity=true;
	public boolean timeCounter=true;
//	public boolean usesound=false;

	public void newPlay() {
		loadOptions();
		board = new Board();
		board.buildRandomBoard(boardSize[0],boardSize[1],difficulty,gravity);
	}

	public void setSize(int s) {
		size = s;

		switch (s) {
		case 1:
			boardSize[0] = 6 + 2;
			boardSize[1] = 8 + 2;
			break;
		case 2:
			boardSize[0] = 6 + 2;
			boardSize[1] = 12 + 2;
			break;
		case 3:
		default:
			boardSize[0] = 6 + 2;
			boardSize[1] = 16 + 2;
			break;
		}
	}

	public void sleep(int deciSeconds) {
		try {
			Thread.sleep(deciSeconds*100);
		} catch (InterruptedException e) { }
	}

	public ShisenSho() {
		instance = this;
		setSize(size);
	}

	public static synchronized ShisenSho app() {
		return instance;
	}

	public synchronized ShisenShoView getView() {
		if (view == null) view = new ShisenShoView(this);
		return view;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		Log.d("ShisenSho", "starting up...");
		loadOptions();
	}

	private void loadOptions() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		// FIXME: handle NumberFormatException here?
		setSize(Integer.parseInt(sp.getString("pref_size", "1")));
		difficulty = Integer.parseInt(sp.getString("pref_diff", "1"));
		gravity = sp.getBoolean("pref_grav", true);
		timeCounter = sp.getBoolean("pref_time", true);
		tilesetid = sp.getString("pref_tile", "");
//		usesound = sp.getBoolean("pref_sound", false);
	}

	public void checkForChangedOptions() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		// FIXME: handle NumberFormatException here?
		int size = Integer.parseInt(sp.getString("pref_size", "1"));
		int difficulty = Integer.parseInt(sp.getString("pref_diff", "1"));
		boolean gravity = sp.getBoolean("pref_grav", true);
		boolean timeCounter = sp.getBoolean("pref_time", true);
		String tilesetid = sp.getString("pref_tile", "");
//		boolean usesound = sp.getBoolean("pref_sound", false);

		boolean needsReset = false;

		if (size != this.size) {
			needsReset = true;
		}

		if (difficulty != this.difficulty) {
			needsReset = true;
		}

		if (gravity != this.gravity) {
			needsReset = true;
		}

		if (timeCounter != this.timeCounter) {
			needsReset = true;
		}

		if ((tilesetid != this.tilesetid) && (view != null)) {
			// tileset can be changed without a reset
			this.tilesetid = tilesetid;
			view.loadTileset();
		}

//		if ((usesound != this.usesound) && (view != null)) {
//			this.usesound = usesound;
//			view.setSoundEnabled(usesound);
//		}

		if (needsReset) {
			if ((view != null) && (activity != null)) {
				view.onOptionsChanged();
			} else {
				Log.d("ShisenSho", "Preferences changed, but no view or activity online - huh?");
			}
		}

	}
}
