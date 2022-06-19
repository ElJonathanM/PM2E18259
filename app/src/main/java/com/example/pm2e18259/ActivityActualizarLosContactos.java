package com.example.pm2e18259;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pm2e18259.Procesos.Pais;
import com.example.pm2e18259.Procesos.SQLiteConexion;
import com.example.pm2e18259.Procesos.Transacciones;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityActualizarLosContactos extends AppCompatActivity {
    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);

    EditText txtactcodigo, txtactnombre, txtacttelefono, txtactnota;
    Button btnatras, btnEditar;
    Spinner cmbactseleccionarpais;
    ArrayList<String> lista_paises;
    ArrayList<Pais> lista;
    ArrayAdapter<CharSequence> adp;
    int codigoPaisSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_los_contactos);

        btnatras = (Button) findViewById(R.id.btnactatras);
        txtactcodigo = (EditText)findViewById(R.id.txtactcodigo);
        txtactnombre = (EditText)findViewById(R.id.txtactnombre);
        txtacttelefono = (EditText)findViewById(R.id.txtacttelefono);
        txtactnota = (EditText)findViewById(R.id.txtactnota);
        cmbactseleccionarpais = (Spinner)findViewById(R.id.cmbactseleccionarpais);
        btnEditar = (Button) findViewById(R.id.acbtnEdit);

        txtactcodigo.setText(getIntent().getStringExtra("codigo"));
        txtactnombre.setText(getIntent().getStringExtra("nombreContacto"));
        txtacttelefono.setText(getIntent().getStringExtra("numeroContacto"));
        txtactnota.setText(getIntent().getStringExtra("notaContacto"));

        ObtenerListaPaises();

        adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item,lista_paises);
        cmbactseleccionarpais.setAdapter(adp);

        cmbactseleccionarpais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String cadena = adapterView.getSelectedItem().toString();
                codigoPaisSeleccionado = Integer.valueOf(extraerNumeros(cadena).toString().replace("]","").replace("[",""));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ActivityListaContactos.class);
                startActivity(intent);
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditarContacto();
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


    private void ObtenerListaPaises() {
        Pais pais = null;
        lista = new ArrayList<Pais>();
        SQLiteDatabase db = conexion.getReadableDatabase();

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

        for (int i=0; i<lista.size();i++)
        {
            lista_paises.add(lista.get(i).getNombrePais()+" ( "+lista.get(i).getCodigo()+" )");
        }
    }

    private void EditarContacto() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String ObjCodigo = txtactcodigo.getText().toString();

        ContentValues valores = new ContentValues();

        valores.put(Transacciones.nombreCompleto, txtactnombre.getText().toString());
        valores.put(Transacciones.telefono, txtacttelefono.getText().toString());
        valores.put(Transacciones.nota, txtactnota.getText().toString());
        valores.put(Transacciones.pais, codigoPaisSeleccionado);

        try {
            db.update(Transacciones.tablacontactos,valores, Transacciones.id +" = "+ ObjCodigo, null);
            db.close();
            Toast.makeText(getApplicationContext(),"Se actualizo correctamente", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ActivityListaContactos.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        }catch (Exception e) {
            Toast.makeText(getApplicationContext(),"No se actualizo", Toast.LENGTH_SHORT).show();
        }

    }

}