// Exercise 22: File Writing
import java.io.FileWriter;
import java.io.IOException;

class FileWritingExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter a string to write to file: ");
        String input = scanner.nextLine();
        
        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(input);
            System.out.println("Data has been written to output.txt");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
        
        scanner.close();
    }
}

// Exercise 23: File Reading
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class FileReadingExample {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            System.out.println("Contents of output.txt:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}

// Exercise 24: ArrayList Example
import java.util.ArrayList;

class ArrayListExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> studentNames = new ArrayList<>();
        
        System.out.println("Enter student names (type 'done' to finish):");
        
        while (true) {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            
            if (name.equalsIgnoreCase("done")) {
                break;
            }
            
            studentNames.add(name);
        }
        
        System.out.println("\nStudent names entered:");
        for (String name : studentNames) {
            System.out.println(name);
        }
        
        scanner.close();
    }
}

// Exercise 25: HashMap Example
import java.util.HashMap;

class HashMapExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer, String> studentMap = new HashMap<>();
        
        System.out.println("Enter student data (type -1 for ID to finish):");
        
        while (true) {
            System.out.print("Enter student ID: ");
            int id = scanner.nextInt();
            
            if (id == -1) {
                break;
            }
            
            scanner.nextLine(); // consume newline
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            
            studentMap.put(id, name);
        }
        
        System.out.print("Enter ID to retrieve name: ");
        int searchId = scanner.nextInt();
        
        String name = studentMap.get(searchId);
        if (name != null) {
            System.out.println("Student name: " + name);
        } else {
            System.out.println("Student not found!");
        }
        
        scanner.close();
    }
}

// Exercise 26: Thread Creation
class MyThread extends Thread {
    private String threadName;
    
    public MyThread(String name) {
        this.threadName = name;
    }
    
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(threadName + " - Count: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(threadName + " interrupted");
            }
        }
    }
}

class ThreadExample {
    public static void main(String[] args) {
        MyThread thread1 = new MyThread("Thread-1");
        MyThread thread2 = new MyThread("Thread-2");
        
        thread1.start();
        thread2.start();
    }
}

// Exercise 27: Lambda Expressions
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LambdaExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "David");
        
        System.out.println("Original list: " + names);
        
        // Sort using lambda expression
        Collections.sort(names, (a, b) -> a.compareTo(b));
        
        System.out.println("Sorted list: " + names);
        
        // Sort by length using lambda
        Collections.sort(names, (a, b) -> Integer.compare(a.length(), b.length()));
        
        System.out.println("Sorted by length: " + names);
    }
}

// Exercise 28: Stream API
import java.util.stream.Collectors;

class StreamAPIExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("Original numbers: " + numbers);
        
        // Filter even numbers using Stream API
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        
        System.out.println("Even numbers: " + evenNumbers);
        
        // Square all numbers
        List<Integer> squaredNumbers = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        
        System.out.println("Squared numbers: " + squaredNumbers);
    }
}

// Exercise 29: Records (Java 16+)
record Person(String name, int age) {
    // Records automatically generate constructor, getters, equals, hashCode, toString
}

class RecordsExample {
    public static void main(String[] args) {
        Person person1 = new Person("Alice", 30);
        Person person2 = new Person("Bob", 25);
        Person person3 = new Person("Charlie", 35);
        
        System.out.println(person1);
        System.out.println(person2);
        System.out.println(person3);
        
        List<Person> people = Arrays.asList(person1, person2, person3);
        
        // Filter people over 25 using Streams
        List<Person> adults = people.stream()
            .filter(p -> p.age() > 25)
            .collect(Collectors.toList());
        
        System.out.println("People over 25: " + adults);
    }
}

// Exercise 30: Pattern Matching for switch (Java 21)
class PatternMatchingSwitch {
    public static void processObject(Object obj) {
        String result = switch (obj) {
            case Integer i -> "Integer with value: " + i;
            case String s -> "String with length: " + s.length();
            case Double d -> "Double with value: " + d;
            case null -> "Null object";
            default -> "Unknown type: " + obj.getClass().getSimpleName();
        };
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        processObject(42);
        processObject("Hello World");
        processObject(3.14);
        processObject(null);
        processObject(Arrays.asList(1, 2, 3));
    }
}
