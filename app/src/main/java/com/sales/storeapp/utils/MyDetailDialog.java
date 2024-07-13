package com.sales.storeapp.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.transition.TransitionManager;

import com.sales.storeapp.R;

public class MyDetailDialog extends AlertDialog {
    private Activity activity;
    private int icon;
    private String titulo;
    private String mensaje;
    private String info;
    private String positiveButtonText = null;
    private String negativeButtonText = null;
    private OnClickListener positiveButtonListener,negativeButtonListener;

    public MyDetailDialog(Activity activity, @DrawableRes int icon, String titulo, String mensaje, String info){
        super(activity);
        this.activity = activity;
        this.icon = icon;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.info = info;
    }



    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View pop = activity.getLayoutInflater().inflate(R.layout.dialog_general_detalle,null);
        builder.setView(pop);
        builder.setCancelable(false);

        final ViewGroup transitionsContainer = (ViewGroup) pop.findViewById(R.id.transitions_container);
        ImageView img_icon = pop.findViewById(R.id.img_icon);
        TextView tv_titulo = pop.findViewById(R.id.tv_titulo);
        TextView tv_mensaje = pop.findViewById(R.id.tv_mensaje);
        final TextView tv_info = pop.findViewById(R.id.tv_info);
        final Button btn_mostrar = pop.findViewById(R.id.btn_mostrar);
        final Button btn_ocultar = pop.findViewById(R.id.btn_ocultar);

        img_icon.setImageResource(icon);
        tv_titulo.setText(titulo);
        tv_mensaje.setText(mensaje);
        tv_info.setText(info);

        btn_mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                tv_info.setVisibility(View.VISIBLE);
                btn_mostrar.setVisibility(View.GONE);
                btn_ocultar.setVisibility(View.VISIBLE);
            }
        });

        btn_ocultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ocultar.setVisibility(View.GONE);
                btn_mostrar.setVisibility(View.VISIBLE);
                tv_info.setVisibility(View.GONE);

            }
        });

        if (positiveButtonListener != null){
            builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                positiveButtonListener.onClick();
            });
        }

        if (negativeButtonListener != null){
            builder.setNegativeButton(negativeButtonText, (dialog, which) -> {
                negativeButtonListener.onClick();
            });
        }

        builder.show();
    }

    public void setPositiveButton(String text, OnClickListener onClickListener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = onClickListener;
    }

    public void setNegativeButton(String text, OnClickListener onClickListener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = onClickListener;
    }

    public interface OnClickListener{
        public void onClick();
    }
}
