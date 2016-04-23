package grupo4.dds.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import queComemos.entrega3.dominio.Dificultad;
import grupo4.dds.main.Routes;
import grupo4.dds.receta.EncabezadoDeReceta;
import grupo4.dds.receta.Receta;
import grupo4.dds.receta.Ingrediente;
import grupo4.dds.repositorios.RepositorioDeRecetas;
import grupo4.dds.repositorios.RepositorioDeUsuarios;
import grupo4.dds.usuario.Usuario;
import grupo4.dds.receta.Temporada;
import queComemos.entrega3.dominio.Dificultad;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

public class RecetaController implements WithGlobalEntityManager,
		TransactionalOps {

	public ModelAndView mostrar(Request request, Response response) {

		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));
		long usuarioId = Routes.usuarioActual.getId();
		Usuario usuario = RepositorioDeUsuarios.instance().buscar(usuarioId);

		HashMap<String, Object> viewModel = new HashMap<>();

		viewModel.put("receta", receta);
		viewModel.put("marcaFavorita", usuario.getMarcaFavorita());
		viewModel.put("condiciones", usuario.getCondiciones());
		viewModel.put("id", Long.parseLong(request.params("id")));

		viewModel.put("favorita", usuario.getHistorial().contains(receta));

		return new ModelAndView(viewModel, "receta.hbs");
	}

	public Receta recetaMostrada(long id) {
		return RepositorioDeRecetas.instance().buscar(id);
	}

	public ModelAndView nuevo(Request request, Response response) {

		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));

		long usuarioId = Routes.usuarioActual.getId();
		Usuario usuario = RepositorioDeUsuarios.instance().buscar(usuarioId);

		HashMap<String, Object> viewModel = new HashMap<>();

		//List<Ingrediente> ingredientes= new ArrayList<Ingrediente>();
		
		//ingredientes.addAll((entityManager().createQuery("FROM Ingrediente",Ingrediente.class).getResultList()));
			
		
		viewModel.put("receta", receta);
		viewModel.put("dificultad", MostradorDificultad.getMostradores(Dificultad.values()));
		viewModel.put("temporadas", Temporada.values());
		viewModel.put("favorita", usuario.getHistorial().contains(receta));
		viewModel.put("id", Long.parseLong(request.params("id")));
		//viewModel.put("ingredientesAgregar", ingredientes);
		//viewModel.put("condimentosAgregar", ingredientes);
		
		return new ModelAndView(viewModel, "editar.hbs");
	}
	
	public ModelAndView modificadoDeIngrediente(Request request, Response response) {

		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));

		HashMap<String, Object> viewModel = new HashMap<>();

		viewModel.put("receta", receta);
		
		viewModel.put("id", Long.parseLong(request.params("id")));
		
		return new ModelAndView(viewModel, "editIngredientes.hbs");
	}
	
	public ModelAndView modificadoDeCondimento(Request request, Response response) {

		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));

		HashMap<String, Object> viewModel = new HashMap<>();

		viewModel.put("receta", receta);
		viewModel.put("id", Long.parseLong(request.params("id")));
		
		return new ModelAndView(viewModel, "editCondimento.hbs");
	}
		


	public Void modificarIngrediente(Request request, Response response) {
		
		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));
		String nombreIngrediente = request.queryParams("ingrediente");
		String dosis = request.queryParams("dosising");
		String ingEliminar = request
				.queryParams("ingredienteSeleccionado");
		
		withTransaction(()->{	
		Ingrediente ingNuevo=null;
	
		if(!((Objects.isNull(nombreIngrediente)||nombreIngrediente.isEmpty())&&
				(Objects.isNull(dosis)||dosis.isEmpty()))){
			ingNuevo= Ingrediente.nuevoIngrediente(nombreIngrediente, Float.parseFloat(dosis));
			receta.agregarIngrediente(usuarioActual(), ingNuevo);
		}
			
		if(!(Objects.isNull(ingEliminar)||ingEliminar.isEmpty())) 
			receta.eliminarIngrediente(usuarioActual(), ingEliminar);
		
		});
		
		response.redirect("/receta/" + Long.parseLong(request.params("id")));
		
		return null;}

	public Void modificarCondimento(Request request, Response response) {
		
		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));
		String nombreCondimento = request.queryParams("condimento");
		String dosis = request.queryParams("dosiscond");
		String condEliminar = request
				.queryParams("condimentoSeleccionado");
		
		withTransaction(() -> {
		Ingrediente condNuevo= null;
		
		if(!((Objects.isNull(nombreCondimento)||nombreCondimento.isEmpty())&&
				(Objects.isNull(dosis)||dosis.isEmpty()))){
			condNuevo= Ingrediente.nuevoIngrediente(nombreCondimento, Float.parseFloat(dosis));
			receta.agregarCondimento(usuarioActual(), condNuevo);
		}
		
		if(!(Objects.isNull(condEliminar)||condEliminar.isEmpty()))
			receta.eliminarCondimento(usuarioActual(), condEliminar);
		});
		
		response.redirect("/receta/" + Long.parseLong(request.params("id")));
		
		return null;}
	

	public Void crear(Request request, Response response) {

		Receta receta = RepositorioDeRecetas.instance().buscar(
				Long.parseLong(request.params("id")));

		String nombre = request.queryParams("nombreDelPlato");
		String calorias = request.queryParams("calorias");
		String dificultad = request.queryParams("dificultad");
		String temporada = request.queryParams("temporada");
		String favorita = request.queryParams("fav");
		int totalCalorias;
		
		long usuarioId = Routes.usuarioActual.getId();
		Usuario usuario = RepositorioDeUsuarios.instance().buscar(usuarioId);
		
						
		if(Objects.isNull(nombre)||nombre.isEmpty()) 
			nombre=receta.getNombreDelPlato();
		
		if(Objects.isNull(calorias)||calorias.isEmpty()){
			totalCalorias=receta.getTotalCalorias();
		}
		else{
			totalCalorias=Integer.parseInt(calorias);
		}
		
		EncabezadoDeReceta encabezado=new EncabezadoDeReceta(nombre,Temporada.valueOf(temporada),Dificultad.valueOf(dificultad),totalCalorias);

		withTransaction(() -> {
			Receta recetaModificada;
			String preparacion = request.queryParams("preparacion");
			
			recetaModificada=receta.modificarEncabezado(usuario, encabezado);
						
			if(!(Objects.isNull(preparacion)||preparacion.isEmpty())){
				recetaModificada=recetaModificada.modificarPreparacion(usuarioActual(), preparacion);
			}
			
			
			if(!Objects.isNull(favorita)) 
				usuario.marcarFavorita(recetaModificada);
			else {
				if(usuario.esFavorita(recetaModificada)) 
					usuario.borrarFavorita(recetaModificada);
			}

		});
		
		response.redirect("/receta/" + Long.parseLong(request.params("id")));
		return null;
	}
	
	public Usuario usuarioActual(){
		long usuarioId = Routes.usuarioActual.getId();
		return RepositorioDeUsuarios.instance().buscar(usuarioId);
	}

}
