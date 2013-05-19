package net.junian.code.qiblasense;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;

public class QiblaSenseActivity extends MapActivity {
	private MapView mapView;
	private MapController mapController;
	private GeoPoint qibla = new GeoPoint((int) (Const.QIBLA_LATITUDE * 1E6),
			(int) (Const.QIBLA_LONGITUDE * 1E6));
	private GeoPoint myPosition = new GeoPoint((int) (-7.2796 * 1E6),
			(int) (112.797218 * 1E6));
	private Projection mapProjection;
	private List<Overlay> mapOverlays;
	private Location locQibla;
	private Location locMyPosition;

	private double bearing(Location source, Location dest)
	{
		double dLong = dest.getLongitude() - source.getLongitude();
		double dLon = Math.toRadians(dLong);
		double lat1 = Math.toRadians(source.getLatitude());
		double lat2 = Math.toRadians(dest.getLatitude());
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1)*Math.sin(lat2) -
		        Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
		double brng = Math.atan2(y, x);
		return Math.toDegrees(brng);
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.menuExit:
			this.finish();
			break;
		case R.id.menuBearing:
			float heading = locMyPosition.bearingTo(locQibla);
			Log.d("bearing", heading + "");
			//heading = -heading / 360.0f + 180.0f;
			Log.d("bearing", heading + "");
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Bearing");
			dialog.setMessage("My Location:\n[" + (myPosition.getLatitudeE6()/1E6) + ", " + (myPosition.getLongitudeE6()/1E6)
					+"]\n\nAngle:\n" + bearing(locMyPosition, locQibla));
			dialog.show();
			break;
		default:
			break;
		}
		return true;
	};

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setSatellite(true);
		mapController = mapView.getController();
		mapProjection = mapView.getProjection();
		mapOverlays = mapView.getOverlays();
		//mapController.setZoom(mapView.getMaxZoomLevel()/2);
		UpdateMapView();
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 250, 10,
				new LocationListener() {

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub

					}

					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub

					}

					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub

					}

					public void onLocationChanged(Location location) {
						// TODO Auto-generated method stub
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();
						myPosition = new GeoPoint((int) (latitude * 1E6), (int)(longitude * 1E6));
						UpdateMapView();
					}
				});
	}

	private void UpdateMapView() {
		// TODO Auto-generated method stub
		mapOverlays.clear();
		mapOverlays.add(new LineOverlay(myPosition, qibla));
		
		//GeoPoint west = new GeoPoint((int)(90*1E6),0);
		//mapOverlays.add(new LineOverlay(myPosition, west, Color.RED));
		
		mapController.animateTo(myPosition);
		mapController.setCenter(myPosition);
		mapController.setZoom(mapView.getMaxZoomLevel() - 2);
		
		locQibla = new Location("qibla");
		locMyPosition = new Location("myPosition");
		locQibla.setLatitude(qibla.getLatitudeE6() / 1E6);
		locQibla.setLongitude(qibla.getLongitudeE6() / 1E6);
		locMyPosition.setLatitude(myPosition.getLatitudeE6() / 1E6);
		locMyPosition.setLongitude(myPosition.getLongitudeE6() / 1E6);
		
		
		
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private class LineOverlay extends Overlay {

		GeoPoint gP1 = new GeoPoint(19240000, -99120000);
		GeoPoint gP2 = new GeoPoint(37423157, -122085008);
		int color = Color.BLUE;

		public LineOverlay(GeoPoint gp1, GeoPoint gp2) {
			this.gP1 = gp1;
			this.gP2 = gp2;
		}

		public LineOverlay(GeoPoint gp1, GeoPoint gp2, int color) {
			this.gP1 = gp1;
			this.gP2 = gp2;
			this.color = color;
		}

		public void draw(Canvas canvas, MapView mapv, boolean shadow) {
			super.draw(canvas, mapv, shadow);

			Paint mPaint = CreatePaint();

			Point p1 = new Point();
			Point p2 = new Point();
			Path path = new Path();

			mapProjection.toPixels(gP1, p1);
			mapProjection.toPixels(gP2, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);
			
			canvas.drawPath(path, mPaint);
			
			//west
			//mPaint = CreatePaint();
			//mPaint.setColor(Color.GREEN);
			path.moveTo(p1.x, p1.y);
			path.lineTo(p1.x - 256, p1.y);
			canvas.drawPath(path, mPaint);
			//north
			//mPaint = CreatePaint();
			//mPaint.setColor(Color.GREEN);
			path.moveTo(p1.x, p1.y);
			path.lineTo(p1.x, p1.y-256);
			canvas.drawPath(path, mPaint);
		}
		
		private Paint CreatePaint()
		{
			Paint mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setColor(color);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(4);
			return mPaint;
		
		}

	}

}