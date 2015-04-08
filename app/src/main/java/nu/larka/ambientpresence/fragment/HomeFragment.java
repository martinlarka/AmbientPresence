package nu.larka.ambientpresence.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nu.larka.ambientpresence.MainActivity;
import nu.larka.ambientpresence.R;
import nu.larka.ambientpresence.adapter.DeviceAdapter;
import nu.larka.ambientpresence.model.Device;
import nu.larka.ambientpresence.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ValueEventListener, View.OnLongClickListener {

    private User homeUser = null;
    private Uri imageUri;
    private ImageView userImageView;
    private TextView titleView;
    private ListView deviceListView;
    private DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArrayList = new ArrayList<>();

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Firebase mFirebaseRef;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        userImageView = (ImageView) v.findViewById(R.id.home_image_view);
        titleView = (TextView) v.findViewById(R.id.home_name);
        deviceListView = (ListView) v.findViewById(R.id.device_list_view);

        if (homeUser != null) {
            titleView.setText(getResources().getString(R.string.office_home) + " - " + homeUser.getName());

            if (!homeUser.hasImage()) {
                userImageView.setImageResource(R.drawable.home500);
            } else {
                userImageView.setImageBitmap(homeUser.getImage());
            }
        } else {
            mFirebaseRef.addListenerForSingleValueEvent(this);
        }
        userImageView.setOnLongClickListener(this);
        // Inflate the layout for this fragment
        deviceAdapter = new DeviceAdapter(v.getContext(), deviceArrayList);
        deviceListView.setAdapter(deviceAdapter);
        deviceListView.setOnItemClickListener(deviceClickListener);

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

                        homeUser.setImage(bitmap);
                        userImageView.setImageBitmap(bitmap);
                        new UploadImageToFirebase().execute(bitmap);

                    } catch (IOException e) {
                    }
                }
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        homeUser = new User(dataSnapshot.getKey());
        homeUser.setName((String)dataSnapshot.child(MainActivity.NAME).getValue());

        titleView.setText(getResources().getString(R.string.office_home) + " - " + homeUser.getName());

        String str = (String) dataSnapshot.child(MainActivity.USER_IMAGE).getValue();
        if (str != null) {
            byte[] imageAsBytes = com.firebase.tubesock.Base64.decode(str.getBytes());
            Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            homeUser.setImage(bmp);
            userImageView.setImageBitmap(bmp);
        } else {
            userImageView.setImageResource(R.drawable.home500);
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public boolean onLongClick(View v) {
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private class UploadImageToFirebase extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bmps) {
            Bitmap bmp;
            bmp = bmps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            bmp.recycle();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            mFirebaseRef.child(MainActivity.USER_IMAGE).setValue(imageFile);
            return null;
        }
    }

    private static void ensurePhotoNotRotated(Context context, Uri imgUri) {
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
            bmp = Bitmap.createScaledBitmap(bmp,400, 400*bmp.getHeight()/bmp.getWidth(), false);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

            OutputStream os;
            try {
                os = context.getContentResolver().openOutputStream(imgUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            bmp.compress(Bitmap.CompressFormat.PNG, 50, os);

            try {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFirebaseRef(Firebase firebaseRef) {
        this.mFirebaseRef = firebaseRef;
    }

    private AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == deviceArrayList.size()) {
                // Add device

            } else {
                // Setup device
                
            }
        }
    };
}
