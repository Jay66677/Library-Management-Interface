import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String author;
    private String bookId;
    private boolean isAvailable;
    private int numberOfCopies;
    private int availableCopies;

    public Book(String title, String author, String bookId, int numberOfCopies) {
        this.title = title;
        this.author = author;
        this.bookId = bookId;
        this.numberOfCopies = numberOfCopies;
        this.availableCopies = numberOfCopies;
        this.isAvailable = (numberOfCopies > 0);
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
        updateAvailability();
    }
    
    public int getAvailableCopies() {
        return availableCopies;
    }
    
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        updateAvailability();
    }
    
    public boolean borrowBook() {
        if (availableCopies > 0) {
            availableCopies--;
            updateAvailability();
            return true;
        }
        return false;
    }

    public void returnBook() {
        if (availableCopies < numberOfCopies) {
            availableCopies++;
            updateAvailability();
        }
    }

    private void updateAvailability() {
        isAvailable = (availableCopies > 0);
    }
    
    public void restock(int additionalCopies) {
        if (additionalCopies > 0) {
            this.numberOfCopies += additionalCopies;
            this.availableCopies += additionalCopies;
            updateAvailability();
        }
    }
    
    @Override
    public String toString() {
        return "Title: " + title + 
               "\nAuthor: " + author + 
               "\nBook ID: " + bookId + 
               "\nAvailable: " + (isAvailable ? "Yes" : "No") + 
               "\nAvailable Copies: " + availableCopies + 
               "\nTotal Copies: " + numberOfCopies;
    }
}
    

