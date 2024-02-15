package br.nnpe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import org.hibernate.Session;

public class Util {
	public static Locale loc_brasil = new Locale("pt", "BR");
	public static DecimalFormat decimalFormat = new DecimalFormat("0.00");
	public static final String MASCARA_CPF = "###.###.###-##";
	public static final String MASCARA_CNPJ = "##.###.###/####-##";

	public static boolean isNullOrEmpty(String campo) {
		return ((campo == null) || "".equals(campo));
	}

	public static Date alteraData(Date date, int qtdDias) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, qtdDias);

		return new Date(calendar.getTimeInMillis());
	}

	public static boolean validaIntervaloData(Dia dataInicial, Dia dataFinal) {
		if (dataInicial.maiorQue(dataFinal)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean validaIntervaloData(java.sql.Date dataInicial,
			java.sql.Date dataFinal) {
		if (dataInicial.compareTo(dataFinal) > 0) {
			return false;
		} else {
			return true;
		}
	}

	public static void validaIntervaloData(String dataInicial, String dataFinal)
			throws Exception {
		if (!validaIntervaloData(dataInicial, dataFinal,
				Constantes.DATA_FORMATO)) {
			throw new Exception("Intervalo de datas invalido");
		}
	}

	public static boolean validaIntervaloData(String dataInicial,
			String dataFinal, String formato) throws Exception {
		java.sql.Date dt1 = FormatDate.parseDate(dataInicial, formato);
		java.sql.Date dt2 = FormatDate.parseDate(dataFinal, formato);

		return validaIntervaloData(dt1, dt2);
	}

	public static boolean validaIntervaloData(Timestamp data_inicio,
			Timestamp data_fim) throws Exception {
		String inicio = FormatDate.format(data_inicio);
		String fim = FormatDate.format(data_fim);

		return validaIntervaloData(inicio, fim, Constantes.DATA_FORMATO);
	}

	public static String removeQuotes(String text) {
		String ret = "";

		if (text != null) {
			for (int i = 0; i < text.length(); i++) {
				if (text.substring(i, i + 1).equals("\"")) {
					ret += "'";
				} else {
					ret += text.substring(i, i + 1);
				}
			}
		}

		return ret;
	}

	public static String formatNumber(String patern, double number) {
		DecimalFormat form = new DecimalFormat(patern);

		return form.format(number);
	}

	public static InputStream getBinaryStream(ResultSet rs, String fieldName)
			throws Exception {
		try {
			Blob blob = rs.getBlob(fieldName);

			return blob.getBinaryStream();
		} catch (Exception e) {
			throw new Exception("getBinaryStream", e);
		}
	}

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
					.substring(1, 3));
		}

		return sb.toString();
	}

	public static String md5(String message)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		return hex(md.digest(message.getBytes("CP1252")));
	}

	public static Timestamp converteStringTimestamp(String valor)
			throws Exception {
		if (valor == null) {
			return null;
		}

		if ((valor.indexOf('/') != -1) && (valor.length() < 10)) {
			DecimalFormat format = new DecimalFormat("00");
			String[] parts = valor.split("/");
			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < parts.length; i++) {
				buffer.append(format.format(Long.parseLong(parts[i])));

				if (i < (parts.length - 1)) {
					buffer.append("/");
				}
			}

			valor = buffer.toString();
		}

		return ((valor.length() == 10)
				? new java.sql.Timestamp(FormatDate.parseDate(valor).getTime())
				: FormatDate.parseTimestamp(valor,
						Constantes.DATA_FORMATO_COMPLETO));
	}

	/**
	 * Substitui o ponto de uma String por uma outra qualquer.
	 * 
	 * @param string
	 * @param replacement
	 * @return
	 */
	public static String substituirPonto(String string, String replacement) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {
			char value = string.charAt(i);

			if (value == '.') {
				buffer.append(replacement);
			} else {
				buffer.append(value);
			}
		}

		return buffer.toString();
	}

	/**
	 * Retona o valor Integer caso diferente de zero.
	 */
	public static Integer integerOrNull(int num) {
		if (num != 0) {
			return new Integer(num);
		}

		return null;
	}

	/**
	 * Retona o valor Double caso diferente de zero.
	 */
	public static Double doubleOrNull(double num) {
		if (num != 0) {
			return new Double(num);
		}

		return null;
	}

	public static double doubleOr0(Double num) {
		if (num == null) {
			return 0;
		}

		return double2Decimal(num.doubleValue());
	}

	/**
	 * @param qtEntrada
	 * @return
	 */
	public static int intOr0(Integer i) {
		if (i == null) {
			return 0;
		} else {
			return i.intValue();
		}
	}

	public static boolean isNumero(String planoConta) {
		try {
			Integer.parseInt(planoConta);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static double double2Decimal(double dob) {
		String doubleValue = String.valueOf(dob);

		if (doubleValue.indexOf("E-") != -1) {
			return 0;
		}

		return Double.parseDouble(decimalFormat.format(dob).replace(',', '.'));
	}

	public static void serializarEmArquivo(Object o) {
		String filename = o.getClass().getName();
		File file = new File(filename);

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);

			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					fileOutputStream);
			objectOutputStream.writeObject(o);
			fileOutputStream.flush();
		} catch (Exception e) {
			Logger.logarExept(e);
		}
	}

	public static String mascarar(String valor, String mascara)
			throws ParseException {
		MaskFormatter formatter = new MaskFormatter(mascara);
		JFormattedTextField textField = new JFormattedTextField();
		formatter.install(textField);
		textField.setText(valor);

		return textField.getText();
	}

	public static String mascararCpf(String cpf) throws ParseException {
		return mascarar(cpf, MASCARA_CPF);
	}

	public static String mascararCnpj(String cnpj) throws ParseException {
		return mascarar(cnpj, MASCARA_CNPJ);
	}

	public static byte[] file2byteArray(File file) throws Exception {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(file));
		int val = bufferedInputStream.read();
		while (val != -1) {
			arrayOutputStream.write(val);
			val = bufferedInputStream.read();
		}
		return arrayOutputStream.toByteArray();
	}

	public static void byteArray2file(byte[] file, String path)
			throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream(
				new File(path));
		fileOutputStream.write(file);
		fileOutputStream.close();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Logger.logar(md5("whiplash"));
		// byteArray2file(
		// file2byteArray(new File("C:\\wall.bmp")),
		// "C:\\sobreira\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\algol-rpg\\midia\\mapas\\ce.png");
		System.out.println(intervalo(0, 5));
	}

	public static List formataLista(List list) {
		List ret = new LinkedList();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			ret.add(object);
		}
		return ret;
	}

	public static int inte(double d) {
		return (int) Math.round(d);
	}

	public static double intervalo(double val1, double val2) {
		return (val1 + (Math.random() * (0.1 + val2 - val1)));
	}

	public static int intervalo(int val1, int val2) {

		return (val1 + (int) (Math.random() * (1 + val2 - val1)));
	}

	public static List removePersistBag(List list, Session session) {
		List ret = new LinkedList();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (session != null) {
				session.evict(object);
			}
			ret.add(object);
		}
		return ret;
	}

	public static int larguraTexto(String msg, Graphics2D g2d) {
		int largura = 0;
		for (int i = 0; i < msg.length(); i++) {
			if (g2d == null) {
				largura += 7;
			} else {
				largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
			}
		}
		return largura;
	}

	public static int criarCorAleatoria() {

		int intervalo = Util.intervalo(1, 10);

		switch (intervalo) {
			case 1 :
				return Color.RED.getRGB();
			case 2 :
				return Color.BLUE.getRGB();
			case 3 :
				return Color.GREEN.getRGB();
			case 4 :
				return Color.YELLOW.getRGB();
			case 5 :
				return Color.ORANGE.getRGB();
			case 6 :
				return Color.BLACK.getRGB();
			case 7 :
				return Color.GRAY.getRGB();
			case 8 :
				return Color.DARK_GRAY.getRGB();
			case 9 :
				return Color.LIGHT_GRAY.getRGB();
			case 10 :
				return Color.WHITE.getRGB();

			default :
				break;
		}

		return Color.WHITE.getRGB();
	}

	public static void dormir(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Logger.logarExept(e);
		}

	}
}
