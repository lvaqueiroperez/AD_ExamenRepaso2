package repasexamen2;

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
    
     Los objetos se pillan del xml dado
    
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

    public static void crearSerializado() throws FileNotFoundException, XMLStreamException, IOException {

        //ACCESO AL XML
        File fich1 = new File("/home/oracle/Desktop/ExamenRepaso2/platos.xml");

        FileReader fich1FR = new FileReader(fich1);

        XMLInputFactory xmlIF = XMLInputFactory.newInstance();
        XMLStreamReader xmlSR = xmlIF.createXMLStreamReader(fich1FR);

        //DATOS A ESCRIBIR
        String codp = "";
        String nombreP = "";
        int grasaTotal = 0;

        while (xmlSR.hasNext()) {

            //DATOS A RECOGER
            String codc = "";
            int peso = 0;

            //data del xml
            int tipoE = 0;
            String localName = "";
            tipoE = xmlSR.getEventType();

            if (tipoE == XMLStreamConstants.START_ELEMENT) {

                localName = xmlSR.getLocalName();

                if (localName.equals("Plato")) {

                    codp = xmlSR.getAttributeValue(0);

                } else if (localName.equals("nomep")) {

                    nombreP = xmlSR.getElementText();

                }

            }

            //COMPROBAMOS SI YA ESTÁN PUESTAS TODAS LAS VARIABLES DEL XML
            //SI ES EL CASO, COMENZAMOS A BUSCAR LAS QUE QUEDAN PARA CREAR
            //EL OBJETO
            if ((codp != "") && (nombreP != "")) {

                //ACCESO AL FICHERO DELIMITADO:
                //variables a recoger
                FileReader readFile1 = new FileReader("/home/oracle/Desktop/ExamenRepaso2/composicion.txt");
                BufferedReader bufferRead1 = new BufferedReader(readFile1);

                String[] arrayDatos;
                String linea;

                while ((linea = bufferRead1.readLine()) != null) {

                    arrayDatos = linea.split("#");

                    for (int i = 0; i < arrayDatos.length; i++) {

                        if (arrayDatos[0].equals(codp)) {

                            codc = arrayDatos[1];
                            peso = Integer.parseInt(arrayDatos[2]);

                            //YA TENEMOS CODP,CODC Y Y PESO 
                            //OJO, EN CONPOSICIÓN LA PRIMARYKEY SE COMPONE DE CODP Y CODC
                            
                            //ACCESO A LA TABLA COMPONENTES
                            
                            
                            
                        }

                    }

                }

            }

            xmlSR.next();

        }

    }

    public static void main(String[] args) {

        try {
            RepasExamen2.getConexion();

            RepasExamen2.closeConexion();

        } catch (SQLException ex) {
            Logger.getLogger(RepasExamen2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
