package repasexamen2;
//HACER POCO A POCO E IR COMPROBANDO !!!

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class RepasExamen2 {
    /*
     Modificación del anterior:debemos crear un fichero serializado que tenga
     objetos con atributos "codigo","nombre","grasa total"
    
     Los objetos se pillan del xml dado y de la tabla dada
    
     a través de los datos de ese xml deberemos acceder al resto de info
    
     NO ARRAYS
    
     CERRAR STREAMS !!!!!!
     */
    
    public static Connection conexion = null;
    
    public static Connection getConexion() throws SQLException {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost";
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
        conexion = DriverManager.getConnection(ulrjdbc);
        return conexion;
    }
    
    public static void closeConexion() throws SQLException {
        conexion.close();
    }
    
    public static void crearSerializado() throws FileNotFoundException, XMLStreamException, IOException, SQLException {

        //CREAMOS EL SERIALIZADO
        FileOutputStream fichR = new FileOutputStream("/home/oracle/Desktop/ExamenRepaso2/platosSerializados.txt");
        ObjectOutputStream fichrOOS = new ObjectOutputStream(fichR);

        //ACCESO AL XML
        File fich1 = new File("/home/oracle/Desktop/ExamenRepaso2/platos.xml");
        
        FileReader fich1FR = new FileReader(fich1);
        
        XMLInputFactory xmlIF = XMLInputFactory.newInstance();
        XMLStreamReader xmlSR = xmlIF.createXMLStreamReader(fich1FR);

        //DATOS A ESCRIBIR
        String codp = null;
        String nombreP = null;
        int grasa = 0;
        int grasaTotal = 0;

        //DATOS A RECOGER
        String codc = "";
        int peso = 0;
        
        while (xmlSR.hasNext()) {

            //data del xml
            int tipoE = 0;
            String localName = "";
            
            tipoE = xmlSR.getEventType();
            
            if (tipoE == XMLStreamConstants.START_ELEMENT) {
                
                localName = xmlSR.getLocalName();
                
                if (localName.equals("Plato")) {
                    //OBTENEMOS CODP
                    codp = xmlSR.getAttributeValue(0);
                    
                } else if (localName.equals("nomep")) {
                    //OBTENEMOS NOMBREP
                    nombreP = xmlSR.getElementText();
                    
                }
                
            }

            //COMPROBAMOS SI YA ESTÁN PUESTAS TODAS LAS VARIABLES DEL XML
            //SI ES EL CASO, COMENZAMOS A BUSCAR LAS QUE QUEDAN PARA CREAR
            //EL OBJETO
            if ((codp != null) && (nombreP != null)) {

                //ACCESO AL FICHERO DELIMITADO:
                //OJO!! TENEMOS QUE LEER TODO EL FICHERO PARA CADA PLATO, POR ESO HACE FALTA CREAR UN BUFFEREDREADER
                //CADA VEZ QUE ENTRAMOS EN ESTE IF Y EN ESTE BUCLE !!!
                FileReader readFile1 = new FileReader("/home/oracle/Desktop/ExamenRepaso2/composicion.txt");
                BufferedReader bufferRead1 = new BufferedReader(readFile1);
                
                String[] arrayDatos;
                String linea;
                
                while ((linea = bufferRead1.readLine()) != null) {
                    
                    arrayDatos = linea.split("#");
                    //OJO, ESTO SOLO ES LA LÍNEA LEÍDA
                    //System.out.println("LÍNEA LEÍDA: [" + arrayDatos[0] + arrayDatos[1] + arrayDatos[2] + "]");

                    if (arrayDatos[0].equals(codp)) {
                        
                        codc = arrayDatos[1];
                        peso = Integer.parseInt(arrayDatos[2]);
                        
                        System.out.println(codp + " " + nombreP + " " + codc + " " + peso);

                        //YA TENEMOS CODP,NOMBRE, CODC Y Y PESO 
                        //OJO, EN CONPOSICIÓN LA PRIMARYKEY SE COMPONE DE CODP Y CODC
                        //ACCESO A LA TABLA COMPONENTES:
                        PreparedStatement psm1 = conexion.prepareStatement("select graxa from componentes where codc = ?");
                        
                        psm1.setString(1, codc);
                        
                        ResultSet rs1 = psm1.executeQuery();
                        
                        rs1.next();
                        //VAMOS ACUMULANDO LA GRASA TOTAL
                        grasa = rs1.getInt(1);
                        
                        grasaTotal += (grasa * peso) / 100;
                        
                    }
                    //UNA VEZ ACABADO CON ESTE CODP, HAY QUE COMPROBAR SI SIGUE HABIENDO LÍNEAS EN EL FICHERO
                    //QUE TENGAN ESE MISMO CODP

                }

                //COMPROBACIÓN FINAL
                System.out.println("FINAL: " + codp + " " + nombreP + " " + " " + codc + " " + peso + " " + grasaTotal);

                //CUANDO ACABEMOS DE LEER TODO LO RELACIONADO CON ESE CODP, AÑADIMOS LOS OBJETOS AL FICHERO SERIALIZADO
                Platos objP = new Platos(codp, nombreP, grasaTotal);
                System.out.println(objP);
                
                fichrOOS.writeObject(objP);
                //REINICIAR LAS VARIABLES UNA VEZ ACABEMOS DE LEER TODO UN CODP
                codp = null;
                nombreP = null;
                
                bufferRead1.close();
                readFile1.close();
            }
            
            xmlSR.next();
            
        }
        //PARA PODER LEER EL SERIALIZADO, METEMOS UN NULL AL FINAL
        
        fichrOOS.writeObject(null);
        
        fich1FR.close();
        fichR.close();
        fichrOOS.close();
        
    }
    
    public static void main(String[] args) throws XMLStreamException, IOException, ClassNotFoundException {
        
        try {
            RepasExamen2.getConexion();
            
            RepasExamen2.crearSerializado();
            
        } catch (SQLException ex) {
            Logger.getLogger(RepasExamen2.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Leemos el serializado para comprobar que todo está bien:
        FileInputStream fichL = new FileInputStream("/home/oracle/Desktop/ExamenRepaso2/platosSerializados.txt");
        ObjectInputStream fichlOIS = new ObjectInputStream(fichL);
        
        Object contenido = 0;
        
        while (contenido != null) {
            
            if (contenido == null) {
                
                System.out.println("fin");
                
            } else {
                
                System.out.println("LECUTRA DEL SERIALIZADO:");
                contenido = fichlOIS.readObject();
                System.out.println(contenido);
                
            }
        }
        
        fichlOIS.close();
        fichL.close();
    }
    
}
