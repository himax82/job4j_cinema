package store;

import model.Account;
import model.Ticket;

import java.util.Collection;

public interface Store {
    Collection<Ticket> findAllTicketsBySessionId(int sessionId);
    void save(Account account);
    void save(Ticket ticket);
    Account findAccountByPhone(String phone);
}