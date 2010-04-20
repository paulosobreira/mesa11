package br.mesa11.visao;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
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
import br.recursos.CarregadorRecursos;

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
		setarHints((Graphics2D) g);
		desenhaCampo(g);
		if (botoes != null) {
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				if (zero.equals(id)) {
					continue;
				}
				Botao botao = (Botao) botoes.get(id);
				g.setColor(Color.RED);
				if (botao.getDestino() != null) {
					g.drawLine((int) (botao.getCentro().x * zoom), (int) (botao
							.getCentro().y * zoom),
							(int) (botao.getDestino().x * zoom), (int) (botao
									.getDestino().y * zoom));
				}
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
		if (jogada != null) {
			for (Iterator iterator = jogada.iterator(); iterator.hasNext();) {
				Point point = (Point) iterator.next();
				g.setClip(null);
				g
						.drawOval((int) (point.x * zoom),
								(int) (point.y * zoom), 1, 1);
			}
		}

	}

	private void desenhaGoleiro(Goleiro goleiro, Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.fill(goleiro.getRetangulo(zoom));

	}

	private void desenhaBotao(Botao botao, Graphics g) {
		if (botao == null)
			return;
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

		// Ellipse2D externo = new Ellipse2D.Float(
		// (int) (botao.getPosition().x * ZOOM), (int) (botao
		// .getPosition().y * ZOOM),
		// (int) (botao.getDiamentro() * ZOOM), (int) (botao
		// .getDiamentro() * ZOOM));

		// g.setClip(externo);

		BufferedImage newBuffer = new BufferedImage(
				(int) (botaoImg.getWidth() * zoom),
				(int) (botaoImg.getHeight() * zoom),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
		Ellipse2D externo = new Ellipse2D.Double(0, 0,
				(botao.getDiamentro() * zoom), (botao.getDiamentro() * zoom));
		Ellipse2D interno = new Ellipse2D.Double(((23 * zoom)), ((22) * zoom),
				(85 * zoom), (85 * zoom));
		graphics2d.setClip(externo);
		graphics2d.drawImage(zoomBuffer, 0, 0, null);
		// graphics2d.setComposite(AlphaComposite.getInstance(
		// AlphaComposite.CLEAR, 0.5f));
		// graphics2d.fill(interno);
		g.drawImage(newBuffer, (int) (botao.getPosition().x * zoom),
				(int) (botao.getPosition().y * zoom), null);
	}

	private void desenhaCampo(Graphics g) {
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (int) ((LARGURA_MESA) * zoom),
				(int) ((ALTURA_MESA) * zoom));
		/**
		 * Campo
		 */
		g.setColor(Color.white);
		g.fillRect((int) (BORDA_CAMPO * zoom), (int) (BORDA_CAMPO * zoom),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				(int) ((ALTURA_MESA - DOBRO_BORDA_CAMPO) * zoom));

		g.setColor(Color.green);
		g.fillRect((int) ((BORDA_CAMPO + LINHA) * zoom),
				(int) ((BORDA_CAMPO + LINHA) * zoom), (int) ((LARGURA_MESA
						- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				(int) ((ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom));
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
		g.fillOval((int) (x * zoom), (int) (y * zoom),
				(int) (RAIO_CENTRO * zoom), (int) (RAIO_CENTRO * zoom));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * zoom), (int) ((y + LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		/**
		 * Meia lua de Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		g.fillOval((int) (x * zoom), (int) (y * zoom),
				(int) (RAIO_CENTRO * zoom), (int) (RAIO_CENTRO * zoom));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * zoom), (int) ((y + LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		/**
		 * GdeArae Cima
		 */
		g.setColor(Color.white);
		g.fillRect((int) (ALTURA_GDE_AREA * zoom), (int) (BORDA_CAMPO * zoom),
				(int) ((LARGURA_GDE_AREA) * zoom),
				(int) ((ALTURA_GDE_AREA) * zoom));
		g.setColor(Color.green);
		g.fillRect((int) ((ALTURA_GDE_AREA + LINHA) * zoom),
				(int) ((BORDA_CAMPO + LINHA) * zoom),
				(int) ((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				(int) ((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		/**
		 * GdeArae Baixo
		 */
		g.setColor(Color.white);
		g.fillRect((int) (ALTURA_GDE_AREA * zoom), (int) ((ALTURA_MESA
				- BORDA_CAMPO - ALTURA_GDE_AREA) * zoom),
				(int) ((LARGURA_GDE_AREA) * zoom),
				(int) ((ALTURA_GDE_AREA) * zoom));
		g.setColor(Color.green);
		g
				.fillRect(
						(int) ((ALTURA_GDE_AREA + LINHA) * zoom),
						(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA + LINHA) * zoom),
						(int) ((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
						(int) ((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		/**
		 * PQArae Cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		g.fillRect((int) (x * zoom), (int) (BORDA_CAMPO * zoom),
				(int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		g.setColor(Color.green);
		g.fillRect((int) ((x + LINHA) * zoom),
				(int) ((BORDA_CAMPO + LINHA) * zoom),
				(int) ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				(int) ((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		/**
		 * PQArae Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		g.fillRect((int) (x * zoom), (int) (y * zoom),
				(int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		g.setColor(Color.green);
		g.fillRect((int) ((x + LINHA) * zoom), (int) ((y + LINHA) * zoom),
				(int) ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				(int) ((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		/**
		 * Circulo Centro
		 */
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();
		g.fillOval((int) (x * zoom), (int) (y * zoom),
				(int) (RAIO_CENTRO * zoom), (int) (RAIO_CENTRO * zoom));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * zoom), (int) ((y + LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		/**
		 * meio de campo
		 */
		g.setColor(Color.white);
		g.fillRect((int) ((BORDA_CAMPO) * zoom),
				(int) ((ALTURA_MESA / 2) * zoom),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				(int) (LINHA * zoom));

		/**
		 * Penalti cima
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * zoom),
				(int) ((BORDA_CAMPO + PENALTI) * zoom),
				(int) (DOBRO_LINHA * zoom), (int) (DOBRO_LINHA * zoom));

		/**
		 * Penalti Baixo
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * zoom), (int) ((ALTURA_MESA
				- BORDA_CAMPO - PENALTI) * zoom), (int) (DOBRO_LINHA * zoom),
				(int) (DOBRO_LINHA * zoom));
		/**
		 * Centro
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * zoom),
				(int) (((ALTURA_MESA - LINHA) / 2) * zoom),
				(int) (DOBRO_LINHA * zoom), (int) (DOBRO_LINHA * zoom));
		Graphics2D graphics2D = (Graphics2D) g;
		desengaGol(graphics2D);
		desenhaFiguras(graphics2D);
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
		g.setColor(Color.BLACK);

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
}
