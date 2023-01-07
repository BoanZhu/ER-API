package io.github.MigadaTang.dao;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.entity.LayoutInfoDO;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class LayoutInfoDAO {

    public static LayoutInfoDO selectByID(Long ID) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        LayoutInfoDO LayoutInfoDO = mapper.selectByID(ID);
        sqlSession.close();
        return LayoutInfoDO;
    }

    public static LayoutInfoDO selectByBelongObjID(Long relatedObjID, BelongObjType belongObjType) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        LayoutInfoDO LayoutInfoDO = mapper.selectByBelongObjID(relatedObjID, belongObjType);
        sqlSession.close();
        return LayoutInfoDO;
    }

    public static List<LayoutInfoDO> selectByLayoutInfo(LayoutInfoDO layoutInfoDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        List<LayoutInfoDO> dos = mapper.selectByLayoutInfo(layoutInfoDO);
        sqlSession.close();
        return dos;
    }

    public static int insert(LayoutInfoDO layoutInfoDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        int ret = mapper.insert(layoutInfoDO);
        sqlSession.close();
        return ret;
    }

    public static int updateByID(LayoutInfoDO layoutInfoDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        int ret = mapper.updateByID(layoutInfoDO);
        sqlSession.close();
        return ret;
    }

    public static int updateByObjID(LayoutInfoDO layoutInfoDO) {
        SqlSession sqlSession = ER.sqlSessionFactory.openSession(true);
        LayoutInfoMapper mapper = sqlSession.getMapper(LayoutInfoMapper.class);
        int ret = mapper.updateByObjID(layoutInfoDO);
        sqlSession.close();
        return ret;
    }

}
