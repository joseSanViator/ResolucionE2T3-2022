package main;

import java.util.Iterator;
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
		jugador.setEquipo(equipo);
		try {	
				tran=em.getTransaction();
				tran.begin();
				em.persist(jugador);
				tran.commit();
		}catch(Exception ex){
			exito=false;
			System.out.println(ex.toString());
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
			
			//Traigo el objeto jugador de la base de datos.
			Jugador j=em.find(Jugador.class, jugador.getDni());
			
			if(equipoOrigen!=null&&equipoDestino!=null) {
				//solo podemos realizar el traspaso si ambos equipos existen.
				tran=em.getTransaction();
				tran.begin();
				j.setEquipo(equipoDestino);
				tran.commit();
			}else {
				exito=false;
			}
			
		}catch(Exception ex){
			exito=false;
			System.out.println(ex.toString());
		}
		return exito;
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
	
	public double mediaEdadEquipo(int idEquipo) {
		double media=0;
		String jpql="SELECT avg(j.edad) FROM Jugador j WHERE j.equipo.id='"+idEquipo+"'";
		Query query=em.createQuery(jpql);
		//Devolvemos un único resultado que, por defecto es un Object, y lo casteamos al tipo adecuado (duble)
		media=(double)query.getSingleResult();
		return media;
	}
	public List<Equipo> equiposDivision(String division){
		String jpql="SELECT e FROM Equipo e WHERE e.division=?1 AND e.nombre!=?2";
		Query query=em.createQuery(jpql);
		query.setParameter(1, division);
		//JugadoresMercadoPrimera
		division=division.toLowerCase();
		query.setParameter(2, "JugadoresMercado"
		+Character.toUpperCase(division.charAt(0))+division.substring(1, division.length()-1));
		List<Equipo> equipos=query.getResultList();
		return equipos;
	}
	
	public void imprimirEquipos(List<Equipo> equipos) {
		
		for(Equipo equipo:equipos) {
				System.out.println(equipo);
				List<Jugador> jugadores=equipo.getJugadores();
				Iterator<Jugador> iteradorJugadores=jugadores.iterator();
				while(iteradorJugadores.hasNext()) {
					System.out.println(iteradorJugadores.next());
				}
		}
	}
	public boolean borrarEquipo(int idEquipo) {
	boolean exito=true;
		try {
		/*extraer de la base de datos el equipo con id=idEquipo
		  Podemos extraer de la BD el equipo con este método dado que buscamos por id, si buscaramos por otro campo
		  esto no sería posible y nos veríamos relegados a usar JPQL*/
		Equipo equipo=em.find(Equipo.class, idEquipo);
		
		//Extraer de la base de de datos el equipo JugadoresMercadoDivision, donde division es la del equipo a borrar.
		String jpql="SELECT e FROM Equipo e where e.nombre=?1";
		Query query=em.createQuery(jpql);
		String division=equipo.getDivision();
		query.setParameter(1, "JugadoresMercado"
		+Character.toUpperCase(division.charAt(0))+division.substring(1, division.length()-1));
		
		Equipo equipoAgentesLibres=null;
		//En el equipo equipoAgentesLibres voy a almacenar a todos los jugadores del equipo que voy a eliminar.
		List<Equipo> listadoEquipos=query.getResultList();
		System.out.println(listadoEquipos.size());
		if(listadoEquipos.size()==0) {
			System.out.println("Agentes es NULL");
			String nombre="JugadoresMercado"
					+Character.toUpperCase(division.charAt(0))+division.substring(1, division.length()-1);
			Equipo e=new Equipo(nombre,division);
			crearEquipo(e);
		}else {
			equipoAgentesLibres=listadoEquipos.get(0);	
		}
		
		List<Jugador> jugadores=equipo.getJugadores();
		//Cada jugador del equipo voy a traspasarlo al equipoAgentesLibres
		
		for(Jugador jugador:jugadores) {
			traspaso(equipo.getId(),equipoAgentesLibres.getId(),jugador);
		}
		
		//Llegados a este punto, el equipo que queremos borrar debería tener 0 jugadores
		EntityTransaction tran=em.getTransaction();
		tran.begin();
		em.remove(equipo);
		tran.commit();
		
		}catch(Exception ex) {
			exito=false;
			ex.printStackTrace();
		}
		return exito;
	}
}
