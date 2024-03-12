package br.servlet;


import br.mesa11.ProxyComandos;
import br.nnpe.Constantes;
import br.nnpe.Logger;
import br.nnpe.ZipUtil;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author paulo.sobreira
 */
public class ServletMesa11 extends HttpServlet {

    private ProxyComandos proxyComandos;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    public void init() throws ServletException {
        super.init();
        proxyComandos = new ProxyComandos();
        Lang.setSrvgame(true);
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                        createSchema(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            Logger.logarExept(e);
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

                if (Constantes.modoZip) {
                    dumparDadosZip(ZipUtil.compactarObjeto(Logger.debug,
                            escrever, res.getOutputStream()));
                }else{
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    dumparDados(escrever);
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

    private void dumparDadosZip(ByteArrayOutputStream byteArrayOutputStream)
            throws IOException {
        if (Logger.debug) {
            String basePath = getServletContext().getRealPath("")
                    + File.separator + "WEB-INF" + File.separator + "dump"
                    + File.separator;
            FileOutputStream fileOutputStream = new FileOutputStream(basePath
                    + "Pack-" + System.currentTimeMillis() + ".zip");
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        }

    }
    private void dumparDados(Object escrever) throws IOException {
        if (Logger.debug) {
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
    public void doGetHtml(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String param = request.getParameter("act");
        response.setContentType("text/html");
        PrintWriter printWriter = response.getWriter();
        try {
            printWriter.println("<html><body>");

            if ("create_schema".equals(param)) {
                createSchema(printWriter);
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

    private void createSchema(PrintWriter printWriter)
            throws Exception {
        SchemaExport export = new SchemaExport();
        export.create(EnumSet.of(TargetType.DATABASE), getMetaData().buildMetadata());
    }

    private MetadataSources getMetaData() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(CarregadorRecursos.recursoComoStream("META-INF/persistence.xml"));
        NodeList list = doc.getElementsByTagName("property");
        String url = null, pass = null, user = null, driver = null;
        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String attr = element.getAttribute("name");
                if ("javax.persistence.jdbc.url".equals(attr)) {
                    url = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.user".equals(attr)) {
                    user = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.password".equals(attr)) {
                    pass = element.getAttribute("value");
                } else if ("javax.persistence.jdbc.driver".equals(attr)) {
                    driver = element.getAttribute("value");
                }
            }
        }
        Class.forName(driver);
        Connection connection =
                DriverManager.getConnection(url, user, pass);
        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
                        .applySetting(AvailableSettings.CONNECTION_PROVIDER, new MyConnectionProvider(connection))
                        .build());

        metadata.addAnnotatedClass(br.hibernate.Usuario.class);
        metadata.addAnnotatedClass(br.hibernate.Time.class);
        metadata.addAnnotatedClass(br.hibernate.Botao.class);
        metadata.addAnnotatedClass(br.hibernate.Goleiro.class);
        metadata.addAnnotatedClass(br.hibernate.PartidaMesa11.class);
        metadata.addAnnotatedClass(br.hibernate.CampeonatoMesa11.class);
        metadata.addAnnotatedClass(br.hibernate.TimesCampeonatoMesa11.class);
        metadata.addAnnotatedClass(br.hibernate.JogadoresCampeonatoMesa11.class);
        metadata.addAnnotatedClass(br.hibernate.RodadaCampeonatoMesa11.class);
        return metadata;
    }

    private static class MyConnectionProvider implements ConnectionProvider {
        private final Connection connection;

        public MyConnectionProvider(Connection connection) {
            this.connection = connection;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }

        @Override
        public Connection getConnection() {
            return connection; // Interesting part here
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return true;
        }
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
