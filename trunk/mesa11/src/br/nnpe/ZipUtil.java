/**
 * 
 */
package br.nnpe;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author paulo.sobreira
 * 
 */
public class ZipUtil {

	public static Object lerDados(String path) throws Exception {
		ZipInputStream zin = new ZipInputStream(new FileInputStream(path));
		zin.getNextEntry();
		ByteArrayOutputStream arrayDinamico = new ByteArrayOutputStream();
		int byt = zin.read();

		while (-1 != byt) {
			arrayDinamico.write(byt);
			byt = zin.read();
		}

		arrayDinamico.flush();

		ByteArrayInputStream bin = new ByteArrayInputStream(arrayDinamico
				.toByteArray());
		XMLDecoder decoder = new XMLDecoder(bin);
		return decoder.readObject();
	}

	public static void gravarDados(Object object, String path, String nome)
			throws IOException {
		Logger.logar(path);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
		encoder.writeObject(object);
		encoder.flush();
		ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(path));
		ZipEntry entry = new ZipEntry(nome);
		zipOutputStream.putNextEntry(entry);
		zipOutputStream.write(byteArrayOutputStream.toByteArray());
		zipOutputStream.closeEntry();
		zipOutputStream.close();
	}

	public static Object descompactarObjeto(InputStream in) {
		try {

			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry entry = zin.getNextEntry();
			ByteArrayOutputStream arrayDinamico = new ByteArrayOutputStream();
			int byt = zin.read();

			while (-1 != byt) {
				arrayDinamico.write(byt);
				byt = zin.read();
			}

			arrayDinamico.flush();

			ByteArrayInputStream bin = new ByteArrayInputStream(arrayDinamico
					.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bin);

			zin.close();

			return ois.readObject();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return null;
	}

	public static ByteArrayOutputStream compactarObjeto(boolean debug,
			Object aCompact, OutputStream stream) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(aCompact);
			oos.flush();

			ZipOutputStream zipOutputStream = new ZipOutputStream(stream);
			ZipEntry entry = new ZipEntry("root");
			zipOutputStream.putNextEntry(entry);
			zipOutputStream.write(bos.toByteArray());
			zipOutputStream.closeEntry();
			zipOutputStream.close();
			if (debug) {
				ByteArrayOutputStream bosDump = new ByteArrayOutputStream();
				zipOutputStream = new ZipOutputStream(bosDump);
				entry = new ZipEntry("root");
				zipOutputStream.putNextEntry(entry);
				zipOutputStream.write(bos.toByteArray());
				zipOutputStream.closeEntry();
				zipOutputStream.close();
				return bosDump;
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return null;
	}

}
