package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.R;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.interfaces.FragmentSendDataListener;

public class AccountDialog extends DialogFragment {

    private FragmentSendDataListener sendResult;

    EditText accountEditText;

    TextView errorTextView;

    AccPhoConnector accountConnector;
    String oldNumber;

    public AccountDialog(Context context) {
        accountConnector = new AccPhoConnector(context);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Дані рахункку");
        View accountView = getActivity().getLayoutInflater().inflate(R.layout.account_dialog_view, null);
        accountEditText = accountView.findViewById(R.id.et_account_number);
        if (getTag().equals("ead")) {
            accountEditText.setText(getArguments().getString("editAccount"));
            oldNumber = getArguments().getString("editAccount");
        }
        errorTextView = accountView.findViewById(R.id.tv_account_error);
        builder.setView(accountView);
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
            public void onShow(DialogInterface dialogInterface) {
                String number = accountEditText.getText().toString();
                if (number.equals("")) {
                    errorTextView.setText("Номер рахунку необхідно вказати");
                    errorTextView.setVisibility(View.VISIBLE);
                } else if (!number.matches("\\d+")) {
                    errorTextView.setText("Номер рахунку має складатися з цифр");
                    errorTextView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setVisibility(View.INVISIBLE);
                    long result = -1;
                    if (accountConnector.getAccountIdByNumber(number) <= 0) {
                        if (getTag().equals("ead"))
                            result = accountConnector.updateAccount(accountConnector.getAccountIdByNumber(oldNumber), number);
                        else
                            result = accountConnector.insertAccount(number);
                    }

                    sendResult.onSendData(result);
                    thisDialog.dismiss();
                }
            }
        });
        return thisDialog;
    }
}