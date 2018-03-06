package com.pmdm.mijuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import sun.rmi.runtime.Log;

/**
 * Created by Dani on 19/02/2018.
 */

public class Jugador{

    // Atributo en el que se cargará la imagen del mosquetero.
    public Texture img;

    public int vida = 3;
    public int monedasConseguidas = 0;

    //filas y columnas de la sprite de animacion
    private static final int filas = 4;
    private static final int columnas = 3;

    //Animacion que se muestra en el render
    private Animation jugador;

    //Animacion para cada una de las direcciones
    private Animation jugadorArriba;
    private Animation jugadorAbajo;
    private Animation jugadorDerecha;
    private Animation jugadorIzquierda;

    // Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación
    // servirá para determinar cual es el frame que se debe representar.
    public float stateTime;

    // Contendrá el frame que se va a mostrar en cada momento.
    public TextureRegion cuadroActual;
    //Posicion en el eje de cordenadas del jugador
    public float jugadorX, jugadorY;

    //coordenadas pulsadas
    public float tactilX, tactilY;

    //velocidad de movimiento del personaje
    private final float velocidad = 100;

    //ancho y alto del jugador
    public int anchoJugador, altoJugador;

    private MiJuego juego;
    private Sound sonidoPasos;

    public float posicionInicialX = 1670, posicionInicialY=1730;


    public Jugador(MiJuego juego, Sound sonidoPasos){

        this.juego=juego;
        this.sonidoPasos=sonidoPasos;

        //caramos la imagen
        img = new Texture(Gdx.files.internal("mosquetero.png"));

        TextureRegion[][] textureRegions =
                TextureRegion.split(img,img.getWidth()/columnas, img.getHeight()/filas);

        //Cargamos las animaciones
        jugadorArriba = new Animation(0.150f, textureRegions[0]);
        jugadorDerecha = new Animation(0.150f, textureRegions[1]);
        jugadorAbajo = new Animation(0.150f, textureRegions[2]);
        jugadorIzquierda = new Animation(0.150f, textureRegions[3]);

        //animacion inicial del jugador
        jugador = jugadorAbajo;

        //stateTime a 0
        stateTime = jugador.getAnimationDuration()/2f;

        //posicion inicial del jugador
        jugadorX = posicionInicialX;
        jugadorY = posicionInicialY;


        //Extraemos e iniciamos el frame que debe ir asociado al momento actual
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime);
        anchoJugador = cuadroActual.getRegionWidth();
        altoJugador = cuadroActual.getRegionHeight();


    }

   public void movimientoPersonaje(){

        //controles de la flecha
        boolean arriba = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean abajo = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean izqd = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean dcha = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        float jugadorAnteriorX = jugadorX, jugadorAnteriorY= jugadorY;

        if (arriba && !abajo) {
            jugadorY += 1 * velocidad * Gdx.graphics.getDeltaTime() ;
            jugador = jugadorArriba;
        } else if (abajo && !arriba) {
            jugadorY -= 1 * Gdx.graphics.getDeltaTime() * velocidad;
            jugador = jugadorAbajo;
        } else if (izqd && !dcha) {
            jugadorX -= 1 * Gdx.graphics.getDeltaTime() * velocidad;
            jugador = jugadorIzquierda;
        } else if (dcha && !izqd) {
            jugadorX += 1 * Gdx.graphics.getDeltaTime() * velocidad;
            jugador = jugadorDerecha;
        }else{
            //esto hace que el personaje se quede en su animacion estatica que es la del centro del sprite
            stateTime = jugador.getAnimationDuration()/2f;
        }

        if (juego.colisiones()){

            jugadorX = jugadorAnteriorX;
            jugadorY = jugadorAnteriorY;
        }

        //Extraemos el frame que debe ir asociado al momento actual
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime,true);
        anchoJugador = cuadroActual.getRegionWidth();
        altoJugador = cuadroActual.getRegionHeight();
    }

    public void movimientoTactilPersonaje(){

        if(Gdx.input.isTouched()){

            float jugadorAnteriorX = jugadorX, jugadorAnteriorY= jugadorY;

            if(tactilX<Gdx.graphics.getWidth()*1/3){
                jugadorX -= 1 * Gdx.graphics.getDeltaTime() * velocidad;
                jugador = jugadorIzquierda;
            }else if(tactilX>Gdx.graphics.getWidth()*2/3){
                jugadorX += 1 * Gdx.graphics.getDeltaTime() * velocidad;
                jugador = jugadorDerecha;
            }else if(tactilX>Gdx.graphics.getWidth()*1/3 && tactilX<Gdx.graphics.getWidth()*2/3 && tactilY<Gdx.graphics.getHeight()/2){
                jugadorY += 1 * Gdx.graphics.getDeltaTime() * velocidad;
                jugador = jugadorArriba;
            }else if(tactilX>Gdx.graphics.getWidth()*1/3 && tactilX<Gdx.graphics.getWidth()*2/3 && tactilY>Gdx.graphics.getHeight()/2){
                jugadorY -= 1 * Gdx.graphics.getDeltaTime() * velocidad;
                jugador = jugadorAbajo;
                System.out.println(Gdx.graphics.getHeight());
            }

            if (juego.colisiones()){

                jugadorX = jugadorAnteriorX;
                jugadorY = jugadorAnteriorY;
            }

        }else{
            stateTime = jugador.getAnimationDuration()/2f;
        }

        //Extraemos el frame que debe ir asociado al momento actual
        cuadroActual = (TextureRegion) jugador.getKeyFrame(stateTime,true);
        anchoJugador = cuadroActual.getRegionWidth();
        altoJugador = cuadroActual.getRegionHeight();

    }

}
