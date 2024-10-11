/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2020-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2020-2021 Stefan Niedermann <info@niedermann.it>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.LiveData;

import it.niedermann.owncloud.notes.main.MainActivity;
import it.niedermann.owncloud.notes.persistence.entity.Note;
import it.niedermann.owncloud.notes.shared.util.ShareUtil;

public class AppendToNoteActivity extends MainActivity {

    private static final String TAG = AppendToNoteActivity.class.getSimpleName();

    String receivedText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receivedText = ShareUtil.extractSharedText(getIntent()); //获取共享文本
        @Nullable final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(R.string.append_to_note);
        } else {
            Log.e(TAG, "SupportActionBar is null. Expected toolbar to be present to set a title.");
        }
        binding.activityNotesListView.searchToolbar.setSubtitle(receivedText);
    }

    @Override
    public void onNoteClick(int position, View v) {
        if (!TextUtils.isEmpty(receivedText)) {//检查获取的文本是否为空
            final var fullNote$ = mainViewModel.getFullNote$(((Note) adapter.getItem(position)).getId());//观察完整笔记数据的变化
            fullNote$.observe(this, (fullNote) -> {
                fullNote$.removeObservers(this);//有数据则移除观察防止内存泄漏
                final String oldContent = fullNote.getContent();//获取内容
                String newContent;
                //原内容不为空则添加，为空则直接使用新内容
                if (!TextUtils.isEmpty(oldContent)) {
                    newContent = oldContent + "\n\n" + receivedText;
                } else {
                    newContent = receivedText;
                }
                final var updateLiveData = mainViewModel.updateNoteAndSync(fullNote, newContent, null);//观察更新结果
                updateLiveData.observe(this, (next) -> {
                    Toast.makeText(this, getString(R.string.added_content, receivedText), Toast.LENGTH_SHORT).show()
                    updateLiveData.removeObservers(this);
                });
            });
        } else {
            //文本为空发出提示
            Toast.makeText(this, R.string.shared_text_empty, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
