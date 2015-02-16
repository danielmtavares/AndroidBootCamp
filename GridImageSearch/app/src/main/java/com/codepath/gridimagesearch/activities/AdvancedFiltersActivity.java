package com.codepath.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.helpers.SearchFilter;


public class AdvancedFiltersActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_filters);

        Bundle extras = getIntent().getExtras();

        Spinner spImageSizeFilter = (Spinner) findViewById(R.id.spImageSizeFilter);
        setSpinnerToValue(spImageSizeFilter, extras.getString(SearchFilter.IMAGE_SIZE));

        Spinner spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        setSpinnerToValue(spColorFilter, extras.getString(SearchFilter.COLOR));

        Spinner spImageTypeFilter = (Spinner) findViewById(R.id.spImageTypeFilter);
        setSpinnerToValue(spImageTypeFilter, extras.getString(SearchFilter.IMAGE_TYPE));

        EditText etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
        etSiteFilter.setText(extras.getString(SearchFilter.SITE));
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    public void onSubmit(View view) {
        Intent data = new Intent();

        Spinner spImageSizeFilter = (Spinner) findViewById(R.id.spImageSizeFilter);
        String imageSize = "";
        if (spImageSizeFilter.getSelectedItemPosition() != 0) {
            imageSize = spImageSizeFilter.getSelectedItem().toString();
        }
        data.putExtra(SearchFilter.IMAGE_SIZE, imageSize);

        Spinner spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        String colorFilter = "";
        if (spColorFilter.getSelectedItemPosition() != 0) {
            colorFilter = spColorFilter.getSelectedItem().toString();
        }
        data.putExtra(SearchFilter.COLOR, colorFilter);

        Spinner spImageTypeFilter = (Spinner) findViewById(R.id.spImageTypeFilter);
        String imageType = "";
        if (spImageTypeFilter.getSelectedItemPosition() != 0) {
            imageType = spImageTypeFilter.getSelectedItem().toString();
        }
        data.putExtra(SearchFilter.IMAGE_TYPE, imageType);

        EditText etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
        data.putExtra(SearchFilter.SITE, etSiteFilter.getText().toString());

        setResult(RESULT_OK, data);
        finish();
    }
}
