package br.mesa11.cliente;

import br.applet.Mesa11Applet;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.EditorTime;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.recursos.Lang;
import br.tos.ClassificacaoTime;
import br.tos.ClassificacaoUsuario;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ControleChatCliente {
    private FormLogin formLogin;
    private ChatWindow chatWindow;
    private Mesa11Applet mesa11Applet;
    private SessaoCliente sessaoCliente;
    private Thread threadAtualizadora;
    private boolean comunicacaoServer = true;
    private ControleJogosCliente controleJogosCliente;
    private ControleCampeonatoCliente controleCampeonato;

    public boolean isComunicacaoServer() {
        return comunicacaoServer;
    }

    public void setComunicacaoServer(boolean comunicacaoServer) {
        this.comunicacaoServer = comunicacaoServer;
    }

    public SessaoCliente getSessaoCliente() {
        return sessaoCliente;
    }

    public ControleChatCliente(Mesa11Applet mesa11Applet) {
        this.mesa11Applet = mesa11Applet;
        threadAtualizadora = new Thread(new Runnable() {

            public void run() {
                while (comunicacaoServer) {
                    try {
                        Thread.sleep(10000);
                        atualizaVisao();
                    } catch (Exception e) {
                        Logger.logarExept(e);
                    }
                }
            }
        });
        threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
        chatWindow = new ChatWindow(this);
        controleJogosCliente = new ControleJogosCliente(chatWindow, this,
                mesa11Applet);
        controleCampeonato = new ControleCampeonatoCliente(controleJogosCliente,
                this);
        atualizaVisao();
        mesa11Applet.getFrame().setLayout(new BorderLayout());
        mesa11Applet.getFrame().add(chatWindow.getMainPanel(),
                BorderLayout.CENTER);
        mesa11Applet.getFrame().setSize(800, 410);
        mesa11Applet.getFrame().pack();
        mesa11Applet.getFrame().setTitle(ConstantesMesa11.TITULO + getVersao());
        mesa11Applet.getFrame().setResizable(false);
        mesa11Applet.getFrame().setVisible(true);

        threadAtualizadora.start();
    }

    public ControleChatCliente(Mesa11Applet applet,
                               SessaoCliente sessaoCliente) {
        this(applet);
        this.sessaoCliente = sessaoCliente;
    }

    public void logar() {
        formLogin = new FormLogin(mesa11Applet);
        formLogin.setToolTipText(Lang.msg("formularioLogin"));
        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                formLogin, Lang.msg("formularioLogin"),
                JOptionPane.OK_CANCEL_OPTION);

        if (JOptionPane.OK_OPTION == result) {
            registrarUsuario();
            atualizaVisao();
            Logger.logar("formLogin.getNome().getText() "
                    + formLogin.getNome().getText());
        } else {
            logarGuest();
        }

    }

    public void logarGuest() {
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.GUEST_LOGIN_APPLET);
        Object ret = mesa11Applet.enviarObjeto(mesa11to);
        if (ret instanceof NnpeTO) {
            mesa11to = (NnpeTO) ret;
            this.sessaoCliente = (SessaoCliente) mesa11to.getData();
        }
        atualizaVisao();
    }


    private void atualizaVisao() {
        if (chatWindow == null) {
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.ATUALIZAR_VISAO);
        mesa11to.setSessaoCliente(sessaoCliente);
        Object ret = enviarObjeto(mesa11to);
        if (ret == null) {
            return;
        }
        mesa11to = (NnpeTO) ret;
        DadosMesa11 dadosMesa11 = (DadosMesa11) mesa11to.getData();
        chatWindow.atualizar(dadosMesa11);
        controleJogosCliente.setDadosMesa11(dadosMesa11);
    }

    public Object enviarObjeto(NnpeTO mesa11to) {
        if (mesa11Applet == null) {
            Logger.logar("enviarObjeto mesa11Applet null");
            return null;
        }
        return mesa11Applet.enviarObjeto(mesa11to);
    }

    private boolean registrarUsuario() {
        NnpeTO mesa11to = new NnpeTO();
        ClienteMesa11 clienteMesa11 = new ClienteMesa11();
        mesa11to.setData(clienteMesa11);
        clienteMesa11.setNomeJogador(formLogin.getNome().getText());

        try {
            if (!Util.isNullOrEmpty(
                    new String(formLogin.getSenha().getPassword()))) {
                clienteMesa11.setSenhaJogador(Util
                        .md5(new String(formLogin.getSenha().getPassword())));
            }
        } catch (Exception e) {
            Logger.logarExept(e);
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        clienteMesa11.setEmailJogador(formLogin.getEmailRegistrar().getText());

        if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
                && !Util.isNullOrEmpty(clienteMesa11.getSenhaJogador())) {
            mesa11to.setComando(ConstantesMesa11.LOGAR);
        } else if (!Util.isNullOrEmpty(formLogin.getNomeRegistrar().getText())
                || !Util.isNullOrEmpty(
                formLogin.getEmailRegistrar().getText())) {
            int resultado = 0;
            try {
                resultado = Integer
                        .parseInt(formLogin.getResultadorConta().getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mesa11Applet.getFrame(),
                        Lang.msg("resultadoContaErrado"), Lang.msg("erro"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if ((formLogin.getConta1() + formLogin.getConta2()) != resultado) {
                JOptionPane.showMessageDialog(mesa11Applet.getFrame(),
                        Lang.msg("resultadoContaErrado"), Lang.msg("erro"),
                        JOptionPane.ERROR_MESSAGE);
                return false;

            }
            mesa11to.setComando(ConstantesMesa11.NOVO_USUARIO);
        }
        Logger.logar("registrarUsuario mesa11to.getComando() "
                + mesa11to.getComando());
        Object ret = mesa11Applet.enviarObjeto(mesa11to);
        if (ret == null) {
            return false;
        }
        if (ret instanceof NnpeTO) {
            mesa11to = (NnpeTO) ret;
            this.sessaoCliente = (SessaoCliente) mesa11to.getData();
            if (sessaoCliente.getSenhaCriada() != null) {
                chatWindow.getTextAreaChat().append(Lang.msg("guardeSenhaGerada") + " - " + Lang.msg("senhaGerada", new String[]{sessaoCliente.getNomeJogador(), sessaoCliente.getSenhaCriada()}));
            }
        }
        return true;
    }

    public void setChatWindow(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;

    }

    public ChatWindow getChatWindow() {
        return chatWindow;
    }

    private boolean retornoNaoValido(Object ret) {
        if (ret instanceof ErroServ || ret instanceof MsgSrv) {
            return true;
        }
        return false;
    }

    public void enviarTexto(String text) {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        ClienteMesa11 clienteMesa11 = new ClienteMesa11(sessaoCliente);
        clienteMesa11.setTexto(text);
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setData(clienteMesa11);
        mesa11to.setComando(ConstantesMesa11.ENVIAR_TEXTO);
        Object ret = enviarObjeto(mesa11to);
        if (retornoNaoValido(ret)) {
            return;
        }
        if (ret == null) {
            JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
                    Lang.msg("problemasRede"), "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        mesa11to = (NnpeTO) ret;
        chatWindow.atualizar((DadosMesa11) mesa11to.getData());
    }

    public void criarJogo() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        controleJogosCliente.criarJogo();

    }

    public void entarJogo() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        String jogoSelecionado = chatWindow.obterJogoSelecionado();
        if (jogoSelecionado == null) {
            return;
        }
        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                Lang.msg("entrarJogo") + jogoSelecionado,
                Lang.msg("entrarJogo"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controleJogosCliente.entrarJogo(jogoSelecionado);
        }

    }

    public void verDetalhesJogo() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        String jogoSelecionado = chatWindow.obterJogoSelecionado();
        if (jogoSelecionado == null) {
            return;
        }
        int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                Lang.msg("verDetalhesJogo") + " " + jogoSelecionado,
                Lang.msg("verDetalhesJogo"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controleJogosCliente.verDetalhesJogo(jogoSelecionado);
        }

    }

    public void verDetalhesJogador() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
    }

    public void criarTime() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        Time time = new Time();
        time.setLoginCriador(sessaoCliente.getNomeJogador());
        time.setNomeJogador(sessaoCliente.getNomeJogador());
        time.setQtdePontos(0);
        time.setCor1RGB(Util.criarCorAleatoria());
        time.setCor2RGB(Util.criarCorAleatoria());
        time.setCor3RGB(Util.criarCorAleatoria());
        time.setCor4RGB(Util.criarCorAleatoria());
        time.setCor5RGB(Util.criarCorAleatoria());
        time.setCor6RGB(Util.criarCorAleatoria());
        Goleiro goleiro = new Goleiro();
        goleiro.setForca(500);
        goleiro.setPrecisao(500);
        goleiro.setDefesa(500);
        goleiro.setGoleiro(true);
        goleiro.setTitular(true);
        goleiro.setTime(time);
        goleiro.setNumero(1);
        goleiro.setLoginCriador(sessaoCliente.getNomeJogador());
        time.getBotoes().add(goleiro);
        for (int i = 0; i < 10; i++) {
            Botao botao = new Botao();
            botao.setForca(500);
            botao.setPrecisao(500);
            botao.setDefesa(500);
            botao.setGoleiro(false);
            botao.setTitular(false);
            botao.setTime(time);
            botao.setNumero(i + 2);
            botao.setLoginCriador(sessaoCliente.getNomeJogador());
            time.getBotoes().add(botao);
        }
        ControleJogo controleJogo = new ControleJogo(mesa11Applet, null, null,
                null);
        EditorTime editorTime = new EditorTime(time, controleJogo);
        int ret = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
                editorTime, Lang.msg("criarTime"), JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            controleJogo.salvarTime(time);
        }

    }

    public void verClassificacao() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.VER_CLASSIFICACAO);
        Object ret = enviarObjeto(mesa11to);
        if (!(ret instanceof NnpeTO)) {
            return;
        }
        mesa11to = (NnpeTO) ret;
        Map data = (Map) mesa11to.getData();
        List dadosTimes = (List) data
                .get(ConstantesMesa11.VER_CLASSIFICACAO_TIMES);
        List dadosJogadores = (List) data
                .get(ConstantesMesa11.VER_CLASSIFICACAO_JOGADORES);
        JPanel classificacaoPanel = gerarPanelClassificacao(dadosTimes,
                dadosJogadores);
        JOptionPane.showMessageDialog(this.mesa11Applet.getFrame(),
                classificacaoPanel, Lang.msg("classificacao"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public JPanel gerarPanelClassificacao(final List dadosTimes,
                                          final List dadosJogadores) {
        final JTable timesTable = new JTable();

        final TableModel timesTableModel = new AbstractTableModel() {

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                ClassificacaoTime value = (ClassificacaoTime) dadosTimes
                        .get(rowIndex);

                switch (columnIndex) {
                    case 0:
                        return value.getTime();
                    case 1:
                        return value.getJogos();
                    case 2:
                        return value.getVitorias();
                    case 3:
                        return value.getEmpates();
                    case 4:
                        return value.getDerrotas();
                    case 5:
                        return value.getSaldoGols();
                    case 6:
                        return value.getGolsFavor();
                    case 7:
                        return value.getGolsContra();
                    default:
                        return "";
                }
            }

            @Override
            public int getRowCount() {
                if (dadosTimes == null) {
                    return 0;
                }
                return dadosTimes.size();
            }

            @Override
            public int getColumnCount() {
                return 8;
            }

            @Override
            public String getColumnName(int columnIndex) {

                switch (columnIndex) {
                    case 0:
                        return Lang.msg("time");
                    case 1:
                        return Lang.msg("jogos");
                    case 2:
                        return Lang.msg("vitorias");
                    case 3:
                        return Lang.msg("empates");
                    case 4:
                        return Lang.msg("derrotas");
                    case 5:
                        return Lang.msg("saldogols");
                    case 6:
                        return Lang.msg("golsfavor");
                    case 7:
                        return Lang.msg("golscontra");
                    default:
                        return "";
                }

            }
        };

        timesTable.setModel(timesTableModel);
        JScrollPane timesJs = new JScrollPane(timesTable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(620, 150);
            }
        };
        final JTable jogadoresTable = new JTable();

        final TableModel jogadoresTableModel = new AbstractTableModel() {

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                ClassificacaoUsuario value = (ClassificacaoUsuario) dadosJogadores
                        .get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return value.getLogin();
                    case 1:
                        return value.getJogos();
                    case 2:
                        return value.getVitorias();
                    case 3:
                        return value.getEmpates();
                    case 4:
                        return value.getDerrotas();
                    case 5:
                        return value.getSaldoGols();
                    case 6:
                        return value.getGolsFavor();
                    case 7:
                        return value.getGolsContra();
                    default:
                        return "";
                }
            }

            @Override
            public int getRowCount() {
                if (dadosJogadores == null) {
                    return 0;
                }
                return dadosJogadores.size();
            }

            @Override
            public int getColumnCount() {
                return 8;
            }

            @Override
            public String getColumnName(int columnIndex) {

                switch (columnIndex) {
                    case 0:
                        return Lang.msg("jogador");
                    case 1:
                        return Lang.msg("jogos");
                    case 2:
                        return Lang.msg("vitorias");
                    case 3:
                        return Lang.msg("empates");
                    case 4:
                        return Lang.msg("derrotas");
                    case 5:
                        return Lang.msg("saldogols");
                    case 6:
                        return Lang.msg("golsfavor");
                    case 7:
                        return Lang.msg("golscontra");
                    default:
                        return "";
                }

            }
        };

        jogadoresTable.setModel(jogadoresTableModel);
        JScrollPane jogadoresJs = new JScrollPane(jogadoresTable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(620, 150);
            }
        };

        JPanel jPanelTimes = new JPanel();
        jPanelTimes.setBorder(new TitledBorder("rankingTimes") {
            @Override
            public String getTitle() {
                return Lang.msg("rankingTimes");
            }
        });
        jPanelTimes.add(timesJs);

        JPanel jPanelJogs = new JPanel();
        jPanelJogs.setBorder(new TitledBorder("rankingJogadores") {
            @Override
            public String getTitle() {
                return Lang.msg("rankingJogadores");
            }
        });
        jPanelJogs.add(jogadoresJs);

        JPanel jPanel = new JPanel(new GridLayout(2, 1));
        jPanel.add(jPanelTimes);
        jPanel.add(jPanelJogs);
        return jPanel;
    }

    public int getLatenciaMinima() {
        return mesa11Applet.getLatenciaMinima();
    }

    public int getLatenciaReal() {
        return mesa11Applet.getLatenciaReal();
    }

    public void editarTime() {
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.OBTER_LISTA_TIMES_JOGADOR);
        mesa11to.setData(sessaoCliente.getNomeJogador());
        Object ret = enviarObjeto(mesa11to);
        JComboBox jComboBoxTimes = new JComboBox(
                new String[]{Lang.msg("semTimes")});
        boolean semTimes = true;
        if (ret instanceof NnpeTO) {
            mesa11to = (NnpeTO) ret;
            String[] times = (String[]) mesa11to.getData();
            jComboBoxTimes = new JComboBox(times);
            semTimes = false;
        }
        JPanel panelTimes = new JPanel();
        panelTimes.setBorder(new TitledBorder("") {
            @Override
            public String getTitle() {
                return Lang.msg("escolhaTime");
            }
        });
        panelTimes.add(jComboBoxTimes);
        int showConfirmDialog = JOptionPane.showConfirmDialog(
                chatWindow.getMainPanel(), panelTimes, Lang.msg("editarTime"),
                JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION != showConfirmDialog) {
            return;
        }
        if (!semTimes) {
            String timeSelecionado = (String) jComboBoxTimes.getSelectedItem();
            Logger.logar("timeSelecionado " + timeSelecionado);
            mesa11to = new NnpeTO();
            mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
            mesa11to.setData(timeSelecionado);
            ret = enviarObjeto(mesa11to);
            if (ret instanceof NnpeTO) {
                mesa11to = (NnpeTO) ret;
                Time time = (Time) mesa11to.getData();
                ControleJogo controleJogo = new ControleJogo(mesa11Applet, null,
                        null, null);
                EditorTime editorTime = new EditorTime(time, controleJogo);
                int retOpt = JOptionPane.showConfirmDialog(
                        chatWindow.getMainPanel(), editorTime,
                        Lang.msg("editarTime"), JOptionPane.YES_NO_OPTION);
                if (retOpt == JOptionPane.YES_OPTION) {
                    controleJogo.salvarTime(time);
                }

            }
        }

    }

    public void sairJogo() {
        NnpeTO mesa11to = new NnpeTO();
        mesa11to.setComando(ConstantesMesa11.SAIR_JOGO);
        mesa11to.setData(sessaoCliente.getNomeJogador());
        Object ret = enviarObjeto(mesa11to);
        if (controleJogosCliente != null) {
            controleJogosCliente.sairJogo();
        }
    }

    public void atualizaInfo() {
        if (chatWindow != null)
            chatWindow.atualizaInfo();

    }

    public void criarJogoVsCPU() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        controleJogosCliente.criarJogoVsCPU();

    }

    public String getVersao() {
        return mesa11Applet.getVersao();
    }

    public void criarCampeonato() {
        if (sessaoCliente == null) {
            logar();
            return;
        }
        controleCampeonato.criarCampeonato();
    }

    public void verCampeonato() {
        controleCampeonato.verCampeonatos();
    }
}
