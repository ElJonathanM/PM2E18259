package com.example.pm2e18259;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm2e18259.Procesos.Pais;
import com.example.pm2e18259.Procesos.SQLiteConexion;
import com.example.pm2e18259.Procesos.Transacciones;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
    SQLiteDatabase db;

    EditText txtnombre, txttelefono, txtnota;
    Spinner sppais;
    ImageView imgselfi;
    Button btntomarfoto, btnguardarcontacto, btnlistacontactos;
    FloatingActionButton fab;

    Bitmap imagen;
    ArrayList<String> lista_paises;
    ArrayList<Pais> lista;
    static final int PETICION_ACCESO_CAM = 100;
    static final int TAKE_PIC_REQUEST = 101;
    int codigoPaisSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtnombre = (EditText) findViewById(R.id.txtnombre);
        txttelefono = (EditText) findViewById(R.id.txtTelefono);
        txtnota = (EditText) findViewById(R.id.txtnota);
        sppais = (Spinner)findViewById(R.id.cmbpais);
        imgselfi = (ImageView) findViewById(R.id.imgselfi);

        btnguardarcontacto = (Button) findViewById(R.id.btnguardar);
        btntomarfoto = (Button) findViewById(R.id.btntomarfoto);
        btnlistacontactos = (Button)findViewById(R.id.btnlistacontactos);
        fab = (FloatingActionButton) findViewById(R.id.floatAgregarPais);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityAgregarPais.class);
                startActivity(intent);

            }
        });

        btnlistacontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityListaContactos.class);
                startActivity(intent);
            }
        });

        btnguardarcontacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    validarDatos();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Debe de tomar una foto ",Toast.LENGTH_LONG).show();
                }

            }
        });

        btntomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        ObtenerListaPaises();

        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item,lista_paises);
        sppais.setAdapter(adp);

        sppais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String cadena = adapterView.getSelectedItem().toString();
                codigoPaisSeleccionado = Integer.valueOf(extraerNumeros(cadena).toString().replace("]","").replace("[",""));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    List<Integer> extraerNumeros(String cadena) {
        List<Integer> todosLosNumeros = new ArrayList<Integer>();
        Matcher encuentrador = Pattern.compile("\\d+").matcher(cadena);
        while (encuentrador.find()) {
            todosLosNumeros.add(Integer.parseInt(encuentrador.group()));
        }
        return todosLosNumeros;
    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PETICION_ACCESO_CAM);
        }else{
            tomarFoto();
        }
    }
    private void tomarFoto() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takepic.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takepic,TAKE_PIC_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requescode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requescode, resultCode, data);

        if(requescode == TAKE_PIC_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imagen = (Bitmap) extras.get("data");
            imgselfi.setImageBitmap(imagen);
        }else if (resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            imgselfi.setImageURI(imageUri);

        }

    }

    private void validarDatos() {
        if (lista_paises.size() == 0){
            Toast.makeText(getApplicationContext(), "Debe de escribir un Pais" ,Toast.LENGTH_LONG).show();
        }else  if (txtnombre.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un nombre" ,Toast.LENGTH_LONG).show();
        }else if (txttelefono.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un telefono" ,Toast.LENGTH_LONG).show();
        }else if (txtnota.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir una nota" ,Toast.LENGTH_LONG).show();
        }else{
            guardarContacto(imagen);
        }
    }

    private void guardarContacto(Bitmap bitmap) {
        db = conexion.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] ArrayFoto  = stream.toByteArray();

        ContentValues valores = new ContentValues();

        valores.put(Transacciones.nombreCompleto, txtnombre.getText().toString());
        valores.put(Transacciones.telefono, txttelefono.getText().toString());
        valores.put(Transacciones.nota, txtnota.getText().toString());
        valores.put(String.valueOf(Transacciones.foto),ArrayFoto);
        valores.put(Transacciones.pais, codigoPaisSeleccionado);

        Long resultado = db.insert(Transacciones.tablacontactos, Transacciones.id, valores);

        Toast.makeText(getApplicationContext(), "Registro ingresado con exito, Codigo " + resultado.toString()
                ,Toast.LENGTH_LONG).show();

        db.close();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void ObtenerListaPaises() {
        Pais pais = null;
        lista = new ArrayList<Pais>();
        db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tblPaises,null);

        while (cursor.moveToNext()) {
            pais = new Pais();

            pais.setCodigo(cursor.getString(0));
            pais.setNombrePais(cursor.getString(1));

            lista.add(pais);
        }

        cursor.close();
        fillCombo();

    }

    private void fillCombo() {
        lista_paises = new ArrayList<String>();

        for (int i=0; i<lista.size();i++) {
            lista_paises.add(lista.get(i).getNombrePais()+" ( "+lista.get(i).getCodigo()+" )");
        }
    }
}