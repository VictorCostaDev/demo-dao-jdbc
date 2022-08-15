package application;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class App2 {
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
        System.out.println("=== TEST 1: findById =======");
        Department dep = departmentDao.findById(1);
        System.out.println(dep);

        System.out.println("\n=== TEST 2: findAll =======");
        List<Department> departmentList = departmentDao.findAll();
        for (Department department : departmentList) {
            System.out.println(department);
        }

         
        System.out.println("\n=== TEST 3: insert =======");
        Department newDepartment = new Department(null, "Music");
        departmentDao.insert(newDepartment);
        System.out.println("Inserted! New id: " + newDepartment.getId());

        System.out.println("\n=== TEST 4: update =======");
		Department dep2 = departmentDao.findById(1);
		dep2.setName("Food");
		departmentDao.update(dep2);
		System.out.println("Update completed");
        
        System.out.println("\n=== TEST 5: delete =======");
		System.out.print("Enter id for delete test: ");
		int id = scan.nextInt();
		departmentDao.deleteById(id);
		System.out.println("Delete completed");

        scan.close();
    }
}
