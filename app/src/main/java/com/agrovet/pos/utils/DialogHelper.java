package com.agrovet.pos.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.agrovet.pos.R;

public class DialogHelper {

    public enum DialogType {
        SUCCESS, ERROR, INFO, WARNING
    }

    public static void showCustomAlert(Context context, DialogType type, String title, String message, Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_alert, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView iconView = view.findViewById(R.id.img_alert_icon);
        TextView titleView = view.findViewById(R.id.txt_alert_title);
        TextView messageView = view.findViewById(R.id.txt_alert_message);
        Button btnPositive = view.findViewById(R.id.btn_alert_positive);

        titleView.setText(title);
        messageView.setText(message);

        switch (type) {
            case SUCCESS:
                iconView.setImageResource(android.R.drawable.ic_dialog_info);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.verde_exito));
                titleView.setTextColor(ContextCompat.getColor(context, R.color.verde_exito));
                break;
            case ERROR:
                iconView.setImageResource(android.R.drawable.ic_dialog_alert);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.rojo_error));
                titleView.setTextColor(ContextCompat.getColor(context, R.color.rojo_error));
                break;
            case WARNING:
                iconView.setImageResource(android.R.drawable.ic_dialog_alert);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.mostaza));
                titleView.setTextColor(ContextCompat.getColor(context, R.color.mostaza));
                break;
            default:
                iconView.setImageResource(android.R.drawable.ic_dialog_info);
                iconView.setColorFilter(ContextCompat.getColor(context, R.color.teal));
                titleView.setTextColor(ContextCompat.getColor(context, R.color.teal));
                break;
        }

        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });

        dialog.show();
    }
}
