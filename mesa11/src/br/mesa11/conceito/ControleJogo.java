package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.servidor.JogoServidor;
import br.mesa11.visao.BotaoTableModel;
import br.mesa11.visao.EditorTime;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;
import br.nnpe.PopupListener;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;
import br.tos.BotaoPosSrvMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.JogadaMesa11;
import br.tos.Mesa11TO;
import br.tos.PosicaoBtnsSrvMesa11;

public class ControleJogo {
	private Map botoes = new HashMap();
	private Map botoesImagens = new HashMap();
	private Map botoesComThread = new HashMap();
	private List<Animacao> listaAnimacoes = new LinkedList<Animacao>();
	private Botao bola;
	private MesaPanel mesaPanel;
	private JScrollPane scrollPane;
	private Point velhoPontoTela;
	private Point novoPontoTela;
	private Point ultLateral;
	private Shape ultGol;
	private Shape ultMetaEscanteio;
	private JFrame frame;
	private Botao botaoSelecionado;
	private Point pontoClicado;
	private Point pontoBtnDirClicado;
	private Point pontoPasando;
	private boolean carregaBotao;
	private boolean chutaBola;
	private int numRecursoes;
	private ControlePartida controlePartida;
	private Evento eventoAtual;
	private AtualizadorVisual atualizadorTela;
	private Mesa11Applet mesa11Applet;
	private JogoServidor jogoServidor;
	private String timeClienteOnline;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Animacao animacaoCliente = null;
	private Animacao animacaoJogada = null;
	private boolean esperandoJogadaOnline;
	private int numeroJogadas;
	private long stampUltimaJogadaOnline;
	private String dica;
	private boolean jogoTerminado;
	private long tempoTerminado;
	private boolean jogoIniciado;
	private long tempoIniciado;
	private String nomeJogadorOnline;
	private ControleDicas controleDicas;

	public ControleJogo(Mesa11Applet mesa11Applet, String timeClienteOnline,
			DadosJogoSrvMesa11 dadosJogoSrvMesa11, String nomeJogadorOnline) {
		this.mesa11Applet = mesa11Applet;
		this.timeClienteOnline = timeClienteOnline;
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
		this.frame = new JFrame();
		mesaPanel = new MesaPanel(this);
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		adicinaListentesEventosMouse();
		adicinaListentesEventosTeclado();
		this.nomeJogadorOnline = nomeJogadorOnline;
		frame
				.setTitle(ConstantesMesa11.TITULO_VERSAO + " "
						+ timeClienteOnline);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		if (isJogoOnlineCliente()) {
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				int ret = JOptionPane.showConfirmDialog(frame, Lang
						.msg("sairJogo"), Lang.msg("confirmaSairJogo"),
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				if (isJogoOnlineCliente()) {
					sairJogoOnline();
				}
				limparJogo();
				super.windowClosing(e);
			}

		});
	}

	public long getTempoTerminado() {
		return tempoTerminado;
	}

	public long getTempoIniciado() {
		return tempoIniciado;
	}

	public ControleJogo(JFrame frame) {
		this.frame = frame;
		mesaPanel = new MesaPanel(this);
		criarPopupMenu();
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		adicinaListentesEventosMouse();
		adicinaListentesEventosTeclado();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		if (isJogoOnlineCliente()) {
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int ret = JOptionPane.showConfirmDialog(
						ControleJogo.this.frame, Lang.msg("sairJogo"), Lang
								.msg("confirmaSairJogo"),
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.NO_OPTION) {
					return;
				}
				if (isJogoOnlineCliente()) {
					sairJogoOnline();
				}
				limparJogo();
				super.windowClosing(e);
			}

		});
		bola = new Bola(0);
		botoesImagens.put(bola.getId(), CarregadorRecursos
				.carregaImg("bola.png"));
		botoes.put(bola.getId(), bola);
		bolaCentro();
	}

	public ControleJogo(JogoServidor jogoServidor) {
		this.frame = frame;
		mesaPanel = new MesaPanel(this);
		scrollPane = new JScrollPane(mesaPanel,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		bola = new Bola(0);
		botoes.put(bola.getId(), bola);
		this.jogoServidor = jogoServidor;
	}

	public List<Animacao> getListaAnimacoes() {
		return listaAnimacoes;
	}

	public Map getBotoesImagens() {
		return botoesImagens;
	}

	public boolean isBolaFora() {
		if (eventoAtual != null) {
			return eventoAtual.isBolaFora();
		}
		return false;
	}

	private void adicinaListentesEventosTeclado() {
		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				Point p = new Point((int) (scrollPane.getViewport()
						.getViewPosition().x), (int) (scrollPane.getViewport()
						.getViewPosition().y));

				if (keycode == KeyEvent.VK_LEFT) {
					p.x -= 10;
				} else if (keycode == KeyEvent.VK_RIGHT) {
					p.x += 10;
				} else if (keycode == KeyEvent.VK_UP) {
					p.y -= 10;
				} else if (keycode == KeyEvent.VK_DOWN) {
					p.y += 10;
				}
				if (p.x < 0 || p.y < 0) {
					return;
				}
				if (p.x > ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
						.getViewport().getWidth())
						|| p.y > ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
								.getViewport().getHeight()))) {
					return;
				}
				novoPontoTela = p;
				super.keyPressed(e);
			}

		});
	}

	private void adicinaListentesEventosMouse() {
		mesaPanel.addMouseWheelListener(new MesaMouseWheelListener(this));
		mesaPanel.addMouseMotionListener(new MesaMouseMotionListener(this));
		mesaPanel.addMouseListener(new MesaMouseListener(this));
	}

	public void test() {

		final JFrame frame = new JFrame("mesa11");
		frame.setResizable(false);
		frame.getContentPane().setLayout(new BorderLayout());
	}

	public void iniciaJogoLivre() {
		controlePartida = new ControlePartida(this);
		controleDicas = new ControleDicas(this);
		controlePartida.iniciaJogoLivre();
		setJogoIniciado(true);
	}

	public void iniciaJogoOnline(DadosJogoSrvMesa11 dadosJogoSrvMesa11,
			Time timeCasa, Time timeVisita) {
		bola = new Bola(0);
		if (isJogoOnlineCliente()) {
			botoesImagens.put(bola.getId(), CarregadorRecursos
					.carregaImg("bola.png"));
		}
		botoes.put(bola.getId(), bola);
		controlePartida = new ControlePartida(this);
		controleDicas = new ControleDicas(this);
		controlePartida.iniciaJogoOnline(dadosJogoSrvMesa11, timeCasa,
				timeVisita);
		bolaCentro();
		setJogoIniciado(true);

	}

	protected void centralizaBola() {
		if (bola == null || bola.getCentro() == null) {
			centroCampo();
			return;
		}
		Point p = new Point((int) (bola.getCentro().x * mesaPanel.zoom)
				- (scrollPane.getViewport().getWidth() / 2), (int) (bola
				.getCentro().y * mesaPanel.zoom)
				- (scrollPane.getViewport().getHeight() / 2));
		if (p.x < 0) {
			p.x = 1;
		}
		double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > maxY) {
			p.y = Util.inte(maxY) - 1;
		}
		novoPontoTela = p;
	}

	public Shape limitesViewPort() {
		if (velhoPontoTela == null) {
			return null;
		}
		Rectangle rectangle = scrollPane.getViewport().getBounds();
		rectangle.width += 50;
		rectangle.height += 50;
		rectangle.x = velhoPontoTela.x;
		rectangle.y = velhoPontoTela.y;
		return rectangle;
	}

	public JogoServidor getJogoServidor() {
		return jogoServidor;
	}

	public void setJogoServidor(JogoServidor jogoServidor) {
		this.jogoServidor = jogoServidor;
	}

	public void centroCampo() {

		Point p = new Point(
				(int) (mesaPanel.getCentro().getLocation().x * mesaPanel.zoom)
						- (scrollPane.getViewport().getWidth() / 2),
				(int) (mesaPanel.getCentro().getLocation().y * mesaPanel.zoom)
						- (scrollPane.getViewport().getHeight() / 2));
		double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > maxX) {
			p.x = Util.inte(maxX) - 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > maxY) {
			p.y = Util.inte(maxY) - 1;
		}
		novoPontoTela = p;
	}

	protected void propagaColisao(Animacao animacao, Botao causador) {
		if (numRecursoes > 11) {
			return;
		}
		numRecursoes++;
		Botao botao = (Botao) botoes.get(animacao.getObjetoAnimacao());
		List trajetoriaBotao = animacao.getPontosAnimacao();
		Set bolaIngnora = new HashSet();
		boolean bolaBateu = false;
		for (int i = 0; i < trajetoriaBotao.size(); i += 5) {
			Object objTrajetoria = trajetoriaBotao.get(i);
			if (objTrajetoria instanceof Point) {
				Point point = (Point) objTrajetoria;
				/**
				 * Bola
				 */
				if (botao instanceof Bola) {
					Rectangle rectangle = new Rectangle(point.x
							- bola.getRaio(), point.y - bola.getRaio(), bola
							.getDiamentro(), bola.getDiamentro());
					boolean defesaGoleiro = defesaGoleiro(rectangle,
							bolaIngnora);
					if (!bolaBateu
							&& (mesaPanel.verificaIntersectsGol(rectangle) || defesaGoleiro)) {
						bolaBateu = true;
						double angulo = GeoUtil.calculaAngulo(point, botao
								.getDestino(), 0);
						double rebatimentoBola = trajetoriaBotao.size();
						if (defesaGoleiro) {
							angulo = GeoUtil.calculaAngulo(botao
									.getCentroInicio(), point, 90);
							angulo = 180 - angulo;
							if (!verificaDentroCampo(botao))
								rebatimentoBola *= .3;
							else {
								rebatimentoBola *= .5;
							}
							eventoAtual.setPonto(point);
							eventoAtual
									.setEventoCod(ConstantesMesa11.GOLEIRO_DEFESA);
						}

						while (i < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						Point destino = GeoUtil
								.calculaPonto(angulo, Util
										.inte(rebatimentoBola * 0.3), botao
										.getCentro());
						botao.setDestino(destino);
						List novaTrajetoria = GeoUtil.drawBresenhamLine(point,
								destino);
						trajetoriaBotao.addAll(novaTrajetoria);
					} else {
						bolaBateu = false;
					}
				}
				botao.setCentro(point);
				/**
				 * Colisão com botões
				 */
				for (Iterator iterator = botoes.keySet().iterator(); iterator
						.hasNext();) {
					Long id = (Long) iterator.next();
					Botao botaoAnalisado = (Botao) botoes.get(id);
					if (botao.equals(botaoAnalisado)) {
						continue;
					}
					if (causador.equals(botaoAnalisado)) {
						continue;
					}
					if (botaoAnalisado instanceof Goleiro) {
						continue;
					}
					if (!verificaDentroCampo(botaoAnalisado)) {
						continue;
					}

					List raioPonto = GeoUtil.drawBresenhamLine(point,
							botaoAnalisado.getCentro());
					if ((raioPonto.size() - (botao.getRaio())) <= (botaoAnalisado
							.getRaio())) {
						if (((botao instanceof Bola && bolaIngnora
								.contains(botaoAnalisado)) || (botaoAnalisado instanceof Bola && bolaIngnora
								.contains(botao)))
								&& verificaDentroCampo(bola)) {
							eventoAtual.setUltimoContato(botaoAnalisado);
							continue;
						}
						if ((botao instanceof Bola) && Math.random() > .8) {
							Logger.logar("Passou pelo jogador");
							bolaIngnora.add(botaoAnalisado);
							continue;
						}
						double angulo = GeoUtil.calculaAngulo(point,
								botaoAnalisado.getCentro(), 90);
						Point destino = null;
						double detAtingido = trajetoriaBotao.size();
						if ((botaoAnalisado instanceof Bola)) {
							eventoAtual.setPonto(point);
							eventoAtual.setUltimoContato(botao);
							eventoAtual
									.setEventoCod(ConstantesMesa11.CONTATO_BOTAO_BOLA);
							Logger.logar("Botão Acerta Bola");
							detAtingido *= (1 - (i / detAtingido));
							bolaIngnora.add(botao);
							Logger.logar("Botão Acerta Bola detAtingido="
									+ detAtingido);
						} else {
							if ((botao instanceof Bola)) {
								Logger.logar("Bola Acerta Botão");
								eventoAtual.setPonto(point);
								eventoAtual.setUltimoContato(botaoAnalisado);
								eventoAtual
										.setEventoCod(ConstantesMesa11.CONTATO_BOTAO_BOLA);
								detAtingido *= 0.05;
							} else {
								Logger
										.logar("botaoAnalisado "
												+ botaoAnalisado);
								detAtingido *= 0.3;
							}

						}
						destino = GeoUtil.calculaPonto(angulo, Util
								.inte(detAtingido), botaoAnalisado.getCentro());
						botaoAnalisado.setDestino(destino);
						animacao = new Animacao();
						if (botaoAnalisado.getCentroInicio() == null)
							botaoAnalisado.setCentroInicio(botaoAnalisado
									.getCentro());
						animacao.setObjetoAnimacao(botaoAnalisado.getId());
						animacao.setPontosAnimacao(botaoAnalisado
								.getTrajetoria());
						trajetoriaBotao.set(i, animacao);
						List novaTrajetoria = new ArrayList();

						/**
						 * Rebatimento de bola em botão
						 */
						int dest = 0;
						if ((botao instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), bola.getCentro(), 90);
							dest = Util.inte(trajetoriaBotao.size() * .2);
							Logger
									.logar("Rebatimento de bola em botão (Botao Bola) dest="
											+ dest);
						} else if ((botaoAnalisado instanceof Bola)) {
							angulo = GeoUtil.calculaAngulo(botao.getCentro(),
									botao.getDestino(), 90);
							dest = Util.inte(detAtingido * .4);
							Logger
									.logar("Rebatimento de bola em botão (BotaoAnalizado Bola) dest="
											+ dest);
						} else {
							angulo = GeoUtil.calculaAngulo(botaoAnalisado
									.getCentro(), point, 90);
							dest = Util.inte(trajetoriaBotao.size() * .1);
							Logger.logar("Botão Com Botão");
							if (!eventoAtual.isNaBola()) {
								eventoAtual.setPonto(point);
								eventoAtual.setUltimoContato(botaoAnalisado);
								eventoAtual
										.setEventoCod(ConstantesMesa11.CONTATO_BOTAO_BOTAO);
							}
						}

						destino = GeoUtil.calculaPonto(angulo, dest, botao
								.getCentro());
						botao.setDestino(destino);
						novaTrajetoria = GeoUtil.drawBresenhamLine(point,
								destino);
						while (i + 1 < trajetoriaBotao.size()) {
							trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
						}
						trajetoriaBotao.addAll(novaTrajetoria);
						propagaColisao(animacao, botao);

						break;
					}
				}
			}
		}
	}

	public Time getTimeCima() {
		return controlePartida.getTimeCima();
	}

	public Time getTimeBaixo() {
		return controlePartida.getTimeBaixo();
	}

	private boolean verificaPosseGoleiro(Botao botao) {
		if (!(botao instanceof Bola))
			return false;
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao g = (Botao) botoes.get(id);
			if (g instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) g;
				if (GeoUtil.drawBresenhamLine(botao.getCentro(),
						goleiro.getCentro()).size() < goleiro.getRaio()) {
					return true;
				}
			}
		}
		return false;
	}

	boolean verificaForaDosLimites(Point point) {
		if (point.x < (mesaPanel.BORDA_CAMPO / 4)) {
			return true;
		}
		if (point.x > (mesaPanel.LARGURA_MESA - (mesaPanel.BORDA_CAMPO / 4))) {
			return true;
		}
		if (point.y < (mesaPanel.BORDA_CAMPO / 4)) {
			return true;
		}
		if (point.y > (mesaPanel.ALTURA_MESA - (mesaPanel.BORDA_CAMPO / 4))) {
			return true;
		}
		return false;
	}

	private boolean defesaGoleiro(Rectangle r, Set bolaIngnora) {
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (bolaIngnora.contains(goleiro)) {
					return false;
				}
				if (goleiro.getShape(1).intersects(r)) {
					// if (Math.random() > .7) {
					// bolaIngnora.add(goleiro);
					// Logger.logar("frango");
					// return false;
					// }
					Logger.logar("Goleiro Defendeu");
					return true;
				}
			}
		}
		return false;
	}

	public void centralizaBotao(Botao b) {
		Rectangle rectangle = (Rectangle) limitesViewPort();
		if (rectangle == null)
			return;

		Point ori = new Point((int) rectangle.getCenterX() - 25,
				(int) rectangle.getCenterY() - 25);
		Point des = new Point((int) (b.getCentro().x * mesaPanel.zoom),
				(int) (b.getCentro().y * mesaPanel.zoom));
		List reta = GeoUtil.drawBresenhamLine(ori, des);
		Point p = des;
		if (!reta.isEmpty()) {
			int cont = reta.size() / 10;
			for (int i = cont; i < reta.size(); i += cont) {
				p = (Point) reta.get(i);
				if (rectangle.contains(p)) {
					p.x -= ((rectangle.width - 50) / 2);
					p.y -= ((rectangle.height - 50) / 2);
					break;
				}
			}
		}
		if (p.x < 0) {
			p.x = 1;
		}
		if (p.y < 0) {
			p.y = 1;
		}
		int largMax = (int) ((mesaPanel.getWidth() * mesaPanel.zoom) - scrollPane
				.getViewport().getWidth());
		if (p.x > largMax) {
			p.x = largMax - 1;
		}
		int altMax = (int) ((mesaPanel.getHeight() * mesaPanel.zoom) - (scrollPane
				.getViewport().getHeight()));
		if (p.y > altMax) {
			p.y = altMax - 1;
		}
		novoPontoTela = p;
	}

	public Botao getBola() {
		return (Botao) botoes.get(new Long(0));
	}

	public void bolaPenaltiCima() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getPenaltyCima().getLocation());
		centralizaBola();

	}

	public void bolaPenaltiBaixo() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getPenaltyBaixo().getLocation());
		centralizaBola();
	}

	public void bolaCentro() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		bola.setPosition(mesaPanel.getCentro().getLocation());
		centralizaBola();
	}

	private Botao obterUmCobrador(Time time) {
		List listBtns = time.getBotoes();
		List sortear = new ArrayList();
		for (Iterator iterator = listBtns.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (!(botao instanceof Goleiro)) {
				sortear.add(botao);
			}
		}
		Collections.shuffle(sortear);
		return (Botao) sortear.get(0);
	}

	public void escCimaDir(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}

		Point p = mesaPanel.getCampoCima().getLocation();
		p.x += mesaPanel.getCampoCima().getWidth() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
		if (times != null && times.length > 0) {
			Botao botao = obterUmCobrador(times[0]);
			limparPerimetro(p);
			botao.setCentroTodos(new Point2D.Double(p.x + botao.getDiamentro()
					* .55, p.y - botao.getDiamentro() * .55));
			Botao marcador = botao;
			while (marcador.equals(botao)) {
				marcador = obterUmCobrador(times[0]);
			}
			Point2D.Double posicao = new Point2D.Double(mesaPanel
					.getPequenaAreaCima().getBounds().getCenterX(), mesaPanel
					.getPequenaAreaCima().getBounds().getCenterY());
			while (mesaPanel.getPequenaAreaCima().contains(posicao)) {
				posicao = new Point2D.Double(Util.intervalo(mesaPanel
						.getGrandeAreaCima().x
						+ (mesaPanel.getGrandeAreaCima().getWidth() / 2),
						mesaPanel.getGrandeAreaCima().x
								+ mesaPanel.getGrandeAreaCima().getWidth()),
						Util.intervalo(mesaPanel.getGrandeAreaCima().y,
								mesaPanel.getGrandeAreaCima().y
										+ mesaPanel.getGrandeAreaCima()
												.getHeight()));
			}
			marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));
		}

	}

	public void escCimaEsc(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoCima().getLocation();
		bola.setPosition(mesaPanel.getCampoCima().getLocation());
		centralizaBola();
		if (times != null && times.length > 0) {
			Botao botao = obterUmCobrador(times[0]);
			limparPerimetro(p);
			botao.setCentroTodos(new Point2D.Double(p.x - botao.getDiamentro()
					* .55, p.y - botao.getDiamentro() * .55));
			Botao marcador = botao;
			while (marcador.equals(botao)) {
				marcador = obterUmCobrador(times[0]);
			}
			Point2D.Double posicao = new Point2D.Double(mesaPanel
					.getPequenaAreaCima().getBounds().getCenterX(), mesaPanel
					.getPequenaAreaCima().getBounds().getCenterY());
			while (mesaPanel.getPequenaAreaCima().contains(posicao)) {
				posicao = new Point2D.Double(Util.intervalo(mesaPanel
						.getGrandeAreaCima().x, mesaPanel.getGrandeAreaCima().x
						+ (mesaPanel.getGrandeAreaCima().getWidth() / 2)), Util
						.intervalo(mesaPanel.getGrandeAreaCima().y, mesaPanel
								.getGrandeAreaCima().y
								+ mesaPanel.getGrandeAreaCima().getHeight()));
			}
			marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));

		}
	}

	public void escBaixoDir(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoBaixo().getLocation();
		p.x += mesaPanel.getCampoBaixo().getWidth() - bola.getDiamentro();
		p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
		if (times != null && times.length > 0) {
			limparPerimetro(p);
			Botao botao = obterUmCobrador(times[0]);
			botao.setCentroTodos(new Point2D.Double(p.x + botao.getDiamentro()
					* .65, p.y + botao.getDiamentro() * .65));

			Botao marcador = botao;
			while (marcador.equals(botao)) {
				marcador = obterUmCobrador(times[0]);
			}
			Point2D.Double posicao = new Point2D.Double(mesaPanel
					.getPequenaAreaBaixo().getBounds().getCenterX(), mesaPanel
					.getPequenaAreaBaixo().getBounds().getCenterY());
			while (mesaPanel.getPequenaAreaBaixo().contains(posicao)) {
				posicao = new Point2D.Double(Util.intervalo(mesaPanel
						.getGrandeAreaBaixo().x
						+ (mesaPanel.getGrandeAreaBaixo().getWidth() / 2),
						mesaPanel.getGrandeAreaBaixo().x
								+ mesaPanel.getGrandeAreaBaixo().getWidth()),
						Util.intervalo(mesaPanel.getGrandeAreaBaixo().y,
								mesaPanel.getGrandeAreaBaixo().y
										+ mesaPanel.getGrandeAreaBaixo()
												.getHeight()));
			}
			marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));

		}
	}

	public void escBaixoEsc(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getCampoBaixo().getLocation();
		p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
		if (times != null && times.length > 0) {
			limparPerimetro(p);
			Botao botao = obterUmCobrador(times[0]);
			botao.setCentroTodos(new Point2D.Double(p.x - botao.getDiamentro()
					* .55, p.y + botao.getDiamentro() * .55));
			Botao marcador = botao;
			while (marcador.equals(botao)) {
				marcador = obterUmCobrador(times[0]);
			}
			Point2D.Double posicao = new Point2D.Double(mesaPanel
					.getPequenaAreaBaixo().getBounds().getCenterX(), mesaPanel
					.getPequenaAreaBaixo().getBounds().getCenterY());
			while (mesaPanel.getPequenaAreaBaixo().contains(posicao)) {
				posicao = new Point2D.Double(Util
						.intervalo(mesaPanel.getGrandeAreaBaixo().x,
								mesaPanel.getGrandeAreaBaixo().x
										+ (mesaPanel.getGrandeAreaBaixo()
												.getWidth() / 2)), Util
						.intervalo(mesaPanel.getGrandeAreaBaixo().y, mesaPanel
								.getGrandeAreaBaixo().y
								+ mesaPanel.getGrandeAreaBaixo().getHeight()));
			}
			marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));
		}
	}

	public void metaCima(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getPequenaAreaCima().getLocation();
		if (Math.random() > .5)
			p.x += mesaPanel.getPequenaAreaCima().getWidth()
					- bola.getDiamentro();
		p.y += mesaPanel.getPequenaAreaCima().getHeight() - bola.getDiamentro();
		bola.setPosition(p);
		centralizaBola();
		if (times != null && times.length > 0) {
			limparPerimetro(p);
			Botao botao = obterUmCobrador(times[0]);
			botao.setCentroTodos(new Point2D.Double(p.x, p.y
					- botao.getDiamentro()));
		}

	}

	public void metaBaixo(Time... times) {
		if (bola == null || mesaPanel == null) {
			return;
		}
		Point p = mesaPanel.getPequenaAreaBaixo().getLocation();
		if (Math.random() > .5)
			p.x += mesaPanel.getPequenaAreaBaixo().getWidth()
					- bola.getDiamentro();

		bola.setPosition(p);
		centralizaBola();
		if (times != null && times.length > 0) {
			limparPerimetro(p);
			Botao botao = obterUmCobrador(times[0]);
			botao.setCentroTodos(new Point2D.Double(p.x, p.y
					+ botao.getDiamentro()));
		}

	}

	public void lateral() {
		if (bola == null || mesaPanel == null) {
			return;
		}
		if (ultLateral != null) {
			bola.setCentroInicio(ultLateral);
			bola.setCentro(ultLateral);
			centralizaBola();
		}

	}

	public Point getLateral() {
		return ultLateral;
	}

	public void setLateral(Point lateral) {
		if (lateral != null) {
			if (lateral.x < 3000) {
				lateral.x += 20;
			} else {
				lateral.x -= 20;
			}
		}
		if (eventoAtual != null) {
			eventoAtual.setPonto(lateral);
			eventoAtual.setEventoCod(ConstantesMesa11.LATERAL);
		}
		this.ultLateral = lateral;
	}

	public void criarPopupMenu() {

		JPopupMenu popup = new JPopupMenu();
		JMenuItem moverBotao = new JMenuItem() {
			public String getText() {
				return Lang.msg("moverBotao");
			}
		};
		popup.add(moverBotao);
		moverBotao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moverBotao();
			}
		});
		JMenuItem soltarBotao = new JMenuItem() {
			public String getText() {
				return Lang.msg("soltarBotao");
			}
		};
		popup.add(soltarBotao);
		soltarBotao.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				soltarBotao();
			}
		});

		JMenuItem chutarBola = new JMenuItem() {
			public String getText() {
				return Lang.msg("chutarBola", new String[] { (chutaBola ? Lang
						.msg("sim") : Lang.msg("nao")) });
			}
		};
		popup.add(chutarBola);
		chutarBola.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chutarBola();
			}
		});

		JMenuItem limparPerimetro = new JMenuItem() {
			public String getText() {
				return Lang.msg("limparPerimetro");
			}
		};
		popup.add(limparPerimetro);
		limparPerimetro.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				limparPerimetro(pontoBtnDirClicado);
			}
		});

		JMenuItem limparPerimetroCirculo = new JMenuItem() {
			public String getText() {
				return Lang.msg("limparPerimetroCirculo");
			}
		};
		popup.add(limparPerimetroCirculo);
		limparPerimetroCirculo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				limparPerimetroCirculo(pontoBtnDirClicado);
			}
		});

		MouseListener popupListener = new PopupListener(popup, this);
		mesaPanel.addMouseListener(popupListener);
	}

	protected void limparPerimetro(Point p) {
		if (p == null) {
			return;
		}
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Bola || b instanceof Goleiro) {
				continue;
			}
			double reta = GeoUtil.distaciaEntrePontos(p, b.getCentro());
			if (reta < ConstantesMesa11.PERIMETRO) {
				posicionaBotaoAleatoriamenteNoSeuCampo(b);
			}
		}
	}

	protected void limparPerimetroCirculo(Point p) {
		limparPerimetroCirculo(p, ConstantesMesa11.PERIMETRO);
	}

	protected void limparPerimetroCirculo(Point p, double perimetro) {
		limparPerimetroCirculo(p, (int) perimetro);
	}

	protected void limparPerimetroCirculo(Point p, int perimetro) {
		if (p == null) {
			return;
		}
		List circulo = GeoUtil.drawCircle(p.x, p.y, perimetro);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (b instanceof Bola || b instanceof Goleiro) {
				continue;
			}
			double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(p, b
					.getCentro());
			if (distaciaEntrePontos < perimetro) {
				boolean botaoPosicionado = false;
				int cont = 0;
				Point point = null;
				while (!botaoPosicionado) {
					cont++;
					if (cont > 100) {
						botaoPosicionado = true;
						Logger.logar("Nao posicionou botao " + b);
					}
					point = (Point) circulo.get(Util.intervalo(0, circulo
							.size() - 1));
					if (!mesaPanel.getCampoBaixo().contains(point)
							&& !mesaPanel.getCampoCima().contains(point)) {
						continue;
					}
					if (verificaTemBotao(point)) {
						continue;
					}

					botaoPosicionado = true;
				}
				if (point != null)
					b.setCentroTodos(point);
			}
		}
	}

	private void posicionaBotaoAleatoriamenteNoSeuCampo(Botao b) {
		boolean botaoPosicionado = false;

		while (!botaoPosicionado) {
			if (ConstantesMesa11.CAMPO_CIMA == b.getTime().getCampo()) {

				int valx = Util.intervalo(mesaPanel.getCampoCima().x, mesaPanel
						.getCampoCima().x
						+ mesaPanel.getCampoCima().width);
				int valy = Util.intervalo(mesaPanel.getCampoCima().y, mesaPanel
						.getCampoCima().y
						+ mesaPanel.getCampoCima().height);
				Point point = new Point(valx, valy);
				if (verificaTemBotao(point)) {
					continue;
				}
				b.setCentroTodos(point);
				botaoPosicionado = true;
			} else {
				int valx = Util.intervalo(mesaPanel.getCampoBaixo().x,
						mesaPanel.getCampoBaixo().x
								+ mesaPanel.getCampoCima().width);
				int valy = Util.intervalo(mesaPanel.getCampoBaixo().y,
						mesaPanel.getCampoBaixo().y
								+ mesaPanel.getCampoCima().height);
				Point point = new Point(valx, valy);
				if (verificaTemBotao(point)) {
					continue;
				}
				b.setCentroTodos(point);
				botaoPosicionado = true;
			}
		}

	}

	public boolean verificaTemBotao(Point p) {
		if (p == null) {
			return false;
		}
		return verificaTemBotao(p, null);
	}

	public boolean verificaTemBotao(Point p, Botao exceto) {
		Rectangle2D rectangle2d = new Rectangle2D.Double(p.x
				- ConstantesMesa11.RAIO_BOTAO, p.y
				- ConstantesMesa11.RAIO_BOTAO,
				ConstantesMesa11.DIAMENTRO_BOTAO,
				ConstantesMesa11.DIAMENTRO_BOTAO);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao b = (Botao) botoes.get(id);
			if (exceto != null && exceto.equals(b)) {
				continue;
			}
			if (b.getShape(1).intersects(rectangle2d)) {
				return true;
			}
		}
		return false;
	}

	protected void soltarBotao() {
		if (botaoSelecionado != null) {
			if (verificaTemBotao(botaoSelecionado.getCentro(), botaoSelecionado)) {
				return;
			}
			botaoSelecionado.setCentroInicio(botaoSelecionado.getCentro());
			botaoSelecionado = null;
			carregaBotao = false;
		}

	}

	public boolean verificaDentroCampo(Botao botao) {
		if (mesaPanel == null)
			return true;
		return (mesaPanel.getCampoBaixo().intersects(
				botao.getShape(1).getBounds2D()) || mesaPanel.getCampoCima()
				.intersects(botao.getShape(1).getBounds2D()));
	}

	protected void chutarBola() {
		chutaBola = !chutaBola;

	}

	protected void moverBotao() {
		carregaBotao = true;
	}

	public MesaPanel getMesaPanel() {
		return mesaPanel;

	}

	public Point getPontoClicado() {
		return pontoClicado;
	}

	public void setPontoClicado(Point pontoClicado) {
		this.pontoClicado = pontoClicado;
	}

	public Botao getBotaoSelecionado() {
		return botaoSelecionado;
	}

	public void setBotaoSelecionado(Botao botaoSelecionado) {
		this.botaoSelecionado = botaoSelecionado;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public boolean isCarregaBotao() {
		return carregaBotao;
	}

	public void setCarregaBotao(boolean carregaBotao) {
		this.carregaBotao = carregaBotao;
	}

	public boolean isChutaBola() {
		return chutaBola;
	}

	public void setChutaBola(boolean chutaBola) {
		this.chutaBola = chutaBola;
	}

	public boolean isAnimando() {
		return !botoesComThread.isEmpty();
	}

	public Map getBotoes() {
		return botoes;
	}

	public Point getVelhoPontoTela() {
		return velhoPontoTela;
	}

	public void setVelhoPontoTela(Point velhoPontoTela) {
		this.velhoPontoTela = velhoPontoTela;
	}

	public Point getNovoPontoTela() {
		return novoPontoTela;
	}

	public void setNovoPontoTela(Point novoPontoTela) {
		this.novoPontoTela = novoPontoTela;
	}

	public Map getBotoesComThread() {
		return botoesComThread;
	}

	public void setBotoesComThread(Map botoesComThread) {
		this.botoesComThread = botoesComThread;
	}

	public Point getPontoPasando() {
		return pontoPasando;
	}

	public void setPontoPasando(Point pontoPasando) {
		this.pontoPasando = pontoPasando;
	}

	public void setZoom(double d) {
		mesaPanel.zoom = d;
		centralizaBola();

	}

	public int getNumRecursoes() {
		return numRecursoes;
	}

	public void setNumRecursoes(int numRecursoes) {
		this.numRecursoes = numRecursoes;
	}

	public JFrame getFrame() {
		return frame;
	}

	public Point getPontoBtnDirClicado() {
		return pontoBtnDirClicado;
	}

	public void setPontoBtnDirClicado(Point pontoBtnDirClicado) {
		this.pontoBtnDirClicado = pontoBtnDirClicado;
	}

	public String tempoJogoFormatado() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.getTempoJogoFormatado();
		}
		if (controlePartida == null) {
			return "";
		}
		return controlePartida.tempoJogoFormatado();
	}

	public String tempoRestanteJogoFormatado() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.getTempoRestanteJogoFormatado();
		}
		if (controlePartida == null) {
			return "";
		}
		if (jogoTerminado) {
			return "0";
		}
		return controlePartida.tempoRestanteJogoFormatado();
	}

	public String tempoJogadaRestanteJogoFormatado() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.getTempoJogadaRestanteJogoFormatado();
		}
		if (controlePartida == null) {
			return "";
		}
		if (jogoTerminado) {
			return "0";
		}
		return controlePartida.tempoJogadaRestanteJogoFormatado();
	}

	public void zerarTimerJogada() {
		controlePartida.zerarTimerJogada();

	}

	public boolean veririficaVez(Botao b) {
		if (dadosJogoSrvMesa11 != null) {
			return b.getTime().getNome()
					.equals(dadosJogoSrvMesa11.getTimeVez());
		}
		return controlePartida.veririficaVez(b);
	}

	public Time timeJogadaVez() {
		if (controlePartida == null) {
			return null;
		}
		return controlePartida.timeJogadaVez();
	}

	public Evento getEventoAtual() {
		return eventoAtual;
	}

	public void setEventoAtual(Evento eventoAutal) {
		this.eventoAtual = eventoAutal;
	}

	public void reversaoJogada() {
		if (isJogoOnlineCliente()) {
			return;
		}
		controlePartida.reversaoJogada();
	}

	public void zeraJogadaTime(Time time) {
		controlePartida.zeraJogadaTime(time);

	}

	public void falta(Point ponto, Botao levouFalta) {

		if (ConstantesMesa11.CAMPO_CIMA.equals(levouFalta.getTime().getCampo())) {
			if (mesaPanel.getGrandeAreaBaixo().contains(ponto)) {
				limparPerimetroCirculo(mesaPanel.getPenaltyBaixo()
						.getLocation(), mesaPanel.getGrandeAreaBaixo()
						.getWidth() * 0.55);
				Point penalBaixo = mesaPanel.getPenaltyBaixo().getLocation();
				getBola().setCentroTodos(
						new Point(penalBaixo.x + getBola().getRaio(),
								penalBaixo.y + getBola().getRaio()));
				levouFalta.setCentroTodos(new Point(penalBaixo.x, penalBaixo.y
						- Util.inte((levouFalta.getDiamentro() * 1.4))));
				controlePartida.centralizaGoleiroBaixo();
			} else {
				if (verificaBolaNoPerimetro(ponto)) {
					limparPerimetroCirculo(ponto);
					double calculaAngulo = GeoUtil.calculaAngulo(mesaPanel
							.golBaixo(), ponto, 90);
					Point p = GeoUtil.calculaPonto(calculaAngulo, levouFalta
							.getDiamentro(), ponto);
					levouFalta.setCentroTodos(p);
					bola.setCentroTodos(ponto);
				}
			}
		} else {
			if (mesaPanel.getGrandeAreaCima().contains(ponto)) {
				limparPerimetroCirculo(
						mesaPanel.getPenaltyCima().getLocation(), mesaPanel
								.getGrandeAreaCima().getWidth() * 0.55);
				Point penalCima = mesaPanel.getPenaltyCima().getLocation();
				getBola().setCentroTodos(
						new Point(penalCima.x + getBola().getRaio(),
								penalCima.y + getBola().getRaio()));
				levouFalta.setCentroTodos(new Point(penalCima.x, penalCima.y
						+ Util.inte((levouFalta.getDiamentro() * 1.4))));
				controlePartida.centralizaGoleiroCima();
			} else {
				if (verificaBolaNoPerimetro(ponto)) {
					limparPerimetroCirculo(ponto);
					double calculaAngulo = GeoUtil.calculaAngulo(mesaPanel
							.golCima(), ponto, 90);
					Point p = GeoUtil.calculaPonto(calculaAngulo, levouFalta
							.getDiamentro(), ponto);
					levouFalta.setCentroTodos(p);
					bola.setCentroTodos(ponto);
				}
			}
		}

		zeraJogadaTime(levouFalta.getTime());
	}

	private boolean verificaBolaNoPerimetro(Point ponto) {
		if (ponto == null) {
			return false;
		}
		double reta = GeoUtil.distaciaEntrePontos(ponto, bola.getCentro());
		return (reta < ConstantesMesa11.PERIMETRO);
	}

	public void porcessaLateral() {
		Time timeLateral = controlePartida.getTimeCima().equals(
				eventoAtual.getUltimoContato().getTime()) ? controlePartida
				.getTimeBaixo() : controlePartida.getTimeCima();
		int tamretaMin = Integer.MAX_VALUE;
		List btnsLateral = timeLateral.getBotoes();
		Botao botaoLateral = null;
		for (Iterator iterator = btnsLateral.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (!(botao instanceof Goleiro)) {
				double reta = GeoUtil.distaciaEntrePontos(ultLateral, botao
						.getCentro());
				if (reta < tamretaMin) {
					tamretaMin = (int) reta;
					botaoLateral = botao;
				}
			}
		}
		limparPerimetroCirculo(ultLateral);
		zeraJogadaTime(timeLateral);
		if (ultLateral.x < 3000) {
			botaoLateral.setCentroTodos(new Point(ultLateral.x
					- botaoLateral.getDiamentro(), ultLateral.y));
		} else {
			botaoLateral.setCentroTodos(new Point(ultLateral.x
					+ botaoLateral.getDiamentro(), ultLateral.y));
		}
		lateral();
	}

	public void salvarTime(Time time) {
		validaTime(time);
		if (isJogoOnlineCliente()) {
			salvarTimeOnline(time);
		} else {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
			encoder.writeObject(time);
			encoder.flush();
			JTextArea xmlArea = new JTextArea(30, 50);
			xmlArea.setText(new String(byteArrayOutputStream.toByteArray())
					+ "</java>");
			xmlArea.setEditable(false);
			xmlArea.setSelectionStart(0);
			xmlArea.setSelectionEnd(xmlArea.getCaretPosition());
			JScrollPane xmlPane = new JScrollPane(xmlArea);
			xmlPane.setBorder(new TitledBorder(Lang.msg("salvarTimeInfo")));
			JOptionPane.showMessageDialog(frame, xmlPane, Lang
					.msg("salvarTime"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void salvarTimeOnline(Time time) {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(time);
		mesa11to.setComando(ConstantesMesa11.SALVAR_TIME);
		mesa11Applet.enviarObjeto(mesa11to);
	}

	private void validaTime(Time time) {
		List botoesTime = time.getBotoes();
		List remover = new ArrayList();
		List adicionar = new ArrayList();
		for (Iterator iterator = botoesTime.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (!(botao instanceof Goleiro) && botao.isGoleiro()) {
				remover.add(botao);
				adicionar.add(BotaoUtils.converteGoleiro(botao));
			}
			if ((botao instanceof Goleiro) && !botao.isGoleiro()) {
				remover.add(botao);
				adicionar.add(BotaoUtils.converteBotao((Goleiro) botao));
			}
		}
		botoesTime.removeAll(remover);
		botoesTime.addAll(adicionar);

	}

	public void inserirBotaoEditor(Time time, BotaoTableModel botaoTableModel) {
		Botao botao = new Botao();
		botao.setTime(time);
		botao.setNome(time.getNome());
		int numero = 0;
		List botoes = time.getBotoes();
		for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
			Botao b = (Botao) iterator.next();
			if (b.getNumero() > numero) {
				numero = b.getNumero();
			}
		}
		botao.setNumero(numero + 1);
		botao.setTitular(true);
		botao.setGoleiro(false);
		botao.setForca(Util.intervalo(500, 1000));
		botao.setPrecisao(Util.intervalo(500, 1000));
		botao.setDefesa(Util.intervalo(500, 1000));
		botaoTableModel.inserirLinha(botao);
	}

	public void carregarTime() {
		try {
			JTextArea xmlArea = new JTextArea(30, 50);
			JScrollPane xmlPane = new JScrollPane(xmlArea);
			xmlPane.setBorder(new TitledBorder(Lang.msg("xmlTimeSalvoInfo")));
			JOptionPane.showMessageDialog(frame, xmlPane, Lang
					.msg("xmlTimeSalvo"), JOptionPane.INFORMATION_MESSAGE);

			if (Util.isNullOrEmpty(xmlArea.getText())) {
				return;
			}
			ByteArrayInputStream bin = new ByteArrayInputStream(xmlArea
					.getText().getBytes());
			XMLDecoder xmlDecoder = new XMLDecoder(bin);
			Time time = (Time) xmlDecoder.readObject();
			EditorTime editorTime = new EditorTime(time, this);
			JOptionPane.showMessageDialog(frame, editorTime);
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			StringBuffer retorno = new StringBuffer();
			int size = ((trace.length > 10) ? 10 : trace.length);
			for (int i = 0; i < size; i++)
				retorno.append(trace[i] + "\n");
			JOptionPane.showMessageDialog(frame, retorno.toString(), Lang
					.msg("erro"), JOptionPane.ERROR_MESSAGE);
			Logger.logarExept(e);
		}

	}

	public boolean verificaBolaPertoGoleiroTime(Time time) {
		return controlePartida.verificaBolaPertoGoleiroTime(time, bola);
	}

	public boolean verificaGol(Botao botao) {
		if (mesaPanel == null)
			return true;
		return (mesaPanel.getAreaGolBaixo().contains(
				botao.getShape(1).getBounds2D()) || mesaPanel.getAreaGolCima()
				.contains(botao.getShape(1).getBounds2D()));
	}

	public void setGol(Botao botao) {
		if (eventoAtual != null) {
			eventoAtual.setPonto(botao.getCentro());
			eventoAtual.setEventoCod(ConstantesMesa11.GOL);
		}
		ultGol = botao.getShape(1).getBounds2D();
	}

	public boolean verificaMetaEscanteio(Botao botao) {
		if (mesaPanel == null)
			return true;
		return (mesaPanel.getAreaEscateioBaixo().contains(
				botao.getShape(1).getBounds2D()) || mesaPanel
				.getAreaEscateioCima()
				.contains(botao.getShape(1).getBounds2D()));
	}

	public void setMetaEscanteio(Botao botao) {
		if (eventoAtual != null) {
			eventoAtual.setPonto(botao.getCentro());
			eventoAtual.setEventoCod(ConstantesMesa11.META_ESCANTEIO);
		}
		ultMetaEscanteio = botao.getShape(1).getBounds2D();
	}

	public Shape getUltGol() {
		return ultGol;
	}

	public Shape getUltMetaEscanteio() {
		return ultMetaEscanteio;
	}

	public void processarEscanteio(Time time) {
		Point escDir = null;
		Point escEsq = null;
		double distDir, distEsq;
		Point bolaEscanteio = ultMetaEscanteio.getBounds().getLocation();
		if (!controlePartida.getTimeCima().equals(time)) {
			escEsq = mesaPanel.getCampoCima().getLocation();
			Point p = mesaPanel.getCampoCima().getLocation();
			p.x += mesaPanel.getCampoCima().getWidth() - bola.getDiamentro();
			escDir = p;
			distDir = GeoUtil.distaciaEntrePontos(escDir, bolaEscanteio);
			distEsq = GeoUtil.distaciaEntrePontos(escEsq, bolaEscanteio);
			if (distDir < distEsq) {
				escCimaDir(time);
			} else {
				escCimaEsc(time);
			}
		} else {
			escEsq = mesaPanel.getCampoBaixo().getLocation();
			Point p = mesaPanel.getCampoBaixo().getLocation();
			p.x += mesaPanel.getCampoBaixo().getWidth() - bola.getDiamentro();
			escDir = p;
			distDir = GeoUtil.distaciaEntrePontos(escDir, bolaEscanteio);
			distEsq = GeoUtil.distaciaEntrePontos(escEsq, bolaEscanteio);
			if (distDir < distEsq) {
				escBaixoDir(time);
			} else {
				escBaixoEsc(time);
			}
		}
		reversaoJogada();
		Logger.logar("Escanteio " + time);

	}

	public void processarMeta(Time time) {
		Point metaDir = null;
		Point metaEsq = null;
		double distDir, distEsq;
		Point bolaEscanteio = ultMetaEscanteio.getBounds().getLocation();
		if (controlePartida.getTimeCima().equals(time)) {
			metaCima(time);
		} else {
			metaBaixo(time);
		}
		reversaoJogada();
	}

	public void processarGolContra(Time time) {
		controlePartida.processarGolContra(time);
		Logger.logar("GolContra " + time);

	}

	public void processarGol(Time time) {
		controlePartida.processarGol(time);
		reversaoJogada();
		Logger.logar("Gol " + time);

	}

	public void limparJogo() {
		Logger.logar("matarTodasThreads");
		frame.setVisible(false);
		WindowListener[] windowListeners = frame.getWindowListeners();
		for (int i = 0; i < windowListeners.length; i++) {
			frame.removeWindowListener(windowListeners[i]);
		}
		frame.getContentPane().removeAll();
		pararVideo();

	}

	private void pararVideo() {
		if (atualizadorTela != null) {
			atualizadorTela.setAlive(false);
		}
	}

	public void inicializaVideo() {
		pararVideo();
		atualizadorTela = new AtualizadorVisual(this);
		atualizadorTela.start();
		frame.setSize(800, 600);
		frame.setVisible(true);

	}

	public Time obterTimeMandante() {
		if (controlePartida == null) {
			return null;
		}
		return controlePartida.getTimeCima();
	}

	public Time obterTimeVisita() {
		if (controlePartida == null) {
			return null;
		}
		return controlePartida.getTimeBaixo();
	}

	public String verGols(Time time) {
		if (dadosJogoSrvMesa11 != null) {
			if (time.getNome().equals(dadosJogoSrvMesa11.getTimeCasa())) {
				return String.valueOf(dadosJogoSrvMesa11.getGolsCasa());
			}
			if (time.getNome().equals(dadosJogoSrvMesa11.getTimeVisita())) {
				return String.valueOf(dadosJogoSrvMesa11.getGolsVisita());
			}
			return " Erro ";
		}
		if (controlePartida == null) {
			return null;
		}
		return controlePartida.verGols(time);
	}

	public int verGolsInt(Time time) {
		if (controlePartida == null) {
			return 0;
		}
		return controlePartida.verGolsInt(time);
	}

	public Integer obterNumJogadas(Time time) {
		if (isJogoOnlineCliente()) {
			if (dadosJogoSrvMesa11.getTimeCasa().equals(time.getNome())) {
				return dadosJogoSrvMesa11.getNumeroJogadasTimeCasa();
			}
			if (dadosJogoSrvMesa11.getTimeVisita().equals(time.getNome())) {
				return dadosJogoSrvMesa11.getNumeroJogadasTimeVisita();
			}
			return null;
		}
		if (controlePartida == null) {
			return null;
		}
		return controlePartida.obterNumJogadas(time);
	}

	public boolean incrementaJogada() {
		if (controlePartida == null) {
			return false;
		}
		return controlePartida.incrementaJogada();
	}

	public void zerarJogadas() {
		controlePartida.zerarJogadas();
	}

	public Goleiro obterGoleiroCima() {
		return controlePartida.getTimeCima().obterGoleiro();
	}

	public Goleiro obterGoleiroBaixo() {
		return controlePartida.getTimeBaixo().obterGoleiro();
	}

	public void verificaBolaParouEmCimaBotao() {
		if (eventoAtual == null) {
			return;
		}
		if (eventoAtual.isBolaFora()) {
			return;
		}
		String eventoCod = eventoAtual.getEventoCod();
		if ((ConstantesMesa11.GOLEIRO_DEFESA.equals(eventoCod) || (ConstantesMesa11.CONTATO_BOTAO_BOLA
				.equals(eventoCod) || ConstantesMesa11.CONTATO_BOLA_BOTAO
				.equals(eventoCod)))) {

			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				if (id.intValue() == 0) {
					continue;
				}
				Botao botao = (Botao) botoes.get(id);
				if (botao instanceof Goleiro) {
					continue;
				}
				double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(
						getBola().getCentro(), botao.getCentro());
				if (distaciaEntrePontos < botao.getDiamentro()
						&& verificaDentroCampo(bola)) {
					eventoAtual.setUltimoContato(botao);
					Logger.logar("verificaBolaParouEmCimaBotao " + botao);
				}
			}
		}
	}

	public boolean isJogoOnlineCliente() {
		return mesa11Applet != null;
	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		if (mesa11Applet == null) {
			Logger.logar("enviarObjeto mesa11Applet null");
			return null;
		}
		return mesa11Applet.enviarObjeto(mesa11to);
	}

	public DadosJogoSrvMesa11 getDadosJogoSrvMesa11() {
		return dadosJogoSrvMesa11;
	}

	public void setDadosJogoSrvMesa11(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public boolean verificaVezOnline() {
		return timeClienteOnline.equals(dadosJogoSrvMesa11.getTimeVez());
	}

	public boolean isJogoOnlineSrvidor() {
		return jogoServidor != null;
	}

	public void efetuarJogada() {
		if (isJogoOnlineCliente()) {
			efetuaJogadaCliente();
			return;
		}
		Point p1 = getPontoClicado();
		Point p2 = getPontoPasando();
		efetuaJogada(p1, p2);
	}

	public boolean efetuaJogada(Point p1, Point p2) {
		if (controleDicas != null) {
			controleDicas.mudarDica();
		}
		Evento evento = new Evento();
		animacaoCliente = null;
		animacaoJogada = null;
		double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(p1, p2);

		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (goleiro.getShape(1).contains(p1)) {
					if (veririficaVez(goleiro)) {
						boolean returnGoleiro = false;
						double rotacao = goleiro.getRotacao();
						Point centroGoleiro = goleiro.getCentro();
						double retaGoleiro = GeoUtil.distaciaEntrePontos(
								goleiro.getCentro(), p1);

						if (retaGoleiro > (goleiro.getRaio() / 2)) {
							goleiro.setRotacao(GeoUtil.calculaAngulo(goleiro
									.getCentro(), p2, 0));
							if (((goleiro.getTime().equals(getTimeCima()) && mesaPanel
									.getGrandeAreaCima().contains(
											goleiro.getShape(1).getBounds())) || (goleiro
									.getTime().equals(getTimeBaixo()) && mesaPanel
									.getGrandeAreaBaixo().contains(
											goleiro.getShape(1).getBounds())))
									&& !goleiro.getShape(1).intersects(
											bola.getShape(1).getBounds2D())) {
								evento.setPonto(p2);
								evento
										.setEventoCod(ConstantesMesa11.GOLEIRO_ROTACAO);

								returnGoleiro = true;
							} else {
								goleiro.setRotacao(rotacao);
							}
						} else {
							goleiro.setCentroTodos(p2);
							if (((goleiro.getTime().equals(getTimeCima()) && mesaPanel
									.getGrandeAreaCima().contains(
											goleiro.getShape(1).getBounds())) || (goleiro
									.getTime().equals(getTimeBaixo()) && mesaPanel
									.getGrandeAreaBaixo().contains(
											goleiro.getShape(1).getBounds())))
									&& !goleiro.getShape(1).intersects(
											bola.getShape(1).getBounds2D())) {
								goleiro.setCentroTodos(p2);
								evento.setPonto(p2);
								evento
										.setEventoCod(ConstantesMesa11.GOLEIRO_MOVEU);
								returnGoleiro = true;
							} else {
								goleiro.setCentroTodos(centroGoleiro);
							}
						}
						setPontoClicado(null);
						if (isJogoOnlineSrvidor() && returnGoleiro) {
							animacaoCliente = new Animacao();
							animacaoCliente.setTimeStamp(System
									.currentTimeMillis());
						}

						return returnGoleiro;
					}
				}
			}
			if (botao.getCentro() == null) {
				continue;
			}
			double raioPonto = GeoUtil.distaciaEntrePontos(p1, botao
					.getCentro());
			if (raioPonto <= botao.getRaio()) {
				if (!veririficaVez(botao)) {
					setPontoClicado(null);
					return false;
				}
				if (botao instanceof Bola) {
					boolean areaGoleiroCima = false;
					Goleiro goleiroCima = obterGoleiroCima();
					double distaciaEntrePontosCima = GeoUtil
							.distaciaEntrePontos(botao.getCentro(), goleiroCima
									.getCentro());
					if (distaciaEntrePontosCima < goleiroCima.getDiamentro()
							&& verificaDentroCampo(bola)) {
						areaGoleiroCima = true;
						evento.setUltimoContato(goleiroCima);
						evento.setNaBola(true);
						evento.setEventoCod(ConstantesMesa11.CHUTE_GOLEIRO);
					}
					boolean areaGoleiroBaixo = false;
					Goleiro goleiroBaixo = obterGoleiroBaixo();
					double distaciaEntrePontosBaixo = GeoUtil
							.distaciaEntrePontos(botao.getCentro(),
									goleiroBaixo.getCentro());
					if (distaciaEntrePontosBaixo < goleiroBaixo.getDiamentro()) {
						areaGoleiroBaixo = true;
						evento.setUltimoContato(goleiroBaixo);
						evento.setNaBola(true);
						evento.setEventoCod(ConstantesMesa11.CHUTE_GOLEIRO);
					}
					if (!areaGoleiroBaixo && !areaGoleiroCima) {
						continue;
					}
				}
				setLateral(null);
				if (botao instanceof Goleiro) {
					setPontoClicado(null);
					return false;
				}
				double angulo = GeoUtil.calculaAngulo(botao.getCentro(), p2,
						270);
				if (isChutaBola()) {
					angulo = GeoUtil.calculaAngulo(botao.getCentro(), getBola()
							.getCentro(), 90);
				}

				Point destino = GeoUtil.calculaPonto(angulo, Util
						.inte(distaciaEntrePontos * 10), botao.getCentro());
				botao.setDestino(destino);
				evento.setPonto(p1);
				evento.setBotaoEvento(botao);
				animacaoJogada = new Animacao();
				if (botao.getCentroInicio() == null)
					botao.setCentroInicio(botao.getCentro());
				animacaoJogada.setObjetoAnimacao(botao.getId());
				animacaoJogada.setPontosAnimacao(botao.getTrajetoria());
				setNumRecursoes(0);
				setEventoAtual(evento);
				propagaColisao(animacaoJogada, botao);
				verificaBolaParouEmCimaBotao();
				break;
			}

		}
		if (animacaoJogada == null) {
			setPontoClicado(null);
			return false;
		}
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao.getCentroInicio() != null)
				botao.setCentro(botao.getCentroInicio());
		}
		getBotoesComThread().clear();
		Animador animador = new Animador(animacaoJogada, this);
		Thread thread = new Thread(animador);
		getBotoesComThread().put(animacaoJogada.getObjetoAnimacao(), thread);
		thread.start();
		setPontoClicado(null);
		zerarTimerJogada();
		Thread threadEventos = new Thread(new ControleEvento(this));
		threadEventos.start();
		return true;
	}

	public void sairJogoOnline() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.SAIR_JOGO);
		mesa11to.setData(nomeJogadorOnline);
		Object ret = enviarObjeto(mesa11to);
	}

	private void efetuaJogadaCliente() {
		Mesa11TO mesa11to = new Mesa11TO();
		JogadaMesa11 jogadaMesa11 = new JogadaMesa11(timeClienteOnline,
				dadosJogoSrvMesa11);
		Point p1 = getPontoClicado();
		Point p2 = getPontoPasando();
		jogadaMesa11.setPontoClicado(p1);
		jogadaMesa11.setPontoSolto(p2);
		if (jogadaMesa11.getPontoClicado() == null
				|| jogadaMesa11.getPontoSolto() == null
				|| (stampUltimaJogadaOnline + 500) > System.currentTimeMillis()
				|| !verificaTemBotao(jogadaMesa11.getPontoClicado())) {
			setDica(ConstantesMesa11.JOGADA_INVALIDA);
			return;
		}

		mesa11to.setData(jogadaMesa11);
		mesa11to.setComando(ConstantesMesa11.JOGADA);
		esperandoJogadaOnline = true;
		stampUltimaJogadaOnline = System.currentTimeMillis();
		Object ret = enviarObjeto(mesa11to);
		if (!ConstantesMesa11.OK.equals(ret)) {
			setDica(ConstantesMesa11.PROBLEMA_REDE);
			esperandoJogadaOnline = false;
		}
		setPontoClicado(null);
		setPontoPasando(null);
	}

	public Object obterUltimaJogada() {
		// int size = listaAnimacoes.size() - 1;
		// if (size < 0) {
		// return null;
		// }
		// Animacao animacao = listaAnimacoes.get(size);
		// animacao.setIndex(size);
		return animacaoCliente;
	}

	public void executaAnimacao(final Animacao animacao) {
		Animador animador = new Animador(animacao, this);
		Thread thread = new Thread(animador);
		if (animacao.getObjetoAnimacao() != null)
			getBotoesComThread().put(animacao.getObjetoAnimacao(), thread);
		thread.start();
		Thread threadAtualizaBotoesClienteOnline = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isAnimando()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Logger.logarExept(e);
					}
				}
				atualizaBotoesClienteOnline(animacao.getTimeStamp(), animacao
						.getObjetoAnimacao() != null);
				esperandoJogadaOnline = false;
			}
		});
		threadAtualizaBotoesClienteOnline.start();
	}

	public void atualizaBotoesClienteOnline(long timeStampAnimacao,
			boolean centralizaBola) {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_POSICAO_BOTOES);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo() + "-"
				+ timeStampAnimacao);
		Object ret = enviarObjeto(mesa11to);
		if (ret != null && ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11 = (PosicaoBtnsSrvMesa11) mesa11to
					.getData();
			if (posicaoBtnsSrvMesa11 != null) {
				List<BotaoPosSrvMesa11> btns = posicaoBtnsSrvMesa11.getBotoes();
				synchronized (botoes) {
					for (BotaoPosSrvMesa11 botaoPosSrvMesa11 : btns) {
						if (isAnimando()) {
							continue;
						}
						Botao botao = (Botao) botoes.get(botaoPosSrvMesa11
								.getId());
						botao.setCentroTodos(new Point(botaoPosSrvMesa11
								.getPos()));
						if (botao instanceof Goleiro) {
							Goleiro goleiro = (Goleiro) botao;
							goleiro.setRotacao(botaoPosSrvMesa11.getRotacao());
						}
					}
				}
			}
		}
		esperandoJogadaOnline = false;
		if (centralizaBola)
			centralizaBola();
	}

	public int getNumeroJogadas() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.getNumeroJogadas();
		}
		if (isJogoOnlineSrvidor()) {
			return jogoServidor.getDadosJogoSrvMesa11().getNumeroJogadas();
		}
		return numeroJogadas;
	}

	public void setNumeroJogadas(int numeroJogadas) {
		this.numeroJogadas = numeroJogadas;
	}

	public boolean isEsperandoJogadaOnline() {
		return esperandoJogadaOnline;
	}

	public void configuraAnimacaoServidor() {
		if (isJogoOnlineSrvidor()) {
			listaAnimacoes.add(animacaoJogada);
			animacaoCliente = animacaoJogada;
			animacaoCliente.setTimeStamp(System.currentTimeMillis());
		}

	}

	public Animacao getAnimacaoCliente() {
		return animacaoCliente;
	}

	public void setAnimacaoCliente(Animacao animacaoCliente) {
		this.animacaoCliente = animacaoCliente;
	}

	public String getDica() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.getDica();
		}
		return dica;
	}

	public void setDica(String dica) {
		this.dica = dica;
	}

	public boolean isJogoTerminado() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.isJogoTerminado();
		}
		return jogoTerminado;
	}

	public void setJogoTerminado(boolean jogoTerminado) {
		if (tempoTerminado == 0) {
			tempoTerminado = System.currentTimeMillis();
		}
		this.jogoTerminado = jogoTerminado;
	}

	public boolean isJogoIniciado() {
		if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
			return dadosJogoSrvMesa11.isJogoTerminado();
		}
		return jogoIniciado;
	}

	public void setJogoIniciado(boolean jogoIniciado) {
		if (tempoIniciado == 0) {
			tempoIniciado = System.currentTimeMillis();
		}
		this.jogoIniciado = jogoIniciado;
	}

	public void mudarDica() {
		if (controleDicas != null) {
			controleDicas.mudarDica();
		}

	}

	public void fimJogoServidor() {
		if (!isJogoOnlineSrvidor()) {
			return;
		}
		jogoServidor.fimJogoServidor();

	}

}
