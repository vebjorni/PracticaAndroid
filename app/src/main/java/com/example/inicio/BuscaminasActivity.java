package com.example.inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BuscaminasActivity extends AppCompatActivity implements View.OnTouchListener{

    private Tablero fondo;
    int x,y;
    private casilla[][] casillas;
    private boolean activo = true;

    private String nombreJugador;
    public int numeroVictorias= 0, numeroPerdidas = 0;

    private EditText et1;
    private TextView tw1,tw2;
    private Button start, atras, reiniciar;
    private LinearLayout layout;


    public BuscaminasActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        //No permite que la pantalla gire
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscaminas);

        et1 = (EditText) findViewById(R.id.et1);
        tw1 = (TextView) findViewById(R.id.tw1);
        start = (Button) findViewById(R.id.start);
        atras = (Button) findViewById(R.id.atras);
        reiniciar = (Button) findViewById(R.id.reiniciar);
        layout = (LinearLayout) findViewById(R.id.layout1);
        tw2 = (TextView) findViewById(R.id.tw2);


        fondo = new Tablero(this);
        fondo.setOnTouchListener(this);
        layout.addView(fondo);
        casillas = new casilla[8][8];
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                casillas[f][c] = new casilla();
            }
        }
        this.disponerBombas();
        this.contarBombasPerimetro();
        getSupportActionBar().hide();
    }
//OnClick - boton atras
    public void salir(View v) {
actualizarUser();
        finish();
    }

//OnClick - boton reiniciar
    public void reiniciar(View v) {

        casillas = new casilla[8][8];
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                casillas[f][c] = new casilla();
            }
        }
        this.disponerBombas();
        this.contarBombasPerimetro();
        activo = true;

        fondo.invalidate();
    }

//Metodo para la funcion onTouch.
    public boolean onTouch(View v, MotionEvent event) {
        if (activo)
            for (int f = 0; f < 8; f++) {
                for (int c = 0; c < 8; c++) {
                    if (casillas[f][c].dentro((int) event.getX(),
                            (int) event.getY())) {
                        casillas[f][c].destapado = true;
                        if (casillas[f][c].contenido == 80) {
                            numeroPerdidas=numeroPerdidas+1;

                            Toast.makeText(this, "Booooooooommmmmmmmmmmm",
                                    Toast.LENGTH_LONG).show();
                            activo = false;
                        } else if (casillas[f][c].contenido == 0)
                            recorrer(f, c);
                        fondo.invalidate();
                    }
                }
            }
        if (gano() && activo) {
            numeroVictorias=numeroVictorias+1;

            Toast.makeText(this, "Ganaste", Toast.LENGTH_LONG).show();
            activo = false;

        }

        return true;
    }

    class Tablero extends View {

        public Tablero(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(0, 0, 0);
            int ancho = 0;
            if (canvas.getWidth() < canvas.getHeight())
                ancho = fondo.getWidth();
            else
                ancho = fondo.getHeight();
            int anchocua = ancho / 8;
            Paint paint = new Paint();
            paint.setTextSize(50);
            Paint paint2 = new Paint();
            paint2.setTextSize(50);
            paint2.setTypeface(Typeface.DEFAULT_BOLD);
            paint2.setARGB(255, 0, 0, 255);
            Paint paintlinea1 = new Paint();
            paintlinea1.setARGB(255, 255, 255, 255);
            int filaact = 0;
            for (int f = 0; f < 8; f++) {
                for (int c = 0; c < 8; c++) {
                    casillas[f][c].fijarxy(c * anchocua, filaact, anchocua);
                    if (casillas[f][c].destapado == false)
                        paint.setARGB(153, 204, 204, 204);
                    else
                        paint.setARGB(255, 153, 153, 153);
                    canvas.drawRect(c * anchocua, filaact, c * anchocua
                            + anchocua - 2, filaact + anchocua - 2, paint);
                    // linea blanca
                    canvas.drawLine(c * anchocua, filaact, c * anchocua
                            + anchocua, filaact, paintlinea1);
                    canvas.drawLine(c * anchocua + anchocua - 1, filaact, c
                                    * anchocua + anchocua - 1, filaact + anchocua,
                            paintlinea1);

                    if (casillas[f][c].contenido >= 1
                            && casillas[f][c].contenido <= 8
                            && casillas[f][c].destapado)
                        canvas.drawText(
                                String.valueOf(casillas[f][c].contenido), c
                                        * anchocua + (anchocua / 2) - 8,
                                filaact + anchocua / 2, paint2);

                    if (casillas[f][c].contenido == 80
                            && casillas[f][c].destapado) {
                        Paint bomba = new Paint();
                        bomba.setARGB(255, 255, 0, 0);
                        canvas.drawCircle(c * anchocua + (anchocua / 2),
                                filaact + (anchocua / 2), 8, bomba);
                    }

                }
                filaact = filaact + anchocua;
            }
        }
    }

    private void disponerBombas() {
        int cantidad = 8;
        do {
            int fila = (int) (Math.random() * 8);
            int columna = (int) (Math.random() * 8);
            if (casillas[fila][columna].contenido == 0) {
                casillas[fila][columna].contenido = 80;
                cantidad--;
            }
        } while (cantidad != 0);
    }

    private boolean gano() {
        int cant = 0;

        for (int f = 0; f < 8; f++)
            for (int c = 0; c < 8; c++)
                if (casillas[f][c].destapado)
                    cant++;
        if (cant == 56) {
            return true;
        }else {
            return false;
        }

    }

    private void contarBombasPerimetro() {
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                if (casillas[f][c].contenido == 0) {
                    int cant = contarCoordenada(f, c);
                    casillas[f][c].contenido = cant;
                }
            }
        }
    }

    int contarCoordenada(int fila, int columna) {
        int total = 0;
        if (fila - 1 >= 0 && columna - 1 >= 0) {
            if (casillas[fila - 1][columna - 1].contenido == 80)
                total++;
        }
        if (fila - 1 >= 0) {
            if (casillas[fila - 1][columna].contenido == 80)
                total++;
        }
        if (fila - 1 >= 0 && columna + 1 < 8) {
            if (casillas[fila - 1][columna + 1].contenido == 80)
                total++;
        }

        if (columna + 1 < 8) {
            if (casillas[fila][columna + 1].contenido == 80)
                total++;
        }
        if (fila + 1 < 8 && columna + 1 < 8) {
            if (casillas[fila + 1][columna + 1].contenido == 80)
                total++;
        }

        if (fila + 1 < 8) {
            if (casillas[fila + 1][columna].contenido == 80)
                total++;
        }
        if (fila + 1 < 8 && columna - 1 >= 0) {
            if (casillas[fila + 1][columna - 1].contenido == 80)
                total++;
        }
        if (columna - 1 >= 0) {
            if (casillas[fila][columna - 1].contenido == 80)
                total++;
        }
        return total;
    }

    private void recorrer(int fil, int col) {
        if (fil >= 0 && fil < 8 && col >= 0 && col < 8) {
            if (casillas[fil][col].contenido == 0) {
                casillas[fil][col].destapado = true;
                casillas[fil][col].contenido = 50;
                recorrer(fil, col + 1);
                recorrer(fil, col - 1);
                recorrer(fil + 1, col);
                recorrer(fil - 1, col);
                recorrer(fil - 1, col - 1);
                recorrer(fil - 1, col + 1);
                recorrer(fil + 1, col + 1);
                recorrer(fil + 1, col - 1);
            } else if (casillas[fil][col].contenido >= 1
                    && casillas[fil][col].contenido <= 8) {
                casillas[fil][col].destapado = true;
            }
        }
    }

    //A partir de aqui el codigo es de la bbdd


    //cuando se cree un usuario
    public void partidasUser() {
    //Abrimos la conexion con la bbdd
       AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        //Creamos un contenedor
        ContentValues registro = new ContentValues();
        //Rellenamos el contenedor
        registro.put("nombreJugador", nombreJugador);
        registro.put("numeroVictorias", 0);
        registro.put("numeroPerdidas", 0);
        //Ejecutamos la consulta que nos permitira insertar los datos
        bd.insert("partidas", null, registro);

        bd.close();

    }

    //cuando se actualiza un user

    public void actualizarUser(){
        int nVictoriaBD = 0;
        int nPerdidadBD = 0;
        //Abrimos conexion con la bbdd
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        //creamos un cursor para que nos coja los datos de nuestro user
        Cursor fila = bd.rawQuery(
                "select numeroVictorias,numeroPerdidas  from partidas where nombreJugador="+nombreJugador, null);
        if (fila.moveToFirst()) {
           nVictoriaBD = fila.getInt(0);
           nPerdidadBD = fila.getInt(1);
        }

        //Creamos un contenedor
        ContentValues actualiza = new ContentValues();
        //Llenamos el contenedor con los datos
        actualiza.put("numeroVictorias", numeroVictorias + nVictoriaBD);
        actualiza.put("numeroPerdidas", numeroPerdidas + nPerdidadBD);
        //Ejecutamos la consulta para que se actualice
        bd.update("partidas", actualiza, "nombreJugador=" + nombreJugador, null);
        bd.close();
}


    // Aqui esta el codigo para hacer invisible el logueo y que aparezca el buscaminas
    public void inviVisi(View Button){

            et1.setVisibility(View.INVISIBLE);
            tw1.setVisibility(View.INVISIBLE);
            start.setVisibility(View.INVISIBLE);

            atras.setVisibility(View.VISIBLE);
            reiniciar.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);

            nombreJugador = String.valueOf(et1.getText());

            partidasUser();
    }
}




