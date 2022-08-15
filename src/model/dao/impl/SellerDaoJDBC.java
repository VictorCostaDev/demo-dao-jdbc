package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    public SellerDaoJDBC(Connection conn) {
        // construtor
        this.conn = conn;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(
                "INSERT INTO seller "
                + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                + "VALUES (?, ?, ?, ?, ?)", 
                Statement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, seller.getName());
            pStatement.setString(2, seller.getEmail());
            pStatement.setDate(3, new Date(seller.getBirthDate().getTime()));
            pStatement.setDouble(4, seller.getBaseSalary());
            pStatement.setInt(5, seller.getDepartment().getId());

            int rowsAffected = pStatement.executeUpdate();

            if(rowsAffected > 0) {
                ResultSet resultSet = pStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    seller.setId(id);
                }
                DB.closeResultSet(resultSet);
            } else {
                throw new DbException("Erro inexperado, nenhuma linha foi afetada");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
        }
    }

    @Override
    public void update(Seller seller) {
        PreparedStatement pStatement = null;
        try {
            pStatement = conn.prepareStatement(
                "UPDATE seller "
                + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                + "WHERE Id = ?");
            pStatement.setString(1, seller.getName());
            pStatement.setString(2, seller.getEmail());
            pStatement.setDate(3, new Date(seller.getBirthDate().getTime()));
            pStatement.setDouble(4, seller.getBaseSalary());
            pStatement.setInt(5, seller.getDepartment().getId());
            pStatement.setInt(6, seller.getId());

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
            pStatement = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
            pStatement.setInt(1, id);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
        }
        
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = conn.prepareStatement( 
            "SELECT seller.*,department.Name as DepName "
            + "FROM seller INNER JOIN department "
            + "ON seller.DepartmentId = department.Id "
            + "WHERE seller.Id = ?");

            pStatement.setInt(1, id);
            resultSet = pStatement.executeQuery();
            if(resultSet.next()) {
                Department dep = instantiateDepartment(resultSet);
                Seller seller = instantiateSeller(resultSet, dep);
                return seller;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
            DB.closeResultSet(resultSet);
        }
    }

    private Seller instantiateSeller(ResultSet resultSet, Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(resultSet.getInt("Id"));
        seller.setName(resultSet.getString("Name"));
        seller.setEmail(resultSet.getString("Email"));
        seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
        seller.setBirthDate(resultSet.getDate("BirthDate"));
        seller.setDepartment(dep);
        return seller;
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department dep = new Department();
        dep.setId(resultSet.getInt("DepartmentId"));
        dep.setName(resultSet.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = conn.prepareStatement(
                "SELECT seller.*,department.Name as DepName "
                + "FROM seller INNER JOIN department "
                + "ON seller.DepartmentId = department.Id "
                + "ORDER BY Name");

            resultSet = pStatement.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while(resultSet.next()) {

                Department dep = map.get(resultSet.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    map.put(resultSet.getInt("DepartmentId"), dep);
                }
                Seller seller = instantiateSeller(resultSet, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Seller> FindByDepartment(Department department) {
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            pStatement = conn.prepareStatement(
                "SELECT seller.*,department.Name as DepName "
                + "FROM seller INNER JOIN department "
                + "ON seller.DepartmentId = department.Id "
                + "WHERE DepartmentId = ? "
                + "ORDER BY Name");

            pStatement.setInt(1, department.getId());
            resultSet = pStatement.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while(resultSet.next()) {

                Department dep = map.get(resultSet.getInt("DepartmentId"));
                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    map.put(resultSet.getInt("DepartmentId"), dep);
                }
                Seller seller = instantiateSeller(resultSet, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(pStatement);
            DB.closeResultSet(resultSet);
        }
    }
    
}
