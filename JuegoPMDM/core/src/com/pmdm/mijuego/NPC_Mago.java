package com.pmdm.mijuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Dani on 19/02/2018.
 */

public class NPC_Mago{

    private MiJuego juego;

    private SpriteBatch sb;

    //Animaciones posicionales del mago
    private Animation magoAbajo;
    private Animation magoArriba;
    private Animation magoDerecha;
    private Animation magoIzquierda;

    //array con los objetos animacion del mago
    public Animation[] mago;

    //Atributos de anchura y altura del sprite
    public int anchoMago, altoMago;

    //posicion inicial de cada uno de los magos
    public float[] magoX;
    public float[] magoY;

    //poscion final de cada uno de los magos
    public float[] destinoMagoX;
    public float[] destinoMagoY;

    //numero total de enemigos
    private static final int numeroMagos = 20;

    //tiempo
    public float stateTimeMago;

    private Texture img;

    public TextureRegion cuadroActual;

    private int FRAME_COLS=3;
    private int FRAME_ROWS=4;


    public NPC_Mago(MiJuego juego){
        sb = new SpriteBatch();

        this.juego=juego;

        mago = new Animation[numeroMagos];

        magoX = new float[numeroMagos];
        magoY = new float[numeroMagos];
        destinoMagoX = new float[numeroMagos];
        destinoMagoY = new float[numeroMagos];

        img = new Texture(Gdx.files.internal("magorojo.png"));

        //Sacamos los frames de la imagen
        TextureRegion[][] tmp
                = TextureRegion.split(img, img.getWidth()/FRAME_COLS, img.getHeight()/FRAME_ROWS );

        //cargamos las animaciones
        magoArriba = new Animation(0.150f, tmp[0]);
        magoArriba.setPlayMode(Animation.PlayMode.LOOP);
        magoDerecha = new Animation(0.150f, tmp[1]);
        magoDerecha.setPlayMode(Animation.PlayMode.LOOP);
        magoAbajo = new Animation(0.150f, tmp[2]);
        magoAbajo.setPlayMode(Animation.PlayMode.LOOP);
        magoIzquierda = new Animation(0.150f, tmp[3]);
        magoIzquierda.setPlayMode(Animation.PlayMode.LOOP);

        cuadroActual = (TextureRegion) magoAbajo.getKeyFrame(stateTimeMago);
        anchoMago = cuadroActual.getRegionWidth();
        altoMago = cuadroActual.getRegionHeight();

        for(int i=0 ; i<numeroMagos; i++){

            magoX[i] = ((float) (Math.random() * juego.anchoMapa));
            magoY[i] = ((float) (Math.random() * juego.altoMapa));

            if(i%2 == 0){
                //los enemigos pares se moveran en vertical
                destinoMagoX[i] = magoX[i];
                destinoMagoY[i] = ((float) (Math.random() * juego.altoMapa));

                if(magoY[i]< destinoMagoY[i]){
                    mago[i] = magoArriba;
                }else{
                    mago[i] = magoAbajo;
                }
            }else{
                //los enemigos impares se moveran en horizontal
                destinoMagoX[i] = ((float) (Math.random() * juego.anchoMapa));
                destinoMagoY[i] = magoX[i];

                //Determinamos cual de las animaciones horizontales se utiliza

                if(magoX[i]<destinoMagoX[i]){
                    mago[i] = magoDerecha;
                }else{
                    mago[i] = magoIzquierda;
                }
            }
        }

        //ponemos el tiempo a 0
        stateTimeMago = 0f;
    }



    public void actualizarNPC(int i){

        float delta = 10f;

        if(destinoMagoY[i]>magoY[i]){
            magoY[i] += delta *Gdx.graphics.getDeltaTime();
            mago[i] = magoArriba;
        }

        if(destinoMagoY[i]<magoY[i]){
            magoY[i] -= delta*Gdx.graphics.getDeltaTime();
            mago[i] = magoAbajo;
        }

        if(destinoMagoX[i]>magoX[i]){
            magoX[i] += delta*Gdx.graphics.getDeltaTime();
            mago[i] = magoDerecha;
        }

        if(destinoMagoX[i]<magoX[i]){
            magoX[i] -= delta*Gdx.graphics.getDeltaTime();
            mago[i] = magoIzquierda;
        }

        cuadroActual = (TextureRegion) mago[i].getKeyFrame(stateTimeMago);

    }
}
