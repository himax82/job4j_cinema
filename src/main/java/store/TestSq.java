package store;

import model.Account;

public class TestSq {

    public static void main(String[] args) {
        Store store = SqlStore.instOf();
        store.save(new Account("Petya", "petya@mail.ru", "8564"));
        System.out.println(store.findAccountByPhone("8564"));
    }
}
