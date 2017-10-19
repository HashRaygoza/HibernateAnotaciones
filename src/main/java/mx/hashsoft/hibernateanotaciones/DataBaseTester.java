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
                Boolean tablaOK = dataBaseTester.existeTabla(cx, tabla.name());
                
                resultadoTablas = resultadoTablas && tablaOK;
                
                if(tablaOK) {
                    // Obtenemos todos los campos de la clase indicada
                    Field[] fields = clase.getDeclaredFields();
                    Boolean camposOK = dataBaseTester.existenColumnas(cx, tabla.name(), fields);
                    
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
    public Boolean existeTabla(Connection conexion, String nombreTabla) {
        Boolean existe = true;

        try {
            DatabaseMetaData metadata = conexion.getMetaData();
            ResultSet tabla = metadata.getTables(null, null, nombreTabla, null);

            existe = tabla.next();
            
            if (existe == false) {
                System.out.println("\nLa tabla " + nombreTabla + " NO existe");
            } else {
                System.out.println("\nLa tabla " + nombreTabla + " SI existe");
            }

        } catch (SQLException ex) {
            System.out.println("Error de conexion a DB");
            ex.printStackTrace();
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
    public Boolean existenColumnas(Connection conexion, String nombreTabla, Field[] campos) {
        ResultSet data;
        Boolean existen = true;
        DatabaseMetaData metadata;

        try {
            metadata = conexion.getMetaData();

            for (Field campo : campos) {
                // Obtenemos la anotacion Column de cada uno de los campos
                Column columna = campo.getAnnotation(Column.class);
                data = metadata.getColumns(null, null, nombreTabla, columna.name());

                if (data.next() == true) {
                    existen = existen && true;
                    System.out.println("\tEXISTE el campo " + nombreTabla + "." + columna.name());
                } else {
                    existen = existen && false;
                    System.out.println("\tNO EXISTE el campo " + nombreTabla + "." + columna.name());
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR DE CONEXION A DB");
            e.printStackTrace();
            return false;
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
