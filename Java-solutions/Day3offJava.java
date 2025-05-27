// Exercise 31: Basic JDBC Connection
import java.sql.*;

class BasicJDBCConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:students.db";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to the database");
                
                // Create table if not exists
                String createTable = """
                    CREATE TABLE IF NOT EXISTS students (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        age INTEGER
                    )
                    """;
                
                Statement stmt = conn.createStatement();
                stmt.execute(createTable);
                
                // Insert sample data
                String insert = "INSERT INTO students (name, age) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insert);
                pstmt.setString(1, "John Doe");
                pstmt.setInt(2, 20);
                pstmt.executeUpdate();
                
                // Select data
                String select = "SELECT * FROM students";
                ResultSet rs = stmt.executeQuery(select);
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                     ", Name: " + rs.getString("name") + 
                                     ", Age: " + rs.getInt("age"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}

// Exercise 32: Insert and Update Operations in JDBC
class StudentDAO {
    private String url = "jdbc:sqlite:students.db";
    
    public void insertStudent(String name, int age) {
        String sql = "INSERT INTO students (name, age) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();
            System.out.println("Student inserted successfully");
            
        } catch (SQLException e) {
            System.out.println("Error inserting student: " + e.getMessage());
        }
    }
    
    public void updateStudent(int id, String name, int age) {
        String sql = "UPDATE students SET name = ?, age = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Student updated successfully");
            
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        dao.insertStudent("Alice Smith", 22);
        dao.updateStudent(1, "John Updated", 21);
    }
}

// Exercise 33: Transaction Handling in JDBC
class TransactionHandling {
    private String url = "jdbc:sqlite:bank.db";
    
    public void setupDatabase() {
        String createTable = """
            CREATE TABLE IF NOT EXISTS accounts (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                balance DECIMAL(10,2)
            )
            """;
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTable);
            
            // Insert sample accounts
            stmt.execute("INSERT OR IGNORE INTO accounts VALUES (1, 'Alice', 1000.00)");
            stmt.execute("INSERT OR IGNORE INTO accounts VALUES (2, 'Bob', 500.00)");
            
        } catch (SQLException e) {
            System.out.println("Error setting up database: " + e.getMessage());
        }
    }
    
    public void transferMoney(int fromAccount, int toAccount, double amount) {
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);  // Start transaction
            
            // Debit from source account
            String debitSQL = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            PreparedStatement debitStmt = conn.prepareStatement(debitSQL);
            debitStmt.setDouble(1, amount);
            debitStmt.setInt(2, fromAccount);
            debitStmt.executeUpdate();
            
            // Credit to destination account
            String creditSQL = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            PreparedStatement creditStmt = conn.prepareStatement(creditSQL);
            creditStmt.setDouble(1, amount);
            creditStmt.setInt(2, toAccount);
            creditStmt.executeUpdate();
            
            conn.commit();  // Commit transaction
            System.out.println("Transfer successful: $" + amount + " from account " + 
                             fromAccount + " to account " + toAccount);
            
        } catch (SQLException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            // Rollback would happen automatically when connection closes
        }
    }
    
    public static void main(String[] args) {
        TransactionHandling th = new TransactionHandling();
        th.setupDatabase();
        th.transferMoney(1, 2, 100.0);
    }
}

// Exercise 34: Create and Use Java Modules
// Note: This requires proper module structure with module-info.java files
// Here's a simplified example showing the concept

// module-info.java for com.utils
/*
module com.utils {
    exports com.utils;
}
*/

// module-info.java for com.greetings
/*
module com.greetings {
    requires com.utils;
}
*/

// com/utils/StringUtils.java
package com.utils;

public class StringUtils {
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

// com/greetings/GreetingApp.java
package com.greetings;
import com.utils.StringUtils;

public class GreetingApp {
    public static void main(String[] args) {
        String message = StringUtils.capitalize("hello from modules!");
        System.out.println(message);
    }
}

// Exercise 35: TCP Client-Server Chat
import java.io.*;
import java.net.*;

// Server class
class ChatServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server listening on port 12345");
            
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client: " + inputLine);
                out.println("Echo: " + inputLine);
                
                if ("bye".equals(inputLine)) {
                    break;
                }
            }
            
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}

// Client class
class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("Server: " + in.readLine());
                
                if ("bye".equals(userInput)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}

// Exercise 36: HTTP Client API (Java 11+)
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class HTTPClientExample {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/users/octocat"))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();
        
        try {
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Headers: " + response.headers().map());
            System.out.println("Response Body: " + response.body());
            
        } catch (Exception e) {
            System.out.println("HTTP request failed: " + e.getMessage());
        }
    }
}

// Exercise 37: Using javap to Inspect Bytecode
class BytecodeExample {
    private int value;
    
    public BytecodeExample(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public int calculate(int x, int y) {
        return (x + y) * value;
    }
    
    public static void main(String[] args) {
        System.out.println("Compile this class and run: javap -c BytecodeExample");
        System.out.println("This will show the bytecode instructions for each method.");
        
        BytecodeExample example = new BytecodeExample(5);
        System.out.println("Result: " + example.calculate(3, 4));
    }
}

// Exercise 38: Decompile a Class File
class DecompileExample {
    private String name;
    private int count;
    
    public DecompileExample(String name) {
        this.name = name;
        this.count = 0;
    }
    
    public void increment() {
        count++;
    }
    
    public String getInfo() {
        return name + ": " + count;
    }
    
    public static void main(String[] args) {
        System.out.println("Compile this class and use JD-GUI or CFR to decompile the .class file");
        System.out.println("Example commands:");
        System.out.println("java -jar cfr.jar DecompileExample.class");
        
        DecompileExample example = new DecompileExample("Test");
        example.increment();
        System.out.println(example.getInfo());
    }
}

// Exercise 39: Reflection in Java
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

class ReflectionTarget {
    private String name;
    private int value;
    
    public ReflectionTarget() {
        this.name = "Default";
        this.value = 0;
    }
    
    public ReflectionTarget(String name, int value) {
        this.name = name;
        this.value = value;
    }
    
    public void displayInfo() {
        System.out.println("Name: " + name + ", Value: " + value);
    }
    
    private void secretMethod() {
        System.out.println("This is a private method!");
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

class ReflectionExample {
    public static void main(String[] args) {
        try {
            // Load class
            Class<?> clazz = Class.forName("ReflectionTarget");
            System.out.println("Class name: " + clazz.getName());
            
            // Get constructors
            Constructor<?>[] constructors = clazz.getConstructors();
            System.out.println("\nConstructors:");
            for (Constructor<?> constructor : constructors) {
                System.out.println(constructor);
            }
            
            // Get methods
            Method[] methods = clazz.getDeclaredMethods();
            System.out.println("\nMethods:");
            for (Method method : methods) {
                System.out.println(method.getName() + " - " + method.getParameterCount() + " parameters");
            }
            
            // Get fields
            Field[] fields = clazz.getDeclaredFields();
            System.out.println("\nFields:");
            for (Field field : fields) {
                System.out.println(field.getName() + " - " + field.getType());
            }
            
            // Create instance and invoke methods
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method displayMethod = clazz.getMethod("displayInfo");
            displayMethod.invoke(instance);
            
            // Invoke method with parameters
            Method setNameMethod = clazz.getMethod("setName", String.class);
            setNameMethod.invoke(instance, "Reflection Test");
            displayMethod.invoke(instance);
            
            // Access private method
            Method secretMethod = clazz.getDeclaredMethod("secretMethod");
            secretMethod.setAccessible(true);
            secretMethod.invoke(instance);
            
        } catch (Exception e) {
            System.out.println("Reflection error: " + e.getMessage());
        }
    }
}

// Exercise 40: Virtual Threads (Java 21)
class VirtualThreadsExample {
    public static void main(String[] args) {
        System.out.println("Starting virtual threads example...");
        
        long startTime = System.currentTimeMillis();
        
        // Create 100,000 virtual threads
        for (int i = 0; i < 100_000; i++) {
            final int threadId = i;
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(1000); // Simulate some work
                    if (threadId % 10_000 == 0) {
                        System.out.println("Virtual thread " + threadId + " completed");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Wait a bit for threads to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Virtual threads example completed in " + (endTime - startTime) + "ms");
        
        // Compare with platform threads (smaller number)
        System.out.println("\nStarting platform threads example...");
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) { // Much smaller number for platform threads
            final int threadId = i;
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    if (threadId % 100 == 0) {
                        System.out.println("Platform thread " + threadId + " completed");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        endTime = System.currentTimeMillis();
        System.out.println("Platform threads example completed in " + (endTime - startTime) + "ms");
    }
}

// Exercise 41: Executor Service and Callable
import java.util.concurrent.*;
import java.util.ArrayList;

class CallableTask implements Callable<Integer> {
    private int number;
    
    public CallableTask(int number) {
        this.number = number;
    }
    
    @Override
    public Integer call() throws Exception {
        // Simulate some computation
        Thread.sleep(1000);
        return number * number;
    }
}

class ExecutorServiceExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Create a list of Callable tasks
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        
        // Submit tasks
        for (int i = 1; i <= 10; i++) {
            Future<Integer> future = executor.submit(new CallableTask(i));
            futures.add(future);
        }
        
        // Collect results
        System.out.println("Collecting results from Callable tasks:");
        for (int i = 0; i < futures.size(); i++) {
            try {
                Integer result = futures.get(i).get(); // This blocks until result is available
                System.out.println("Task " + (i + 1) + " result: " + result);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error getting result: " + e.getMessage());
            }
        }
        
        // Shutdown executor
        executor.shutdown();
        
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        System.out.println("All tasks completed!");
        
        // Example with invokeAll
        ExecutorService executor2 = Executors.newFixedThreadPool(3);
        
        ArrayList<CallableTask> tasks = new ArrayList<>();
        for (int i = 11; i <= 15; i++) {
            tasks.add(new CallableTask(i));
        }
        
        try {
            System.out.println("\nUsing invokeAll:");
            List<Future<Integer>> results = executor2.invokeAll(tasks);
            
            for (int i = 0; i < results.size(); i++) {
                try {
                    Integer result = results.get(i).get();
                    System.out.println("InvokeAll task " + (i + 1) + " result: " + result);
                } catch (ExecutionException e) {
                    System.out.println("Task execution error: " + e.getMessage());
                }
            }
            
        } catch (InterruptedException e) {
            System.out.println("InvokeAll interrupted: " + e.getMessage());
        }
        
        executor2.shutdown();
        System.out.println("Executor service example completed!");
    }
}

// Main class to demonstrate all exercises
class CoreJavaExercisesMain {
    public static void main(String[] args) {
        System.out.println("=== Core Java Exercises Complete Implementation ===");
        System.out.println("This file contains implementations for all 41 exercises.");
        System.out.println("Each exercise is implemented as a separate class.");
        System.out.println("To run individual exercises, copy the relevant class to a separate file.");
        System.out.println("Some exercises require specific setup (like database or external libraries).");
        System.out.println("\nExercise Categories:");
        System.out.println("1-11: Basic Java concepts");
        System.out.println("12-16: Methods and String manipulation");
        System.out.println("17-19: OOP concepts");
        System.out.println("20-23: Exception handling and File I/O");
        System.out.println("24-25: Collections");
        System.out.println("26-28: Multithreading and functional programming");
        System.out.println("29-30: Modern Java features");
        System.out.println("31-33: JDBC and database operations");
        System.out.println("34-35: Modules and networking");
        System.out.println("36-41: Advanced topics and modern features");
    }
}
