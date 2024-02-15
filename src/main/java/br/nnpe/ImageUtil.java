package br.nnpe;

import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import br.recursos.CarregadorRecursos;

/**
 * @author Paulo Sobreira Criado Em 21/08/2005
 */
public class ImageUtil {
    // This method returns a buffered image with the contents of an image

    public static BufferedImage geraResize(BufferedImage src, double fatorx,
                                           double fatory) {
        AffineTransform afZoom = new AffineTransform();
        afZoom.setToScale(fatorx, fatory);
        BufferedImage dst = new BufferedImage((int) Math.round(src.getWidth()
                * fatorx), (int) Math.round(src.getHeight() * fatory),
                BufferedImage.TYPE_INT_ARGB);
        AffineTransformOp op = new AffineTransformOp(afZoom,
                AffineTransformOp.TYPE_BILINEAR);
        op.filter(src, dst);
        return dst;
    }

    public static BufferedImage gerarFade(BufferedImage src, int translucidez) {
        ImageIcon img = new ImageIcon(src);
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
                if (argbArray[3] != 0)
                    argbArray[3] = translucidez;
                destRaster.setPixel(i, j, argbArray);
            }
        }

        return bufferedImageRetorno;
    }

    /**
     * Serve pra nada essa porra!!!
     *
     * @param image
     * @return
     */
    public static boolean hasAlpha(Image image) {
        if (true) {
            return false;
        }
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;

            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }

    public static BufferedImage geraTransparencia(BufferedImage src) {
        ImageIcon img = new ImageIcon(src);
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
                if (c.getRed() > 250 && c.getGreen() > 250 && c.getBlue() > 250) {
                    argbArray[3] = 0;
                }

                destRaster.setPixel(i, j, argbArray);
            }
        }

        return bufferedImageRetorno;
    }

    public static byte[] bufferedImage2ByteArray(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "jpg", os);
        } catch (Exception e) {
            Logger.logarExept(e);
        }
        return os.toByteArray();
    }

    public static Color gerarCorTransparente(Color color, int transp) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                transp);
    }

    public static ImageIcon carregarImagem(String img) {
        try {
            BufferedImage carregaImagem = CarregadorRecursos.carregaImg(img);
            ImageIcon icon = new ImageIcon(carregaImagem);
            return icon;
        } catch (Exception e1) {
            Logger.logarExept(e1);
        }
        return null;
    }

}
