package br.mesa11;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class MesaPanel extends JPanel {

	public static final int LARGURA_MESA = 3430;
	public static final int ALTURA_MESA = 5286;
	public static final int BORDA_CAMPO = 230;
	public static final int DOBRO_BORDA_CAMPO = BORDA_CAMPO * 2;
	private static final int LINHA = 10;
	private static final int DOBRO_LINHA = LINHA * 2;
	public static final int LARGURA_GDE_AREA = 1714;
	public static final int ALTURA_GDE_AREA = 857;
	public static final int LARGURA_PQ_AREA = ALTURA_GDE_AREA;
	public static final int ALTURA_PQ_AREA = 314;
	public static final int RAIO_CENTRO = 1000;
	public static final int PENALTI = 586;
	public static int ZOOM = 1;
	public static Rectangle campoCima;
	public static Rectangle campoBaixo;
	public static Rectangle grandeAreaCima;
	public static Rectangle grandeAreaBaixo;
	public static Rectangle pequenaAreaCima;
	public static Rectangle pequenaAreaBaixo;
	public static Rectangle centro;
	public static Rectangle penaltyCima;
	public static Rectangle penaltyBaixo;

	public MesaPanel() {
		setSize(LARGURA_MESA, ALTURA_MESA);
		campoCima = new Rectangle(0, 0);
		campoBaixo = new Rectangle(0, 0);
		grandeAreaCima = new Rectangle(0, 0);
		grandeAreaBaixo = new Rectangle(0, 0);
		pequenaAreaCima = new Rectangle(0, 0);
		pequenaAreaBaixo = new Rectangle(0, 0);
		centro = new Rectangle(0, 0);
		penaltyCima = new Rectangle(0, 0);
		penaltyBaixo = new Rectangle(0, 0);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(LARGURA_MESA / ZOOM, ALTURA_MESA / ZOOM);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, LARGURA_MESA / ZOOM, ALTURA_MESA / ZOOM);
		// Campo
		g.setColor(Color.white);
		g.fillRect(BORDA_CAMPO / ZOOM, BORDA_CAMPO / ZOOM,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO) / ZOOM,
				(ALTURA_MESA - DOBRO_BORDA_CAMPO) / ZOOM);

		g.setColor(Color.green);
		g.fillRect((BORDA_CAMPO + LINHA) / ZOOM, (BORDA_CAMPO + LINHA) / ZOOM,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) / ZOOM,
				(ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) / ZOOM);
		// Meia lua de cima
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = BORDA_CAMPO;
		g.fillOval(x / ZOOM, y / ZOOM, RAIO_CENTRO / ZOOM, RAIO_CENTRO / ZOOM);
		g.setColor(Color.green);
		g.fillOval((x + LINHA) / ZOOM, (y + LINHA) / ZOOM,
				(RAIO_CENTRO - DOBRO_LINHA) / ZOOM, (RAIO_CENTRO - DOBRO_LINHA)
						/ ZOOM);
		// Meia lua de Baixo
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		g.fillOval(x / ZOOM, y / ZOOM, RAIO_CENTRO / ZOOM, RAIO_CENTRO / ZOOM);
		g.setColor(Color.green);
		g.fillOval((x + LINHA) / ZOOM, (y + LINHA) / ZOOM,
				(RAIO_CENTRO - DOBRO_LINHA) / ZOOM, (RAIO_CENTRO - DOBRO_LINHA)
						/ ZOOM);
		// GdeArae Cima
		g.setColor(Color.white);
		g.fillRect(ALTURA_GDE_AREA / ZOOM, BORDA_CAMPO / ZOOM,
				(LARGURA_GDE_AREA) / ZOOM, (ALTURA_GDE_AREA) / ZOOM);
		g.setColor(Color.green);
		g.fillRect((ALTURA_GDE_AREA + LINHA) / ZOOM, (BORDA_CAMPO + LINHA)
				/ ZOOM, (LARGURA_GDE_AREA - DOBRO_LINHA) / ZOOM,
				(ALTURA_GDE_AREA - DOBRO_LINHA) / ZOOM);
		// GdeArae Baixo
		g.setColor(Color.white);
		g.fillRect(ALTURA_GDE_AREA / ZOOM,
				(ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) / ZOOM,
				(LARGURA_GDE_AREA) / ZOOM, (ALTURA_GDE_AREA) / ZOOM);
		g.setColor(Color.green);
		g.fillRect((ALTURA_GDE_AREA + LINHA) / ZOOM, (ALTURA_MESA - BORDA_CAMPO
				- ALTURA_GDE_AREA + LINHA)
				/ ZOOM, (LARGURA_GDE_AREA - DOBRO_LINHA) / ZOOM,
				(ALTURA_GDE_AREA - DOBRO_LINHA) / ZOOM);
		// PQArae Cima
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		g.fillRect(x / ZOOM, BORDA_CAMPO / ZOOM, (LARGURA_PQ_AREA) / ZOOM,
				(ALTURA_PQ_AREA) / ZOOM);
		g.setColor(Color.green);
		g.fillRect((x + LINHA) / ZOOM, (BORDA_CAMPO + LINHA) / ZOOM,
				(LARGURA_PQ_AREA - DOBRO_LINHA) / ZOOM,
				(ALTURA_PQ_AREA - DOBRO_LINHA) / ZOOM);
		// PQArae Baixo
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		g.fillRect(x / ZOOM, y / ZOOM, (LARGURA_PQ_AREA) / ZOOM,
				(ALTURA_PQ_AREA) / ZOOM);
		g.setColor(Color.green);
		g.fillRect((x + LINHA) / ZOOM, (y + LINHA) / ZOOM,
				(LARGURA_PQ_AREA - DOBRO_LINHA) / ZOOM,
				(ALTURA_PQ_AREA - DOBRO_LINHA) / ZOOM);
		// Circulo Centro
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();
		g.fillOval(x / ZOOM, y / ZOOM, RAIO_CENTRO / ZOOM, RAIO_CENTRO / ZOOM);
		g.setColor(Color.green);
		g.fillOval((x + LINHA) / ZOOM, (y + LINHA) / ZOOM,
				(RAIO_CENTRO - DOBRO_LINHA) / ZOOM, (RAIO_CENTRO - DOBRO_LINHA)
						/ ZOOM);
		// meio de campo
		g.setColor(Color.white);
		g.fillRect((BORDA_CAMPO) / ZOOM, (ALTURA_MESA / 2) / ZOOM,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO) / ZOOM, LINHA / ZOOM);

		// Penalti cima
		g.setColor(Color.white);
		g.fillOval((LARGURA_MESA / 2) / ZOOM, (BORDA_CAMPO + PENALTI) / ZOOM,
				DOBRO_LINHA / ZOOM, DOBRO_LINHA / ZOOM);

		// Penalti Baixo
		g.setColor(Color.white);
		g.fillOval((LARGURA_MESA / 2) / ZOOM,
				(ALTURA_MESA - BORDA_CAMPO - PENALTI) / ZOOM, DOBRO_LINHA
						/ ZOOM, DOBRO_LINHA / ZOOM);
		// Centro
		g.setColor(Color.white);
		g.fillOval((LARGURA_MESA / 2) / ZOOM, ((ALTURA_MESA - LINHA) / 2)
				/ ZOOM, DOBRO_LINHA / ZOOM, DOBRO_LINHA / ZOOM);
		Graphics2D graphics2D = (Graphics2D) g;
		desenhaFiguras(graphics2D);
	}

	private void desenhaFiguras(Graphics2D g) {
		g.setColor(Color.BLACK);
		campoCima.setBounds(BORDA_CAMPO / ZOOM, BORDA_CAMPO / ZOOM,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO) / ZOOM,
				((ALTURA_MESA / 2) - BORDA_CAMPO) / ZOOM);
		campoBaixo.setBounds(BORDA_CAMPO / ZOOM, ((ALTURA_MESA / 2)) / ZOOM,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO) / ZOOM,
				((ALTURA_MESA / 2) - BORDA_CAMPO) / ZOOM);
		grandeAreaCima.setBounds(ALTURA_GDE_AREA / ZOOM, BORDA_CAMPO / ZOOM,
				(LARGURA_GDE_AREA) / ZOOM, (ALTURA_GDE_AREA) / ZOOM);

		grandeAreaBaixo.setBounds(ALTURA_GDE_AREA / ZOOM, (ALTURA_MESA
				- BORDA_CAMPO - ALTURA_GDE_AREA)
				/ ZOOM, (LARGURA_GDE_AREA) / ZOOM, (ALTURA_GDE_AREA) / ZOOM);

		pequenaAreaCima.setBounds((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2)
				/ ZOOM, BORDA_CAMPO / ZOOM, (LARGURA_PQ_AREA) / ZOOM,
				(ALTURA_PQ_AREA) / ZOOM);
		pequenaAreaBaixo.setBounds((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2)
				/ ZOOM, (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA) / ZOOM,
				(LARGURA_PQ_AREA) / ZOOM, (ALTURA_PQ_AREA) / ZOOM);

		centro.setBounds((LARGURA_MESA / 2) / ZOOM, (ALTURA_MESA / 2) / ZOOM,
				DOBRO_LINHA / ZOOM, DOBRO_LINHA / ZOOM);

		penaltyCima.setBounds((LARGURA_MESA / 2) / ZOOM,
				(BORDA_CAMPO + PENALTI) / ZOOM, DOBRO_LINHA / ZOOM, DOBRO_LINHA
						/ ZOOM);
		penaltyBaixo.setBounds((LARGURA_MESA / 2) / ZOOM, (ALTURA_MESA
				- BORDA_CAMPO - PENALTI)
				/ ZOOM, DOBRO_LINHA / ZOOM, DOBRO_LINHA / ZOOM);
		g.draw(campoCima);
		g.draw(campoBaixo);
		g.draw(grandeAreaCima);
		g.draw(grandeAreaBaixo);
		g.draw(pequenaAreaCima);
		g.draw(pequenaAreaBaixo);
		g.draw(centro);
		g.draw(penaltyCima);
		g.draw(penaltyBaixo);

	}

	private int calculaYcentro() {
		return (ALTURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	private int calculaXcentro() {
		return (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	public Point pointCentro() {

		int x = calculaXcentro() / ZOOM;
		int y = calculaYcentro() / ZOOM;
		Point point = new Point(x, y);
		System.out.println(point);
		return point;
	}
}
