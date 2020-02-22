package com.example.inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //Boton play te manda a la activity del Buscaminas
    public void play(View view) {
        Intent i = new Intent(this, BuscaminasActivity.class );
        startActivity(i);
    }
    //Boton Score te manda a la activity de la puntuacion, donde muestra los datos de la bbdd
    public void score(View view) {
        Intent i = new Intent(this, datosBBDD.class );
        startActivity(i);
    }
    //boton salir cierra la aplicacion
    public void exit(View view){
        System.exit(0);
    }

    //mteodo onCreate para el menu
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        //Aqui usamos un switch para cada item del menu(los cuales estan en el xml -> res/menu/menu.xml)
        switch (item.getItemId()) {
            //El primero te manda a un link externo
            case R.id.historia:
                String url = "https://www.elobservador.com.uy/nota/buscaminas-la-historia-de-uno-de-los-juegos-mas-adictivos-201821911290";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            // El segundo te muestra un mensaje en pantalla
            case R.id.creadoPor:
                Toast toast = Toast.makeText(this, "Raul Sanchez. 2º DAM", Toast.LENGTH_LONG);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
