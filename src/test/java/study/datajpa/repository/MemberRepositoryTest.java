package study.datajpa.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

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
}