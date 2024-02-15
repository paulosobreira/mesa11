package br.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.hibernate.Botao;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.MesaPanel;
import br.nnpe.Logger;
import br.nnpe.tos.ErroServ;
import br.recursos.CarregadorRecursos;

@Path("/mesa11Rest")
public class Mesa11Rest {

	@GET
	@Path("/restTest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response criarSessaoVisitante() {
		Botao botao = new Botao();
		botao.setNome("Teste");
		return Response.status(200).entity(botao).build();
	}

	@GET
	@Path("/campoBg")
	@Produces("image/jpg")
	public Response campoBg() {
		try {
			ControleJogo controleJogo = new ControleJogo();
			controleJogo.setJogoIniciado(true);
			MesaPanel mesaPanel = new MesaPanel(controleJogo);
			BufferedImage bg = mesaPanel.desenhaCampo();
			if (bg == null) {
				return Response.status(200).entity("null").build();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bg, "jpg", baos);
			byte[] imageData = baos.toByteArray();
			return Response.status(200).entity(imageData).build();
		} catch (Exception e) {
			Logger.topExecpts(e);
			return Response.status(500)
					.entity(new ErroServ(e).obterErroFormatado())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
	
	@GET
	@Path("/png/{recurso}")
	@Produces("image/png")
	public Response png(@PathParam("recurso") String recurso) {
		try {
			BufferedImage buffer = CarregadorRecursos
					.carregaBufferedImage(recurso + ".png");
			if (buffer == null) {
				return Response.status(200).entity("null").build();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buffer, "png", baos);
			byte[] imageData = baos.toByteArray();
			return Response.ok(new ByteArrayInputStream(imageData)).build();
		} catch (Exception e) {
			Logger.topExecpts(e);
			return Response.status(500)
					.entity(new ErroServ(e).obterErroFormatado())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

}
