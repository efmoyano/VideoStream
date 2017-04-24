package ar.unsta.robotteam.videoStream;

import ar.unsta.robotteam.videoStream.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.WindowManager;
import android.widget.ImageView;
import ar.unsta.robotteam.network.video.VideoClient;
import ar.unsta.robotteam.network.video.VideoEventListener;
import ar.unsta.robotteam.utils.GuiUtils;

public class VideoActivity extends Activity {

	public VideoClient rtspClient;
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		initComponents();

		createRtspClient();

	}

	@Override
	public void onBackPressed() {
	}

	private void initComponents() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.activity_video);

		imageView = (ImageView) findViewById(R.id.imageView1);

	}

	private void createRtspClient() {
		rtspClient = new VideoClient(new VideoEventListener() {

			@Override
			public void onImageReceived(final Bitmap p_image) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						imageView.setImageBitmap(p_image);

					}
				});
			}
		});

		String serverIp = null;
		int serverPort = 0;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			serverIp = extras.getString("serverip");
			serverPort = extras.getInt("serverport");
		}

		rtspClient.setServerIP(serverIp);
		rtspClient.setServerPortNumber(serverPort);

		if (rtspClient.connect()) {

			rtspClient.play();
			GuiUtils.showMessage(this, "Connection Successful");

		} else {

			GuiUtils.showMessage(this, "Could not connect to the server");

		} // endif connect
	}
}
