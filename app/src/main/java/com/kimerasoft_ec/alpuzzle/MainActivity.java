package com.kimerasoft_ec.alpuzzle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE = 1;
    private GridView grvPuzzle;
    private static final int READ_PERMISSION = 2;
    public static Bitmap currentImage;
    private static final int imageSize = 640;
    private List<PuzzleItem> items;
    private PuzzleItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grvPuzzle = (GridView) findViewById(R.id.grvPuzzle);
        askFormPermissions();
    }

    private void askFormPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION);
            return;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showGalleryDialog()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, LOAD_IMAGE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.itmChange:
                if (currentImage == null)
                    showGalleryDialog();
                else
                {
                    currentImage = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.confirmation));
                    builder.setMessage(getResources().getString(R.string.current_image));
                    builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showGalleryDialog();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.itmView:
                if (currentImage != null)
                {
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_image),
                            Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.cannot_user_app),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case LOAD_IMAGE:
                    Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
                    startActivity(intent);
                    if (ConfigurationActivity.selectedItem != -1)
                    {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null && cursor.moveToFirst())
                        {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String image = cursor.getString(columnIndex);
                            cursor.close();
                            currentImage = BitmapFactory.decodeFile(image);
                            generatePuzzle(ConfigurationActivity.selectedItem);
                        }
                    }
                    else
                        currentImage = null;
                    break;
            }
        }
    }
    private void generatePuzzle(int n)
    {
        int parts = ((n == ConfigurationActivity.LEVEL_BASIC)?ConfigurationActivity.BASIC:(n == ConfigurationActivity.LEVEL_MEDIUM)?ConfigurationActivity.MEDIUM:ConfigurationActivity.ADVANCED);
        Bitmap bitmap = Bitmap.createScaledBitmap(currentImage, grvPuzzle.get, imageSize, true);
        grvPuzzle.setNumColumns(parts);
        if (items == null)
        {
            items = new ArrayList<>();
            adapter = new PuzzleItemAdapter(getApplicationContext(), items);
            grvPuzzle.setAdapter(adapter);
        }
        else
            items.clear();
        int id = 0;
        int section = imageSize / parts;
        for (int i = 0; i < parts; i++)
            for (int j = 0; j < parts; j++)
            {
                Bitmap part = Bitmap.createBitmap(bitmap, section * j, section * i, section, section);
                PuzzleItem item = new PuzzleItem(i, j, part, id++);
                items.add(item);
            }
        adapter.notifyDataSetChanged();
    }
}
