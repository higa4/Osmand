package com.osmand;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.osmand.map.ITileSource;
import com.osmand.map.TileSourceManager;
import com.osmand.map.TileSourceManager.TileSourceTemplate;
import com.osmand.osm.LatLon;

public class OsmandSettings {
	
	// These settings are stored in SharedPreferences
	public static final String SHARED_PREFERENCES_NAME = "com.osmand.settings";

	// this value string is synchronized with android.xml preference name	
	public static final String USE_INTERNET_TO_DOWNLOAD_TILES = "use_internet_to_download_tiles";
	public static boolean isUsingInternetToDownloadTiles(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		return prefs.getBoolean(USE_INTERNET_TO_DOWNLOAD_TILES, true);
	}
	
	// this value string is synchronized with android.xml preference name
	public static final String SHOW_POI_OVER_MAP = "show_poi_over_map";
	public static boolean isShowingPoiOverMap(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		return prefs.getBoolean(SHOW_POI_OVER_MAP, false);
	}
	
	// this value string is synchronized with android.xml preference name
	public static final String ROTATE_MAP_TO_BEARING = "rotate_map_to_bearing";
	public static boolean isRotateMapToBearing(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		return prefs.getBoolean(ROTATE_MAP_TO_BEARING, false);
	}
	
	// this value string is synchronized with android.xml preference name
	public static final String MAP_VIEW_3D = "map_view_3d";
	public static boolean isMapView3D(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		return prefs.getBoolean(MAP_VIEW_3D, false);
	}
	
	
	// this value string is synchronized with android.xml preference name
	public static final String MAP_TILE_SOURCES = "map_tile_sources";
	public static ITileSource getMapTileSource(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		String tileName = prefs.getString(MAP_TILE_SOURCES, null);
		if(tileName != null){
			List<TileSourceTemplate> list = TileSourceManager.getKnownSourceTemplates();
			for(TileSourceTemplate l : list){
				if(l.getName().equals(tileName)){
					return l;
				}
			}
		}
		return TileSourceManager.getMapnikSource();
	}
	
	public static String getMapTileSourceName(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		String tileName = prefs.getString(MAP_TILE_SOURCES, null);
		if(tileName != null){
			return tileName;
		}
		return TileSourceManager.getMapnikSource().getName();
	}

	// This value is a key for saving last known location shown on the map
	public static final String LAST_KNOWN_MAP_LAT = "last_known_map_lat";
	public static final String LAST_KNOWN_MAP_LON = "last_known_map_lon";
	public static final String LAST_KNOWN_MAP_ZOOM = "last_known_map_zoom";
	public static LatLon getLastKnownMapLocation(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		float lat = prefs.getFloat(LAST_KNOWN_MAP_LAT, 0);
		float lon = prefs.getFloat(LAST_KNOWN_MAP_LON, 0);
		return new LatLon(lat, lon);
	}
	
	public static void setLastKnownMapLocation(Context ctx, double latitude, double longitude){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		Editor edit = prefs.edit();
		edit.putFloat(LAST_KNOWN_MAP_LAT, (float) latitude);
		edit.putFloat(LAST_KNOWN_MAP_LON, (float) longitude);
		edit.commit();
	}
	
	public static int getLastKnownMapZoom(Context ctx){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		return prefs.getInt(LAST_KNOWN_MAP_ZOOM, 3);
	}
	
	public static void setLastKnownMapZoom(Context ctx, int zoom){
		SharedPreferences prefs = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
		Editor edit = prefs.edit();
		edit.putInt(LAST_KNOWN_MAP_ZOOM, zoom);
		edit.commit();
	}

}