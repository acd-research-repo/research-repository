package com.acd.researchrepo.repository;

import com.acd.researchrepo.model.User;
import com.acd.researchrepo.model.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  Optional<User> findById(Integer userId);

  /**
 * Determines whether any user is associated with the specified department.
 *
 * @param departmentId the department identifier
 * @return {@code true} if at least one user is associated with the department, {@code false} otherwise
 */
boolean existsByDepartmentDepartmentId(Integer departmentId);

  /**
 * Finds users assigned to a department with the specified role.
 *
 * @param departmentId the department identifier
 * @param role         the user role to match
 * @return users matching the department and role
 */
List<User> findByDepartmentDepartmentIdAndRole(Integer departmentId, UserRole role);

  /**
 * Counts the users associated with a department.
 *
 * @param departmentId the identifier of the department
 * @return the number of users associated with the department
 */
long countByDepartmentDepartmentId(Integer departmentId);

  /**
   * Counts users grouped by department for the specified department IDs.
   *
   * @param ids department IDs to include
   * @return rows containing each department ID and its associated user count
   */
  @Query(
      "SELECT u.department.departmentId, COUNT(u) FROM User u "
          + "WHERE u.department.departmentId IN :ids AND u.department IS NOT NULL "
          + "GROUP BY u.department.departmentId")
  List<Object[]> countByDepartmentIds(@Param("ids") List<Integer> ids);

  @Query(
      "SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) "
          + "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))")
  Page<User> searchByEmailOrFullName(@Param("search") String search, Pageable pageable);
}
