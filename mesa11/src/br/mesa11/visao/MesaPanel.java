package br.mesa11.visao;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;

public class MesaPanel extends JPanel {

	public static final Long zero = new Long(0);
	public final static Color green2 = new Color(0, 200, 0, 150);
	public final static Color green = new Color(0, 255, 0, 150);
	// public final static Color green2 = Color.white;
	// public final static Color green = Color.white;
	public final static Color lightWhite = new Color(255, 255, 255, 200);
	public final static Color red = new Color(250, 0, 0, 150);
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

	public double zoom = 0.7;
	private Rectangle campoCima;
	private Rectangle campoCimaSemLinhas;
	private Rectangle campoBaixo;
	private Rectangle campoBaixoSemLinhas;
	private Rectangle areaEscateioCima;
	private Rectangle areaGolCima;
	private Rectangle linhaGolCima;
	private Rectangle areaGolBaixo;
	private Rectangle linhaGolBaixo;
	private Rectangle areaEscateioBaixo;

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
	private Ellipse2D zoomedMeioCampoBorda;
	private Ellipse2D zoomedPenaltiCima;
	private Ellipse2D zoomedPenaltiBaixo;
	private Ellipse2D zoomedCentro;
	private Rectangle2D[] zoomedFaixasGrama = new Rectangle2D[FAIXAS];
	private Shape limitesViewPort;
	private double oldZoom;
	public static BufferedImage grama1;
	public static BufferedImage grama2;

	public static BufferedImage grama1Zoomed;
	public static BufferedImage grama2Zoomed;

	public MesaPanel(ControleJogo controleJogo) {
		if (!controleJogo.isJogoOnlineSrvidor()) {
			grama1 = CarregadorRecursos.carregaImg("grama1.jpg");
			grama2 = CarregadorRecursos.carregaImg("grama2.jpg");
		}
		setSize(LARGURA_MESA * 2, ALTURA_MESA * 2);
		areaEscateioCima = new Rectangle((BORDA_CAMPO + LINHA), 0,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA), BORDA_CAMPO
						+ LINHA);
		areaEscateioBaixo = new Rectangle((BORDA_CAMPO + LINHA),
				(ALTURA_MESA - BORDA_CAMPO) - LINHA, (LARGURA_MESA
						- DOBRO_BORDA_CAMPO - DOBRO_LINHA), BORDA_CAMPO + LINHA);
		campoCima = new Rectangle(BORDA_CAMPO, BORDA_CAMPO, LARGURA_MESA
				- DOBRO_BORDA_CAMPO, (ALTURA_MESA / 2) - BORDA_CAMPO);
		campoCimaSemLinhas = new Rectangle(campoCima.x + LINHA, campoCima.y
				+ LINHA, campoCima.width - (2 * LINHA), campoCima.height);
		campoBaixo = new Rectangle(BORDA_CAMPO, (ALTURA_MESA / 2), LARGURA_MESA
				- DOBRO_BORDA_CAMPO, (ALTURA_MESA / 2) - BORDA_CAMPO);
		campoBaixoSemLinhas = new Rectangle(campoBaixo.x + LINHA, campoBaixo.y,
				campoBaixo.width - (2 * LINHA), campoBaixo.height - LINHA);
		grandeAreaCima = new Rectangle(ALTURA_GDE_AREA, BORDA_CAMPO,
				LARGURA_GDE_AREA, ALTURA_GDE_AREA);
		grandeAreaBaixo = new Rectangle(ALTURA_GDE_AREA, ALTURA_MESA
				- BORDA_CAMPO - ALTURA_GDE_AREA, LARGURA_GDE_AREA,
				ALTURA_GDE_AREA);
		pequenaAreaCima = new Rectangle(LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2,
				BORDA_CAMPO, LARGURA_PQ_AREA, ALTURA_PQ_AREA);
		pequenaAreaBaixo = new Rectangle(LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2,
				ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA, LARGURA_PQ_AREA,
				ALTURA_PQ_AREA);
		centro = new Rectangle(LARGURA_MESA / 2, ALTURA_MESA / 2, DOBRO_LINHA,
				DOBRO_LINHA);
		penaltyCima = new Rectangle(LARGURA_MESA / 2, BORDA_CAMPO + PENALTI,
				DOBRO_LINHA, DOBRO_LINHA);
		penaltyBaixo = new Rectangle(LARGURA_MESA / 2, ALTURA_MESA
				- BORDA_CAMPO - PENALTI, DOBRO_LINHA, DOBRO_LINHA);
		hasteDireitaGolCima = new Rectangle(Util.inte(pequenaAreaCima.getX()
				+ pequenaAreaCima.getWidth() - (110)),
				Util.inte(pequenaAreaCima.getY() + (LINHA)
						- (pequenaAreaCima.getHeight() * .50)), Util.inte(10),
				Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteEsquerdaGolCima = new Rectangle(
				Util.inte(pequenaAreaCima.getX() + (110)),
				Util.inte(pequenaAreaCima.getY() + (LINHA)
						- (pequenaAreaCima.getHeight() * .50)), Util.inte(10),
				Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteTopoGolCima = new Rectangle(
				Util.inte(hasteEsquerdaGolCima.getX()),
				Util.inte(hasteDireitaGolCima.getY()),
				Util.inte(hasteDireitaGolCima.getX()
						- hasteEsquerdaGolCima.getX()), Util.inte(10));
		areaGolCima = new Rectangle(
				Util.inte(hasteEsquerdaGolCima.getCenterX()),
				Util.inte(hasteTopoGolCima.getY()), Util.inte(hasteTopoGolCima
						.getWidth()), Util.inte(hasteEsquerdaGolCima
						.getHeight() - (LINHA)));
		linhaGolCima = new Rectangle(areaGolCima.x, areaGolCima.y
				+ areaGolCima.height, areaGolCima.width, LINHA);
		hasteDireitaGolBaixo = new Rectangle(Util.inte(hasteDireitaGolCima
				.getX()), Util.inte(pequenaAreaBaixo.getY() - (LINHA)
				+ pequenaAreaBaixo.getHeight()), 10, Util.inte(pequenaAreaBaixo
				.getHeight() - (pequenaAreaBaixo.getHeight() * .50)));
		hasteEsquerdaGolBaixo = new Rectangle(Util.inte(hasteEsquerdaGolCima
				.getX()), Util.inte(pequenaAreaBaixo.getY() - (LINHA)
				+ pequenaAreaBaixo.getHeight()), 10, Util.inte(pequenaAreaBaixo
				.getHeight() - (pequenaAreaBaixo.getHeight() * .50)));
		hasteTopoGolBaixo = new Rectangle(Util.inte(hasteEsquerdaGolBaixo
				.getX()), Util.inte(hasteDireitaGolBaixo.getY()
				+ hasteDireitaGolBaixo.getHeight()),
				Util.inte(hasteDireitaGolBaixo.getX()
						- hasteEsquerdaGolBaixo.getX() + Util.inte(10)),
				Util.inte(10));
		areaGolBaixo = new Rectangle(Util.inte(hasteEsquerdaGolBaixo
				.getCenterX()), Util.inte(hasteTopoGolBaixo.getY()
				- hasteEsquerdaGolBaixo.getHeight() + (LINHA)),
				Util.inte(hasteTopoGolCima.getWidth()),
				Util.inte(hasteEsquerdaGolBaixo.getHeight() - (LINHA)));
		linhaGolBaixo = new Rectangle(areaGolBaixo.x, areaGolBaixo.y - LINHA,
				areaGolBaixo.width, LINHA);
		this.controleJogo = controleJogo;
		this.botoes = controleJogo.getBotoes();

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

	public Rectangle getAreaEscateioCima() {
		return areaEscateioCima;
	}

	public Rectangle getAreaGolCima() {
		return areaGolCima;
	}

	public Rectangle getAreaGolBaixo() {
		return areaGolBaixo;
	}

	public Rectangle getLinhaGolCima() {
		return linhaGolCima;
	}

	public Rectangle getLinhaGolBaixo() {
		return linhaGolBaixo;
	}

	public Rectangle getAreaEscateioBaixo() {
		return areaEscateioBaixo;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		limitesViewPort = controleJogo.limitesViewPort();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(limitesViewPort.getBounds().x,
				limitesViewPort.getBounds().y, (int) limitesViewPort
						.getBounds().getWidth(), (int) limitesViewPort
						.getBounds().getHeight());
		if (limitesViewPort == null) {
			limitesViewPort = new Rectangle(0, 0, LARGURA_MESA, ALTURA_MESA);
		} else {
			// Rectangle rectangle = (Rectangle) limitesViewPort;
			// rectangle.width -= 100;
			// rectangle.height -= 100;
			// rectangle.x += 50;
			// rectangle.y += 50;
		}
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
				if (botao instanceof Goleiro) {
					Goleiro goleiro = (Goleiro) botao;
					desenhaGoleiro(goleiro, g2d);
				} else {
					desenhaBotao(botao, g2d);
				}

			}
			desenhaBotao((Botao) botoes.get(new Long(0)), g2d);
		}
		simulaRota(g2d);
		desenhaSombraGoleiro(g2d);
		// Graphics2D g2d = (Graphics2D) g;
		// if (limitesViewPort != null && controleJogo.getBola() != null) {
		// Rectangle rectangle = (Rectangle) limitesViewPort;
		// g2d.drawOval((int) rectangle.getCenterX() - 25, (int) rectangle
		// .getCenterY() - 25, 10, 10);
		// Botao b = controleJogo.getBola();
		// Point ori = new Point((int) rectangle.getCenterX()-25, (int)
		// rectangle
		// .getCenterY()-25);
		// Point des = new Point((int) (b.getCentro().x * zoom),
		// (int) (b.getCentro().y * zoom));
		// g2d.drawLine(ori.x, ori.y, des.x, des.y);
		//
		// }
		// desennhaCirculo(g2d);
		desenhaInfoJogo(g2d);
		if (Logger.debug) {
			if (controleJogo.ptDstBola != null
					&& controleJogo.getBola() != null) {
				g2d.setColor(Color.BLACK);
				if (controleJogo.ptDstBola != null)
					g2d.fillOval(Util.inte(controleJogo.ptDstBola.x * zoom),
							Util.inte(controleJogo.ptDstBola.y * zoom), 5, 5);
				if (controleJogo.golJogadaCpu != null)
					g2d.drawLine(
							Util.inte(controleJogo.getBola().getCentro().x
									* zoom),
							Util.inte(controleJogo.getBola().getCentro().y
									* zoom),
							Util.inte(controleJogo.golJogadaCpu.x * zoom),
							Util.inte(controleJogo.golJogadaCpu.y * zoom));
			}
		}
	}

	private void desenhaSombraGoleiro(Graphics2D g2d) {
		if (controleJogo.getPontoClicado() != null
				&& controleJogo.getPontoPasando() != null && botoes != null) {
			g2d.setColor(Color.LIGHT_GRAY);
			Point p0 = (Point) controleJogo.getPontoClicado();
			if (controleJogo.getBola().getShape(1).getBounds2D().contains(p0)) {
				return;
			}

			Point pAtual = (Point) controleJogo.getPontoPasando();
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				Botao botao = (Botao) botoes.get(id);
				if (botao == null || botao.getCentro() == null
						|| !(botao instanceof Goleiro)) {
					continue;
				}
				List raioPonto = GeoUtil.drawBresenhamLine(p0,
						botao.getCentro());
				if (raioPonto.size() <= (botao.getRaio() / 2)) {
					Goleiro g = new Goleiro();
					g.setCentroTodos(pAtual);
					g.setRotacao(((Goleiro) botao).getRotacao());
					if ((getGrandeAreaCima()
							.contains(g.getShape(1).getBounds())
							|| getGrandeAreaBaixo().contains(
									g.getShape(1).getBounds())
							|| getAreaGolCima().intersects(
									g.getShape(1).getBounds()) || getAreaGolBaixo()
							.intersects(g.getShape(1).getBounds()))
							&& !g.getShape(1).intersects(
									controleJogo.getBola().getShape(1)
											.getBounds2D())) {
						g2d.setColor(lightWhite);
					} else {
						g2d.setColor(red);
					}

					g2d.fill(g.getShape(zoom));
					break;
				} else if ((raioPonto.size() > (botao.getRaio() / 2))
						&& (raioPonto.size() <= botao.getRaio())) {
					Goleiro g = new Goleiro();
					g.setCentroTodos(botao.getCentro());

					g.setRotacao(GeoUtil.calculaAngulo(g.getCentro(), pAtual, 0));

					if ((getGrandeAreaCima()
							.contains(g.getShape(1).getBounds())
							|| getGrandeAreaBaixo().contains(
									g.getShape(1).getBounds())
							|| getAreaGolCima().intersects(
									g.getShape(1).getBounds()) || getAreaGolBaixo()
							.intersects(g.getShape(1).getBounds()))
							&& !g.getShape(1).intersects(
									controleJogo.getBola().getShape(1)
											.getBounds2D())) {
						g2d.setColor(lightWhite);
					} else {
						g2d.setColor(red);
					}

					g2d.fill(g.getShape(zoom));
					break;
				}
			}

		}

	}

	private void desenhaInfoJogo(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 150);
		int y = limitesViewPort.getBounds().y + 20;
		Time timeMandante = controleJogo.obterTimeMandante();
		Time timeVisita = controleJogo.obterTimeVisita();
		if (timeMandante != null && timeVisita != null) {
			int newx = limitesViewPort.getBounds().x
					+ (limitesViewPort.getBounds().width / 2);
			Color cM1 = new Color(timeMandante.getCor1());
			Color cM2 = new Color(timeMandante.getCor2());
			Color corFundo = ImageUtil.gerarCorTransparente(cM1, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx - 100, y - 15, 100, 20, 10, 10);
			int valor = (cM1.getRed() + cM1.getGreen() + cM1.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + timeMandante.getNomeAbrev(), newx - 90, y);
			corFundo = ImageUtil.gerarCorTransparente(cM2, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx, y - 15, 20, 20, 10, 10);
			valor = (cM2.getRed() + cM2.getGreen() + cM2.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + controleJogo.verGols(timeMandante), newx + 10,
					y);

			Color cV1 = new Color(timeVisita.getCor1());
			Color cV2 = new Color(timeVisita.getCor2());
			corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx + 60, y - 15, 100, 20, 10, 10);
			valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + timeVisita.getNomeAbrev(), newx + 70, y);
			corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx + 40, y - 15, 20, 20, 10, 10);
			valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + controleJogo.verGols(timeVisita), newx + 51, y);
		}
		Time time = controleJogo.timeJogadaVez();
		if (time != null) {
			Color c1 = new Color(time.getCor1());
			g2d.setColor(lightWhite);
			g2d.fillRoundRect(x - 10, y - 15, 100, 20, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + controleJogo.tempoRestanteJogoFormatado(), x, y);
			g2d.drawString(Lang.msg("de") + " ", x + 35, y);
			g2d.drawString(controleJogo.tempoJogoFormatado(), x + 55, y);
			y += 25;
			g2d.setColor(lightWhite);
			g2d.fillRoundRect(x - 10, y - 15, 100, 20, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.drawString(
					Lang.msg("jogadas") + " "
							+ controleJogo.obterNumJogadas(time) + " "
							+ Lang.msg("de") + " "
							+ controleJogo.getNumeroJogadas(), x, y);

			y += 25;
			if (controleJogo.isEsperandoJogadaOnline()
					|| controleJogo.isAnimando()) {
				g2d.setColor(lightWhite);
				g2d.fillRoundRect(x - 10, y - 15, 100, 20, 10, 10);
				g2d.setColor(Color.BLACK);
				g2d.drawString("" + Lang.msg("aguarde"), x, y);
			} else {
				Color corFundo = ImageUtil.gerarCorTransparente(c1, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 100, 20, 10, 10);
				int valor = (c1.getRed() + c1.getGreen() + c1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString("" + time.getNomeAbrev(), x, y);
			}
		}
		if (controleJogo.isJogoOnlineCliente()
				|| (controleJogo.isJogoIniciado() && !(controleJogo
						.isAnimando()))) {
			y += 50;
			g2d.setColor(lightWhite);
			g2d.fillRoundRect(x - 10, y - 40, 100, 45, 10, 10);
			g2d.setColor(Color.BLACK);
			Font fontOri = g2d.getFont();
			g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 48));
			g2d.drawString(
					" " + controleJogo.tempoJogadaRestanteJogoFormatado(),
					x - 5, y);

			g2d.setFont(fontOri);
		}
		y += 25;
		if (controleJogo.getPontoPasando() != null
				&& !(controleJogo.isAnimando())) {
			Botao botao = controleJogo.obterBotao(controleJogo
					.getPontoPasando());
			if (botao != null) {
				if (botao.getId() == 0) {
					return;
				}
				Color cV1 = new Color(botao.getTime().getCor1());
				Color cV2 = new Color(botao.getTime().getCor2());
				Color corFundo = ImageUtil.gerarCorTransparente(cV1, 200);

				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 100, 20, 10, 10);
				int valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString("" + botao.getNome(), x, y);
				y += 22;
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 70, 20, 10, 10);
				valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString(Lang.msg("forca"), x, y);
				corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x + 60, y - 15, 30, 20, 10, 10);
				valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString("" + botao.getForca(), x + 68, y);
				y += 22;
				corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 70, 20, 10, 10);
				valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString(Lang.msg("precisao"), x, y);
				corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x + 60, y - 15, 30, 20, 10, 10);
				valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString("" + botao.getPrecisao(), x + 68, y);
				y += 22;
				corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 70, 20, 10, 10);
				valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString(Lang.msg("defesa"), x, y);
				corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x + 60, y - 15, 30, 20, 10, 10);
				valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawString("" + botao.getDefesa(), x + 68, y);
			}
		}

		x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width / 2);
		y = limitesViewPort.getBounds().y
				+ (limitesViewPort.getBounds().height - 60);
		String dica = controleJogo.getDica();
		if (Util.isNullOrEmpty(dica)) {
			controleJogo.mudarDica();
			dica = controleJogo.getDica();
		}
		Color corTexto = null;
		if (ConstantesMesa11.PROBLEMA_REDE.equals(dica)) {
			g2d.setColor(red);
			corTexto = Color.WHITE;
		} else {
			g2d.setColor(lightWhite);
			corTexto = Color.BLACK;
		}
		Font fontOri = g2d.getFont();
		if (!Util.isNullOrEmpty(dica) && !dica.startsWith("dica"))
			g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 48));

		String msg = Lang.msg(dica);
		int largura = 0;
		for (int i = 0; i < msg.length(); i++) {
			largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
		}
		x -= largura / 2;

		if (!Util.isNullOrEmpty(dica) && !dica.startsWith("dica")) {
			g2d.fillRoundRect(x - 10, y - 40, largura + 20, 45, 10, 10);
		} else {
			g2d.fillRoundRect(x - 10, y - 15, largura + 20, 20, 10, 10);
		}

		g2d.setColor(corTexto);
		g2d.drawString("" + msg, x, y);

		g2d.setFont(fontOri);
	}

	private void desennhaCirculo(Graphics g2d) {
		if (controleJogo.getPontoBtnDirClicado() != null) {
			List l = GeoUtil.drawCircle(
					Util.inte(controleJogo.getPontoBtnDirClicado().x * zoom),
					Util.inte(controleJogo.getPontoBtnDirClicado().y * zoom),
					Util.inte(ConstantesMesa11.PERIMETRO * zoom));
			g2d.fillOval(
					Util.inte(controleJogo.getPontoBtnDirClicado().x * zoom),
					Util.inte(controleJogo.getPontoBtnDirClicado().y * zoom),
					2, 2);
			g2d.setColor(Color.BLACK);
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Point p = (Point) iterator.next();
				g2d.fillOval(p.x, p.y, 2, 2);
			}
		}
	}

	private void simulaRota(Graphics2D g2d) {
		if (controleJogo.getPontoClicado() != null
				&& controleJogo.getPontoPasando() != null && botoes != null) {
			g2d.setColor(Color.LIGHT_GRAY);
			Point p0 = (Point) controleJogo.getPontoClicado();
			Point pAtual = (Point) controleJogo.getPontoPasando();
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				Botao botao = (Botao) botoes.get(id);
				if (botao == null || botao.getCentro() == null) {
					continue;
				}
				List raioPonto = GeoUtil.drawBresenhamLine(p0,
						botao.getCentro());
				if (raioPonto.size() <= botao.getRaio()) {
					g2d.drawLine(Util.inte(p0.x * zoom),
							Util.inte(p0.y * zoom), Util.inte(pAtual.x * zoom),
							Util.inte(pAtual.y * zoom));
					if (botao instanceof Goleiro) {
						continue;
					}
					double angulo = GeoUtil.calculaAngulo(botao.getCentro(),
							pAtual, 270);
					Point destino = GeoUtil.calculaPonto(angulo,
							Util.inte(GeoUtil.drawBresenhamLine(p0, pAtual)
									.size() * 10), botao.getCentro());
					g2d.drawLine(Util.inte(botao.getCentro().x * zoom),
							Util.inte(botao.getCentro().y * zoom),
							Util.inte(destino.x * zoom),
							Util.inte(destino.y * zoom));
					break;
				}
			}

		}

	}

	private void desenhaGoleiro(Goleiro goleiro, Graphics g) {
		if (!limitesViewPort.contains(new Point(Util.inte(goleiro.getCentro().x
				* zoom), Util.inte(goleiro.getCentro().y * zoom)))) {
			return;
		}
		int botx = (int) (goleiro.getPosition().x * zoom);
		int boty = (int) (goleiro.getPosition().y * zoom);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		Shape goleroShape = goleiro.getShape(zoom);
		g2d.fill(goleroShape);

		AffineTransform afZoom = new AffineTransform();
		AffineTransform afRotate = new AffineTransform();
		afZoom.setToScale(zoom, zoom);
		double rad = Math.toRadians(goleiro.getRotacao());
		afRotate.setToRotation(rad, 200, 200);

		BufferedImage botaoImg = (BufferedImage) controleJogo
				.getBotoesImagens().get(goleiro.getId());
		BufferedImage newBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
		setarHints(graphics2d);
		graphics2d.drawImage(botaoImg, 0, 170, null);
		graphics2d.dispose();

		BufferedImage zoomBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage rotateBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);

		AffineTransformOp op = new AffineTransformOp(afRotate,
				AffineTransformOp.TYPE_BILINEAR);
		op.filter(newBuffer, zoomBuffer);
		AffineTransformOp op2 = new AffineTransformOp(afZoom,
				AffineTransformOp.TYPE_BILINEAR);
		op2.filter(zoomBuffer, rotateBuffer);
		g.drawImage(rotateBuffer, botx, boty - Util.inte(170 * zoom), null);

	}

	private void desenhaBotao(Botao botao, Graphics g) {
		if (botao == null || botao.getPosition() == null)
			return;
		int botx = (int) (botao.getPosition().x * zoom);
		int boty = (int) (botao.getPosition().y * zoom);
		if (!limitesViewPort.contains(new Point(botx + botao.getRaio(), boty
				+ botao.getRaio()))) {
			return;
		}
		AffineTransform affineTransform = AffineTransform.getScaleInstance(
				zoom, zoom);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage botaoImg = (BufferedImage) controleJogo
				.getBotoesImagens().get(botao.getId());
		if (botaoImg == null)
			return;

		BufferedImage zoomBuffer = new BufferedImage(
				(int) (botaoImg.getWidth() * zoom),
				(int) (botaoImg.getHeight() * zoom),
				BufferedImage.TYPE_INT_ARGB);

		affineTransformOp.filter(botaoImg, zoomBuffer);
		g.drawImage(zoomBuffer, botx, boty, null);
		// g.setColor(Color.black);
		// g.drawOval(botx, boty, Util.inte(botao.getDiamentro() * zoom), Util
		// .inte(botao.getDiamentro() * zoom));
	}

	private void desenhaCampo(Graphics2D g) {
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		zoomedMesa = new Rectangle2D.Double(0, 0, ((LARGURA_MESA) * zoom),
				((ALTURA_MESA) * zoom));
		if (limitesViewPort.intersects(zoomedMesa))
			g.fill(zoomedMesa);
		/**
		 * Campo
		 */
		g.setColor(Color.white);
		zoomedBorda = new Rectangle2D.Double((BORDA_CAMPO * zoom),
				(BORDA_CAMPO * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO) * zoom));
		if (limitesViewPort.intersects(zoomedBorda))
			g.fill(zoomedBorda);

		g.setColor(green);
		zoomedGrama = new Rectangle2D.Double(((BORDA_CAMPO + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom), ((LARGURA_MESA
						- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom));

		// if (limitesViewPort.intersects(zoomedGrama))
		// g.fill(zoomedGrama);
		int alturaBordaAtual = (BORDA_CAMPO + LINHA);
		int contFaixas = 0;
		AffineTransform affineTransform = AffineTransform.getScaleInstance(
				zoom, zoom);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		if (zoom != oldZoom) {
			grama1Zoomed = new BufferedImage(
					Util.inte(grama1.getWidth() * zoom), Util.inte(grama1
							.getHeight() * zoom), grama1.getType());
			grama2Zoomed = new BufferedImage(
					Util.inte(grama2.getWidth() * zoom), Util.inte(grama1
							.getHeight() * zoom), grama2.getType());
			affineTransformOp.filter(grama1, grama1Zoomed);
			affineTransformOp.filter(grama2, grama2Zoomed);
		}
		oldZoom = zoom;
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				g.setColor(green2);
				zoomedFaixasGrama[contFaixas] = new Rectangle2D.Double(
						((BORDA_CAMPO + LINHA) * zoom),
						((alturaBordaAtual) * zoom), ((LARGURA_MESA
								- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
						((ALTURA_FAIXA) * zoom));

				if (limitesViewPort.intersects(zoomedFaixasGrama[contFaixas])) {
					// g.fill(zoomedFaixasGrama[contFaixas]);
					if (limitesViewPort
							.intersects(zoomedFaixasGrama[contFaixas])) {
						g.drawImage(
								grama1Zoomed,
								Util.inte(zoomedFaixasGrama[contFaixas].getX()),
								Util.inte(zoomedFaixasGrama[contFaixas].getY()),
								null);
					}

				}
				contFaixas++;
				alturaBordaAtual += (ALTURA_FAIXA - LINHA);
				continue;
			} else {
				zoomedFaixasGrama[i] = new Rectangle2D.Double(
						((BORDA_CAMPO + LINHA) * zoom),
						((alturaBordaAtual) * zoom), ((LARGURA_MESA
								- DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
						((ALTURA_FAIXA) * zoom));

				if (limitesViewPort.intersects(zoomedFaixasGrama[i])) {
					g.drawImage(grama2Zoomed,
							Util.inte(zoomedFaixasGrama[i].getX()),
							Util.inte(zoomedFaixasGrama[i].getY()), null);
				}

			}
			alturaBordaAtual += (ALTURA_FAIXA);
		}
		Rectangle2D rect = new Rectangle2D.Double(
				((BORDA_CAMPO + LINHA) * zoom), ((alturaBordaAtual) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				((ALTURA_FAIXA / 5) * zoom));
		BufferedImage gramaResto = new BufferedImage(grama2.getWidth(),
				(ALTURA_FAIXA / 5), grama2.getType());
		Graphics graphicsGrama = gramaResto.getGraphics();
		graphicsGrama.drawImage(grama2, 0, 0, null);
		g.drawImage(gramaResto, affineTransformOp, Util.inte(rect.getX()),
				Util.inte(rect.getY()));
		/**
		 * Meia lua de cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = BORDA_CAMPO;
		zoomedMeiaLuaCimaBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedMeiaLuaCimaBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedMeiaLuaCimaBorda);
		g.setColor(green);
		zoomedMeiaLuaCimaGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedMeiaLuaCimaGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedMeiaLuaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedMeiaLuaCimaBorda.getWidth()),
					Util.inte(zoomedMeiaLuaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0 * zoom), (0 * zoom),
					(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedMeiaLuaCimaBorda.getX()),
					Util.inte(zoomedMeiaLuaCimaBorda.getY()), null);

		}
		/**
		 * Meia lua de Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		zoomedMeiaLuaBaixoBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedMeiaLuaBaixoBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedMeiaLuaBaixoBorda);
		g.setColor(green);
		zoomedMeiaLuaBaixoGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedMeiaLuaBaixoGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedMeiaLuaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedMeiaLuaBaixoBorda.getWidth()),
					Util.inte(zoomedMeiaLuaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0), (0), (RAIO_CENTRO * zoom),
					(RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			graphics.fill(new Rectangle2D.Double(0,
					(RAIO_CENTRO - ALTURA_GDE_AREA) * zoom,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedMeiaLuaBaixoBorda.getX()),
					Util.inte(zoomedMeiaLuaBaixoBorda.getY()), null);

		}
		/**
		 * GdeArae Cima
		 */
		g.setColor(Color.white);
		zoomedGdeAreaCimaBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom), (BORDA_CAMPO * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedGdeAreaCimaBorda))
		// g.fill(zoomedGdeAreaCimaBorda);
		g.setColor(green);
		zoomedGdeAreaCimaGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedGdeAreaCimaGrama)) {
			// g.fill(zoomedGdeAreaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedGdeAreaCimaBorda.getWidth()),
					Util.inte(zoomedGdeAreaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom),
					((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedGdeAreaCimaBorda.getX()),
					Util.inte(zoomedGdeAreaCimaBorda.getY()), null);
		}
		/**
		 * GdeArae Baixo
		 */
		g.setColor(Color.white);
		zoomedGdeAreaBaixoBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom),
				((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedGdeAreaBaixoBorda))
		// g.fill(zoomedGdeAreaBaixoBorda);
		g.setColor(green);
		zoomedGdeAreaBaixoGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom), ((ALTURA_MESA - BORDA_CAMPO
						- ALTURA_GDE_AREA + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedGdeAreaBaixoGrama)) {
			// g.fill(zoomedGdeAreaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedGdeAreaBaixoBorda.getWidth()),
					Util.inte(zoomedGdeAreaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double((0), (0),
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom),
					((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedGdeAreaBaixoBorda.getX()),
					Util.inte(zoomedGdeAreaBaixoBorda.getY()), null);

		}
		/**
		 * PQArae Cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);

		zoomedpqAreaCimaBorda = new Rectangle2D.Double((x * zoom),
				(BORDA_CAMPO * zoom), ((LARGURA_PQ_AREA) * zoom),
				((ALTURA_PQ_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedpqAreaCimaBorda))
		// g.fill(zoomedpqAreaCimaBorda);
		g.setColor(green);
		zoomedpqAreaCimaGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedpqAreaCimaGrama)) {
			// g.fill(zoomedpqAreaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedpqAreaCimaBorda.getWidth()),
					Util.inte(zoomedpqAreaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedpqAreaCimaBorda.getX()),
					Util.inte(zoomedpqAreaCimaBorda.getY()), null);
		}
		/**
		 * PQArae Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		zoomedpqAreaBaixoBorda = new Rectangle2D.Double((x * zoom), (y * zoom),
				((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedpqAreaBaixoBorda))
		// g.fill(zoomedpqAreaBaixoBorda);
		g.setColor(green);
		zoomedpqAreaBaixoGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedpqAreaBaixoGrama)) {
			// g.fill(zoomedpqAreaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedpqAreaBaixoBorda.getWidth()),
					Util.inte(zoomedpqAreaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedpqAreaBaixoBorda.getX()),
					Util.inte(zoomedpqAreaBaixoBorda.getY()), null);
		}
		/**
		 * Circulo Centro
		 */
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();

		zoomedcentroBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedcentroBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedcentroBorda);

		g.setColor(green);
		zoomedcentroGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedcentroGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedcentroGrama);
			BufferedImage bi = new BufferedImage(Util.inte(zoomedcentroBorda
					.getWidth()), Util.inte(zoomedcentroBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0), (0), (RAIO_CENTRO * zoom),
					(RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite.getInstance(
					AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedcentroBorda.getX()),
					Util.inte(zoomedcentroBorda.getY()), null);

		}

		/**
		 * meio de campo
		 */
		g.setColor(Color.white);
		zoomedMeioCampoBorda = new Ellipse2D.Double(((BORDA_CAMPO) * zoom),
				((ALTURA_MESA / 2) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom), (LINHA * zoom));
		if (zoomedMeioCampoBorda.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedMeioCampoBorda);

		/**
		 * Penalti cima
		 */
		g.setColor(Color.white);
		zoomedPenaltiCima = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				((BORDA_CAMPO + PENALTI) * zoom), (DOBRO_LINHA * zoom),
				(int) (DOBRO_LINHA * zoom));
		if (zoomedPenaltiCima.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedPenaltiCima);
		/**
		 * Penalti Baixo
		 */
		g.setColor(Color.white);
		zoomedPenaltiBaixo = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				((ALTURA_MESA - BORDA_CAMPO - PENALTI) * zoom),
				(DOBRO_LINHA * zoom), (DOBRO_LINHA * zoom));
		if (zoomedPenaltiBaixo.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedPenaltiBaixo);
		/**
		 * Centro
		 */
		g.setColor(Color.white);
		zoomedCentro = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				(((ALTURA_MESA - LINHA) / 2) * zoom), (DOBRO_LINHA * zoom),
				(DOBRO_LINHA * zoom));
		if (zoomedCentro.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedCentro);
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
		Rectangle2D.Double hasteEsquerdaGolCimaTemp = new Rectangle2D.Double();
		hasteEsquerdaGolCimaTemp.setRect(pequenaAreaCimaTemp.getX()
				+ (110 * zoom), pequenaAreaCimaTemp.getY() + (LINHA * zoom)
				- (pequenaAreaCimaTemp.getHeight() * .50), 10 * zoom,
				pequenaAreaCimaTemp.getHeight() * .50);
		g.fill(hasteEsquerdaGolCimaTemp);
		Rectangle2D.Double hasteDireitaGolCimaTemp = new Rectangle2D.Double();
		hasteDireitaGolCimaTemp.setRect(pequenaAreaCimaTemp.getX()
				+ pequenaAreaCimaTemp.getWidth() - (110 * zoom),
				pequenaAreaCimaTemp.getY() + (LINHA * zoom)
						- (pequenaAreaCimaTemp.getHeight() * .50), 10 * zoom,
				pequenaAreaCimaTemp.getHeight() * .50);
		g.fill(hasteDireitaGolCimaTemp);
		Rectangle2D.Double hasteTopoGolCimaTemp = new Rectangle2D.Double();
		hasteTopoGolCimaTemp.setRect(hasteEsquerdaGolCimaTemp.getX(),
				hasteDireitaGolCimaTemp.getY(), hasteDireitaGolCimaTemp.getX()
						- hasteEsquerdaGolCimaTemp.getX(), 10 * zoom);
		g.fill(hasteTopoGolCimaTemp);
		Color corRede = new Color(255, 255, 255, 100);
		Rectangle2D.Double areaGolCimaTemp = new Rectangle2D.Double();
		areaGolCimaTemp.setRect(hasteEsquerdaGolCimaTemp.getCenterX(),
				hasteTopoGolCimaTemp.getY(), hasteTopoGolCimaTemp.getWidth(),
				hasteEsquerdaGolCimaTemp.getHeight() - (LINHA * zoom));
		g.setColor(corRede);
		g.fill(areaGolCimaTemp);
		g.setColor(Color.black);
		Rectangle2D.Double hasteEsquerdaGolBaixoTemp = new Rectangle2D.Double();
		hasteEsquerdaGolBaixoTemp.setRect(
				hasteEsquerdaGolCimaTemp.getX(),
				pequenaAreaBaixoTemp.getY() - (LINHA * zoom)
						+ pequenaAreaBaixoTemp.getHeight(),
				10 * zoom,
				pequenaAreaBaixoTemp.getHeight()
						- (pequenaAreaBaixoTemp.getHeight() * .50));
		g.fill(hasteEsquerdaGolBaixoTemp);
		Rectangle2D.Double hasteDireitaGolBaixoTemp = new Rectangle2D.Double();
		hasteDireitaGolBaixoTemp.setRect(
				hasteDireitaGolCimaTemp.getX(),
				pequenaAreaBaixoTemp.getY() - (LINHA * zoom)
						+ pequenaAreaBaixoTemp.getHeight(),
				10 * zoom,
				pequenaAreaBaixoTemp.getHeight()
						- (pequenaAreaBaixoTemp.getHeight() * .50));
		g.fill(hasteDireitaGolBaixoTemp);
		Rectangle2D.Double hasteTopoGolBaixoTemp = new Rectangle2D.Double();
		hasteTopoGolBaixoTemp.setRect(
				hasteEsquerdaGolBaixoTemp.getX(),
				hasteDireitaGolBaixoTemp.getY()
						+ hasteDireitaGolBaixoTemp.getHeight(),
				hasteDireitaGolBaixoTemp.getX()
						- hasteEsquerdaGolBaixoTemp.getX()
						+ Util.inte(10 * zoom), 10 * zoom);
		g.fill(hasteTopoGolBaixoTemp);

		Rectangle2D.Double areaGolBaixoTemp = new Rectangle2D.Double();
		areaGolBaixoTemp.setRect(
				hasteEsquerdaGolBaixoTemp.getCenterX(),
				hasteTopoGolBaixoTemp.getY()
						- hasteEsquerdaGolBaixoTemp.getHeight()
						+ (LINHA * zoom), hasteTopoGolCimaTemp.getWidth(),
				hasteEsquerdaGolBaixoTemp.getHeight() - (LINHA * zoom));
		g.setColor(corRede);
		g.fill(areaGolBaixoTemp);

	}

	private void desenhaFiguras(Graphics2D g) {
		g.setColor(Color.cyan);

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
		// g.setColor(Color.cyan);
		// g.fill(areaGolBaixo);
		// g.fill(areaGolCima);
		// g.fill(areaEscateioBaixo);
		// g.fill(areaEscateioCima);
		// g.fill(campoBaixoSemLinhas);

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
		Point p = new Point(Util.inte(getPenaltyBaixo().x),
				Util.inte(getPequenaAreaBaixo().getLocation().y
						+ getPequenaAreaBaixo().getHeight() + (LINHA * 2)));
		return p;
	}

	public Point golCima() {
		Point p = new Point(Util.inte(getPenaltyCima().x),
				Util.inte(getPequenaAreaCima().getLocation().y - 20));
		return p;
	}

	public Rectangle getHasteTopoGolCima() {
		return hasteTopoGolCima;
	}

	public Rectangle getCampoCima() {
		return campoCima;
	}

	public Rectangle getCampoBaixo() {
		return campoBaixo;
	}

	public Rectangle getCampoCimaSemLinhas() {
		return campoCimaSemLinhas;
	}

	public Rectangle getCampoBaixoSemLinhas() {
		return campoBaixoSemLinhas;
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

	public static void main(String[] args) {
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				Logger.logar(i);
			}
		}
		// BufferedImage bufferedImage = new BufferedWriter(out)
		// for (int j = 0; j < 25; j++) {
		// g.drawImage(grama2, affineTransformOp, Util
		// .inte(zoomedFaixasGrama[i].getX() + j * grama2.getWidth()
		// * zoom), Util.inte(zoomedFaixasGrama[i].getY()));
		// }

	}

	public Rectangle getHasteDireitaGolCima() {
		return hasteDireitaGolCima;
	}

	public Rectangle getHasteEsquerdaGolCima() {
		return hasteEsquerdaGolCima;
	}

	public Rectangle getHasteDireitaGolBaixo() {
		return hasteDireitaGolBaixo;
	}

	public Rectangle getHasteEsquerdaGolBaixo() {
		return hasteEsquerdaGolBaixo;
	}
}
