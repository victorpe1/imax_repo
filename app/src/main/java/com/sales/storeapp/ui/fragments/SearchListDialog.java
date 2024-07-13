package com.sales.storeapp.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sales.storeapp.R;
import com.sales.storeapp.ui.adapters.SimpleRecyclerViewAdapter;

import java.util.ArrayList;

public class SearchListDialog extends AppCompatDialogFragment {
    public static String ARGUMENT_LIST = "ARGUMENT_LIST";

    private EditText edtSearch;
    private RecyclerView recycler;
    private SearchListDialogListener listener;
    private SimpleRecyclerViewAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_search_list, null);

        builder.setView(view);

        edtSearch = view.findViewById(R.id.edt_search);
        recycler = view.findViewById(R.id.recycler);

        if (getArguments() != null) {
            arrayList = getArguments().getStringArrayList(ARGUMENT_LIST);
        }
        adapter = new SimpleRecyclerViewAdapter(arrayList);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SearchListDialogListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() + "must implement ExampleDialogListener");
        }
    }

    public interface SearchListDialogListener {
        void onItemSelected(int position);
    }
}
