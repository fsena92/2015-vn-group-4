package grupo4.dds.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import queComemos.entrega3.dominio.Dificultad;
import grupo4.dds.main.Routes;
import grupo4.dds.receta.Receta;
import grupo4.dds.receta.Temporada;
import grupo4.dds.repositorios.RepositorioDeRecetas;
import grupo4.dds.repositorios.RepositorioDeUsuarios;
import grupo4.dds.controller.MostradorDificultad;
import grupo4.dds.usuario.Usuario;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class ConsultasController {

	public ModelAndView listar(Request request, Response response) {
		
		
		String filtroNombre = request.queryParams("filtroNombre");
		String filtroDificultad = request.queryParams("filtroDificultad");
		String filtroTemporada = request.queryParams("filtroTemporada");
		String filtroCaloriasDesde = (request.queryParams("filtroCaloriasDesde"));
		String filtroCaloriasHasta = (request.queryParams("filtroCaloriasHasta"));

		HashMap<String, Object> viewModel = new HashMap<>();
		
		viewModel.put("recetas", recetasAMostrar(filtroNombre, filtroDificultad, filtroTemporada, filtroCaloriasDesde, filtroCaloriasHasta));
		viewModel.put("filtroNombre", filtroNombre);
		viewModel.put("filtroDificultad", filtroDificultad);
		viewModel.put("filtroTemporada", filtroTemporada);
		viewModel.put("filtroCaloriasDesde", filtroCaloriasDesde);
		viewModel.put("filtroCaloriasHasta", filtroCaloriasHasta);
		viewModel.put("temporadas", Temporada.values());
		viewModel.put("dificultad", MostradorDificultad.getMostradores(Dificultad.values()));

		return new ModelAndView(viewModel, "consultas.hbs");
	}
	
	public ModelAndView mostrar(Request request, Response response){
		HashMap <String,Object> viewModel = new HashMap<>();
		viewModel.put("temporadas", Temporada.values());
		viewModel.put("dificultad", MostradorDificultad.getMostradores(Dificultad.values()));
		return new ModelAndView(viewModel, "consultas.hbs");
		
	}
	
	public List<Receta> recetasAMostrar(String filtroNombre, String filtroDificultad, String filtroTemporada, String caloriasDesde, String caloriasHasta){
		
		List<Receta> recetas;
		Long id = Routes.usuarioActual.getId();
		Usuario user = RepositorioDeUsuarios.instance().buscar(id);
		Consulta consulta=new Consulta(filtroNombre,filtroDificultad,filtroTemporada,caloriasDesde,caloriasHasta);
		
		recetas = RepositorioDeRecetas.instance().buscarPorFiltros(consulta);
	    
		return recetas.stream().filter(r-> r.puedeSerVistaPor(user)).collect(Collectors.toList());
	}

}
