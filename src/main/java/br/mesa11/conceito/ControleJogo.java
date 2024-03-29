package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
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
import br.nnpe.tos.NnpeTO;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;
import br.tos.BotaoPosSrvMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.JogadaMesa11;
import br.tos.PosicaoBtnsSrvMesa11;

public class ControleJogo {
    private ControleDicas controleDicas;
    private ControlePartida controlePartida;
    private Mesa11Applet mesa11Applet;
    private JogoServidor jogoServidor;
    private JFrame frame;
    private Map<Long, Botao> botoes = new HashMap<Long, Botao>();
    private Map<Long, Thread> botoesComThread = new HashMap<Long, Thread>();
    private Map<Long, Animacao> animacoesCliente = new HashMap<Long, Animacao>();
    private Map botoesImagens = new HashMap();
    private Botao bola;
    private MesaPanel mesaPanel;
    private JScrollPane scrollPane;
    private Point velhoPontoTela;
    private Point novoPontoTela;
    private Point ultLateral;
    private Shape ultGol;
    private Shape ultMetaEscanteio;
    private Botao botaoSelecionado;
    private Point pontoClicado;
    private Point pontoBtnDirClicado;
    private Point pontoPasando;
    private Point pontoArrastando;
    private boolean carregaBotao;
    private boolean chutaBola;
    private int numRecursoes;
    private Evento eventoAtual;
    private AtualizadorVisual atualizadorTela;
    private String timeClienteOnline;
    private DadosJogoSrvMesa11 dadosJogoSrvMesa11;

    private Animacao animacaoJogada = null;
    private boolean esperandoJogadaOnline;
    private boolean saiuJogoOnline;
    private int numeroJogadas;
    private String dica;
    private boolean jogoTerminado;
    private long tempoTerminado;
    private boolean jogoIniciado;
    private long tempoIniciado;
    private String nomeJogadorOnline;
    private String nivelJogo = ConstantesMesa11.NIVEL_MEDIO;
    public Point ptDstBola;
    public Point golJogadaCpu;
    private boolean processando;
    private String codeBase;
    private JProgressBar progressBar;
    private JFrame progressBarFrame;
    private Point posicaoBolaJogada;
    private Thread autoCloseProgressBar;
    private boolean autoMira;
    private boolean controleEventosRodando;
    private Botao btnAssistido;
    Map<Long, Botao> botoesCopy;
    private Long sequenciaAnimacao = 0l;

    public ControleJogo(Mesa11Applet mesa11Applet, String timeClienteOnline,
                        DadosJogoSrvMesa11 dadosJogoSrvMesa11, String nomeJogadorOnline) {
        this.mesa11Applet = mesa11Applet;
        this.timeClienteOnline = timeClienteOnline;
        this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
        this.frame = new JFrame();
        criarProgressBar();
        Logger.logar("criarProgressBar()");
        mesaPanel = new MesaPanel(this);
        scrollPane = new JScrollPane(mesaPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        adicinaListentesEventosMouse();
        adicinaListentesEventosTeclado();
        this.nomeJogadorOnline = nomeJogadorOnline;
        frame.setTitle(ConstantesMesa11.TITULO + getVersao() + " "
                + timeClienteOnline);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        if (isJogoOnlineCliente()) {
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }
        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                int ret = JOptionPane.showConfirmDialog(frame,
                        Lang.msg("sairJogo"), Lang.msg("confirmaSairJogo"),
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.NO_OPTION) {
                    return;
                }
                if (isJogoOnlineCliente()) {
                    Logger.logar("isJogoOnlineCliente()");
                    sairJogoOnline();
                }
                matarTodasThreads();
                super.windowClosing(e);
            }

        });
        frame.addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(WindowEvent e) {
                if (6 == e.getNewState()) {
                    Toolkit kit = frame.getToolkit();
                    Dimension screenSize = kit.getScreenSize();
                    int meio = (int) (screenSize.getWidth() / 2);
                    frame.setLocation(meio - 500, 0);
                    frame.setSize(1000, frame.getHeight() - 15);
                }
            }
        });
    }

    public boolean isSaiuJogoOnline() {
        return saiuJogoOnline;
    }

    public void setSaiuJogoOnline(boolean saiuJogoOnline) {
        this.saiuJogoOnline = saiuJogoOnline;
    }

    private String getVersao() {
        return mesa11Applet.getVersao();
    }

    public long getTempoTerminado() {
        return tempoTerminado;
    }

    public long getTempoIniciado() {
        return tempoIniciado;
    }

    public String getNivelJogo() {
        return nivelJogo;
    }

    public void setNivelJogo(String nivelJogo) {
        this.nivelJogo = nivelJogo;
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
                int ret = JOptionPane.showConfirmDialog(ControleJogo.this.frame,
                        Lang.msg("sairJogo"), Lang.msg("confirmaSairJogo"),
                        JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.NO_OPTION) {
                    return;
                }
                if (isJogoOnlineCliente()) {
                    sairJogoOnline();
                }
                matarTodasThreads();
                super.windowClosing(e);
                System.exit(0);
            }

        });
        bola = new Bola(0);
        BufferedImage buff = CarregadorRecursos.carregaImg("bola.png");
        BufferedImage newBuffer = new BufferedImage((buff.getWidth()),
                (buff.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
        setarHints(graphics2d);
        Ellipse2D externo = new Ellipse2D.Double(0, 0, (buff.getWidth()),
                (buff.getHeight()));
        graphics2d.setClip(externo);
        graphics2d.drawImage(buff, 1, 1, null);
        botoesImagens.put(bola.getId(), newBuffer);

        botoes.put(bola.getId(), bola);
    }

    private void setarHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
    }

    public ControleJogo(JogoServidor jogoServidor) {
        this.frame = frame;
        this.jogoServidor = jogoServidor;
        mesaPanel = new MesaPanel(this);
        scrollPane = new JScrollPane(mesaPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bola = new Bola(0);
        botoes.put(bola.getId(), bola);
    }

    public ControleJogo() {
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
                Point p = new Point(
                        (int) (scrollPane.getViewport().getViewPosition().x),
                        (int) (scrollPane.getViewport().getViewPosition().y));

                if (keycode == KeyEvent.VK_LEFT) {
                    p.x -= 10;
                } else if (keycode == KeyEvent.VK_RIGHT) {
                    p.x += 10;
                } else if (keycode == KeyEvent.VK_UP) {
                    p.y -= 10;
                } else if (keycode == KeyEvent.VK_DOWN) {
                    p.y += 10;
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
        boolean iniciaJogoLivre = controlePartida.iniciaJogoLivre();
        if (iniciaJogoLivre) {
            criarProgressBar();
            controleDicas = new ControleDicas(this);
            setDica("inicioJogo");
            AtualizadorJogadaCPU atualizadorJogadaCPU = new AtualizadorJogadaCPU(
                    this);
            atualizadorJogadaCPU.start();
        }
    }

    private void mostraProgressBar() {
        if (progressBarFrame != null && !progressBarFrame.isVisible()) {
            progressBarFrame.requestFocus();
            progressBarFrame.setVisible(true);
            if (autoCloseProgressBar == null
                    || !autoCloseProgressBar.isAlive()) {
                autoCloseProgressBar = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        escondePorgressBar();
                    }
                });
                autoCloseProgressBar.start();
            }
            return;
        }
    }

    public void iniciaJogoOnline(DadosJogoSrvMesa11 dadosJogoSrvMesa11,
                                 Time timeCasa, Time timeVisita) {
        bola = new Bola(0);
        autoMira = dadosJogoSrvMesa11.isAutoMira();
        if (isJogoOnlineCliente()) {
            BufferedImage buff = CarregadorRecursos.carregaImg("bola.png");
            BufferedImage newBuffer = new BufferedImage((buff.getWidth()),
                    (buff.getHeight()), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
            setarHints(graphics2d);
            Ellipse2D externo = new Ellipse2D.Double(0, 0, (buff.getWidth()),
                    (buff.getHeight()));
            graphics2d.setClip(externo);
            graphics2d.drawImage(buff, 1, 1, null);
            botoesImagens.put(bola.getId(), newBuffer);
        }
        botoes.put(bola.getId(), bola);
        controlePartida = new ControlePartida(this);
        controleDicas = new ControleDicas(this);
        controlePartida.iniciaJogoOnline(dadosJogoSrvMesa11, timeCasa,
                timeVisita);
        bolaCentro();
        setJogoIniciado(true);
    }

    public void centralizaBola() {
        if (bola == null || bola.getCentro() == null) {
            centroCampo();
            return;
        }
        Point p = new Point(
                (int) (bola.getCentro().x * mesaPanel.zoom)
                        - (scrollPane.getViewport().getWidth() / 2),
                (int) (bola.getCentro().y * mesaPanel.zoom)
                        - (scrollPane.getViewport().getHeight() / 2));
        if (p.x < 0) {
            p.x = 1;
        }
        double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom)
                - scrollPane.getViewport().getWidth());
        if (p.x > maxX) {
            p.x = Util.inte(maxX) - 1;
        }
        if (p.y < 0) {
            p.y = 1;
        }
        double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom)
                - (scrollPane.getViewport().getHeight()));
        if (p.y > maxY) {
            p.y = Util.inte(maxY) - 1;
        }
        novoPontoTela = p;
        // pontoPasando = bola.getCentro();
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

    public Shape miniViewPort() {
        Rectangle limitesViewPort = (Rectangle) limitesViewPort();
        Rectangle rectangle = new Rectangle(limitesViewPort.x + 90,
                limitesViewPort.y + 90, limitesViewPort.width - 220,
                limitesViewPort.height - 220);
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
        double maxX = ((mesaPanel.getWidth() * mesaPanel.zoom)
                - scrollPane.getViewport().getWidth());
        if (p.x > maxX) {
            p.x = Util.inte(maxX) - 1;
        }
        if (p.y < 0) {
            p.y = 1;
        }
        double maxY = ((mesaPanel.getHeight() * mesaPanel.zoom)
                - (scrollPane.getViewport().getHeight()));
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
                    Rectangle rectangle = new Rectangle(
                            point.x - bola.getRaio(), point.y - bola.getRaio(),
                            bola.getDiamentro(), bola.getDiamentro());
                    boolean defesaGoleiro = defesaGoleiro(rectangle,
                            bolaIngnora);
                    if (!bolaBateu
                            && (mesaPanel.verificaIntersectsGol(rectangle)
                            || defesaGoleiro)) {
                        bolaBateu = true;
                        double angulo = GeoUtil.calculaAngulo(point,
                                botao.getDestino(), 0);
                        double rebatimentoBola = trajetoriaBotao.size();
                        if (defesaGoleiro) {
                            angulo = GeoUtil.calculaAngulo(
                                    botao.getCentroInicio(), point, 90);
                            angulo = 180 - angulo;
                            if (!verificaDentroCampo(botao))
                                rebatimentoBola *= .3;
                            else {
                                rebatimentoBola *= .5;
                            }
                            eventoAtual.setPonto(point);
                            eventoAtual.setEventoCod(
                                    ConstantesMesa11.GOLEIRO_DEFESA);
                        }

                        while (i < trajetoriaBotao.size()) {
                            trajetoriaBotao.remove(trajetoriaBotao.size() - 1);
                        }
                        Point destino = GeoUtil.calculaPonto(angulo,
                                Util.inte(rebatimentoBola * 0.3),
                                botao.getCentro());
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
                        .hasNext(); ) {
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

                    if ((GeoUtil.distaciaEntrePontos(point,
                            botaoAnalisado.getCentro())
                            - (botao.getRaio())) <= (botaoAnalisado
                            .getRaio())) {
                        if (((botao instanceof Bola
                                && bolaIngnora.contains(botaoAnalisado))
                                || (botaoAnalisado instanceof Bola
                                && bolaIngnora.contains(botao)))
                                && verificaDentroCampo(bola)) {
                            eventoAtual.setUltimoContato(botaoAnalisado);
                            continue;
                        }
                        if ((botao instanceof Bola)
                                && Math.random() > botaoAnalisado.getDefesa()
                                / 1000.0) {
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
                            eventoAtual.setEventoCod(
                                    ConstantesMesa11.CONTATO_BOTAO_BOLA);
                            detAtingido *= (1 - (i / detAtingido));
                            bolaIngnora.add(botao);
                        } else {
                            if ((botao instanceof Bola)) {
                                eventoAtual.setPonto(point);
                                eventoAtual.setUltimoContato(botaoAnalisado);
                                eventoAtual.setEventoCod(
                                        ConstantesMesa11.CONTATO_BOTAO_BOLA);
                                detAtingido *= 0.05;
                            } else {
                                Logger.logar(
                                        "botaoAnalisado " + botaoAnalisado);
                                detAtingido *= 0.3;
                            }

                        }
                        if (Math.random() > botao.getPrecisao() / 1000.0) {
                            int variancia = 5
                                    * (10 - botao.getPrecisao() / 100);
                            angulo += Util.intervalo(-(variancia), variancia);
                        }
                        destino = GeoUtil.calculaPonto(angulo,
                                Util.inte(detAtingido),
                                botaoAnalisado.getCentro());
                        botaoAnalisado.setDestino(destino);
                        animacao = new Animacao();
                        if (botaoAnalisado.getCentroInicio() == null)
                            botaoAnalisado.setCentroInicio(
                                    botaoAnalisado.getCentro());
                        animacao.setObjetoAnimacao(botaoAnalisado.getId());
                        animacao.setPontosAnimacao(
                                botaoAnalisado.getTrajetoria());
                        trajetoriaBotao.set(i, animacao);
                        List novaTrajetoria = new ArrayList();

                        /**
                         * Rebatimento de bola em botão
                         */
                        int dest = 0;
                        if ((botao instanceof Bola)) {
                            angulo = GeoUtil.calculaAngulo(
                                    botaoAnalisado.getCentro(),
                                    bola.getCentro(), 90);
                            dest = Util.inte(trajetoriaBotao.size() * .2);
                            // Logger
                            // .logar("Rebatimento de bola em botão (Botao Bola)
                            // dest="
                            // + dest);
                        } else if ((botaoAnalisado instanceof Bola)) {
                            angulo = GeoUtil.calculaAngulo(botao.getCentro(),
                                    botao.getDestino(), 90);
                            dest = Util.inte(detAtingido * .4);
                            // Logger
                            // .logar("Rebatimento de bola em botão
                            // (BotaoAnalizado Bola) dest="
                            // + dest);
                        } else {
                            angulo = GeoUtil.calculaAngulo(
                                    botaoAnalisado.getCentro(), point, 90);
                            dest = Util.inte(trajetoriaBotao.size() * .1);
                            // Logger.logar("Botão Com Botão");
                            if (!eventoAtual.isNaBola()) {
                                eventoAtual.setPonto(point);
                                eventoAtual.setUltimoContato(botaoAnalisado);
                                eventoAtual.setEventoCod(
                                        ConstantesMesa11.CONTATO_BOTAO_BOTAO);
                            }
                        }

                        destino = GeoUtil.calculaPonto(angulo, dest,
                                botao.getCentro());
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
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao g = (Botao) botoes.get(id);
            if (g instanceof Goleiro) {
                Goleiro goleiro = (Goleiro) g;
                if (GeoUtil
                        .drawBresenhamLine(botao.getCentro(),
                                goleiro.getCentro())
                        .size() < goleiro.getRaio()) {
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
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao botao = (Botao) botoes.get(id);
            if (botao instanceof Goleiro) {
                Goleiro goleiro = (Goleiro) botao;
                if (bolaIngnora.contains(goleiro)) {
                    return false;
                }
                if (goleiro.getShape(1).intersects(r)) {
                    if (posicaoBolaJogada != null && (mesaPanel
                            .getPequenaAreaBaixo().contains(posicaoBolaJogada)
                            || mesaPanel.getPequenaAreaCima()
                            .contains(posicaoBolaJogada))) {
                        return true;
                    }

                    if (Math.random() > goleiro.getDefesa() / 1000.0) {
                        bolaIngnora.add(goleiro);
                        Logger.logar("frango");
                        return false;
                    }
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
        int largMax = (int) ((mesaPanel.getWidth() * mesaPanel.zoom)
                - scrollPane.getViewport().getWidth());
        if (p.x > largMax) {
            p.x = largMax - 1;
        }
        int altMax = (int) ((mesaPanel.getHeight() * mesaPanel.zoom)
                - (scrollPane.getViewport().getHeight()));
        if (p.y > altMax) {
            p.y = altMax - 1;
        }
        novoPontoTela = p;
        pontoPasando = p;
    }

    public void centralizaPonto(Point2D po) {
        Rectangle rectangle = (Rectangle) limitesViewPort();
        if (rectangle == null)
            return;
        if (po == null) {
            return;
        }
        Point ori = new Point((int) rectangle.getCenterX(),
                (int) rectangle.getCenterY());
        Point des = new Point((int) (po.getX() * mesaPanel.zoom),
                (int) (po.getY() * mesaPanel.zoom));
        List reta = GeoUtil.drawBresenhamLine(ori, des);
        Point p = des;
        if (!reta.isEmpty()) {
            int cont = reta.size() / 50;
            for (int i = cont; i < reta.size(); i += cont) {
                p = (Point) reta.get(i);
                if (rectangle.contains(p)) {
                    p.x -= ((rectangle.width) / 2);
                    p.y -= ((rectangle.height) / 2);
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
        int largMax = (int) ((mesaPanel.getWidth() * mesaPanel.zoom)
                - scrollPane.getViewport().getWidth());
        if (p.x > largMax) {
            p.x = largMax - 1;
        }
        int altMax = (int) ((mesaPanel.getHeight() * mesaPanel.zoom)
                - (scrollPane.getViewport().getHeight()));
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
        bola.setCentroTodos(mesaPanel.getCentro().getLocation());
        centralizaBola();
        adicionaJogadaCliente();
    }

    private Botao obterUmCobrador(Time time) {
        List listBtns = time.getBotoes();
        List sortear = new ArrayList();
        for (Iterator iterator = listBtns.iterator(); iterator.hasNext(); ) {
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
        Point p = obterEscanteioDireitoCima();
        bola.setCentroTodos(p);
        centralizaBola();
        if (times != null && times.length > 0) {
            Botao botao = obterUmCobrador(times[0]);
            limparPerimetro(p);
            botao.setCentroTodos(
                    new Point2D.Double(p.x + botao.getDiamentro() * .55,
                            p.y - botao.getDiamentro() * .55));
            Botao marcador = botao;
            while (marcador.equals(botao)) {
                marcador = obterUmCobrador(times[0]);
            }
            Point2D.Double posicao = new Point2D.Double(
                    mesaPanel.getPequenaAreaCima().getBounds().getCenterX(),
                    mesaPanel.getPequenaAreaCima().getBounds().getCenterY());
            while (mesaPanel.getPequenaAreaCima().contains(posicao)) {
                posicao = new Point2D.Double(
                        Util.intervalo(
                                mesaPanel.getGrandeAreaCima().x + (mesaPanel
                                        .getGrandeAreaCima().getWidth() / 2),
                                mesaPanel.getGrandeAreaCima().x
                                        + mesaPanel.getGrandeAreaCima().getWidth()),
                        Util.intervalo(mesaPanel.getGrandeAreaCima().y,
                                mesaPanel.getGrandeAreaCima().y + mesaPanel
                                        .getGrandeAreaCima().getHeight()));
            }
            marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));
        }

    }

    public void escCimaEsc(Time... times) {
        if (bola == null || mesaPanel == null) {
            return;
        }
        Point p = obterEscanteioEsrquerdoCima();
        bola.setCentroTodos(p);
        centralizaBola();
        if (times != null && times.length > 0) {
            Botao botao = obterUmCobrador(times[0]);
            limparPerimetro(p);
            botao.setCentroTodos(
                    new Point2D.Double(p.x - botao.getDiamentro() * .55,
                            p.y - botao.getDiamentro() * .55));
            Botao marcador = botao;
            while (marcador.equals(botao)) {
                marcador = obterUmCobrador(times[0]);
            }
            Point2D.Double posicao = new Point2D.Double(
                    mesaPanel.getPequenaAreaCima().getBounds().getCenterX(),
                    mesaPanel.getPequenaAreaCima().getBounds().getCenterY());
            while (mesaPanel.getPequenaAreaCima().contains(posicao)) {
                posicao = new Point2D.Double(
                        Util.intervalo(mesaPanel.getGrandeAreaCima().x,
                                mesaPanel.getGrandeAreaCima().x + (mesaPanel
                                        .getGrandeAreaCima().getWidth() / 2)),
                        Util.intervalo(mesaPanel.getGrandeAreaCima().y,
                                mesaPanel.getGrandeAreaCima().y + mesaPanel
                                        .getGrandeAreaCima().getHeight()));
            }
            marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));

        }
    }

    public void escBaixoDir(Time... times) {
        if (bola == null || mesaPanel == null) {
            return;
        }
        Point p = obterEscanteioDireitoBaixo();
        bola.setCentroTodos(p);
        centralizaBola();
        if (times != null && times.length > 0) {
            limparPerimetro(p);
            Botao botao = obterUmCobrador(times[0]);
            botao.setCentroTodos(
                    new Point2D.Double(p.x + botao.getDiamentro() * .65,
                            p.y + botao.getDiamentro() * .65));

            Botao marcador = botao;
            while (marcador.equals(botao)) {
                marcador = obterUmCobrador(times[0]);
            }
            Point2D.Double posicao = new Point2D.Double(
                    mesaPanel.getPequenaAreaBaixo().getBounds().getCenterX(),
                    mesaPanel.getPequenaAreaBaixo().getBounds().getCenterY());
            while (mesaPanel.getPequenaAreaBaixo().contains(posicao)) {
                posicao = new Point2D.Double(
                        Util.intervalo(
                                mesaPanel.getGrandeAreaBaixo().x + (mesaPanel
                                        .getGrandeAreaBaixo().getWidth() / 2),
                                mesaPanel.getGrandeAreaBaixo().x
                                        + mesaPanel.getGrandeAreaBaixo().getWidth()),
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().y,
                                mesaPanel.getGrandeAreaBaixo().y + mesaPanel
                                        .getGrandeAreaBaixo().getHeight()));
            }
            marcador.setCentroTodos(new Point2D.Double(posicao.x, posicao.y));

        }
    }

    public void escBaixoEsc(Time... times) {
        if (bola == null || mesaPanel == null) {
            return;
        }
        Point p = obterEscanteioEsrquerdoBaixo();
        bola.setCentroTodos(p);
        centralizaBola();
        if (times != null && times.length > 0) {
            limparPerimetro(p);
            Botao botao = obterUmCobrador(times[0]);
            botao.setCentroTodos(
                    new Point2D.Double(p.x - botao.getDiamentro() * .55,
                            p.y + botao.getDiamentro() * .55));
            Botao marcador = botao;
            while (marcador.equals(botao)) {
                marcador = obterUmCobrador(times[0]);
            }
            Point2D.Double posicao = new Point2D.Double(
                    mesaPanel.getPequenaAreaBaixo().getBounds().getCenterX(),
                    mesaPanel.getPequenaAreaBaixo().getBounds().getCenterY());
            while (mesaPanel.getPequenaAreaBaixo().contains(posicao)) {
                posicao = new Point2D.Double(
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().x,
                                mesaPanel.getGrandeAreaBaixo().x + (mesaPanel
                                        .getGrandeAreaBaixo().getWidth() / 2)),
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().y,
                                mesaPanel.getGrandeAreaBaixo().y + mesaPanel
                                        .getGrandeAreaBaixo().getHeight()));
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
            botao.setCentroTodos(
                    new Point2D.Double(p.x, p.y - botao.getDiamentro()));
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
            botao.setCentroTodos(
                    new Point2D.Double(p.x, p.y + botao.getDiamentro()));
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
        JMenuItem jogadaCPU = new JMenuItem() {
            public String getText() {
                return Lang.msg("jogadaCPU");
            }
        };
        popup.add(jogadaCPU);
        jogadaCPU.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jogadaCPU();
            }
        });

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
                return Lang.msg("chutarBola", new String[]{
                        (chutaBola ? Lang.msg("sim") : Lang.msg("nao"))});
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
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
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
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao b = (Botao) botoes.get(id);
            if (b instanceof Bola || b instanceof Goleiro) {
                continue;
            }
            double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(p,
                    b.getCentro());
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
                    point = (Point) circulo
                            .get(Util.intervalo(0, circulo.size() - 1));
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

    public void posicionaBotaoAleatoriamenteNoSeuCampo(Botao b) {
        boolean botaoPosicionado = false;

        while (!botaoPosicionado) {
            if (ConstantesMesa11.CAMPO_CIMA == b.getTime().getCampo()) {

                int valx = Util.intervalo(mesaPanel.getCampoCima().x,
                        mesaPanel.getCampoCima().x
                                + mesaPanel.getCampoCima().width);
                int valy = Util.intervalo(mesaPanel.getCampoCima().y,
                        mesaPanel.getCampoCima().y
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

    public boolean verificaTemBotao(Point p, Botao... exceto) {
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao b = (Botao) botoes.get(id);
            boolean expt = false;
            if (exceto != null) {
                for (int i = 0; i < exceto.length; i++) {
                    if (exceto[i] != null && exceto[i].equals(b)) {
                        expt = true;
                    }
                }
            }
            if (expt) {
                continue;
            }
            if (b instanceof Goleiro) {
                // TODO GOleiro maudito
                Goleiro goleiro = (Goleiro) b;
                if (goleiro.getShape(1).contains(p)) {
                    System.out.println(
                            "verificaTemBotao(Point p, Botao... exceto) Goleiro");
                }
                // return ;
            }
            if (GeoUtil.distaciaEntrePontos(p,
                    b.getCentro()) < ConstantesMesa11.RAIO_BOTAO) {
                return true;
            }
        }
        return false;
    }

    protected void soltarBotao() {
        if (botaoSelecionado != null) {
            if (verificaTemBotao(botaoSelecionado.getCentro(),
                    botaoSelecionado)) {
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
        if (mesaPanel.getAreaGolBaixo()
                .intersects(botao.getShape(1).getBounds2D())
                || mesaPanel.getAreaGolCima()
                .intersects(botao.getShape(1).getBounds2D())) {
            return true;
        }
        boolean dt = (mesaPanel.getCampoBaixoSemLinhas()
                .intersects(botao.getShape(1).getBounds2D())
                || mesaPanel.getCampoCimaSemLinhas()
                .intersects(botao.getShape(1).getBounds2D())
                || mesaPanel.getLinhaGolBaixo()
                .intersects(botao.getShape(1).getBounds2D())
                || mesaPanel.getLinhaGolCima()
                .intersects(botao.getShape(1).getBounds2D()));
        return dt;
    }

    protected void chutarBola() {
        chutaBola = !chutaBola;

    }

    public boolean isProcessando() {
        if (isJogoOnlineCliente()) {
            return dadosJogoSrvMesa11.isProcessando();
        }
        return processando;
    }

    public void setProcessando(boolean processando) {
        this.processando = processando;
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
        synchronized (botoesComThread) {
            for (Iterator iterator = botoesComThread.keySet()
                    .iterator(); iterator.hasNext(); ) {
                Long id = (Long) iterator.next();
                Thread thread = botoesComThread.get(id);
                return thread.isAlive();
            }
        }
        return false;
    }

    public Map<Long, Botao> getBotoes() {
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

    public Point getPontoPasando() {
        return pontoPasando;
    }

    public void setPontoPasando(Point pontoPasando) {
        this.pontoPasando = pontoPasando;
    }

    public void setZoom(double d) {
        mesaPanel.mouseZoom = d;
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
        if (dica == null)
            setDica("reversao");
        controlePartida.reversaoJogada();
    }

    public void zeraJogadaTime(Time time) {
        controlePartida.zeraJogadaTime(time);

    }

    public void falta(Point ponto, Botao levouFalta) {

        if (ConstantesMesa11.CAMPO_CIMA
                .equals(levouFalta.getTime().getCampo())) {
            if (mesaPanel.getGrandeAreaBaixo().contains(ponto)) {
                limparPerimetroCirculo(
                        mesaPanel.getPenaltyBaixo().getLocation(),
                        mesaPanel.getGrandeAreaBaixo().getWidth() * 0.55);
                Point penalBaixo = mesaPanel.getPenaltyBaixo().getLocation();
                getBola().setCentroTodos(
                        new Point(penalBaixo.x + getBola().getRaio(),
                                penalBaixo.y + getBola().getRaio()));
                levouFalta.setCentroTodos(new Point(penalBaixo.x, penalBaixo.y
                        - Util.inte((levouFalta.getDiamentro() * 1.4))));
                controlePartida.centralizaGoleiroBaixo();
                setDica("penalti");
            } else if (mesaPanel.getGrandeAreaCima().contains(ponto)) {
                limparPerimetroCirculo(mesaPanel.getPenaltyCima().getLocation(),
                        mesaPanel.getGrandeAreaCima().getWidth() * 0.55);
                metaCima(levouFalta.getTime());
                setDica("falta");
            } else {
                if (verificaBolaNoPerimetro(ponto)) {
                    limparPerimetroCirculo(ponto);
                    double calculaAngulo = GeoUtil
                            .calculaAngulo(mesaPanel.golBaixo(), ponto, 90);
                    Point p = GeoUtil.calculaPonto(calculaAngulo,
                            levouFalta.getDiamentro(), ponto);
                    levouFalta.setCentroTodos(p);
                    bola.setCentroTodos(ponto);
                    setDica("falta");
                }
            }
        } else {
            if (mesaPanel.getGrandeAreaCima().contains(ponto)) {
                limparPerimetroCirculo(mesaPanel.getPenaltyCima().getLocation(),
                        mesaPanel.getGrandeAreaCima().getWidth() * 0.55);
                Point penalCima = mesaPanel.getPenaltyCima().getLocation();
                getBola().setCentroTodos(
                        new Point(penalCima.x + getBola().getRaio(),
                                penalCima.y + getBola().getRaio()));
                levouFalta.setCentroTodos(new Point(penalCima.x, penalCima.y
                        + Util.inte((levouFalta.getDiamentro() * 1.4))));
                controlePartida.centralizaGoleiroCima();
                setDica("penalti");
            } else if (mesaPanel.getGrandeAreaBaixo().contains(ponto)) {
                limparPerimetroCirculo(
                        mesaPanel.getPenaltyBaixo().getLocation(),
                        mesaPanel.getGrandeAreaBaixo().getWidth() * 0.55);
                metaBaixo(levouFalta.getTime());
                setDica("falta");
            } else {
                if (verificaBolaNoPerimetro(ponto)) {
                    limparPerimetroCirculo(ponto);
                    double calculaAngulo = GeoUtil
                            .calculaAngulo(mesaPanel.golCima(), ponto, 90);
                    Point p = GeoUtil.calculaPonto(calculaAngulo,
                            levouFalta.getDiamentro(), ponto);
                    levouFalta.setCentroTodos(p);
                    bola.setCentroTodos(ponto);
                    setDica("falta");
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
        Time timeLateral = controlePartida.getTimeCima()
                .equals(eventoAtual.getUltimoContato().getTime())
                ? controlePartida.getTimeBaixo()
                : controlePartida.getTimeCima();
        int tamretaMin = Integer.MAX_VALUE;
        List btnsLateral = timeLateral.getBotoes();
        Botao botaoLateral = null;
        for (Iterator iterator = btnsLateral.iterator(); iterator.hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            if (!(botao instanceof Goleiro)) {
                double reta = GeoUtil.distaciaEntrePontos(ultLateral,
                        botao.getCentro());
                if (reta < tamretaMin) {
                    tamretaMin = (int) reta;
                    botaoLateral = botao;
                }
            }
        }
        limparPerimetroCirculo(ultLateral);
        zeraJogadaTime(timeLateral);
        if (ultLateral.x < 3000) {
            botaoLateral.setCentroTodos(new Point(
                    ultLateral.x - botaoLateral.getDiamentro(), ultLateral.y));
        } else {
            botaoLateral.setCentroTodos(new Point(
                    ultLateral.x + botaoLateral.getDiamentro(), ultLateral.y));
        }
        lateral();
    }

    public void salvarTime(Time time) {
        validaTime(time);
        if (isJogoOnlineCliente()) {
            salvarTimeOnline(time);
        } else {
            gerarXmlTime(time);
        }
    }

    public void gerarXmlTime(Time time) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
        encoder.writeObject(time);
        encoder.flush();
        JTextArea xmlArea = new JTextArea(30, 50);
        xmlArea.setText(
                new String(byteArrayOutputStream.toByteArray()) + "</java>");
        xmlArea.setEditable(false);
        xmlArea.setSelectionStart(0);
        xmlArea.setSelectionEnd(xmlArea.getCaretPosition());
        JScrollPane xmlPane = new JScrollPane(xmlArea);
        xmlPane.setBorder(new TitledBorder(Lang.msg("salvarTimeInfo")));
        JOptionPane.showMessageDialog(frame, xmlPane, Lang.msg("salvarTime"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void salvarTimeOnline(Time time) {
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setData(time);
        mesa11to.setComando(ConstantesMesa11.SALVAR_TIME);
        mesa11Applet.enviarObjeto(mesa11to);
    }

    private void validaTime(Time time) {
        List botoesTime = time.getBotoes();
        List remover = new ArrayList();
        List adicionar = new ArrayList();
        for (Iterator iterator = botoesTime.iterator(); iterator.hasNext(); ) {
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
        for (Iterator iterator = botoes.iterator(); iterator.hasNext(); ) {
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
            JOptionPane.showMessageDialog(frame, xmlPane,
                    Lang.msg("xmlTimeSalvo"), JOptionPane.INFORMATION_MESSAGE);

            if (Util.isNullOrEmpty(xmlArea.getText())) {
                return;
            }
            ByteArrayInputStream bin = new ByteArrayInputStream(
                    xmlArea.getText().getBytes());
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
            JOptionPane.showMessageDialog(frame, retorno.toString(),
                    Lang.msg("erro"), JOptionPane.ERROR_MESSAGE);
            Logger.logarExept(e);
        }

    }

    public boolean verificaBolaPertoGoleiroTime(Time time) {
        return controlePartida.verificaBolaPertoGoleiroTime(time, bola);
    }

    public boolean verificaGol(Botao botao) {
        if (mesaPanel == null)
            return false;
        return (mesaPanel.getAreaGolBaixo()
                .intersects(botao.getShape(1).getBounds2D())
                || mesaPanel.getAreaGolCima()
                .intersects(botao.getShape(1).getBounds2D()));
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
            return false;
        if ((mesaPanel.getLinhaGolBaixo()
                .intersects(botao.getShape(1).getBounds())
                || mesaPanel.getLinhaGolCima()
                .intersects(botao.getShape(1).getBounds()))) {
            return false;
        }
        return (mesaPanel.getAreaEscateioBaixo()
                .contains(botao.getShape(1).getBounds())
                || mesaPanel.getAreaEscateioCima()
                .contains(botao.getShape(1).getBounds()));
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
            escEsq = obterEscanteioEsrquerdoCima();
            escDir = obterEscanteioDireitoCima();

            distDir = GeoUtil.distaciaEntrePontos(escDir, bolaEscanteio);
            distEsq = GeoUtil.distaciaEntrePontos(escEsq, bolaEscanteio);
            if (distDir < distEsq) {
                escCimaDir(time);
            } else {
                escCimaEsc(time);
            }
        } else {
            escEsq = obterEscanteioEsrquerdoBaixo();
            escDir = obterEscanteioDireitoBaixo();
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
        setDica("escanteio");

    }

    private Point obterEscanteioDireitoBaixo() {
        Point p = mesaPanel.getCampoBaixo().getLocation();
        p.x += mesaPanel.getCampoBaixo().getWidth() - bola.getRaio();
        p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getRaio();
        return p;
    }

    private Point obterEscanteioEsrquerdoBaixo() {
        Point p = mesaPanel.getCampoBaixo().getLocation();
        p.y += mesaPanel.getCampoBaixo().getHeight() - bola.getRaio();
        p.x += bola.getRaio();
        return p;
    }

    private Point obterEscanteioDireitoCima() {
        Point p = mesaPanel.getCampoCima().getLocation();
        p.x += mesaPanel.getCampoCima().getWidth() - bola.getRaio();
        p.y += bola.getRaio();
        return p;
    }

    private Point obterEscanteioEsrquerdoCima() {
        Point p = mesaPanel.getCampoCima().getLocation();
        p.x += bola.getRaio();
        p.y += bola.getRaio();
        return p;
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
        setDica("meta");
    }

    public void processarGolContra(Botao botao) {
        controlePartida.processarGolContra(botao);
        Logger.logar("GolContra " + botao.getTime());
        setDica("golContra");
    }

    public void processarGol(Botao botao) {
        setDica("gol");
        controlePartida.processarGol(botao);
        Logger.logar("Gol " + botao.getTime());

    }

    public void matarTodasThreads() {
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
        frame.setVisible(true);
        frame.setSize(1000, 700);
    }

    private void criarProgressBar() {
        progressBarFrame = new JFrame();
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 20);
        JPanel jPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 100);
            }
        };
        jPanel.add(progressBar);
        progressBarFrame.getContentPane().add(jPanel);
        progressBarFrame.setTitle(Lang.msg("carregando"));
        progressBarFrame.setSize(500, 100);
        progressBarFrame.setLocation(this.frame.getLocation());
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

    public boolean isAssistido() {
        return autoMira;
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
            return "";
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
        if (controlePartida != null && controlePartida.getTimeCima() != null) {
            return controlePartida.getTimeCima().obterGoleiro();
        }
        return null;

    }

    public Goleiro obterGoleiroBaixo() {
        if (controlePartida != null && controlePartida.getTimeBaixo() != null) {
            return controlePartida.getTimeBaixo().obterGoleiro();
        }
        return null;
    }

    public void verificaBolaParouEmCimaBotao() {
        if (eventoAtual == null) {
            return;
        }
        if (eventoAtual.isBolaFora()) {
            return;
        }
        String eventoCod = eventoAtual.getEventoCod();
        if ((ConstantesMesa11.GOLEIRO_DEFESA.equals(eventoCod)
                || (ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(eventoCod)
                || ConstantesMesa11.CONTATO_BOLA_BOTAO
                .equals(eventoCod)))) {

            for (Iterator iterator = botoes.keySet().iterator(); iterator
                    .hasNext(); ) {
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

    public Object enviarObjeto(NnpeTO mesa11to) {
        if (mesa11Applet == null) {
            Logger.logar("enviarObjeto mesa11Applet null");
            return null;
        }
        return mesa11Applet.enviarObjeto(mesa11to);
    }

    public Mesa11Applet getMesa11Applet() {
        return mesa11Applet;
    }

    public DadosJogoSrvMesa11 getDadosJogoSrvMesa11() {
        return dadosJogoSrvMesa11;
    }

    public void setDadosJogoSrvMesa11(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
        this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
    }

    public boolean verificaVezOnline() {
        Logger.logar("verificaVezOnline() timeClienteOnline "
                + timeClienteOnline + " dadosJogoSrvMesa11.getTimeVez() "
                + dadosJogoSrvMesa11.getTimeVez());
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
        if (p1 == null || p2 == null) {
            return;
        }
        efetuaJogada(p1, p2);
    }

    public String mudarDica() {
        if (controleDicas == null) {
            controleDicas = new ControleDicas(this);
        }
        controleDicas.mudarDica();
        return dica;
    }

    public boolean efetuaJogada(Point p1, Point p2) {
        if (isControleEventosRodando()) {
            return false;
        }
        dica = null;
        if (bola != null) {
            posicaoBolaJogada = bola.getCentro();
        }
        Evento evento = new Evento();
        animacaoJogada = null;
        double distaciaEntrePontos = GeoUtil.distaciaEntrePontos(p1, p2);

        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao botao = (Botao) botoes.get(id);
            if (!veririficaVez(botao)) {
                continue;
            }
            if (botao instanceof Goleiro) {
                Goleiro goleiro = (Goleiro) botao;
                if (goleiro.getShape(1).contains(p1)) {
                    boolean returnGoleiro = false;
                    double rotacao = goleiro.getRotacao();
                    Point centroGoleiro = goleiro.getCentro();
                    double retaGoleiro = GeoUtil
                            .distaciaEntrePontos(goleiro.getCentro(), p1);

                    if (retaGoleiro > (goleiro.getRaio() / 2)) {
                        goleiro.setRotacao(GeoUtil
                                .calculaAngulo(goleiro.getCentro(), p2, 0));
                        if (((goleiro.getTime().equals(getTimeCima())
                                && (mesaPanel.getGrandeAreaCima().contains(
                                goleiro.getShape(1).getBounds())
                                || mesaPanel.getAreaGolCima()
                                .intersects(goleiro.getShape(1)
                                        .getBounds()))

                        ) || (goleiro.getTime().equals(getTimeBaixo())
                                && (mesaPanel.getGrandeAreaBaixo().contains(
                                goleiro.getShape(1).getBounds())
                                || mesaPanel.getAreaGolBaixo()
                                .intersects(goleiro.getShape(1)
                                        .getBounds()))))
                                && !goleiro.getShape(1).intersects(
                                bola.getShape(1).getBounds2D())) {
                            evento.setPonto(p2);
                            evento.setEventoCod(
                                    ConstantesMesa11.GOLEIRO_ROTACAO);

                            returnGoleiro = true;
                        } else {
                            goleiro.setRotacao(rotacao);
                        }
                    } else {
                        goleiro.setCentroTodos(p2);
                        if (((goleiro.getTime().equals(getTimeCima())
                                && (mesaPanel.getGrandeAreaCima().contains(
                                goleiro.getShape(1).getBounds())
                                || mesaPanel.getAreaGolCima()
                                .intersects(goleiro.getShape(1)
                                        .getBounds())))
                                || (goleiro.getTime().equals(getTimeBaixo())
                                && (mesaPanel.getGrandeAreaBaixo()
                                .contains(goleiro.getShape(1)
                                        .getBounds())
                                || mesaPanel.getAreaGolBaixo()
                                .intersects(goleiro
                                        .getShape(1)
                                        .getBounds()))))
                                && !goleiro.getShape(1).intersects(
                                bola.getShape(1).getBounds2D())) {
                            goleiro.setCentroTodos(p2);
                            evento.setPonto(p2);
                            evento.setEventoCod(ConstantesMesa11.GOLEIRO_MOVEU);
                            returnGoleiro = true;
                        } else {
                            goleiro.setCentroTodos(centroGoleiro);
                        }
                    }
                    setPontoClicado(null);
                    adicionaJogadaCliente();
                    return returnGoleiro;
                }
            }
            if (botao.getCentro() == null) {
                continue;
            }
            double raioPonto = GeoUtil.distaciaEntrePontos(p1,
                    botao.getCentro());
            if (raioPonto <= botao.getRaio()) {
                if (!veririficaVez(botao)) {
                    setPontoClicado(null);
                    return false;
                }
                if (botao instanceof Bola) {
                    boolean areaGoleiroCima = false;
                    Goleiro goleiroCima = obterGoleiroCima();
                    double distaciaEntrePontosCima = GeoUtil
                            .distaciaEntrePontos(botao.getCentro(),
                                    goleiroCima.getCentro());
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
                    if (distaciaEntrePontosBaixo < goleiroBaixo
                            .getDiamentro()) {
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
                    angulo = GeoUtil.calculaAngulo(botao.getCentro(),
                            getBola().getCentro(), 90);
                }
                int forca = 6;
                if (Math.random() > botao.getForca() / 1000.0) {
                    int variancia = 10 - botao.getPrecisao() / 100;
                    forca += Util.intervalo(0, variancia);
                    Logger.logar("Forca " + forca);
                }

                Point destino = GeoUtil.calculaPonto(angulo,
                        Util.inte(distaciaEntrePontos * forca),
                        botao.getCentro());
                botao.setDestino(destino);
                evento.setPonto(p1);
                evento.setBotaoEvento(botao);
                animacaoJogada = new Animacao();
                adicionaAnimacaoCliente();
                if (botao.getCentroInicio() == null)
                    botao.setCentroInicio(botao.getCentro());
                animacaoJogada.setObjetoAnimacao(botao.getId());
                animacaoJogada.setPontosAnimacao(botao.getTrajetoria());
                setNumRecursoes(0);
                setEventoAtual(evento);
                propagaColisao(animacaoJogada, botao);
                verificaBolaParouEmCimaBotao();
                break;
            } else {
                if (botao.getId() != 0 && (!(botao instanceof Goleiro))) {
                    if (!verificaDentroCampo(botao) || verificaTemBotao(
                            botao.getCentro(), new Botao[]{botao, bola}))
                        posicionaBotaoAleatoriamenteNoSeuCampo(botao);
                }
            }

        }
        if (animacaoJogada == null) {
            setPontoClicado(null);
            return false;
        }
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao botao = (Botao) botoes.get(id);
            if (botao.getCentroInicio() != null)
                botao.setCentro(botao.getCentroInicio());
        }
        synchronized (botoesComThread) {
            botoesComThread.clear();
        }
        Animador animador = new Animador(animacaoJogada, this);
        Thread thread = new Thread(animador);
        synchronized (botoesComThread) {
            botoesComThread.put(animacaoJogada.getObjetoAnimacao(), thread);
        }
        thread.start();
        setPontoClicado(null);
        zerarTimerJogada();
        Thread threadEventos = new Thread(new ControleEvento(this));
        threadEventos.start();
        return true;
    }

    public void adicionaJogadaCliente() {
        if (!isJogoOnlineSrvidor()) {
            return;
        }
        animacaoJogada = new Animacao();
        animacaoJogada.setObjetoAnimacao(bola.getId());
        adicionaAnimacaoCliente();
        Animador animador = new Animador(animacaoJogada, this);
        Thread thread = new Thread(animador);
        synchronized (botoesComThread) {
            botoesComThread.put(animacaoJogada.getObjetoAnimacao(), thread);
        }
        thread.start();
    }

    public void sairJogoOnline() {
        setSaiuJogoOnline(true);
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.SAIR_JOGO);
        mesa11to.setData(nomeJogadorOnline);
        Object ret = enviarObjeto(mesa11to);
    }

    private void efetuaJogadaCliente() {
        Logger.logar("efetuaJogadaCliente()");
        if (dadosJogoSrvMesa11 != null && dadosJogoSrvMesa11.isWo()) {
            Logger.logar(
                    "if (dadosJogoSrvMesa11 != null && dadosJogoSrvMesa11.isWo())");
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        JogadaMesa11 jogadaMesa11 = new JogadaMesa11(timeClienteOnline,
                dadosJogoSrvMesa11);
        Point p1 = getPontoClicado();
        Point p2 = getPontoPasando();
        jogadaMesa11.setPontoClicado(p1);
        jogadaMesa11.setPontoSolto(p2);
        if (jogadaMesa11.getPontoClicado() == null
                || jogadaMesa11.getPontoSolto() == null) {
            setDica(ConstantesMesa11.JOGADA_INVALIDA);
            setPontoClicado(null);
            setPontoPasando(null);
            return;
        }

        mesa11to.setData(jogadaMesa11);
        mesa11to.setComando(ConstantesMesa11.JOGADA);
        esperandoJogadaOnline = true;
        Object ret = enviarObjeto(mesa11to);
        Logger.logar("JOGADA ret" + ret);
        if (!ConstantesMesa11.OK.equals(ret)) {
            esperandoJogadaOnline = false;
        }
        setPontoClicado(null);
        setPontoPasando(null);
    }

    public void executaAnimacao(final Animacao animacao) {
        Animador animador = new Animador(animacao, this);
        Thread thread = new Thread(animador);
        if (animacao.getObjetoAnimacao() != null) {
            synchronized (botoesComThread) {
                botoesComThread.put(animacao.getObjetoAnimacao(), thread);
            }
        }
        thread.start();
    }

    public int getNumeroJogadas() {
        if (isJogoOnlineCliente() && dadosJogoSrvMesa11 != null) {
            return dadosJogoSrvMesa11.getNumeroJogadas();
        }
        // if (isJogoOnlineSrvidor()) {
        // return jogoServidor.getDadosJogoSrvMesa11().getNumeroJogadas();
        // }
        return numeroJogadas;
    }

    public void setNumeroJogadas(int numeroJogadas) {
        this.numeroJogadas = numeroJogadas;
    }

    public boolean isEsperandoJogadaOnline() {
        return esperandoJogadaOnline;
    }

    public void setEsperandoJogadaOnline(boolean esperandoJogadaOnline) {
        this.esperandoJogadaOnline = esperandoJogadaOnline;
    }

    public void adicionaAnimacaoCliente() {
        if (!isJogoOnlineSrvidor()) {
            return;
        }
        synchronized (sequenciaAnimacao) {
            animacaoJogada = new Animacao();
            animacaoJogada.setSequencia(sequenciaAnimacao++);
            animacaoJogada.setDica(getDica());
            if (animacaoJogada.getPosicaoBtnsSrvMesa11() == null) {
                animacaoJogada
                        .setPosicaoBtnsSrvMesa11(gerarDadosPosicaoBotoes());
            }
            if (getAnimacoesCliente() != null) {
                getAnimacoesCliente().put(animacaoJogada.getSequencia(),
                        animacaoJogada);
            }
        }
    }

    public String getDica() {
        return dica;
    }

    public void setDica(String dica) {
        this.dica = dica;
    }

    public boolean isJogoTerminado() {
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
            return dadosJogoSrvMesa11.isJogoIniciado();
        }
        return jogoIniciado;
    }

    public void setJogoIniciado(boolean jogoIniciado) {
        if (tempoIniciado == 0) {
            tempoIniciado = System.currentTimeMillis();
        }
        this.jogoIniciado = jogoIniciado;
    }

    public void fimJogoServidor() {
        if (!isJogoOnlineSrvidor()) {
            return;
        }
        jogoServidor.fimJogoServidor();

    }

    public boolean verificaPosicaoDiffBotoes() {
        if (dadosJogoSrvMesa11 == null) {
            return false;
        }
        if (botoes == null) {
            return false;
        }
        int sumx = 0;
        int sumy = 0;
        int sumAng = 0;
        Set keySet = botoes.keySet();
        for (Object object : keySet) {
            Botao botao = (Botao) botoes.get(object);
            if (botao.getCentro() != null) {
                sumx += botao.getCentro().x;
                sumy += botao.getCentro().y;
            }
            sumAng += botao.getAngulo();
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.VERIFICA_POSICAO_DIFF_BOTOES);
        mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo() + "-" + sumx + ""
                + sumy + "" + sumAng);
        Object ret = enviarObjeto(mesa11to);
        if (ConstantesMesa11.OK.equals(ret)) {
            return true;
        }
        return false;
    }

    public void jogadaCPU() {
        if (isAnimando()) {
            Logger.logar("jogadaCPU() isAnimando()");
            return;
        }
        Time timeJogadaVez = timeJogadaVez();
        if (timeJogadaVez == null) {
            Logger.logar("jogadaCPU() timeJogadaVez == null");
            return;
        }

        if (!autoMira && !timeJogadaVez.isControladoCPU()) {
            Logger.logar(
                    "jogadaCPU() !autoMira && !timeJogadaVez.isControladoCPU()");
            return;
        }
        if (autoMira && !timeJogadaVez.isControladoCPU()
                && btnAssistido == null) {
            Logger.logar(
                    "jogadaCPU() autoMira && !timeJogadaVez.isControladoCPU()&& btnAssistido == null");
            return;
        }

        boolean bolaNaArea = false;

        if (ConstantesMesa11.CAMPO_CIMA.equals(timeJogadaVez.getCampo())) {
            if (mesaPanel.getGrandeAreaCima().contains(bola.getCentro())
                    || mesaPanel.getLinhaGolCima().contains(bola.getCentro())) {
                bolaNaArea = true;
            }
        } else {
            if (mesaPanel.getGrandeAreaBaixo().contains(bola.getCentro())
                    || mesaPanel.getLinhaGolBaixo()
                    .contains(bola.getCentro())) {
                bolaNaArea = true;
            }
        }

        Point metaCimaDireita = obterMetaDireitaCima();
        Point metaCimaEsquerda = obterMetaEsquerdaCima();
        Point metaBaixoEsquerda = obterMetaEsquerdaBaixo();
        Point metaBaixoDireita = obterMetaDireitaBaixo();

        boolean meta = false;
        if (GeoUtil.distaciaEntrePontos(bola.getCentro(),
                metaCimaDireita) < bola.getDiamentro()
                || GeoUtil.distaciaEntrePontos(bola.getCentro(),
                metaCimaEsquerda) < bola.getDiamentro()
                || GeoUtil.distaciaEntrePontos(bola.getCentro(),
                metaBaixoDireita) < bola.getDiamentro()
                || GeoUtil.distaciaEntrePontos(bola.getCentro(),
                metaBaixoEsquerda) < bola.getDiamentro()) {
            meta = true;
            bolaNaArea = false;
        }

        if (bolaNaArea) {
            Goleiro goleiro = timeJogadaVez.obterGoleiro();
            Point gol = obterTrajetoriaCPUCampoOposto(goleiro);
            int cont = 0;
            while (gol == null) {
                gol = obterTrajetoriaCPUCampoOposto(goleiro, true);
                cont++;
                if (cont > 50) {
                    break;
                }
            }
            double angBtnJogada = GeoUtil.calculaAngulo(bola.getCentro(), gol,
                    270);
            goleiro.setCentroTodos(GeoUtil.calculaPonto(angBtnJogada,
                    Util.intervalo(goleiro.getRaio() / 2, goleiro.getRaio()),
                    bola.getCentro()));
            ptDstBola = GeoUtil.calculaPonto(angBtnJogada, 1000,
                    bola.getCentro());
            efetuaJogada(bola.getCentro(), ptDstBola);
            golJogadaCpu = gol;
            return;
        }
        Goleiro goleiro = timeJogadaVez.obterGoleiro();
        if (!goleiro.getCentro().equals(mesaPanel.golCima())
                || !goleiro.getCentro().equals(mesaPanel.golBaixo())) {
            if (ConstantesMesa11.CAMPO_CIMA.equals(timeJogadaVez.getCampo())) {
                controlePartida.centralizaGoleiroCima();
            } else {
                controlePartida.centralizaGoleiroBaixo();
            }
        }
        List botoesTimeVez = timeJogadaVez.getBotoes();

        Set descartados = new HashSet();
        Botao btnPrximo = null;
        if (autoMira && !timeJogadaVez.isControladoCPU()
                && btnAssistido != null) {
            btnPrximo = btnAssistido;
        } else {
            btnPrximo = obterBtnJogadaCPU(botoesTimeVez, descartados);
        }

        /**
         * Jogada Botão mais proximo chutar gol
         */

        int contBtn = 0;
        int contGol = 0;
        double angBolaGol = 0;
        Point gol = null;
        if (btnPrximo != null) {
            gol = caluclarPontGol(btnPrximo);
            if (gol != null) {
                angBolaGol = GeoUtil.calculaAngulo(bola.getCentro(), gol, 270);
                ptDstBola = GeoUtil.calculaPonto(angBolaGol,
                        btnPrximo.getRaio() + bola.getRaio() - 1,
                        bola.getCentro());
            }
        }
        while (btnPrximo == null) {
            Logger.logar("while (btnPrximo == null) ");
            btnPrximo = obterBtnJogadaCPU(botoesTimeVez, descartados);
            contBtn++;
            if (contBtn > 5) {
                btnPrximo = obterBtnProximo(botoesTimeVez, new HashSet(), true);
                contBtn = 0;
                break;
            }
        }
        while ((btnPrximo != null && ptDstBola != null
                && GeoUtil.distaciaEntrePontos(btnPrximo.getCentro(),
                ptDstBola) > GeoUtil.distaciaEntrePontos(
                btnPrximo.getCentro(), bola.getCentro()))
                && GeoUtil.distaciaEntrePontos(btnPrximo.getCentro(),
                bola.getCentro()) > btnPrximo.getRaio()
                && !btnPrximo.getShape(1)
                .intersects(bola.getShape(1).getBounds())) {
            if (autoMira && !timeJogadaVez.isControladoCPU()) {
                break;
            }
            Logger.logar("Recalculando caluclarPontGol Gol " + gol
                    + " btnPrximo " + btnPrximo);
            gol = caluclarPontGol(btnPrximo);
            contGol++;
            if (gol == null) {
                if (contGol < 10) {
                    continue;
                } else {
                    gol = bola.getCentro();
                }
            } else {
                angBolaGol = GeoUtil.calculaAngulo(bola.getCentro(), gol, 270);
                ptDstBola = GeoUtil.calculaPonto(angBolaGol,
                        btnPrximo.getRaio() + bola.getRaio() - 1,
                        bola.getCentro());
            }
            if (contGol > 20) {
                descartados.add(btnPrximo);
                btnPrximo = obterBtnJogadaCPU(botoesTimeVez, descartados);
                contBtn++;
                contGol = 0;
                if (descartados.size() > 5) {
                    btnPrximo = obterBtnJogadaCPU(botoesTimeVez, new HashSet());
                    Logger.logar(
                            "ReCALCULANDO descartados.size() > 5 " + btnPrximo);
                    break;
                }
                if (btnPrximo == null && !descartados.isEmpty()) {
                    btnPrximo = (Botao) descartados.iterator().next();
                    Logger.logar(
                            "ReCALCULANDO btnPrximo == null && !descartados.isEmpty() "
                                    + btnPrximo);
                    break;
                }

            }

        }
        boolean recuarGoleiro = false;
        if (btnPrximo == null) {
            btnPrximo = obterBtnProximo(botoesTimeVez, new HashSet(), true);
            Logger.logar("BTN MAIS proximo");
        }
        if (bola.getCentro().equals(gol)) {
            recuarGoleiro = true;
            gol = recuarGoleiro(btnPrximo);
            Logger.logar("RECUAR GOLEIRO");
        }
        if (gol == null) {
            gol = bola.getCentro();
            Logger.logar("BOLA CENTRO 3");
        }

        if (ptDstBola == null) {
            angBolaGol = GeoUtil.calculaAngulo(bola.getCentro(), gol, 270);
            ptDstBola = GeoUtil.calculaPonto(angBolaGol,
                    btnPrximo.getRaio() + bola.getRaio() - 1, bola.getCentro());
        }

        double angBtnJogada = GeoUtil.calculaAngulo(ptDstBola,
                btnPrximo.getCentro(), 90);
        double div = 10.0;
        double sumFor = 0;
        for (Iterator iterator = botoesTimeVez.iterator(); iterator
                .hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            sumFor += botao.getForca();
        }
        div = (sumFor / botoesTimeVez.size()) / 110.0;
        if (div == 0) {
            div = 10.0;
        }
        double forca = GeoUtil.distaciaEntrePontos(btnPrximo.getCentro(),
                bola.getCentro()) / div;
        int jogadasRestantes = numeroJogadas
                - obterNumJogadas(btnPrximo.getTime());

        boolean chutarGol = false;
        if (mesaPanel.getAreaGolBaixo().contains(gol)
                || mesaPanel.getAreaGolCima().contains(gol)) {
            chutarGol = true;
        }
        boolean nabola = false;
        Point penalBaixo = mesaPanel.getPenaltyBaixo().getLocation();
        penalBaixo = new Point(penalBaixo.x + getBola().getRaio(),
                penalBaixo.y + getBola().getRaio());
        Point penalCima = mesaPanel.getPenaltyCima().getLocation();
        penalCima = new Point(penalCima.x + getBola().getRaio(),
                penalCima.y + getBola().getRaio());

        if (bola.getCentro().equals(gol)) {
            nabola = true;
        }
        if (recuarGoleiro) {
            forca *= 1.5;
        } else if (penalBaixo.equals(bola.getCentro())
                || penalCima.equals(bola.getCentro())) {
            forca *= 10;
        } else if (verificaBolaCentro()) {
            forca = Util.intervalo(30, 40);
        } else if (verificaBolaEscanteio()) {
            forca *= 15;
        } else if (meta) {
            forca *= 15;
        } else if (chutarGol) {
            if (jogadasRestantes == 1)
                forca *= 5;
            else {
                forca *= 4;
            }
        } else if (nabola) {
            forca *= 2;
        } else {
            forca *= (2 + (1 - (jogadasRestantes / numeroJogadas)));
        }
        Point ptDstBtn = GeoUtil.calculaPonto(angBtnJogada, Util.inte(forca),
                btnPrximo.getCentro());
        golJogadaCpu = gol;
        efetuaJogada(btnPrximo.getCentro(), ptDstBtn);
    }

    private Point recuarGoleiro(Botao btnPrximo) {
        Point gol = null;
        if (ConstantesMesa11.CAMPO_CIMA
                .equals(btnPrximo.getTime().getCampo())) {
            if (GeoUtil.distaciaEntrePontos(bola.getCentro(),
                    mesaPanel.getPenaltyCima().getLocation()) < GeoUtil
                    .distaciaEntrePontos(bola.getCentro(),
                            mesaPanel.getCentro().getLocation())) {
                gol = obterTrajetoriaCPUGdAreaPropria(btnPrximo);
            }
        } else {
            if (GeoUtil.distaciaEntrePontos(bola.getCentro(),
                    mesaPanel.getPenaltyBaixo().getLocation()) < GeoUtil
                    .distaciaEntrePontos(bola.getCentro(),
                            mesaPanel.getCentro().getLocation())) {
                gol = obterTrajetoriaCPUGdAreaPropria(btnPrximo);
            }

        }
        return gol;
    }

    public boolean verificaBolaCentro() {
        return bola.getCentro().equals(mesaPanel.getCentro().getLocation());
    }

    private Point obterMetaDireitaBaixo() {
        Point metaBaixoEsquerda = obterMetaEsquerdaBaixo();
        metaBaixoEsquerda.x += mesaPanel.getPequenaAreaBaixo().getWidth()
                - bola.getDiamentro();
        return new Point(metaBaixoEsquerda.x, metaBaixoEsquerda.y);
    }

    private Point obterMetaEsquerdaBaixo() {
        return mesaPanel.getPequenaAreaBaixo().getLocation();
    }

    private Point obterMetaEsquerdaCima() {
        Point metaCimaDireita = obterMetaDireitaCima();
        metaCimaDireita.y += mesaPanel.getPequenaAreaCima().getHeight()
                - bola.getDiamentro();
        return new Point(metaCimaDireita.x, metaCimaDireita.y);
    }

    private Point obterMetaDireitaCima() {
        return mesaPanel.getPequenaAreaCima().getLocation();
    }

    private Botao obterBtnJogadaCPU(List botoesTimeVez, Set descartados) {
        Botao btnPrximo = obterBtnProximoLivre(botoesTimeVez, descartados);
        /**
         * Botão mais proximo
         */
        if (btnPrximo == null) {
            btnPrximo = obterBtnProximoAntesLinhaBola(botoesTimeVez,
                    descartados);
        }
        if (btnPrximo == null) {
            btnPrximo = obterBtnProximo(botoesTimeVez, descartados, false);
        }

        return btnPrximo;
    }

    private Point caluclarPontGol(Botao btnPrximo) {
        Point gol = null;
        if (!verificaBolaEscanteio()) {
            gol = obterTrajetoriaCPUGol(btnPrximo);
        }
        if (gol == null) {
            gol = obterTrajetoriaCPUGdAreaOposta(btnPrximo);
        }
        if (gol == null) {
            gol = obterTrajetoriaCPUCampoOposto(btnPrximo);
        }
        return gol;
    }

    private Point obterTrajetoriaCPUGdAreaPropria(Botao btnPrximo) {
        Point gol = null;
        List canidatos = new ArrayList();
        for (int i = 0; i < 50; i++) {
            if (ConstantesMesa11.CAMPO_BAIXO
                    .equals(btnPrximo.getTime().getCampo())) {
                if (mesaPanel.getCampoCima()
                        .intersects(bola.getShape(1).getBounds())) {
                    continue;
                }
                gol = new Point(
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().x,
                                mesaPanel.getGrandeAreaBaixo().x
                                        + mesaPanel.getGrandeAreaBaixo().width),
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().y,
                                mesaPanel.getGrandeAreaBaixo().y + mesaPanel
                                        .getGrandeAreaBaixo().height));
            } else {
                if (mesaPanel.getCampoBaixo()
                        .intersects(bola.getShape(1).getBounds())) {
                    continue;
                }
                gol = new Point(
                        Util.intervalo(mesaPanel.getGrandeAreaCima().x,
                                mesaPanel.getGrandeAreaCima().x
                                        + mesaPanel.getGrandeAreaCima().width),
                        Util.intervalo(mesaPanel.getGrandeAreaCima().y,
                                mesaPanel.getGrandeAreaCima().y + mesaPanel
                                        .getGrandeAreaCima().height));
            }
            if (validaCaimhoGol(gol)) {
                canidatos.add(gol);
            }
        }

        Point golRet = null;
        double menorDist = java.lang.Double.MAX_VALUE;

        for (Iterator iterator = canidatos.iterator(); iterator.hasNext(); ) {
            Point p = (Point) iterator.next();
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), p);
            if (distaciaEntrePontos < menorDist) {
                golRet = p;
                menorDist = distaciaEntrePontos;
            }

        }
        return golRet;

    }

    private boolean verificaBolaEscanteio() {
        return bola.getCentro().equals(obterEscanteioDireitoBaixo())
                || bola.getCentro().equals(obterEscanteioEsrquerdoBaixo())
                || bola.getCentro().equals(obterEscanteioDireitoCima())
                || bola.getCentro().equals(obterEscanteioEsrquerdoCima());
    }

    private Point obterTrajetoriaCPUCampoOposto(Botao btnPrximo) {
        return obterTrajetoriaCPUCampoOposto(btnPrximo, false);
    }

    private Point obterTrajetoriaCPUCampoOposto(Botao btnPrximo,
                                                boolean msmSemValidar) {
        Point gol = null;
        List canidatos = new ArrayList();
        for (int i = 0; i < 50; i++) {
            if (ConstantesMesa11.CAMPO_CIMA
                    .equals(btnPrximo.getTime().getCampo())) {
                gol = new Point(
                        Util.intervalo(mesaPanel.getCampoBaixo().x,
                                mesaPanel.getCampoBaixo().x
                                        + mesaPanel.getCampoBaixo().width),
                        Util.intervalo(mesaPanel.getCampoBaixo().y,
                                mesaPanel.getCampoBaixo().y
                                        + mesaPanel.getCampoBaixo().height
                                        / 2));
            } else {
                gol = new Point(
                        Util.intervalo(mesaPanel.getCampoCima().x,
                                mesaPanel.getCampoCima().x
                                        + mesaPanel.getCampoCima().width),
                        Util.intervalo(mesaPanel.getCampoCima().y
                                        + mesaPanel.getCampoCima().height / 2,
                                mesaPanel.getCampoCima().y
                                        + mesaPanel.getCampoCima().height));
            }
            if (msmSemValidar || validaCaimhoGol(gol)) {
                // Logger.logar("obterTrajetoriaCPUCampoOposto valida "
                // + msmSemValidar);
                canidatos.add(gol);
            }
        }

        Point golRet = null;
        double menorDist = java.lang.Double.MAX_VALUE;

        for (Iterator iterator = canidatos.iterator(); iterator.hasNext(); ) {
            Point p = (Point) iterator.next();
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), p);
            if (distaciaEntrePontos < menorDist) {
                golRet = p;
                menorDist = distaciaEntrePontos;
            }

        }
        return golRet;
    }

    private Point obterTrajetoriaCPUGdAreaOposta(Botao btnPrximo) {
        Point gol = null;
        List canidatos = new ArrayList();
        for (int i = 0; i < 50; i++) {
            if (ConstantesMesa11.CAMPO_CIMA
                    .equals(btnPrximo.getTime().getCampo())) {
                if (mesaPanel.getCampoCima()
                        .intersects(bola.getShape(1).getBounds())) {
                    continue;
                }
                gol = new Point(
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().x,
                                mesaPanel.getGrandeAreaBaixo().x
                                        + mesaPanel.getGrandeAreaBaixo().width),
                        Util.intervalo(mesaPanel.getGrandeAreaBaixo().y,
                                mesaPanel.getGrandeAreaBaixo().y + mesaPanel
                                        .getGrandeAreaBaixo().height));
            } else {
                if (mesaPanel.getCampoBaixo()
                        .intersects(bola.getShape(1).getBounds())) {
                    continue;
                }
                gol = new Point(
                        Util.intervalo(mesaPanel.getGrandeAreaCima().x,
                                mesaPanel.getGrandeAreaCima().x
                                        + mesaPanel.getGrandeAreaCima().width),
                        Util.intervalo(mesaPanel.getGrandeAreaCima().y,
                                mesaPanel.getGrandeAreaCima().y + mesaPanel
                                        .getGrandeAreaCima().height));
            }
            if (validaCaimhoGol(gol)) {
                // Logger.logar("obterTrajetoriaCPUGdAreaOposta");
                canidatos.add(gol);
            }
        }

        Point golRet = null;
        double menorDist = java.lang.Double.MAX_VALUE;

        for (Iterator iterator = canidatos.iterator(); iterator.hasNext(); ) {
            Point p = (Point) iterator.next();
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), p);
            if (distaciaEntrePontos < menorDist) {
                golRet = p;
                menorDist = distaciaEntrePontos;
            }

        }
        return golRet;

    }

    private Botao obterBtnProximoAntesLinhaBola(List botoesTimeVez,
                                                Set descartados) {
        Botao btnPrximo = null;
        double menorDist = Integer.MAX_VALUE;
        Point gol = null;
        for (Iterator iterator = botoesTimeVez.iterator(); iterator
                .hasNext(); ) {
            Botao b = (Botao) iterator.next();
            if (b instanceof Goleiro) {
                continue;
            }
            if (descartados.contains(b)) {
                continue;
            }
            if (ConstantesMesa11.CAMPO_CIMA.equals(b.getTime().getCampo())) {
                gol = mesaPanel.getPenaltyBaixo().getLocation();
            } else {
                gol = mesaPanel.getPenaltyCima().getLocation();
            }
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), b.getCentro());
            if (distaciaEntrePontos < menorDist
                    && ((GeoUtil.distaciaEntrePontos(b.getCentro(), gol)
                    - b.getRaio()) < GeoUtil.distaciaEntrePontos(
                    bola.getCentro(), gol))) {
                if (!validaCaimho(b, bola.getCentro())) {
                    continue;
                }
                menorDist = distaciaEntrePontos;
                btnPrximo = b;
            }
        }
        Logger.logar("obterBtnProximoAntesLinhaBola btn " + btnPrximo);
        return btnPrximo;
    }

    private Botao obterBtnProximoLivre(List botoesTimeVez, Set descartados) {
        Botao btnPrximo = null;
        double menorDist = Integer.MAX_VALUE;
        double ang = 0;
        for (Iterator iterator = botoesTimeVez.iterator(); iterator
                .hasNext(); ) {
            Botao b = (Botao) iterator.next();
            if (b instanceof Goleiro) {
                continue;
            }
            if (descartados.contains(b)) {
                continue;
            }
            if (b.getShape(1).intersects(bola.getShape(1).getBounds())) {
                return b;
            }
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), b.getCentro());
            if (distaciaEntrePontos < menorDist
                    && validaCaimho(b, bola.getCentro())) {
                menorDist = distaciaEntrePontos;
                btnPrximo = b;
            }
        }
        return btnPrximo;
    }

    private Botao obterBtnProximo(List botoesTimeVez, Set descartados,
                                  boolean mesmoSemValidarCaminho) {
        Botao btnPrximo = null;
        double menorDist = Integer.MAX_VALUE;
        for (Iterator iterator = botoesTimeVez.iterator(); iterator
                .hasNext(); ) {
            Botao b = (Botao) iterator.next();
            if (b instanceof Goleiro) {
                continue;
            }
            if (descartados.contains(b)) {
                continue;
            }
            if (mesmoSemValidarCaminho
                    && b.getShape(1).intersects(bola.getShape(1).getBounds())) {
                return b;
            }
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), b.getCentro());
            if (distaciaEntrePontos < menorDist && (mesmoSemValidarCaminho
                    || validaCaimho(b, bola.getCentro()))) {
                menorDist = distaciaEntrePontos;
                btnPrximo = b;
            }
        }
        Logger.logar("obterBtnProximo " + btnPrximo);
        return btnPrximo;
    }

    private Point obterTrajetoriaCPUGol(Botao btnPrximo) {
        Point gol = null;
        int jogadasRestantes = numeroJogadas
                - obterNumJogadas(btnPrximo.getTime());
        int dstChutar = MesaPanel.LARGURA_GDE_AREA;
        if (jogadasRestantes > 1) {
            // Logger.logar("obterTrajetoriaCPUGol jogadas restantes "
            // + jogadasRestantes);
            dstChutar *= .7;
        }
        Point centroGolBaixo = new Point(
                Util.inte(mesaPanel.getAreaGolBaixo().getCenterX()),
                Util.inte(mesaPanel.getAreaGolBaixo().getCenterY()));
        Point centroGolCima = new Point(
                Util.inte(mesaPanel.getAreaGolCima().getCenterX()),
                Util.inte(mesaPanel.getAreaGolCima().getCenterY()));
        List canidatos = new ArrayList();
        for (int i = 0; i < 50; i++) {
            if (ConstantesMesa11.CAMPO_CIMA
                    .equals(btnPrximo.getTime().getCampo())) {
                if (GeoUtil.distaciaEntrePontos(bola.getCentro(),
                        centroGolBaixo) > dstChutar) {
                    continue;
                }
                gol = new Point(
                        Util.inte(Util.intervalo(mesaPanel.getAreaGolBaixo().x,
                                mesaPanel.getAreaGolBaixo().x + mesaPanel
                                        .getAreaGolBaixo().getWidth())),
                        Util.inte(Util.intervalo(mesaPanel.getAreaGolBaixo().y,
                                mesaPanel.getAreaGolBaixo().y + mesaPanel
                                        .getAreaGolBaixo().getHeight())));
            } else {
                if (GeoUtil.distaciaEntrePontos(bola.getCentro(),
                        centroGolCima) > dstChutar) {
                    continue;
                }
                gol = new Point(
                        Util.inte(Util.intervalo(mesaPanel.getAreaGolCima().x,
                                mesaPanel.getAreaGolCima().x + mesaPanel
                                        .getAreaGolCima().getWidth())),
                        Util.inte(Util.intervalo(mesaPanel.getAreaGolCima().y,
                                mesaPanel.getAreaGolCima().y + mesaPanel
                                        .getAreaGolCima().getHeight())));
            }
            if (validaCaimhoGol(gol)) {
                // Logger.logar("obterTrajetoriaCPUGol i=" + i);
                canidatos.add(gol);
            }
        }

        Point golRet = null;
        double menorDist = java.lang.Double.MAX_VALUE;
        for (Iterator iterator = canidatos.iterator(); iterator.hasNext(); ) {
            Point p = (Point) iterator.next();
            double distaciaEntrePontos = GeoUtil
                    .distaciaEntrePontos(bola.getCentro(), p);
            if (distaciaEntrePontos < menorDist) {
                golRet = p;
                menorDist = distaciaEntrePontos;
            }

        }
        return golRet;
    }

    private boolean validaCaimhoGol(Point gol) {
        return validaCaimho(bola, gol);
    }

    private boolean validaCaimho(Botao ori, Point gol) {
        Botao bolaEmCimaBotao = null;
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Botao b = (Botao) botoes.get(iterator.next());
            if (!(b instanceof Goleiro)) {
                if (b.getShape(1).contains(bola.getShape(1).getBounds())) {
                    bolaEmCimaBotao = b;
                }
            }

        }
        List linha = GeoUtil.drawBresenhamLine(ori.getCentro(), gol);
        boolean caminhoGol = true;
        Botao btTest = new Botao();
        for (int j = 0; j < linha.size(); j += 15) {
            for (Iterator iterator = botoes.keySet().iterator(); iterator
                    .hasNext(); ) {
                Long id = (Long) iterator.next();
                Botao botaoAnalisado = (Botao) botoes.get(id);
                if ((ori.getId() != 0) && botaoAnalisado instanceof Goleiro) {
                    continue;
                }

                if (bolaEmCimaBotao != null && bolaEmCimaBotao.getShape(1)
                        .intersects(botaoAnalisado.getShape(1).getBounds())) {
                    continue;
                }

                Point pt = (Point) linha.get(j);
                if (ori.getId() != 0) {
                    btTest.setCentroTodos(pt);
                    if (botaoAnalisado.getId() != 0
                            && !ori.equals(botaoAnalisado)
                            && botaoAnalisado.getShape(1).intersects(
                            btTest.getShape(1).getBounds())) {
                        return false;
                    }
                } else if (ori.getId() == 0) {
                    btTest = new Bola(0);
                    btTest.setCentroTodos(pt);
                    Rectangle bounds = btTest.getShape(1).getBounds();
                    if (botaoAnalisado.getId() != 0
                            && !ori.equals(botaoAnalisado)
                            && (botaoAnalisado.getShape(1).intersects(bounds)
                            || mesaPanel.getHasteDireitaGolBaixo()
                            .intersects(bounds)
                            || mesaPanel.getHasteDireitaGolCima()
                            .intersects(bounds)
                            || mesaPanel.getHasteEsquerdaGolBaixo()
                            .intersects(bounds)
                            || mesaPanel.getHasteEsquerdaGolCima()
                            .intersects(bounds))) {
                        return false;
                    }
                }
            }
        }
        return caminhoGol;
    }

    public Botao obterBotao(Point p) {
        if (p == null) {
            return null;
        }
        if (botoes == null) {
            return null;
        }
        Map botoes = getBotoesCopia();
        if (botoes == null) {
            return null;
        }
        for (Iterator iterator = botoes.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao b = (Botao) botoes.get(id);
            if (GeoUtil.distaciaEntrePontos(p,
                    b.getCentro()) < ConstantesMesa11.RAIO_BOTAO) {
                return b;
            }
        }
        return null;
    }

    public void incrementaBarraCarregando() {
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                mostraProgressBar();
                if (progressBar == null) {
                    return;
                }
                int value = progressBar.getValue();
                value++;
                progressBar.setValue(value);
                progressBar.repaint();
            }
        });
        run.start();
    }

    public void escondePorgressBar() {
        if (progressBarFrame != null) {
            progressBarFrame.setVisible(false);
        }
    }

    public void randomizarAtributos(Time time) {
        List botoesList = time.getBotoes();
        for (Iterator iterator = botoesList.iterator(); iterator.hasNext(); ) {
            Botao b = (Botao) iterator.next();
            b.setDefesa(new Integer(Util.intervalo(500, 999)));
            b.setPrecisao(new Integer(Util.intervalo(500, 999)));
            b.setForca(new Integer(Util.intervalo(500, 999)));
        }

    }

    public Point2D getPontoPasandoZoom() {
        if (pontoPasando == null) {
            return null;
        }
        if (mesaPanel == null) {
            return null;
        }
        return new Point2D.Double(pontoPasando.x * mesaPanel.zoom,
                pontoPasando.y * mesaPanel.zoom);
    }

    public List<GolJogador> getGolsTempo() {
        return controlePartida.getGolJogadors();
    }

    public Botao obterBotao(Long idJogador) {
        return botoes.get(idJogador);
    }

    public long getTempoRestanteJogo() {
        if (controlePartida == null) {
            return 0;
        }
        if (jogoTerminado) {
            return 0;
        }
        return controlePartida.tempoRestanteJogo();
    }

    public Map getBotoesCopia() {
        if (botoesCopy == null) {
            return null;
        }
        HashMap<Long, Botao> botoesCopy2 = new HashMap<Long, Botao>();
        while (botoesCopy2.isEmpty()) {
            try {
                botoesCopy2.putAll(botoesCopy);
            } catch (Exception e) {
                Logger.logarExept(e);
            }
        }
        return botoesCopy2;
    }

    public void atualizaBotoesCopia() {
        botoesCopy = new HashMap<Long, Botao>();
        try {
            while (botoesCopy.isEmpty()) {
                botoesCopy.putAll(botoes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciaJogoLivreAssistido() {
        autoMira = true;
        iniciaJogoLivre();
    }

    public void setarBtnAssistido() {
        if (isJogoOnlineCliente() && verificaVezOnline()
                && botaoSelecionado != null) {
            if (!dadosJogoSrvMesa11.getTimeVez()
                    .equals(botaoSelecionado.getTime().getNome())) {
                return;
            }
            NnpeTO mesa11to = new NnpeTO();
            JogadaMesa11 jogadaMesa11 = new JogadaMesa11(timeClienteOnline,
                    dadosJogoSrvMesa11);
            jogadaMesa11.setIdBtnJogadaAssitida(botaoSelecionado.getId());
            mesa11to.setData(jogadaMesa11);
            mesa11to.setComando(ConstantesMesa11.JOGADA_ASSITIDA);
            esperandoJogadaOnline = true;
            Object ret = enviarObjeto(mesa11to);
        }
        Logger.logar("setarBtnAssistido() " + botaoSelecionado);
        btnAssistido = botaoSelecionado;
    }

    public Botao getBtnAssistido() {
        return btnAssistido;
    }

    public void zeraBtnAssistido() {
        if (autoMira) {
            btnAssistido = null;
        }
    }

    public boolean verificaLag() {
        if (mesa11Applet == null) {
            return false;
        }
        return mesa11Applet.getLatenciaReal() > 50;
    }

    public int getLag() {
        if (mesa11Applet == null) {
            return 0;
        }
        return mesa11Applet.getLatenciaReal();
    }

    public Point getPontoArrastando() {
        return pontoArrastando;
    }

    public void setPontoArrastando(Point pontoArrastando) {
        this.pontoArrastando = pontoArrastando;
    }

    public void removeBkg() {
        if (mesaPanel != null) {
            mesaPanel.setDesenhaSplash(false);
        }
    }

    public void removerBotoesComThread(Long objetoAnimacao) {
        synchronized (botoesComThread) {
            botoesComThread.remove(objetoAnimacao);
        }
    }

    public void adicionarBotoesComThread(Long objetoAnimacao, Thread thread) {
        synchronized (botoesComThread) {
            botoesComThread.put(objetoAnimacao, thread);
        }
    }

    public Thread obterBotoesComThread(Long objetoAnimacao) {
        synchronized (botoesComThread) {
            return botoesComThread.get(objetoAnimacao);
        }
    }

    public Map<Long, Animacao> getAnimacoesCliente() {
        return animacoesCliente;
    }

    public PosicaoBtnsSrvMesa11 gerarDadosPosicaoBotoes() {
        if (!isJogoOnlineSrvidor()) {
            return null;
        }
        PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11 = new PosicaoBtnsSrvMesa11();
        List<BotaoPosSrvMesa11> botaoPosSrvMesa11List = new ArrayList();
        Map botoesCopia = getBotoesCopia();
        if (botoesCopia == null) {
            return null;
        }
        for (Iterator iterator = botoesCopia.keySet().iterator(); iterator
                .hasNext(); ) {
            Long id = (Long) iterator.next();
            Botao botao = (Botao) botoesCopia.get(id);
            BotaoPosSrvMesa11 botaoPosSrvMesa11 = new BotaoPosSrvMesa11();
            botaoPosSrvMesa11.setId(id);
            botaoPosSrvMesa11.setPos(botao.getCentro());
            if (botao instanceof Goleiro) {
                Goleiro goleiro = (Goleiro) botao;
                botaoPosSrvMesa11.setRotacao(goleiro.getRotacao());
            }
            botaoPosSrvMesa11List.add(botaoPosSrvMesa11);

        }
        posicaoBtnsSrvMesa11.setBotoes(botaoPosSrvMesa11List);
        posicaoBtnsSrvMesa11.setTimeStamp(System.currentTimeMillis());
        return posicaoBtnsSrvMesa11;
    }

    public void atualizaPosicoesBotoes(
            PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11) {
        if (posicaoBtnsSrvMesa11 != null) {
            List<BotaoPosSrvMesa11> btns = posicaoBtnsSrvMesa11.getBotoes();
            for (BotaoPosSrvMesa11 botaoPosSrvMesa11 : btns) {
                Botao botao = (Botao) botoes.get(botaoPosSrvMesa11.getId());
                botao.setCentroTodos(new Point(botaoPosSrvMesa11.getPos()));
                if (botao instanceof Goleiro) {
                    Goleiro goleiro = (Goleiro) botao;
                    goleiro.setRotacao(botaoPosSrvMesa11.getRotacao());
                }
            }
        }
    }

    public Long getSequenciaAnimacao() {
        return sequenciaAnimacao;
    }

    public boolean isControleEventosRodando() {
        return controleEventosRodando;
    }

    public void setControleEventosRodando(boolean controleEventosRodando) {
        this.controleEventosRodando = controleEventosRodando;
    }

}
