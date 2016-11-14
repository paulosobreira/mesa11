package br.recursos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImagemTimes {

	public static void main(String[] args) throws IOException {
		File file = new File("./WebContent/midia");
		File times = new File("./src/br/recursos/imagens_times.txt");
		FileWriter fileWriter = new FileWriter(times);
		String[] list = file.list();
		for (int i = 0; i < list.length; i++) {
			String escrever = list[i];
			if (i < list.length - 1) {
				escrever += "\n";
			}
			fileWriter.write(escrever);
			System.out.println(list[i]);
		}
		fileWriter.close();
	}
}
