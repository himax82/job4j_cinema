package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Account;
import model.JsonPost;
import model.Ticket;
import store.SqlStore;
import store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HallServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        int sessionId = Integer.parseInt(req.getParameter("id_session"));
        String json = GSON.toJson(
                SqlStore.instOf().findAllTicketsBySessionId(sessionId)
        );
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), StandardCharsets.UTF_8));
        JsonPost post = GSON.fromJson(req.getReader(), JsonPost.class);
        Account account = post.getAccount();
        Store store = SqlStore.instOf();
        store.findAllTicketsBySessionId(1);
        Account foundAcc = store.findAccountByPhone(account.getPhone());
        if (foundAcc != null) {
            account.setId(foundAcc.getId());
        }
        String place = post.getPlace();
        int sessionId = post.getSessionId();
        String[] arP = place.split(";");
        List<Ticket> list = new ArrayList<>();
        store.save(account);
        if (account.getId() == 0) {
            writer.print("409 Conflict");
            writer.flush();
            return;
        }
        for (int i = 0; i < arP.length; i++) {
            String[] arT = arP[i].split("");
            list.add(new Ticket(0, sessionId, Integer.parseInt(arT[0]),
                    Integer.parseInt(arT[1]), account.getId()));
        }
        for (Ticket t : list) {
            store.save(t);
            if (t.getId() == 0) {
                writer.print("409 Conflict");
                writer.flush();
                return;
            }
        }
        writer.print("200 OK");
        writer.flush();
    }
}