package br.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import br.mesa11.ProxyComandos;
import br.nnpe.Constantes;
import br.nnpe.Email;
import br.nnpe.HibernateUtil;
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
	private static String replaceHost = "{host}";
	public static String webDir;
	private ProxyComandos proxyComandos;

	public static String mediaDir;
	public static Email email;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

	public void init() throws ServletException {
		super.init();
		webDir = getServletContext().getRealPath("") + File.separator;
		webInfDir = webDir + "WEB-INF" + File.separator;
		mediaDir = webDir + "midia" + File.separator;
		proxyComandos = new ProxyComandos(webDir, webInfDir);
		Lang.setSrvgame(true);
		try {
			email = new Email(getServletContext().getRealPath("")
					+ File.separator + "WEB-INF" + File.separator);
		} catch (Exception e) {
			Logger.logarExept(e);
			email = null;
		}
		if (Logger.debug) {
			email = null;
		}
	}

	private String obterHost() throws UnknownHostException {
		String host = "";
		try {
			Properties properties = new Properties();
			properties.load(
					this.getClass().getResourceAsStream("server.properties"));
			host = properties.getProperty("host");
			if (!Util.isNullOrEmpty(host)) {
				return host;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String ip = Inet4Address.getLocalHost().getHostAddress();
		int port = 80;
		try {
			// Connector[] connectors = ServerFactory.getServer()
			// .findService("Catalina").findConnectors();
			MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null)
					.get(0);
			ObjectName name = new ObjectName("Catalina", "type", "Server");
			Server server = (Server) mBeanServer.getAttribute(name,
					"managedResource");
			Connector[] connectors = server.findService("Catalina")
					.findConnectors();
			for (int i = 0; i < connectors.length; i++) {
				if ("HTTP/1.1".equals(connectors[i].getProtocol())) {
					port = connectors[i].getPort();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		host = ip + ":" + port;
		return host;
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

				if (Constantes.modoZip) {
					dumaparDadosZip(ZipUtil.compactarObjeto(Logger.debug,
							escrever, res.getOutputStream()));
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
		if (Logger.debug) {
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
		if (false && (escrever != null)) {
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					arrayOutputStream);
			objectOutputStream.writeObject(escrever);
			String basePath = getServletContext().getRealPath("")
					+ File.separator + "WEB-INF" + File.separator + "dump"
					+ File.separator;
			FileOutputStream fileOutputStream = new FileOutputStream(
					basePath + escrever.getClass().getSimpleName() + "-"
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
		String param = request.getParameter("act");
		if ("backup".equals(param)) {
			response.setContentType("application/x-zip-compressed");
			try {
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + "mesa11_data" + "_"
								+ dateFormat.format(new Date()) + ".zip"
								+ "\"");

				byte[] ret = obterBytesBase();
				if (ret == null) {
					return;
				}
				response.getOutputStream().write(ret);
				response.flushBuffer();
			} catch (Exception e) {
				Logger.topExecpts(e);
			}
			return;
		}
		response.setContentType("text/html");
		PrintWriter printWriter = response.getWriter();
		try {
			printWriter.println("<html><body>");

			AnnotationConfiguration cfg = new AnnotationConfiguration();
			cfg.configure("hibernate.cfg.xml");

			SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

			if ("create_schema".equals(param)) {
				createSchema(cfg, sessionFactory, printWriter);
			} else if ("update_schema".equals(param)) {
				updateSchema(cfg, sessionFactory, printWriter);
			} else if ("x".equals(param)) {
				topExceptions(response, printWriter);
			}
			printWriter.println("<br/> ");
		} catch (Exception e) {
			printWriter.println(e.getMessage());
		}
		printWriter.println("<br/><a href='conf.jsp'>back</a>");
		printWriter.println("</body></html>");
		response.flushBuffer();
	}

	public byte[] obterBytesBase() {
		try {

			ZipOutputStream zipOutputStream = new ZipOutputStream(
					new FileOutputStream(webInfDir + "mesa11bkp.zip"));

			zipDir(webDir + "midia", zipOutputStream);

			zipOutputStream.close();

			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(webInfDir + "mesa11bkp.zip"));
			int byt = bufferedInputStream.read();

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
				ZipEntry anEntry = new ZipEntry(f.getName());
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

	private void topExceptions(HttpServletResponse res, PrintWriter printWriter)
			throws IOException {

		printWriter.write("<h2>Mesa-11 Exce��es</h2><br><hr>");
		synchronized (Logger.topExceptions) {
			Set top = Logger.topExceptions.keySet();
			for (Iterator iterator = top.iterator(); iterator.hasNext();) {
				String exept = (String) iterator.next();
				printWriter.write(
						"Quantidade : " + Logger.topExceptions.get(exept));
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
