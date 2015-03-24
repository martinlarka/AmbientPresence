package nu.larka.ambientpresence.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private String displayName;
    private Uri imageUri;
    private ImageView userImageView;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        TextView titleView = (TextView) v.findViewById(R.id.home_name);
        titleView.setText(displayName);

        userImageView = (ImageView) v.findViewById(R.id.home_image_view);
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload new image
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "AMBIENTPRESENCE_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File image = null;
                try {
                    image = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                    imageUri = Uri.fromFile(image);
                    Log.d("IMAGE absoulte", image.getAbsolutePath());
                    Log.d("IMAGE URI", imageUri.toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        Context context = getActivity().getApplicationContext();
                        ensurePhotoNotRotated(context, imageUri);
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        userImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                    }
                }
        }
    }

    public void setHomeName(String displayName) {
        this.displayName = displayName;
    }

    public static void ensurePhotoNotRotated(Context context, Uri imgUri) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(imgUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int angle = 0;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            angle = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
        }

        if (angle != 0) {
            InputStream is;
            try {
                is = context.getContentResolver().openInputStream(imgUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);

            Bitmap bmp = BitmapFactory.decodeStream(is);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

            OutputStream os;
            try {
                os = context.getContentResolver().openOutputStream(imgUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

            try {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
