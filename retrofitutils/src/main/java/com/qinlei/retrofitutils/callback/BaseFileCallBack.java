package com.qinlei.retrofitutils.callback;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * @author cyc
 * @date 18/8/15
 */
public abstract class BaseFileCallBack extends BaseCallback<File> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;


    protected BaseFileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public File parseNetworkResponse(Response response) throws IOException {
        return saveFile(response);
    }

    private File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[4096];
        int len = 0;
        FileOutputStream fos = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                is = ((ResponseBody) Objects.requireNonNull(response.body())).byteStream();
            }
            long total = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                total = ((ResponseBody) Objects.requireNonNull(response.body())).contentLength();
            }

            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;

                inProgress(finalSum * 1.0f / total, total);
            }
            fos.flush();

            return file;

        } finally {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    ((ResponseBody) Objects.requireNonNull(response.body())).close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ignored) {
            }

        }
    }


}
