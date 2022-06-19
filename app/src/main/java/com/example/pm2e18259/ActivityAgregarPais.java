package com.example.pm2e18259;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pm2e18259.Procesos.SQLiteConexion;
import com.example.pm2e18259.Procesos.Transacciones;

public class ActivityAgregarPais extends AppCompatActivity {

    EditText txtpaiscodigo,txtpaisnombre;
    Button btnpaisguardar, btnpaisatras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pais);

        txtpaiscodigo = (EditText) findViewById(R.id.txtpaiscodigo);
        txtpaisnombre = (EditText) findViewById(R.id.txtpaisnombre);
        btnpaisguardar = (Button) findViewById(R.id.btnpaisguardar);
        btnpaisatras = (Button) findViewById(R.id.btnpaisatras);

        btnpaisguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarPais();
            }
        });

        btnpaisatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AgregarPais() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.codigo,txtpaiscodigo.getText().toString());
        valores.put(Transacciones.p_pais,txtpaisnombre.getText().toString());

        Long resultado = db.insert(Transacciones.tblPaises,Transacciones.codigo,valores);
        Toast.makeText(getApplicationContext(),"Guardado Exitosamente!"+resultado.toString(),Toast.LENGTH_LONG).show();
        db.close();

        limpiarPantalla();

    }

    private void limpiarPantalla() {
        txtpaisnombre.setText("");
        txtpaiscodigo.setText("");
    }

}