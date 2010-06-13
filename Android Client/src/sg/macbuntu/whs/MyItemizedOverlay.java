package sg.macbuntu.whs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	//Show feedback form for corresponding site onTap event
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		String site = item.getSnippet();

        Intent i = new Intent(mContext, Feedback.class);
        i.putExtra("site", site);
        mContext.startActivity(i);
        ((Activity) mContext).finish();
		
		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
