package com.ese2013.mub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ese2013.mub.model.DailyMenuplan;
import com.ese2013.mub.model.Day;
import com.ese2013.mub.model.Mensa;
import com.ese2013.mub.model.Menu;
import com.ese2013.mub.model.WeeklyMenuplan;

/**
 * 
 * This class works the CriteriaMatcher Algoritm and ensures 
 */
public class CriteriaMatcher {
	private List<Criteria> container;
	private List<Mensa> temp;

	public CriteriaMatcher() {
		container = new ArrayList<Criteria>();
		temp = new ArrayList<Mensa>();
	}

	/**
	 * Matches a Set of criteria to each {@link Menu} of the current day and returns a
	 * List of {@link Criteria} objects. The List contains a Criteria object for each
	 * matching criteria of the criteriaSet. The Algorithm also fills this
	 * Criteria object with all the Menus the String is matching and the mensas
	 * this Menu is served in. If criteriaSet or mensas are empty, an empty list is returned.
	 * Is either criteriaSet or mensas are null a {@link NullPointerException} may be thrown.
	 * 
	 * @param criteriaSet
	 *            String set of criterias you want to match with.
	 * @param mensas
	 *            List of {@link Mensa}s you want to match the criterias with.
	 * @return List of Criteria Objects which stores the matching menus and the
	 *         mensa in which the menu is served in.
	 */
	public List<Criteria> match(Set<String> criteriaSet, List<Mensa> mensas) {
		for (String criteria : criteriaSet) {
			Criteria crit = new Criteria();
			crit.setCriteriaName(criteria);
			for (Mensa mensa : mensas) {
				WeeklyMenuplan weekly = mensa.getMenuplan();
				DailyMenuplan daily = weekly.getDailymenuplan(Day.today());
				if (daily != null) {
					for (Menu menu : daily.getMenus()) {
						if ((menu.getDescription().toLowerCase(Locale.getDefault())).contains(criteria
								.toLowerCase(Locale.getDefault()))) {

							if (!container.contains(crit))
								container.add(crit);

							if (!crit.getMap().containsKey(menu)) {
								temp = new ArrayList<Mensa>();
								temp.add(mensa);
								crit.getMap().put(menu, temp);
							} else {
								temp = new ArrayList<Mensa>();
								temp = crit.getMap().get(menu);
								temp.add(mensa);

							}
						}
					}
				}
			}
		}
		return container;
	}
}
package com.ese2013.mub.service;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ese2013.mub.util.SharedPrefsHandler;

public class BootReceiver extends BroadcastReceiver {
	/**
	 * Receives BootUpComplete Broadcast and sets an {@link AlarmManager} with
	 * an Intent for the {@link AlarmReceiver}. Its actions depend on the
	 * predefined values made in the Settings, if there are now settings made,
	 * it does nothing else it sets an daily repeating AlarmManager, starting
	 * the AlarmReceiver.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPrefsHandler pref = new SharedPrefsHandler(context);
		if (pref.getDoNotification()) {
			Calendar tenOClock = Calendar.getInstance(Locale.getDefault());
			tenOClock.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 10, 0, 0);
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),
					PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.setInexactRepeating(AlarmManager.RTC, tenOClock.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					operation);
		}
	}
}
package ch.xonix.mensa.unibe;

import ch.xonix.mensa.unibe.model.Mensa;
import ch.xonix.mensa.unibe.request.MensasRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Main extends Activity {

	private static final CharSequence MESSAGE_GET_DATA = "lade...";
	protected static final String MENSA_ID_KEY = "ch.xonix.mensa.unibe::mensa_ID";
	protected static final String MENSA_NAME_KEY = "ch.xonix.mensa.unibe::mensa_name";
	private ListView mensaListView;
	private ArrayAdapter<Mensa> mensaListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create a array adapter
		mensaListAdapter = new ArrayAdapter<Mensa>(this,
				android.R.layout.simple_list_item_1);
		mensaListView = (ListView) this.findViewById(R.id.mensa_list);
		mensaListView.setAdapter(mensaListAdapter);
		// make aysnc request to get a list of all canteens
		Toast.makeText(Main.this, MESSAGE_GET_DATA, Toast.LENGTH_SHORT).show();
		new MensasRequest(mensaListAdapter).execute();

		// ad OnItemClickListener
		mensaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long arg3) {
				Mensa mensa = (Mensa) parent.getItemAtPosition(pos);
				Intent intent = new Intent(Main.this, MenuplanActivity.class);
				intent.putExtra(MENSA_ID_KEY, mensa.getId());
				intent.putExtra(MENSA_NAME_KEY, mensa.getName());
				Main.this.startActivity(intent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
package com.ese2013.mub;

import com.ese2013.mub.social.LoginService;
import com.ese2013.mub.social.SocialManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This simple abstract class has the functionality which is common for both
 * Invited and Sent Invites Fragment.
 */
public abstract class AbstractInvitationsFragment extends Fragment {
	private ListView invitedList;
	private InvitationsBaseAdapter adapter;
	private MenuItem menuItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		adapter = createAdapter();
		View view = inflater.inflate(R.layout.fragment_invited, null);
		invitedList = (ListView) view.findViewById(R.id.invited_list);
		setUpEmptyView(view);
		invitedList.setAdapter(adapter);
		setHasOptionsMenu(true);
		return view;
	}

	protected abstract InvitationsBaseAdapter createAdapter();

	protected void setUpEmptyView(View view) {
		TextView showMessage = (TextView) view.findViewById(R.id.show_message);
		if (LoginService.isLoggedIn())
			showMessage.setText(R.string.no_invites);
		else
			showMessage.setText(R.string.not_loged_in);

		invitedList.setEmptyView(showMessage);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (LoginService.isLoggedIn())
			inflater.inflate(R.menu.invitations_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_invite_button:
			((DrawerMenuActivity) getActivity()).createInvitation();
			return true;
		case R.id.refresh:
			SocialManager.getInstance().loadInvites();
			menuItem = item;
			menuItem.setActionView(R.layout.progress_bar);
			menuItem.expandActionView();
			Toast.makeText(getActivity(), R.string.toast_refreshing_msg, Toast.LENGTH_SHORT).show();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SocialManager.getInstance().removeObserver(adapter);
	}

	/**
	 * changes the progressbar in the ActionBar back to not loading
	 */
	protected void loadingFinished() {
		if (menuItem != null) {
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	}
}
package com.ese2013.mub;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 
 * Listener which sets the viability of a view from visible to gone and vice versa
 *
 */
public class ToggleListener implements OnClickListener {
	private View toToggle;
	private Context ctx;
	
	public ToggleListener(View toToggle, Context ctx){
		this.toToggle = toToggle;
		this.ctx = ctx;
	}
	@Override
	public void onClick(View v) {
		
		if(toToggle.isShown()){
			 ToggleAnimation.slide_up(ctx, toToggle);
			 toToggle.setVisibility(View.GONE);
		}
		else{
			ToggleAnimation.slide_down(ctx, toToggle);
			 toToggle.setVisibility(View.VISIBLE);
		}
			
	}

}
package com.ese2013.mub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This activity is just used as a callback for the push notifications. It
 * starts the normal app and opens it at the invited tab.
 */
public class PushNotificationCallbackActivity extends Activity {
	public static final String SHOW_INVITES = "showInvites";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, DrawerMenuActivity.class);
		intent.putExtra(SHOW_INVITES, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}