import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class StudentBook implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String bookId;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String condition; // To track any damages before borrowing
    private double fine;
    private boolean isReturned;

    public StudentBook(String studentId, String bookId, String bookTitle, String condition) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14); // 2 weeks borrowing period
        this.condition = condition;
        this.fine = 0.0;
        this.isReturned = false;
    }

    // Getters and setters
    public String getStudentId() {
        return studentId;
    }

    public String getBookId() {
        return bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.isReturned = true;
        calculateFine();
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public double getFine() {
        if (!isReturned) {
            calculateFine();
        }
        return fine;
    }
    
    public void setFine(double fine) {
        this.fine = fine;
    }
    
    public boolean isReturned() {
        return isReturned;
    }
    
    // Calculate the fine for late submissions
    public void calculateFine() {
        if (isReturned) {
            if (returnDate.isAfter(dueDate)) {
                long weeksLate = ChronoUnit.WEEKS.between(dueDate, returnDate) + 1;
                fine = 0;
                for (int i = 1; i <= weeksLate; i++) {
                    fine += 10 * i;
                }
            }
        } else {
            // If not returned yet, calculate fine based on current date
            LocalDate today = LocalDate.now();
            if (today.isAfter(dueDate)) {
                long weeksLate = ChronoUnit.WEEKS.between(dueDate, today) + 1;
                fine = 0;
                for (int i = 1; i <= weeksLate; i++) {
                    fine += 10 * i;
                }
            }
        }
    }
    
    public boolean isDelayed() {
        if (isReturned) {
            return returnDate.isAfter(dueDate);
        } else {
            return LocalDate.now().isAfter(dueDate);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Book Title: ").append(bookTitle).append("\n");
        sb.append("Book ID: ").append(bookId).append("\n");
        sb.append("Borrow Date: ").append(borrowDate).append("\n");
        sb.append("Due Date: ").append(dueDate).append("\n");
        
        if (isReturned) {
            sb.append("Return Date: ").append(returnDate).append("\n");
        }
        
        sb.append("Condition Note: ").append(condition).append("\n");
        
        if (isDelayed()) {
            sb.append("Status: DELAYED\n");
            sb.append("Fine: Rs. ").append(fine).append("\n");
        } else if (isReturned) {
            sb.append("Status: RETURNED\n");
        } else {
            sb.append("Status: BORROWED (Due in ").append(ChronoUnit.DAYS.between(LocalDate.now(), dueDate)).append(" days)\n");
        }
        
        return sb.toString();
    }
}