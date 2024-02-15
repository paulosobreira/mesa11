package br.servlet;


import br.mesa11.ProxyComandos;
import br.nnpe.Constantes;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.ZipUtil;
import br.recursos.Lang;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author paulo.sobreira
 */
public class ServletMesa11 extends HttpServlet {

    public static String webInfDir;
    public static String webDir;
    private ProxyComandos proxyComandos;

    public static String mediaDir;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    public void init() throws ServletException {
        super.init();
        webDir = getServletContext().getRealPath("") + File.separator;
        webInfDir = webDir + "WEB-INF" + File.separator;
        mediaDir = webDir + "midia" + File.separator;
        proxyComandos = new ProxyComandos(webDir, webInfDir);
        Lang.setSrvgame(true);
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
        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        try {
            printWriter.println("<html><body>");

            if ("create_schema".equals(param)) {
            } else if ("update_schema".equals(param)) {
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


    private void topExceptions(HttpServletResponse res, PrintWriter printWriter)
            throws IOException {

        printWriter.write("<h2>Mesa-11 Erros</h2><br><hr>");
        synchronized (Logger.topExceptions) {
            Set top = Logger.topExceptions.keySet();
            for (Iterator iterator = top.iterator(); iterator.hasNext(); ) {
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


}
