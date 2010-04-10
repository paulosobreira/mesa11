package br.mesa11.visao;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import br.hibernate.Botao;
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

	public static double ZOOM = 1;
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

	private Map botoes;
	private List jogada;

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

	public MesaPanel() {
		setSize(LARGURA_MESA * 2, ALTURA_MESA * 2);
		campoCima = new Rectangle(0, 0);
		campoBaixo = new Rectangle(0, 0);
		grandeAreaCima = new Rectangle(0, 0);
		grandeAreaBaixo = new Rectangle(0, 0);
		pequenaAreaCima = new Rectangle(0, 0);
		pequenaAreaBaixo = new Rectangle(0, 0);
		centro = new Rectangle(0, 0);
		penaltyCima = new Rectangle(0, 0);
		penaltyBaixo = new Rectangle(0, 0);
		hasteDireitaGolCima = new Rectangle(0, 0);
		hasteEsquerdaGolCima = new Rectangle(0, 0);
		hasteTopoGolCima = new Rectangle(0, 0);

		hasteDireitaGolBaixo = new Rectangle(0, 0);
		hasteEsquerdaGolBaixo = new Rectangle(0, 0);
		hasteTopoGolBaixo = new Rectangle(0, 0);
	}

	public MesaPanel(Map botoes, List jogada) {
		this();
		this.botoes = botoes;
		this.jogada = jogada;
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			botao.setImgBotao(CarregadorRecursos.carregaImg(botao.getImagem()));
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) (LARGURA_MESA), (int) (ALTURA_MESA));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		desenhaCampo(g);
		if (botoes != null) {
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				if (zero.equals(id)) {
					continue;
				}
				Botao botao = (Botao) botoes.get(id);
				g.setColor(Color.BLUE);
				g.fillOval((int) (botao.getCentro().x * ZOOM), (int) (botao
						.getCentro().y * ZOOM), 2, 2);
				g.setColor(Color.RED);
				if (botao.getDestino() != null) {
					g.drawLine((int) (botao.getCentro().x * ZOOM), (int) (botao
							.getCentro().y * ZOOM),
							(int) (botao.getDestino().x * ZOOM), (int) (botao
									.getDestino().y * ZOOM));
				}
				desenhaBotao(botao, g);
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
						.drawOval((int) (point.x * ZOOM),
								(int) (point.y * ZOOM), 1, 1);
			}
		}
	}

	private void desenhaBotao(Botao botao, Graphics g) {
		AffineTransform affineTransform = AffineTransform.getScaleInstance(
				ZOOM, ZOOM);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage botaoImg = botao.getImgBotao();
		if (botaoImg == null)
			return;
		BufferedImage zoomBuffer = new BufferedImage(
				(int) (botaoImg.getWidth() * ZOOM),
				(int) (botaoImg.getHeight() * ZOOM),
				BufferedImage.TYPE_INT_ARGB);

		affineTransformOp.filter(botaoImg, zoomBuffer);
		Ellipse2D ellipse2D = new Ellipse2D.Float(
				(int) (botao.getPosition().x * ZOOM), (int) (botao
						.getPosition().y * ZOOM),
				(int) (botao.getDiamentro() * ZOOM), (int) (botao
						.getDiamentro() * ZOOM));
		g.setClip(ellipse2D);
		g.drawImage(zoomBuffer, (int) (botao.getPosition().x * ZOOM),
				(int) (botao.getPosition().y * ZOOM), null);

	}

	private void desenhaCampo(Graphics g) {
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (int) ((LARGURA_MESA) * ZOOM),
				(int) ((ALTURA_MESA) * ZOOM));
		/**
		 * Campo
		 */
		g.setColor(Color.white);
		g.fillRect((int) (BORDA_CAMPO * ZOOM), (int) (BORDA_CAMPO * ZOOM),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
				(int) ((ALTURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM));

		g.setColor(Color.green);
		g.fillRect((int) ((BORDA_CAMPO + LINHA) * ZOOM),
				(int) ((BORDA_CAMPO + LINHA) * ZOOM), (int) ((LARGURA_MESA
						- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * ZOOM),
				(int) ((ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * ZOOM));
		int alturaBordaAtual = (BORDA_CAMPO + LINHA);
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				g.setColor(green2);
				g.fillRect((int) ((BORDA_CAMPO + LINHA) * ZOOM),
						(int) ((alturaBordaAtual) * ZOOM), (int) ((LARGURA_MESA
								- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * ZOOM),
						(int) ((ALTURA_FAIXA) * ZOOM));
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
		g.fillOval((int) (x * ZOOM), (int) (y * ZOOM),
				(int) (RAIO_CENTRO * ZOOM), (int) (RAIO_CENTRO * ZOOM));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * ZOOM), (int) ((y + LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM));
		/**
		 * Meia lua de Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		g.fillOval((int) (x * ZOOM), (int) (y * ZOOM),
				(int) (RAIO_CENTRO * ZOOM), (int) (RAIO_CENTRO * ZOOM));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * ZOOM), (int) ((y + LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM));
		/**
		 * GdeArae Cima
		 */
		g.setColor(Color.white);
		g.fillRect((int) (ALTURA_GDE_AREA * ZOOM), (int) (BORDA_CAMPO * ZOOM),
				(int) ((LARGURA_GDE_AREA) * ZOOM),
				(int) ((ALTURA_GDE_AREA) * ZOOM));
		g.setColor(Color.green);
		g.fillRect((int) ((ALTURA_GDE_AREA + LINHA) * ZOOM),
				(int) ((BORDA_CAMPO + LINHA) * ZOOM),
				(int) ((LARGURA_GDE_AREA - DOBRO_LINHA) * ZOOM),
				(int) ((ALTURA_GDE_AREA - DOBRO_LINHA) * ZOOM));
		/**
		 * GdeArae Baixo
		 */
		g.setColor(Color.white);
		g.fillRect((int) (ALTURA_GDE_AREA * ZOOM), (int) ((ALTURA_MESA
				- BORDA_CAMPO - ALTURA_GDE_AREA) * ZOOM),
				(int) ((LARGURA_GDE_AREA) * ZOOM),
				(int) ((ALTURA_GDE_AREA) * ZOOM));
		g.setColor(Color.green);
		g
				.fillRect(
						(int) ((ALTURA_GDE_AREA + LINHA) * ZOOM),
						(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA + LINHA) * ZOOM),
						(int) ((LARGURA_GDE_AREA - DOBRO_LINHA) * ZOOM),
						(int) ((ALTURA_GDE_AREA - DOBRO_LINHA) * ZOOM));
		/**
		 * PQArae Cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		g.fillRect((int) (x * ZOOM), (int) (BORDA_CAMPO * ZOOM),
				(int) ((LARGURA_PQ_AREA) * ZOOM),
				(int) ((ALTURA_PQ_AREA) * ZOOM));
		g.setColor(Color.green);
		g.fillRect((int) ((x + LINHA) * ZOOM),
				(int) ((BORDA_CAMPO + LINHA) * ZOOM),
				(int) ((LARGURA_PQ_AREA - DOBRO_LINHA) * ZOOM),
				(int) ((ALTURA_PQ_AREA - DOBRO_LINHA) * ZOOM));
		/**
		 * PQArae Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		g.fillRect((int) (x * ZOOM), (int) (y * ZOOM),
				(int) ((LARGURA_PQ_AREA) * ZOOM),
				(int) ((ALTURA_PQ_AREA) * ZOOM));
		g.setColor(Color.green);
		g.fillRect((int) ((x + LINHA) * ZOOM), (int) ((y + LINHA) * ZOOM),
				(int) ((LARGURA_PQ_AREA - DOBRO_LINHA) * ZOOM),
				(int) ((ALTURA_PQ_AREA - DOBRO_LINHA) * ZOOM));
		/**
		 * Circulo Centro
		 */
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();
		g.fillOval((int) (x * ZOOM), (int) (y * ZOOM),
				(int) (RAIO_CENTRO * ZOOM), (int) (RAIO_CENTRO * ZOOM));
		g.setColor(Color.green);
		g.fillOval((int) ((x + LINHA) * ZOOM), (int) ((y + LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM),
				(int) ((RAIO_CENTRO - DOBRO_LINHA) * ZOOM));
		/**
		 * meio de campo
		 */
		g.setColor(Color.white);
		g.fillRect((int) ((BORDA_CAMPO) * ZOOM),
				(int) ((ALTURA_MESA / 2) * ZOOM),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
				(int) (LINHA * ZOOM));

		/**
		 * Penalti cima
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * ZOOM),
				(int) ((BORDA_CAMPO + PENALTI) * ZOOM),
				(int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));

		/**
		 * Penalti Baixo
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * ZOOM), (int) ((ALTURA_MESA
				- BORDA_CAMPO - PENALTI) * ZOOM), (int) (DOBRO_LINHA * ZOOM),
				(int) (DOBRO_LINHA * ZOOM));
		/**
		 * Centro
		 */
		g.setColor(Color.white);
		g.fillOval((int) ((LARGURA_MESA / 2) * ZOOM),
				(int) (((ALTURA_MESA - LINHA) / 2) * ZOOM),
				(int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));
		Graphics2D graphics2D = (Graphics2D) g;
		desenhaFiguras(graphics2D);
	}

	private void desenhaFiguras(Graphics2D g) {
		g.setColor(Color.BLACK);
		campoCima.setBounds((int) (BORDA_CAMPO * ZOOM),
				(int) (BORDA_CAMPO * ZOOM),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
				(int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * ZOOM));
		campoBaixo.setBounds((int) (BORDA_CAMPO * ZOOM),
				(int) (((ALTURA_MESA / 2)) * ZOOM),
				(int) ((LARGURA_MESA - DOBRO_BORDA_CAMPO) * ZOOM),
				(int) (((ALTURA_MESA / 2) - BORDA_CAMPO) * ZOOM));
		grandeAreaCima.setBounds((int) (ALTURA_GDE_AREA * ZOOM),
				(int) (BORDA_CAMPO * ZOOM), (int) ((LARGURA_GDE_AREA) * ZOOM),
				(int) ((ALTURA_GDE_AREA) * ZOOM));

		grandeAreaBaixo.setBounds((int) (ALTURA_GDE_AREA * ZOOM),
				(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * ZOOM),
				(int) ((LARGURA_GDE_AREA) * ZOOM),
				(int) ((ALTURA_GDE_AREA) * ZOOM));

		pequenaAreaCima.setBounds(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * ZOOM),
				(int) (BORDA_CAMPO * ZOOM), (int) ((LARGURA_PQ_AREA) * ZOOM),
				(int) ((ALTURA_PQ_AREA) * ZOOM));

		pequenaAreaBaixo.setBounds(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * ZOOM),
				(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA) * ZOOM),
				(int) ((LARGURA_PQ_AREA) * ZOOM),
				(int) ((ALTURA_PQ_AREA) * ZOOM));

		hasteEsquerdaGolCima.setBounds(Util.inte(pequenaAreaCima.getX()
				+ (110 * ZOOM)), Util.inte(pequenaAreaCima.getY()
				- (pequenaAreaCima.getHeight() * .50)), Util.inte(10 * ZOOM),
				Util.inte(pequenaAreaCima.getHeight() * .50));
		g.fill(hasteEsquerdaGolCima);

		hasteDireitaGolCima.setBounds(Util.inte(pequenaAreaCima.getX()
				+ pequenaAreaCima.getWidth() - (110 * ZOOM)), Util
				.inte(pequenaAreaCima.getY()
						- (pequenaAreaCima.getHeight() * .50)), Util
				.inte(10 * ZOOM), Util.inte(pequenaAreaCima.getHeight() * .50));
		g.fill(hasteDireitaGolCima);

		hasteTopoGolCima.setBounds(Util.inte(hasteEsquerdaGolCima.getX()), Util
				.inte(hasteDireitaGolCima.getY()),
				Util.inte(hasteDireitaGolCima.getX()
						- hasteEsquerdaGolCima.getX()), Util.inte(10 * ZOOM));
		g.fill(hasteTopoGolCima);

		hasteEsquerdaGolBaixo.setBounds(Util.inte(hasteEsquerdaGolCima.getX()),
				Util.inte(pequenaAreaBaixo.getY()
						+ pequenaAreaBaixo.getHeight()), Util.inte(10 * ZOOM),
				Util.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		g.fill(hasteEsquerdaGolBaixo);

		hasteDireitaGolBaixo.setBounds(Util.inte(hasteDireitaGolCima.getX()),
				Util.inte(pequenaAreaBaixo.getY()
						+ pequenaAreaBaixo.getHeight()), Util.inte(10 * ZOOM),
				Util.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		g.fill(hasteDireitaGolBaixo);

		hasteTopoGolBaixo.setBounds(Util.inte(hasteEsquerdaGolBaixo.getX()),
				Util.inte(hasteDireitaGolBaixo.getY()
						+ hasteDireitaGolBaixo.getHeight()), Util
						.inte(hasteDireitaGolBaixo.getX()
								- hasteEsquerdaGolBaixo.getX()
								+ Util.inte(10 * ZOOM)), Util.inte(10 * ZOOM));
		g.fill(hasteTopoGolBaixo);

		centro.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
				(int) ((ALTURA_MESA / 2) * ZOOM), (int) (DOBRO_LINHA * ZOOM),
				(int) (DOBRO_LINHA * ZOOM));

		penaltyCima.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
				(int) ((BORDA_CAMPO + PENALTI) * ZOOM),
				(int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));
		penaltyBaixo.setBounds((int) ((LARGURA_MESA / 2) * ZOOM),
				(int) ((ALTURA_MESA - BORDA_CAMPO - PENALTI) * ZOOM),
				(int) (DOBRO_LINHA * ZOOM), (int) (DOBRO_LINHA * ZOOM));

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

		return new Point((int) ((LARGURA_MESA / 2) * ZOOM)
				- (int) (DOBRO_LINHA * ZOOM / 2),
				(int) ((ALTURA_MESA / 2) * ZOOM)
						- (int) (DOBRO_LINHA * ZOOM / 2));
	}
}
