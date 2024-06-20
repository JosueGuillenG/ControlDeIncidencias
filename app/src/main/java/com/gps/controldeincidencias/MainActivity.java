package com.gps.controldeincidencias;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    //EditText etToken;
    LottieAnimationView boton;
    LottieAnimationView botonSignIn;
    EditText textoEmail;
    EditText textoContrasena;
    TextView errorLogin;
    Button loginB;
    Button regresarB;
    private FirebaseFunctions mFunctions;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //etToken
        boton = findViewById(R.id.lottiealertanimation);
        botonSignIn = findViewById(R.id.signIn);

        boton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                addMessage("Notificacion");
                return false;
            }
        });


        botonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.notificacionmenu).setVisibility(View.GONE);

                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    //reload();
                    findViewById(R.id.loginmenu).setVisibility(View.VISIBLE);

                    regresarB = findViewById(R.id.regresarB);
                    regresarB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            findViewById(R.id.loginmenu).setVisibility(View.GONE);
                            findViewById(R.id.notificacionmenu).setVisibility(View.VISIBLE);

                        }
                    });
                    textoEmail = findViewById(R.id.textEmailAddress);
                    textoContrasena = findViewById(R.id.textPassword);
                    errorLogin = findViewById(R.id.error);
                    loginB = findViewById(R.id.signinbutton);
                    loginB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            login();
                        }
                    });
                } else {
                    //MOSTRAR DIRECTO LOS REPORTES
                    loadMenuReporte();
                }
            }
        });

        registrationtoken();
        mFunctions = FirebaseFunctions.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //addMessage("Hola a todos");

        findViewById(R.id.alerta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.notificacionmenu).setVisibility(View.GONE);
                findViewById(R.id.alertasmenu).setVisibility(View.VISIBLE);
                findViewById(R.id.almenu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.notificacionmenu).setVisibility(View.VISIBLE);
                        findViewById(R.id.alertasmenu).setVisibility(View.GONE);
                    }
                });
                loadalertas();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
    }

    private void login() {
        String email = textoEmail.getText().toString();
        String password = textoContrasena.getText().toString();
        errorLogin.setText("");
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            errorLogin.setText("Error Login, email o contrasena faltante.");
            Toast.makeText(MainActivity.this, "Error Login", Toast.LENGTH_SHORT).show();

        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                ///Log.d(TAG, "signInWithEmail:success");
                                //System.out.println("signInWithEmail:success");
                                Toast.makeText(MainActivity.this, "Login in", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                loadMenuReporte();
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                //      Toast.LENGTH_SHORT).show();
                                errorLogin.setText("Error Login");
                                Toast.makeText(MainActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }

    }

    private void loadalertas(){
        findViewById(R.id.atrasalertas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadalertas();
            }
        });
        findViewById(R.id.nextalertas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextalertas();
            }
        });
        findViewById(R.id.nextalertas).setVisibility(View.VISIBLE);
        //CARGAR REPORTES DE LA DATABASE Y AGREGAR LISTENER
        //LEER DATOS


        first = db.collection("notificaciones")
                .limit(5);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        alertas(documentSnapshots);
                        // Get the last visible document
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        // Construct a new query starting at this document,
                        // get the next 25 cities.
                        nextQ = db.collection("notificaciones")
                                .startAfter(lastVisible)
                                .limit(5);

                        // Use the query for pagination
                        // ...
                    }
                });
    }
    private void nextalertas() {
        nextQ.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        alertas(documentSnapshots);
                        // Get the last visible document
                        if (documentSnapshots.size() > 0) {
                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                            // Construct a new query starting at this document,
                            // get the next 25 cities.
                            nextQ = db.collection("notificaciones")
                                    .startAfter(lastVisible)
                                    .limit(5);
                        }

                        // Use the query for pagination
                        // ...
                    }
                });
    }

    List<notificaciones> listaAlerta = new ArrayList<notificaciones>();
    private void alertas(QuerySnapshot documentSnapshots) {
        if (documentSnapshots.size() > 0) {
            ListView ly = findViewById(R.id.listaAlertas);
            listaAlerta.clear();
            notificaciones[] listaD = new notificaciones[documentSnapshots.getDocuments().size()];
            for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                listaAlerta.add(document.toObject(notificaciones.class));
                listaD[listaAlerta.size() - 1] = listaAlerta.get(listaAlerta.size() - 1);

            }

            notificacionesAlerta adapter = new notificacionesAlerta(getApplicationContext(), listaD, this);
            ly.setAdapter(adapter);
        } else {
            findViewById(R.id.next).setVisibility(View.GONE);
        }
    }
    public void abriralerta(Intent intent){
        startActivity(intent);
    }

    Button salirReporte;
    TextView nombreReporte;
    TextView descripcionReporte;

    Query first;
    Query nextQ;
    List<reporte> lista = new ArrayList<reporte>();

    private void loadMenuReporte() {

        findViewById(R.id.loginmenu).setVisibility(View.GONE);
        findViewById(R.id.notificacionmenu).setVisibility(View.GONE);
        findViewById(R.id.reportesmenu).setVisibility(View.VISIBLE);
        findViewById(R.id.reporte).setVisibility(View.GONE);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        findViewById(R.id.atras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findViewById(R.id.loginmenu).setVisibility(View.GONE);
                findViewById(R.id.notificacionmenu).setVisibility(View.VISIBLE);
                findViewById(R.id.reportesmenu).setVisibility(View.GONE);
                findViewById(R.id.reporte).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.generar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.reportesmenu).setVisibility(View.GONE);
                findViewById(R.id.reporte).setVisibility(View.GONE);
                findViewById(R.id.generarreporte).setVisibility(View.VISIBLE);

                findViewById(R.id.atrasReporte).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.reportesmenu).setVisibility(View.VISIBLE);
                        findViewById(R.id.reporte).setVisibility(View.GONE);
                        findViewById(R.id.generarreporte).setVisibility(View.GONE);
                    }
                });
                findViewById(R.id.generarReporteBoton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText nombrereporte =findViewById(R.id.reporteNombreGenerar);
                        EditText descripcion =findViewById(R.id.reporteDescripcionGenerar);
                        EditText[] tipos = new EditText[6];
                        tipos[0] = findViewById(R.id.tipo1);
                        tipos[1] = findViewById(R.id.tipo2);
                        tipos[2] = findViewById(R.id.tipo3);
                        tipos[3] = findViewById(R.id.tipo4);
                        tipos[4] = findViewById(R.id.tipo5);
                        tipos[5] = findViewById(R.id.tipo6);
                        System.out.println(nombrereporte.getText().toString());
                        if(nombrereporte.getText().toString() == null ||nombrereporte.getText().length()== 0 || descripcion.getText().length()== 0|| descripcion.getText().toString() == null){
                            Toast.makeText(MainActivity.this, "Llena todos el formato", Toast.LENGTH_SHORT).show();
                        }else {


                            Map<String, Object> data = new HashMap<>();

                            data.put("nombre", nombrereporte.getText().toString());
                            data.put("descripcion", descripcion.getText().toString());
                            data.put("fecha", Timestamp.now());
                            data.put("localizacion", getLocation());

                            Map<String, Boolean> tiposS = new HashMap<>();
                            for (int i = 0; i < tipos.length; i++) {
                                if(tipos[i].getText().length()> 0 && tipos[i].getText().toString() != null)
                                    tiposS.put(tipos[i].getText().toString(), true);
                            }
                            if(tiposS.size() > 0)
                                data.put("Tipos", tiposS);
                            db.collection("reportes")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            nombrereporte.setText("");
                            descripcion.setText("");
                            for (int i = 0; i < tipos.length; i++) {
                                tipos[i].setText("");
                            }
                            findViewById(R.id.reportesmenu).setVisibility(View.VISIBLE);
                            findViewById(R.id.reporte).setVisibility(View.GONE);
                            findViewById(R.id.generarreporte).setVisibility(View.GONE);
                            loadMenuReporte();

                        }
                    }
                });
            }
        });

        findViewById(R.id.recargar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMenuReporte();
            }
        });
        findViewById(R.id.next).setVisibility(View.VISIBLE);
        //CARGAR REPORTES DE LA DATABASE Y AGREGAR LISTENER
        //LEER DATOS


        first = db.collection("reportes")
                .limit(5);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // ...

                        reportes(documentSnapshots);
                        // Get the last visible document
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        // Construct a new query starting at this document,
                        // get the next 25 cities.
                        nextQ = db.collection("reportes")
                                .startAfter(lastVisible)
                                .limit(5);

                        // Use the query for pagination
                        // ...
                    }
                });

    }

    private void next() {
        nextQ.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        reportes(documentSnapshots);
                        // Get the last visible document
                        if (documentSnapshots.size() > 0) {
                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                            // Construct a new query starting at this document,
                            // get the next 25 cities.
                            nextQ = db.collection("reportes")
                                    .startAfter(lastVisible)
                                    .limit(5);
                        }

                        // Use the query for pagination
                        // ...
                    }
                });
    }

    private void reportes(QuerySnapshot documentSnapshots) {
        if (documentSnapshots.size() > 0) {
            ListView ly = findViewById(R.id.lista);
            lista.clear();
            reporte[] listaD = new reporte[documentSnapshots.getDocuments().size()];
            for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                lista.add(document.toObject(reporte.class));
                listaD[lista.size() - 1] = lista.get(lista.size() - 1);

            }

            reporteAdaptador adapter = new reporteAdaptador(getApplicationContext(), listaD, this);
            ly.setAdapter(adapter);
        } else {
            findViewById(R.id.next).setVisibility(View.GONE);
        }
    }

    public void cargarReporte(int i) {

        findViewById(R.id.loginmenu).setVisibility(View.GONE);
        findViewById(R.id.notificacionmenu).setVisibility(View.GONE);
        findViewById(R.id.reportesmenu).setVisibility(View.GONE);
        findViewById(R.id.reporte).setVisibility(View.VISIBLE);
        salirReporte = findViewById(R.id.retrocederReporte);
        salirReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findViewById(R.id.loginmenu).setVisibility(View.GONE);
                findViewById(R.id.notificacionmenu).setVisibility(View.GONE);
                findViewById(R.id.reportesmenu).setVisibility(View.VISIBLE);
                findViewById(R.id.reporte).setVisibility(View.GONE);
            }
        });
        TextView fecha = findViewById(R.id.fecha);
        TextView localizacion = findViewById(R.id.localizacion);
        TextView tipos = findViewById(R.id.Tipos);

        //CARGAR REPORTE CON LOS DATOS DEL REPORTE SELECCIONADO
        nombreReporte = findViewById(R.id.reportenombre);
        descripcionReporte = findViewById(R.id.reportedescripcion);
        nombreReporte.setText(lista.get(i).getNombre());
        descripcionReporte.setText(lista.get(i).getDescripcion());
        fecha.setText(lista.get(i).getFecha().toString());
        localizacion.setText("Localizacion: Latitud:" + lista.get(i).getLocalizacion().getLatitude() + " Longitud:" + lista.get(i).getLocalizacion().getLongitude());
        String tiposS = "";
        if(lista.get(i).getTipos()!= null) {
            for (Object it : lista.get(i).getTipos().keySet()) {

                //en nuestro ejemplo va a mostrarnos los países
                System.out.println(it.toString());
                System.out.println(lista.get(i).getTipos().get(it).toString());
                tiposS += it.toString() + " \n";

            }
        }
        tipos.setText("Tipos \n " + tiposS);
        /*if(i==1){
            nombreReporte.setText("Reporte de prueba");
            descripcionReporte.setText("Descripcion del reporte donde indicaremos lo sucedido al incidente");
        }else if(i==2){
            nombreReporte.setText("Segunda prueba de reporte");
            descripcionReporte.setText("No ha sucedido nada todo tranquilo, Solo necesitamos unas pruebas mas y acabamos, casi terminado");
        }*/
    }

    void registrationtoken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //System.out.println(token);
                        //Toast.makeText(MainActivity.this, "Device: " + token, Toast.LENGTH_SHORT).show();
                        //etToken.setText(token);

                        FirebaseMessaging.getInstance().subscribeToTopic("Notificar")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = "Subscribed";
                                        if (!task.isSuccessful()) {
                                            msg = "Subscribe failed";
                                        }
                                        //System.out.println(msg);
                                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        //mensaje1.setText("Localización agregada");
        //mensaje2.setText("");
    }

    Location loca;

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        loca = loc;
        //latitud = loc.getLatitude();
        //longitud = loc.getLongitude();
        /*if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //mensaje2.setText("Mi direccion es: \n"
                      //      + DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public GeoPoint getLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            GeoPoint gp = new GeoPoint(lat,lon);
            //LatLng latLng = new LatLng(lat, lon);
            return gp;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    private Task<String> addMessage(String text) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        //Aqui podria ir la localizacion,Nombre, Lugar, etc
        EditText eTN = findViewById(R.id.nombre);
        String nombreP = "Guest";
        if(eTN.getText().toString() != ""){
            nombreP = eTN.getText().toString();
        }

        data.put("nombre", nombreP);
        data.put("fecha", Timestamp.now());
        data.put("localizacion", getLocation());
        db.collection("notificaciones")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        Toast.makeText(MainActivity.this, "Notificado", Toast.LENGTH_SHORT).show();
        return mFunctions
                .getHttpsCallable("addMessage")
                .call("")
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        String result = (String) task.getResult().getData();
                        System.out.println(result);
                        return result;
                    }
                });
    }


}