# simple-jdbc
This is a simple lab you can follow to build a basic boilerplate JDBC project. When you complete this lab you will have a database
with one or more tables and a Java application with CRUD functionality that can access those tables. Start by launching DBeaver, and creating a new maven project in IntelliJ.



pull properties
establish Connection
build DAO interface & classes
preform CRUD with DAO



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

We will now go over each of those 3 steps in the code stub above. First we need to pull the information from the jdbc.properties file we created earlier. If the file is correctly located in the maven sources root directory, the file will get packaged into the application on the classpath. Then we can access it like this:

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
jdbc:postgresql://training-kyle-p.cvtq9j4axrge.us-east-1.rds.amazonaws.com:5432/Revagenda?user=postgres&password=password123&curentSchema=PUBLIC
```