import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library {
    private static Library instance;
    
    private List<Book> books;
    private List<Student> students;
    private List<Librarian> librarians;
    
    private static final String BOOKS_FILE = "books.txt";
    private static final String STUDENTS_FILE = "students.txt";
    private static final String LIBRARIANS_FILE = "librarians.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Library() {
        this.books = new ArrayList<>();
        this.students = new ArrayList<>();
        this.librarians = new ArrayList<>();
        loadData();
    }

    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public void saveData() {
        try {
            saveBooksToFile();
            saveStudentsToFile();
            saveLibrariansToFile();
            System.out.println("All data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void loadData() {
        try {
            loadBooksFromFile();
            loadStudentsFromFile();
            loadLibrariansFromFile();

            if (librarians.isEmpty()) {
                librarians.add(new Librarian("Admin", "L001"));
            }
            
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            books = new ArrayList<>();
            students = new ArrayList<>();
            librarians = new ArrayList<>();
            
            librarians.add(new Librarian("Admin", "L001"));
        }
    }
    
    // Save books to text file
    private void saveBooksToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            writer.println("# Book Data Format: title|author|bookId|numberOfCopies|availableCopies");
            for (Book book : books) {
                writer.println(book.getTitle() + "|" + 
                              book.getAuthor() + "|" + 
                              book.getBookId() + "|" + 
                              book.getNumberOfCopies() + "|" + 
                              book.getAvailableCopies());
            }
        }
    }
    
    // Save students to text file
    private void saveStudentsToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            writer.println("# Student Data Format:");
            writer.println("# STUDENT|name|id|totalFine");
            writer.println("# BOOK|studentId|bookId|bookTitle|borrowDate|dueDate|returnDate|condition|fine|isReturned");
            
            for (Student student : students) {
                writer.println("STUDENT|" + student.getName() + "|" + 
                              student.getId() + "|" + 
                              student.getTotalFine());
                
                for (StudentBook book : student.getBorrowedBooks()) {
                    writer.println("BOOK|" + 
                                  student.getId() + "|" + 
                                  book.getBookId() + "|" + 
                                  book.getBookTitle() + "|" + 
                                  book.getBorrowDate().format(DATE_FORMAT) + "|" + 
                                  book.getDueDate().format(DATE_FORMAT) + "|" + 
                                  (book.isReturned() ? book.getReturnDate().format(DATE_FORMAT) : "NULL") + "|" + 
                                  book.getCondition().replace("|", "//") + "|" + 
                                  book.getFine() + "|" + 
                                  book.isReturned());
                }
            }
        }
    }
    
    // Save librarians to text file
    private void saveLibrariansToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LIBRARIANS_FILE))) {
            writer.println("# Librarian Data Format: name|id");
            for (Librarian librarian : librarians) {
                writer.println(librarian.getName() + "|" + librarian.getId());
            }
        }
    }
    
    // Load books from text file
    private void loadBooksFromFile() throws IOException {
        File file = new File(BOOKS_FILE);
        books = new ArrayList<>();
        
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine || line.startsWith("#")) {
                    firstLine = false;
                    continue;  // Skip header or comment lines
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String title = parts[0];
                    String author = parts[1];
                    String bookId = parts[2];
                    int numberOfCopies = Integer.parseInt(parts[3]);
                    int availableCopies = Integer.parseInt(parts[4]);
                    
                    Book book = new Book(title, author, bookId, numberOfCopies);
                    book.setAvailableCopies(availableCopies);
                    books.add(book);
                }
            }
        }
    }
    
    // Load students from text file
    private void loadStudentsFromFile() throws IOException {
        File file = new File(STUDENTS_FILE);
        students = new ArrayList<>();
        
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstTwoLines = true;
            int lineCount = 0;
            Student currentStudent = null;
            
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (lineCount <= 2 || line.startsWith("#")) {
                    continue;  // Skip header or comment lines
                }
                
                String[] parts = line.split("\\|");
                if (parts.length > 0) {
                    if (parts[0].equals("STUDENT")) {
                        if (parts.length >= 4) {
                            String name = parts[1];
                            String id = parts[2];
                            double totalFine = Double.parseDouble(parts[3]);
                            
                            currentStudent = new Student(name, id);
                            currentStudent.setTotalFine(totalFine);
                            students.add(currentStudent);
                        }
                    } else if (parts[0].equals("BOOK") && currentStudent != null) {
                        if (parts.length >= 10) {
                            String bookId = parts[2];
                            String bookTitle = parts[3];
                            LocalDate borrowDate = LocalDate.parse(parts[4], DATE_FORMAT);
                            LocalDate dueDate = LocalDate.parse(parts[5], DATE_FORMAT);
                            String returnDateStr = parts[6];
                            String condition = parts[7].replace("//", "|");
                            double fine = Double.parseDouble(parts[8]);
                            boolean isReturned = Boolean.parseBoolean(parts[9]);
                            
                            // Create new StudentBook with constructor
                            StudentBook studentBook = new StudentBook(currentStudent.getId(), bookId, bookTitle, condition);
                            
                            // Set fields that aren't part of constructor
                            // Use reflection or setters
                            try {
                                java.lang.reflect.Field borrowDateField = StudentBook.class.getDeclaredField("borrowDate");
                                borrowDateField.setAccessible(true);
                                borrowDateField.set(studentBook, borrowDate);
                                
                                java.lang.reflect.Field dueDateField = StudentBook.class.getDeclaredField("dueDate");
                                dueDateField.setAccessible(true);
                                dueDateField.set(studentBook, dueDate);
                                
                                studentBook.setFine(fine);
                                
                                if (isReturned && !returnDateStr.equals("NULL")) {
                                    LocalDate returnDate = LocalDate.parse(returnDateStr, DATE_FORMAT);
                                    studentBook.setReturnDate(returnDate);
                                }
                                
                                currentStudent.addBorrowedBook(studentBook);
                            } catch (Exception e) {
                                System.out.println("Error loading student book: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Load librarians from text file
    private void loadLibrariansFromFile() throws IOException {
        File file = new File(LIBRARIANS_FILE);
        librarians = new ArrayList<>();
        
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine || line.startsWith("#")) {
                    firstLine = false;
                    continue;  // Skip header or comment lines
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String name = parts[0];
                    String id = parts[1];
                    
                    librarians.add(new Librarian(name, id));
                }
            }
        }
    }
    
    // Rest of the Library class methods remain unchanged
    public void addBook(Book book) {
        books.add(book);
        saveData();
    }
    
    public Book getBookById(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }
    
    public List<Book> getLowAvailabilityBooks(double threshold) {
        List<Book> lowAvailability = new ArrayList<>();
        for (Book book : books) {
            if (book.getAvailableCopies() < book.getNumberOfCopies() * threshold) {
                lowAvailability.add(book);
            }
        }
        return lowAvailability;
    }
    
    // Student management
    public void addStudent(Student student) {
        students.add(student);
        saveData();
    }
    
    public Student getStudentById(String studentId) {
        for (Student student : students) {
            if (student.getId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    public List<Student> getStudentsWithFines() {
        List<Student> withFines = new ArrayList<>();
        for (Student student : students) {
            student.calculateTotalFine();
            if (student.getTotalFine() > 0) {
                withFines.add(student);
            }
        }
        return withFines;
    }
    
    // Librarian management
    public void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
        saveData();
    }
    
    public Librarian getLibrarianById(String librarianId) {
        for (Librarian librarian : librarians) {
            if (librarian.getId().equals(librarianId)) {
                return librarian;
            }
        }
        return null;
    }

    public void searchBooks(int searchType, String searchTerm, Student student) {
        List<Book> matchingBooks = new ArrayList<>();
        
        switch (searchType) {
            case 1: // Title
                for (Book book : books) {
                    if (book.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) {
                        matchingBooks.add(book);
                    }
                }
                break;
            case 2: // Author
                for (Book book : books) {
                    if (book.getAuthor().toLowerCase().contains(searchTerm.toLowerCase())) {
                        matchingBooks.add(book);
                    }
                }
                break;
            case 3: // Book ID
                for (Book book : books) {
                    if (book.getBookId().equals(searchTerm)) {
                        matchingBooks.add(book);
                    }
                }
                break;
            default:
                System.out.println("Invalid search type.");
                return;
        }
        
        if (matchingBooks.isEmpty()) {
            System.out.println("No books found matching your search criteria.");
        } else {
            System.out.println("\nFound " + matchingBooks.size() + " matching books:");
            
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < matchingBooks.size(); i++) {
                Book book = matchingBooks.get(i);
                System.out.println("\n" + (i + 1) + ". " + book);
                
                if (student != null && book.isAvailable()) {
                    System.out.print("\nDo you want to borrow this book? (Y/N): ");
                    String choice = scanner.nextLine();
                    
                    if (choice.equalsIgnoreCase("Y")) {
                        borrowBook(student, book);
                    }
                }
            }
        }
    }

    public void borrowBook(Student student, Book book) {
        if (!book.isAvailable()) {
            System.out.println("Sorry, this book is not available for borrowing.");
            return;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter any condition notes about the book (damage, etc.) or press Enter if none: ");
        String condition = scanner.nextLine();
        
        if (book.borrowBook()) {
            StudentBook studentBook = new StudentBook(student.getId(), book.getBookId(), book.getTitle(), condition);
            student.addBorrowedBook(studentBook);
            
            System.out.println("\nBook borrowed successfully!");
            System.out.println("Due date: " + studentBook.getDueDate());
            saveData();
        } else {
            System.out.println("Failed to borrow book. No copies available.");
        }
    }
    
    public void returnBook(Student student, Librarian librarian) {
        Scanner scanner = new Scanner(System.in);
        
        List<StudentBook> borrowed = new ArrayList<>();
        for (StudentBook book : student.getBorrowedBooks()) {
            if (!book.isReturned()) {
                borrowed.add(book);
            }
        }
        
        if (borrowed.isEmpty()) {
            System.out.println("This student has no books to return.");
            return;
        }
        
        System.out.println("\n===== RETURN BOOK =====");
        System.out.println("Books borrowed by " + student.getName() + ":");
        
        for (int i = 0; i < borrowed.size(); i++) {
            StudentBook book = borrowed.get(i);
            System.out.println((i+1) + ". " + book.getBookTitle() + " (ID: " + book.getBookId() + ")");
            System.out.println("   Borrowed on: " + book.getBorrowDate());
            System.out.println("   Due date: " + book.getDueDate());
            if (book.isDelayed()) {
                System.out.println("   Status: OVERDUE");
                System.out.println("   Current fine: Rs. " + book.getFine());
            } else {
                System.out.println("   Status: Within due date");
            }
        }
        
        System.out.print("\nEnter the number of the book to return (or 0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 
        
        if (choice < 1 || choice > borrowed.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        StudentBook selectedBook = borrowed.get(choice - 1);
        System.out.print("Enter any notes about return condition: ");
        String returnCondition = scanner.nextLine();
        
        StudentBook returnedBook = librarian.acceptBookReturn(student, selectedBook.getBookId(), returnCondition);
        
        if (returnedBook != null) {
            System.out.println("\nBook returned successfully!");
            
            if (returnedBook.getFine() > 0) {
                System.out.println("Fine for late return: Rs. " + returnedBook.getFine());
                student.calculateTotalFine();
                System.out.println("Total outstanding fine: Rs. " + student.getTotalFine());
            }
            
            saveData();
        } else {
            System.out.println("Error returning book.");
        }
    }
    
    public List<StudentBook> getBorrowedInstances(String bookId) {
        List<StudentBook> borrowedInstances = new ArrayList<>();
        
        for (Student student : students) {
            for (StudentBook studentBook : student.getBorrowedBooks()) {
                if (studentBook.getBookId().equals(bookId) && !studentBook.isReturned()) {
                    borrowedInstances.add(studentBook);
                }
            }
        }
        
        return borrowedInstances;
    }
}