package com.kimerasoft_ec.alpuzzle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_IMAGE = 1;
    private int level;
    private GridView grvPuzzle;
    private static final int READ_PERMISSION = 2;
    public static Bitmap currentImage, selectedImage;
    private int imageSize = 1024;
    private List<PuzzleItem> items;
    private PuzzleItemAdapter adapter;
    private static final int CONFIGURATION_CODE = 3;
    private PuzzleItem selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grvPuzzle = (GridView) findViewById(R.id.grvPuzzle);
        askFormPermissions();
        level = ConfigurationActivity.LEVEL_MEDIUM;
        currentImage = BitmapFactory.decodeResource(getResources(), R.drawable.algebra);
        generatePuzzle(level);
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
            case R.id.itmConfiguration:
                Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
                startActivityForResult(intent, CONFIGURATION_CODE);
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
            switch (requestCode) {
                case LOAD_IMAGE:
                    selectedItem = null;
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String image = cursor.getString(columnIndex);
                        cursor.close();
                        this.selectedImage = BitmapFactory.decodeFile(image);
                        Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
                        startActivityForResult(intent, CONFIGURATION_CODE);
                    } else
                        currentImage = null;
                    break;
                case CONFIGURATION_CODE:
                    if (currentImage != null) {
                        if (this.selectedImage != null)
                            currentImage = this.selectedImage;
                        level = data.getIntExtra(ConfigurationActivity.LEVEL_PARAM, 0);
                        generatePuzzle(level);
                    }
                    break;
            }
        }
        else if (requestCode == CONFIGURATION_CODE)
            this.selectedImage = null;
    }
    private void generatePuzzle(int n)
    {
        int parts = ((n == ConfigurationActivity.LEVEL_BASIC)?ConfigurationActivity.BASIC:(n == ConfigurationActivity.LEVEL_MEDIUM)?ConfigurationActivity.MEDIUM:ConfigurationActivity.ADVANCED);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageSize = size.x;
        Bitmap bitmap = Bitmap.createScaledBitmap(currentImage, imageSize, imageSize, true);
        grvPuzzle.setNumColumns(parts);
        if (items == null)
        {
            items = new ArrayList<>();
            adapter = new PuzzleItemAdapter(getApplicationContext(), items);
            grvPuzzle.setAdapter(adapter);
            grvPuzzle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectedItem == null || items.get(position).equals(selectedItem))
                        selectedItem = items.get(position);
                    else
                    {
                        PuzzleItem other = items.get(position);
                        interchangeItems(selectedItem, other);
                        adapter.notifyDataSetChanged();
                        selectedItem = null;
                        if (verify())
                            Toast.makeText(getApplicationContext(), getString(R.string.congratulations), Toast.LENGTH_LONG).show();
                    }
                }
            });
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
        reorderItems();
    }
    private boolean verify()
    {
        for (int i = 0; i < items.size(); i++)
            if (i != items.get(i).getId())
                return false;
        return true;
    }
    private void reorderItems()
    {
        int max = items.size();
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < max; i++)
        {
            int number;
            do
            {
                number = rand.nextInt(max);
            }
            while(numbers.contains(number));
            numbers.add(number);
            PuzzleItem item1 = items.get(i);
            PuzzleItem item2 = items.get(number);
            if (!item1.isInterchanged())
            {
                item1.setInterchanged(true);
                item2.setInterchanged(true);
                interchangeItems(item1, item2);
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void interchangeItems(PuzzleItem item1, PuzzleItem item2)
    {
        int idItem = item1.getId();
        Bitmap image = item1.getImage();
        int x = item1.getX();
        int y = item1.getY();
        item1.setId(item2.getId());
        item1.setImage(item2.getImage());
        item1.setX(item2.getX());
        item1.setY(item2.getY());
        item2.setId(idItem);
        item2.setImage(image);
        item2.setX(x);
        item2.setY(y);
    }
}
