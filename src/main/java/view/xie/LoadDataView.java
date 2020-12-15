/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.xie;

import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;
import logic.SubredditLogic;
import reddit.DeveloperAccount;
import reddit.wrapper.AccountWrapper;
import reddit.wrapper.CommentSort;
import reddit.wrapper.CommentWrapper;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;
import reddit.wrapper.SubredditWrapper;

/**
 *
 * @author Lu
 */
@WebServlet(name = "LoadDataView", urlPatterns = {"/LoadDataView"})
public class LoadDataView extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Load Data View</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form action=\"LoadDataView\" method=\"post\">");
            out.println("Subreddit Name:    ");
            out.println("<input type='text' name=\"subText\" value=\"\"><br><br>");
            out.println("Subreddit :   ");
            out.println("<select name=\"subDrop\">");
            SubredditLogic logic = LogicFactory.getFor("Subreddit");
            List<Subreddit> entities = logic.getAll();
            for (Subreddit e : entities) {
                //for other tables replace the code bellow with
                //extractDataAsList in a loop to fill the data.
                out.printf("<option name=\"%s\">%s</option>", e.getName(), e.getName());
            }
            out.println("</select><br><br>");
            out.println("<input type=\"submit\" name=\"searchbyname\" value=\"Load\"><br><br>");
            out.println("</form>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        //2.Check button was pressed.
        if (request.getParameter("load") != null) {
            SubredditLogic sLogic = LogicFactory.getFor("Subreddit");
            PostLogic pLogic = LogicFactory.getFor("Post");
            CommentLogic cLogic = LogicFactory.getFor("Comment");
            RedditAccountLogic raLogic = LogicFactory.getFor("RedditAccount");
            //TODO fill in your reddit infromation here

            //dopost.1.Create RedditWrapper object 
            RedditWrapper scrap = new RedditWrapper();
            //1.authenticate using the DeceloperAccount
            //DeveloperAccount
            String clientID = "7aMfpfll8XJYng";
            String clientSecret ="FU5kB8SD1dHM_w4WwaOau8DMJxo4cw";
            String redditUser = "Federal_Lab_2202";
            String algonquinUser = "xie00055";

            DeveloperAccount dev = new DeveloperAccount()
                    .setClientID(clientID)
                    .setClientSecret(clientSecret)
                    .setRedditUser(redditUser)
                    .setAlgonquinUser(algonquinUser);

            //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
            scrap.authenticate(dev).setLogger(false);
            //get the name of subreddit
            scrap.configureCurentSubreddit(sLogic.getSubredditWithName(request.getParameter("subText")).getName(), 2, SubSort.BEST);

            //check the DB to see if it exists, if not add it
            //create a lambda that accepts post
            Consumer<PostWrapper> saveData = (PostWrapper post) -> {
                if (post.isPinned()) {
                    return;
                }
                AccountWrapper aw = post.getAuthor();
                RedditAccount acc = raLogic.getRedditAccountWithName(aw.getName());
                if (acc == null) {
                    Map<String, String[]> map = new HashMap<>(6);
                    map.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(aw.getCommentKarma())});
                    map.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(aw.getLinkKarma())});
                    map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw.getCreated())});
                    map.put(RedditAccountLogic.NAME, new String[]{aw.getName()});
                    acc = raLogic.createEntity(map);
                    raLogic.add(acc);
                }
                
                //do the same as RTedditAccount for post
                //dont forget to add the dependencies
                post.getSubreddit();
                post.getUniqueID();
                post.configComments(2, 2, CommentSort.CONFIDENCE);
                post.processComments((CommentWrapper comment) -> {
                    if (comment.isPinned() || comment.getDepth() == 0) {
                        return;
                    }

                    AccountWrapper aw1 = comment.getAuthor();
                    RedditAccount acc1 = raLogic.getRedditAccountWithName(aw1.getName());
                    if (acc1 == null) {
                        Map<String, String[]> map = new HashMap<>(6);
                        map.put(RedditAccountLogic.COMMENTPOINTS, new String[]{Integer.toString(aw1.getCommentKarma())});
                        map.put(RedditAccountLogic.LINKPOINTS, new String[]{Integer.toString(aw1.getLinkKarma())});
                        map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw1.getCreated())});
                        map.put(RedditAccountLogic.NAME, new String[]{aw1.getName()});
                        acc1 = raLogic.createEntity(map);
                        raLogic.add(acc1);
                    }
                    //do the same as RTedditAccount for comment
                //dont forget to add the dependencies
                    comment.getPost();
                    comment.getUniqueID();
                
                });
            };
            
            //get the next page and process every post
            scrap.requestNextPage().proccessCurrentPage(saveData);
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

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
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("entities", extractTableData(req));
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/ShowTable-Account.jsp").forward(req, resp);
    }

    private List<?> extractTableData(HttpServletRequest req) {
        String search = req.getParameter("searchbyname");
        SubredditLogic logic = LogicFactory.getFor("Subreddit");
        List<Subreddit> list;
        if (search != null) {
            list = logic.search(search);
        } else {
            list = logic.getAll();
        }
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return appendDatatoNewList(list, logic::extractDataAsList);
    }

    private <T> List<?> appendDatatoNewList(List<T> list, Function<T, List<?>> toArray) {
        List<List<?>> newlist = new ArrayList<>(list.size());
        list.forEach(i -> newlist.add(toArray.apply(i)));
        return newlist;
    }

}
