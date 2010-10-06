<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //evita o caching no servidor proxy
%>

<html>
<head>
<title>Mesa-11</title>
<META NAME="description"
	CONTENT="Futebol de botão,times,futebol,campeonato brasileiro">

<style type="text/css">
div {
	cursor: pointer;
	font-family: sans-serif;
}

A:link {text-decoration: none; 
		color: #0084B4;}
A:visited {text-decoration: none;
		color: #0084B4;} 
A:active {text-decoration: none;
		color: #0084B4;}
A:hover {text-decoration: underline overline; color: black;}

#link {
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	text-align: center;
	padding: 3px;
	padding-left: 20px;
	padding-right: 20px;
	cursor: pointer;
	font-family: sans-serif;
	color: #0084B4;
	text-decoration: none;
}


img {
	border-style: none;
	border-width: 0px;
	border-color: #5C5E5D;
	padding: 10px;
	padding-left: 0px;
	padding-right: 0px;
}

#title {
	border-style: none;
	text-align: left;
	font-family: Arial, sans-serif;
	font-size: 24px;
	font-weight: bold;
	line-height: 24px;
	height: 100px;
}

#adds {
	font-family: Arial, sans-serif;
	font-size: 24px;
	font-weight: bold;
	position: relative;
	float :left;
}

#main{
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	padding: 3px;
	padding-left: 20px;
	padding-right: 20px;
	border-style: none;
	position: relative;
	float: right;	
}

#shots{
	text-align: left;
	padding-top: 10px;
}

#shotsPromo{
	border-style: solid;
	border-width: 1px;
	border-color: #B8CFE5;
	text-align: center;
	padding: 0px;
}

#description {
	color: #666666;
	font-size: 13px;
	font-style: italic;
}
</style>
<script type="text/javascript" src="highslide/highslide-full.js"></script>
<link rel="stylesheet" type="text/css" href="highslide/highslide.css" />

<!--
	2) Optionally override the settings defined at the top
	of the highslide.js file. The parameter hs.graphicsDir is important!
-->

<script type="text/javascript">
	
	hs.graphicsDir = 'highslide/graphics/';
	hs.align = 'center';
	hs.transitions = ['expand', 'crossfade'];
	hs.outlineType = 'rounded-white';
	hs.fadeInOut = true;
	hs.dimmingOpacity = 0.75;

	// define the restraining box
	hs.useBox = true;
	hs.width = 800;
	hs.height = 600;

	// Add the controlbar
	hs.addSlideshow({
		//slideshowGroup: 'group1',
		interval: 5000,
		repeat: false,
		useControls: true,
		fixedControls: 'fit',
		overlayOptions: {
			opacity: 1,
			position: 'bottom center',
			hideOnMouseOut: true
		}
	});


	
</script>

</head>
<body>
<center>
<table >
	<tr> 
	<td>
	<div id="adds">
		<script type="text/javascript"><!--
		google_ad_client = "pub-1471236111248665";
		/* 120x600, criado 14/06/10 */
		google_ad_slot = "5219714006";
		google_ad_width = 120;
		google_ad_height = 600;
		//-->
		</script>
		<script type="text/javascript"
		src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
		</script>
	</div>
	</td>
	<td>
	<div id="main">
		<div id="title">	
			<span >Mesa-11
				<h1 id="description">Futebol de botão</h1>
			</span>
			<a id="link" style="position: absolute; left: 150px; top: 10px;font-size: 16px;"
				href="http://twitter.com/mesa11" style="text-align: right;"
				target="_BLANK">Twitter</a>
			<a id="link" style="position: absolute; left: 250px; top: 10px;font-size: 16px;"
				href="http://sowbreira.appspot.com/" style="text-align: right;"
				target="_BLANK">Site Autor</a>
			<a id="link" style="position: absolute; left: 370px; top: 10px;font-size: 16px;"
				href="mailto:sowbreira@gmail.com" style="text-align: right;"
				target="_BLANK">Sugestões, Bugs ,Colaborar ...</a>		
			<a id="link" style="position: absolute; left: 220px; top: 50px;font-size: 16px;"
				href="http://www.java.com/" style="text-align: right;"
				target="_BLANK">Instale o Java</a>
			<a id="link" style="position: absolute; left: 370px; top: 50px;font-size: 16px;"
				href="mesa11.html" style="text-align: right;"
				target="_BLANK">Versao 1 Jogador</a>	
			<br>					
		</div>
		<div style="text-align: left;">
			<applet code="br.applet.Mesa11Applet.class"	archive="mesa11.jar" width="820px" height="380px"> 
			</applet>
		</div>
		<div id="shots" class="highslide-gallery">
			<a href="m11-1.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-1.jpg" width="130" height="120" /></a>
			<a href="m11-2.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-2.jpg" width="130" height="120" /></a>
			<a href="m11-3.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-3.jpg" width="130" height="120" /></a>
			<a href="m11-4.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-4.jpg" width="130" height="120" /></a>
			<a href="m11-5.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-5.jpg" width="130" height="120" /></a>
			<a href="m11-6.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-6.jpg" width="130" height="120" /></a>
		</div>
	</div>
	</td>
	<td>
		<div id="shotsPromo" class="highslide-gallery">
			Veja Tambem <br><br>
			<a id="link" 
				href="http://www.f1mane.com" style="text-align: center;"
				target="_BLANK">  F1-mane </a><br><br>
			<a href="m11-1.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-1.jpg" width="130" height="120" /></a><br>
			<a href="m11-2.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-2.jpg" width="130" height="120" /></a><br>
			<a href="m11-3.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-3.jpg" width="130" height="120" /></a><br>
			<a href="m11-4.jpg" onclick="if (!(navigator.userAgent.indexOf('Firefox')==-1)) {return hs.expand(this)}"> <img src="m11-4.jpg" width="130" height="120" /></a><br>
		</div>	
	</td>
	</tr>
</table>
</center>
</body>
</html>