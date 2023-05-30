package main;

import java.util.List;

import entidades.Equipo;
import entidades.Jugador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

public class GestionEquipo {
//Ni atributos ni métodos estáticos.
	private EntityManager em=null;
	public GestionEquipo() {
		EntityManagerFactory factory=Persistence.createEntityManagerFactory("gestionEquipos");
		em=factory.createEntityManager();
	}
	public boolean crearEquipo(Equipo equipo) {
		boolean exito=true;
		try {
			EntityTransaction tran=em.getTransaction();
			tran.begin();
			em.persist(equipo);
			tran.commit();	
		}catch(Exception ex){
			exito=false;
		}
		return exito;
	}
	
	public boolean crearJugador(int idEquipo, Jugador jugador) {
		boolean exito=true;
		EntityTransaction tran=null;
		//Extraemos de la base de datos el objeto Equipo que coincida con el idEspecificado.
		Equipo equipo=em.find(Equipo.class, idEquipo);
		try {
			if(equipo!=null) {
				//Introducimos al jugador en el equipo
				tran=em.getTransaction();
				tran.begin();
				//Si el equipo ha sido encontrado:
				List<Jugador>jugadores=equipo.getJugadores();
				jugadores.add(jugador);
				tran.commit();
			}else {
				exito=false;
			}	
		}catch(Exception ex){
			exito=false;
		}
		return exito;
	}
	public boolean traspaso(int idOrigen,int idDestino, Jugador jugador) {
		EntityTransaction tran=null;
		boolean exito=true;
		//Extraemos de la base de datos los objetos equipo origen y destino
		try {
			Equipo equipoOrigen=em.find(Equipo.class, idOrigen);
			Equipo equipoDestino=em.find(Equipo.class, idDestino);
			
			if(equipoOrigen!=null&&equipoDestino!=null) {
				//solo podemos realizar el traspaso si ambos equipos existen.
				tran=em.getTransaction();
				tran.begin();
				List<Jugador> jugadoresOrigen=equipoOrigen.getJugadores();
				List<Jugador> jugadoresDestino=equipoDestino.getJugadores();
				
				exito=jugadoresOrigen.remove(jugador);
				if(!jugadoresDestino.contains(jugador)) {
					jugadoresDestino.add(jugador);			
				}else {
					exito=false;
				}
				tran.commit();
			}else {
				exito=false;
			}
		}catch(Exception ex){
			exito=false;
		}
	}
	
	public List<Jugador> buscarNombre(String nombre){
		String jpql="SELECT j FROM Jugador j where j.nombre LIKE :nombreJugador";
		Query query=em.createQuery(jpql);
		query.setParameter("nombreJugador", "%"+nombre+"%");
		List<Jugador> jugadores=query.getResultList();
		return jugadores;
	}
	public List<Jugador> filtroJugadores(int edadMin, int edadMax, String posición){
		String jpql="SELECT j FROM Jugador j where j.edad>=?1 and j.edad<=?2 and j.posicion=?3";
		Query query=em.createQuery(jpql);
		query.setParameter(1, edadMin);
		query.setParameter(2, edadMax);
		query.setParameter(3, posición);
		List<Jugador> jugadores=query.getResultList();
		return jugadores;
	}
	public boolean borrarJugador(String dni) {
		String jpql="DELETE FROM Jugador j where j.dni='"+dni+"'";
		Query query=em.createQuery(jpql);
		/*Dado que voy a modificar el contenido de la base de datos (voy a eliminar, presuntamente, un registro),
		 debo utilizar una transacción*/
		
		EntityTransaction tran=em.getTransaction();
		tran.begin();
		int valores=query.executeUpdate();
		tran.commit();
		return valores==1?true:false;
	}
	
	public int numeroJugadoresEquipo(int idEquipo) {
		String jpql="SELECT e FROM Equipo e where id="+idEquipo;
		Query query=em.createQuery(jpql);
		//Extraemos al equipo de la base de datos
		Equipo e=(Equipo)query.getSingleResult();
		//extraemos la lista de jugadores y calculamos su tamaño.
		int numJugadores=e.getJugadores().size();
		return numJugadores;
	}
}
