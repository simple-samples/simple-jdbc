import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String ...args) {
        //We need to store the database credentials safely and securely
        //We need connection to mariaDB
        //We will need an interface for CRUD
        //we will implement this interface on our repos


        Connection conn = ConnectionManager.getConnection();

        AssociateModel tiffany = new AssociateModel(1, "Tiffany", "Obi", 25);
        AssociateModel kyle = new AssociateModel(2, "Kyle", "Plummer", 36);
        AssociateModel cody = new AssociateModel(3, "Cody", "Gonsowski", 22);
        AssociateModel stefan = new AssociateModel(4, "Stefan", "Riley", 29);
        AssociateModel shabana = new AssociateModel(5, "Shabana", "Mehr", 35);
        AssociateModel ahmad = new AssociateModel(6, "Ahmad", "Rawashdeh", 38);


        AssociateRepo repo = new AssociateRepo();

        System.out.println("Creating...");
        repo.create(tiffany);
        repo.create(kyle);
        repo.create(cody);
        repo.create(stefan);
        repo.create(shabana);
        repo.create(ahmad);
        System.out.println("Created.");

        Scanner sc = new Scanner(System.in);
        sc.nextLine();




        System.out.println("Updating...");
        tiffany.setLastName("Chestnut");
        kyle.setFirstName("Sir Kyle");
        repo.update(tiffany);
        repo.update(kyle);
        System.out.println("updated.");

        sc.nextLine();

        System.out.print("Associate with ID 4:  ");
        AssociateModel queryAssociate = repo.read(4);
        System.out.println("name: " + queryAssociate.getLastName() + ", " + queryAssociate.getFirstName());

        sc.nextLine();

        System.out.println("deleting... ");
        repo.delete(1);
        repo.delete(5);
        System.out.println("deleted");

        sc.nextLine();
    }
}
