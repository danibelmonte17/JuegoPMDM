package com.pmdm.mijuego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;


public class MiJuego extends ApplicationAdapter implements InputProcessor {

	public TiledMap mapa;

	private OrthogonalTiledMapRenderer mapaRenderer;

	public OrthographicCamera camara;

	private Jugador jugador;
	private NPC_Mago magos;
	private Moneda monedas;

    // Atributo que permitirá la representación de la imagen de textura anterior.
    private Sprite spritePersonaje;
    // Atributo que permite dibujar imágenes 2D, en este caso el spritePersonaje.
    private SpriteBatch sb;

    //ancho ,alto del mapa y ancho ,alto de las baldosas
    public int anchoMapa, altoMapa, anchoBaldosa, altoBaldosa;

    //capa de obstaculos
    private TiledMapTileLayer capaObstaculos;
    private boolean[][] obstaculo;

    private boolean controlTactil;

    //Audio del juego
    private Music musicaFondo;
    private Sound sonidoColisionEnemigo, sonidoPasos, sonidoObstaculos, sonidoGameOver, sonidoMoneda, sonidoVictoria;

    public MiJuego(boolean tactil){
        controlTactil = tactil;
    }

    @Override
	public void create () {

        //creamos el spritebatch
        sb = new SpriteBatch();

		//tamaño de la camara
        camara = new OrthographicCamera(800, 540);

        camara.update();

        mapa = new TmxMapLoader().load("TerrenoJuego.tmx");

        mapaRenderer = new OrthogonalTiledMapRenderer(mapa);

        //capa de suelo
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);

        anchoBaldosa = (int) capa.getTileWidth();
        altoBaldosa = (int) capa.getTileHeight();
        anchoMapa = capa.getWidth()*altoBaldosa;
        altoMapa=capa.getHeight()*altoBaldosa;

        //instanciar sonidos
        sonidoColisionEnemigo = Gdx.audio.newSound(Gdx.files.internal("qubodup-PowerDrain.ogg"));
        sonidoPasos = Gdx.audio.newSound(Gdx.files.internal("Fantozzi-SandR3.ogg"));
        sonidoObstaculos = Gdx.audio.newSound(Gdx.files.internal("wall.wav"));
        sonidoGameOver = Gdx.audio.newSound(Gdx.files.internal("gameover.mp3"));
        sonidoMoneda = Gdx.audio.newSound(Gdx.files.internal("monedaMario.mp3"));
        sonidoVictoria = Gdx.audio.newSound(Gdx.files.internal("victoria.mp3"));

        jugador = new Jugador(this,sonidoPasos);

        magos = new NPC_Mago(this);

        monedas = new Moneda();

        //ponemos a la escucha el evento de pulsar en la pantalla tactil

        Gdx.input.setInputProcessor(this);

        //cargamos la capa de obstaculos
        capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(3);

        //Cargamos la matriz de los obstáculos del mapa de baldosas.
        int anchoCapa = capaObstaculos.getWidth(), altoCapa = capaObstaculos.getHeight();
        obstaculo = new boolean[anchoCapa][altoCapa];
        for (int x = 0; x < anchoCapa; x++) {
            for (int y = 0; y < altoCapa; y++) {
                obstaculo[x][y] = (capaObstaculos.getCell(x, y) != null);
            }
        }


        //iniciar musica de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("fondo.mp3"));
        musicaFondo.setVolume(0.5f);
        musicaFondo.setLooping(true);
        musicaFondo.play();


    }

	@Override
	public void render () {
        if(controlTactil)
            jugador.movimientoTactilPersonaje();
        else
            jugador.movimientoPersonaje();

        colisionesConEnemigos();
        colisionMonedas();
        renderizarJuego();
	}
	
	@Override
	public void dispose () {
        mapa.dispose();
        mapaRenderer.dispose();
        sb.dispose();
        musicaFondo.dispose();
        sonidoObstaculos.dispose();
        sonidoPasos.dispose();
        sonidoColisionEnemigo.dispose();
        jugador.img.dispose();
	}

	private void renderizarJuego(){
        //Ponemos el color del fondo a negro
        Gdx.gl.glClearColor(0,0,0,1);
        //Borramos la pantalla
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Actualizamos la cámara del juego
        camara.position.set(jugador.jugadorX, jugador.jugadorY, 0f);

        camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth/2f,
                anchoMapa-camara.viewportWidth/2f);

        camara.position.y = MathUtils.clamp(camara.position.y, camara.viewportHeight/2f,
                altoMapa-camara.viewportHeight/2f);

        camara.update();

        //Vinculamos el objeto de dibuja el TiledMap con la cámara del juego
        mapaRenderer.setView(camara);
        //Dibujamos el TiledMap
        int[] capas = {0,1,2,3};
        mapaRenderer.render(capas);

        // extraemos el tiempo de la última actualización del sprite y la acumulamos a stateTime.
        jugador.stateTime += Gdx.graphics.getDeltaTime();
        magos.stateTimeMago +=Gdx.graphics.getDeltaTime();

        // le indicamos al SpriteBatch que se muestre en el sistema de coordenadas
        // específicas de la cámara.
        sb.setProjectionMatrix(camara.combined);

        //Inicializamos el objeto SpriteBatch
        sb.begin();

        //Pintamos el objeto Sprite a través del objeto SpriteBatch
        sb.draw(jugador.cuadroActual, jugador.jugadorX, jugador.jugadorY);

        //Renderizar enemigos
        for(int i=0; i<magos.mago.length; i++){
            magos.actualizarNPC(i);
            sb.draw(magos.cuadroActual,magos.magoX[i],magos.magoY[i]);
        }

        for(int i=0; i<monedas.posicionX.length; i++){
            sb.draw(monedas.textura, monedas.posicionX[i], monedas.posicionY[i]);
        }

        //Finalizamos el objeto SpriteBatch
        sb.end();

        mapaRenderer.render(new int[]{4});
    }

    //variables de control de colisiones
    private boolean hasColisionadoAntes = false;
    private boolean hasColisionadoEnemigosAntes = false;
    private int posColision=-1;

    private void colisionMonedas(){
        //Rectangulo del jugador
        Rectangle rJugador = new Rectangle(jugador.jugadorX, jugador.jugadorY , jugador.anchoJugador, jugador.altoJugador);

        Rectangle rMoneda;

        for(int i=0; i<monedas.posicionX.length; i++){

            rMoneda = new Rectangle(monedas.posicionX[i], monedas.posicionY[i], monedas.anchoMoneda, monedas.altoMoneda);

            if(rJugador.overlaps(rMoneda) && !monedas.conseguidas[i]){
                sonidoMoneda.play(0.5f);
                monedas.conseguidas[i]=true;
                jugador.monedasConseguidas++;
            }

        }

        if(jugador.monedasConseguidas>=3){

            //Al conseguir las tres monedas te vas al principio y suena la musica de victoria
            jugador.vida=3;
            jugador.monedasConseguidas=0;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    musicaFondo.stop();
                    sonidoVictoria.play(0.4f);
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    musicaFondo.play();
                }
            }).start();

            for(int i=0; i<monedas.conseguidas.length; i++){
                monedas.conseguidas[i]=false;
            }

            jugador.jugadorX = jugador.posicionInicialX;
            jugador.jugadorY = jugador.posicionInicialY;
        }
    }




    private void colisionesConEnemigos(){
	    //Rectangulo del jugador
	    Rectangle rJugador = new Rectangle(jugador.jugadorX, jugador.jugadorY , jugador.anchoJugador, jugador.altoJugador);

	    //Rectangulo de los enemigos
	    Rectangle rMago;

	    for(int i=0; i<magos.mago.length; i++){

	        //instanciar el rectangulo de uno de los magos
	        rMago = new Rectangle(magos.magoX[i], magos.magoY[i], magos.anchoMago, magos.altoMago);

	        //Comprobar si se solapan
            if(rJugador.overlaps(rMago) ){
                //Hacer lo que haya que hacer cuando hay colision
                if(!hasColisionadoEnemigosAntes || posColision!=i){
                    sonidoColisionEnemigo.play(0.7f);
                    hasColisionadoEnemigosAntes=true;
                    posColision = i;
                    if(rJugador.getX()<rMago.getX())
                        jugador.jugadorX-=15;
                    else if(rJugador.getX()>rMago.getX())
                        jugador.jugadorX+=15;

                    if(rJugador.getY()<rMago.getY())
                        jugador.jugadorY-=15;
                    else if(rJugador.getY()<rMago.getY())
                        jugador.jugadorY+=15;

                    jugador.vida--;

                    if(jugador.vida<=0){

                        //al quedarse sin vidas el jugador suena una musica de game over
                        //y devuelve a tres la vida
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                musicaFondo.stop();
                                sonidoGameOver.play(0.5f);
                                jugador.vida=3;
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                musicaFondo.play();
                            }
                        }).start();

                        jugador.jugadorX = jugador.posicionInicialX;
                        jugador.jugadorY = jugador.posicionInicialY;
                    }


                }
            }

        }


    }


    public boolean colisiones(){
        if ((jugador.jugadorX < 0 || jugador.jugadorY < 0 ||
                jugador.jugadorX > (anchoMapa - jugador.anchoJugador) ||

                jugador.jugadorY > (altoMapa - jugador.altoJugador)) ||

                (obstaculo[(int) ((jugador.jugadorX + jugador.anchoJugador/4) / anchoBaldosa)][((int) (jugador.jugadorY) / altoBaldosa)])

                || (obstaculo[(int) ((jugador.jugadorX + 3*jugador.anchoJugador/4) / anchoBaldosa)]

                [((int) (jugador.jugadorY) / altoBaldosa)])){

            if(!hasColisionadoAntes){
                sonidoObstaculos.play();
                hasColisionadoAntes=true;
            }

            return true;
        }else{
            hasColisionadoAntes=false;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT || keycode == Input.Keys.DOWN || keycode == Input.Keys.UP)
            sonidoPasos.loop(0.2f);

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT || keycode == Input.Keys.DOWN || keycode == Input.Keys.UP){
            sonidoPasos.stop();
            hasColisionadoAntes=false;
            hasColisionadoEnemigosAntes=false;
            posColision=-1;
        }


        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //recoger las coordenadas que hemos pinchado en la pantalla

        jugador.tactilX = screenX;

        jugador.tactilY = screenY;

        sonidoPasos.loop(0.3f);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        sonidoPasos.stop();
        hasColisionadoAntes=false;
        hasColisionadoEnemigosAntes=false;
        posColision=-1;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
