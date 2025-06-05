import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Librarian extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    public Librarian(String name, String id) {
        super(name, id);
    }

    @Override
    public void displayInterface() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n===== LIBRARIAN INTERFACE =====");
            System.out.println("Welcome, " + name + " (ID: " + id + ")");
            System.out.println("1. Check Books Status");
            System.out.println("2. Restock Books");
            System.out.println("3. Add New Book Entry");
            System.out.println("4. Check Fines to be Paid");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 
                
                switch (choice) {
                    case 1:
                        checkBooksStatus();
                        break;
                    case 2:
                        restockBooks();
                        break;
                    case 3:
                        addNewBook();
                        break;
                    case 4:
                        checkFines();
                        break;
                    case 5:
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); 
                choice = 0;
            }
        } while (choice != 5);
    }
    
    private void checkBooksStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== CHECK BOOKS STATUS =====");
        System.out.println("1. Search for a specific book");
        System.out.println("2. Check books with low availability");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            switch (choice) {
                case 1:
                    System.out.println("\nSearch by:");
                    System.out.println("1. Title");
                    System.out.println("2. Author");
                    System.out.println("3. Book ID");
                    System.out.print("Enter your choice: ");
                    
                    int searchChoice = scanner.nextInt();
                    scanner.nextLine(); 
                    
                    System.out.print("Enter search term: ");
                    String searchTerm = scanner.nextLine();
                    
                    Library.getInstance().searchBooks(searchChoice, searchTerm, null);
                    break;
                case 2:
                    checkLowAvailabilityBooks();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void checkLowAvailabilityBooks() {
        System.out.println("\n===== BOOKS WITH LOW AVAILABILITY =====");
        // Define "low" as less than 20% of total copies available
        List<Book> lowAvailability = Library.getInstance().getLowAvailabilityBooks(0.2);
        
        if (lowAvailability.isEmpty()) {
            System.out.println("All books have adequate availability.");
        } else {
            System.out.println("The following books have low availability:");
            
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < lowAvailability.size(); i++) {
                Book book = lowAvailability.get(i);
                System.out.println("\n" + (i+1) + ". " + book.getTitle() + " by " + book.getAuthor());
                System.out.println("   Book ID: " + book.getBookId());
                System.out.println("   Available copies: " + book.getAvailableCopies() + "/" + book.getNumberOfCopies());
                
                List<StudentBook> borrowedInstances = Library.getInstance().getBorrowedInstances(book.getBookId());
                if (!borrowedInstances.isEmpty()) {
                    System.out.println("   Currently borrowed by " + borrowedInstances.size() + " students.");
                    System.out.println("   Would you like to see details? (Y/N): ");
                    String choice = scanner.nextLine();
                    
                    if (choice.equalsIgnoreCase("Y")) {
                        System.out.println("\n   === BORROWED INSTANCES ===");
                        for (StudentBook studentBook : borrowedInstances) {
                            Student student = Library.getInstance().getStudentById(studentBook.getStudentId());
                            System.out.println("   Student: " + student.getName() + " (ID: " + student.getId() + ")");
                            System.out.println("   Borrow Date: " + studentBook.getBorrowDate());
                            System.out.println("   Due Date: " + studentBook.getDueDate());
                            if (studentBook.isDelayed()) {
                                System.out.println("   Status: OVERDUE");
                                System.out.println("   Fine: Rs. " + studentBook.getFine());
                            } else {
                                System.out.println("   Status: Within due date");
                            }
                            System.out.println();
                        }
                    }
                }
            }
        }
    }
    
    private void restockBooks() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== RESTOCK BOOKS =====");
        System.out.print("Enter Book ID to restock: ");
        String bookId = scanner.nextLine();
        
        try {
            Book book = Library.getInstance().getBookById(bookId);
            if (book != null) {
                System.out.println("\nBook Details:");
                System.out.println(book);
                
                System.out.print("\nEnter number of copies to add: ");
                int copies = scanner.nextInt();
                scanner.nextLine(); 
                
                if (copies > 0) {
                    book.restock(copies);
                    System.out.println("Successfully added " + copies + " copies.");
                    System.out.println("Updated total: " + book.getNumberOfCopies() + " copies.");
                    System.out.println("Updated available: " + book.getAvailableCopies() + " copies.");
                } else {
                    System.out.println("Number of copies must be positive.");
                }
            } else {
                System.out.println("Book not found with ID: " + bookId);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void addNewBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== ADD NEW BOOK =====");
        
        try {
            System.out.print("Enter Book Title: ");
            String title = scanner.nextLine();
            
            System.out.print("Enter Author: ");
            String author = scanner.nextLine();
            
            System.out.print("Enter Book ID: ");
            String bookId = scanner.nextLine();
            
            if (Library.getInstance().getBookById(bookId) != null) {
                System.out.println("Error: Book ID already exists. Each Book ID must be unique.");
                return;
            }
            
            System.out.print("Enter Number of Copies: ");
            int copies = scanner.nextInt();
            scanner.nextLine();
            
            if (copies <= 0) {
                System.out.println("Error: Number of copies must be positive.");
                return;
            }
            
            Book newBook = new Book(title, author, bookId, copies);
            Library.getInstance().addBook(newBook);
            
            System.out.println("\nBook added successfully!");
            System.out.println(newBook);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void checkFines() {
        System.out.println("\n===== STUDENT FINES =====");
        List<Student> studentsWithFines = Library.getInstance().getStudentsWithFines();
        
        if (studentsWithFines.isEmpty()) {
            System.out.println("No students have outstanding fines.");
        } else {
            System.out.println("The following students have outstanding fines:");
            
            for (Student student : studentsWithFines) {
                System.out.println("\nStudent: " + student.getName() + " (ID: " + student.getId() + ")");
                System.out.println("Total Fine: Rs. " + student.getTotalFine());
                
                System.out.println("\nFine breakdown:");
                for (StudentBook book : student.getBorrowedBooks()) {
                    if (book.getFine() > 0) {
                        System.out.println("- " + book.getBookTitle() + " (ID: " + book.getBookId() + ")");
                        System.out.println("  Fine: Rs. " + book.getFine());
                        
                        if (book.isReturned()) {
                            System.out.println("  Status: Returned late on " + book.getReturnDate());
                        } else {
                            System.out.println("  Status: Not returned (due on " + book.getDueDate() + ")");
                        }
                    }
                }
                System.out.println("----------------------------------------");
            }
        }
    }
    
    public StudentBook acceptBookReturn(Student student, String bookId, String returnCondition) {
        StudentBook borrowed = student.findBorrowedBook(bookId);
        
        if (borrowed != null) {
            borrowed.setReturnDate(LocalDate.now());
            
            if (returnCondition != null && !returnCondition.isEmpty()) {
                borrowed.setCondition(borrowed.getCondition() + "\nReturn condition: " + returnCondition);
            }
            
            Book book = Library.getInstance().getBookById(bookId);
            if (book != null) {
                book.returnBook();
            }
            
            return borrowed;
        }
        
        return null;
    }
}