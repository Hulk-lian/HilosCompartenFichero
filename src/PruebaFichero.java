import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class ControladorFichero{
	
	private PrintWriter fichero;//mas recomendable que el bufferedwirter
	private boolean terminado=true;
	
	public ControladorFichero(String nomFichero){	
		try {
			fichero= new PrintWriter(new FileWriter(nomFichero));
			
		} catch (IOException e) {
			System.err.println("Error al crear el fichero "+e.getMessage());
		}
	}

	public synchronized void heTerminado(){
		this.terminado=true;
		notifyAll();
	}
	
	public synchronized void println(String cadena){
		fichero.println(cadena);
	}
	public synchronized void print(String cadena) throws InterruptedException{
		
		while (!terminado){
			this.wait();
		}
		//terminado=false;
		fichero.print(cadena);
		
	}
	public void close(){
		fichero.close();
	}
	
}
class Escritor extends Thread{
	private ControladorFichero destino;
	private String contenido="";
	
	public Escritor(ControladorFichero fichero){
		this.destino=fichero;
	}
	public void fraseAdd(String cadena){
		this.contenido+=cadena;
	}
	@Override
	public void run(){
		//destino.println(contenido);// funciona porque println es una operacion atomica
		try {
		for(int i=0;i< contenido.length();i++){

				destino.print(contenido.substring(i,i+1));
			}
			destino.heTerminado();
			
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		destino.println("");
	}
}

public class PruebaFichero {
	public static void main(String[] args) {
		
		ControladorFichero cFichero;
		String ruta= "poema.txt";
		
		String parrafo1="¡Ser o no ser esa es la cuestion! ¿Qué debe más dignamente....?";
		String parrafo2="En un lugar de la mancha , de cuyo nombre no me quiero acordar";
		
		cFichero= new ControladorFichero(ruta);
		Escritor cervantes=new Escritor(cFichero);
		Escritor shakespeare= new Escritor(cFichero);
		
		shakespeare.fraseAdd(parrafo1);
		cervantes.fraseAdd(parrafo2);
		
		shakespeare.start();
		cervantes.start();
		
		try {
			shakespeare.join();
			cervantes.join();
			cFichero.close();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Todo correcto");
	}

}
