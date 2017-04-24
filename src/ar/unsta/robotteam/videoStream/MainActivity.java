package ar.unsta.robotteam.videoStream;

import ar.unsta.robotteam.videoStream.R;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import ar.unsta.robotteam.network.utils.NetworkUtils;
import ar.unsta.robotteam.network.utils.ServerFinder;
import ar.unsta.robotteam.network.video.VideoClient;
import ar.unsta.robotteam.utils.GuiUtils;

public class MainActivity extends Activity {

	private EditText tf_serverIP;
	private EditText tf_serverPort;
	private Context m_context;
	String serverIp;
	int serverPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		m_context = this;

		initComponents();

	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

	public void initComponents() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		tf_serverIP = (EditText) findViewById(R.id.tf_serverIP);
		tf_serverPort = (EditText) findViewById(R.id.tf_serverPort);
	}

	public void buttonConnectClickListener(View v) {

		if (GuiUtils.validateTextField(tf_serverIP, "Insert an Ip address")
				&& GuiUtils.validateTextField(tf_serverPort,
						"Insert a port number")) {

			if (canConnect()) {

				serverIp = tf_serverIP.getText().toString();
				serverPort = Integer.parseInt(tf_serverPort.getText()
						.toString());

				Intent i = new Intent(getApplicationContext(),
						VideoActivity.class);
				i.putExtra("serverip", serverIp);
				i.putExtra("serverport", serverPort);
				startActivity(i);

			} else {
				GuiUtils.alertDialogToSettings(m_context, "WiFi Disconected",
						"You will be redirected to the wifi settings", "ok",
						Settings.ACTION_WIFI_SETTINGS);
			}// endif can connect
		}// endif validation
	}

	public void buttonFindServerClickListener(View v) {

		if (NetworkUtils.canConnect(m_context)) {
			ServerFinder serverFinder = new ServerFinder();

			serverFinder.find(VideoClient.VIDEO_SERVICE_ID);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (serverFinder.foundServer()) {

				serverIp = serverFinder.getServerIp();
				System.out.println(serverIp);
				tf_serverIP.setText(serverIp);

				serverPort = serverFinder.getServerPort();
				System.out.println(serverPort);
				tf_serverPort.setText(serverPort + "");

			} else {
			}
		} else {

			GuiUtils.alertDialogToSettings(m_context, "WiFi Disconected",
					"You will be redirected to the wifi settings", "ok",
					Settings.ACTION_WIFI_SETTINGS);
		}
	}

	public void buttonDisconnectClickListener(View v) {

	}

	private boolean canConnect() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (intent.getBooleanExtra(
						WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {

				} else {

					GuiUtils.alertDialogToSettings(m_context,
							"WiFi Disconected",
							"You will be redirected to the wifi settings",
							"ok", Settings.ACTION_WIFI_SETTINGS);
				}
			}
		}
	};
}
