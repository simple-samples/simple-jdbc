import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssociateRepo implements DataSourceCRUD<AssociateModel>{
    private final Connection connection;

    public AssociateRepo() {
        connection = ConnectionManager.getConnection();
    }

    @Override
    public AssociateModel create(AssociateModel model) {
        //JDBC logic here

        try {
            String sql = "INSERT INTO associates (associate_id, first_name, last_name, age) VALUES (?,?,?,?)";

            String partOne = "INSERT INTO ";
            //how to get table name? Reflection. Reflect on the class and name the table after it.
            // Or, you could have users of your library add your custom annotations in order to tell you
            //what the table name should be.
            String tableName = "";//so figure this part out.
            String partTwo = " (";
            String columnList = "";
            //Again, we need to reflect here. We want to get and iterate over all fields, and we want to create
            // a column name. We can also have the user use custom annotations here to specify the name.
            //We need to create a comma separated list of column names. Maybe keep track of the number of columns for later
            String partThree = ") VALUES (";
            String values = "?,?,?,?";
            String partFour = ")";



            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, model.getId());
            pstmt.setString(2, model.getFirstName());
            pstmt.setString(3, model.getLastName());
            pstmt.setInt(4, model.getAge());

            pstmt.executeUpdate();
            ResultSet rs =  pstmt.getGeneratedKeys();
            if(rs.next()) {
                rs.getInt(1);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public AssociateModel read(Integer id) {
        try {
            String sql = "SELECT * FROM associates WHERE associate_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            AssociateModel model = new AssociateModel();
            while(rs.next()) {
                model.setId(rs.getInt("associate_id"));
                model.setFirstName(rs.getString("first_name"));
                model.setLastName(rs.getString("last_name"));
                model.setAge(rs.getInt("age"));
            }

            return model;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AssociateModel update(AssociateModel model) {
        try {
            String sql = "UPDATE associates SET first_name = ?, last_name = ?, age = ? WHERE associate_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, model.getFirstName());
            pstmt.setString(2, model.getLastName());
            pstmt.setInt(3, model.getAge());
            pstmt.setInt(4, model.getId());

            pstmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

    @Override
    public void delete(Integer id) {
        try {
            String sql = "DELETE FROM associates WHERE associate_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
