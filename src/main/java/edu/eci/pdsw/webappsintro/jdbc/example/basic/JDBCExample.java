/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            mostrarTablas(con);
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=2105700;
            registrarNuevoProducto(con, suCodigoECI, "SU NOMBRE", 99999999);            
            con.commit();
                        
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
        String statement = "INSERT INTO ORD_PRODUCTOS (codigo, nombre, precio)"
                + "VALUES(?, ?, ?)";
        PreparedStatement regProducto = con.prepareStatement(statement);
        regProducto.setInt(1, codigo);
        regProducto.setString(2, nombre);
        regProducto.setInt(3, precio);
        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException{
        List<String> np=new LinkedList<>();
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla
        String statement = "SELECT ord.nombre, ord.codigo FROM ORD_PRODUCTOS ord WHERE ord.codigo = ?";
        PreparedStatement x = con.prepareStatement(statement);
        x.setInt(1, codigoPedido);
        ResultSet y = x.executeQuery();
        while(y.next()){
            np.add(y.getString("nombre"));
        }
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException{
        
        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultado del ResultSet
        String statement = "SELECT SUM(ord.cantidad * orp.precio) AS sumaProductos "
                + "FROM ORD_PRODUCTOS AS orp JOIN ORD_DETALLES_PEDIDO AS ord ON (ord.pedido_fk = orp.codigo) WHERE ord.pedido_fk = ?";
        PreparedStatement x = con.prepareStatement(statement);
        x.setInt(1, codigoPedido);
        ResultSet y = x.executeQuery();
        
        if(!y.first())return 123;
        return y.getInt("sumaProductos");
    }
    
    public static void mostrarTablas(Connection con) throws SQLException{
        String statement = "SELECT orp.*, ord.* FROM ORD_PRODUCTOS AS orp JOIN ORD_DETALLES_PEDIDO AS ord "
                + "ON (ord.pedido_fk = orp.codigo)";
        PreparedStatement x = con.prepareStatement(statement);
        ResultSet y = x.executeQuery();
        while(y.next()){
            System.out.println(y.getString("nombre") + " " + y.getInt("precio") + " " + y.getInt("cantidad") + " " + y.getInt("pedido_fk"));
        }
    }
}
