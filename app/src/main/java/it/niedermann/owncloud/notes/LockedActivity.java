/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2020-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2020-2021 Stefan Niedermann <info@niedermann.it>
 * SPDX-FileCopyrightText: 2020 Christoph Loy <loy.christoph@gmail.com>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import it.niedermann.owncloud.notes.branding.BrandedActivity;
import it.niedermann.owncloud.notes.exception.ExceptionHandler;

public abstract class LockedActivity extends BrandedActivity {

    private static final String TAG = LockedActivity.class.getSimpleName();

    private static final int REQUEST_CODE_UNLOCK = 100;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置未捕获异常处理器
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));//
        // 检查用户偏好设置，防止屏幕截图
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_prevent_screen_capture), false)) {
            // 设置安全标志，防止屏幕捕获
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        if (isTaskRoot()) {
            askToUnlock();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isTaskRoot()) {
            askToUnlock();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTaskRoot()) {
            NotesApplication.updateLastInteraction();//更新交互时间（活动时）
        }
    }

    @Override
    public void onBackPressed() {//按下后退时更新交互时间
        super.onBackPressed();
        NotesApplication.updateLastInteraction();
    }

    @Override
    //启用新活动更新交互时间
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        NotesApplication.updateLastInteraction();
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        NotesApplication.updateLastInteraction();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent) {
        NotesApplication.updateLastInteraction();
        super.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        NotesApplication.updateLastInteraction();
        super.startActivity(intent, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//解锁请求的结果处理
        if (requestCode == REQUEST_CODE_UNLOCK) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Successfully unlocked device");
                NotesApplication.unlock();
            } else {
                Log.e(TAG, "Result code of unlocking was " + resultCode);
                finish();
            }
        }
    }
//解锁方法提示
    private void askToUnlock() {
        if (NotesApplication.isLocked()) {
            final var keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(getString(R.string.unlock_notes), null);
                if (intent != null) { // 确保意图不为空
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//添加了对intent的检测，否则上述创建的intent有可能因为设备或权限问题返回null
                    startActivityForResult(intent, REQUEST_CODE_UNLOCK);
                } else {
                    Log.e(TAG, "Unlock intent is null");
                }
            } else {
                Log.e(TAG, "Keyguard manager is null");
            }
        }
    }
