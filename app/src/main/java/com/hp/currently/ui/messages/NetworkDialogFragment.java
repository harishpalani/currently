package com.hp.currently.ui.messages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.hp.currently.R;

/**
 * Created by HP Labs on 8/24/2015.
 */
public class NetworkDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.network_error_title)
                .setMessage(R.string.network_error_message)
                .setPositiveButton(R.string.network_error_button_text, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
