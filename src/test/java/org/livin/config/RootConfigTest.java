package org.livin.config;

import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class) //중요
@ContextConfiguration(classes = {RootConfig.class}) //중요
@Log4j2
class RootConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    @DisplayName("DataSource 연결")
    void dataSource() throws SQLException {
        try(Connection conn = dataSource.getConnection()){
            log.info("DataSource 준비 완료");
            log.info(conn);
        }
    }

    @Test
    public void testSqlSessionFactory(){
        try(SqlSession session = sqlSessionFactory.openSession();
            Connection conn = session.getConnection()){
            log.info(session);
            log.info(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}