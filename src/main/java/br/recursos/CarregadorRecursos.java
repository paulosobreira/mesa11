package br.recursos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import br.nnpe.ImageUtil;
import br.nnpe.Logger;

public class CarregadorRecursos {

    private static Map bufferImages = new HashMap();

    public static URL carregarImagem(String imagem) {
        return CarregadorRecursos.class.getResource(imagem);
    }

    public static BufferedImage carregaImagem(String file) {
        try {
            return ImageIO.read(CarregadorRecursos.class.getResource(file));
        } catch (IOException e) {
            Logger.logar(e);
        }
        return null;
    }

    public static BufferedImage carregaImagemFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            Logger.logar(e);
        }
        return null;
    }

    public static BufferedImage carregaImagemLocal(String stringUrl) {
        try {
            String current = new java.io.File(".").getCanonicalPath();
            Logger.logar("Current dir:" + current);
            String currentDir = System.getProperty("user.dir");
            Logger.logar("Current dir using System:" + currentDir);
            return ImageIO.read(new File(currentDir + stringUrl));
        } catch (Exception e) {
            Logger.logar(e);
        }
        return null;

    }

    public static BufferedImage carregaBufferedImage(String file) {
        BufferedImage buffer = null;
        try {
            buffer = carregaImagemSemCache(file);
            if (buffer == null) {
                Logger.logar("img=" + buffer);
                System.exit(1);
            }

        } catch (Exception e) {
            Logger.logar("Erro gerando transparencia para :" + file);
            Logger.logarExept(e);
        }

        return buffer;
    }

    public static BufferedImage carregaBufferedImageTransparecia(String file,
                                                                 Color cor) {
        BufferedImage buffer = carregaImagem(file);
        ImageIcon img = new ImageIcon(buffer);
        BufferedImage srcBufferedImage = new BufferedImage(img.getIconWidth(),
                img.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        srcBufferedImage.getGraphics().drawImage(img.getImage(), 0, 0, null);

        BufferedImage bufferedImageRetorno = new BufferedImage(
                img.getIconWidth(), img.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Raster srcRaster = srcBufferedImage.getData();
        WritableRaster destRaster = bufferedImageRetorno.getRaster();
        int[] argbArray = new int[4];

        for (int i = 0; i < img.getIconWidth(); i++) {
            for (int j = 0; j < img.getIconHeight(); j++) {
                argbArray = new int[4];
                argbArray = srcRaster.getPixel(i, j, argbArray);

                Color c = new Color(argbArray[0], argbArray[1], argbArray[2],
                        argbArray[3]);
                if (c.equals(cor)) {
                    argbArray[3] = 0;
                }
                destRaster.setPixel(i, j, argbArray);
            }
        }

        return bufferedImageRetorno;
    }

    public static BufferedImage carregaBufferedImageTranspareciaBranca(
            String file) {
        BufferedImage buffer = null;
        try {
            buffer = carregaImagemSemCache(file);
            if (buffer == null) {
                Logger.logar("img=" + buffer);
                System.exit(1);
            }

        } catch (Exception e) {
            Logger.logar("Erro gerando transparencia para :" + file);
        }

        return ImageUtil.geraTransparencia(buffer);
    }

    public static BufferedImage carregaBackGround(String backGroundStr,
                                                  JPanel panel) {
        BufferedImage backGround = carregaImagemSemCache(backGroundStr);
        panel.setSize(backGround.getWidth(), backGround.getHeight());
        if (backGround == null) {
            Logger.logar("backGround=" + backGround);
            System.exit(1);
        }
        return backGround;
    }

    public static InputStream recursoComoStream(String string) {
        CarregadorRecursos rec = new CarregadorRecursos();

        return rec.getClass().getResourceAsStream("/" + string);
    }

    public static void main(String[] args) throws URISyntaxException,
            IOException {
        List carList = new LinkedList();
        File file = new File("src/sowbreira/f1mane/recursos/carros");
        File[] dir = file.listFiles();
        for (int i = 0; i < dir.length; i++) {
            if (!dir[i].getName().startsWith(".")) {
                File[] imgCar = dir[i].listFiles();
                for (int j = 0; j < imgCar.length; j++) {
                    if (!imgCar[j].getName().startsWith(".")
                            && !imgCar[j].getName().equals("Thumbs.db")) {
                        String str = imgCar[j].getPath().split("recursos")[1];
                        str = str.substring(1, str.length());
                        carList.add(str);

                    }
                }
            }
        }
        FileWriter fileWriter = new FileWriter(
                "src/sowbreira/f1mane/recursos/carlist.txt");
        for (Iterator iterator = carList.iterator(); iterator.hasNext(); ) {
            String carro = (String) iterator.next();
            StringBuffer nCarro = new StringBuffer();
            for (int i = 0; i < carro.length(); i++) {
                if (carro.charAt(i) == '\\') {
                    nCarro.append('/');
                } else {
                    nCarro.append(carro.charAt(i));
                }
            }
            Logger.logar(nCarro.toString());
            fileWriter.write(nCarro.toString() + "\n");
        }
        fileWriter.close();
    }

    public static BufferedImage carregaImg(String img) {
        BufferedImage bufferedImage = (BufferedImage) bufferImages.get(img);
        if (bufferedImage != null) {
            return bufferedImage;
        }
        bufferedImage = carregaImagemSemCache(img);
        bufferImages.put(img, bufferedImage);
        return bufferedImage;
    }

    public static BufferedImage carregaImagemSemCache(String file) {
        try {
            return ImageIO.read(CarregadorRecursos.class.getResource("/"+file));
        } catch (IOException e) {
            Logger.logar(e);
        }
        return null;
    }

    public static BufferedImage carregaImagemURL(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            Logger.logar(e);
        }
        return null;
    }
}
