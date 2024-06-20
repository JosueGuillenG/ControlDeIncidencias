package com.gps.controldeincidencias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gps.controldeincidencias.R;
import com.gps.controldeincidencias.reporte;

public class reporteAdaptador extends BaseAdapter {
    Context context;
    reporte[] reporte;
    LayoutInflater inflater;
    MainActivity main;

    public reporteAdaptador(Context ctx, reporte[] rep,MainActivity ma){
        context = ctx;
        this.reporte = rep;
        inflater = LayoutInflater.from(ctx);
        main = ma;
    }

    @Override
    public int getCount() {
        return reporte.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.reporte,null);
        TextView textView = (TextView) convertView.findViewById(R.id.name);
        textView.setText(reporte[position].getNombre());

        Button boton = (Button) convertView.findViewById(R.id.open);
        boton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("cargar reporte");
                        main.cargarReporte(position);
                    }
                }
        );

        return convertView;
    }
}
