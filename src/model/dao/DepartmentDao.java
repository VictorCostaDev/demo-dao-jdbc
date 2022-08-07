package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
    
    void insert(Department department);  // Inserir um Department no banco de dados
    void update(Department department); // Da um update no Department
    void deleteById(Integer id);  // Detar um Department do Database
    Department findById(Integer id);  // Retorna um Department do database
    List<Department> findAll();
}
