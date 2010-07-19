
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", -1); //evita o caching no servidor proxy
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<title>Mesa 11 Online</title>
<style type="text/css">
.class1 A:link {
	text-decoration: none;
	color: white;
}

.class1 A:visited {
	text-decoration: none;
	color: white;
}

.class1 A:active {
	text-decoration: none;
	color: white;
}

.class1 A:hover {
	text-decoration: underline;
	color: black;
}

.class2 A:link {
	text-decoration: none;
	color: black;
}

.class2 A:visited {
	text-decoration: none;
	color: black;
}

.class2 A:active {
	text-decoration: none;
	color: black;
}

.class2 A:hover {
	text-decoration: underline;
	color: black;
}
</style>
</head>
<body style="background-color: white">
<center>

<div><applet code="br.applet.Mesa11Applet.class"
	archive="mesa11.jar" width="600px" height="300px"></applet></div>
<div>
	<span class="class2"> 
		<a	href="http://sowbreira.appspot.com/">http://sowbreira.appspot.com/</a>
	</span> 
	<br>
	<span class="class2">
		<a href="http://www.java.com/pt_BR/">
			Se abaixo nao aparecer nada Clique Aqui e instale a versao mais nova do
			Java. 
		</a>
		<br>
		<a href="http://sowbreira.appspot.com/f1mane/F1Mane.html">
			Vers&atilde;odo Jogo modo 1 jogador 
		</a> 
		<a href="ServletBaseDados?tipo="> Bkp dados
		</a>
	</span>

	<span class="class2"> <a href="mailto:sowbreira@gmail.com">
		Duvidas, Criticas, Sugestões, Bugs ,Colaborar ...</a> 
	</span>
</div>
</center>
</body>
</html>
