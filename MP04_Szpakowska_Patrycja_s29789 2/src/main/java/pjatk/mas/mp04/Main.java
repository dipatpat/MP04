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
            demonstrateClasses(sessionFactory);
            demonstrateAssociations(sessionFactory);

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
        System.out.println("\n========== CLASS DEMO==========");

        var address = new Address("ul. Koszykowa", "86", "Warsaw", "02-008");
        address.setApartmentNumber("12");

        var patient = new Patient(
                "PL123456", "Anna", "Kowalska",
                LocalDate.of(1990, 5, 15),
                address
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
        System.out.printf("  → derived age (not stored): %d%n", patient.getAge());
        System.out.printf("  → eligible for Penicillin vaccine: %b%n",
                patient.isEligibleForVaccination("Penicillin"));
        System.out.printf("  → eligible for Flu vaccine: %b%n",
                patient.isEligibleForVaccination("Flu"));
        System.out.printf("  → eligible for Flu vaccine (min age 65): %b%n",
                patient.isEligibleForVaccination("Flu", 65));

        Long id = patient.getId();

        System.out.println("\n--- READ ---");
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient loaded = session.get(Patient.class, id);
            System.out.println("By id:  " + loaded);
            System.out.printf("  → derived age (not stored in DB): %d%n", loaded.getAge());

            getAllObjects(session, Patient.class)
                    .forEach(p -> System.out.println("  " + p));

            System.out.println("findPatientsByCity(Warsaw):");
            Patient.findPatientsByCity(session, "Warsaw")
                    .forEach(p -> System.out.println("  " + p));
            session.getTransaction().commit();
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient patient1 = session.get(Patient.class, id);

            patient1.setEmail("anna.new@email.com");
            patient1.addAllergy("Ibuprofen");
            patient1.setHomeAddress(new Address("ul. Nowy Swiat", "5", "Krakow", "31-007"));

            session.getTransaction().commit();
            System.out.println("Updated: " + patient1);
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient loaded = session.get(Patient.class, id);
            System.out.println("Loaded: " + loaded);
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s");
        Thread.sleep(60_000);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Patient patient2 = session.get(Patient.class, id);
            session.remove(patient2);
            session.getTransaction().commit();
            System.out.println("Deleted patient id=" + id);
        }

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


    private static <T> List<T> getAllObjects(Session session, Class<T> theClass) {
        var criteriaBuilder = session.getCriteriaBuilder();
        var jpaCriteriaQuery = criteriaBuilder.createQuery(theClass);
        jpaCriteriaQuery.select(jpaCriteriaQuery.from(theClass));
        return session.createQuery(jpaCriteriaQuery).list();
    }

    private static void demonstrateAssociations(SessionFactory sf) throws InterruptedException {
        System.out.println("\n==========  ASSOCIATIONS DEMO ==========");

        var nurse1 = new Nurse("Katarzyna", "Maj",   "RN-101");
        var nurse2 = new Nurse("Piotr",     "Kowal", "RN-102");
        var wardA  = new Ward("W-A", "Cardiology A", 2);
        var wardB  = new Ward("W-B", "Cardiology B", 3);

        wardA.addNurse(nurse1);
        nurse2.addWard(wardA);
        nurse1.addWard(wardB);

        try (Session session = sf.openSession()) {
            session.beginTransaction();
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

        System.out.println("\n>>> PAUSING 60s");
        Thread.sleep(60_000);
    }


    private static void demonstrateInheritance(SessionFactory sf) throws InterruptedException {
        System.out.println("\n========== INHERITANCE DEMO ==========");

        var doctor       = new Doctor("Adam", "Lis", "Surgery", "LIC-999", "Surgeon");
        var receptionist = new Receptionist("Maria", "Kos", "Admissions", "DESK-3");

        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.persist(doctor);
            session.persist(receptionist);
            session.getTransaction().commit();
        }

        doctor.recordAppointment();
        doctor.recordAppointment();
        doctor.recordAppointment();
        receptionist.handleCall();
        receptionist.handleCall();

        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Doctor d = session.get(Doctor.class, doctor.getId());
            d.setAppointmentsThisShift(doctor.getAppointmentsThisShift());
            Receptionist r = session.get(Receptionist.class, receptionist.getId());
            r.setCallsHandledThisShift(receptionist.getCallsHandledThisShift());
            session.getTransaction().commit();
        }

        System.out.println("All ClinicStaff:");
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            getAllObjects(session, ClinicStaff.class)
                    .forEach(s -> System.out.printf("  [%s] %s%n",
                            s.getClass().getSimpleName(), s.generateWorkReport()));
            session.getTransaction().commit();
        }

        System.out.println("\n>>> PAUSING 60s");
        Thread.sleep(60_000);
    }
}
