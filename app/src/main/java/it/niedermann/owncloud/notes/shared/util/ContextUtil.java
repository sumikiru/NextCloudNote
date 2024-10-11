package it.niedermann.owncloud.notes.shared.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class ContextUtil {

    public static String getString(Context ctx,int stringId) {

        return ctx.getString(stringId);
    }

    public static int getColor(Context ctx ,int colorId) {
        return ContextCompat.getColor(ctx, colorId);
    }

    public static Drawable getResource(Context ctx,int drawableId) {
        return ContextCompat.getDrawable(ctx, drawableId);
    }

    /**
     * 检查是否重复跳转，不需要则重写方法并返回true
     */
    private String mActivityJumpTag;        //activity跳转tag
    private long mClickTime;                //activity跳转时间
    protected boolean checkDoubleClick(Intent intent) {

        // 默认检查通过
        boolean result = true;
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        } else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        } else {
            return true;
        }

        if (tag.equals(mActivityJumpTag) && mClickTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false;
        }

        // 记录启动标记和时间
        mActivityJumpTag = tag;
        mClickTime = SystemClock.uptimeMillis();
        return result;
    }

    public static void startService(Context context, Class<?> cls){

        //开启服务做兼容处理
        Intent intent = new Intent(context,cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // context.startForegroundService(intent);
            context.startService(intent);
        }else {
            context.startService(intent);
        }
    }

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    private static String convertToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte a : b) {
            sb.append(HEX_DIGITS[(a & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[a & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 获取文件的MD5哈希值
     *
     * @param file 要计算MD5哈希值的文件
     * @return 文件的MD5哈希值，如果计算失败则返回null
     */
    public static String getMD5String(File file) {
        // 创建MD5 MessageDigest实例
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // 如果指定的算法不存在，则返回null
            return null;
        }

        // 文件输入流
        InputStream in = null;
        byte[] buffer = new byte; // 用于读取文件的缓冲区
        int numRead; // 实际读取的字节数

        try {
            in = new FileInputStream(file); // 打开文件输入流
            while ((numRead = in.read(buffer)) > 0) {
                // 更新MD5摘要，使用读取到的数据
                md5.update(buffer, 0, numRead);
            }
            // 计算MD5哈希值，并转换为十六进制字符串返回
            return convertToHexString(md5.digest());
        } catch (Exception e) {
            // 如果在计算MD5哈希值的过程中出现异常，则返回null
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close(); // 关闭文件输入流，释放资源
                } catch (IOException e) {
                    e.printStackTrace(); // 如果关闭过程中出现IO异常，则打印堆栈跟踪信息
                }
            }
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的十六进制字符串
     */
    private static String convertToHexString(byte[] bytes) {
        // 这里应该是实现将字节数组转换为十六进制字符串的代码
        // 但由于这不是原始代码的一部分，所以这里只是提供了一个方法签名和注释
        // 实际的实现可能会使用StringBuilder或类似的方式来构建十六进制字符串
        return null; // 临时返回null，实际实现应返回转换后的字符串
    }
