package com.mires.hackatonipn.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {
    private final HikariDataSource dataSource;

    public ConnectionPoolManager() {
        final int threads = Runtime.getRuntime().availableProcessors() * 2 + 1;
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/hackaton?useSSL=false");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername("root");
        config.setPassword("root");
        config.setMinimumIdle(threads);
        config.setMaximumPoolSize(threads);
        config.setConnectionTimeout(3000000);
        config.setConnectionTestQuery("SELECT 1");
        dataSource = new HikariDataSource(config);
        System.out.println("Connection pool initialized.");
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close(final Connection conn, final PreparedStatement ps, final ResultSet res) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (res != null) {
            try {
                res.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
