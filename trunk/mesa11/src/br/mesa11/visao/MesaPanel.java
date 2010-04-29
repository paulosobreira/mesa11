package br.mesa11.visao;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Util;

public class MesaPanel extends JPanel {

	public static final Long zero = new Long(0);
	public final static Color green2 = new Color(0, 200, 0);
	public static final String MUTEX = "MUTEX";
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
	public static final int FAIXAS = 14;
	public static final int ALTURA_FAIXA = (ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA)
			/ FAIXAS;

	public double zoom = 1;
	private Rectangle campoCima;
	private Rectangle campoBaixo;
	private Rectangle grandeAreaCima;
	private Rectangle grandeAreaBaixo;
	private Rectangle pequenaAreaCima;
	private Rectangle pequenaAreaBaixo;
	private Rectangle centro;
	private Rectangle penaltyCima;
	private Rectangle penaltyBaixo;
	private Rectangle hasteDireitaGolCima;
	private Rectangle hasteEsquerdaGolCima;
	private Rectangle hasteTopoGolCima;

	private Rectangle hasteDireitaGolBaixo;
	private Rectangle hasteEsquerdaGolBaixo;
	private Rectangle hasteTopoGolBaixo;

	private ControleJogo controleJogo;

	private Map botoes;
	private List jogada;
	private Rectangle2D zoomedMesa;
	private Rectangle2D zoomedBorda;
	private Rectangle2D zoomedGrama;
	private Ellipse2D zoomedMeiaLuaCimaBorda;
	private Ellipse2D zoomedMeiaLuaCimaGrama;
	private Ellipse2D zoomedMeiaLuaBaixoBorda;
	private Ellipse2D zoomedMeiaLuaBaixoGrama;
	private Rectangle2D zoomedGdeAreaCimaBorda;
	private Rectangle2D zoomedGdeAreaCimaGrama;
	private Rectangle2D zoomedGdeAreaBaixoBorda;
	private Rectangle2D zoomedGdeAreaBaixoGrama;
	private Rectangle2D zoomedpqAreaCimaBorda;
	private Rectangle2D zoomedpqAreaCimaGrama;
	private Rectangle2D zoomedpqAreaBaixoBorda;
	private Rectangle2D zoomedpqAreaBaixoGrama;
	private Ellipse2D zoomedcentroBorda;
	private Ellipse2D zoomedcentroGrama;
	private Double zoomedMeioCampoBorda;
	private Double zoomedPenaltiCima;
	private Double zoomedPenaltiBaixo;
	private Double zoomedCentro;

	public Rectangle getHasteTopoGolCima() {
		return hasteTopoGolCima;
	}

	public Rectangle getCampoCima() {
		return campoCima;
	}

	public Rectangle getCampoBaixo() {
		return campoBaixo;
	}

	public Rectangle getGrandeAreaCima() {
		return grandeAreaCima;
	}

	public Rectangle getGrandeAreaBaixo() {
		return grandeAreaBaixo;
	}

	public Rectangle getPequenaAreaCima() {
		return pequenaAreaCima;
	}

	public Rectangle getPequenaAreaBaixo() {
		return pequenaAreaBaixo;
	}

	public Rectangle getCentro() {
		return centro;
	}

	public Rectangle getPenaltyCima() {
		return penaltyCima;
	}

	public Rectangle getPenaltyBaixo() {
		return penaltyBaixo;
	}

	public MesaPanel(ControleJogo controleJogo) {
		setSize(LARGURA_MESA * 2, ALTURA_MESA * 2);
		campoCima = new Rectangle((int) (BORDA_CAMPO * zoom),
				(int) (BORDA_CAMPO * zoom),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				(int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * zoom));
		campoBaixo = new Rectangle((int) (BORDA_CAMPO * zoom),
				(int) (((ALTURA_MESA / 2)) * zoom),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				(int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * zoom));
		grandeAreaCima = new Rectangle((int) (ALTURA_GDE_AREA * zoom),
				(int) (BORDA_CAMPO * zoom), (int) ((LARGURA_GDE_AREA) * zoom),
				(int) ((ALTURA_GDE_AREA) * zoom));
		grandeAreaBaixo = new Rectangle((int) (ALTURA_GDE_AREA * zoom),
				(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * zoom),
				(int) ((LARGURA_GDE_AREA) * zoom),
				(int) ((ALTURA_GDE_AREA) * zoom));
		pequenaAreaCima = new Rectangle(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
				(int) (BORDA_CAMPO * zoom), (int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		pequenaAreaBaixo = new Rectangle(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
				(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA) * zoom),
				(int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		centro = new Rectangle((int) ((LARGURA_MESA / 2) * zoom),
				(int) ((ALTURA_MESA / 2) * zoom), (int) (DOBRO_LINHA * zoom),
				(int) (DOBRO_LINHA * zoom));
		penaltyCima = new Rectangle((int) ((LARGURA_MESA / 2) * zoom),
				(int) ((BORDA_CAMPO + PENALTI) * zoom),
				(int) (DOBRO_LINHA * zoom), (int) (DOBRO_LINHA * zoom));
		penaltyBaixo = new Rectangle((int) ((LARGURA_MESA / 2) * zoom),
				(int) ((ALTURA_MESA - BORDA_CAMPO - PENALTI) * zoom),
				(int) (DOBRO_LINHA * zoom), (int) (DOBRO_LINHA * zoom));
		hasteDireitaGolCima = new Rectangle(Util.inte(pequenaAreaCima.getX()
				+ pequenaAreaCima.getWidth() - (110 * zoom)), Util
				.inte(pequenaAreaCima.getY() + (LINHA * zoom)
						- (pequenaAreaCima.getHeight() * .50)), Util
				.inte(10 * zoom), Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteEsquerdaGolCima = new Rectangle(Util.inte(pequenaAreaCima.getX()
				+ (110 * zoom)), Util.inte(pequenaAreaCima.getY()
				+ (LINHA * zoom) - (pequenaAreaCima.getHeight() * .50)), Util
				.inte(10 * zoom), Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteTopoGolCima = new Rectangle(
				Util.inte(hasteEsquerdaGolCima.getX()), Util
						.inte(hasteDireitaGolCima.getY()), Util
						.inte(hasteDireitaGolCima.getX()
								- hasteEsquerdaGolCima.getX()), Util
						.inte(10 * zoom));
		hasteDireitaGolBaixo = new Rectangle(Util.inte(hasteDireitaGolCima
				.getX()), Util.inte(pequenaAreaBaixo.getY() - (LINHA * zoom)
				+ pequenaAreaBaixo.getHeight()), Util.inte(10 * zoom), Util
				.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		hasteEsquerdaGolBaixo = new Rectangle(Util.inte(hasteEsquerdaGolCima
				.getX()), Util.inte(pequenaAreaBaixo.getY() - (LINHA * zoom)
				+ pequenaAreaBaixo.getHeight()), Util.inte(10 * zoom), Util
				.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		hasteTopoGolBaixo = new Rectangle(Util.inte(hasteEsquerdaGolBaixo
				.getX()), Util.inte(hasteDireitaGolBaixo.getY()
				+ hasteDireitaGolBaixo.getHeight()), Util
				.inte(hasteDireitaGolBaixo.getX()
						- hasteEsquerdaGolBaixo.getX() + Util.inte(10 * zoom)),
				Util.inte(10 * zoom));
		this.controleJogo = controleJogo;
		this.botoes = controleJogo.getBotoes();
		this.jogada = controleJogo.getJogada();

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) (LARGURA_MESA), (int) (ALTURA_MESA));
	}

	private void setarHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		setarHints(g2d);
		desenhaCampo(g2d);
		desengaGol(g2d);
		desenhaFiguras(g2d);
		if (botoes != null) {
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				if (zero.equals(id)) {
					continue;
				}
				Botao botao = (Botao) botoes.get(id);
				// g.setColor(Color.RED);
				// if (botao.getDestino() != null) {
				// g.drawLine((int) (botao.getCentro().x * zoom), (int) (botao
				// .getCentro().y * zoom),
				// (int) (botao.getDestino().x * zoom), (int) (botao
				// .getDestino().y * zoom));
				// }
				if (botao instanceof Goleiro) {
					Goleiro goleiro = (Goleiro) botao;
					desenhaGoleiro(goleiro, g);
				} else {
					desenhaBotao(botao, g);
				}
				// g.drawOval((int) (botao.getPosition().x * ZOOM), (int) (botao
				// .getPosition().y * ZOOM),
				// (int) (botao.getDiamentro() * ZOOM), (int) (botao
				// .getDiamentro() * ZOOM));

			}
			desenhaBotao((Botao) botoes.get(new Long(0)), g);
		}
		// if (jogada != null) {
		// for (Iterator iterator = jogada.iterator(); iterator.hasNext();) {
		// Point point = (Point) iterator.next();
		// g.setClip(null);
		// g
		// .drawOval((int) (point.x * zoom),
		// (int) (point.y * zoom), 1, 1);
		// }
		// }
		// Graphics2D g2d = (Graphics2D) g;
		// if (controleJogo.limitesViewPort() != null)
		// g2d.draw(controleJogo.limitesViewPort());
	}

	private void desenhaGoleiro(Goleiro goleiro, Graphics g) {
		if (!controleJogo.limitesViewPort().contains(
				new Point(Util.inte(goleiro.getCentro().x * zoom), Util
						.inte(goleiro.getCentro().y * zoom)))) {
			return;
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		Shape goleroShape = goleiro.getRetangulo(zoom);
		g2d.fill(goleroShape);

	}

	private void desenhaBotao(Botao botao, Graphics g) {
		if (botao == null)
			return;
		int botx = (int) (botao.getPosition().x * zoom);
		int boty = (int) (botao.getPosition().y * zoom);
		if (!controleJogo.limitesViewPort().contains(
				new Point(botx + botao.getRaio(), boty + botao.getRaio()))) {
			return;
		}
		AffineTransform affineTransform = AffineTransform.getScaleInstance(
				zoom, zoom);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage botaoImg = botao.getImgBotao();
		if (botaoImg == null)
			return;
		BufferedImage zoomBuffer = new BufferedImage(
				(int) (botaoImg.getWidth() * zoom),
				(int) (botaoImg.getHeight() * zoom),
				BufferedImage.TYPE_INT_ARGB);

		affineTransformOp.filter(botaoImg, zoomBuffer);
		BufferedImage newBuffer = new BufferedImage(
				(int) (botaoImg.getWidth() * zoom),
				(int) (botaoImg.getHeight() * zoom),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
		Ellipse2D externo = new Ellipse2D.Double(0, 0,
				(botao.getDiamentro() * zoom), (botao.getDiamentro() * zoom));
		graphics2d.setClip(externo);
		graphics2d.drawImage(zoomBuffer, 0, 0, null);
		g.drawImage(newBuffer, botx, boty, null);
	}

	private void desenhaCampo(Graphics2D g) {
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		zoomedMesa = new Rectangle2D.Double(0, 0, ((LARGURA_MESA) * zoom),
				((ALTURA_MESA) * zoom));
		g.draw(zoomedMesa);
		/**
		 * Campo
		 */
		g.setColor(Color.white);
		zoomedBorda = new Rectangle2D.Double((BORDA_CAMPO * zoom),
				(BORDA_CAMPO * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO) * zoom));
		g.draw(zoomedBorda);

		g.setColor(Color.green);
		zoomedGrama = new Rectangle2D.Double(((BORDA_CAMPO + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom), ((LARGURA_MESA
						- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom));

		g.draw(zoomedGrama);
		int alturaBordaAtual = (BORDA_CAMPO + LINHA);
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				g.setColor(green2);
				g.fillRect((int) ((BORDA_CAMPO + LINHA) * zoom),
						(int) ((alturaBordaAtual) * zoom), (int) ((LARGURA_MESA
								- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
						(int) ((ALTURA_FAIXA) * zoom));
				// alturaBordaAtual += (ALTURA_FAIXA - LINHA);
				// continue;
			}
			alturaBordaAtual += (ALTURA_FAIXA);
		}
		/**
		 * Meia lua de cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = BORDA_CAMPO;
		zoomedMeiaLuaCimaBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		g.draw(zoomedMeiaLuaCimaBorda);
		g.setColor(Color.green);
		zoomedMeiaLuaCimaGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		g.draw(zoomedMeiaLuaCimaGrama);
		/**
		 * Meia lua de Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		zoomedMeiaLuaBaixoBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		g.draw(zoomedMeiaLuaBaixoBorda);
		g.setColor(Color.green);
		zoomedMeiaLuaBaixoGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		g.draw(zoomedMeiaLuaBaixoGrama);
		/**
		 * GdeArae Cima
		 */
		g.setColor(Color.white);
		zoomedGdeAreaCimaBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom), (BORDA_CAMPO * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));

		g.draw(zoomedGdeAreaCimaBorda);
		g.setColor(Color.green);
		zoomedGdeAreaCimaGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));

		g.draw(zoomedGdeAreaCimaGrama);
		/**
		 * GdeArae Baixo
		 */
		g.setColor(Color.white);
		zoomedGdeAreaBaixoBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom),
				((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));

		g.draw(zoomedGdeAreaBaixoBorda);
		g.setColor(Color.green);
		zoomedGdeAreaBaixoGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom), ((ALTURA_MESA - BORDA_CAMPO
						- ALTURA_GDE_AREA + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		g.draw(zoomedGdeAreaBaixoGrama);
		/**
		 * PQArae Cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);

		zoomedpqAreaCimaBorda = new Rectangle2D.Double((x * zoom),
				(BORDA_CAMPO * zoom), ((LARGURA_PQ_AREA) * zoom),
				((ALTURA_PQ_AREA) * zoom));
		g.draw(zoomedpqAreaCimaBorda);
		g.setColor(Color.green);
		zoomedpqAreaCimaGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		g.draw(zoomedpqAreaCimaGrama);
		/**
		 * PQArae Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		zoomedpqAreaBaixoBorda = new Rectangle2D.Double((x * zoom), (y * zoom),
				((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom));
		g.draw(zoomedpqAreaBaixoBorda);
		g.setColor(Color.green);
		zoomedpqAreaBaixoGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		g.draw(zoomedpqAreaBaixoGrama);
		/**
		 * Circulo Centro
		 */
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();
		zoomedcentroBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		g.draw(zoomedcentroBorda);
		g.setColor(Color.green);
		zoomedcentroGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		g.draw(zoomedcentroGrama);
		/**
		 * meio de campo
		 */
		g.setColor(Color.white);
		zoomedMeioCampoBorda = new Rectangle2D.Double(((BORDA_CAMPO) * zoom),
				((ALTURA_MESA / 2) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom), (LINHA * zoom));
		g.draw(zoomedMeioCampoBorda);

		/**
		 * Penalti cima
		 */
		g.setColor(Color.white);
		zoomedPenaltiCima = new Rectangle2D.Double(((LARGURA_MESA / 2) * zoom),
				((BORDA_CAMPO + PENALTI) * zoom), (DOBRO_LINHA * zoom),
				(int) (DOBRO_LINHA * zoom));
		g.draw(zoomedPenaltiCima);
		/**
		 * Penalti Baixo
		 */
		g.setColor(Color.white);
		zoomedPenaltiBaixo = new Rectangle2D.Double(
				((LARGURA_MESA / 2) * zoom),
				((ALTURA_MESA - BORDA_CAMPO - PENALTI) * zoom),
				(DOBRO_LINHA * zoom), (DOBRO_LINHA * zoom));
		g.draw(zoomedPenaltiBaixo);
		/**
		 * Centro
		 */
		g.setColor(Color.white);
		zoomedCentro = new Rectangle2D.Double(((LARGURA_MESA / 2) * zoom),
				(((ALTURA_MESA - LINHA) / 2) * zoom), (DOBRO_LINHA * zoom),
				(DOBRO_LINHA * zoom));
		g.draw(zoomedCentro);
	}

	private void desengaGol(Graphics2D g) {
		g.setColor(Color.black);
		Rectangle pequenaAreaCimaTemp = new Rectangle();
		pequenaAreaCimaTemp.setBounds(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
				(int) (BORDA_CAMPO * zoom), (int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		Rectangle pequenaAreaBaixoTemp = new Rectangle();
		pequenaAreaBaixoTemp.setBounds(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
				(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA) * zoom),
				(int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		Rectangle hasteEsquerdaGolCimaTemp = new Rectangle();
		hasteEsquerdaGolCimaTemp.setBounds(Util.inte(pequenaAreaCimaTemp.getX()
				+ (110 * zoom)), Util.inte(pequenaAreaCimaTemp.getY()
				+ (LINHA * zoom) - (pequenaAreaCimaTemp.getHeight() * .50)),
				Util.inte(10 * zoom), Util
						.inte(pequenaAreaCimaTemp.getHeight() * .50));
		g.fill(hasteEsquerdaGolCimaTemp);
		Rectangle hasteDireitaGolCimaTemp = new Rectangle();
		hasteDireitaGolCimaTemp.setBounds(Util.inte(pequenaAreaCimaTemp.getX()
				+ pequenaAreaCimaTemp.getWidth() - (110 * zoom)), Util
				.inte(pequenaAreaCimaTemp.getY() + (LINHA * zoom)
						- (pequenaAreaCimaTemp.getHeight() * .50)), Util
				.inte(10 * zoom), Util
				.inte(pequenaAreaCimaTemp.getHeight() * .50));
		g.fill(hasteDireitaGolCimaTemp);
		Rectangle hasteTopoGolCimaTemp = new Rectangle();
		hasteTopoGolCimaTemp.setBounds(Util.inte(hasteEsquerdaGolCimaTemp
				.getX()), Util.inte(hasteDireitaGolCimaTemp.getY()), Util
				.inte(hasteDireitaGolCimaTemp.getX()
						- hasteEsquerdaGolCimaTemp.getX()), Util
				.inte(10 * zoom));
		g.fill(hasteTopoGolCimaTemp);
		Rectangle hasteEsquerdaGolBaixoTemp = new Rectangle();
		hasteEsquerdaGolBaixoTemp.setBounds(Util.inte(hasteEsquerdaGolCimaTemp
				.getX()), Util.inte(pequenaAreaBaixoTemp.getY()
				- (LINHA * zoom) + pequenaAreaBaixoTemp.getHeight()), Util
				.inte(10 * zoom), Util.inte(pequenaAreaBaixoTemp.getHeight()
				- (pequenaAreaBaixoTemp.getHeight() * .50)));
		g.fill(hasteEsquerdaGolBaixoTemp);
		Rectangle hasteDireitaGolBaixoTemp = new Rectangle();
		hasteDireitaGolBaixoTemp.setBounds(Util.inte(hasteDireitaGolCimaTemp
				.getX()), Util.inte(pequenaAreaBaixoTemp.getY()
				- (LINHA * zoom) + pequenaAreaBaixoTemp.getHeight()), Util
				.inte(10 * zoom), Util.inte(pequenaAreaBaixoTemp.getHeight()
				- (pequenaAreaBaixoTemp.getHeight() * .50)));
		g.fill(hasteDireitaGolBaixoTemp);
		Rectangle hasteTopoGolBaixoTemp = new Rectangle();
		hasteTopoGolBaixoTemp.setBounds(Util.inte(hasteEsquerdaGolBaixoTemp
				.getX()), Util.inte(hasteDireitaGolBaixoTemp.getY()
				+ hasteDireitaGolBaixoTemp.getHeight()), Util
				.inte(hasteDireitaGolBaixoTemp.getX()
						- hasteEsquerdaGolBaixoTemp.getX()
						+ Util.inte(10 * zoom)), Util.inte(10 * zoom));
		g.fill(hasteTopoGolBaixoTemp);

	}

	private void desenhaFiguras(Graphics2D g) {
		// g.setColor(Color.BLACK);

		// campoCima.setBounds((int) (BORDA_CAMPO * ZOOM),
		// (int) (BORDA_CAMPO * ZOOM),
		// (int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
		// (int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * ZOOM));
		// campoBaixo.setBounds((int) (BORDA_CAMPO * ZOOM),
		// (int) (((ALTURA_MESA / 2)) * ZOOM),
		// (int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
		// (int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * ZOOM));
		// grandeAreaCima.setBounds((int) (ALTURA_GDE_AREA * ZOOM),
		// (int) (BORDA_CAMPO * ZOOM), (int) ((LARGURA_GDE_AREA) * ZOOM),
		// (int) ((ALTURA_GDE_AREA) * ZOOM));
		//
		// grandeAreaBaixo.setBounds((int) (ALTURA_GDE_AREA * ZOOM),
		// (int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * ZOOM),
		// (int) ((LARGURA_GDE_AREA) * ZOOM),
		// (int) ((ALTURA_GDE_AREA) * ZOOM));

		// centro.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
		// (int) ((ALTURA_MESA / 2) * ZOOM), (int) (DOBRO_LINHA * ZOOM),
		// (int) (DOBRO_LINHA * ZOOM));
		//
		// penaltyCima.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
		// (int) ((BORDA_CAMPO + PENALTI) * ZOOM),
		// (int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));
		// penaltyBaixo.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
		// (int) ((ALTURA_MESA - BORDA_CAMPO - PENALTI) * ZOOM),
		// (int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));

		// g.fill(campoCima);
		// g.fill(campoBaixo);
		// g.fill(grandeAreaCima);
		// g.fill(grandeAreaBaixo);
		// g.fill(pequenaAreaCima);
		// g.fill(pequenaAreaBaixo);
		// g.fill(centro);
		// g.fill(penaltyCima);
		// g.fill(penaltyBaixo);

	}

	private int calculaYcentro() {
		return (ALTURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	private int calculaXcentro() {
		return (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	public Point pointCentro() {

		return new Point((int) ((LARGURA_MESA / 2) * zoom)
				- (int) (DOBRO_LINHA * zoom / 2),
				(int) ((ALTURA_MESA / 2) * zoom)
						- (int) (DOBRO_LINHA * zoom / 2));
	}

	public boolean verificaIntersectsGol(Rectangle r) {
		return hasteDireitaGolBaixo.intersects(r)
				|| hasteDireitaGolCima.intersects(r)
				|| hasteEsquerdaGolBaixo.intersects(r)
				|| hasteEsquerdaGolCima.intersects(r)
				|| hasteTopoGolBaixo.intersects(r)
				|| hasteTopoGolCima.intersects(r);
	}

	public Point golBaixo() {
		Point p = new Point(Util.inte(getPenaltyBaixo().x), Util
				.inte(getPequenaAreaBaixo().getLocation().y
						+ getPequenaAreaBaixo().getHeight() + (LINHA * 2)));
		return p;
	}

	public Point golCima() {
		Point p = new Point(Util.inte(getPenaltyCima().x), Util
				.inte(getPequenaAreaCima().getLocation().y - 20));
		return p;
	}
}
