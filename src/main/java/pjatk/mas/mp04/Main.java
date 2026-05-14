package pjatk.mas.mp04;

import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pjatk.mas.mp04.model.*;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Server h2Console = Server.createWebServer("-web", "-webPort", "8082").start();
        System.out.println("H2 Console started at http://localhost:8082");
        System.out.println("  JDBC URL : jdbc:h2:mem:clinic");
        System.out.println("  Username : sa      Password: (leave empty)");

        StandardServiceRegistry registry = null;
        SessionFactory sessionFactory = null;
        try {
            registry = new StandardServiceRegistryBuilder().configure().build();
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//            demonstrateClasses(sessionFactory);
//            demonstrateAssociations(sessionFactory);

            demonstrateInheritance(sessionFactory);

        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        } finally {
            if (sessionFactory != null) sessionFactory.close();
            h2Console.stop();
        }
    }


    private static void demonstrateClasses(SessionFactory sessionFactory) throws InterruptedException {
        System.out.println("\n========== RM – CLASSES ==========");

        System.out.println("\n--- CREATE ---");

        var patient = new Patient(
                "PL123456", "Anna", "Kowalska",
                LocalDate.of(1990, 5, 15),
                new Address("ul. Koszykowa 86", "Warsaw", "02-008")
        );
        patient.setEmail("anna.kowalska@email.com");
        patient.addAllergy("Penicillin");
        patient.addAllergy("Aspirin");

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(patient);
            session.getTransaction().commit();
        }
        System.out.println("Saved:  " + patient);
        System.out.printf("  → derived age (not stored in DB): %d%n", patient.getAge());

        Long id = patient.getId();

        System.out.println("\n--- READ (after create) ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // load by primary key
            Patient loaded = session.get(Patient.class, id);
            System.out.println("By id:  " + loaded);
            System.out.printf("  → derived age (not stored in DB): %d%n", loaded.getAge());

            // load all using Criteria API (type-safe, no query strings)
            System.out.println("All patients via Criteria API:");
            getAllObjects(session, Patient.class)
                    .forEach(p -> System.out.println("  " + p));
            session.getTransaction().commit();
        }

        System.out.println("\n--- UPDATE ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient p = session.get(Patient.class, id);

            p.setEmail("anna.new@email.com");
            p.addAllergy("Ibuprofen");
            p.setHomeAddress(new Address("ul. Nowy Swiat 5", "Krakow", "31-007"));

            session.getTransaction().commit();
            System.out.println("Updated: " + p);
        }

        System.out.println("\n--- READ (after update) ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient loaded = session.get(Patient.class, id);
            System.out.println("Loaded: " + loaded);
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s");
        Thread.sleep(60_000);

        System.out.println("\n--- DELETE ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient p = session.get(Patient.class, id);
            session.remove(p);
            session.getTransaction().commit();
            System.out.println("Deleted patient id=" + id);
        }

        System.out.println("\n--- READ (after delete) ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient p = session.get(Patient.class, id);
            System.out.println(p == null
                    ? "Patient not found in DB — deleted successfully."
                    : "ERROR: patient still present!");
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s");
        Thread.sleep(60_000);
    }

    // Generic helper — loads all rows of any entity type using the Criteria API.
    // Equivalent to "SELECT * FROM <table>" but type-safe: no query strings,
    // the compiler catches class-name typos.
    private static <T> List<T> getAllObjects(Session session, Class<T> theClass) {
        var cb = session.getCriteriaBuilder();
        var cq = cb.createQuery(theClass);
        cq.select(cq.from(theClass));
        return session.createQuery(cq).list();
    }

    private static void demonstrateAssociations(SessionFactory sf) throws InterruptedException {
        System.out.println("\n==========  ASSOCIATIONS ==========");

        var nurse1 = new Nurse("Katarzyna", "Maj",   "RN-101");
        var nurse2 = new Nurse("Piotr",     "Kowal", "RN-102");
        var wardA  = new Ward("W-A", "Cardiology A", 2);
        var wardB  = new Ward("W-B", "Cardiology B", 3);

        wardA.addNurse(nurse1);   // add from the Ward side
        nurse2.addWard(wardA);    // add from the Nurse side — same result
        nurse1.addWard(wardB);    // add from the Nurse side

        try (Session session = sf.openSession()) {
            session.beginTransaction();
            // nurses are persisted automatically via CascadeType.PERSIST on Ward
            session.persist(wardA);
            session.persist(wardB);
            session.getTransaction().commit();
        }

        System.out.println("Wards and their nurses:");
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            getAllObjects(session, Ward.class)
                    .forEach(w -> System.out.println("  " + w));
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s — query WARDS, NURSES, WARD_NURSES in H2 console now");
        Thread.sleep(60_000);
    }


    private static void demonstrateInheritance(SessionFactory sf) throws InterruptedException {
        System.out.println("\n========== INHERITANCE ==========");

        var doctor       = new Doctor("Adam", "Lis", "Surgery", "LIC-999", "Surgeon");
        var receptionist = new Receptionist("Maria", "Kos", "Admissions", "DESK-3");

        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.persist(doctor);
            session.persist(receptionist);
            session.getTransaction().commit();
        }

        System.out.println("All ClinicStaff:");
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            List<ClinicStaff> staff = session.createQuery("from ClinicStaff", ClinicStaff.class).list();
            for (ClinicStaff s : staff) {
                System.out.printf("  [%s] %s%n", s.getClass().getSimpleName(), s.generateWorkReport());
            }
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s — query CLINIC_STAFF, DOCTORS, RECEPTIONISTS in H2 console now");
        Thread.sleep(60_000);
    }
}
