package br.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String tipo = req.getParameter("tipo");
		if (!Util.isNullOrEmpty(tipo)) {
			if ("x".equals(tipo)) {
				topExceptions(res);
			}
		}
		try {
			ObjectInputStream inputStream = new ObjectInputStream(req
					.getInputStream());

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
				Logger.logar("Input null");
			}

			PrintWriter printWriter = res.getWriter();
			printWriter.write("ServletAlgol Ok");
			res.flushBuffer();
		} catch (Exception e) {
			Logger.topExecpts(e);
		}
	}

	private void topExceptions(HttpServletResponse res) throws IOException {
		res.setContentType("text/html");
		PrintWriter printWriter = res.getWriter();
		printWriter.write("<html><body>");
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
		printWriter.write("</body></html>");
		res.flushBuffer();
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
}
