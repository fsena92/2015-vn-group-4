package grupo4.dds.repositorios;

import grupo4.dds.controller.Consulta;
import grupo4.dds.monitores.Monitor;
import grupo4.dds.receta.Receta;
import grupo4.dds.receta.Temporada;
import grupo4.dds.receta.busqueda.filtros.Filtro;
import grupo4.dds.receta.busqueda.postProcesamiento.PostProcesamiento;
import grupo4.dds.usuario.Usuario;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;

import queComemos.entrega3.dominio.Dificultad;

import java.util.Objects;


public class RepositorioDeRecetas extends Repositorio<Receta> implements WithGlobalEntityManager {

	private static final RepositorioDeRecetas self = new RepositorioDeRecetas();
	
	public static RepositorioDeRecetas instance() {
		return self;
	}

	public RepositorioDeRecetas() {
		elementType = Receta.class;
	}
	
	 public List<Receta> listar() {
		 return entityManager().createQuery("from Receta", Receta.class).getResultList();
	 }
	
	/* Servicios */

	public List<Receta> listarRecetasPara(Usuario usuario, List<Filtro> filtros, PostProcesamiento postProcesamiento) {
		
		Stream<Receta> stream = recetasQuePuedeVer(usuario);
		
		if(filtros != null)
			for (Filtro filtro : filtros)
				stream = stream.filter(r -> filtro.test(usuario, r));
		
		List<Receta> recetasFiltradas = stream.collect(Collectors.toList());
		List<Receta> consulta;
		
		if (postProcesamiento == null) 
			consulta = recetasFiltradas;
		else
			consulta = postProcesamiento.procesar(recetasFiltradas);

		notificarATodos(usuario, consulta, filtros);
		
		usuario.consulto(consulta);

		return consulta;
	}
	
	public void notificarATodos(Usuario usuario, List<Receta> consulta, List<Filtro> filtros) {
		monitores().forEach(monitor -> monitor.notificarConsulta(usuario, consulta, filtros));
	}
	
	public List<Monitor> monitores() {
		return entityManager().createQuery("from Monitor", Monitor.class).getResultList();
	}
	
	public Receta buscar(long id) {
		 return entityManager().find(Receta.class, id);
	}

	public List<Receta> buscar(String nombreReceta) {
		return entityManager().createQuery("from Receta where nombreDelPlato like '%" + nombreReceta + "%'", Receta.class).getResultList();
	}
	
	
	/* Servicios privados */
	
	private Stream<Receta> recetasQuePuedeVer(Usuario usuario) {
		
		HashSet<Receta> todasLasRecetas = new HashSet<>(this.list());
		todasLasRecetas.addAll(RepositorioRecetasExterno.get().getRecetas());
		
		return todasLasRecetas.stream().filter(r -> usuario.puedeVer(r));
	}
	
	/* Accesors and Mutators */
	
	public void agregarMonitor(Monitor monitor) {
		entityManager().persist(monitor);
	}
	
	public void removerMonitor(Monitor monitor) {
		entityManager().remove(monitor);
	}
	
	   public List<Receta> buscarPorFiltros(Consulta consulta){
			String query="from Receta where id=id";
				
			if (!(Objects.isNull(consulta.getNombre())||consulta.getNombre().isEmpty())){
				query=query+" AND nombreDelPlato like '%" + consulta.getNombre() + "%'";
			}
			
			if(!((Objects.isNull(consulta.getDesde())||Objects.isNull(consulta.getHasta()))||(consulta.getDesde().isEmpty()||consulta.getHasta().isEmpty()))){
				query=query+" AND totalCalorias between "+ Integer.parseInt(consulta.getDesde()) + " AND " + Integer.parseInt(consulta.getHasta());
			}
			
			if(!(Objects.isNull(consulta.getDificultad())||consulta.getDificultad().isEmpty())){
				query=query+" AND  dificultad = " + Dificultad.valueOf(consulta.getDificultad()).ordinal();
			}
			
			if(!(Objects.isNull(consulta.getTemporada())||consulta.getTemporada().isEmpty())){
				query=query+" AND temporada = " + Temporada.valueOf(consulta.getTemporada()).ordinal();
			}
		
			return entityManager().createQuery(query,Receta.class).getResultList();	
		}
	
}
