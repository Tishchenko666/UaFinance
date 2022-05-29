package com.tish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tish.adapters.AccountListAdapter;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.dialogs.AccountDialog;

import java.util.List;

public class AccountManagerFragment extends Fragment {

    ListView accountListView;
    FloatingActionButton addAccountButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_manager, container, false);
        accountListView = view.findViewById(R.id.lv_accounts);
        addAccountButton = view.findViewById(R.id.fab_add_account);

        AccPhoConnector accountConnector = new AccPhoConnector(getContext());
        List<String> accountList = accountConnector.getAccounts();
        AccountListAdapter accountAdapter = new AccountListAdapter(getContext(), accountList, getActivity().getSupportFragmentManager());
        accountListView.setAdapter(accountAdapter);

        accountListView.setLongClickable(true);
        accountListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog deleteDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Видалити рахунок")
                        .setMessage("Ви певні, що бажаєте видалити рахунок " +
                                accountList.get(position) + "?")
                        .setPositiveButton("Так, видалити", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int result = accountConnector.deleteAccount(accountList.get(position));
                                if (result > 0) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .detach(AccountManagerFragment.this)
                                            .attach(AccountManagerFragment.this).commit();
                                    dialog.dismiss();
                                } else
                                    Toast.makeText(getContext(), "При видаленні сталась помилка", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                deleteDialog.show();
                return true;
            }
        });

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountDialog addAccountDialog = new AccountDialog(getContext());
                addAccountDialog.show(getActivity().getSupportFragmentManager(), "aad");
            }
        });
        return view;
    }
}
