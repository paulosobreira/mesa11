package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.CampeonatoMesa11;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.Java2sAutoComboBox;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.recursos.Lang;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;

public class ControleJogosCliente {
    private ChatWindow chatWindow;
    private boolean segundoUniforme;
    private boolean segundoUniformeCpu;
    private ControleChatCliente controleChatCliente;
    private JComboBox jComboBoxTimes = new JComboBox(
            new String[]{Lang.msg("semTimes")});
    private JComboBox jComboBoxTimesCpu = new JComboBox(
            new String[]{Lang.msg("semTimes")});

    private DadosMesa11 dadosMesa11;
    private MonitorJogo monitorJogo;
    private Mesa11Applet mesa11Applet;

    public DadosMesa11 getDadosMesa11() {
        return dadosMesa11;
    }

    public void setDadosMesa11(DadosMesa11 dadosMesa11) {
        this.dadosMesa11 = dadosMesa11;
    }

    public ControleJogosCliente(ChatWindow chatWindow,
                                ControleChatCliente controleChatCliente,
                                Mesa11Applet mesa11Applet) {
        super();
        this.chatWindow = chatWindow;
        this.controleChatCliente = controleChatCliente;
        this.mesa11Applet = mesa11Applet;
    }

    private Object enviarObjeto(NnpeTO mesa11to) {
        if (mesa11Applet == null) {
            Logger.logar("enviarObjeto mesa11Applet null");
            return null;
        }
        return mesa11Applet.enviarObjeto(mesa11to);
    }

    public void criarJogo() {
        if (monitorJogo != null && monitorJogo.isAlive()) {
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    Lang.msg("jaEstaEmUmJogo"));
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
        Object ret = enviarObjeto(mesa11to);
        if (!(ret instanceof NnpeTO)) {
            return;
        }
        final JLabel uniforme = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };

        mesa11to = (NnpeTO) ret;
        String[] times = (String[]) mesa11to.getData();
        jComboBoxTimes = new JComboBox(times);
        String nomeTime0 = times[0];
        mesa11to = new NnpeTO();
        mesa11to.setData(nomeTime0);
        mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
        ret = enviarObjeto(mesa11to);
        mesa11to = (NnpeTO) ret;
        Time time = (Time) mesa11to.getData();
        if (Util.isNullOrEmpty(time.getImagem())) {
            uniforme.setIcon(new ImageIcon(
                    BotaoUtils.desenhaUniforme(time, segundoUniforme ? 2 : 1)));
        } else {
            ImageIcon icon = ImageUtil
                    .carregarImagem("times/"
                            + time.getImagem());
            uniforme.setIcon(icon);
        }
        segundoUniforme = false;

        JPanel panelComboTimes = new JPanel();
        jComboBoxTimes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                    NnpeTO mesa11to = new NnpeTO();
                    mesa11to.setData(nomeTime);
                    mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                    Object ret = enviarObjeto(mesa11to);
                    mesa11to = (NnpeTO) ret;
                    Time time = (Time) mesa11to.getData();

                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniforme.setIcon(
                                new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                        segundoUniforme ? 2 : 1)));
                    } else {
                        ImageIcon icon = ImageUtil
                                .carregarImagem("times/" + time.getImagem());
                        uniforme.setIcon(icon);
                    }
                    segundoUniforme = false;
                }
            }
        });

        uniforme.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniforme = !segundoUniforme;
                String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                NnpeTO mesa11to = new NnpeTO();
                mesa11to.setData(nomeTime);
                mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                Object ret = enviarObjeto(mesa11to);
                mesa11to = (NnpeTO) ret;
                Time time = (Time) mesa11to.getData();
                uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time,
                        segundoUniforme ? 2 : 1)));
            }
        });
        JPanel uniformesPanel = new JPanel();
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("cliqueSegundoUniforme");
            }
        });

        uniformesPanel.add(uniforme);

        panelComboTimes.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("escolhaTime");
            }
        });
        panelComboTimes.add(jComboBoxTimes);
        JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
        escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
        escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

        JPanel opcoesJogoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });
        JComboBox numJogadaCombo = new JComboBox();
        for (int i = 3; i < 21; i++) {
            numJogadaCombo.addItem(new Integer(i));
        }
        numJogadaCombo.setSelectedIndex(4);
        opcoesJogoPanel.add(numJogadaCombo);

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });
        JComboBox tempoJogoCombo = new JComboBox();
        tempoJogoCombo.addItem(new Integer(8));
        tempoJogoCombo.addItem(new Integer(10));
        tempoJogoCombo.addItem(new Integer(16));
        tempoJogoCombo.addItem(new Integer(20));
        tempoJogoCombo.addItem(new Integer(40));
        tempoJogoCombo.addItem(new Integer(60));
        tempoJogoCombo.addItem(new Integer(90));
        opcoesJogoPanel.add(tempoJogoCombo);
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });
        JComboBox tempoJogadaCombo = new JComboBox();
        tempoJogadaCombo.addItem(new Integer(20));
        tempoJogadaCombo.addItem(new Integer(30));
        tempoJogadaCombo.addItem(new Integer(40));
        tempoJogadaCombo.addItem(new Integer(50));
        tempoJogadaCombo.addItem(new Integer(60));
        tempoJogadaCombo.addItem(new Integer(90));
        tempoJogadaCombo.setSelectedIndex(1);
        opcoesJogoPanel.add(tempoJogadaCombo);

        JComboBox campoBolaCombo = new JComboBox();
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("campoBola");
            }
        });
        opcoesJogoPanel.add(campoBolaCombo);

        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("senhaJogo");
            }
        });
        JTextField jTextFieldSenhaJogo = new JTextField();
        opcoesJogoPanel.add(jTextFieldSenhaJogo);

        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                iniciarJogoPanel, Lang.msg("criarJogo"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            DadosJogoSrvMesa11 dadosJogoSrvMesa11 = new DadosJogoSrvMesa11();
            String jogador = controleChatCliente.getSessaoCliente()
                    .getNomeJogador();
            dadosJogoSrvMesa11.setNomeCriador(jogador);
            String nomeTime = (String) jComboBoxTimes.getSelectedItem();
            dadosJogoSrvMesa11.setTimeCasa(nomeTime);
            dadosJogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
            dadosJogoSrvMesa11
                    .setTempoJogo((Integer) tempoJogoCombo.getSelectedItem());
            dadosJogoSrvMesa11.setNumeroJogadas(
                    (Integer) numJogadaCombo.getSelectedItem());
            dadosJogoSrvMesa11.setTempoJogoJogada(
                    (Integer) tempoJogadaCombo.getSelectedItem());
            dadosJogoSrvMesa11.setBolaCampoCasa(
                    Lang.key((String) campoBolaCombo.getSelectedItem()));
            dadosJogoSrvMesa11.setSenhaJogo(jTextFieldSenhaJogo.getText());
            mesa11to = new NnpeTO();
            mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO);
            mesa11to.setData(dadosJogoSrvMesa11);
            ret = enviarObjeto(mesa11to);
            if (ret instanceof NnpeTO) {
                mesa11to = (NnpeTO) ret;
                dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
                monitorJogo = new MonitorJogo(controleChatCliente, this,
                        dadosJogoSrvMesa11, mesa11Applet,
                        dadosJogoSrvMesa11.getTimeCasa());
                monitorJogo.start();
            }
        }
    }

    public void entrarJogo(String jogoSelecionado) {
        if (monitorJogo != null && monitorJogo.isAlive()) {
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    Lang.msg("jaEstaEmUmJogo"));
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
        mesa11to.setData(jogoSelecionado);
        Object ret = enviarObjeto(mesa11to);
        if (!(ret instanceof NnpeTO)) {
            return;
        }
        mesa11to = (NnpeTO) ret;
        final DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
                .getData();

        final JLabel uniformeCasa = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        mesa11to = new NnpeTO();
        mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
        mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
        ret = enviarObjeto(mesa11to);
        mesa11to = (NnpeTO) ret;
        Time timeCasa = (Time) mesa11to.getData();
        if (Util.isNullOrEmpty(timeCasa.getImagem())) {
            uniformeCasa.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
                    timeCasa,
                    dadosJogoSrvMesa11.isSegundoUniformeTimeCasa() ? 2 : 1)));
        } else {
            ImageIcon icon = ImageUtil
                    .carregarImagem("times/"
                            + timeCasa.getImagem());
            uniformeCasa.setIcon(icon);
        }
        final JLabel uniforme = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
        ret = enviarObjeto(mesa11to);
        if (ret instanceof NnpeTO) {
            mesa11to = (NnpeTO) ret;
            String[] times = (String[]) mesa11to.getData();
            Vector timesDisponiveis = new Vector();
            for (int i = 0; i < times.length; i++) {
                if (!dadosJogoSrvMesa11.getTimeCasa().equals(times[i])) {
                    timesDisponiveis.add(times[i]);
                }
            }
            jComboBoxTimes = new JComboBox(timesDisponiveis);
            String nomeTime = (String) timesDisponiveis.get(0);
            mesa11to = new NnpeTO();
            mesa11to.setData(nomeTime);
            mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
            ret = enviarObjeto(mesa11to);
            mesa11to = (NnpeTO) ret;
            Time time = (Time) mesa11to.getData();
            if (Util.isNullOrEmpty(time.getImagem())) {
                uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time,
                        segundoUniforme ? 2 : 1)));
            } else {
                ImageIcon icon = ImageUtil
                        .carregarImagem(
                                "times/" + time.getImagem());
                uniforme.setIcon(icon);
            }
            segundoUniforme = false;
        }
        jComboBoxTimes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                    NnpeTO mesa11to = new NnpeTO();
                    mesa11to.setData(nomeTime);
                    mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                    Object ret = enviarObjeto(mesa11to);
                    mesa11to = (NnpeTO) ret;
                    Time time = (Time) mesa11to.getData();
                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniforme.setIcon(
                                new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                        segundoUniforme ? 2 : 1)));
                    } else {
                        ImageIcon icon = ImageUtil.carregarImagem(
                                "times/"
                                        + time.getImagem());
                        uniforme.setIcon(icon);
                    }
                    segundoUniforme = false;
                }
            }
        });
        if (dadosJogoSrvMesa11.getIdRodadaCampeonato() != 0) {
            jComboBoxTimes.setSelectedItem(dadosJogoSrvMesa11.getTimeVisita());
            jComboBoxTimes.setEnabled(false);
        }

        uniforme.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniforme = !segundoUniforme;
                String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                NnpeTO mesa11to = new NnpeTO();
                mesa11to.setData(nomeTime);
                mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                Object ret = enviarObjeto(mesa11to);
                mesa11to = (NnpeTO) ret;
                Time time = (Time) mesa11to.getData();
                if (Util.isNullOrEmpty(time.getImagem())) {
                    uniforme.setIcon(new ImageIcon(BotaoUtils
                            .desenhaUniforme(time, segundoUniforme ? 2 : 1)));
                }
            }
        });
        JPanel uniformesPanel = new JPanel();
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("CliqueSegundoUniforme");
            }
        });
        uniformesPanel.add(uniformeCasa);
        uniformesPanel.add(uniforme);
        JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
        panelComboTimes.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("escolhaTime");
            }
        });
        panelComboTimes.add(new JLabel(dadosJogoSrvMesa11.getTimeCasa() + " - "
                + dadosJogoSrvMesa11.getNomeCriador()));
        panelComboTimes.add(jComboBoxTimes);
        JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
        escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
        escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

        JPanel opcoesJogoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getNumeroJogadas();
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getTempoJogo();
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getTempoJogoJogada();
            }
        });
        JComboBox campoBolaCombo = new JComboBox();
        if (ConstantesMesa11.BOLA
                .equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
            campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
            campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
        } else {
            campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
        }
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("campoBola");
            }
        });
        opcoesJogoPanel.add(campoBolaCombo);
        if (dadosJogoSrvMesa11.getIdRodadaCampeonato() != 0) {
            campoBolaCombo.setSelectedItem(
                    Lang.msg(dadosJogoSrvMesa11.getBolaCampoVisita()));
            campoBolaCombo.setEnabled(false);
        }

        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("senhaJogo");
            }
        });
        JTextField jTextFieldSenhaJogo = new JTextField();
        opcoesJogoPanel.add(jTextFieldSenhaJogo);
        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                iniciarJogoPanel, Lang.msg("criarJogo"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String jogador = controleChatCliente.getSessaoCliente()
                    .getNomeJogador();
            String nomeTime = (String) jComboBoxTimes.getSelectedItem();
            dadosJogoSrvMesa11.setSegundoUniformeTimeVisita(segundoUniforme);
            if (dadosJogoSrvMesa11.getIdRodadaCampeonato() == 0) {
                dadosJogoSrvMesa11.setTimeVisita(nomeTime);
                dadosJogoSrvMesa11.setBolaCampoVisita(
                        Lang.key((String) campoBolaCombo.getSelectedItem()));
            }
            dadosJogoSrvMesa11.setSenhaJogo(jTextFieldSenhaJogo.getText());
            dadosJogoSrvMesa11.setNomeVisitante(
                    controleChatCliente.getSessaoCliente().getNomeJogador());
            mesa11to = new NnpeTO();
            mesa11to.setComando(ConstantesMesa11.ENTRAR_JOGO);
            mesa11to.setData(dadosJogoSrvMesa11);
            ret = enviarObjeto(mesa11to);
            if (ret instanceof NnpeTO) {
                mesa11to = (NnpeTO) ret;
                DadosJogoSrvMesa11 dadosJogoSrvMesa11Jogo = (DadosJogoSrvMesa11) mesa11to
                        .getData();
                Logger.logar("Entar Jogo");
                monitorJogo = new MonitorJogo(controleChatCliente, this,
                        dadosJogoSrvMesa11Jogo, mesa11Applet,
                        dadosJogoSrvMesa11.getTimeVisita());
                monitorJogo.start();
            }
        }

    }

    public void verDetalhesJogo(String jogoSelecionado) {
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
        mesa11to.setData(jogoSelecionado);
        Object ret = enviarObjeto(mesa11to);
        if (!(ret instanceof NnpeTO)) {
            return;
        }
        mesa11to = (NnpeTO) ret;
        final DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
                .getData();
        final JLabel uniformeCasa = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        mesa11to = new NnpeTO();
        mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
        mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
        ret = enviarObjeto(mesa11to);
        mesa11to = (NnpeTO) ret;
        Time timeCasa = (Time) mesa11to.getData();
        if (Util.isNullOrEmpty(timeCasa.getImagem())) {
            uniformeCasa.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
                    timeCasa,
                    dadosJogoSrvMesa11.isSegundoUniformeTimeCasa() ? 2 : 1)));
        } else {
            ImageIcon icon = ImageUtil
                    .carregarImagem("times/"
                            + timeCasa.getImagem());
            uniformeCasa.setIcon(icon);
        }
        final JLabel uniforme = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())) {
            mesa11to = new NnpeTO();
            mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
            mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
            ret = enviarObjeto(mesa11to);
            mesa11to = (NnpeTO) ret;
            Time timeVisita = (Time) mesa11to.getData();

            if (Util.isNullOrEmpty(timeVisita.getImagem())) {
                uniforme.setIcon(
                        new ImageIcon(BotaoUtils.desenhaUniforme(timeVisita,
                                dadosJogoSrvMesa11.isSegundoUniformeTimeVisita()
                                        ? 2
                                        : 1)));
            } else {
                ImageIcon icon = ImageUtil
                        .carregarImagem("times/" + timeVisita.getImagem());
                uniforme.setIcon(icon);
            }
        }
        JPanel uniformesPanel = new JPanel();
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("times");
            }
        });
        uniformesPanel.add(uniformeCasa);
        uniformesPanel.add(uniforme);
        JPanel panelComboTimes = new JPanel(new GridLayout(1, 2, 10, 5));
        panelComboTimes.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("jogadores");
            }
        });
        String nmTimeCasa = Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeCasa())
                ? ""
                : dadosJogoSrvMesa11.getTimeCasa();
        String nmCriador = Util
                .isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())
                ? ""
                : dadosJogoSrvMesa11.getNomeCriador();

        panelComboTimes.add(new JLabel(nmTimeCasa + " - "
                + dadosJogoSrvMesa11.getGolsCasa() + " - " + nmCriador));
        String nmTimeViz = Util
                .isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())
                ? ""
                : dadosJogoSrvMesa11.getTimeVisita();
        String nmViz = Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())
                ? ""
                : dadosJogoSrvMesa11.getNomeVisitante();
        if (dadosJogoSrvMesa11.isJogoVsCpu()) {
            nmViz = "CPU";
        }
        if (!(Util.isNullOrEmpty(nmViz)))
            panelComboTimes.add(new JLabel(nmTimeViz + " - "
                    + dadosJogoSrvMesa11.getGolsVisita() + " - " + nmViz));
        JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
        escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
        escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);
        JPanel opcoesJogoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getNumeroJogadas();
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getTempoJogo();
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return "" + dadosJogoSrvMesa11.getTempoJogoJogada();
            }
        });

        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

        JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                iniciarJogoPanel, Lang.msg("criarJogo"),
                JOptionPane.INFORMATION_MESSAGE);

    }

    public boolean verificaJogosNasListas(String nomeJogo) {
        if (dadosMesa11 == null) {
            return false;
        }
        if (dadosMesa11.getJogosCriados().contains(nomeJogo)) {
            return true;
        }
        if (dadosMesa11.getJogosAndamento().contains(nomeJogo)) {
            return true;
        }
        return false;
    }

    public void sairJogo() {
        if (monitorJogo != null) {
            monitorJogo.setJogoTerminado(true);
            monitorJogo.interrupt();
        }
        monitorJogo = null;
    }

    public void criarJogoVsCPU() {
        if (monitorJogo != null && monitorJogo.isAlive()) {
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    Lang.msg("jaEstaEmUmJogo"));
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
        Object ret = enviarObjeto(mesa11to);
        final JLabel uniforme = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        final JLabel uniformeCpu = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        if (ret instanceof NnpeTO) {
            mesa11to = (NnpeTO) ret;
            String[] times = (String[]) mesa11to.getData();
            jComboBoxTimes = new JComboBox(times);
            jComboBoxTimesCpu = new JComboBox(times);
            String nomeTime = times[0];
            mesa11to = new NnpeTO();
            mesa11to.setData(nomeTime);
            mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
            ret = enviarObjeto(mesa11to);
            mesa11to = (NnpeTO) ret;
            Time time = (Time) mesa11to.getData();
            if (Util.isNullOrEmpty(time.getImagem())) {
                uniforme.setIcon(
                        new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
                uniformeCpu.setIcon(
                        new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
            } else {
                ImageIcon icon = ImageUtil
                        .carregarImagem("times/" + time.getImagem());
                uniforme.setIcon(icon);
                uniformeCpu.setIcon(icon);
            }
            segundoUniforme = false;
            segundoUniformeCpu = false;
        }

        jComboBoxTimes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                    NnpeTO mesa11to = new NnpeTO();
                    mesa11to.setData(nomeTime);
                    mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                    Object ret = enviarObjeto(mesa11to);
                    mesa11to = (NnpeTO) ret;
                    Time time = (Time) mesa11to.getData();
                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniforme.setIcon(new ImageIcon(
                                BotaoUtils.desenhaUniforme(time, 1)));
                    } else {
                        ImageIcon icon = ImageUtil
                                .carregarImagem("times/" + time.getImagem());
                        uniforme.setIcon(icon);
                    }
                    segundoUniforme = false;
                }
            }
        });
        jComboBoxTimesCpu.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    String nomeTime = (String) jComboBoxTimesCpu
                            .getSelectedItem();
                    NnpeTO mesa11to = new NnpeTO();
                    mesa11to.setData(nomeTime);
                    mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                    Object ret = enviarObjeto(mesa11to);
                    mesa11to = (NnpeTO) ret;
                    Time time = (Time) mesa11to.getData();
                    if (Util.isNullOrEmpty(time.getImagem())) {
                        uniformeCpu.setIcon(new ImageIcon(
                                BotaoUtils.desenhaUniforme(time, 1)));
                    } else {
                        ImageIcon icon = ImageUtil
                                .carregarImagem("times/" + time.getImagem());
                        uniformeCpu.setIcon(icon);
                    }
                    segundoUniformeCpu = false;
                }
            }
        });

        uniforme.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniforme = !segundoUniforme;
                String nomeTime = (String) jComboBoxTimes.getSelectedItem();
                NnpeTO mesa11to = new NnpeTO();
                mesa11to.setData(nomeTime);
                mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                Object ret = enviarObjeto(mesa11to);
                mesa11to = (NnpeTO) ret;
                Time time = (Time) mesa11to.getData();
                if (Util.isNullOrEmpty(time.getImagem())) {
                    uniforme.setIcon(new ImageIcon(BotaoUtils
                            .desenhaUniforme(time, segundoUniforme ? 2 : 1)));
                }
            }
        });
        uniformeCpu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniformeCpu = !segundoUniformeCpu;
                String nomeTime = (String) jComboBoxTimesCpu.getSelectedItem();
                NnpeTO mesa11to = new NnpeTO();
                mesa11to.setData(nomeTime);
                mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
                Object ret = enviarObjeto(mesa11to);
                mesa11to = (NnpeTO) ret;
                Time time = (Time) mesa11to.getData();
                if (Util.isNullOrEmpty(time.getImagem())) {
                    uniformeCpu.setIcon(
                            new ImageIcon(BotaoUtils.desenhaUniforme(time,
                                    segundoUniformeCpu ? 2 : 1)));
                }
            }
        });

        JPanel uniformesPanel = new JPanel();
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("cliqueSegundoUniforme");
            }
        });

        uniformesPanel.add(uniforme);
        uniformesPanel.add(uniformeCpu);
        JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
        JPanel panelComboTimes1 = new JPanel();
        panelComboTimes1.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("escolhaTime");
            }
        });
        panelComboTimes1.add(jComboBoxTimes);
        JPanel panelComboTimes2 = new JPanel();
        panelComboTimes2.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("escolhaTimeCpu");
            }
        });

        panelComboTimes2.add(jComboBoxTimesCpu);
        panelComboTimes.add(panelComboTimes1);
        panelComboTimes.add(panelComboTimes2);
        JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
        escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
        escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

        JPanel opcoesJogoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });
        JComboBox numJogadaCombo = new JComboBox();
        for (int i = 3; i < 21; i++) {
            numJogadaCombo.addItem(new Integer(i));
        }
        numJogadaCombo.setSelectedIndex(4);
        opcoesJogoPanel.add(numJogadaCombo);

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });
        JComboBox tempoJogoCombo = new JComboBox();
        tempoJogoCombo.addItem(new Integer(8));
        tempoJogoCombo.addItem(new Integer(10));
        tempoJogoCombo.addItem(new Integer(16));
        tempoJogoCombo.addItem(new Integer(20));
        tempoJogoCombo.addItem(new Integer(40));
        tempoJogoCombo.addItem(new Integer(60));
        tempoJogoCombo.addItem(new Integer(90));
        opcoesJogoPanel.add(tempoJogoCombo);
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });
        JComboBox tempoJogadaCombo = new JComboBox();
        tempoJogadaCombo.addItem(new Integer(20));
        tempoJogadaCombo.addItem(new Integer(30));
        tempoJogadaCombo.addItem(new Integer(40));
        tempoJogadaCombo.addItem(new Integer(50));
        tempoJogadaCombo.addItem(new Integer(60));
        tempoJogadaCombo.addItem(new Integer(90));
        tempoJogadaCombo.setSelectedIndex(1);
        opcoesJogoPanel.add(tempoJogadaCombo);

        JComboBox campoBolaCombo = new JComboBox();
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
        campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("campoBola");
            }
        });
        opcoesJogoPanel.add(campoBolaCombo);

        JCheckBox autoMira = new JCheckBox();

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("autoMira");
            }
        });
        opcoesJogoPanel.add(autoMira);

        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

        while (jComboBoxTimesCpu.getItemCount() > 1
                && jComboBoxTimes.getSelectedItem()
                .equals(jComboBoxTimesCpu.getSelectedItem())) {
            jComboBoxTimesCpu.setSelectedIndex(
                    Util.intervalo(0, jComboBoxTimesCpu.getItemCount() - 1));
            Logger.logar("Selecionado Outro Time Baixo");
        }

        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                iniciarJogoPanel, Lang.msg("criarJogo"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            DadosJogoSrvMesa11 dadosJogoSrvMesa11 = new DadosJogoSrvMesa11();
            String jogador = controleChatCliente.getSessaoCliente()
                    .getNomeJogador();
            dadosJogoSrvMesa11.setNomeCriador(jogador);
            String nomeTime = (String) jComboBoxTimes.getSelectedItem();
            String nomeTimeCpu = (String) jComboBoxTimesCpu.getSelectedItem();
            if (nomeTime.equals(nomeTimeCpu)) {
                JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                        Lang.msg("timesIguais"));
                return;
            }
            dadosJogoSrvMesa11.setTimeCasa(nomeTime);
            dadosJogoSrvMesa11.setTimeVisita(nomeTimeCpu);
            dadosJogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
            dadosJogoSrvMesa11.setSegundoUniformeTimeVisita(segundoUniformeCpu);
            dadosJogoSrvMesa11
                    .setTempoJogo((Integer) tempoJogoCombo.getSelectedItem());
            dadosJogoSrvMesa11.setNumeroJogadas(
                    (Integer) numJogadaCombo.getSelectedItem());
            dadosJogoSrvMesa11.setTempoJogoJogada(
                    (Integer) tempoJogadaCombo.getSelectedItem());
            dadosJogoSrvMesa11.setBolaCampoCasa(
                    Lang.key((String) campoBolaCombo.getSelectedItem()));
            dadosJogoSrvMesa11.setAutoMira(autoMira.isSelected());
            if (ConstantesMesa11.BOLA
                    .equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
                if (Math.random() > 0.5)
                    dadosJogoSrvMesa11
                            .setBolaCampoVisita(ConstantesMesa11.CAMPO_CIMA);
                else
                    dadosJogoSrvMesa11
                            .setBolaCampoVisita(ConstantesMesa11.CAMPO_BAIXO);
            } else {
                dadosJogoSrvMesa11.setBolaCampoVisita(ConstantesMesa11.BOLA);
            }
            mesa11to = new NnpeTO();
            mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO_CPU);
            mesa11to.setData(dadosJogoSrvMesa11);
            ret = enviarObjeto(mesa11to);
            if (ret instanceof NnpeTO) {
                mesa11to = (NnpeTO) ret;
                dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
                monitorJogo = new MonitorJogo(controleChatCliente, this,
                        dadosJogoSrvMesa11, mesa11Applet,
                        dadosJogoSrvMesa11.getTimeCasa());
                monitorJogo.start();
            }
        }

    }

    public void criarJogoCampeonato(
            RodadaCampeonatoMesa11 rodadaCampeonatoMesa11,
            String nomeCampeonato) {
        if (monitorJogo != null && monitorJogo.isAlive()) {
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    Lang.msg("jaEstaEmUmJogo"));
            return;
        }
        final JLabel uniformeCasa = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        final JLabel uniformeVisita = new JLabel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
                        ConstantesMesa11.DIAMENTRO_BOTAO + 10);
            }
        };
        final Time timeCasa = rodadaCampeonatoMesa11.getTimeCasa();
        final Time timeVisita = rodadaCampeonatoMesa11.getTimeVisita();

        if (Util.isNullOrEmpty(timeCasa.getImagem())) {
            uniformeCasa.setIcon(
                    new ImageIcon(BotaoUtils.desenhaUniforme(timeCasa, 1)));
        } else {
            ImageIcon icon = ImageUtil
                    .carregarImagem("times/"
                            + timeCasa.getImagem());
            uniformeCasa.setIcon(icon);
        }

        if (Util.isNullOrEmpty(timeVisita.getImagem())) {
            uniformeVisita.setIcon(
                    new ImageIcon(BotaoUtils.desenhaUniforme(timeCasa, 1)));
        } else {
            ImageIcon icon = ImageUtil
                    .carregarImagem("times/"
                            + timeVisita.getImagem());
            uniformeVisita.setIcon(icon);
        }

        uniformeCasa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniforme = !segundoUniforme;
                if (Util.isNullOrEmpty(timeCasa.getImagem())) {
                    uniformeCasa.setIcon(
                            new ImageIcon(BotaoUtils.desenhaUniforme(timeCasa,
                                    segundoUniforme ? 2 : 1)));
                }
            }
        });
        uniformeVisita.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                segundoUniformeCpu = !segundoUniformeCpu;
                if (Util.isNullOrEmpty(timeVisita.getImagem())) {
                    uniformeVisita.setIcon(
                            new ImageIcon(BotaoUtils.desenhaUniforme(timeVisita,
                                    segundoUniformeCpu ? 2 : 1)));
                }
            }
        });

        JPanel uniformesPanel = new JPanel();
        uniformesPanel.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("cliqueSegundoUniforme");
            }
        });

        uniformesPanel.add(uniformeCasa);
        uniformesPanel.add(uniformeVisita);
        JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
        JPanel panelComboTimes1 = new JPanel();
        panelComboTimes1.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("timeCasa");
            }
        });
        panelComboTimes1.add(new JLabel() {
            @Override
            public String getText() {
                return timeCasa.getNome();
            }
        });
        JPanel panelComboTimes2 = new JPanel();
        panelComboTimes2.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("timeVisita");
            }
        });

        panelComboTimes2.add(new JLabel() {
            @Override
            public String getText() {
                return timeVisita.getNome();
            }
        });
        panelComboTimes.add(panelComboTimes1);
        panelComboTimes.add(panelComboTimes2);

        JPanel panelComboJogadores = new JPanel(new GridLayout(1, 2));
        JPanel panelComboJogadores1 = new JPanel();
        panelComboJogadores1.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("jogadorCasa");
            }
        });
        String jogadorCasa = "";
        if (rodadaCampeonatoMesa11.getJogadorCasa() != null) {
            jogadorCasa = rodadaCampeonatoMesa11.getJogadorCasa().getLogin();
        }

        panelComboJogadores1.add(new JLabel(
                Util.isNullOrEmpty(jogadorCasa) ? "CPU" : jogadorCasa));
        JPanel panelComboJogadores2 = new JPanel();
        panelComboJogadores2.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("jogadorVisita");
            }
        });
        String jogadorVisita = "";
        if (rodadaCampeonatoMesa11.getJogadorVisita() != null) {
            jogadorVisita = rodadaCampeonatoMesa11.getJogadorVisita()
                    .getLogin();
        }
        panelComboJogadores2.add(new JLabel(
                Util.isNullOrEmpty(jogadorVisita) ? "CPU" : jogadorVisita));
        panelComboJogadores.add(panelComboJogadores1);
        panelComboJogadores.add(panelComboJogadores2);

        JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
        escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
        escolhaTimesPanel.add(panelComboJogadores, BorderLayout.CENTER);
        escolhaTimesPanel.add(uniformesPanel, BorderLayout.SOUTH);

        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.DADOS_CAMPEONATO);
        mesa11to.setData(nomeCampeonato);
        Object ret = enviarObjeto(mesa11to);
        Object[] dadosCampeonato = (Object[]) ret;

        JPanel opcoesJogoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("numeroJogadas");
            }
        });
        opcoesJogoPanel.add(new JLabel(dadosCampeonato[2].toString()) {
            @Override
            public Color getForeground() {
                return Color.BLUE;
            }
        });

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogoMinutos");
            }
        });
        opcoesJogoPanel.add(new JLabel(dadosCampeonato[1].toString()) {
            @Override
            public Color getForeground() {
                return Color.BLUE;
            }
        });

        opcoesJogoPanel.add(new JLabel() {
            @Override
            public String getText() {
                return Lang.msg("tempoJogadaSegundos");
            }
        });
        opcoesJogoPanel.add(new JLabel(dadosCampeonato[3].toString()) {
            @Override
            public Color getForeground() {
                return Color.BLUE;
            }
        });

        JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
        iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
        iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                iniciarJogoPanel, Lang.msg("criarJogo"),
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            DadosJogoSrvMesa11 dadosJogoSrvMesa11 = new DadosJogoSrvMesa11();
            dadosJogoSrvMesa11.setTimeCasa(
                    rodadaCampeonatoMesa11.getTimeCasa().getNome());
            dadosJogoSrvMesa11.setTimeVisita(
                    rodadaCampeonatoMesa11.getTimeVisita().getNome());
            dadosJogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
            dadosJogoSrvMesa11.setSegundoUniformeTimeVisita(segundoUniformeCpu);
            dadosJogoSrvMesa11.setTempoJogo((Integer) dadosCampeonato[1]);
            dadosJogoSrvMesa11.setNumeroJogadas((Integer) dadosCampeonato[2]);
            dadosJogoSrvMesa11.setTempoJogoJogada((Integer) dadosCampeonato[3]);
            if (Math.random() > 0.5)
                dadosJogoSrvMesa11.setBolaCampoVisita(ConstantesMesa11.BOLA);
            else
                dadosJogoSrvMesa11.setBolaCampoCasa(ConstantesMesa11.BOLA);

            if (ConstantesMesa11.BOLA
                    .equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
                if (Math.random() > 0.5) {
                    dadosJogoSrvMesa11
                            .setBolaCampoVisita(ConstantesMesa11.CAMPO_CIMA);
                } else {
                    dadosJogoSrvMesa11
                            .setBolaCampoVisita(ConstantesMesa11.CAMPO_BAIXO);
                }
            }
            if (ConstantesMesa11.BOLA
                    .equals(dadosJogoSrvMesa11.getBolaCampoVisita())) {
                if (Math.random() > 0.5) {
                    dadosJogoSrvMesa11
                            .setBolaCampoCasa(ConstantesMesa11.CAMPO_CIMA);
                } else {
                    dadosJogoSrvMesa11
                            .setBolaCampoCasa(ConstantesMesa11.CAMPO_BAIXO);
                }
            }
            dadosJogoSrvMesa11
                    .setIdRodadaCampeonato(rodadaCampeonatoMesa11.getId());
            if (rodadaCampeonatoMesa11.getJogadorCasa() != null)
                dadosJogoSrvMesa11.setNomeCriador(
                        rodadaCampeonatoMesa11.getJogadorCasa().getLogin());
            if (rodadaCampeonatoMesa11.getJogadorVisita() != null)
                dadosJogoSrvMesa11.setNomeVisitante(
                        rodadaCampeonatoMesa11.getJogadorVisita().getLogin());
            mesa11to = new NnpeTO();
            mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO_CAMPEONATO);
            mesa11to.setSessaoCliente(controleChatCliente.getSessaoCliente());
            mesa11to.setData(dadosJogoSrvMesa11);
            ret = enviarObjeto(mesa11to);
            if (ret instanceof NnpeTO) {
                mesa11to = (NnpeTO) ret;
                dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
                String nomeJogador = controleChatCliente.getSessaoCliente()
                        .getNomeJogador();
                String timeJogador = "";
                if (rodadaCampeonatoMesa11.getJogadorCasa() != null
                        && nomeJogador.equals(rodadaCampeonatoMesa11
                        .getJogadorCasa().getLogin())) {
                    timeJogador = rodadaCampeonatoMesa11.getTimeCasa()
                            .getNome();
                }
                if (rodadaCampeonatoMesa11.getJogadorVisita() != null
                        && nomeJogador.equals(rodadaCampeonatoMesa11
                        .getJogadorVisita().getLogin())) {
                    timeJogador = rodadaCampeonatoMesa11.getTimeVisita()
                            .getNome();
                }
                monitorJogo = new MonitorJogo(controleChatCliente, this,
                        dadosJogoSrvMesa11, mesa11Applet, timeJogador);
                monitorJogo.start();
            }
        }

    }

    public String getVersao() {
        return mesa11Applet.getVersao();
    }

}
