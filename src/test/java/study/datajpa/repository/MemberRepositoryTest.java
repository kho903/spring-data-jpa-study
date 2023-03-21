package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	MemberQueryRepository memberQueryRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	public void testMember()  {
		System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
		// memberRepository.getClass() = class jdk.proxy2.$Proxy120

		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		Optional<Member> optionalFindMember = memberRepository.findById(savedMember.getId());
		Member findMember = optionalFindMember.get();

		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member);
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);

		// 리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);

		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGreaterThen() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> res = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
		assertThat(res.get(0).getUsername()).isEqualTo("AAA");
		assertThat(res.get(0).getAge()).isEqualTo(20);
		assertThat(res.size()).isEqualTo(1);
	}

	@Test
	public void findHelloBy() {
		List<Member> helloBy = memberRepository.findTop3HelloBy();
	}


	@Test
	void testNamedQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsername("AAA");
		assertThat(result.get(0)).isEqualTo(m1);
		assertThat(result.get(1)).isEqualTo(m2);
	}

	@Test
	void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findUser("AAA", 10);
		assertThat(result.get(0)).isEqualTo(m1);
		assertThat(result.size()).isEqualTo(1);
	}


	@Test
	void findUsernameList() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<String> result = memberRepository.findUsernameList();
		assertThat(result.get(0)).isEqualTo(m1.getUsername());
		assertThat(result.get(1)).isEqualTo(m2.getUsername());
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	void findMemberDto() {
		Team team = new Team("teamA");
		teamRepository.save(team);

		Member m1 = new Member("AAA", 10);
		m1.setTeam(team);
		memberRepository.save(m1);


		List<MemberDto> result = memberRepository.findMemberDto();
		for (MemberDto memberDto : result) {
			System.out.println("memberDto = " + memberDto);
		}
	}

	@Test
	void findByNames() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
		assertThat(result.get(0)).isEqualTo(m1);
		assertThat(result.get(1)).isEqualTo(m2);
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	void returnType() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		Member m3 = new Member("BBB", 30);
		memberRepository.save(m1);
		memberRepository.save(m2);
		memberRepository.save(m3);

		List<Member> aaa = memberRepository.findListByUsername("AAA");
		Member findMember = memberRepository.findMemberByUsername("AAA");
		Optional<Member> optionalAAA = memberRepository.findOptionalByUsername("AAA");
		System.out.println(aaa);
		System.out.println("findMember = " + findMember);
		System.out.println(optionalAAA);

		List<Member> emptyCollection = memberRepository.findListByUsername("asd");
		System.out.println("emptyCollection = " + emptyCollection); // not null, empty collection

		Member nullMember = memberRepository.findMemberByUsername("asd");
		System.out.println("nullMember = " + nullMember); // null

		Optional<Member> optionalEmpty = memberRepository.findOptionalByUsername("asd");
		System.out.println("optionalEmpty = " + optionalEmpty); // optionalEmpty = Optional.empty

		// 단건 조회가 아닐 경우 IncorrectResultSizeDataAccessException
		// Optional<Member> bbb = memberRepository.findOptionalByUsername("BBB");
		// System.out.println("bbb = " + bbb);
	}

	@Test
	void paging() {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));

		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
		int age = 10;

		// when
		Page<Member> page = memberRepository.findByAge(age, pageRequest);

		// map() 을 사용해 dto로 변환
		Page<MemberDto> toMapToDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

		// Page<Member> page = memberRepository.findByAgeQuery(age, pageRequest);
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();

		// then
		System.out.println(content);
		System.out.println("totalElements = " + totalElements);

		assertThat(content.size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();

	}

	@Test
	void bulkUpdate() {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));

		// when
		int resultCount = memberRepository.bulkAgePlus(20);
		// em.flush();
		// em.clear(); // @Modifying 내에 clearAutomatically = true 로 대체 가능

		Member member5 = memberRepository.findByUsername("member5").get(0);
		System.out.println("member5 = " + member5); // 40이 된다. -> em.flush(), em.clear() 로 초기화 후 해결

		// then
		assertThat(resultCount).isEqualTo(3);
	}

	@Test
	void findMemberLazy() {
		// given
		// member1 -> teamA
		// member2 -> teamB

		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);

		em.flush();
		em.clear();

		// when
		// List<Member> members = memberRepository.findAll(); // N + 1 -> 오버라이드 후 @EntityGraph로 해결 (페치 조인)
		// List<Member> members = memberRepository.findMemberFetchJoin();
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");
		for (Member member : members) {
			System.out.println("member.username = " + member.getUsername());
			System.out.println("member.team.class = " + member.getTeam().getClass());
			System.out.println("member.team = " + member.getTeam().getName());
		}
	}

	@Test
	void queryHint() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// when
		// Member findMember = memberRepository.findById(member1.getId()).get();
		Member findMember = memberRepository.findReadOnlyByUsername("member1");
		findMember.setUsername("member2");

		// 쿼리 힌트 사용 전 : 변경 감지로 업데이트 쿼리 발생 -> 원본이 있어야 함. 즉, 데이터를 2개 가지고 있어야 함. (메모리 낭비)
		// 사용 후 : 변경을 무시.
		em.flush();
	}

	@Test
	void lock() {
		// given
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		em.flush();
		em.clear();

		// when
		List<Member> result = memberRepository.findLockByUsername("member1");
		/*
		select
			m1_0.member_id,
			m1_0.age,
			m1_0.team_id,
			m1_0.username
		from
			member m1_0
		where
			m1_0.username=? for update
		 */
	}

	@Test
	void callCustom() {
		List<Member> result = memberRepository.findMemberCustom();
	}

	@Test
	void specBasic() {
		// given
		Team teamA = new Team("teamA");
		em.persist(teamA);

		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);

		em.flush();
		em.clear();

		// when
		Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
		List<Member> result = memberRepository.findAll(spec);

		Assertions.assertThat(result.size()).isEqualTo(1);
	}
}
