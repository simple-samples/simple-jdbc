# simple-jdbc
This is a simple lab you can follow to build a basic boilerplate JDBC project. When you complete this lab you will have a database
with one or more tables and a Java application with CRUD functionality that can access those tables. Start by launching DBeaver, and creating a new maven project in IntelliJ.

## Project Setup & PostgreSQL Dependency
Create a new maven project and add the following dependency to your POM.xml:

```XML
<!-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.4.0</version>
</dependency>
```


## Create Tables
First we want to create tables. To do this we will connect DBeaver to our database and write the SQL statements.  

Start by establishing a connection to the database. You will need to know the host, port, username, and password in order to connect. This lab is designed to use a PostgreSQL database. It is assumed the database already exists and is reachable. Create a new connection in DBeaver and enter the host, post, username, and password into the new connection window. While you are there, select the PostgreSQL tab and check the box that reads "Show all databases". Hit OK to save the connection, and then connect.  
  
Once connected to the database (if you expand the database in the Database Navigator it will establish a connection) start a new SQL script. You can do this by clicking on your database > SQL Editor > New SQL Script. Now you should have a blank text window where you can type and execute SQL statements. At the top of the screen verify you are connected to the correct server and you are working with the correct database and schema.  

Use the following SQL statements to create two tables:
```SQL
CREATE TABLE users (
	user_id SERIAL,
	username VARCHAR(200) NOT NULL,
	email VARCHAR(200) NOT NULL,
	"password" VARCHAR(200) NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_id)
);
```

```SQL
CREATE TABLE tasks (
	task_id SERIAL PRIMARY KEY,
	title VARCHAR(60) NOT NULL,
	message VARCHAR(2000),
	user_id INT,
	completed BOOL,
	CONSTRAINT tasks_users_fk FOREIGN KEY (user_id) REFERENCES users (user_id)
);
```

Note the data type for the id columns. In PostgreSQL the `SERIAL` data type is a self-incrtementing integer. Other database vendors implement this functionality differently. The purpose of this is to have an id field which increments and assigns IDs automatically.

Now that we have our tables built we can insert data into them. There's no need to do so now, but below are example statements to do this. We will refer back to these examples later when it comes time to write our JDBC code.

INSERT (create): 
```SQL
INSERT INTO users (username, email, password)
VALUES ('trainer', 'trainer@Revature.com', 'P4ssW0rd1!');
```

SELECT (read):
```SQL
SELECT *
FROM users
WHERE user_id = 999;
```

UPDATE:
```SQL
UPDATE users 
SET username = 'newUserName',
email = 'newEmail@email.com',
password = 'newPass123!';
```

DELETE:
```SQL
DELETE FROM users
WHERE user_id = 999;
```

## Properties File
Next we need to save the credentials to access the database with our Java application, but we also want to make sure we don't publish these credentials to our git repository where someone might find them. To do this we are going to pull the info from a file that will be ignored by git. Start by adding the following line to your .gitignore:
```
*.properties
```
This tells git to ignore all files which end with ".properties". We are going to create a properties file and save our connection info there. Create a new file in the resources root directory (src/main/resources/), name it `jdbc.properties`. In that file add your connection info in the form of key value pairs like the example below:
```
# Connection info for Kyle's training db
host=training-kyle-p.cvtq9j4axrge.us-east-1.rds.amazonaws.com
port=5432
driver=org.postgresql.Driver
username=postgres
password=password123
dbname=Revagenda
schema=public
```

Note the format of this file. This is a series of key/value pairs. Each line has one pair, like this: `key=value`. 


## Datbase Connection
The first thing we need to do is establish a connection to our database. Now that we have the properties file ready to go, we will be picking up the data inside and using it to build a connection string. For PostgreSQL the connection string will look like this:
```
jdbc:postgresql://HOSTNAME:PORT/DATABASENAME?user=USERNAME&password=PASSWORD&curentSchema=SCHEMA
```
Note that each of the tokens in caps are values we want to pull from the `jdbc.properties` file.  
  
Start by creating a new class, the ConnectionManager. Give it a method to establish a connection. You can build this in a number of ways, this lab will suggest building a static Connection reference, a private constructor, and a public static method which will mantain exactly one Conncetion object (see: Singleton Design Pattern).

```Java
public class ConnectionManager {
	private static Connection connection;
  
	private ConnectionManager() {
  
	}
  
	public static Connection getConnection() {
        if(connection == null) {
            connect();
        }
        return connection;
    }
}
```

Now we just need to write the connect() method to do most of the work. First let's stub out this method, and then we will add in the functionality:

```Java
    private static void connect() {
	try {
		//Step 1: Pull the properties
		
		//Step 2: create the connection string
		
		//Step 3: connect
	
	} catch (Exception e) {
		e.printStackTrace();
	}
```

We will now go over each of those 3 steps in the code stub above. First we need to pull the information from the `jdbc.properties` file we created earlier. If the file is correctly located in the maven sources root directory, the file will get packaged into the application on the classpath. Then we can access it like this:

```Java
		//Step 1: Pull the properties
		Properties props = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream input = loader.getResourceAsStream("jdbc.properties");
		props.load(input);
```

We set up a `Properties` object called `props` to hold the info from our file. Then we get a `ClassLoader` called `loader` and use that to get an `InputStream` called `input` which is a buffer containing the contents of the `jdbc.properties` file. Finally we use our `props` object to call `load(input)` which parses the key/value pairs in the file. Now we can access those values in the next steps from our `props` object.  
  
Next we want to use these properties to assemble the connection string. We will be hard coding parts of the string, and filling in the variables. Remember we will want the string that looks like this:
```
jdbc:postgresql://HOSTNAME:PORT/DATABASENAME?user=USERNAME&password=PASSWORD&curentSchema=SCHEMA
```

So, if we used the example data above the completed string would look like this:
```
jdbc:postgresql://training-kyle-p.cvtq9j4axrge.us-east-1.rds.amazonaws.com:5432/Revagenda?user=postgres&password=password123&curentSchema=public
```

We will get the values out of our `props` object by repeatedly calling the `getProperty()` method, each time with the key as the parameter to retrieve the associates value.

```Java
	//Step 2: create the connection string
	String host = props.getProperty("host");
	String port = props.getProperty("port");
	String dbname = props.getProperty("dbname");
	String username = props.getProperty("username");
	String password = props.getProperty("password");
	String schema = props.getProperty("schema");
	String driver = props.getProperty("driver");

	//then concatinate these tokens into a completed string:
	StringBuilder builder = new
	StringBuilder("jdbc:postgresql://");
	builder.append(host);
	builder.append(":");
	builder.append(port);
	builder.append("/");
	builder.append(dbname);
	builder.append("?user=");
	builder.append(username);
	builder.append("&password=");
	builder.append(password);
	builder.append("&currentSchema=");
	builder.append(schema);
```
We declare a `StringBuilder` and then use it to concatinate all of the string parts together. Now we are finally ready to establish our connection. Before we do that, we should look at one other little thing. Sometimes, for some reason, the JVM will not properly load the driver class needed to connect. It should be loaded into memory for us, sometimes it doesn't. So sometimes we have to use a hack to force the JVM to load the necessary code. We can simply do the following:
```Java
Class.forName(driver);
```
`driver` is a string pulled from the `jdbc.properties` file which has the fully qualified name of the class that the JVM needs to load. We actually discard the result of this method call, we don't need it. This line would return the class to us so we could reflect on it, but we don't have to. Once the JVM has loaded it into memory (which happens implicitly because of this method call) we don't care about the rest.  
  
So, finally, here's the code for step 3:
```Java
	Class.forName(driver);
	connection = DriverManager.getConnection(builder.toString());
```


That's the whole connection manager class. Now we can call the static method `ConnectionManager.getConnection()` which will return a working connection object reference. We set this up as a singleton, so there should only ever be one object in existence. 

## Data Access Objects
Next we will design an interface that our DAOs will implement. Recall that an interface in Java sets forth a promise that any class which implements the interface will implement it's abstract methods.  
  
Create a new interface called DataSourceCRUD. Any class which implements this interface should be capable of preforming CRUD (create, read, update, delete) functions. This interface will be generic, and the type will be resolved by each implementing class.

```Java
public interface DataSourceCRUD<T> {
    void create(T t);
    T read(int id);
    List<T> readAll();
    void update(T t);
    void delete(int id);
}
```
Now we will create two concrete classes which implement this interface. Each will need to provide its own implementations for the 5 methods above. Create a `UserDAO` and a `TaskDAO` class.

```Java
//UserDAO
public class UserDAO implements DataSourceCRUD<User> {

    Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }
}
```

```Java
//TaskDAO
public class TaskDAO implements DataSourceCRUD<User> {

    Connection connection;

    public TaskDAO(Connection connection) {
        this.connection = connection;
    }
}
```

You may have noticed IntelliJ giving you errors about the classes we just made. This is because it insists that you implement those methods defined in the interface. So, go ahead and let IntelliJ stub out those methods for you by hovering over the error and clicking "implement methods...".  
  
Now you should have 5 override methods which all lack a concrete implementation. We are going to write the implementations for the UserDAO first. Let's start with `public void create(User user);`  
  
Each of these CRUD methods are going to work very similarly. We will create a SQL string, parameterize it, execute it, and if necessary marshal the results. Let's start by stubbing out the method:

```Java
	@Override
	public void create(User user) {
		try {
			//Step 1: write the statement

			//Step 2: Parameterize the statement

			//Step 3: Execute the statement

			//Step 4: Marshal the results if necessary
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
```

The first thing we need to do here is write our SQL statement. Start by writing in a string the exact SQL code needed to add a new row into the database. Refer to the insert statement near the beginning of this lab.

```Java
String sql = "INSERT INTO users (username, email, password) VALUES ('trainer', 'trainer@Revature.com', 'P4ssW0rd1!')";
```
Now we want to remove those values and add in placeholders. In the next step we will be parameterizing those placeholders. Java JDBC uses the question mark `?` character to represent a variable.
```Java
String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
```
Our string is ready to go. We want to turn this into a `PreparedStatement` which will allow us to safely parameterize those variables. The complete code for Step 1:

```Java
	//Step 1: write the statement
	String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
	PreparedStatement pstmt = connection.prepareStatement(sql);
```
We take that SQL string and hand it off to the `connection.prepareStatement()` method. This string is used to make the statement. Next we need to parameterize those variables. This is the process of setting the values we want to insert. Parameterizing them in this way gives you protection against [SQL Injection](https://xkcd.com/327/) and the variables not being hard coded allows this statement to be re-used with any User information.  
  
To parameterize the `PreparedStatement` we use it's set methods. There are several, each for a different data type. We need to call the appropriate method: `setString()` for Strings,`setInteger()` for Integers, and so on. Each of these methods takes 2 parameters, the index and the value. The index represents which `?` is being parameterized, and the value is what is concatinated into the string. Keep in mind these indices start a 1, not at 0. They refer to the `?`s from left to right, so for our statement above we will want to set parameter 1 to the username, 2 to the email, and 3 to the password.

```Java
	//Step 2: Parameterize the statement
	pstmt.setString(1, user.getUserName());
	pstmt.setString(2, user.getEmail());
	pstmt.setString(3, user.getPassword());
```
Note that we are pulling the data out of the user object that get passed to this method in the parameter list.  
  
The next thing to do is execute our statement. We can choose one of several methods on the `PreparedStatement` object: `execute()`, `executeUpdate()`, and `executeQuery()`.  
  
 - `execute()` returns a bool indicating success or failure.
 - `executeUpdate()` returns an int indicating how many rows were affected.
 - `executeQuery()` returns a ResultSet object containing the results of the query.

```Java
	//Step 3: Execute the statement
	pstmt.executeUpdate();
```
Note that we don't do anything with the `int` returned from this method call. We could if we cared about the affected row count.  
  
The `create()` method is now complete. We don't need to worry about marshaling a result set here. Next we will implement the `update()` and `delete()` methods. Because these are all so similar to create, here are the completed methods:

```Java
    @Override
    public void update(User user) {

        try {
            String sql = "UPDATE users SET user_name = ?, email = ?, password = ?, WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getUserId());
            pstmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
```

```Java
    @Override
    public void delete(int id) {
        try {
            String sql = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
```
Lastly we will implement the `read()` and `readAll()` methods. These are also similar to the last few, with the added step of marshaling the results. So here is the completed `read()` method.

```Java
@Override
public User read(int id) {
	User user = new User();
	try {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, id);
		ResultSet results = pstmt.executeQuery();


		//Marshal the results
		if(results.next()) {
			user.setUserId(results.getInt("user_id"));
			user.setUserName(results.getString("user_name"));
			user.setEmail(results.getString("email"));
			user.setPassword(results.getString("password"));
		}

	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	return user;
}
```
Take a look at the extra code here under `//Marshal the results`. A ResultSet can be thought of like a database table, or an excel spreadsheet. There are several rows, depending on how many results we get from our query. There are several columns, matching the columns of the table we queried. We are going to be looping through the set, working with one row at a time. In this case there is only one row, as we queried the database for one unique user. Next in the `readAll()` method we will loop through several results:

```Java
@Override
    public List<User> readAll() {
        List<User> userList = new LinkedList<>();
        try {
			String sql = "SELECT * FROM users";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			ResultSet results = pstmt.executeQuery();
			
			
			while(results.next()) {
				User user = new User();
				user.setUserId(results.getInt("user_id"));
				user.setUserName(results.getString("user_name"));
				user.setEmail(results.getString("email"));
				user.setPassword(results.getString("password"));
				userList.add(user);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userList;
	}
```
Here we looped through the results in a `while` loop. Note how both the if statement above and the while loop here use the `results.next()` method. This is how we iterate through the set. This is similar to the `Iterable` interface which has two methods `hasNext()` and `next()`. The `ResultSet` `next()` is a combination of both. It advances the cursor if possible, or returns false if there is no next row. Remember that the ResultSet starts with the cursor NOT POINTING to the first item in the list. `next()` must be called once to advance the cursor to the first item in the list. Hence why we call it inside the if statement - if there is a result, use it, else skip over the block.  
  
The last thing to note is what we do with each row in the set. We pull out the value from the database by calling the `ResultSet` object's get methods. Just like the `PreparedStatement` from earlier which has several set methods for different types, this has several get methods, one for each type. These methods take in a string parameter which is the column name from the database table.
  
## TaskDAO Implementation
After studying the code above you should be able to produce implementations for the TaskDAO class. It will be very similar to the UserDAO we just completed. Take a look at the table definition at the top of this document, keep that in mind as you write the SQL scripts. Remember, each CRUD method will preform almost the same exact steps: Write the SQL string, parameterize it, execute it, and if necessary marshal the results.
