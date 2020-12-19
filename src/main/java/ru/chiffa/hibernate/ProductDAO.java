package ru.chiffa.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDAO {
    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            init();
            saveOrUpdate(new Product("Document", 10));
            saveOrUpdate(new Product("Secret  Document", 100));
            saveOrUpdate(new Product("Top Secret Document", 1000));
            System.out.println(findAll());
            deleteById(1L);
            System.out.println(findAll());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private static void init() throws IOException {
        factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        prepareTable();
    }

    private static void prepareTable() throws IOException {

        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.createNativeQuery(Files.lines(Paths.get("src/main/prepareDB.sql")).collect(Collectors.joining(" ")))
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    private static void shutdown() {
        factory.close();
    }

    public static Product findById(Long id) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Product product = session.get(Product.class, id);
            session.getTransaction().commit();
            return product;
        }
    }

    public static List<Product> findAll() {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            List<Product> result = session.createQuery("from Product", Product.class).getResultList();
            session.getTransaction().commit();
            return result;
        }
    }

    public static void deleteById(Long id) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.remove(session.get(Product.class, id));
            session.getTransaction().commit();
        }
    }

    public static Product saveOrUpdate(Product product) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.saveOrUpdate(product);
            session.getTransaction().commit();
            return product;
        }
    }

}
