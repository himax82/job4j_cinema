package store;

import org.apache.commons.dbcp2.BasicDataSource;
import model.Account;
import model.Ticket;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class SqlStore implements Store {

    private static final Logger LOG = LogManager.getLogger(SqlStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private SqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("C:\\Users\\user\\IdeaProjects\\job4j_cinema\\db.properties")
        )) {
            cfg.load(io);
        } catch (IOException e) {
            LOG.error("Not Found file db.properties");
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            LOG.error("Class not found in classpath");
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new SqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTicketsBySessionId(int sessionId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement statement = cn.prepareStatement("SELECT * FROM ticket WHERE session_id = ?")) {
            statement.setInt(1, sessionId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new Ticket(
                            rs.getInt("id"),
                            rs.getInt("session_id"),
                            rs.getInt("row"),
                            rs.getInt("cell"),
                            rs.getInt("account_id")
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error("SQL FindAllTicket Error", e);
        }
        return tickets;
    }

    @Override
    public void save(Account account) {
        if (account.getId() == 0) {
            create(account);
        } else {
            update(account);
        }
    }

    @Override
    public void save(Ticket ticket) {
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ticket.getSessionId());
                ps.setInt(2, ticket.getRow());
                ps.setInt(3, ticket.getCell());
                ps.setInt(4, ticket.getAccountId());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticket.setId(rs.getInt(1));
                    }
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                LOG.error("SQL violation of uniqueness", e);
            }
        } catch (Exception e) {
            LOG.error("SQL Create Error", e);
        }
    }

    @Override
    public Account findAccountByPhone(String phone) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement statement = cn.prepareStatement("SELECT * FROM account WHERE phone = ?")) {
            statement.setString(1, phone);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    account = new Account(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                    account.setId(rs.getInt("id"));
                    return account;
                }
            }
        } catch (Exception e) {
            LOG.error("SQL Find phone error", e);
        }
        return account;
    }

    private void create(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement = cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getEmail());
            statement.setString(3, account.getPhone());
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("SQL create account error", e);
        }
    }

    private void update(Account account) {
        String query = "UPDATE account SET username = ?, email = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement statement = cn.prepareStatement(query)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getEmail());
            statement.setInt(3, account.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            LOG.error("SQL update account error", e);
        }
    }
}