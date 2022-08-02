# simple-jdbc
This is a simple lab you can follow to build a basic boilerplate JDBC project. When you complete this lab you will have a database
with one or more tables and a Java application with CRUD functionality that can access those tables.


start with dbeaver - connect & create tables
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
	user_id SERIAL, /*This is the self-incrementing data type in postgresql. Some other SQL flavors will implement this functionality differently*/
	username VARCHAR(200) NOT NULL,
	email VARCHAR(200) NOT NULL,
	"password" VARCHAR(200) NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_id) /*We could also simply put "PRIMARY KEY" on the user_id definition*/
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

