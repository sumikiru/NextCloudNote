/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2018-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.quicksettings;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import it.niedermann.owncloud.notes.edit.EditNoteActivity;

/**
 * This {@link TileService} adds a quick settings tile that leads to the new note view.
 */
public class NewNoteTileService extends TileService {

    @Override
    public void onStartListening() {
        final var tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        promptForNoteTitle();
        // create new note intent
        final var newNoteIntent = new Intent(getApplicationContext(), EditNoteActivity.class);
        // ensure it won't open twice if already running
        newNoteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // ask to unlock the screen if locked, then start new note intent
        unlockAndRun(() -> startActivityAndCollapse(newNoteIntent));
    }
}

//在快速设置中可以选择标题
@Override
public void onClick() {
    promptForNoteTitle();
}

private void promptForNoteTitle() {
    // 创建一个 EditText 用于用户输入标题
    EditText inputField = new EditText(this);

    // 设置对话框
    new AlertDialog.Builder(this)
            .setTitle("输入笔记标题")
            .setView(inputField) // 将 EditText 添加到对话框
            .setPositiveButton("确定", (dialog, which) -> {
                String noteTitle = inputField.getText().toString().trim(); // 获取用户输入的标题
                if (!noteTitle.isEmpty()) { // 检查标题是否为空
                launchEditNoteActivity(noteTitle);} // 启动编辑笔记活动
            }
             else {
        Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show(); // 显示提示信息
    })
            .setNegativeButton("取消", null) // 如果用户选择取消，关闭对话框
            .show();
}

private void launchEditNoteActivity(String title) {
    Intent newNoteIntent = new Intent(getApplicationContext(), EditNoteActivity.class);
    newNoteIntent.putExtra("NOTE_TITLE", title); // 将标题传递给活动
    newNoteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

    // 启动编辑活动
    startActivity(newNoteIntent);
}


//在快速设置时可以快捷选择优先级
private void promptForNotePriority() {
    String[] priorities = {"高", "中", "低"};

    new AlertDialog.Builder(this)
            .setTitle("选择笔记优先级")
            .setItems(priorities, (dialog, which) -> {
                String selectedPriority = priorities[which];
                // 这里用监视器将选择的优先级与标题一起传递
                launchEditNoteActivity(selectedTitle, selectedPriority);
            })
            .show();
}

//快速创建完成后发送确认通知
private void showConfirmationNotification(String title) {
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    if (notificationManager == null) {
        return; // 确保 notificationManager 不为 null
    }
    String channelId = "note_channel";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(channelId, "Notes", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    Notification notification = new NotificationCompat.Builder(this, channelId)
            .setContentTitle("新笔记已创建")
            .setContentText("标题: " + title)
            .setSmallIcon(R.drawable.ic_note)
            .setAutoCancel(true) // 点击后自动消失
            .build();

    notificationManager.notify(1, notification);
}


