package br.mesa11.conceito;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;
import br.tos.DadosJogoSrvMesa11;

public class ControlePartida {

    private ControleJogo controleJogo;
    private int tempoJogoMinutos;
    private Hashtable times;
    private int tempoJogoMilis;
    private long inicioJogoMilis;
    private long fimJogoMilis;
    private int tempoJogadaSegundos;
    private long tempoJogadaAtualMilis;
    private long tempoJogadaFimMilis;
    private boolean virouTimes = false;
    private String campoTimeComBola;
    private Time timeCima;
    private Time timeBaixo;
    private Map<Time, Integer> mapaGols = new Hashtable<Time, Integer>();
    private Map<Time, Integer> mapaJogadas = new Hashtable<Time, Integer>();
    private ControlePosicionamento controleFormacao;
    private MesaPanel mesaPanel;
    private boolean bateuCentroCima;
    private boolean bateuCentroBaixo;
    private boolean segundoUniformeCima;
    private boolean segundoUniformeBaixo;
    private Thread timerJogo;
    private List<GolJogador> golJogadors = new LinkedList<GolJogador>();

    public ControlePartida(ControleJogo controleJogo) {
        super();
        this.controleJogo = controleJogo;
        final Properties properties = new Properties();
        try {
            times = new Hashtable();
            properties.load(
                    CarregadorRecursos.recursoComoStream("times/times.properties"));
            Enumeration propName = properties.propertyNames();
            while (propName.hasMoreElements()) {
                final String name = (String) propName.nextElement();
                times.put(name, properties.getProperty(name));

            }
        } catch (IOException e) {
            Logger.logarExept(e);
        }
    }

    public boolean iniciaJogoLivre() {
        GridLayout gridLayout = new GridLayout(6, 2);
        gridLayout.setHgap(15);
        gridLayout.setVgap(5);
        JPanel escolhaTimesPanel = new JPanel(gridLayout);
        final JComboBox timesCima = new JComboBox();
        final JComboBox timesBaixo = new JComboBox();

        final JCheckBox timeCimaCPU = new JCheckBox();
        final JCheckBox timeBaixoCPU = new JCheckBox();

        final JRadioButton bolaCima = new JRadioButton();
        bolaCima.setSelected(true);
        final JRadioButton bolaBaixo = new JRadioButton();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(bolaCima);
        buttonGroup.add(bolaBaixo);

        escolhaTimesPanel.setBorder(new TitledBorder("") {

            @Override
            public String getTitle() {
                return Lang.msg("baterCentro");
            }
        });
        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("timeCima");
            }
        });
        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("timeBaixo");
            }
        });

        escolhaTimesPanel.add(timesCima);
        escolhaTimesPanel.add(timesBaixo);
        final JLabel uniformeCima = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        final JLabel uniformeBaixo = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        JPanel uniformesPanel = new JPanel(new GridLayout(1, 2));
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("cliqueSegundoUniforme");
            }
        });
        uniformesPanel.add(uniformeCima);
        uniformesPanel.add(uniformeBaixo);
        timesCima.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String xmlCima = obterKey(
                            (String) timesCima.getSelectedItem());
                    XMLDecoder xmlDecoder = new XMLDecoder(
                            CarregadorRecursos.recursoComoStream("times/" + xmlCima));
                    Time time = (Time) xmlDecoder.readObject();
                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniformeCima.setIcon(
                                new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                        segundoUniformeBaixo ? 2 : 1)));
                    } else {
                        ImageIcon icon = ImageUtil
                                .carregarImagem("times/" + time.getImagem());
                        uniformeCima.setIcon(icon);
                    }
                    segundoUniformeCima = false;
                }
            }
        });
        timesBaixo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String xmlBaixo = obterKey(
                            (String) timesBaixo.getSelectedItem());
                    XMLDecoder xmlDecoder = new XMLDecoder(
                            CarregadorRecursos.recursoComoStream("times/" + xmlBaixo));
                    Time time = (Time) xmlDecoder.readObject();
                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniformeBaixo.setIcon(
                                new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                        segundoUniformeBaixo ? 2 : 1)));
                    } else {
                        ImageIcon icon = ImageUtil
                                .carregarImagem("times/" + time.getImagem());
                        uniformeBaixo.setIcon(icon);
                    }
                    segundoUniformeBaixo = false;
                }
            }
        });

        uniformeCima.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniformeCima = !segundoUniformeCima;
                String xmlCima = obterKey((String) timesCima.getSelectedItem());
                XMLDecoder xmlDecoder = new XMLDecoder(
                        CarregadorRecursos.recursoComoStream(xmlCima));
                Time time = (Time) xmlDecoder.readObject();
                if (Util.isNullOrEmpty(time.getImagem())) {
                    uniformeCima.setIcon(
                            new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                    segundoUniformeBaixo ? 2 : 1)));
                } else {
                    ImageIcon icon = ImageUtil
                            .carregarImagem("times/" + time.getImagem());
                    uniformeCima.setIcon(icon);
                }

            }
        });

        uniformeBaixo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniformeBaixo = !segundoUniformeBaixo;
                String xmlBaixo = obterKey(
                        (String) timesBaixo.getSelectedItem());
                XMLDecoder xmlDecoder = new XMLDecoder(
                        CarregadorRecursos.recursoComoStream(xmlBaixo));
                Time time = (Time) xmlDecoder.readObject();
                if (Util.isNullOrEmpty(time.getImagem())) {
                    uniformeBaixo.setIcon(
                            new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                    segundoUniformeBaixo ? 2 : 1)));
                } else {
                    ImageIcon icon = ImageUtil
                            .carregarImagem("times/" + time.getImagem());
                    uniformeBaixo.setIcon(icon);
                }

            }
        });

        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("bateCentroCima");
            }
        });
        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("bateCentroBaixo");
            }
        });

        escolhaTimesPanel.add(bolaCima);

        escolhaTimesPanel.add(bolaBaixo);

        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("cpuCima");
            }
        });
        escolhaTimesPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("cpuBaixo");
            }
        });

        escolhaTimesPanel.add(timeCimaCPU);

        escolhaTimesPanel.add(timeBaixoCPU);

        JPanel tempoJogoPanel = new JPanel(new GridLayout(3, 2));
        tempoJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });
        final JComboBox numJogadaCombo = new JComboBox();
        for (int i = 3; i < 21; i++) {
            numJogadaCombo.addItem(new Integer(i));
        }
        numJogadaCombo.setSelectedItem(new Integer(7));
        tempoJogoPanel.add(numJogadaCombo);

        tempoJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });
        final JComboBox tempoJogoCombo = new JComboBox();
        tempoJogoCombo.addItem(new Integer(8));
        tempoJogoCombo.addItem(new Integer(10));
        tempoJogoCombo.addItem(new Integer(16));
        tempoJogoCombo.addItem(new Integer(20));
        tempoJogoCombo.addItem(new Integer(30));
        tempoJogoCombo.addItem(new Integer(40));
        tempoJogoCombo.addItem(new Integer(60));
        tempoJogoCombo.addItem(new Integer(90));
        tempoJogoPanel.add(tempoJogoCombo);
        tempoJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });
        final JComboBox tempoJogadaCombo = new JComboBox();
        tempoJogadaCombo.addItem(new Integer(20));
        tempoJogadaCombo.addItem(new Integer(30));
        tempoJogadaCombo.addItem(new Integer(40));
        tempoJogadaCombo.addItem(new Integer(50));
        tempoJogadaCombo.addItem(new Integer(60));
        tempoJogoPanel.add(tempoJogadaCombo);

        JFrame frame = controleJogo.getFrame();
        final Map botoesImagens = controleJogo.getBotoesImagens();
        final Map botoes = controleJogo.getBotoes();
        mesaPanel = controleJogo.getMesaPanel();
        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(tempoJogoPanel, BorderLayout.NORTH);
        iniciarJogoPanel.add(uniformesPanel, BorderLayout.SOUTH);
        List itens = new ArrayList();
        for (Iterator iterator = times.keySet().iterator(); iterator
                .hasNext(); ) {
            String key = (String) iterator.next();
            itens.add(times.get(key));

        }
        Collections.shuffle(itens);
        for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
            timesBaixo.addItem(iterator.next());
        }
        Collections.shuffle(itens);
        for (Iterator iterator = itens.iterator(); iterator.hasNext(); ) {
            timesCima.addItem(iterator.next());
        }
        while (timesBaixo.getItemCount() > 1 && timesCima.getSelectedItem()
                .equals(timesBaixo.getSelectedItem())) {
            timesBaixo.setSelectedIndex(
                    Util.intervalo(0, timesBaixo.getItemCount() - 1));
            Logger.logar("Selecionado Outro Time Baixo");
        }
        int val = JOptionPane.showConfirmDialog(frame, iniciarJogoPanel,
                Lang.msg("escolhaTimes"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (val != JOptionPane.YES_OPTION) {
            return false;
        }
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                controleJogo.bolaCentro();
                controleJogo.removeBkg();
                processaTempoJogo(tempoJogoCombo.getSelectedItem());
                String xmlCima = obterKey((String) timesCima.getSelectedItem());
                String xmlBaixo = obterKey(
                        (String) timesBaixo.getSelectedItem());
                if (xmlCima.equals(xmlBaixo)) {
                    return;
                }

                XMLDecoder xmlDecoder = new XMLDecoder(
                        CarregadorRecursos.recursoComoStream("times/"+xmlCima));
                timeCima = (Time) xmlDecoder.readObject();
                timeCima.setControladoCPU(timeCimaCPU.isSelected());
                timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
                timeCima.setSegundoUniforme(segundoUniformeCima);
                for (int i = 0; i < 11; i++) {
                    Long id = new Long(i + 2);
                    Botao botao = (Botao) timeCima.getBotoes().get(i);
                    if (botao.getId() == null
                            || MesaPanel.zero.equals(botao.getId())) {
                        botao.setId(id);
                    }
                    if (botao instanceof Goleiro || botao.isGoleiro()) {
                        botoesImagens.put(botao.getId(),
                                BotaoUtils.desenhaUniformeGoleiro(timeCima,
                                        timeCima.isSegundoUniforme() ? 2 : 1,
                                        (Goleiro) botao));
                        botao.setCentro(mesaPanel.golCima());
                    } else {
                        carregarBotaoImagemBotao(botoesImagens, botao,
                                timeCima);
                    }
                    botoes.put(botao.getId(), botao);

                }
                controleFormacao = new ControlePosicionamento(controleJogo);

                controleFormacao.posicionaTimeCima(timeCima,
                        bolaCima.isSelected());
                bateuCentroCima = bolaCima.isSelected();
                xmlDecoder = new XMLDecoder(
                        CarregadorRecursos.recursoComoStream("times/"+xmlBaixo));
                timeBaixo = (Time) xmlDecoder.readObject();
                timeBaixo.setControladoCPU(timeBaixoCPU.isSelected());
                timeBaixo.setCampo(ConstantesMesa11.CAMPO_BAIXO);
                timeBaixo.setSegundoUniforme(segundoUniformeBaixo);
                for (int i = 0; i < 11; i++) {
                    Long id = new Long(i + 20);
                    Botao botao = (Botao) timeBaixo.getBotoes().get(i);
                    if (botao.getId() == null
                            || MesaPanel.zero.equals(botao.getId())) {
                        botao.setId(id);
                    }
                    if (botao instanceof Goleiro || botao.isGoleiro()) {
                        botoesImagens.put(botao.getId(),
                                BotaoUtils.desenhaUniformeGoleiro(timeBaixo,
                                        segundoUniformeBaixo ? 2 : 1,
                                        (Goleiro) botao));
                        botao.setCentro(mesaPanel.golBaixo());
                    } else {
                        carregarBotaoImagemBotao(botoesImagens, botao,
                                timeBaixo);
                    }
                    botoes.put(botao.getId(), botao);
                }
                controleFormacao.posicionaTimeBaixo(timeBaixo,
                        bolaBaixo.isSelected());
                bateuCentroBaixo = bolaBaixo.isSelected();
                controleJogo.bolaCentro();
                iniciaTempoJogada(tempoJogadaCombo.getSelectedItem(),
                        bolaCima.isSelected()
                                ? ConstantesMesa11.CAMPO_CIMA
                                : ConstantesMesa11.CAMPO_BAIXO);
                if (timeCima.getId() == null) {
                    timeCima.setId(System.currentTimeMillis());
                }
                if (timeBaixo.getId() == null) {
                    timeBaixo.setId(timeCima.getId() + 1);
                }
                controleJogo.setNumeroJogadas(
                        (Integer) numJogadaCombo.getSelectedItem());
                mapaGols.put(timeCima, new Integer(0));
                mapaGols.put(timeBaixo, new Integer(0));
                mapaJogadas.put(timeCima, new Integer(0));
                mapaJogadas.put(timeBaixo, new Integer(0));
                controleJogo.escondePorgressBar();
                controleJogo.setJogoIniciado(true);
                controleJogo.atualizaBotoesCopia();
            }
        });
        run.start();
        return true;
    }

    private void carregarBotaoImagemBotao(Map botoesImagens, Botao botao,
                                          Time time) {

        if (controleJogo != null && controleJogo.isJogoOnlineSrvidor()) {
            return;
        }
        controleJogo.incrementaBarraCarregando();
        BufferedImage buff = null;
        boolean imgCust = false;

        if (!Util.isNullOrEmpty(botao.getImagem())) {
            URL url = null;
            try {
                String caminho = "times/"
                        + botao.getImagem();
                buff = CarregadorRecursos.carregaImagemLocal(caminho);
                if (buff == null) {
                    url = new URL(caminho);
                    Logger.logar(url);
                    buff = CarregadorRecursos.carregaImagemURL(url);
                }
                if (buff == null) {
                    caminho = "times/"
                            + time.getImagem();
                    buff = CarregadorRecursos.carregaImagemLocal(caminho);
                    if (buff == null) {
                        url = new URL(caminho);
                        Logger.logar(url);
                        buff = CarregadorRecursos.carregaImagemURL(url);
                    }
                }
                imgCust = true;
            } catch (Exception e) {
                Logger.logarExept(e);
            } finally {
            }
        }
        if (buff == null)
            buff = BotaoUtils.desenhaUniforme(time,
                    time.isSegundoUniforme() ? 2 : 1, botao);
        BufferedImage newBuffer = null;
        if (imgCust) {
            newBuffer = new BufferedImage((buff.getWidth() + 1),
                    (buff.getHeight() + 1), BufferedImage.TYPE_INT_ARGB);
        } else {
            newBuffer = new BufferedImage((buff.getWidth()), (buff.getHeight()),
                    BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
        setarHints(graphics2d);

        Ellipse2D externo = null;
        if (imgCust) {
            externo = new Ellipse2D.Double(1, 1, (buff.getWidth() - 1),
                    (buff.getHeight() - 1));
        } else {
            externo = new Ellipse2D.Double(0, 0, (buff.getWidth()),
                    (buff.getHeight()));
        }

        graphics2d.setClip(externo);
        graphics2d.drawImage(buff, 0, 0, null);
        botoesImagens.put(botao.getId(), newBuffer);

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

    private void iniciaTempoJogada(Object selectedItem, String timeComBola) {
        tempoJogadaSegundos = (Integer) selectedItem;
        this.campoTimeComBola = timeComBola;
        zerarTimerJogada();
        if (controleJogo.isJogoOnlineCliente()) {
            return;
        }
        if (timerJogo != null) {
            timerJogo.interrupt();
        }
        timerJogo = new TimerJogo(this, controleJogo);
        timerJogo.start();
        Logger.logar("timeComBola " + timeComBola);
    }

    private void processaTempoJogo(Object selectedItem) {
        tempoJogoMinutos = (Integer) selectedItem;
        tempoJogoMilis = tempoJogoMinutos * 60 * 1000;
        inicioJogoMilis = System.currentTimeMillis();
        fimJogoMilis = inicioJogoMilis + tempoJogoMilis;
        Logger.logar("Inicio Jogo " + df.format(inicioJogoMilis));
        Logger.logar("Tempo Jogo " + formatarTempo(tempoJogoMilis));
        Logger.logar("Fim Jogo " + df.format(fimJogoMilis));
    }

    private static DecimalFormat dez = new DecimalFormat("00");
    private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String formatarTempo(long fullnum) {

        long minu = (fullnum / 60000);
        long seg = ((fullnum - (minu * 60000)) / 1000);
        if (minu > 0)
            return (minu) + ":" + dez.format(Math.abs(seg));
        else
            return seg + "";
    }

    private String obterKey(String value) {
        for (Iterator iterator = times.keySet().iterator(); iterator
                .hasNext(); ) {
            String key = (String) iterator.next();
            String val = (String) times.get(key);
            if (value.equals(val)) {
                return key;
            }
        }
        return null;
    }

    public String tempoJogoFormatado() {
        if (Util.isNullOrEmpty(campoTimeComBola)) {
            return "";
        }
        return formatarTempo(tempoJogoMilis);
    }

    public String tempoRestanteJogoFormatado() {
        if (Util.isNullOrEmpty(campoTimeComBola)) {
            return "";
        }
        return formatarTempo(fimJogoMilis - System.currentTimeMillis());
    }

    public long tempoRestanteJogo() {
        long tempo = fimJogoMilis - System.currentTimeMillis();
        if (tempo < 0) {
            return 0;
        }
        return tempo;
    }

    public String tempoJogadaRestanteJogoFormatado() {
        if (Util.isNullOrEmpty(campoTimeComBola)) {
            return "";
        }
        return formatarTempo(tempoJogadaFimMilis - System.currentTimeMillis());
    }

    public void verificaFalhaPorTempo() {
        if (tempoJogadaFimMilis < System.currentTimeMillis()) {
            Logger.logar("verificaFalhaPorTempo");
            reversaoJogada();
            controleJogo.setDica("reversao");
        }
    }

    public void zerarTimerJogada() {
        tempoJogadaAtualMilis = System.currentTimeMillis();
        tempoJogadaFimMilis = tempoJogadaAtualMilis
                + (tempoJogadaSegundos * 1000);
    }

    public void verificaIntervalo() {
        if (virouTimes) {
            return;
        }
        if (controleJogo.isAnimando()) {
            return;
        }
        if (System.currentTimeMillis() > (inicioJogoMilis
                + (tempoJogoMilis / 2))) {
            Time aux = timeBaixo;
            timeBaixo = timeCima;
            timeCima = aux;
            timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
            timeBaixo.setCampo(ConstantesMesa11.CAMPO_BAIXO);
            controleFormacao.posicionaTimeCima(timeCima, !bateuCentroBaixo);
            if (!bateuCentroBaixo) {
                zeraJogadaTime(timeCima);
            }
            controleFormacao.posicionaTimeBaixo(timeBaixo, !bateuCentroCima);
            if (!bateuCentroCima) {
                zeraJogadaTime(timeBaixo);
            }
            centralizaGoleiroBaixo();
            centralizaGoleiroCima();
            zerarJogadas();
            controleJogo.bolaCentro();
            virouTimes = true;

            Logger.logar("Intervalo");
            controleJogo.setDica("intervalo");
            controleJogo.adicionaJogadaCliente();
        }
    }

    public boolean veririficaVez(Botao b) {
        if (b == null) {
            return false;
        }
        Time time = b.getTime();
        if (b.getId() == 0) {
            Goleiro goleiroCima = controleJogo.obterGoleiroCima();
            double distaciaEntrePontosCima = GeoUtil.distaciaEntrePontos(
                    b.getCentro(), goleiroCima.getCentro());
            if (distaciaEntrePontosCima < goleiroCima.getDiamentro()) {
                time = goleiroCima.getTime();
            }
            Goleiro goleiroBaixo = controleJogo.obterGoleiroBaixo();
            double distaciaEntrePontosBaixo = GeoUtil.distaciaEntrePontos(
                    b.getCentro(), goleiroBaixo.getCentro());
            if (distaciaEntrePontosBaixo < goleiroBaixo.getDiamentro()) {
                time = goleiroBaixo.getTime();
            }
        }
        if (time == null) {
            return false;
        }
        return campoTimeComBola.equals(time.getCampo());
    }

    public Time timeJogadaVez() {
        if (controleJogo.isJogoOnlineCliente()) {
            return timeCima.getNome()
                    .equals(controleJogo.getDadosJogoSrvMesa11().getTimeVez())
                    ? timeCima
                    : timeBaixo;
        }
        if (Util.isNullOrEmpty(campoTimeComBola)) {
            return null;
        }
        return timeCima.getCampo().equals(campoTimeComBola)
                ? timeCima
                : timeBaixo;
    }

    public void reversaoJogada() {
        Time timePerdeuBola = timeJogadaVez();
        List<Botao> botoes = timePerdeuBola.getBotoes();
        for (Iterator iterator = botoes.iterator(); iterator.hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            if (GeoUtil.distaciaEntrePontos(controleJogo.getBola().getCentro(),
                    botao.getCentro()) < ConstantesMesa11.RAIO_BOTAO) {
                controleJogo.posicionaBotaoAleatoriamenteNoSeuCampo(botao);
            }
        }
        campoTimeComBola();
        Logger.logar("reversaoJogada");
        zerarJogadas();
        zerarTimerJogada();
        controleJogo.centralizaBola();
    }

    private void campoTimeComBola() {
        campoTimeComBola = (ConstantesMesa11.CAMPO_BAIXO
                .equals(campoTimeComBola)
                ? ConstantesMesa11.CAMPO_CIMA
                : ConstantesMesa11.CAMPO_BAIXO);
    }

    public void zeraJogadaTime(Time time) {
        campoTimeComBola = time.getCampo();
        // Logger.logar("zeraJogadaTime");
        zerarTimerJogada();
    }

    public Time getTimeCima() {
        return timeCima;
    }

    public void setTimeCima(Time timeCima) {
        this.timeCima = timeCima;
    }

    public Time getTimeBaixo() {
        return timeBaixo;
    }

    public void setTimeBaixo(Time timeBaixo) {
        this.timeBaixo = timeBaixo;
    }

    public boolean verificaBolaPertoGoleiroTime(Time time, Botao bola) {
        Goleiro goleiroTime = obterGoleiroTime(time);
        double distGoleiroTime = GeoUtil
                .distaciaEntrePontos(goleiroTime.getCentro(), bola.getCentro());
        double outraDistancia = 0;
        if (timeCima.equals(time)) {
            goleiroTime = obterGoleiroTime(timeBaixo);
            outraDistancia = GeoUtil.distaciaEntrePontos(
                    goleiroTime.getCentro(), bola.getCentro());
        } else {
            goleiroTime = obterGoleiroTime(timeCima);
            outraDistancia = GeoUtil.distaciaEntrePontos(
                    goleiroTime.getCentro(), bola.getCentro());
        }
        return distGoleiroTime < outraDistancia;
    }

    public Goleiro obterGoleiroTime(Time time) {
        return time.obterGoleiro();
    }

    public void processarGol(Botao botao) {
        Time time = botao.getTime();
        if (timeCima.equals(time)) {
            controleFormacao.posicionaTimeCima(timeCima, false);
            controleFormacao.posicionaTimeBaixo(timeBaixo, true);
            Integer gols = (Integer) mapaGols.get(timeCima);
            mapaGols.put(timeCima, new Integer(gols.intValue() + 1));
        } else {
            controleFormacao.posicionaTimeCima(timeCima, true);
            controleFormacao.posicionaTimeBaixo(timeBaixo, false);
            Integer gols = (Integer) mapaGols.get(timeBaixo);
            mapaGols.put(timeBaixo, new Integer(gols.intValue() + 1));
        }
        GolJogador golJogador = new GolJogador();
        golJogador.setIdJogador(botao.getId());
        golJogador.setTempoGol(tempoRestanteJogoFormatado());
        golJogadors.add(golJogador);
        centralizaGoleiroBaixo();
        centralizaGoleiroCima();
        zerarJogadas();
        controleJogo.bolaCentro();
        campoTimeComBola();
    }

    public void processarGolContra(Botao botao) {
        Time time = botao.getTime();
        if (timeCima.equals(time)) {
            controleFormacao.posicionaTimeCima(timeCima, true);
            controleFormacao.posicionaTimeBaixo(timeBaixo, false);
            Integer gols = (Integer) mapaGols.get(timeBaixo);
            mapaGols.put(timeBaixo, new Integer(gols.intValue() + 1));
        } else {
            controleFormacao.posicionaTimeCima(timeCima, false);
            controleFormacao.posicionaTimeBaixo(timeBaixo, true);
            Integer gols = (Integer) mapaGols.get(timeCima);
            mapaGols.put(timeCima, new Integer(gols.intValue() + 1));
        }
        GolJogador golJogador = new GolJogador();
        golJogador.setIdJogador(botao.getId());
        golJogador.setTempoGol(tempoRestanteJogoFormatado());
        golJogador.setContra(true);
        golJogadors.add(golJogador);
        centralizaGoleiroBaixo();
        centralizaGoleiroCima();
        zerarJogadas();
        controleJogo.bolaCentro();
        campoTimeComBola();
    }

    public List<GolJogador> getGolJogadors() {
        return golJogadors;
    }

    public void centralizaGoleiroBaixo() {
        for (int i = 0; i < 11; i++) {
            Long id = new Long(i + 20);
            Botao botao = (Botao) timeBaixo.getBotoes().get(i);
            if (botao instanceof Goleiro || botao.isGoleiro()) {
                botao.setCentro(mesaPanel.golBaixo());
                botao.setAngulo(0);
                break;
            }
        }

    }

    public void centralizaGoleiroCima() {
        for (int i = 0; i < 11; i++) {
            Long id = new Long(i + 20);
            Botao botao = (Botao) timeCima.getBotoes().get(i);
            if (botao instanceof Goleiro || botao.isGoleiro()) {
                botao.setCentroTodos(mesaPanel.golCima());
                botao.setAngulo(0);
                break;
            }
        }

    }

    public String verGols(Time time) {
        Integer integer = mapaGols.get(time);
        if (integer == null) {
            return "";
        }
        return integer.toString();
    }

    public Integer verGolsInt(Time time) {
        return mapaGols.get(time);
    }

    public boolean incrementaJogada() {
        Time time = timeJogadaVez();
        Integer numJogadas = (Integer) mapaJogadas.get(time);
        if (numJogadas.intValue() >= (controleJogo.getNumeroJogadas() - 1)) {
            zerarJogadas();
            return false;
        } else {
            mapaJogadas.put(time, new Integer(numJogadas + 1));
            return true;
        }
    }

    public void zerarJogadas() {
        Set keySet = mapaJogadas.keySet();
        for (Iterator iterator = keySet.iterator(); iterator.hasNext(); ) {
            Object time = (Object) iterator.next();
            mapaJogadas.put((Time) time, new Integer(0));
        }
    }

    public Integer obterNumJogadas(Time time) {
        return mapaJogadas.get(time);
    }

    public void iniciaJogoOnline(DadosJogoSrvMesa11 dadosJogoSrvMesa11,
                                 Time timeCasa, Time timeVisita) {
        this.mesaPanel = controleJogo.getMesaPanel();
        Map botoes = controleJogo.getBotoes();
        Map botoesImagens = controleJogo.getBotoesImagens();
        controleFormacao = new ControlePosicionamento(controleJogo);
        Time timeBola = null;
        if (ConstantesMesa11.BOLA
                .equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
            if (ConstantesMesa11.CAMPO_CIMA
                    .equals(dadosJogoSrvMesa11.getBolaCampoVisita())) {
                controleFormacao.posicionaTimeCima(timeVisita, false);
                controleFormacao.posicionaTimeBaixo(timeCasa, true);
                timeCima = timeVisita;
                timeBaixo = timeCasa;
                timeBola = timeBaixo;
                bateuCentroBaixo = true;
                bateuCentroCima = false;
            } else {
                controleFormacao.posicionaTimeBaixo(timeVisita, false);
                controleFormacao.posicionaTimeCima(timeCasa, true);
                timeCima = timeCasa;
                timeBaixo = timeVisita;
                timeBola = timeCima;
                bateuCentroCima = true;
                bateuCentroBaixo = false;
            }
        }
        if (ConstantesMesa11.BOLA
                .equals(dadosJogoSrvMesa11.getBolaCampoVisita())) {
            if (ConstantesMesa11.CAMPO_CIMA
                    .equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
                controleFormacao.posicionaTimeCima(timeCasa, false);
                controleFormacao.posicionaTimeBaixo(timeVisita, true);
                timeCima = timeCasa;
                timeBaixo = timeVisita;
                timeBola = timeBaixo;
                bateuCentroBaixo = true;
                bateuCentroCima = false;
            } else {
                controleFormacao.posicionaTimeBaixo(timeCasa, false);
                controleFormacao.posicionaTimeCima(timeVisita, true);
                timeCima = timeVisita;
                timeBaixo = timeCasa;
                timeBola = timeCima;
                bateuCentroCima = true;
                bateuCentroBaixo = false;
            }
        }
        timeCima.setCampo(ConstantesMesa11.CAMPO_CIMA);
        timeBaixo.setCampo(ConstantesMesa11.CAMPO_BAIXO);
        List botoesTimeCima = timeCima.getBotoes();
        for (Iterator iterator = botoesTimeCima.iterator(); iterator
                .hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            botoes.put(botao.getId(), botao);
            if (botao instanceof Goleiro || botao.isGoleiro()) {
                botoesImagens.put(botao.getId(),
                        BotaoUtils.desenhaUniformeGoleiro(timeCima,
                                timeCima.isSegundoUniforme() ? 2 : 1,
                                (Goleiro) botao));
                botao.setCentro(mesaPanel.golCima());
            } else {
                carregarBotaoImagemBotao(botoesImagens, botao, timeCima);
            }
        }
        List botoesTimeBaixo = timeBaixo.getBotoes();
        for (Iterator iterator = botoesTimeBaixo.iterator(); iterator
                .hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            botoes.put(botao.getId(), botao);
            if (botao instanceof Goleiro || botao.isGoleiro()) {
                botoesImagens.put(botao.getId(),
                        BotaoUtils.desenhaUniformeGoleiro(timeBaixo,
                                timeBaixo.isSegundoUniforme() ? 2 : 1,
                                (Goleiro) botao));
                botao.setCentro(mesaPanel.golBaixo());
            } else {
                carregarBotaoImagemBotao(botoesImagens, botao, timeBaixo);
            }
        }
        if (controleJogo.isJogoOnlineSrvidor()) {
            botoesImagens.clear();
            mapaGols.put(timeCima, new Integer(0));
            mapaGols.put(timeBaixo, new Integer(0));
            mapaJogadas.put(timeCima, new Integer(0));
            mapaJogadas.put(timeBaixo, new Integer(0));
            String campoTimeComBola = timeBola.equals(timeCima)
                    ? timeCima.getCampo()
                    : timeBaixo.getCampo();
            processaTempoJogo(controleJogo.getJogoServidor()
                    .getDadosJogoSrvMesa11().getTempoJogo());
            iniciaTempoJogada(controleJogo.getJogoServidor()
                            .getDadosJogoSrvMesa11().getTempoJogoJogada(),
                    campoTimeComBola);
        }
        controleJogo.escondePorgressBar();
    }

    public void verificaFimJogo() {
        if (controleJogo.isAnimando()) {
            return;
        }
        if (System.currentTimeMillis() > fimJogoMilis
                || controleJogo.getTempoRestanteJogo() <= 0) {
            controleJogo.setDica("fimJogo");
            controleJogo.setJogoTerminado(true);
            controleJogo.fimJogoServidor();
            Logger.logar("fimJogo");
        }

    }
}
