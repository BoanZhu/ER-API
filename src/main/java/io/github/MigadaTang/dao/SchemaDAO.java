package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.entity.SchemaDO;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class SchemaDAO {
    public static List<SchemaDO> selectAll() {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        List<SchemaDO> dos = mapper.selectAll();
        sqlSession.close();
        return dos;
    }

    public static List<SchemaDO> selectBySchema(SchemaDO schemaDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        List<SchemaDO> dos = mapper.selectBySchema(schemaDO);
        sqlSession.close();
        return dos;
    }

    public static SchemaDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        SchemaDO schemaDO = mapper.selectByID(ID);
        sqlSession.close();
        return schemaDO;
    }

    public static int insert(SchemaDO schemaDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        int ret = mapper.insert(schemaDO);
        sqlSession.close();
        return ret;
    }

    public static int deleteByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        int ret = mapper.deleteByID(ID);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(SchemaDO schemaDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        SchemaMapper mapper = sqlSession.getMapper(SchemaMapper.class);
        int ret = mapper.updateByID(schemaDO);
        sqlSession.close();
        return ret;
    }

}
