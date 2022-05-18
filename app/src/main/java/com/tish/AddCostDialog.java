package com.tish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.db.connectors.CostConnector;

public class AddCostDialog extends DialogFragment {

    EditText amountEditText;
    EditText dateEditText;
    EditText marketNameEditText;
    EditText cityEditText;
    EditText addressEditText;

    Spinner categorySpinner;
    Spinner accountSpinner;

    TextView errorTextView;

    CostConnector costConnector;

    public AddCostDialog(Context context) {
        costConnector = new CostConnector(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Введіть дані витрати");
        View addCostView = getActivity().getLayoutInflater().inflate(R.layout.add_cost_dialog_view, null);
        //find views
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
                //actions
            }
        });
        return thisDialog;
    }
}
