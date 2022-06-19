package com.example.pm2e18259;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pm2e18259.Procesos.Contactos;
import com.example.pm2e18259.Procesos.SQLiteConexion;
import com.example.pm2e18259.Procesos.Transacciones;

import java.util.ArrayList;

public class ActivityListaContactos extends AppCompatActivity {
    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

    EditText txtbuscarpais;
    Button btnlistaatras, btnactualizarContacto, btneliminarcontacto, btncompartir, btnverimagen;
    ListView listacontactos;

    ArrayAdapter adp;
    ArrayList<Contactos> listaContactos;
    ArrayList <String> ArregloContactos;

    Intent intent;
    Contactos contacto;

    static final int PETICION_LLAMADA_TELEFONO = 102;
    int previousPosition = 1, count=1;
    long previousMil=0;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        btnlistaatras = (Button) findViewById(R.id.btnlistaatras);
        btnactualizarContacto = (Button) findViewById(R.id.btnactualizarcontacto);
        btneliminarcontacto = (Button) findViewById(R.id.btnelicontacto);
        btncompartir = (Button) findViewById(R.id.btncompartir);
        btnverimagen = (Button) findViewById(R.id.btnverimagen);
        listacontactos = (ListView) findViewById(R.id.listacontactos);
        intent = new Intent(getApplicationContext(),ActivityActualizarLosContactos.class);

        obtenerlistaContactos();
        adp = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,ArregloContactos);
        listacontactos.setAdapter(adp);
        txtbuscarpais = (EditText) findViewById(R.id.txtbuscarpais);
        txtbuscarpais.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adp.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listacontactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(previousPosition==i){
                    count++;
                    if(count==2 && System.currentTimeMillis()-previousMil<=1000){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Acción");
                        alertDialogBuilder.setMessage("¿Desea llamar a "+contacto.getNombreContacto()+"?")
                                .setCancelable(false)
                                .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try{
                                            permisoLlamada();
                                        }catch (Exception ex){
                                            ex.toString();
                                        }
                                        Toast.makeText(getApplicationContext(),"Llamada en Proceso",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        count=1;
                    }
                }
                else {
                    previousPosition=i;
                    count=1;
                    previousMil=System.currentTimeMillis();
                    contacto = listaContactos.get(i);
                    setContactoSeleccionado();
                }
            }

        });


        btnlistaatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        btnverimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(getApplicationContext(),ActivityVerFoto.class);
                    intent.putExtra("codigoParaFoto", contacto.getCodigo()+"");
                    startActivity(intent);
                }catch (NullPointerException e){
                    Intent intent = new Intent(getApplicationContext(),ActivityVerFoto.class);
                    intent.putExtra("codigoParaFoto", "1");
                    startActivity(intent);
                }

            }
        });

        btnactualizarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent);
            }
        });

        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarContacto();
            }
        });

        btneliminarcontacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Eliminar Contacto");
                alertDialogBuilder
                        .setMessage("¿Desea eliminar el contacto?")
                        .setCancelable(false)
                        .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                eliminarContacto();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    private void permisoLlamada() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE}, PETICION_LLAMADA_TELEFONO);
        }else{
            LlamarContacto();
        }
    }

    private void LlamarContacto() {
        String numero = "+"+contacto.getCodigoPais()+contacto.getNumeroContacto();
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numero));
        startActivity(intent);
    }

    private void eliminarContacto() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        int obtenerCodigo = contacto.getCodigo();

        db.delete(Transacciones.tablacontactos,Transacciones.id +" = "+ obtenerCodigo, null);

        Toast.makeText(getApplicationContext(), "Registro eliminado con exito, Codigo " + obtenerCodigo
                ,Toast.LENGTH_LONG).show();
        db.close();

        Intent intent = new Intent(this, ActivityListaContactos.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void enviarContacto(){
        String contactoEnviado = "El numero de "+contacto.getNombreContacto().toString()+
                " es +"+contacto.getCodigoPais()+contacto.getNumeroContacto() ;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, contactoEnviado);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void setContactoSeleccionado() {
        intent.putExtra("codigo", contacto.getCodigo()+"");
        intent.putExtra("nombreContacto", contacto.getNombreContacto());
        intent.putExtra("numeroContacto", contacto.getNumeroContacto()+"");
        intent.putExtra("codigoPais", contacto.getCodigoPais()+"");
        intent.putExtra("notaContacto", contacto.getNota());
    }

    private void obtenerlistaContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();

        Contactos list_contact = null;
        listaContactos = new ArrayList<Contactos>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ Transacciones.tablacontactos, null);
        while (cursor.moveToNext()) {
            list_contact = new Contactos();
            list_contact.setCodigo(cursor.getInt(0));
            list_contact.setNombreContacto(cursor.getString(1));
            list_contact.setNumeroContacto(cursor.getInt(2));
            list_contact.setNota(cursor.getString(3));
            list_contact.setCodigoPais(cursor.getString(5));
            listaContactos.add(list_contact);
        }
        cursor.close();
        llenarlista();

    }

    private void llenarlista() {
        ArregloContactos = new ArrayList<String>();

        for (int i=0; i<listaContactos.size();i++) {
            ArregloContactos.add(listaContactos.get(i).getNombreContacto()+" | "+
                    listaContactos.get(i).getCodigoPais()+
                    listaContactos.get(i).getNumeroContacto());

        }
    }

}