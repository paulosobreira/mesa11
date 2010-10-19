package br.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import br.hibernate.HibernateUtil;
import br.mesa11.ConstantesMesa11;
import br.mesa11.ProxyComandos;
import br.nnpe.Email;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.ZipUtil;
import br.recursos.Lang;

/**
 * @author paulo.sobreira
 * 
 */
public class ServletMesa11 extends HttpServlet {

	public static String webInfDir;

	public static String webDir;
	private ProxyComandos proxyComandos;
	public static Email email;

	public void init() throws ServletException {
		super.init();
		webDir = getServletContext().getRealPath("") + File.separator;
		webInfDir = webDir + "WEB-INF" + File.separator;
		proxyComandos = new ProxyComandos(webDir, webInfDir);
		Lang.setSrvgame(true);
		try {
			email = new Email(getServletContext().getRealPath("")
					+ File.separator + "WEB-INF" + File.separator);
		} catch (Exception e) {
			Logger.logarExept(e);
			email = null;
		}
	}

	public void destroy() {
		super.destroy();
	}

	public void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doGet(arg0, arg1);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		try {
			ObjectInputStream inputStream = null;
			try {
				inputStream = new ObjectInputStream(req.getInputStream());
			} catch (Exception e) {
				Logger.logar("inputStream null - > doGetHtml");
			}

			if (inputStream != null) {
				Object object = null;

				object = inputStream.readObject();

				Object escrever = proxyComandos.processarObjeto(object);

				if (ConstantesMesa11.modoZip) {
					dumaparDadosZip(ZipUtil.compactarObjeto(
							ConstantesMesa11.debug, escrever, res
									.getOutputStream()));
				} else {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					dumaparDados(escrever);
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					oos.writeObject(escrever);
					oos.flush();
					res.getOutputStream().write(bos.toByteArray());
				}

				return;
			} else {
				doGetHtml(req, res);
				return;
			}
		} catch (Exception e) {
			Logger.topExecpts(e);
		}
	}

	private void dumaparDadosZip(ByteArrayOutputStream byteArrayOutputStream)
			throws IOException {
		if (ConstantesMesa11.debug) {
			// String basePath = getServletContext().getRealPath("")
			// + File.separator + "WEB-INF" + File.separator + "dump"
			// + File.separator;
			// FileOutputStream fileOutputStream = new FileOutputStream(basePath
			// + "Pack-" + System.currentTimeMillis() + ".zip");
			// fileOutputStream.write(byteArrayOutputStream.toByteArray());
			// fileOutputStream.close();

		}

	}

	private void dumaparDados(Object escrever) throws IOException {
		if (ConstantesMesa11.debug && (escrever != null)) {
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					arrayOutputStream);
			objectOutputStream.writeObject(escrever);
			String basePath = getServletContext().getRealPath("")
					+ File.separator + "WEB-INF" + File.separator + "dump"
					+ File.separator;
			FileOutputStream fileOutputStream = new FileOutputStream(basePath
					+ escrever.getClass().getSimpleName() + "-"
					+ System.currentTimeMillis() + ".txt");
			fileOutputStream.write(arrayOutputStream.toByteArray());
			fileOutputStream.close();

		}

	}

	public static void main(String[] args) {
		// Enumeration e = System.getProperties().propertyNames();
		// while (e.hasMoreElements()) {
		// String element = (String) e.nextElement();
		// System.out.print(element + " - ");
		// Logger.logar(System.getProperties().getProperty(element));
		//
		// }

	}

	public void doGetHtml(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter printWriter = response.getWriter();
		response.setContentType("text/html");
		try {
			printWriter.println("<html><body>");

			AnnotationConfiguration cfg = new AnnotationConfiguration();
			cfg.configure("hibernate.cfg.xml");

			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
			String param = request.getParameter("act");

			if ("create_schema".equals(param)) {
				createSchema(cfg, sessionFactory, printWriter);
			} else if ("update_schema".equals(param)) {
				updateSchema(cfg, sessionFactory, printWriter);
			} else if ("x".equals(param)) {
				topExceptions(response, printWriter);
			}
			printWriter.println("<br/> " + param + " done");
		} catch (Exception e) {
			printWriter.println(e.getMessage());
		}
		printWriter.println("<br/><a href='conf.jsp'>back</a>");
		printWriter.println("</body></html>");
		response.flushBuffer();
	}

	private void topExceptions(HttpServletResponse res, PrintWriter printWriter)
			throws IOException {

		printWriter.write("<h2>Mesa-11 Exceções</h2><br><hr>");
		synchronized (Logger.topExceptions) {
			Set top = Logger.topExceptions.keySet();
			for (Iterator iterator = top.iterator(); iterator.hasNext();) {
				String exept = (String) iterator.next();
				printWriter.write("Quantidade : "
						+ Logger.topExceptions.get(exept));
				printWriter.write("<br>");
				printWriter.write(exept);
				printWriter.write("<br><hr>");
			}
		}
		res.flushBuffer();
	}

	private void updateSchema(AnnotationConfiguration cfg,
			SessionFactory sessionFactory, PrintWriter printWriter)
			throws SQLException {
		Dialect dialect = Dialect.getDialect(cfg.getProperties());
		Session session = sessionFactory.openSession();
		DatabaseMetadata meta = new DatabaseMetadata(session.connection(),
				dialect);
		String[] strings = cfg.generateSchemaUpdateScript(dialect, meta);
		executeStatement(sessionFactory, strings, printWriter);

	}

	private void executeStatement(SessionFactory sessionFactory,
			String[] strings, PrintWriter printWriter) throws SQLException {

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		for (int i = 0; i < strings.length; i++) {
			String string = strings[i];
			java.sql.Statement statement = session.connection()
					.createStatement();
			statement.execute(string);
			printWriter.println("<br/> " + string);
		}

		session.flush();

	}

	private void createSchema(AnnotationConfiguration cfg,
			SessionFactory sessionFactory, PrintWriter printWriter)
			throws HibernateException, SQLException {
		Dialect dialect = Dialect.getDialect(cfg.getProperties());
		String[] strings = cfg.generateSchemaCreationScript(dialect);
		executeStatement(sessionFactory, strings, printWriter);
	}

}
