import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Library library = Library.getInstance();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        System.out.println("Welcome to Library Management System");
        while (running) {
            try {
                System.out.println("\n________ MAIN MENU _________");
                System.out.println("1. Login as Student");
                System.out.println("2. Login as Librarian");
                System.out.println("3. Register New Student");
                System.out.println("4. Exit Program");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); 
                
                switch (choice) {
                    case 1:
                        loginAsStudent(scanner, library);
                        break;
                    case 2:
                        loginAsLibrarian(scanner, library);
                        break;
                    case 3:
                        registerNewStudent(scanner, library);
                        break;
                    case 4:
                        running = false;
                        System.out.println("Saving data and exiting program...");
                        library.saveData();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();  
            }
        }
        
        scanner.close();
        System.out.println("Thank you for using Library Management System!");
    }
    
    private static void loginAsStudent(Scanner scanner, Library library) {
        System.out.println("\n===== STUDENT LOGIN =====");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        
        try {
            Student student = library.getStudentById(studentId);
            
            if (student != null) {
                System.out.println("Welcome back, " + student.getName() + "!");
                student.displayInterface();
            } else {
                throw new Exception("Invalid Student ID. Please check your ID or register as a new student.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void loginAsLibrarian(Scanner scanner, Library library) {
        System.out.println("\n===== LIBRARIAN LOGIN =====");
        System.out.print("Enter Librarian ID: ");
        String librarianId = scanner.nextLine();
        
        try {
            Librarian librarian = library.getLibrarianById(librarianId);
            
            if (librarian != null) {
                System.out.println("Welcome back, " + librarian.getName() + "!");
                boolean librarianMenu = true;
                
                while (librarianMenu) {
                    System.out.println("\n===== LIBRARIAN MENU =====");
                    System.out.println("1. Access Librarian Interface");
                    System.out.println("2. Process Book Return");
                    System.out.println("3. Return to Main Menu");
                    System.out.print("Enter your choice: ");
                    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); 
                    
                    switch (choice) {
                        case 1:
                            librarian.displayInterface();
                            break;
                        case 2:
                            processBookReturn(scanner, library, librarian);
                            break;
                        case 3:
                            librarianMenu = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } else {
                throw new Exception("Invalid Librarian ID.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void registerNewStudent(Scanner scanner, Library library) {
        System.out.println("\n===== NEW STUDENT REGISTRATION =====");
        
        try {
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Student ID: ");
            String studentId = scanner.nextLine();
            
            if (library.getStudentById(studentId) != null) {
                System.out.println("Error: Student ID already exists. Please use another ID.");
                return;
            }
            
            Student newStudent = new Student(name, studentId);
            library.addStudent(newStudent);
            
            System.out.println("\nStudent registered successfully!");
            System.out.println("Name: " + name);
            System.out.println("ID: " + studentId);
            
            System.out.print("\nDo you want to login now? (Y/N): ");
            String choice = scanner.nextLine();
            
            if (choice.equalsIgnoreCase("Y")) {
                newStudent.displayInterface();
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }
    
    private static void processBookReturn(Scanner scanner, Library library, Librarian librarian) {
        System.out.println("\n===== PROCESS BOOK RETURN =====");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();
        
        try {
            Student student = library.getStudentById(studentId);
            
            if (student != null) {
                library.returnBook(student, librarian);
            } else {
                System.out.println("Student not found with ID: " + studentId);
            }
        } catch (Exception e) {
            System.out.println("Error processing return: " + e.getMessage());
        }
    }
}