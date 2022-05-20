package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.R;
import com.tish.db.connectors.CostConnector;
import com.tish.db.connectors.GeoConnector;
import com.tish.interfaces.FragmentSendDataListener;
import com.tish.models.Geolocation;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class EditGeoDialog extends DialogFragment {

    private FragmentSendDataListener sendUpdateResult;

    EditText cityEditText;
    EditText addressEditText;

    TextView countryTextView;
    TextView errorTextView;

    Geolocation editGeo;
    int costId;
    Context context;
    CostConnector costConnector;

    Locale locale = new Locale("ukr", "UA");
    Geocoder geocoder;
    List<Address> ads;

    boolean canBeSaved = true;


    public EditGeoDialog(Context context, Geolocation geo, int costId) {
        this.context = context;
        costConnector = new CostConnector(context);
        editGeo = geo;
        this.costId = costId;
        geocoder = new Geocoder(context, locale);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendUpdateResult = (FragmentSendDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Змініть геодані");
        View editGeoView = getActivity().getLayoutInflater().inflate(R.layout.edit_geo_dialog_view, null);
        cityEditText = editGeoView.findViewById(R.id.et_edit_geo_city);
        cityEditText.setText(editGeo.getCity());
        addressEditText = editGeoView.findViewById(R.id.et_edit_geo_address);
        addressEditText.setText(editGeo.getAddress());
        countryTextView = editGeoView.findViewById(R.id.tv_edit_geo_country);
        countryTextView.setText(editGeo.getCountry());
        errorTextView = editGeoView.findViewById(R.id.tv_edit_geo_error);
        builder.setView(editGeoView);
        builder.setPositiveButton("Змінити", null);
        builder.setNegativeButton("Відмінити", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog thisDialog = builder.create();
        thisDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button saveButton = thisDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String city = cityEditText.getText().toString();
                        String address = addressEditText.getText().toString();
                        if (city.equals("") && address.equals("")) {
                            errorTextView.setText("Необхідно вказати місто та адресу");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (city.equals("")) {
                            errorTextView.setText("Необхідно вказати місто");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (address.equals("")) {
                            errorTextView.setText("Необхідно вказати адресу");
                            errorTextView.setVisibility(View.VISIBLE);
                        } else {
                            errorTextView.setVisibility(View.INVISIBLE);
                            if ((!city.equals(editGeo.getCity()) || !address.equals(editGeo.getAddress()))
                                    || (!city.equals(editGeo.getCity()) && !address.equals(editGeo.getAddress()))) {
                                try {
                                    ads = geocoder.getFromLocationName(address + " " + city, 1);
                                    if (ads.size() > 0) {
                                        MathContext mathContext = new MathContext(5, RoundingMode.HALF_UP);
                                        editGeo.setLongitude(new BigDecimal(ads.get(0).getLongitude(), mathContext).doubleValue());
                                        editGeo.setLatitude(new BigDecimal(ads.get(0).getLatitude(), mathContext).doubleValue());
                                        editGeo.setCountry(ads.get(0).getCountryName());
                                        editGeo.setCity(city);
                                        editGeo.setAddress(address);
                                        canBeSaved = true;
                                    } else {
                                        errorTextView.setText("У цьому місті немає такої адреси");
                                        errorTextView.setVisibility(View.VISIBLE);
                                        canBeSaved = false;
                                    }
                                } catch (IOException e) {
                                    Toast.makeText(context, "Обробка адреси неможлива. Спробуйте пізніше", Toast.LENGTH_SHORT).show();
                                    canBeSaved = true;
                                }
                            } else if (city.equals(editGeo.getCity()) && address.equals(editGeo.getAddress())) {
                                thisDialog.dismiss();
                            }

                            if (canBeSaved) {
                                int updateResult = costConnector.updateGeoInCost(editGeo, costId);
                                sendUpdateResult.onSendData(updateResult);
                                thisDialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        return thisDialog;
    }
}