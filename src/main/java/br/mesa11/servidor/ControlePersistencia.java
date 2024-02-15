package br.mesa11.servidor;

import br.hibernate.*;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Dia;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.recursos.Lang;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Paulo Sobreira Criado em 23/02/2010
 */
public class ControlePersistencia {

    public static Session getSession() {
        return HibernateUtil.getSession();
    }

    public ControlePersistencia() {
        super();
    }

    public static void main(String[] args) throws Exception {
        // ControlePersistencia controlePersistencia = new ControlePersistencia(
        // "d:" + File.separator);
        // controlePersistencia.paddockDadosSrv.getJogadoresMap().put("teste3",
        // "Paulo sobreira");
        // controlePersistencia.gravarDados();
        Dia dia = new Dia("01/06/2009");
        Dia hj = new Dia();
        Logger.logar(hj.daysBetween(dia));
    }

    public Object salvarTime(Time time) {
        if (Util.isNullOrEmpty(time.getNome())) {
            return new MsgSrv("timeSemNome");
        }
        if (Util.isNullOrEmpty(time.getNomeAbrev())) {
            return new MsgSrv("timeSemNomeAbrev");
        }
        if (time.getNome().length() > ConstantesMesa11.TAMANHO_MAX_NOME_TIME) {
            return new MsgSrv("nomeTimeMuitoGrande");
        }
        if (time.getNomeAbrev().length() > ConstantesMesa11.TAMANHO_MAX_NOME_ABREV_TIME) {
            return new MsgSrv("nomeAbreviadoTimeMuitoGrande");
        }
        List botoes = time.getBotoes();
        for (Iterator iterator = botoes.iterator(); iterator.hasNext(); ) {
            Botao botao = (Botao) iterator.next();
            if (Util.isNullOrEmpty(botao.getNome())) {
                botao.setNome(time.getNome());
            } else if (botao.getNome().length() > ConstantesMesa11.TAMANHO_MAX_NOME_TIME) {
                return new MsgSrv("nomeBotaoMuitoGrande");
            }
        }
        Session session = ControlePersistencia.getSession();

        List times = session.createCriteria(Time.class)
                .add(Restrictions.eq("nomeAbrev", time.getNomeAbrev())).list();
        if (time.getId() == null && !times.isEmpty()) {
            Time t = (Time) times.get(0);
            return new MsgSrv(Lang.msg("nomeJaEstaSendoUsado", new String[]{
                    time.getNomeAbrev(), t.getNome()}));
        }
        Transaction transaction = session.beginTransaction();
        try {
            if (time.getId() == null) {
                session.saveOrUpdate(time);
            } else {
                session.saveOrUpdate(time);
                for (Iterator iterator = time.getBotoes().iterator(); iterator
                        .hasNext(); ) {
                    Botao botao = (Botao) iterator.next();
                    session.saveOrUpdate(botao);
                }
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            Logger.logarExept(e);
            return new ErroServ(e.getMessage());
        } finally {
            session.close();
        }
        return new MsgSrv(Lang.msg("salvoComSucesso"));
    }

    public Object obterTimesJogador(String nomeJogador) {
        Session session = ControlePersistencia.getSession();
        try {
            List times = session.createCriteria(Time.class)
                    .add(Restrictions.eq("nomeJogador", nomeJogador)).list();
            String[] retorno = new String[times.size()];
            int i = 0;
            for (Iterator iterator = times.iterator(); iterator.hasNext(); ) {
                Time time = (Time) iterator.next();
                retorno[i] = time.getNome();
                i++;
            }
            NnpeTO mesa11to = new NnpeTO();
            mesa11to.setData(retorno);
            return mesa11to;
        } finally {
            session.close();
        }

    }

    public Time obterTime(String nome) {
        Session session = ControlePersistencia.getSession();
        try {
            Time time = (Time) session.createCriteria(Time.class)
                    .add(Restrictions.eq("nome", nome)).uniqueResult();
            time.setBotoes(Util.removePersistBag(time.getBotoes(), session));
            session.evict(time);
            return time;
        } finally {
            session.close();
        }

    }

    public Object obterTodosTimes() {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = "select obj.nome from Time obj";
            Query qry = session.createQuery(hql);
            List times = qry.list();
            if (times == null || times.isEmpty()) {
                return new ErroServ(Lang.msg("naoExisteTimes"));
            }
            String[] retorno = new String[times.size()];
            int i = 0;
            for (Iterator iterator = times.iterator(); iterator.hasNext(); ) {
                String nome = (String) iterator.next();
                retorno[i] = nome;
                i++;
            }
            NnpeTO mesa11to = new NnpeTO();
            mesa11to.setData(retorno);
            return mesa11to;
        } finally {
            session.close();
        }
    }

    public void gravarDados(Mesa11Dados... mesa11Dados) throws Exception {
        Session session = ControlePersistencia.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            for (int i = 0; i < mesa11Dados.length; i++) {
                session.saveOrUpdate(mesa11Dados[i]);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }

    public List obterPartidasTimeCasa(String timeCasa, String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            if (Util.isNullOrEmpty(campeonato)) {
                return session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("nomeTimeCasa", timeCasa)).list();
            } else {
                return session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("nomeTimeCasa", timeCasa))
                        .add(Restrictions.eq("campeonato", campeonato)).list();
            }
        } finally {
            session.close();
        }

    }

    public List obterPartidasTimeVisita(String timeVisita, String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            if (Util.isNullOrEmpty(campeonato)) {
                return session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("nomeTimeVisita", timeVisita)).list();
            } else {
                return session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("nomeTimeVisita", timeVisita))
                        .add(Restrictions.eq("campeonato", campeonato)).list();
            }
        } finally {
            session.close();
        }
    }

    public List obterTimes() {
        Session session = ControlePersistencia.getSession();
        try {
            return session.createCriteria(Time.class).list();
        } finally {
            session.close();
        }
    }

    public List obterJogadores() {
        Session session = ControlePersistencia.getSession();
        try {
            return session.createCriteria(Usuario.class).list();
        } finally {
            session.close();
        }
    }

    public List obterPartidasJogadorCasa(String login, String campeonato) {
        Session session = ControlePersistencia.getSession();
        if (Util.isNullOrEmpty(campeonato)) {
            return session.createCriteria(PartidaMesa11.class)
                    .add(Restrictions.eq("nomeJogadorCasa", login)).list();
        } else {
            return session.createCriteria(PartidaMesa11.class)
                    .add(Restrictions.eq("nomeJogadorCasa", login))
                    .add(Restrictions.eq("campeonato", campeonato)).list();
        }

    }

    public List obterPartidasJogadorVisita(String login, String campeonato) {
        Session session = ControlePersistencia.getSession();
        if (Util.isNullOrEmpty(campeonato)) {
            return session.createCriteria(PartidaMesa11.class)
                    .add(Restrictions.eq("nomeJogadorVisita", login)).list();
        } else {
            return session.createCriteria(PartidaMesa11.class)
                    .add(Restrictions.eq("nomeJogadorVisita", login))
                    .add(Restrictions.eq("campeonato", campeonato)).list();
        }

    }

    public Collection obterTimesPartidas(String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            Set partidas = new HashSet();
            List list = null;
            if (Util.isNullOrEmpty(campeonato)) {
                list = session.createCriteria(PartidaMesa11.class).list();
            } else {
                list = session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("campeonato", campeonato)).list();
            }
            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator.next();
                partidas.add(partidaMesa11.getNomeTimeCasa());
                partidas.add(partidaMesa11.getNomeTimeVisita());
            }
            return partidas;
        } finally {
            session.close();
        }
    }

    public Collection obterJogadoresPartidas(String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            Set jogadores = new HashSet();
            List list = null;
            if (Util.isNullOrEmpty(campeonato)) {
                list = session.createCriteria(PartidaMesa11.class).list();
            } else {
                list = session.createCriteria(PartidaMesa11.class)
                        .add(Restrictions.eq("campeonato", campeonato)).list();
            }
            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator.next();
                jogadores.add(partidaMesa11.getNomeJogadorCasa());
                jogadores.add(partidaMesa11.getNomeJogadorVisita());
            }
            return jogadores;
        } finally {
            session.close();
        }
    }

    public NnpeTO obterTodosJogadores() {
        Session session = ControlePersistencia.getSession();
        try {

            Dia dia = new Dia();
            dia.advance(-240);
            String hql = "select obj.login from Usuario obj where obj.ultimoLogon > "
                    + dia.toTimestamp().getTime() + " order by obj.login ";
            Query qry = session.createQuery(hql);
            List jogadores = qry.list();
            String[] retorno = new String[jogadores.size()];
            int i = 0;
            for (Iterator iterator = jogadores.iterator(); iterator.hasNext(); ) {
                String nome = (String) iterator.next();
                retorno[i] = nome;
                i++;
            }
            NnpeTO mesa11to = new NnpeTO();
            mesa11to.setData(retorno);
            return mesa11to;
        } finally {
            session.close();
        }
    }

    public Usuario obterJogadorPorLogin(String login) {
        Session session = ControlePersistencia.getSession();
        try {
            Usuario usuario = (Usuario) session.createCriteria(Usuario.class)
                    .add(Restrictions.eq("login", login)).uniqueResult();
            return usuario;
        } finally {
            session.close();
        }
    }

    public List<CampeonatoMesa11> listarCampeonatos() {
        Session session = ControlePersistencia.getSession();
        try {
            return session.createCriteria(CampeonatoMesa11.class)
                    .addOrder(Order.desc("dataCriacao")).list();
        } finally {
            session.close();
        }

    }

    public Object[] pesquisarDadosCampeonato(String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = "select obj.nome,obj.tempoJogo,obj.numeroJogadas,obj.tempoJogada from CampeonatoMesa11 obj where obj.nome = :campeonato";
            Query qry = session.createQuery(hql);
            qry.setParameter("campeonato", campeonato);
            List list = qry.list();
            if (!list.isEmpty()) {
                return (Object[]) list.get(0);
            }
            return new Object[]{"Sem Dados", 0, 0, 0};
        } finally {
            session.close();
        }
    }

    public CampeonatoMesa11 pesquisaCampeonato(String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            List list = session.createCriteria(CampeonatoMesa11.class)
                    .add(Restrictions.eq("nome", campeonato)).list();
            if (!list.isEmpty()) {
                return (CampeonatoMesa11) list.get(0);
            }
            return null;
        } finally {
            session.close();
        }
    }

    public List verRodada(CampeonatoMesa11 campeonatoMesa11) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = " from RodadaCampeonatoMesa11 obj where obj.rodada = :rodada and obj.campeonatoMesa11.nome = :campeonato ";
            Query qry = session.createQuery(hql);
            qry.setParameter("rodada", campeonatoMesa11.getNumeroRodadas());
            qry.setParameter("campeonato", campeonatoMesa11.getNome());
            List list = qry.list();
            return list;
        } finally {
            session.close();
        }
    }

    public List obterJogadoresCampeonato(String campeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = " from JogadoresCampeonatoMesa11 obj where obj.campeonatoMesa11.nome = :campeonato ";
            Query qry = session.createQuery(hql);
            qry.setParameter("campeonato", campeonato);
            List list = qry.list();
            return list;
        } finally {
            session.close();
        }
    }

    public RodadaCampeonatoMesa11 pesquisarRodadaPorId(long idRodadaCampeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = " from RodadaCampeonatoMesa11 obj where obj.id = :id  ";
            Query qry = session.createQuery(hql);
            qry.setParameter("id", idRodadaCampeonato);
            return (RodadaCampeonatoMesa11) qry.uniqueResult();
        } finally {
            session.close();
        }
    }

    public boolean verificaUsuarioCampeonato(String nomeJogador, long idRodada) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = "select obj.id from CampeonatoMesa11 obj inner join obj.jogadoresCampeonatoMesa11 jog  inner join  obj.rodadaCampeonatoMesa11 rod  where rod.id = :idRodada and jog.usuario.login = :nomeJogador ";
            Query qry = session.createQuery(hql);
            qry.setParameter("idRodada", idRodada);
            qry.setParameter("nomeJogador", nomeJogador);
            List list = qry.list();
            return !list.isEmpty();
        } finally {
            session.close();
        }
    }

    public boolean verificaRodadaFinalizada(long idRodadaCampeonato) {
        Session session = ControlePersistencia.getSession();
        try {
            String hql = "select obj.id from RodadaCampeonatoMesa11 obj where obj.rodadaEfetuda = :rodadaEfetuada and obj.id = :idRodadaCampeonato  ";
            Query qry = session.createQuery(hql);
            qry.setParameter("idRodadaCampeonato", idRodadaCampeonato);
            qry.setParameter("rodadaEfetuada", new Boolean(true));
            List list = qry.list();
            return !list.isEmpty();
        } finally {
            session.close();
        }
    }
}
