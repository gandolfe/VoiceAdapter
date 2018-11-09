package com.zhonghong.views;

public class SettingsPanel {

//	private static final String SUB_TAG = "SettingsPanel";
//	
//	private View panelView;
//	private LinearLayout btnCloseScreen;
//	private LinearLayout btnMute;
//	private LinearLayout btnBluetooth;
//	private LinearLayout btnWifi;
//	private LinearLayout btnLoudness;
//
//	private ImageView ivMute;
//	private ImageView ivBluetooth;
//	private ImageView ivWifi;
//	private ImageView ivLoudness;
//	private TextView tvMute;
//	private TextView tvBluetooth;
//	private TextView tvWifi;
//	private TextView tvLoudness;
//	private TextView volumeTv;
//	private SeekBar volumeBar;
//
//	private VolumeManager mVolumeManager;
//	private SettingsProxy mSettingsProxy;
//	private BTManager mBtManager;
//	private MyWifiManager myWifiManager;
//	private WindowManager mWindowManager;
//	private Context mContext;
//	
//	private boolean isBtCall = false;
//	private boolean isChangeVol = false;
	
	public SettingsPanel(){
		
	}
//	
//	public SettingsPanel(View panelView, Context context) {
//		mContext = context;
//		this.panelView = panelView;
//		initView();
//		initData();
//	}
//
//	private void initView() {
//		btnCloseScreen = (LinearLayout) panelView
//				.findViewById(R.id.btn_colse_screen);
//		btnMute = (LinearLayout) panelView.findViewById(R.id.btn_mute);
//		btnBluetooth = (LinearLayout) panelView
//				.findViewById(R.id.btn_bluetooth);
//		btnWifi = (LinearLayout) panelView.findViewById(R.id.btn_wifi);
//		btnLoudness = (LinearLayout) panelView.findViewById(R.id.btn_loud);
//
//		ivMute = (ImageView) panelView.findViewById(R.id.iv_mute);
//		ivBluetooth = (ImageView) panelView.findViewById(R.id.iv_bluetooth);
//		ivWifi = (ImageView) panelView.findViewById(R.id.iv_wifi);
//		ivLoudness = (ImageView) panelView.findViewById(R.id.iv_loud);
//		tvMute = (TextView) panelView.findViewById(R.id.tv_mute);
//		tvBluetooth = (TextView) panelView.findViewById(R.id.tv_bluetooth);
//		tvWifi = (TextView) panelView.findViewById(R.id.tv_wifi);
//		tvLoudness = (TextView) panelView.findViewById(R.id.tv_loud);
//		
//		volumeTv = (TextView) panelView.findViewById(R.id.volume_tv);
//		volumeBar = (SeekBar) panelView.findViewById(R.id.volume_bar);
//		
//		btnCloseScreen.setOnClickListener(this);
//		btnMute.setOnClickListener(this);
//		btnBluetooth.setOnClickListener(this);
//		btnWifi.setOnClickListener(this);
//		btnLoudness.setOnClickListener(this);
//		btnBluetooth.setOnLongClickListener(this);
//		btnWifi.setOnLongClickListener(this);
//		volumeBar.setOnSeekBarChangeListener(this);
//	}
//
//	private void initData() {
//		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//		mSettingsProxy = new SettingsProxy();
//		mVolumeManager = VolumeManager.getInstance(mContext);
//		mBtManager = BTManager.getInstance(mContext);
//		myWifiManager = MyWifiManager.getInstance(mContext);
//		mVolumeManager.setOnVolumeChangeListener(this);
//		mBtManager.setOnBTStateChangeListener(this);
//		myWifiManager.setOnWifiStateChangeListener(this);
//		updateVolumeUI();
//		updateBtUI();
//		updateWifiUI();
//		updateLoudnessUI();
//	}
//
//	private void updateLoudnessUI() {
//		boolean loudEnabled = mSettingsProxy.isLoudEnabled();
//		if (loudEnabled) {
//			ivLoudness.setImageResource(R.drawable.loud_on_selector);
//			tvLoudness.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//		} else {
//			ivLoudness.setImageResource(R.drawable.loud_off_selector);
//			tvLoudness.setTextColor(mContext.getResources().getColorStateList(R.color.text_grey_white_selector));
//		}
//	}
//	
//	private void updateVolumeUI() {
//		updateMuteUI();
//		int volume = mVolumeManager.getCurrentVolume();
//		String vol = "";
//		if (volume > 9) {
//			vol = String.valueOf(volume);
//		} else {
//			vol = "0" + String.valueOf(volume);
//		}
//		volumeTv.setText(vol);
//		volumeBar.setMax(mVolumeManager.getCurrentMaxVolume());
//		volumeBar.setProgress(volume);
//	}
//
//	private void updateMuteUI() {
//		if (mVolumeManager.isVolumeMute()) {
//			ivMute.setImageResource(R.drawable.mute_on_selector);
//			tvMute.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//		} else {
//			ivMute.setImageResource(R.drawable.mute_off_selector);
//			tvMute.setTextColor(mContext.getResources().getColorStateList(R.color.text_grey_white_selector));
//		}
//	}
//	
//	private void updateBtUI() {
//		int enableState = mBtManager.getBtEnableState();
//		switch (enableState) {
//		case BTManager.STATE_ENABLE:
//			ivBluetooth.setImageResource(R.drawable.bt_on_selector);
//			tvBluetooth.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnBluetooth.setEnabled(true);
//			break;
//		case BTManager.STATE_DISABLE:
//			ivBluetooth.setImageResource(R.drawable.bt_off_selector);
//			tvBluetooth.setTextColor(mContext.getResources().getColorStateList(R.color.text_grey_white_selector));
//			btnBluetooth.setEnabled(true);
//			break;
//		case BTManager.STATE_ENABLING:
//			ivBluetooth.setImageResource(R.drawable.bt_disable);
//			tvBluetooth.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnBluetooth.setEnabled(false);
//			break;
//		case BTManager.STATE_DISABLING:
//			ivBluetooth.setImageResource(R.drawable.bt_disable);
//			tvBluetooth.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnBluetooth.setEnabled(false);
//			break;
//		default:
//			break;
//		}
//	}
//	
//	private void updateWifiUI() {
//		int enableState = myWifiManager.getWifiEnableState();
//		switch (enableState) {
//		case MyWifiManager.STATE_ENABLE:
//			ivWifi.setImageResource(R.drawable.wifi_on_selector);
//			tvWifi.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnWifi.setEnabled(true);
//			break;
//		case MyWifiManager.STATE_DISABLE:
//			ivWifi.setImageResource(R.drawable.wifi_off_selector);
//			tvWifi.setTextColor(mContext.getResources().getColorStateList(R.color.text_grey_white_selector));
//			btnWifi.setEnabled(true);
//			break;
//		case MyWifiManager.STATE_ENABLING:
//			ivWifi.setImageResource(R.drawable.wifi_disable);
//			tvWifi.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnWifi.setEnabled(false);
//			break;
//		case MyWifiManager.STATE_DISABLING:
//			ivWifi.setImageResource(R.drawable.wifi_disable);
//			tvWifi.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			btnWifi.setEnabled(false);
//			break;
//		default:
//			break;
//		}
//	}
//	
//	private void setLoudness(boolean isOn) {
//		mSettingsProxy.enableLoud(isOn);
//	}
//	
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_colse_screen:
//
//			break;
//		case R.id.btn_mute:
//			if (!isBtCall) {
//				boolean isMute = mVolumeManager.isVolumeMute();
//				mVolumeManager.setVolumeMute(!isMute);
//				updateVolumeUI();
//			}
//			
//			break;
//		case R.id.btn_bluetooth:
//			mBtManager.setBtEnable(!mBtManager.isBtEnbale());
//			break;
//		case R.id.btn_wifi:
//			myWifiManager.setWifiEnbale(!myWifiManager.isWifiEnable());
//			break;
//		case R.id.btn_loud:
//			boolean loudEnabled = mSettingsProxy.isLoudEnabled();
//			setLoudness(!loudEnabled);
//			if (!loudEnabled) {
//				ivLoudness.setImageResource(R.drawable.loud_on_selector);
//				tvLoudness.setTextColor(mContext.getResources().getColorStateList(R.color.text_white_grey_selector));
//			} else {
//				ivLoudness.setImageResource(R.drawable.loud_off_selector);
//				tvLoudness.setTextColor(mContext.getResources().getColorStateList(R.color.text_grey_white_selector));
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	@Override
//	public boolean onLongClick(View v) {
//		LogUtil.i(SUB_TAG, "onLongClick");
//		switch (v.getId()) {
//		case R.id.btn_bluetooth:
//			
//			break;
//		case R.id.btn_wifi:
//			openWifiSettingUI();
//			break;
//
//		default:
//			break;
//		}
//		return true;
//	}
//
//	private void openWifiSettingUI() {
//		try {
//			Intent intent = new Intent();
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			intent.setClassName("com.zhonghong.settings", "com.zhonghong.settings.activity.WifiActivity");
//			mContext.startActivity(intent);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**********BT**********/
//	
//	@Override
//	public void onBTEnableStateChanged(int enableState) {
//		updateBtUI();
//	}
//
//	@Override
//	public void onBTConnectStateChanged(boolean isConnect) {
//	}
//
//	@Override
//	public void onBTCallStateChanged(boolean isCalling) {
//		isBtCall = isCalling;
//		mVolumeManager.setBtCall(isBtCall);
//	}
//
//	@Override
//	public void onBTCallInfoChanged() {
//	}
//
//	/**********WIFI**********/
//	
//	@Override
//	public void onWifiEnableStateChanged(int enableState) {
//		updateWifiUI();
//	}
//
//	@Override
//	public void onWifiConnectChanged(boolean isConnect) {
//	}
//
//	@Override
//	public void onWifiLevelChanged(int level) {
//	}
//	
//	/**********音量**********/
//	
//	@Override
//	public void onVolumeChanged(boolean isByShelf) {
//		if (!isChangeVol) {
//			updateVolumeUI();
//		} else {
//			updateMuteUI();
//		}
//		updateVolumeBarValue();
//		if (!isByShelf && !isChangeVol) {
//			showVolumeBar();
//		}
//	}
//	
//	@Override
//	public void onProgressChanged(SeekBar seekBar, int progress,
//			boolean fromUser) {
//		if (fromUser) {
//			if (isBtCall) {
//				if (progress < 1) {
//					volumeBar.setProgress(1);
//				} else {
//					mVolumeManager.setCurrentVolume(progress);
//				}
//			} else {
//				mVolumeManager.setCurrentVolume(progress);
//			}
//			String vol = "";
//			if (isBtCall && progress < 1) {
//				vol = "01";
//			} else {
//				if (progress > 9) {
//					vol = String.valueOf(progress);
//				} else {
//					vol = "0" + String.valueOf(progress);
//				}
//			}
//			volumeTv.setText(vol);
//		}
//	}
//
//	@Override
//	public void onStartTrackingTouch(SeekBar seekBar) {
//		isChangeVol = true;
//	}
//
//	@Override
//	public void onStopTrackingTouch(SeekBar seekBar) {
//		isChangeVol = false;
//	}
//	
//	private void updateVolumeBarValue() {
//		createVolumeBar();
//		if (isVolMute != mVolumeManager.isVolumeMute()) {
//			isVolMute = !isVolMute;
//			if (isVolMute) {
//				volumeIcon.setBackgroundResource(R.drawable.icon_window_volume_mute);
//			} else {
//				volumeIcon.setBackgroundResource(R.drawable.icon_window_volume);
//			}
//		}
//		volumeProgressBar.setMax(mVolumeManager.getCurrentMaxVolume());
//		int volume = mVolumeManager.getCurrentVolume();
//		volumeValueText.setText(volume < 10 ? ("0" + volume) : (volume + ""));
//		volumeProgressBar.setProgress(volume);
//	}
//	
//	private boolean isVolumeBarShow = false;
//	private void showVolumeBar() {
//		createVolumeBar();
//		if (!isVolumeBarShow) {
//			isVolumeBarShow = true;
//			mWindowManager.addView(volumeView, getVolumeParmas());
//			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_VOLUME_BAR, 3000);
//		} else {
//			mHandler.removeMessages(MSG_DISMISS_VOLUME_BAR);
//			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_VOLUME_BAR, 3000);
//		}
//	}
//	
//	private void dismissVolumeBar() {
//		if (isVolumeBarShow) {
//			isVolumeBarShow = false;
//			mWindowManager.removeView(volumeView);
//		}
//	}
//	
//	private View volumeView;
//	private TextView volumeValueText;
//	private ImageView volumeIcon;
//	private ProgressBar volumeProgressBar;
//	private boolean isVolMute = false;
//	private void createVolumeBar() {
//		if (volumeView == null) {
//			LogUtil.i(SUB_TAG, "createVolumeBar");
//			volumeView = LayoutInflater.from(mContext).inflate(R.layout.window_volume_layout, null);
//			volumeValueText = (TextView) volumeView.findViewById(R.id.window_volume_value);
//			volumeIcon = (ImageView) volumeView.findViewById(R.id.window_volume_icon);
//			volumeProgressBar = (ProgressBar) volumeView.findViewById(R.id.window_volume_bar);
//			volumeView.findViewById(R.id.window_volume_bar_layout).setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					LogUtil.i(SUB_TAG, "onClick outside");
//					mHandler.removeMessages(MSG_DISMISS_VOLUME_BAR);
//					dismissVolumeBar();
//				}
//			});
//		}
//	}
//	
//	private WindowManager.LayoutParams getVolumeParmas() {
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
//		params.format = PixelFormat.TRANSLUCENT;
//		params.x = 0;
//		params.y = 0;
//		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN
//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//		return params;
//	}
//	
//	private static final int MSG_DISMISS_VOLUME_BAR = 0X01;
//	private Handler mHandler = new Handler(new Handler.Callback() {
//		
//		@Override
//		public boolean handleMessage(Message msg) {
//
//			switch (msg.what) {
//			case MSG_DISMISS_VOLUME_BAR:
//				dismissVolumeBar();
//				break;
//			default:
//				break;
//			}
//		
//			return false;
//		}
//	});
}
