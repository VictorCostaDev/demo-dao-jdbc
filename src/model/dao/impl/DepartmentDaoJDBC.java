package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao
{

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(
                "INSERT INTO department (Name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
                );
            pStatement.setString(1, department.getName());

            int rowsAffected = pStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pStatement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                }
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
        }
         
    }

    @Override
    public void update(Department department) {
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(
                "UPDATE department SET Name = ? WHERE Id = ?"
            );
            pStatement.setString(1, department.getName());
            pStatement.setInt(2, department.getId());

            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
        }
        
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(
                "DELETE FROM department WHERE id = ?"
            );
            pStatement.setInt(1, id);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;
        try {
            pStatement = conn.prepareStatement(
                "SELECT * FROM department WHERE Id = ?"
            );
            pStatement.setInt(1, id);
            resultSet = pStatement.executeQuery();
            if(resultSet.next()) {
                Department dep = instantiateDepartment(resultSet);
                return dep;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = conn.prepareStatement(
                "SELECT * from department ORDER BY Name"
            );

            resultSet = pStatement.executeQuery();

            List<Department> departmentList = new ArrayList<>();

            while(resultSet.next()) {
                Department dep = instantiateDepartment(resultSet);
                departmentList.add(dep);
            }
            return departmentList;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
            DB.closeResultSet(resultSet);
        }
        
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department dep = new Department();
        dep.setId(resultSet.getInt("Id"));
        dep.setName(resultSet.getString("Name"));
        return dep;
    }
    
}
