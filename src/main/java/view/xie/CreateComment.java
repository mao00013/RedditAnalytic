/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.xie;

import entity.Comment;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;

/**
 *
 * @author Lu Xie
 */
@WebServlet(name = "CreateComment", urlPatterns = { "/CreateComment" } )
public class CreateComment extends HttpServlet {
    private String errorMessage = null;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Comment</title>" );
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<form method=\"post\">" );
            out.println( "Text:<br>" );
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.TEXT );
            out.println( "<br>" );
            out.println( "Cteated:YYYY-MM-DD hh:mm:ss<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.CREATED );
            out.println( "<br>" );
            out.println( "Points:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.POINTS );
            out.println( "<br>" );
            out.println( "Replys:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.REPLYS );
            out.println( "<br>" );
            out.println( "IsReply:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.ISREPLY );
            out.println( "<br>" );
            out.println( "UniqueID:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.UNIQUEID );
            out.println( "<br>" );
            out.println( "PostID:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.POST_ID );
            out.println( "<br>" );
            out.println( "RedditAccountID:<br>" );
            out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", CommentLogic.REDDIT_ACCOUNT_ID );
            out.println( "<br>" );
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if( errorMessage != null && !errorMessage.isEmpty() ){
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }

    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will create a host this method simple
     * delivers the html code. creation will be done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    static int connectionCount = 0;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        log( "POST: Connection=" + connectionCount );
//        if( connectionCount < 3 ){
//            connectionCount++;
//            try {
//                TimeUnit.SECONDS.sleep( 60 );
//            } catch( InterruptedException ex ) {
//                Logger.getLogger( CreateComment.class.getName() ).log( Level.SEVERE, null, ex );
//            }
//        }
        CommentLogic cLogic = LogicFactory.getFor( "Comment" );
        String uniqueId = request.getParameter( CommentLogic.UNIQUEID );
        if( cLogic.getCommentWithUniqueId( uniqueId ) == null ){
            try {
                Comment comment = cLogic.createEntity( request.getParameterMap() );
                //create the two logics for post and reddit account
                //get the entities from logic using getWithId
                //set the entities on your comment object before adding comment to db
            
                PostLogic postLogic = LogicFactory.getFor("Post");
                RedditAccountLogic redditAccountLogic = LogicFactory.getFor("RedditAccount");
                comment.setPostId(postLogic.getWithId(Integer.valueOf(request.getParameter(CommentLogic.POST_ID))));
                comment.setRedditAccountId(redditAccountLogic.getWithId(Integer.valueOf(request.getParameter(CommentLogic.REDDIT_ACCOUNT_ID))));
               cLogic.add( comment );
            } catch( Exception ex ) {
                log("",ex);
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "UniqueId: \"" + uniqueId + "\" already exists";
        }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "CommentTable" );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Comment Entity";
    }

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
