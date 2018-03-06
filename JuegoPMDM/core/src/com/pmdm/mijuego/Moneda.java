package com.pmdm.mijuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Dani on 06/03/2018.
 */

public class Moneda {

    //posiciones de las monedas
    public float[] posicionX;
    public float[] posicionY;
    public boolean[] conseguidas;

    //tamaño
    public int anchoMoneda, altoMoneda;

    //textura moneda
    public Texture textura;

    public Moneda(){
        textura = new Texture(Gdx.files.internal("moneda.png"));

        posicionX = new float[3];
        posicionY = new float[3];
        conseguidas = new boolean[3];

        for(int i = 0; i<conseguidas.length; i++)
            conseguidas[i]=false;

        anchoMoneda = textura.getWidth();
        altoMoneda = textura.getHeight();

        posicionX[0] = 1125;
        posicionY[0] = 1750;

        posicionX[1] = 1663;
        posicionY[1] = 160;

        posicionX[2] = 415;
        posicionY[2] = 1650;



    }
}
