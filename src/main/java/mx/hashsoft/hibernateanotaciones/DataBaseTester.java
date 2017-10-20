/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.hashsoft.hibernateanotaciones;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 *
 * @author David
 */
public class DataBaseTester {

    static public void main(String[] args) {
        try {
            DataBaseTester dataBaseTester = new DataBaseTester();
            Connection cx = dataBaseTester.crearConexion();
            ArrayList<Class> clases = dataBaseTester.clasesVerificar();
            Boolean resultadoTablas=true, resultadoCampos=true;
                        
            for(Class clase : clases) {
                // Obtenemos la anotacion Tabla de la clase
                Table tabla = (Table) clase.getAnnotation(Table.class);
                
                // Obtenemos el nombre de la clase SIN el esquema
                int punto = tabla.name().indexOf(".");
                String nombreTabla = tabla.name().substring(punto +1);
                
                // Buscamos si la tabla existe
                Boolean tablaOK = dataBaseTester.existeTabla(cx, nombreTabla);                
                resultadoTablas = resultadoTablas && tablaOK;
                
                if(tablaOK) {
                    // Obtenemos todos los campos de la clase indicada
                    Field[] fields = clase.getDeclaredFields();
                    Boolean camposOK = dataBaseTester.existenColumnas(cx, nombreTabla, fields);
                    
                    resultadoCampos = resultadoCampos && camposOK;
                }                
            }
            
            System.out.println("\n");
            if((resultadoTablas && resultadoCampos) == true) {
                System.out.println("La base de datos contiene todas las tablas y campos necesarios");
            } else {
                System.out.println("FALTAN TABLAS Y/O CAMPOS VALIDE CON LA SALIDA");
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error al conectar a dB");
            e.printStackTrace();
        }
    }    
    
    /**
     * Verifica que la tabla indicada 
     * @param conexion Conexion a base de datos
     * @param nombreTabla Nombre de la tabla que deseamos verificar
     * @return True si la tabla existe, false en caso contrario
     */
    Boolean existeTabla(Connection conexion, String nombreTabla) throws SQLException {
        Boolean existe = false;

        // Obtenemos los metadatos de la conexion
        DatabaseMetaData metadata = conexion.getMetaData();
        // Buscamos la tabla con el nombre indicado
        ResultSet tabla = metadata.getTables(null, null, nombreTabla, null);
        
        // Para evitar falsos positivos buscamos que el nombre EXACTO exista
        while(tabla.next() == true) {
            String nombre = tabla.getString("TABLE_NAME");
                        
            if(nombre.compareTo(nombreTabla) == 0) {
                System.out.println("EXISTE tabla " + nombreTabla);
                existe = true;
            }
        }
        
        if(existe == false) {
            System.out.println("NO EXISTE tabla " + nombreTabla);
        }
        
        return existe;
    }    
    
    /**
     * Verificamos que las columnas de la tabla indicadas en la clase existan
     * @param conexion Conexion a base de datos
     * @param nombreTabla Nombre de la tabla
     * @param campos Campos que deseamos verificar en la tabla
     * @return True si todos los campos existen, false si hay faltantes
     */
    public Boolean existenColumnas(Connection conexion, String nombreTabla, Field[] campos) throws SQLException {
        Boolean existen = true;
        // Metadatos d ela conexion
        DatabaseMetaData metadata = conexion.getMetaData();
        
        // Buscamos cada campo del arreglo
        for(Field campo: campos) {
            // Obtenemos la anotacion Column
            Column columna = campo.getAnnotation(Column.class);
            ResultSet data = metadata.getColumns(null, null, nombreTabla, columna.name());
            Boolean campoExacto = false;
            
            // Para evitar falsos positivos buscamos el nombre EXACTO de la columna
            while(data.next() == true) {
                String nombreCampo = data.getString("COLUMN_NAME");
                
                if(nombreCampo.compareTo(columna.name()) == 0) {
                    System.out.println("\tEXISTE el campo " + nombreTabla + "." + columna.name());
                    campoExacto = true;
                }
            }
            
            if(campoExacto == false) {
                System.out.println("\tNO EXISTE el campo " + nombreTabla + "." + columna.name());
            }
            
            existen = existen && campoExacto;
        }        
        return existen;
    }
    
    /**
     * Creamos la conexion a base de datos
     * @return Conexion a base de datos
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public Connection crearConexion() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + "?" + "user=" + "tester" + "&" + "password=" + "primeTester" + "");

        return conexion;
    }

    /**
     * Regresa la lista de clases VO que deseamos verificar
     * @return Lista de clases
     */
    public ArrayList<Class> clasesVerificar() {
        ArrayList<Class> clases = new ArrayList<>();

        // Obtenemos el objecto Class de las clases
        Class<FoliosVO> foliosVO = FoliosVO.class;
        Class<ReplicaVO> replicaVO = ReplicaVO.class;
        
        // Agregamos las clases al arreglo
        clases.add(foliosVO);
        clases.add(replicaVO);
        
        return clases;
    }
}
