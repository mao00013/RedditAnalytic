
/**
 * @author XU LENG 
 * @time   3rd Dec 2020
 */
package view.leng;

import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.RedditAccountLogic;
import logic.LogicFactory;


@WebServlet( name = "RedditAccountTable", urlPatterns = { "/RedditAccountTable" } )
public class RedditAccountTableView extends HttpServlet {

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
            out.println("<title>RedditAccountTableView</title>");            
            out.println("</head>");
            
            
            out.println("<body>");
            out.println( "<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">" );
            out.println( "<caption>Reddit Account</caption>" );
            RedditAccountLogic logic = LogicFactory.getFor("RedditAccount");
            out.println( "<tr>" );
            for(int i=0; i< logic.getColumnNames().size();i++)
            {
                out.println("<th>"+logic.getColumnNames().get(i)+"</th>");
            }
            out.println( "</tr>" );
            
            List<RedditAccount> entities = logic.getAll();
            for( RedditAccount e: entities ) {
                //for other tables replace the code bellow with
                //extractDataAsList in a loop to fill the data.
//                out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
//                        logic.extractDataAsList( e ).toArray() );
                out.println( "<tr>" );
                    for (int i = 0 ; i<logic.extractDataAsList(e).size();i++)
                    {
                        out.println( "<td>"+logic.extractDataAsList(e).get(i)+"</td>" );
                    }
                out.println( "</tr>" );
            }

            
                
            out.println( "<tr>" );
            for(int i=0; i< logic.getColumnCodes().size();i++)
            {
                out.println("<th>"+logic.getColumnCodes().get(i)+"</th>");
            }
            out.println( "</tr>" );
            out.println( "</table>" );
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
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
        RedditAccountLogic logic = LogicFactory.getFor( "RedditAccount" );
        RedditAccount account = logic.updateEntity( request.getParameterMap() );
        logic.update( account );
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Reddit Account Information";
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

}
