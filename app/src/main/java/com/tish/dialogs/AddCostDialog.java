package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.tish.db.bases.Category;
import com.tish.db.bases.PhotoManager;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.db.connectors.CostConnector;
import com.tish.models.Cost;
import com.tish.interfaces.FragmentSendDataListener;
import com.tish.models.Geolocation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddCostDialog extends DialogFragment
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private FragmentSendDataListener sendInsertResult;

    EditText amountEditText;
    EditText dateEditText;
    EditText marketNameEditText;
    EditText cityEditText;
    EditText addressEditText;

    CheckBox addGeoCheckBox;

    Spinner categorySpinner;
    Spinner accountSpinner;

    ImageButton makePhotoImageButton;

    TextView errorTextView;

    CostConnector costConnector;
    PhotoManager photoManager;

    String dateRegexDayYear;
    String dateRegexYearDay;

    Context context;

    boolean canBeSaved = true;
    String photoAddress;

    public AddCostDialog(Context context) {
        this.context = context;
        costConnector = new CostConnector(context);
        photoManager = new PhotoManager(context);
        dateRegexDayYear = "[0-3][0-9][-./][0-1][0-9][-./][0-9]{4}";
        dateRegexYearDay = "[0-9]{4}[-./][0-1][0-9][-./][0-3][0-9]";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendInsertResult = (FragmentSendDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Введіть дані витрати");
        View addCostView = getActivity().getLayoutInflater().inflate(R.layout.add_cost_dialog_view, null);
        amountEditText = addCostView.findViewById(R.id.et_cost_amount);
        dateEditText = addCostView.findViewById(R.id.et_cost_date);
        dateEditText.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        marketNameEditText = addCostView.findViewById(R.id.et_cost_market);
        addGeoCheckBox = addCostView.findViewById(R.id.chb_cost_add_geo);
        addGeoCheckBox.setOnCheckedChangeListener(this);
        cityEditText = addCostView.findViewById(R.id.et_cost_geo_city);
        addressEditText = addCostView.findViewById(R.id.et_cost_geo_address);
        categorySpinner = addCostView.findViewById(R.id.spinner_cost_category);
        accountSpinner = addCostView.findViewById(R.id.spinner_cost_account);
        fillSpinners();
        makePhotoImageButton = addCostView.findViewById(R.id.ib_make_photo);
        makePhotoImageButton.setOnClickListener(this);
        errorTextView = addCostView.findViewById(R.id.tv_cost_error);
        builder.setView(addCostView);
        builder.setPositiveButton("Зберегти", null);
        builder.setNegativeButton("Відмінити", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog thisDialog = builder.create();
        thisDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button saveButton = thisDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cost newCost = new Cost();
                        String amount = amountEditText.getText().toString();
                        String date = dateEditText.getText().toString();
                        if (amount.equals("")) {
                            errorTextView.setText("Введіть сумму витрати");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (date.equals("")) {
                            errorTextView.setText("Введіть дату витрати або - для використання поточної");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (!date.matches(dateRegexDayYear) && !date.matches(dateRegexYearDay) && !date.equals("-")) {
                            errorTextView.setText("Формат дати помилковий");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else {
                            errorTextView.setVisibility(View.INVISIBLE);

                            double costAmount = Double.parseDouble(amount);
                            newCost.setAmount(costAmount);
                            if (date.equals("-"))
                                newCost.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            else {
                                date = date.replaceAll("[./]", "-");
                                if (date.matches(dateRegexDayYear)) {
                                    date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();
                                }
                                newCost.setDate(date);
                            }
                            newCost.setCategory(Category.values()[categorySpinner.getSelectedItemPosition()]);
                            if (!marketNameEditText.getText().toString().equals(""))
                                newCost.setMarketName(marketNameEditText.getText().toString());
                            else
                                newCost.setMarketName(null);
                            if (accountSpinner.getSelectedItemPosition() == 0)
                                newCost.setAccountNumber(null);
                            else
                                newCost.setAccountNumber(accountSpinner.getSelectedItem().toString());
                            if (addGeoCheckBox.isChecked()) {
                                Geolocation geo = new Geolocation();
                                String city = cityEditText.getText().toString();
                                String address = addressEditText.getText().toString();
                                if (city.equals("") && address.equals("")) {
                                    errorTextView.setText("Необхідно вказати місто та адресу");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    canBeSaved = false;
                                } else if (city.equals("")) {
                                    errorTextView.setText("Необхідно вказати місто");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    canBeSaved = false;
                                } else if (address.equals("")) {
                                    errorTextView.setText("Необхідно вказати адресу");
                                    errorTextView.setVisibility(View.VISIBLE);
                                    canBeSaved = false;
                                } else {
                                    errorTextView.setVisibility(View.INVISIBLE);
                                    Locale locale = new Locale("ukr", "UA");
                                    Geocoder geocoder = new Geocoder(context, locale);
                                    List<Address> ads;
                                    try {
                                        ads = geocoder.getFromLocationName(address + " " + city, 1);
                                        if (ads.size() > 0) {
                                            geo.setLongitude(ads.get(0).getLongitude());
                                            geo.setLatitude(ads.get(0).getLatitude());
                                            geo.setCountry(ads.get(0).getCountryName());
                                            geo.setCity(city);
                                            geo.setAddress(address);
                                            newCost.setGeo(geo);
                                            canBeSaved = true;
                                        } else {
                                            errorTextView.setText("Перевірте адресу та місто");
                                            errorTextView.setVisibility(View.VISIBLE);
                                            canBeSaved = false;
                                        }
                                    } catch (IOException e) {
                                        Toast.makeText(context, "Обробка адреси неможлива. Спробуйте пізніше", Toast.LENGTH_SHORT).show();
                                        canBeSaved = true;
                                    }
                                }
                            } else {
                                newCost.setGeo(null);
                                canBeSaved = true;
                            }
                            //describe photo adding
                            newCost.setPhotoAddress(photoAddress);

                            if (canBeSaved) {
                                long insertResult = costConnector.insertNewCost(newCost);
                                sendInsertResult.onSendData(insertResult, "TAG_COSTS_FRAGMENT");
                                thisDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        return thisDialog;
    }

    private void fillSpinners() {
        List<String> categoryList = new ArrayList<>();
        for (Category c : Category.values()) {
            categoryList.add(c.getCategoryName());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(categoryList.size() - 1);

        AccPhoConnector accPhoConnector = new AccPhoConnector(context);
        List<String> accountList = accPhoConnector.getAccounts();
        accountList.add(0, "Без рахунку");
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, accountList);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountAdapter);
        accountSpinner.setSelection(0);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            cityEditText.setVisibility(View.VISIBLE);
            addressEditText.setVisibility(View.VISIBLE);
        } else {
            cityEditText.setVisibility(View.INVISIBLE);
            addressEditText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        myCameraRegister.launch(intent);
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
                                makePhotoImageButton.setEnabled(false);
                            }
                        } else {
                            Toast.makeText(getContext(), "Problem", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
}
