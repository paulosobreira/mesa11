package br.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.hibernate.Botao;

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

}
