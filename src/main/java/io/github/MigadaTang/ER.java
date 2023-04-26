package io.github.MigadaTang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ERException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.DatabaseUtil;
import java.sql.PreparedStatement;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * The entrance class for ER diagram.
 */
public class ER {
    public static SqlSessionFactory sqlSessionFactory;

    /**
     * This function initializes the database and all the related mappers required by this tool
     *
     * @param dbType       the type of the database
     * @param hostname     the hostname of the database
     * @param portNum      the port of the database
     * @param databaseName the name of the database
     * @param username     username
     * @param password     password
     * @throws SQLException   exception that might happen during table creation
     * @throws ParseException supported dbType
     */
    public static void initialize(RDBMSType dbType, String hostname, String portNum, String databaseName, String username, String password) throws SQLException, ParseException {
        Properties properties = new Properties();
        properties.setProperty("jdbc.driverClassName", DatabaseUtil.recognDriver(dbType));
        properties.setProperty("jdbc.url", DatabaseUtil.generateDatabaseURL(dbType, hostname, portNum, databaseName));
        properties.setProperty("jdbc.username", username);
        properties.setProperty("jdbc.password", password);
        InputStream mybatisConfig = null;
        try {
            mybatisConfig = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfig, properties);
        if (dbType == RDBMSType.H2) {
            createTables();
        }
    }

    /**
     * The easiest way to initialize this tool. Use the h2 memory database by default
     *
     * @throws SQLException   SQLException
     * @throws ParseException ParseException
     */
    public static void initialize() throws SQLException, ParseException {
        initialize(RDBMSType.H2, "mem", "", "test", "sa", "");
    }

    private static void createTables() throws SQLException {
        Connection conn = sqlSessionFactory.openSession().getConnection();
        Statement stmt = conn.createStatement();
        try (InputStream inputStream = Resources.getResourceAsStream("mysql.sql")) {
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            stmt.execute(sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new ERException(e);
        }
    }

    /**
     * create an empty schema to start on which entities and relationships can be created.
     *
     * @param name the name of the schema
     * @return the created schema
     */
    public static Schema createSchema(String name) {
        return new Schema(0L, name, new ArrayList<>(), new ArrayList<>(), new Date(), new Date());
    }


    /**
     * Delete the current schema from the database and cascade delete all the components in this schema
     *
     * @param schema The schema object expected to be deleted
     */
    public static void deleteSchema(Schema schema) {
        schema.deleteDB();
    }


    /**
     * Load the json string into a schema object
     *
     * @param json the json string
     * @return The schema interpreted from the json string
     * @throws ERException Load json fail
     */
    public static Schema loadFromJSON(String json) throws ERException {
        try {
            return new ObjectMapper().readValue(json, Schema.class);
        } catch (JsonProcessingException e) {
            throw new ERException(String.format("loadFromJSON fail, error: %s", e.getMessage()));
        }
    }

    public static boolean connectToDatabaseAndExecuteSql(String dbType, String hostname, String portNum, String databaseName, String username, String password, String ddl)
        throws ParseException, DBConnectionException {

        RDBMSType databaseType;
        dbType = dbType.toLowerCase();
        if (dbType.equals("mysql")) {
            databaseType = RDBMSType.MYSQL;
        } else if (dbType.equals("oracle")) {
            databaseType = RDBMSType.ORACLE;
        } else if (dbType.equals("db2")) {
            databaseType = RDBMSType.DB2;
        } else if (dbType.equals("h2")) {
            databaseType = RDBMSType.H2;
        } else if (dbType.equals("postgresql")) {
            databaseType = RDBMSType.POSTGRESQL;
        } else if (dbType.equals("sqlserver")) {
            databaseType = RDBMSType.SQLSERVER;
        } else {
            throw new ParseException("Cannot recognise current RDBMS type: " + dbType);
        }

        String dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
        String driver = DatabaseUtil.recognDriver(databaseType);

        Connection connection = null;
        try {
            connection = DatabaseUtil.acquireDBConnection(driver, dbUrl, username, password);
            try (PreparedStatement ps = connection.prepareStatement(ddl)) {
                int result = ps.executeUpdate();
                System.out.println("Result: " + result);
            } catch (SQLException e) {
                throw new DBConnectionException("Fail to execute the sql query: " + e);
            }
        } finally {
            if (connection != null) {
                DatabaseUtil.closeDBConnection(connection);
            }
        }

        // if no exception happened, means the sql statements are executed successfully!
        return true;
    }
}
