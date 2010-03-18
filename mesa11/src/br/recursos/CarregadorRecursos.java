package br.recursos;

import java.awt.image.BufferedImage;
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

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import br.nnpe.ImageUtil;
import br.nnpe.Logger;

public class CarregadorRecursos {

	private static Map bufferCarros = new HashMap();

	public static URL carregarImagem(String imagem) {
		return CarregadorRecursos.class.getResource(imagem);
	}

	public static BufferedImage carregaBufferedImageTranspareciaBranca(
			String file) {
		BufferedImage buffer = null;
		try {
			ImageIcon icon = new ImageIcon(CarregadorRecursos.class
					.getResource(file));
			buffer = ImageUtil.toBufferedImage(icon.getImage());
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
		ImageIcon icon = new ImageIcon(CarregadorRecursos.class
				.getResource(backGroundStr));
		BufferedImage backGround = ImageUtil.toBufferedImage(icon.getImage());
		panel.setSize(backGround.getWidth(), backGround.getHeight());
		if (backGround == null) {
			Logger.logar("backGround=" + backGround);
			System.exit(1);
		}
		return backGround;
	}

	public static InputStream recursoComoStream(String string) {
		CarregadorRecursos rec = new CarregadorRecursos();

		return rec.getClass().getResourceAsStream(string);
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
		for (Iterator iterator = carList.iterator(); iterator.hasNext();) {
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
		BufferedImage bufferedImage = (BufferedImage) bufferCarros.get(img);
		if (bufferedImage != null) {
			return bufferedImage;
		}
		bufferedImage = carregaBufferedImageTranspareciaBranca(img);
		bufferCarros.put(img, bufferedImage);
		return bufferedImage;
	}
}
