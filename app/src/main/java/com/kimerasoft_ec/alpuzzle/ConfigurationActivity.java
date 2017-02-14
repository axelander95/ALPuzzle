package com.kimerasoft_ec.alpuzzle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ConfigurationActivity extends AppCompatActivity {
    public static final int LEVEL_BASIC = 0;
    public static final int LEVEL_MEDIUM = 1;
    public static final int LEVEL_ADVANCED = 2;
    private EditText etRows, etColumns;
    private Spinner spnLevel;
    private Button btnAccept, btnCancel;
    public static final int BASIC = 3;
    public static final int MEDIUM = 4;
    public static final int ADVANCED = 6;
    public static String LEVEL_PARAM = "LEVEL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        initialize();
        fillLevels();
        setEvents();
    }
    private void initialize()
    {
        etRows = (EditText) findViewById(R.id.etRows);
        etColumns = (EditText) findViewById(R.id.etColumns);
        spnLevel = (Spinner) findViewById(R.id.spnLevel);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }
    private void fillLevels()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.levels));
        spnLevel.setAdapter(adapter);
    }
    private void setEvents()
    {
        spnLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String number = String.valueOf(((position == LEVEL_BASIC)?BASIC:(position == LEVEL_MEDIUM)?MEDIUM:ADVANCED));
                etColumns.setText(number);
                etRows.setText(number);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(LEVEL_PARAM, spnLevel.getSelectedItemPosition());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
