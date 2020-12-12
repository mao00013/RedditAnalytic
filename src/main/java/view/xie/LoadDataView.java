/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.xie;

import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.SubredditLogic;

/**
 *
 * @author Lu
 */
@WebServlet(name = "LoadDataView", urlPatterns = { "/LoadDataView" } )
public class LoadDataView extends HttpServlet{
        /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Load Data View</title>");    
            out.println("</head>");
              
            out.println("<body>");
            
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form action=\"LoadData\" method=\"post\">" );
            out.println( "Subreddit Name:<br><br>" );
            out.println( "<input type='text' name=\"%s\" value=\"Subreddit Name\"><br><br>" );
            out.println( "<input type=\"submit\" name=\"searchbyname\" value=\"Search by name\"><br><br>" );
            
            out.println( "Subreddit Name List:<br><br>" );
            out.println( "<select name=\"sub\">" );
            out.println( "<option value=\"volvo\">Volvo</option>" );
            out.println( "<option value=\"saab\">Saab</option>" );
            out.println( "<option value=\"opel\">Opel</option>" );
            out.println( "<option value=\"audi\">Audi</option>" );
            out.println( "</select><br><br>" );
           
            out.println( "</form>" );
            
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
        return builder.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log( "POST" );
        SubredditLogic logic = LogicFactory.getFor( "SubredditTable" );
        Subreddit subreddit = logic.updateEntity( request.getParameterMap() );
        logic.update( subreddit );
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    private static final boolean DEBUG = true;
    
    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
    
      private void fillTableData( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "entities", extractTableData( req ) );
        req.setAttribute( "request", toStringMap( req.getParameterMap() ) );
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        req.getRequestDispatcher( "/jsp/ShowTable-Account.jsp" ).forward( req, resp );
    }
    private List<?> extractTableData( HttpServletRequest req ) {
    String search = req.getParameter( "searchbyname" );
    SubredditLogic logic = LogicFactory.getFor( "Subreddit" );
//    req.setAttribute( "columnName", logic.getColumnNames() );
//    req.setAttribute( "columnCode", logic.getColumnCodes() );
    List<Subreddit> list;
    if( search != null ){
        list = logic.search( search );
    } else {
        list = logic.getAll();
    }
    if( list == null || list.isEmpty() ){
        return Collections.emptyList();
    }
    return appendDatatoNewList( list, logic::extractDataAsList );
}
      private <T> List<?> appendDatatoNewList( List<T> list, Function<T, List<?>> toArray ) {
        List<List<?>> newlist = new ArrayList<>( list.size() );
        list.forEach( i -> newlist.add( toArray.apply( i ) ) );
        return newlist;
    }

}
