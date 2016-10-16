package br.nnpe;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

/**
 * @author Sobreira Criado em 13/09/2005
 */
public class BeanUtil {
	static {
		ConvertUtils.register(new Converter() {
			public Object convert(Class type, Object value) {
				SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();

				if ((value == null) || (value.toString().length() < 1)) {
					return null;
				}

				return sqlTimestampConverter.convert(type, value);
			}
		}, Timestamp.class);
	}

	public static void copiarVO(Object origem, Object destino)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtils.copyProperties(destino, origem);
	}

	public static void copiarCollections(List listOrigem, List listaDestino)
			throws IllegalAccessException, InvocationTargetException,
			InstantiationException {
		for (Iterator iter = listOrigem.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			Object copia = element.getClass().newInstance();
			BeanUtil.copiarVO(element, copia);
			listaDestino.add(copia);
		}
	}
}
