import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Student extends Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<StudentBook> borrowedBooks;
    private double totalFine;

    public Student(String name, String studentId) {
        super(name, studentId);
        this.borrowedBooks = new ArrayList<>();
        this.totalFine = 0.0;
    }

    public List<StudentBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void addBorrowedBook(StudentBook studentBook) {
        borrowedBooks.add(studentBook);
    }
    
    public void removeBorrowedBook(StudentBook studentBook) {
        borrowedBooks.remove(studentBook);
    }

    public double getTotalFine() {
        calculateTotalFine();
        return totalFine;
    }

    public void setTotalFine(double totalFine) {
        this.totalFine = totalFine;
    }
    
    public void payFine(double amount) {
        if (amount > 0 && amount <= totalFine) {
            totalFine -= amount;
            System.out.println("Payment of Rs. " + amount + " processed successfully.");
            System.out.println("Remaining fine: Rs. " + totalFine);
        } else {
            System.out.println("Invalid payment amount.");
        }
    }
    
    public void calculateTotalFine() {
        double calculatedFine = 0.0;
        for (StudentBook book : borrowedBooks) {
            book.calculateFine();
            if (!book.isReturned() || book.isDelayed()) {
                calculatedFine += book.getFine();
            }
        }
        this.totalFine = calculatedFine;
    }
    
    public List<StudentBook> getPendingSubmissions() {
        List<StudentBook> pending = new ArrayList<>();
        for (StudentBook book : borrowedBooks) {
            if (!book.isReturned() && !book.isDelayed()) {
                pending.add(book);
            }
        }
        return pending;
    }
    
    public List<StudentBook> getDelayedSubmissions() {
        List<StudentBook> delayed = new ArrayList<>();
        for (StudentBook book : borrowedBooks) {
            if (!book.isReturned() && book.isDelayed()) {
                delayed.add(book);
            }
        }
        return delayed;
    }

    @Override
    public void displayInterface() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n===== STUDENT INTERFACE =====");
            System.out.println("Welcome, " + name + " (ID: " + id + ")");
            System.out.println("1. Check Books in Library");
            System.out.println("2. Check Pending Submissions");
            System.out.println("3. Check Delayed Submissions");
            System.out.println("4. Check Payment Due");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 
                
                switch (choice) {
                    case 1:
                        checkBooksInLibrary();
                        break;
                    case 2:
                        checkPendingSubmissions();
                        break;
                    case 3:
                        checkDelayedSubmissions();
                        break;
                    case 4:
                        checkPaymentDue();
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
    
    private void checkBooksInLibrary() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== LIBRARY BOOKS =====");
        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Book ID");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Enter search term: ");
            String searchTerm = scanner.nextLine();
            
            Library.getInstance().searchBooks(choice, searchTerm, this);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void checkPendingSubmissions() {
        System.out.println("\n===== PENDING SUBMISSIONS =====");
        List<StudentBook> pending = getPendingSubmissions();
        
        if (pending.isEmpty()) {
            System.out.println("You have no pending book submissions.");
        } else {
            System.out.println("You have " + pending.size() + " books borrowed:");
            for (int i = 0; i < pending.size(); i++) {
                System.out.println("\nBook " + (i+1) + ":");
                System.out.println(pending.get(i));
            }
        }
    }
    
    private void checkDelayedSubmissions() {
        System.out.println("\n===== DELAYED SUBMISSIONS =====");
        List<StudentBook> delayed = getDelayedSubmissions();
        
        if (delayed.isEmpty()) {
            System.out.println("You have no delayed submissions. Thank you for being responsible!");
        } else {
            System.out.println("WARNING: You have " + delayed.size() + " delayed submissions:");
            for (int i = 0; i < delayed.size(); i++) {
                StudentBook book = delayed.get(i);
                book.calculateFine();
                System.out.println("\nBook " + (i+1) + ":");
                System.out.println(book);
            }
            System.out.println("\nPlease return these books as soon as possible to avoid additional fines.");
        }
    }
    
    private void checkPaymentDue() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n===== PAYMENT DUE =====");
        calculateTotalFine();
        
        if (totalFine <= 0) {
            System.out.println("You have no fines left. Thanks for being a responsible library user! :D");
            return;
        }
        
        System.out.println("Total fine due: Rs. " + totalFine);
        System.out.println("\nFine breakdown:");
        
        for (StudentBook book : borrowedBooks) {
            book.calculateFine();
            if (book.getFine() > 0) {
                System.out.println("- " + book.getBookTitle() + " (ID: " + book.getBookId() + ")");
                System.out.println("  Fine: Rs. " + book.getFine());
                if (book.isDelayed() && !book.isReturned()) {
                    System.out.println("  Reason: Book overdue (not returned yet)");
                } else if (book.isDelayed()) {
                    System.out.println("  Reason: Late submission");
                }
                System.out.println();
            }
        }
        
        System.out.println("\nDo you want to pay the fine now? (Y/N): ");
        String choice = scanner.nextLine();
        
        if (choice.equalsIgnoreCase("Y")) {
            System.out.print("Enter amount to pay: Rs. ");
            try {
                double amount = scanner.nextDouble();
                payFine(amount);
            } catch (Exception e) {
                System.out.println("Invalid amount. Payment cancelled.");
            }
        }
    }
    
    public StudentBook findBorrowedBook(String bookId) {
        for (StudentBook book : borrowedBooks) {
            if (book.getBookId().equals(bookId) && !book.isReturned()) {
                return book;
            }
        }
        return null;
    }
}