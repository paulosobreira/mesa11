package br.mesa11.servidor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.hibernate.Botao;
import br.hibernate.CampeonatoMesa11;
import br.hibernate.Mesa11Dados;
import br.hibernate.PartidaMesa11;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Dia;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ErroServ;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;

/**
 * @author Paulo Sobreira Criado em 23/02/2010
 */
public class ControlePersistencia {
	private String webInfDir;

	private String webDir;

	public static Session getSession() {
		Session session = HibernateUtil.currentSession();
		try {
			List jogador = session.createCriteria(Usuario.class).add(
					Restrictions.eq("id", new Long(0))).list();
		} catch (Exception e) {
			Logger.logarExept(e);
			HibernateUtil.closeSession();
			Logger.novaSession = true;
			session = HibernateUtil.currentSession();
		}
		return session;
	}

	public ControlePersistencia(String webDir, String webInfDir) {
		super();
		this.webInfDir = webInfDir;
		this.webDir = webDir;
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
		Logger.logar("ALEMANHA".length());
	}

	public byte[] obterBytesBase() {
		try {
			File file = new File(webInfDir + "hipersonic.tar.gz");
			if (file != null) {
				file.delete();
			}
			Connection connection = ControlePersistencia.getSession()
					.connection();
			String sql = "BACKUP DATABASE TO '" + webInfDir
					+ "hipersonic.tar.gz' BLOCKING";

			connection.createStatement().executeUpdate(sql);
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					new FileOutputStream(webInfDir + "algolbkp.zip"));

			ByteArrayOutputStream hsByteArrayOutputStream = new ByteArrayOutputStream();
			FileInputStream fileInputStream = new FileInputStream(webInfDir
					+ "hipersonic.tar.gz");
			int byt = fileInputStream.read();

			while (-1 != byt) {
				hsByteArrayOutputStream.write(byt);
				byt = fileInputStream.read();
			}

			ZipEntry entry = new ZipEntry("hipersonic.tar.gz");
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write(hsByteArrayOutputStream.toByteArray());

			zipDir(webDir + "midia", zipOutputStream);

			zipOutputStream.close();

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(webInfDir + "algolbkp.zip"));
			byt = bufferedInputStream.read();

			while (-1 != byt) {
				arrayOutputStream.write(byt);
				byt = bufferedInputStream.read();
			}

			arrayOutputStream.flush();

			return arrayOutputStream.toByteArray();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return null;
	}

	public void zipDir(String dir2zip, ZipOutputStream zos) {
		try {
			// create a new File object based on the directory we
			// have to zip File
			File zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDir(filePath, zos);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not
				// a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getAbsolutePath().split(
						"algol-rpg" + File.separator + File.separator)[1]);
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
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
		for (Iterator iterator = botoes.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (Util.isNullOrEmpty(botao.getNome())) {
				botao.setNome(time.getNomeAbrev());
			} else if (botao.getNome().length() > ConstantesMesa11.TAMANHO_MAX_NOME_TIME) {
				return new MsgSrv("nomeBotaoMuitoGrande");
			}
		}
		Session session = ControlePersistencia.getSession();
		Transaction transaction = session.beginTransaction();
		try {
			if (time.getId() == null) {
				session.saveOrUpdate(time);
			} else {
				session.saveOrUpdate(time);
				for (Iterator iterator = time.getBotoes().iterator(); iterator
						.hasNext();) {
					Botao botao = (Botao) iterator.next();
					session.saveOrUpdate(botao);
				}
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			return new ErroServ(e.getMessage());
		}
		return new MsgSrv(Lang.msg("salvoComSucesso"));
	}

	public Object obterTimesJogador(String nomeJogador) {
		Session session = ControlePersistencia.getSession();
		try {
			List times = session.createCriteria(Time.class).add(
					Restrictions.eq("nomeJogador", nomeJogador)).list();
			String[] retorno = new String[times.size()];
			int i = 0;
			for (Iterator iterator = times.iterator(); iterator.hasNext();) {
				Time time = (Time) iterator.next();
				retorno[i] = time.getNome();
				i++;
			}
			Mesa11TO mesa11to = new Mesa11TO();
			mesa11to.setData(retorno);
			return mesa11to;
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public Time obterTime(String nome) {
		Session session = ControlePersistencia.getSession();
		try {
			Time time = (Time) session.createCriteria(Time.class).add(
					Restrictions.eq("nome", nome)).uniqueResult();
			time.setBotoes(Util.removePersistBag(time.getBotoes(), session));
			session.evict(time);
			return time;
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public Object obterTodosTimes() {
		Session session = ControlePersistencia.getSession();
		String hql = "select obj.nome from Time obj";
		Query qry = session.createQuery(hql);
		List times = qry.list();
		String[] retorno = new String[times.size()];
		int i = 0;
		for (Iterator iterator = times.iterator(); iterator.hasNext();) {
			String nome = (String) iterator.next();
			retorno[i] = nome;
			i++;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(retorno);
		return mesa11to;
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
		}

	}

	public List obterPartidasTimeCasa(String timeCasa, String campeonato) {
		Session session = ControlePersistencia.getSession();
		if (Util.isNullOrEmpty(campeonato)) {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeTimeCasa", timeCasa)).list();
		} else {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeTimeCasa", timeCasa)).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}
	}

	public List obterPartidasTimeVisita(String timeVisita, String campeonato) {
		Session session = ControlePersistencia.getSession();
		if (Util.isNullOrEmpty(campeonato)) {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeTimeVisita", timeVisita)).list();
		} else {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeTimeVisita", timeVisita)).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}

	}

	public List obterTimes() {
		Session session = ControlePersistencia.getSession();
		return session.createCriteria(Time.class).list();
	}

	public List obterJogadores() {
		Session session = ControlePersistencia.getSession();
		return session.createCriteria(Usuario.class).list();
	}

	public List obterPartidasJogadorCasa(String login, String campeonato) {
		Session session = ControlePersistencia.getSession();
		if (Util.isNullOrEmpty(campeonato)) {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeJogadorCasa", login)).list();
		} else {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeJogadorCasa", login)).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}

	}

	public List obterPartidasJogadorVisita(String login, String campeonato) {
		Session session = ControlePersistencia.getSession();
		if (Util.isNullOrEmpty(campeonato)) {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeJogadorVisita", login)).list();
		} else {
			return session.createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("nomeJogadorVisita", login)).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}

	}

	public Collection obterTimesPartidas(String campeonato) {
		Set partidas = new HashSet();
		List list = null;
		if (Util.isNullOrEmpty(campeonato)) {
			list = getSession().createCriteria(PartidaMesa11.class).list();
		} else {
			list = getSession().createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator.next();
			partidas.add(partidaMesa11.getNomeTimeCasa());
			partidas.add(partidaMesa11.getNomeTimeVisita());
		}
		return partidas;
	}

	public Collection obterJogadoresPartidas(String campeonato) {
		Set jogadores = new HashSet();
		List list = null;
		if (Util.isNullOrEmpty(campeonato)) {
			list = getSession().createCriteria(PartidaMesa11.class).list();
		} else {
			list = getSession().createCriteria(PartidaMesa11.class).add(
					Restrictions.eq("campeonato", campeonato)).list();
		}
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator.next();
			jogadores.add(partidaMesa11.getNomeJogadorCasa());
			jogadores.add(partidaMesa11.getNomeJogadorVisita());
		}
		return jogadores;
	}

	public Mesa11TO obterTodosJogadores() {
		Dia dia = new Dia();
		dia.advance(-240);
		Session session = ControlePersistencia.getSession();
		String hql = "select obj.login from Usuario obj where obj.ultimoLogon > "
				+ dia.toTimestamp().getTime() + " order by obj.login ";
		Query qry = session.createQuery(hql);
		List jogadores = qry.list();
		String[] retorno = new String[jogadores.size()];
		int i = 0;
		for (Iterator iterator = jogadores.iterator(); iterator.hasNext();) {
			String nome = (String) iterator.next();
			retorno[i] = nome;
			i++;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(retorno);
		return mesa11to;
	}

	public Usuario obterJogadorPorLogin(String login) {
		Session session = ControlePersistencia.getSession();
		Usuario usuario = (Usuario) session.createCriteria(Usuario.class).add(
				Restrictions.eq("login", login)).uniqueResult();
		return usuario;
	}

	public List<CampeonatoMesa11> listarCampeonatos() {
		Session session = ControlePersistencia.getSession();
		return session.createCriteria(CampeonatoMesa11.class).addOrder(
				Order.desc("dataCriacao")).list();

	}

	public Object[] pesquisarDadosCampeonato(String campeonato) {
		Session session = ControlePersistencia.getSession();
		String hql = "select obj.nome,obj.tempoJogo,obj.numeroJogadas,obj.tempoJogada from CampeonatoMesa11 obj where obj.nome = :campeonato";
		Query qry = session.createQuery(hql);
		qry.setParameter("campeonato", campeonato);
		List list = qry.list();
		if (!list.isEmpty()) {
			return (Object[]) list.get(0);
		}
		return new Object[] { "Sem Dados", 0, 0, 0 };
	}

	public CampeonatoMesa11 pesquisaCampeonato(String campeonato) {
		Session session = ControlePersistencia.getSession();
		List list = session.createCriteria(CampeonatoMesa11.class).add(
				Restrictions.eq("nome", campeonato)).list();
		if (!list.isEmpty()) {
			return (CampeonatoMesa11) list.get(0);
		}
		return null;
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
			HibernateUtil.closeSession();
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
			HibernateUtil.closeSession();
		}
	}

	public RodadaCampeonatoMesa11 pesquisarRodadaPorId(long idRodadaCampeonato) {
		Session session = ControlePersistencia.getSession();
		String hql = " from RodadaCampeonatoMesa11 obj where obj.id = :id  ";
		Query qry = session.createQuery(hql);
		qry.setParameter("id", idRodadaCampeonato);
		return (RodadaCampeonatoMesa11) qry.uniqueResult();
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
			HibernateUtil.closeSession();
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
			HibernateUtil.closeSession();
		}
	}
}
