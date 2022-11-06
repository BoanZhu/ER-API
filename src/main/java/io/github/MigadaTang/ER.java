package io.github.MigadaTang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.MigadaTang.dao.*;
import io.github.MigadaTang.exception.ERException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
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
import java.util.List;

public class ER {
    public static SqlSession sqlSession;
    public static AttributeMapper attributeMapper;
    public static EntityMapper entityMapper;
    public static RelationshipMapper relationshipMapper;
    public static SchemaMapper schemaMapper;
    public static LayoutInfoMapper layoutInfoMapper;

    public static void initialize(boolean usePostgre) throws SQLException, IOException {
        String XMLPath = "mybatis-config.xml";
        if (usePostgre) {
            XMLPath = "mybatis-config-postgre.xml";
        }
        InputStream is = Resources.getResourceAsStream(XMLPath);
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        sqlSession = sqlSessionFactory.openSession(true);
        attributeMapper = sqlSession.getMapper(AttributeMapper.class);
        entityMapper = sqlSession.getMapper(EntityMapper.class);
        relationshipMapper = sqlSession.getMapper(RelationshipMapper.class);
        schemaMapper = sqlSession.getMapper(SchemaMapper.class);
        layoutInfoMapper = sqlSession.getMapper(LayoutInfoMapper.class);
        if (!usePostgre) {
            createTables();
        }
    }

    private static void createTables() throws SQLException, IOException {
        Connection conn = sqlSession.getConnection();
        Statement stmt = conn.createStatement();
        String sql = new String(Resources.getResourceAsStream("schema-v1.sql").readAllBytes(), StandardCharsets.UTF_8);
        stmt.execute(sql);
    }

    public static Schema createSchema(String name, String creator) {
        return new Schema(0L, name, new ArrayList<>(), new ArrayList<>(), creator, new Date(), new Date());
    }


    public static void deleteSchema(Schema schema) {
        schema.deleteDB();
    }

    public static List<Schema> queryAllSchema() {
        return Schema.queryAll();
    }

    public static Schema querySchemaByID(Long ID) {
        return Schema.queryByID(ID);
    }

    public static Schema loadFromJSON(String json) throws ERException {
        try {
            return new ObjectMapper().readValue(json, Schema.class);
        } catch (JsonProcessingException e) {
            throw new ERException(String.format("loadFromJSON fail, error: %s", e.getMessage()));
        }
    }
}
