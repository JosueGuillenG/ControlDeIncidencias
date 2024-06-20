package com.gps.controldeincidencias;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class notificacionesAlerta extends BaseAdapter {
    Context context;
    notificaciones[] reporte;
    LayoutInflater inflater;
    MainActivity main;

    public notificacionesAlerta(Context ctx, notificaciones[] rep,MainActivity ma){
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
        TextView subtexto = (TextView) convertView.findViewById(R.id.subtexto);
        subtexto.setText(reporte[position].getFecha().toString() +" " + reporte[position].getLocalizacion());

        Button boton = (Button) convertView.findViewById(R.id.open);
        boton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("cargar reporte");
                        //MANDAR GPS
                        Uri uri = Uri.parse("geo:"+reporte[position].getLocalizacion().getLatitude()+" ,"+reporte[position].getLocalizacion().getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        main.abriralerta(intent);
                    }
                }
        );

        return convertView;
    }
}
