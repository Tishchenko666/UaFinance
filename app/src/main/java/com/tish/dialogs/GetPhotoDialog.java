package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.MainActivity;
import com.tish.R;
import com.tish.db.bases.PhotoManager;
import com.tish.db.connectors.CostConnector;
import com.tish.interfaces.FragmentSendDataListener;

import java.io.File;

public class GetPhotoDialog extends DialogFragment {

    private FragmentSendDataListener sendResult;
    AlertDialog.Builder builder;

    CostConnector costConnector;
    PhotoManager photoManager;

    Context context;
    String photoAddress;
    int costId;

    public GetPhotoDialog(Context context, int costId) {
        this.context = context;
        costConnector = new CostConnector(context);
        photoManager = new PhotoManager(context);
        this.costId = costId;
    }

    public GetPhotoDialog(Context context, int costId, String photoAddress) {
        this.context = context;
        costConnector = new CostConnector(context);
        photoManager = new PhotoManager(context);
        this.costId = costId;
        this.photoAddress = photoAddress;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendResult = (FragmentSendDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        if (getTag().equals("apd"))
            addPhotoView();
        else
            showPhotoView();

        return builder.create();
    }

    private void showPhotoView() {
        builder.setTitle("Фото витрати");
        View showView = getActivity().getLayoutInflater().inflate(R.layout.show_photo_dialog_view, null);
        ImageView photoImageView = showView.findViewById(R.id.iv_show_photo);
        photoImageView.setImageURI(Uri.fromFile(new File(photoAddress)));
        builder.setView(showView);
        builder.setNegativeButton("Видалити", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean isDeleted = photoManager.deletePhoto(photoAddress);
                if (isDeleted) {
                    long result = costConnector.deletePhotoIdInCost(photoAddress, costId);
                    sendResult.onSendData(result, "TAG_COSTS_FRAGMENT");
                } else
                    Toast.makeText(context, "При видаленні сталась помилка", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Закрити", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void addPhotoView() {
        builder.setMessage("Додати фото до витрати?");
        builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                myCameraRegister.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> myCameraRegister = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MainActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.hasExtra("data")) {
                                Bitmap photoBitmap = data.getParcelableExtra("data");
                                photoAddress = photoManager.savePhoto(photoBitmap);
                                long updateResult = costConnector.updatePhotoInCost(photoAddress, costId);
                                Toast.makeText(context, "Фото збережено", Toast.LENGTH_SHORT).show();
                                sendResult.onSendData(updateResult, "TAG_COSTS_FRAGMENT");
                            }
                        } else {
                            Toast.makeText(getContext(), "Problem", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}
