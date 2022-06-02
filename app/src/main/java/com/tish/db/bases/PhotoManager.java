package com.tish.db.bases;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoManager {

    private final String FILE_TYPE = ".jpeg";

    private static boolean directoryExists = false;

    private File path;
    Context context;

    public PhotoManager(Context context) {
        this.context = context;
        path = new File(context.getFilesDir(), "Images");
        if (!directoryExists) {
            directoryExists = path.mkdirs();
        }
    }

    public String savePhoto(Bitmap imageBitmap) {
        File photoFile = new File(path, (System.currentTimeMillis() / 1000) + FILE_TYPE);
        try {
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            boolean saved = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (saved)
                Toast.makeText(context, "Фото збережено", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Проблема збереження", Toast.LENGTH_SHORT).show();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoFile.getPath();
    }

    public boolean deletePhoto(String photoAddress) {
        File photoFile = new File(photoAddress);
        return photoFile.delete();
    }
}
