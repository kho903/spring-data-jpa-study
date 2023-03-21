package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom,
	JpaSpecificationExecutor<Member> {

	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	List<Member> findTop3HelloBy();

	// @Query(name = "Member.findByUsername")
	// 생략시에도 NamedQuery 를 먼저 찾음
	List<Member> findByUsername(@Param("username") String username);

	@Query("select m from Member m where m.username = :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	@Query("select m.username from Member m")
	List<String> findUsernameList();

	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();

	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	List<Member> findListByUsername(String username);
	Member findMemberByUsername(String username);
	Optional<Member> findOptionalByUsername(String username);

	Page<Member> findByAge(int age, Pageable pageable);

	// countQuery를 풀어서 쓰기 - 성능 최적화 : 라고 강의해서 하지만 부트 3.0에서는 디폴트로 해주는 것으로 보임
	@Query(value = "select m from Member m left join m.team t",
			countQuery = "select count(m.username) from Member m")
	Page<Member> findByAgeQuery(int age, Pageable pageable);

	@Modifying(clearAutomatically = true) // JPA에서 executeUpdate 를 붙여주는 역할
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);

	@Query("select m from Member m left join fetch m.team")
	List<Member> findMemberFetchJoin();

	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	@EntityGraph(attributePaths = {"team"})
	@Query("select m from Member m")
	List<Member> findMemberEntityGraph();

	@EntityGraph(attributePaths = {"team"})
	// @EntityGraph("Member.all")
	List<Member> findEntityGraphByUsername(@Param("username") String username);

	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(String username);

	// List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
	// List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
	<T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

	@Query(value = "select * from member where username = ?", nativeQuery = true)
	Member findByNativeQuery(String username);

	@Query(value = "select m.member_id as id, m.username, t.name as teamName "
		+ "from member m left join team t",
		countQuery = "select count(*) from member",
		nativeQuery = true)
	Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
