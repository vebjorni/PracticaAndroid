package com.example.inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class datosBBDD extends AppCompatActivity {

    private ListView lv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_bbdd);

        lv1 = (ListView)findViewById(R.id.lv1);
        ArrayList<String> ranking = new ArrayList<>();

        //Establecemos la conexion con la bbdd
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        //Un cursor que ira fila por fila  para asi mostrar el ranking de cada jugador
        Cursor fila = bd.rawQuery("select nombreJugador, numeroVictorias, numeroPerdidas from partidas", null);
        if(fila.moveToFirst()){
            do{
                ranking.add("Name: " + fila.getString(0) + " \nVictorias: " + fila.getString(1)+ " \tDerrotas:  " + fila.getString(2));
            }while(fila.moveToNext());
        }
        bd.close();//cerramos la conexion

        //Con arrayAdapter añado los datos al listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ranking);
        lv1.setAdapter(adapter);

    }

    //Boton borrar, Permite borrar los datos de la bbdd
    public void borrar(View view){
        //Conectamos con la bbdd
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        //Esta es la consulta que nos permite borrar la bbdd. Si quisiesemos borrar por id(por ejemplo) seria asi: bd.delete("partidas", id=1, null);
        bd.delete("partidas", null, null);
        bd.close();
        //Esto sirve para refrescar la activity
        finish();
        startActivity(getIntent());
    }
    //Boton Insertar. Permite Insertar un usuario de prueba Para asi poder verlo
    public void insertar(View view){
        //Conectamos con la bbdd
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        //Creamos un contenedor para los valores
        ContentValues registro = new ContentValues();
        //metemos los datos al contenedor
        registro.put("nombreJugador", "Prueba"+(int) (Math.random() * 100) + 1);
        registro.put("numeroVictorias", (int) (Math.random() * 10) + 1);
        registro.put("numeroPerdidas", (int) (Math.random() * 10) + 1);
        //Y aqui esta la consulta  que nos permite insertar los datos del contenedor
        bd.insert("partidas", null, registro);
        bd.close();
        //Esto sirve para refrescar la activity
        finish();
        startActivity(getIntent());

    }
    //Boton salir.
    public void salir(View view) {
        finish();
    }


}
