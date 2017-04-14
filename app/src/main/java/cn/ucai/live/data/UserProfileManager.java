package cn.ucai.live.data;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.io.IOException;

import cn.ucai.live.I;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.data.restapi.ApiManager;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.Result;
import cn.ucai.live.utils.ResultUtils;

public class UserProfileManager {
	private static final String TAG = UserProfileManager.class.getSimpleName();
	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	private User currentAppUser;
	IUserModel userModel;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		appContext = context;
		sdkInited = true;
		userModel = new UserModel();
		return true;
	}

	//重置方法，清空内存和sf里面的数据
	public synchronized void reset() {
		currentAppUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}

	public synchronized User getCurrentAppUserInfo() {
		L.e(TAG, "getCurrentAppUserInfo,currentAppUser=" + currentAppUser);
		if (currentAppUser == null || currentAppUser.getMUserName() == null) {
			String username = EMClient.getInstance().getCurrentUser();
			currentAppUser = new User(username);
			String nick = getCurrentUserNick();
			currentAppUser.setMUserNick((nick != null) ? nick : username);

		}
		return currentAppUser;
	}

	public boolean updateCurrentUserNickName(final String nickname) {
		userModel.updateUserNick(appContext, EMClient.getInstance().getCurrentUser(), nickname,
				new OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				boolean updatenick = false;
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							updatenick = true;
							setCurrentAppUserNick(user.getMUserNick());
						}
					}
				}
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK)
						.putExtra(I.User.NICK, updatenick));
			}

			@Override
			public void onError(String error) {
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK)
						.putExtra(I.User.NICK, false));
			}
		});
		return false;
	}

	public void uploadUserAvatar(final File file) {
		userModel.uploadAvatar(appContext, EMClient.getInstance().getCurrentUser(), file, new
				OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				boolean success = false;
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							success = true;
							setCurrentAppUserAvatar(user.getAvatar());
						}
					}
				}
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
						.putExtra(I.Avatar.UPDATE_TIME, success));
			}

			@Override
			public void onError(String error) {
				appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
						.putExtra(I.Avatar.UPDATE_TIME, file));
			}
		});
//		String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
//		if (avatarUrl != null) {
//			setCurrentUserAvatar(avatarUrl);
//		}
//		return avatarUrl;
	}

	public void updateCurrentAppUserInfo(User user) {
		currentAppUser = user;
		setCurrentAppUserNick(user.getMUserNick());
		setCurrentAppUserAvatar(user.getAvatar());
	}

	public void asyncGetCurrentAppUserInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					User user = ApiManager.get().loadUserInfo(EMClient.getInstance().getCurrentUser());
					if (user != null) {
						updateCurrentAppUserInfo(user);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
//		userModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(),
//				new OnCompleteListener<String>() {
//			@Override
//			public void onSuccess(String s) {
//				if (s != null) {
//					Result result = ResultUtils.getResultFromJson(s, User.class);
//					if (result != null && result.isRetMsg()) {
//						User user = (User) result.getRetData();
////						L.e(TAG, "asyncGetCurrentAppUserInfo,user=" + user);
//						if (user != null) {
//							updateCurrentAppUserInfo(user);
//						}
//					}
//				}
//			}
//
//			@Override
//			public void onError(String error) {
//
//			}
//		});
	}

	private void setCurrentAppUserNick(String nickname) {
		getCurrentAppUserInfo().setMUserNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentAppUserAvatar(String avatar) {
		L.e(TAG, "setCurrentAppUserAvatar,avatar=" + avatar);
		getCurrentAppUserInfo().setAvatar(avatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
	}

	private String getCurrentUserNick() {
		return PreferenceManager.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return PreferenceManager.getInstance().getCurrentUserAvatar();
	}
}
