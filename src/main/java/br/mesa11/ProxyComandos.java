package br.mesa11;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.Mesa11Dados;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.Usuario;
import br.mesa11.servidor.ControleCampeonatoServidor;
import br.mesa11.servidor.ControleChatServidor;
import br.mesa11.servidor.ControleJogosServidor;
import br.mesa11.servidor.ControleLogin;
import br.mesa11.servidor.ControlePersistencia;
import br.mesa11.servidor.MonitorAtividade;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.tos.ClienteMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;
import br.tos.JogadaMesa11;

public class ProxyComandos {
    private ControleLogin controleLogin;
    private ControlePersistencia controlePersistencia;
    private ControleChatServidor controleChatServidor;
    private ControleJogosServidor controleJogosServidor;
    private ControleCampeonatoServidor controleCampeonatoServidor;
    private DadosMesa11 dadosMesa11;
    private MonitorAtividade monitorAtividade;

    public ProxyComandos(String webDir, String webInfDir) {
        dadosMesa11 = new DadosMesa11();
        controleLogin = new ControleLogin(dadosMesa11);
        controleChatServidor = new ControleChatServidor(dadosMesa11);
        controlePersistencia = new ControlePersistencia(webDir, webInfDir);
        controleJogosServidor = new ControleJogosServidor(dadosMesa11,
                controlePersistencia, this);
        controleCampeonatoServidor = new ControleCampeonatoServidor(dadosMesa11,
                controlePersistencia, this, controleJogosServidor);
        monitorAtividade = new MonitorAtividade(this);
        monitorAtividade.start();
    }

    public Object processarObjeto(Object object) {
        NnpeTO mesa11TO = (NnpeTO) object;
        if (ConstantesMesa11.ATUALIZAR_VISAO.equals(mesa11TO.getComando())) {
            return atualizarDadosVisao(mesa11TO);
        } else if (ConstantesMesa11.VERIFICA_POSICAO_DIFF_BOTOES
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.verificaPosicaoDiffBotoes(
                    ((String) mesa11TO.getData()).split("-"));
        } else if (ConstantesMesa11.OBTER_DADOS_JOGO
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.obterDadosJogo(mesa11TO);
        } else if (ConstantesMesa11.JOGADA.equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .jogada((JogadaMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.JOGADA_ASSITIDA
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .jogadaAssitida((JogadaMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.SAIR_JOGO.equals(mesa11TO.getComando())) {
            return controleJogosServidor.sairJogo((String) mesa11TO.getData());
        } else if (ConstantesMesa11.LOGAR.equals(mesa11TO.getComando())) {
            return controleLogin.logar((ClienteMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.NOVO_USUARIO
                .equals(mesa11TO.getComando())) {
            return controleLogin
                    .cadastrarUsuario((ClienteMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.ENVIAR_TEXTO
                .equals(mesa11TO.getComando())) {
            return controleChatServidor
                    .receberTexto((ClienteMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.SALVAR_TIME.equals(mesa11TO.getComando())) {
            Time time = (Time) mesa11TO.getData();
            if (controleJogosServidor.verificaJogoEmAndamento(time)) {
                return new MsgSrv("salvarTimeComjogoEmAndamento");
            }
            return controlePersistencia.salvarTime(time);
        } else if (ConstantesMesa11.OBTER_LISTA_TIMES_JOGADOR
                .equals(mesa11TO.getComando())) {
            return controlePersistencia
                    .obterTimesJogador((String) mesa11TO.getData());
        } else if (ConstantesMesa11.OBTER_TIME.equals(mesa11TO.getComando())) {
            Time time = controlePersistencia
                    .obterTime((String) mesa11TO.getData());
            NnpeTO mesa11to = new NnpeTO();
            mesa11to.setData(time);
            return mesa11to;
        } else if (ConstantesMesa11.OBTER_TODOS_TIMES
                .equals(mesa11TO.getComando())) {
            return controlePersistencia.obterTodosTimes();
        } else if (ConstantesMesa11.OBTER_TODOS_JOGADORES
                .equals(mesa11TO.getComando())) {
            return controlePersistencia.obterTodosJogadores();
        } else if (ConstantesMesa11.CRIAR_JOGO.equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .criarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.CRIAR_CAMPEONATO
                .equals(mesa11TO.getComando())) {
            return controleCampeonatoServidor.criarCampeonato(mesa11TO);
        } else if (ConstantesMesa11.LISTAR_CAMPEONATOS
                .equals(mesa11TO.getComando())) {
            return controleCampeonatoServidor.listarCampeonatos();
        } else if (ConstantesMesa11.CRIAR_JOGO_CPU
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .criarJogoCpu((DadosJogoSrvMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.CRIAR_JOGO_CAMPEONATO
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.criarJogoCampeonato(mesa11TO);
        } else if (ConstantesMesa11.ENTRAR_JOGO.equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .entrarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.ENVIAR_IMAGEM
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.gravarImagem(mesa11TO);
        } else if (ConstantesMesa11.OBTER_TODAS_IMAGENS
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.obterTodasImagens();
        } else if (ConstantesMesa11.OBTER_JOGADORES_CAMPEONATO
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .obterJogadoresCampeonato((String) mesa11TO.getData());
        } else if (ConstantesMesa11.VER_RODADA.equals(mesa11TO.getComando())) {
            return controleJogosServidor
                    .verRodada((CampeonatoMesa11) mesa11TO.getData());
        } else if (ConstantesMesa11.VER_CLASSIFICACAO
                .equals(mesa11TO.getComando())) {
            return controleJogosServidor.obterClassificacao();
        } else if (ConstantesMesa11.VER_CAMPEONATO
                .equals(mesa11TO.getComando())) {
            return controleCampeonatoServidor
                    .verCampeonato((String) mesa11TO.getData());

        } else if (ConstantesMesa11.DADOS_CAMPEONATO
                .equals(mesa11TO.getComando())) {
            return controleCampeonatoServidor
                    .dadosCampeonato((String) mesa11TO.getData());
        }

        return null;
    }

    private Object atualizarDadosVisao(NnpeTO mesa11to) {
        if (mesa11to.getSessaoCliente() != null) {
            controleChatServidor
                    .atualizaSessaoCliente(mesa11to.getSessaoCliente());
        }
        mesa11to.setData(dadosMesa11);
        return mesa11to;
    }

    public DadosMesa11 getDadosMesa11() {
        return dadosMesa11;
    }

    public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
        Logger.logar("removerClienteInativo " + sessaoClienteRemover);
        controleJogosServidor.removerClienteInativo(sessaoClienteRemover);
        dadosMesa11.getClientes().remove(sessaoClienteRemover);
    }

    public ControleJogosServidor getControleJogosServidor() {
        return controleJogosServidor;
    }

    public boolean verificaSemSessao(String nomeCriador) {
        return controleLogin.verificaSemSessao(nomeCriador);
    }

    public void gravarDados(Mesa11Dados... mesa11Dados) throws Exception {
        controlePersistencia.gravarDados(mesa11Dados);
    }

    public RodadaCampeonatoMesa11 pesquisarRodadaPorId(
            long idRodadaCampeonato) {
        return controlePersistencia.pesquisarRodadaPorId(idRodadaCampeonato);
    }

    public Usuario pesquisarUsuarioPorLogin(String login) {
        return controlePersistencia.obterJogadorPorLogin(login);
    }

}
